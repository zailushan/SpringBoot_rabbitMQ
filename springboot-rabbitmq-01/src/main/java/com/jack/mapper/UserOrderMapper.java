package com.jack.mapper;

import com.jack.entity.UserOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author cwl
 * @description: TODO
 * @date 2020/9/1 21:02
 */
@Mapper
public interface UserOrderMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(UserOrder record);

    int insertSelective(UserOrder record);

    UserOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserOrder record);

    int updateByPrimaryKey(UserOrder record);

    UserOrder selectByPkAndStatus(@Param("id") Integer id, @Param("status") Integer status);
}
