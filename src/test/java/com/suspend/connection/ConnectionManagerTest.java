package com.suspend.connection;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionManagerTest {

    @Test
    void testConnection() throws SQLException {
        Connection connection = new ConnectionManager().connect();
        assertTrue(connection.isValid(100));
    }
}