package com.throne.seckilling.dao;

import com.throne.seckilling.data_object.SequenceDO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SequenceDOMapper {


    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbg.generated Mon Apr 20 17:33:36 CST 2020
     */
    int insert(SequenceDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbg.generated Mon Apr 20 17:33:36 CST 2020
     */
    int insertSelective(SequenceDO record);

    SequenceDO getSequenceByName(@Param(value = "name") String name);
}
