package com.suspend.executor;

import com.suspend.connection.ConnectionManager;
import org.mariadb.jdbc.export.Prepare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Executor {

    private final Logger logger = LoggerFactory.getLogger(Executor.class);

    // TODO: Map<String, Object> should be a custom structure
    public List<Map<String, Object>> execute(String query) {
        List<Map<String, Object>> result = new ArrayList<>();

        try (Connection connection = ConnectionManager.getInstance(); Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            ResultSetMetaData metaData = resultSet.getMetaData();

            while (resultSet.next()) {
               Map<String, Object> map = new HashMap<>();
               for (int i = 1; i <= metaData.getColumnCount(); i++) {
                   map.put(metaData.getColumnName(i), resultSet.getObject(i));
               }
                result.add(map);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public Long executeUpdate(String query) {
        try (Connection connection = ConnectionManager.getInstance(); PreparedStatement statement = connection.prepareStatement(query)) {

            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getLong(1);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
