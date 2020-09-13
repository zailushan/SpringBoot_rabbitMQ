package com.jack.mapper;

import com.jack.entity.OrderRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderRecord record);

    int insertSelective(OrderRecord record);

    OrderRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderRecord record);

    int updateByPrimaryKey(OrderRecord record);

    List<OrderRecord> selectAll();
}