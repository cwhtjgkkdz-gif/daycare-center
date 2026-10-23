package com.soft231.daycarecenterbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soft231.daycarecenterbackend.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
