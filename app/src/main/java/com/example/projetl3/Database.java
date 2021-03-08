package com.example.projetl3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    String host = "91.234.194.249";
    String dbname = "cp1548652p32_application";
    String username = "cp1548652p32_appli";
    String pwd = "yFEP&1V?RuI8_!9&U^";
    private Connection connection;

    public Database() throws SQLException {
        connect();
    }

    public void connect() throws SQLException {
        String url = "jdbc:mysql://"+host+":3306/"+dbname+"?useSSL=false";
        connection = DriverManager.getConnection(url, username, pwd);
    }

    public Connection getConnection() {
        return connection;
    }
}
