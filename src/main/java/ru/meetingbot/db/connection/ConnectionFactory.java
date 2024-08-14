package ru.meetingbot.db.connection;

import org.postgresql.jdbc2.optional.ConnectionPool;
import ru.meetingbot.util.PropertiesUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Отдаёт Connection
 */
public class ConnectionFactory {
    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties properties = PropertiesUtils.getProperties("database");
        URL = properties.getProperty("db.url");
        USER = properties.getProperty("db.user");
        PASSWORD = properties.getProperty("db.password");
    }

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
