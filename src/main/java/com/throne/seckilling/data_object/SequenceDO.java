package com.throne.seckilling.data_object;

public class SequenceDO {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sequence_info.name
     *
     * @mbg.generated Mon Apr 20 17:33:36 CST 2020
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sequence_info.current_val
     *
     * @mbg.generated Mon Apr 20 17:33:36 CST 2020
     */
    private Integer currentVal;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sequence_info.step
     *
     * @mbg.generated Mon Apr 20 17:33:36 CST 2020
     */
    private Integer step;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sequence_info.max_value
     *
     * @mbg.generated Mon Apr 20 17:33:36 CST 2020
     */
    private Integer maxValue;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sequence_info.init_value
     *
     * @mbg.generated Mon Apr 20 17:33:36 CST 2020
     */
    private Integer initValue;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sequence_info.name
     *
     * @return the value of sequence_info.name
     *
     * @mbg.generated Mon Apr 20 17:33:36 CST 2020
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sequence_info.name
     *
     * @param name the value for sequence_info.name
     *
     * @mbg.generated Mon Apr 20 17:33:36 CST 2020
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sequence_info.current_val
     *
     * @return the value of sequence_info.current_val
     *
     * @mbg.generated Mon Apr 20 17:33:36 CST 2020
     */
    public Integer getCurrentVal() {
        return currentVal;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sequence_info.current_val
     *
     * @param currentVal the value for sequence_info.current_val
     *
     * @mbg.generated Mon Apr 20 17:33:36 CST 2020
     */
    public void setCurrentVal(Integer currentVal) {
        this.currentVal = currentVal;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sequence_info.step
     *
     * @return the value of sequence_info.step
     *
     * @mbg.generated Mon Apr 20 17:33:36 CST 2020
     */
    public Integer getStep() {
        return step;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sequence_info.step
     *
     * @param step the value for sequence_info.step
     *
     * @mbg.generated Mon Apr 20 17:33:36 CST 2020
     */
    public void setStep(Integer step) {
        this.step = step;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sequence_info.max_value
     *
     * @return the value of sequence_info.max_value
     *
     * @mbg.generated Mon Apr 20 17:33:36 CST 2020
     */
    public Integer getMaxValue() {
        return maxValue;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sequence_info.max_value
     *
     * @param maxValue the value for sequence_info.max_value
     *
     * @mbg.generated Mon Apr 20 17:33:36 CST 2020
     */
    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sequence_info.init_value
     *
     * @return the value of sequence_info.init_value
     *
     * @mbg.generated Mon Apr 20 17:33:36 CST 2020
     */
    public Integer getInitValue() {
        return initValue;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sequence_info.init_value
     *
     * @param initValue the value for sequence_info.init_value
     *
     * @mbg.generated Mon Apr 20 17:33:36 CST 2020
     */
    public void setInitValue(Integer initValue) {
        this.initValue = initValue;
    }
}