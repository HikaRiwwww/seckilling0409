package com.throne.seckilling.controller;

import com.throne.seckilling.controller.view_model.ItemVO;
import com.throne.seckilling.error.BusinessException;
import com.throne.seckilling.response.CommonReturnType;
import com.throne.seckilling.service.ItemService;
import com.throne.seckilling.service.model.ItemModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/item")
public class ItemController extends BaseController {
    @Autowired
    private ItemService itemService;

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

    @RequestMapping(value = "/create_page", method = RequestMethod.GET)
    public String getCreatePage(){
        return "/html/create_item.html";
    }

    /**
     * 返回商品列表的html页面
     * @return html
     */
    @RequestMapping(value= "/item_list", method=RequestMethod.GET)
    public String getListPage(){
        return "/html/item_list.html";
    }


    @RequestMapping(value = "/list")
    @ResponseBody
    public CommonReturnType getItemList(){

        return CommonReturnType.create(null);
    }


    public ItemModel convertVOToModel(ItemVO itemVO){
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemVO, itemModel);
        return itemModel;
    }

}
