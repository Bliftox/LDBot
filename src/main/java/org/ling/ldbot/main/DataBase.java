package org.ling.ldbot.main;

import net.dv8tion.jda.api.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBase {

    private final Connection connection;
    private final String tableName = "applications";

    public DataBase(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                    "applicationId TEXT PRIMARY KEY, " +
                    "userId TEXT NOT NULL, " +
                    "username TEXT NOT NULL, " +
                    "userGlobalName TEXT NOT NULL, " +
                    "fieldOneValue TEXT NOT NULL, " +
                    "fieldTwoValue TEXT NOT NULL, " +
                    "fieldThreeValue TEXT NOT NULL, " +
                    "fieldFourValue TEXT NOT NULL, " +
                    "fieldFiveValue TEXT NOT NULL" +
                    ")");
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void saveApplication(String applicationId, User user, String fieldOneValue, String fieldTwoValue, String fieldThreeValue, String fieldFourValue, String fieldFiveValue) throws SQLException {
        String query = "INSERT INTO " + tableName + " (applicationId, userId, username, userGlobalName, fieldOneValue, fieldTwoValue, fieldThreeValue, fieldFourValue, fieldFiveValue) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, applicationId);
            preparedStatement.setString(2, user.getId());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setString(4, user.getGlobalName());
            preparedStatement.setString(5, fieldOneValue);
            preparedStatement.setString(6, fieldTwoValue);
            preparedStatement.setString(7, fieldThreeValue);
            preparedStatement.setString(8, fieldFourValue);
            preparedStatement.setString(9, fieldFiveValue);
            preparedStatement.executeUpdate();
        }
    }

    private String getApplicationField(String applicationId, String fieldName) throws SQLException {
        String query = "SELECT " + fieldName + " FROM " + tableName + " WHERE applicationId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, applicationId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? resultSet.getString(fieldName) : null;
            }
        }
    }

    public String getApplicationUserId(String applicationId) throws SQLException {
        return getApplicationField(applicationId, "userId");
    }

    public boolean applicationExists(String applicationId) throws SQLException {
        String query = "SELECT * FROM " + tableName + " WHERE applicationId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, applicationId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public List<String> getFieldsValue(String applicationId) throws SQLException {
        List<String> fieldValues = new ArrayList<>();
        String query = "SELECT fieldOneValue, fieldTwoValue, fieldThreeValue, fieldFourValue, fieldFiveValue FROM " + tableName + " WHERE applicationId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, applicationId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    fieldValues.add(resultSet.getString("fieldOneValue"));
                    fieldValues.add(resultSet.getString("fieldTwoValue"));
                    fieldValues.add(resultSet.getString("fieldThreeValue"));
                    fieldValues.add(resultSet.getString("fieldFourValue"));
                    fieldValues.add(resultSet.getString("fieldFiveValue"));
                }
            }
        }
        return fieldValues;
    }
}
