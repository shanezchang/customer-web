package com.shane.customer_web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shane.customer_web.model.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserMapper extends BaseMapper<UserEntity> {

}
