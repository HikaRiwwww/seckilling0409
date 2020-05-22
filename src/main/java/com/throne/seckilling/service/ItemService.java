package com.throne.seckilling.service;

import com.throne.seckilling.error.BusinessException;
import com.throne.seckilling.service.model.ItemModel;

import java.util.List;

public interface ItemService {
    List<ItemModel> listItems();

    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    ItemModel getItemById(Integer id) throws BusinessException;

    boolean decreasePromoItemStock(Integer itemId, Integer amount);

    boolean increaseItemStock(Integer itemId, Integer amount);

    boolean increaseSalesIncacheById(Integer itemId, Integer amount);

    ItemModel getCachedItemById(Integer id) throws BusinessException;

    boolean sendDecreaseMsg(Integer itemId, Integer amount);

    String initStockLog(Integer itemId, Integer amount);

    void decreaseCommonItemStock(Integer itemId, Integer amount);

    void increaseSalesById(Integer itemId, Integer amount);
}
