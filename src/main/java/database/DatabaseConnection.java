package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    private static Connection connection;

    static {
        try {
            Properties props = new Properties();
            props.load(DatabaseConnection.class.getClassLoader()
                    .getResourceAsStream("database.properties"));
            URL = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");
        } catch (Exception e) {
            throw new RuntimeException("Error loading database properties: " + e.getMessage());
        }
    }

    private DatabaseConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to database: " + e.getMessage());
        }
        return connection;
    }
}