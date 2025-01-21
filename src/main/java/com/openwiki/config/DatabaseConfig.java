package com.openwiki.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.util.Properties;

public class DatabaseConfig {
    private static Properties props;
    
    static {
        props = new Properties();
        try {
            props.load(DatabaseConfig.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Impossibile caricare config.properties", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = props.getProperty("db.url");
        String username = props.getProperty("db.username");
        String password = props.getProperty("db.password");
        
        return DriverManager.getConnection(url, username, password);
    }
} 