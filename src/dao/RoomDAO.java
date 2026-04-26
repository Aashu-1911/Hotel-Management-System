package dao;

import db.DBConnection;
import model.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    public List<Room> getAvailableRooms() throws SQLException {
        String sql = "SELECT room_id, hotel_id, type, price, status FROM rooms WHERE status = ? ORDER BY hotel_id, room_id";
        List<Room> rooms = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "Available");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rooms.add(mapRoom(resultSet));
                }
            }
        }

        return rooms;
    }

    public List<Room> getAvailableRoomsByHotel(int hotelId) throws SQLException {
        String sql = "SELECT room_id, hotel_id, type, price, status FROM rooms WHERE status = ? AND hotel_id = ? ORDER BY room_id";
        List<Room> rooms = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "Available");
            statement.setInt(2, hotelId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rooms.add(mapRoom(resultSet));
                }
            }
        }

        return rooms;
    }

    public void updateRoomStatus(Connection connection, int roomId, String status) throws SQLException {
        String sql = "UPDATE rooms SET status = ? WHERE room_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, roomId);

            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                throw new SQLException("Room update failed. Room not found: " + roomId);
            }
        }
    }

    private Room mapRoom(ResultSet resultSet) throws SQLException {
        return new Room(
                resultSet.getInt("room_id"),
                resultSet.getInt("hotel_id"),
                resultSet.getString("type"),
                resultSet.getDouble("price"),
                resultSet.getString("status")
        );
    }
}
