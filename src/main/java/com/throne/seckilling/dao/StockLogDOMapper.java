package com.throne.seckilling.dao;

import com.throne.seckilling.data_object.StockLogDO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface StockLogDOMapper {


    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_log
     *
     * @mbg.generated Thu May 21 21:26:01 CST 2020
     */
    int deleteByPrimaryKey(String stockLogId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_log
     *
     * @mbg.generated Thu May 21 21:26:01 CST 2020
     */
    int insert(StockLogDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_log
     *
     * @mbg.generated Thu May 21 21:26:01 CST 2020
     */
    int insertSelective(StockLogDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_log
     *
     * @mbg.generated Thu May 21 21:26:01 CST 2020
     */

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_log
     *
     * @mbg.generated Thu May 21 21:26:01 CST 2020
     */
    StockLogDO selectByPrimaryKey(String stockLogId);


    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_log
     *
     * @mbg.generated Thu May 21 21:26:01 CST 2020
     */
    int updateByPrimaryKeySelective(StockLogDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table stock_log
     *
     * @mbg.generated Thu May 21 21:26:01 CST 2020
     */
    int updateByPrimaryKey(StockLogDO record);
}