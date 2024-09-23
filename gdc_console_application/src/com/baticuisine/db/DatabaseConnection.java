package com.baticuisine.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection("jdbc:postgresql://localhost:5444/baticuisine", "myuser", "AZERAZER1234");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Database Connection Creation Failed : " + e.getMessage());
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection("jdbc:postgresql://localhost:5444/baticuisine", "myuser", "AZERAZER1234");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error re-establishing database connection: " + e.getMessage());
        }
        return connection;
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error closing database connection: " + e.getMessage());
        }
    }
}