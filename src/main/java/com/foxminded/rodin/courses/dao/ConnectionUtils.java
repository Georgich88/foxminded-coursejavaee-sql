package com.foxminded.rodin.courses.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.foxminded.rodin.courses.dao.exceptions.NoDBPropertiesException;

public class ConnectionUtils {

    private static final String PROPERTIES_FILE_PATH = "src/main/resources/db.properties";
    private static final String URL_PROPERY_NAME = "db.url";
    private static final String USER_PROPERY_NAME = "db.user";
    private static final String PASSWORD_PROPERY_NAME = "db.password";

    private final static Logger logger = Logger.getLogger(ConnectionUtils.class);

    public static Connection getConnection() throws SQLException, NoDBPropertiesException {
        return getConnection(PROPERTIES_FILE_PATH);
    }

    public static Connection getConnection(String propertiesFilePath) throws SQLException, NoDBPropertiesException {

        Properties properties = new Properties();

        try {
            InputStream inputProperties = new FileInputStream(propertiesFilePath);
            properties.load(inputProperties);

        } catch (IOException e) {
            logger.error("Cannot read properties file", e);
            throw new NoDBPropertiesException("Cannot read properties file", e);
        }

        Driver driver = new org.postgresql.Driver();
        DriverManager.deregisterDriver(driver);
        return DriverManager.getConnection(properties.getProperty(URL_PROPERY_NAME),
                properties.getProperty(USER_PROPERY_NAME), properties.getProperty(PASSWORD_PROPERY_NAME));

    }

    public static void closeQuietly(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            logger.warn("Cannot close connection", e);
        }
    }

    public static void closeQuietly(ResultSet resultSet) {
        try {
            resultSet.close();
        } catch (SQLException e) {
            logger.warn("Cannot close result set", e);
        }
    }

    public static void closeQuietly(PreparedStatement statement) {
        try {
            statement.close();
        } catch (SQLException e) {
            logger.warn("Cannot close prepared statement", e);
        }
    }

}
