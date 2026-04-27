package dao;

import db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HotelAdminDAO {
    private static final int PRIMARY_ADMIN_ID = 1;
    private static final String ADMIN_EMAIL = "ashish@gmail.com";
    private static final String ADMIN_PASSWORD = "123456";

    public int validateLogin(String email, String password) throws SQLException {
        String sql = "SELECT hotel_id FROM hotel_admin WHERE username = ? AND password = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ensurePrimaryAdminCredentials(connection);

            statement.setString(1, email);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("hotel_id");
                }
            }
        }

        return -1;
    }

    private void ensurePrimaryAdminCredentials(Connection connection) throws SQLException {
        int defaultHotelId = resolveDefaultHotelId(connection);
        String mergeSql = "MERGE INTO hotel_admin target "
                + "USING (SELECT ? AS admin_id, ? AS username, ? AS password, ? AS hotel_id FROM dual) source "
                + "ON (target.admin_id = source.admin_id) "
                + "WHEN MATCHED THEN UPDATE SET "
                + "target.username = source.username, "
                + "target.password = source.password, "
                + "target.hotel_id = source.hotel_id "
                + "WHEN NOT MATCHED THEN INSERT (admin_id, username, password, hotel_id) "
                + "VALUES (source.admin_id, source.username, source.password, source.hotel_id)";

        try (PreparedStatement statement = connection.prepareStatement(mergeSql)) {
            statement.setInt(1, PRIMARY_ADMIN_ID);
            statement.setString(2, ADMIN_EMAIL);
            statement.setString(3, ADMIN_PASSWORD);
            statement.setInt(4, defaultHotelId);
            statement.executeUpdate();
        }
    }

    private int resolveDefaultHotelId(Connection connection) throws SQLException {
        String sql = "SELECT MIN(hotel_id) AS hotel_id FROM hotels";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next() && resultSet.getInt("hotel_id") > 0) {
                return resultSet.getInt("hotel_id");
            }
        }

        return 1;
    }
}
