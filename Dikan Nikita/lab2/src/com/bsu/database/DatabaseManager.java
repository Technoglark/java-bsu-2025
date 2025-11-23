package com.bsu.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class DatabaseManager {
    private static final String URL = "jdbc:h2:./banking_db";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private static final DatabaseManager INSTANCE = new DatabaseManager();
    private Connection connection;

    private DatabaseManager() {
        try {
            Class.forName("org.h2.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Соединение с базой данных установлено.");
            initDatabase();
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Ошибка подключения к БД: " + e.getMessage());
            throw new RuntimeException("Не удалось подключиться к базе данных.", e);
        }
    }

    public static DatabaseManager getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() {
        return this.connection;
    }

    private void initDatabase() {
        try (Statement statement = connection.createStatement()) {
            String schemaSql = new String(Files.readAllBytes(Paths.get("resources/schema.sql")));
            statement.execute(schemaSql);
            System.out.println("Схема базы данных успешно инициализирована.");
        } catch (SQLException | IOException e) {
            System.err.println("Ошибка инициализации схемы БД: " + e.getMessage());
        }
    }
}