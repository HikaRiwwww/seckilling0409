package com.throne.seckilling.service;

import com.throne.seckilling.error.BusinessException;
import com.throne.seckilling.service.model.ItemModel;

import java.util.List;

public interface ItemService {
    List<ItemModel> listItems();

    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    ItemModel getItemById(Integer id) throws BusinessException;

    boolean decreaseItemStock(Integer itemId, Integer amount);
}
