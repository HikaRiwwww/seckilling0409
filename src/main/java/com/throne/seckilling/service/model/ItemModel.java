package com.throne.seckilling.service.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 商品信息的领域模型
 */
public class ItemModel {
    private Integer id;
    @NotNull(message = "商品名称不能为空")
    private String title;
    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    @Min(message = "库存必须大于等于0", value = 0)
    private Integer stock;


    @NotNull(message = "商品描述不能为空")
    private String description;

    @NotNull(message = "图片链接不能为空")
    private String imgUrl;

    @Min(message = "销量必须大于或等于0", value = 0)
    private Integer sales;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }
}
