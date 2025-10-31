package com.example.quanlyquancaphe.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        // Ensure driver is loaded (useful in some environments/modules)
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException ignored) {
            // If driver is on classpath, DriverManager may still find it via ServiceLoader.
        }

        String url = "jdbc:sqlserver://www.quancaphenho.dev;databaseName=QuanCaPheNho;encrypt=false";
        String user = "sa";
        String password = "Tranhuong222@";
        return DriverManager.getConnection(url, user, password);
    }
}
