package com.throne.seckilling.data_object;

import java.util.Date;

public class PromoDO {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column promo_info.id
     *
     * @mbg.generated Tue Apr 21 10:33:25 CST 2020
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column promo_info.promo_name
     *
     * @mbg.generated Tue Apr 21 10:33:25 CST 2020
     */
    private String promoName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column promo_info.start_date
     *
     * @mbg.generated Tue Apr 21 10:33:25 CST 2020
     */
    private Date startDate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column promo_info.end_date
     *
     * @mbg.generated Tue Apr 21 10:33:25 CST 2020
     */
    private Date endDate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column promo_info.item_id
     *
     * @mbg.generated Tue Apr 21 10:33:25 CST 2020
     */
    private Integer itemId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column promo_info.sec_price
     *
     * @mbg.generated Tue Apr 21 10:33:25 CST 2020
     */
    private Double secPrice;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column promo_info.id
     *
     * @return the value of promo_info.id
     *
     * @mbg.generated Tue Apr 21 10:33:25 CST 2020
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column promo_info.id
     *
     * @param id the value for promo_info.id
     *
     * @mbg.generated Tue Apr 21 10:33:25 CST 2020
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column promo_info.promo_name
     *
     * @return the value of promo_info.promo_name
     *
     * @mbg.generated Tue Apr 21 10:33:25 CST 2020
     */
    public String getPromoName() {
        return promoName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column promo_info.promo_name
     *
     * @param promoName the value for promo_info.promo_name
     *
     * @mbg.generated Tue Apr 21 10:33:25 CST 2020
     */
    public void setPromoName(String promoName) {
        this.promoName = promoName == null ? null : promoName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column promo_info.start_date
     *
     * @return the value of promo_info.start_date
     *
     * @mbg.generated Tue Apr 21 10:33:25 CST 2020
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column promo_info.start_date
     *
     * @param startDate the value for promo_info.start_date
     *
     * @mbg.generated Tue Apr 21 10:33:25 CST 2020
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column promo_info.end_date
     *
     * @return the value of promo_info.end_date
     *
     * @mbg.generated Tue Apr 21 10:33:25 CST 2020
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column promo_info.end_date
     *
     * @param endDate the value for promo_info.end_date
     *
     * @mbg.generated Tue Apr 21 10:33:25 CST 2020
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column promo_info.item_id
     *
     * @return the value of promo_info.item_id
     *
     * @mbg.generated Tue Apr 21 10:33:25 CST 2020
     */
    public Integer getItemId() {
        return itemId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column promo_info.item_id
     *
     * @param itemId the value for promo_info.item_id
     *
     * @mbg.generated Tue Apr 21 10:33:25 CST 2020
     */
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column promo_info.sec_price
     *
     * @return the value of promo_info.sec_price
     *
     * @mbg.generated Tue Apr 21 10:33:25 CST 2020
     */
    public Double getSecPrice() {
        return secPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column promo_info.sec_price
     *
     * @param secPrice the value for promo_info.sec_price
     *
     * @mbg.generated Tue Apr 21 10:33:25 CST 2020
     */
    public void setSecPrice(Double secPrice) {
        this.secPrice = secPrice;
    }
}