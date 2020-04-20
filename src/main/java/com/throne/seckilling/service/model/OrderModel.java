package com.throne.seckilling.service.model;

import java.math.BigDecimal;

/**
 * 交易订单模型
 * 仅考虑一个订单里只有一种商品的简单情况
 */
public class OrderModel {

    // 订单号
    private String id;

    // 商品单价
    private BigDecimal itemPrice;

    // 订单总价
    private BigDecimal totalPrice;

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    // 商品数量
    private Integer amount;

    // 商品id
    private Integer itemId;

    // 用户id
    private Integer userId;

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
