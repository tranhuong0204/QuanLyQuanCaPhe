package com.example.quanlyquancaphe.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:sqlserver://www.quancaphenho.dev;databaseName=QuanCaPheNho;encrypt=false";
        String user = "sa";
        String password = "Tranhuong222@";
        return DriverManager.getConnection(url, user, password);
    }
}
