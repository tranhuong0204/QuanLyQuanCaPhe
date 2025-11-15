package com.example.quanlyquancaphe.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:sqlserver://www.quancaphenho.dev;databaseName=QuanCaPheNho;encrypt=false";
    private static final String USER = "sa";
    private static final String PASSWORD = "Tranhuong222@";

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Không tìm thấy driver SQL Server!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
