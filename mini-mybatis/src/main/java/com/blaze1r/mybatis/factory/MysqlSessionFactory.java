package com.blaze1r.mybatis.factory;

import com.blaze1r.mybatis.annotation.Param;
import com.blaze1r.mybatis.annotation.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: MysqlSessionFactory
 * @Author: lin rui
 * @Date: 2025/3/18 12:18
 * @Description:
 */

public class MysqlSessionFactory {
    private static final Logger log = LoggerFactory.getLogger(MysqlSessionFactory.class);
    private static final String URL = "jdbc:mysql://localhost:3306/demo";
    private static final String USER_NAME = "root";
    private static final String PASSWORD = "123456";

    @SuppressWarnings("all")
    public <T> T getMapper(Class<T> mapperClass) {
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{mapperClass}, new MapperInvokeHandler());
    }

    static class MapperInvokeHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            log.info("进入 MapperInvokeHandler");
            if (method.getName().startsWith("select")) {
                return invokeSelect(proxy, method, args);
            }
            return null;
        }

        private Object invokeSelect(Object proxy, Method method, Object[] args) {
            try {
                String sql = createSQL(method);
                log.info("拼接完成的 SQL ===== {}", sql);
                try (Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
                     PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
                    for (int i = 0; i < args.length; i++) {
                        if(args[i] instanceof String){
                            preparedStatement.setString(i + 1, args[i].toString());
                        }
                        else if(args[i] instanceof Integer){
                            preparedStatement.setInt(i + 1, Integer.parseInt(args[i].toString()));
                        }
                    }
                    log.info("填充完成后的 preparedStatement SQL========== {}", preparedStatement.toString());
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        return parseResult(resultSet, method.getReturnType());
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        private Object parseResult(ResultSet resultSet, Class<?> returnType) throws Exception {
            log.info("开始构造返回对象 parseResult");
            Constructor<?> constructor = returnType.getConstructor();
            Object object = constructor.newInstance();
            Field[] declaredFields = returnType.getDeclaredFields();
            for (Field field : declaredFields) {
                Object colValue = null;
                String name = field.getName();
                if (field.getType() == Integer.class) {
                    colValue = resultSet.getInt(name);
                }
                else if (field.getType() == String.class) {
                    colValue = resultSet.getString(name);
                }
                field.setAccessible(true);
                field.set(object, colValue);

            }
            log.info("构造完成返回对象 object=========={}", object);
            return object;
        }

        @SuppressWarnings("all")
        private String createSQL(Method method) {
            log.info("进入方法createSQL()拼接SQL");
            StringBuilder sql = new StringBuilder();
            sql.append(" select ");
            List<String> list = getSelectCols(method.getReturnType());
            sql.append(String.join(",", list));
            sql.append(" from ");
            String tableName = getSelectTableName(method.getReturnType());
            sql.append(tableName);
            sql.append(" where ");
            String where = getSelectWhere(method);
            sql.append(where);
            return sql.toString();
        }

        private String getSelectWhere(Method method) {
            return Arrays.stream(method.getParameters()).map((parameter) -> {
                Param param = parameter.getAnnotation(Param.class);
                String colName = param.value();
                return colName += " = ?";
            }).collect(Collectors.joining(" and "));
        }

        private String getSelectTableName(Class<?> returnType) {
            Table table = returnType.getAnnotation(Table.class);
            if (table == null) {
                throw new RuntimeException("@Table annotation is required");
            }
            return table.tableName();
        }


        private List<String> getSelectCols(Class<?> returnType) {
            Field[] declaredFields = returnType.getDeclaredFields();
            return Arrays.stream(declaredFields).map(Field::getName).collect(Collectors.toList());
        }
    }
}
