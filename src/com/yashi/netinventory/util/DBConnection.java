package com.yashi.netinventory.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central place that hands out JDBC connections to the MySQL database.
 */
public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/network_inventory_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "appuser";
    private static final String PASSWORD = "AppPass123!";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found on classpath.", e);
        }
    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
