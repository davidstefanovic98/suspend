package com.suspend.connection;

import com.suspend.connection.exception.ParsingUrlException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {

    private static Connection instance = null;

    public static Connection connect() {
        Properties properties = getProperties();

        String user = properties.getProperty("suspend.db.user");
        String password = properties.getProperty("suspend.db.password");

        if (user == null || password == null) {
            throw new ParsingUrlException("Username or password not found in application.properties");
        }

        initializeDriver();

        try {
            return DriverManager.getConnection(getConnectionUrl(), user, password);
        } catch (SQLException e) {
            throw new ParsingUrlException("Error while connecting to database");
        }
    }

    public static synchronized Connection getInstance() {
        if (instance == null) {
            instance = connect();
        } else {
            try {
                if (instance.isClosed()) {
                    instance = connect();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public static String getConnectionUrl() {
        Properties properties = getProperties();

        String protocol = properties.getProperty("suspend.db.protocol");
        String host = properties.getProperty("suspend.db.host");
        String port = properties.getProperty("suspend.db.port");
        String name = properties.getProperty("suspend.db.name");

        String url = String.format("%s://%s:%s/%s", protocol, host, port, name);

        if (url == null) {
            throw new ParsingUrlException("Connection url not found in application.properties");
        }

        return url;
    }

    public static void initializeDriver() {
        Properties properties = getProperties();

        String driver = properties.getProperty("suspend.db.driver");

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Properties getProperties() {
          Properties properties = new Properties();

          try(InputStream inputStream = ConnectionManager.class.getResourceAsStream("/application.properties")) {
              properties.load(inputStream);
          } catch (IOException e) {
              e.printStackTrace();
          }
          return properties;
    }
}
