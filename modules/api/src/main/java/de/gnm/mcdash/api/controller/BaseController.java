package de.gnm.mcdash.api.controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class BaseController {

    protected final Connection connection;

    public BaseController(Connection connection) {
        this.connection = connection;
    }

    /**
     * Execute an update query
     * @param query The query to execute
     * @param params The parameters for the query
     * @return The number of rows affected
     */
    protected int executeUpdate(String query, Object... params) {
        try {
            PreparedStatement statement = connection.prepareStatement(query);

            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }

            int i = statement.executeUpdate();
            statement.close();

            return i;
        } catch (SQLException e) {
            return -1;
        }
    }

    /**
     * Get a single result from a query
     * @param query The query to execute
     * @param params The parameters for the query
     * @return The result of the query
     */
    protected HashMap<String, Object> getSingleResult(String query, Object... params) {
        try {
            PreparedStatement statement = connection.prepareStatement(query);

            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }

            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            HashMap<String,Object> result = new HashMap<>();

            if (resultSet.next()) {
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    result.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
            }

            resultSet.close();
            statement.close();

            if (result.isEmpty()) {
                return null;
            }

            return result;
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Get multiple results from a query
     * @param query The query to execute
     * @param params The parameters for the query
     * @return The results of the query
     */
    protected ArrayList<HashMap<String, Object>> getMultipleResults(String query, Object... params) {
        try {
            PreparedStatement statement = connection.prepareStatement(query);

            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }

            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            ArrayList<HashMap<String, Object>> results = new ArrayList<>();

            while (resultSet.next()) {
                HashMap<String, Object> result = new HashMap<>();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    result.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                results.add(result);
            }

            resultSet.close();
            statement.close();

            return results;
        } catch (SQLException e) {
            return null;
        }
    }

}
