package com.blaze1r.mybatis.entity;

import com.blaze1r.mybatis.annotation.Table;
import lombok.Data;

/**
 * @ClassName: User
 * @Author: lin rui
 * @Date: 2025/3/18 12:06
 * @Description:
 */

@Data
@Table(tableName = "user")
public class User {
    private Integer id;
    private Integer age;
    private String name;
}
