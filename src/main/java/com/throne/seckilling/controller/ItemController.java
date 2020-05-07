package com.throne.seckilling.controller;

import com.throne.seckilling.controller.view_model.ItemVO;
import com.throne.seckilling.error.BusinessException;
import com.throne.seckilling.response.CommonReturnType;
import com.throne.seckilling.service.ItemService;
import com.throne.seckilling.service.model.ItemModel;
import com.throne.seckilling.service.model.PromoModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/item")
public class ItemController extends BaseController {
    @Autowired
    private ItemService itemService;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/create_item", method = RequestMethod.POST)
    @ResponseBody
    public CommonReturnType createItem(
            @RequestParam(name = "title") String title,
            @RequestParam(name = "price") BigDecimal price,
            @RequestParam(name = "stock") Integer stock,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "imgUrl") String imgUrl
    ) throws BusinessException {
        // 创建商品
        ItemVO itemVO = new ItemVO();
        itemVO.setDescription(description);
        itemVO.setTitle(title);
        itemVO.setImgUrl(imgUrl);
        itemVO.setPrice(price);
        itemVO.setStock(stock);
        // 类型转换
        ItemModel itemModel = convertVOToModel(itemVO);
        // 调用service处理
        ItemModel item = itemService.createItem(itemModel);
        // 返回创建结果
        if (item == null){
            return CommonReturnType.create("failed", "商品创建失败");
        }else {
            return CommonReturnType.create("success", "商品创建成功");
        }
    }


    @RequestMapping(value = "/list")
    @ResponseBody
    public CommonReturnType getItemList(){
        List<ItemModel> itemModels = itemService.listItems();
        return CommonReturnType.create("success", itemModels);
    }

    @RequestMapping("/get_details")
    @ResponseBody
    public CommonReturnType getItemDetail(@RequestParam (name = "id") Integer id ) throws BusinessException {
        ItemModel itemModel = (ItemModel) redisTemplate.opsForValue().get("item_" + id.toString());
        if (itemModel == null){
            itemModel = itemService.getItemById(id);
            redisTemplate.opsForValue().set("item_"+id.toString(), itemModel);
            redisTemplate.expire("item_"+id.toString(), 5, TimeUnit.MINUTES);
        }
        ItemVO itemVO = convertModelToVO(itemModel);
        return CommonReturnType.create("success", itemVO);
    }

    public ItemModel convertVOToModel(ItemVO itemVO){
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemVO, itemModel);
        return itemModel;
    }

    public ItemVO convertModelToVO(ItemModel itemModel){
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);
        PromoModel promoModel = itemModel.getPromoModel();
        if (promoModel != null){
            itemVO.setPromoStartDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(promoModel.getStartDate()));
            itemVO.setPromoEndDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(promoModel.getEndDate()));
            itemVO.setPromoStatus(promoModel.getStatus());
            itemVO.setSecPrice(promoModel.getSecPrice());
            itemVO.setPromoId(promoModel.getId());
        }
        return itemVO;
    }
}
