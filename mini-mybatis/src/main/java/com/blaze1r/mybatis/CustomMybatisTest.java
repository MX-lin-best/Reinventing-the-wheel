package com.blaze1r.mybatis;

import com.blaze1r.mybatis.entity.User;
import com.blaze1r.mybatis.factory.MysqlSessionFactory;
import com.blaze1r.mybatis.mapper.UserMapper;

import java.sql.*;

/**
 * @ClassName: CustomMybatisTest
 * @Author: lin rui
 * @Date: 2025/3/18 12:07
 * @Description:
 */
public class CustomMybatisTest {

    public static void main(String[] args) throws Exception{
        MysqlSessionFactory mysqlSessionFactory = new MysqlSessionFactory();
        UserMapper mapper = mysqlSessionFactory.getMapper(UserMapper.class);
        User user = mapper.selectById(1);
        User user1 = mapper.selectByName("bob");
        User user2 = mapper.selectByIdAndName(1,"tom");
        System.out.println(user);
        System.out.println(user1);
        System.out.println(user2);
//        User user = jdbcSelect(2);
//        System.out.println(user);

    }

    private static User jdbcSelect(int id) throws SQLException {
        String sql = "select id, age, name from user where id = ?";
        String url = "jdbc:mysql://localhost:3306/demo";
        String username = "root";
        String password = "123456";

        try(Connection connection = DriverManager.getConnection(url, username, password);){
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setAge(resultSet.getInt("age"));
                user.setName(resultSet.getString("name"));
                return user;
            }
        }

        return null;
    }


}
