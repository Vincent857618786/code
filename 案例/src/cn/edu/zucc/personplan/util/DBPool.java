package cn.edu.zucc.personplan.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.beans.PropertyVetoException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DBPool {
    private static DBPool dbPool;
    private ComboPooledDataSource dataSource;

    static {
        dbPool = new DBPool();
    }

    public DBPool() {
        try {
            dataSource = new ComboPooledDataSource();
            dataSource.setUser("root");
            dataSource.setPassword("123456");
            dataSource
                    .setJdbcUrl("jdbc:mysql://localhost:3306/booklib?useUnicode=true&characterEncoding=utf8&useSSL=false");
            dataSource.setDriverClass("com.mysql.jdbc.Driver");
            dataSource.setInitialPoolSize(2);
            dataSource.setMinPoolSize(1);
            dataSource.setMaxPoolSize(10);
            dataSource.setMaxStatements(50);
            dataSource.setMaxIdleTime(60);
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }
    }

    public static DBPool getInstance() {
        return dbPool;
    }

    public final Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("无法从数据源获取连接 ", e);
        }
    }

    public static void main(String[] args) throws SQLException {
        try (Connection con = DBPool.getInstance().getConnection()) {

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
