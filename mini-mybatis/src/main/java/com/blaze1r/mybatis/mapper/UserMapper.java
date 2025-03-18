package com.blaze1r.mybatis.mapper;

import com.blaze1r.mybatis.annotation.Param;
import com.blaze1r.mybatis.entity.User;

/**
 * @ClassName: UserMapper
 * @Author: lin rui
 * @Date: 2025/3/18 12:03
 * @Description:
 */
public interface UserMapper {
    User selectById(@Param(value = "id") int id);

    User selectByName(@Param(value = "name") String name);

    User selectByIdAndName(@Param(value = "id") int id, @Param(value = "name")String name);
}
