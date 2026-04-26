package dao;

import db.DBConnection;
import model.Booking;
import model.BookingRecord;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    private final RoomDAO roomDAO = new RoomDAO();

    public int createPaidBooking(Booking booking) throws SQLException {
        Connection connection = null;

        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            int bookingId = insertBooking(connection, booking);
            roomDAO.updateRoomStatus(connection, booking.getRoomId(), "Booked");

            connection.commit();
            return bookingId;
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    private int insertBooking(Connection connection, Booking booking) throws SQLException {
        boolean hasPeopleColumn = hasColumn(connection, "BOOKINGS", "NUMBER_OF_PEOPLE");
        String sql = hasPeopleColumn
                ? "INSERT INTO bookings (user_id, hotel_id, room_id, number_of_people, check_in, check_out, total_amount, payment_status, payment_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
                : "INSERT INTO bookings (user_id, hotel_id, room_id, check_in, check_out, total_amount, payment_status, payment_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, new String[]{"booking_id"})) {
            int parameterIndex = 1;
            statement.setInt(parameterIndex++, booking.getUserId());
            statement.setInt(parameterIndex++, booking.getHotelId());
            statement.setInt(parameterIndex++, booking.getRoomId());
            if (hasPeopleColumn) {
                statement.setInt(parameterIndex++, booking.getNumberOfPeople());
            }
            statement.setDate(parameterIndex++, Date.valueOf(booking.getCheckIn()));
            statement.setDate(parameterIndex++, Date.valueOf(booking.getCheckOut()));
            statement.setDouble(parameterIndex++, booking.getTotalAmount());
            statement.setString(parameterIndex++, booking.getPaymentStatus());
            statement.setString(parameterIndex, booking.getPaymentId());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException("Booking insert failed. No generated ID returned.");
    }

    public List<BookingRecord> getBookingsByHotel(int hotelId) throws SQLException {
        List<BookingRecord> bookings = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(buildBookingsByHotelSql(hasColumn(connection, "BOOKINGS", "NUMBER_OF_PEOPLE")))) {
            statement.setInt(1, hotelId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    bookings.add(new BookingRecord(
                            resultSet.getInt("booking_id"),
                            resultSet.getString("name"),
                            resultSet.getInt("room_id"),
                            resultSet.getInt("number_of_people"),
                            resultSet.getDate("check_in"),
                            resultSet.getDate("check_out"),
                            resultSet.getDouble("total_amount"),
                            resultSet.getString("payment_status")
                    ));
                }
            }
        }

        return bookings;
    }

    private String buildBookingsByHotelSql(boolean hasPeopleColumn) {
        String peopleProjection = hasPeopleColumn ? "NVL(b.number_of_people, 1)" : "1";
        return "SELECT b.booking_id, u.name, b.room_id, " + peopleProjection + " AS number_of_people, "
                + "b.check_in, b.check_out, b.total_amount, b.payment_status "
                + "FROM bookings b "
                + "JOIN users u ON b.user_id = u.user_id "
                + "WHERE b.hotel_id = ? "
                + "ORDER BY b.booking_id DESC";
    }

    public int getRoomIdForHotelBooking(int bookingId, int hotelId) throws SQLException {
        String sql = "SELECT room_id FROM bookings WHERE booking_id = ? AND hotel_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            statement.setInt(2, hotelId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("room_id");
                }
            }
        }

        return -1;
    }

    public void checkoutCustomer(int bookingId, int hotelId) throws SQLException {
        Connection connection = null;

        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            int roomId = getRoomIdForHotelBooking(connection, bookingId, hotelId);
            if (roomId == -1) {
                throw new SQLException("Booking not found for this hotel.");
            }

            roomDAO.updateRoomStatus(connection, roomId, "Available");
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    private int getRoomIdForHotelBooking(Connection connection, int bookingId, int hotelId) throws SQLException {
        String sql = "SELECT room_id FROM bookings WHERE booking_id = ? AND hotel_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            statement.setInt(2, hotelId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("room_id");
                }
            }
        }

        return -1;
    }

    private boolean hasColumn(Connection connection, String tableName, String columnName) throws SQLException {
        DatabaseMetaData metadata = connection.getMetaData();
        try (ResultSet columns = metadata.getColumns(null, null, tableName, columnName)) {
            return columns.next();
        }
    }
}
