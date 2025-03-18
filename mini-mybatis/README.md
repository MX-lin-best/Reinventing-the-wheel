### 实现步骤
1. 创建实体类``User``, 并需要创建注解``@Table(tableName = "user")``来指定所需要查找的数据库。
2. 创建接口``UserMapper``,并需要创建注解``@Param(value = "id")``来指定查询条件
3. **(最重要)** 创建``MysqlSessionFactory``,利用动态代理，并实现相应方法，大致步骤
   1. 利用 ``泛型 + Proxy.newProxyInstance()``创建动态代理，实现自定义的``InvocationHandler.invoke()``方法
   2. 利用``Method``反射获取所执行的方法参数，注解以及其返回类型，动态的拼接``SQL``
   3. 利用``DriverManager``获取连接,``PreparedStatement``执行``SQL``，``ResultSet``获取相关结果集
   4. 利用``Method``创建返回值类型对象，再通过``ResultSet``获取的相关结果集进行字段赋值
### 原理：反射 + 动态代理 + 泛型