package dao;

import db.DBConnection;
import model.HotelCatalog;
import model.Hotel;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HotelDAO {
    private static final String[][] FAMOUS_INDIAN_HOTELS = {
            {"The Leela Palace", "New Delhi", "4.8", "7800"},
            {"ITC Grand Chola", "Chennai", "4.7", "7200"},
            {"Rambagh Palace", "Jaipur", "4.9", "9800"},
            {"Taj Falaknuma Palace", "Hyderabad", "4.9", "11000"},
            {"The Oberoi Udaivilas", "Udaipur", "4.9", "12500"},
            {"Umaid Bhawan Palace", "Jodhpur", "4.8", "10800"}
    };

    public List<Hotel> getAllHotels() throws SQLException {
        List<Hotel> hotels = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection()) {
            ensureFamousIndianHotels(connection);

            try (PreparedStatement statement = connection.prepareStatement(buildHotelListSql(hasColumn(connection, "HOTELS", "RATING")));
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    hotels.add(new Hotel(
                            resultSet.getInt("hotel_id"),
                            resultSet.getString("hotel_name"),
                            resultSet.getString("address"),
                            resultSet.getDouble("rating")
                    ));
                }
            }
        }

        return hotels;
    }

    public List<HotelCatalog> getHotelCatalog() throws SQLException {
        List<HotelCatalog> hotels = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection()) {
            ensureFamousIndianHotels(connection);

            try (PreparedStatement statement = connection.prepareStatement(buildHotelCatalogSql(hasColumn(connection, "HOTELS", "RATING")));
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    double startingPrice = resultSet.getDouble("starting_price");
                    Double startingPriceValue = resultSet.wasNull() ? null : startingPrice;

                    hotels.add(new HotelCatalog(
                            resultSet.getInt("hotel_id"),
                            resultSet.getString("hotel_name"),
                            resultSet.getString("address"),
                            resultSet.getDouble("rating"),
                            startingPriceValue,
                            resultSet.getInt("available_rooms"),
                            resultSet.getInt("room_types")
                    ));
                }
            }
        }

        return hotels;
    }

    private void ensureFamousIndianHotels(Connection connection) throws SQLException {
        boolean hasRatingColumn = hasColumn(connection, "HOTELS", "RATING");

        for (int i = 0; i < FAMOUS_INDIAN_HOTELS.length; i++) {
            String hotelName = FAMOUS_INDIAN_HOTELS[i][0];
            String address = FAMOUS_INDIAN_HOTELS[i][1];
            double rating = Double.parseDouble(FAMOUS_INDIAN_HOTELS[i][2]);
            double basePrice = Double.parseDouble(FAMOUS_INDIAN_HOTELS[i][3]);

            int hotelId = upsertHotelAndGetId(connection, 11 + i, hotelName, address, rating, hasRatingColumn);
            ensureStarterRooms(connection, hotelId, basePrice);
        }
    }

    private int upsertHotelAndGetId(Connection connection, int preferredHotelId, String hotelName,
                                    String address, double rating, boolean hasRatingColumn) throws SQLException {
        int existingHotelId = getHotelIdByName(connection, hotelName);
        if (existingHotelId > 0) {
            return existingHotelId;
        }

        int hotelIdToUse = nextAvailableHotelId(connection, preferredHotelId);
        String sql = hasRatingColumn
                ? "INSERT INTO hotels (hotel_id, hotel_name, address, rating) VALUES (?, ?, ?, ?)"
                : "INSERT INTO hotels (hotel_id, hotel_name, address) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, hotelIdToUse);
            statement.setString(2, hotelName);
            statement.setString(3, address);
            if (hasRatingColumn) {
                statement.setDouble(4, rating);
            }
            statement.executeUpdate();
        }

        return hotelIdToUse;
    }

    private int getHotelIdByName(Connection connection, String hotelName) throws SQLException {
        String sql = "SELECT hotel_id FROM hotels WHERE LOWER(hotel_name) = LOWER(?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, hotelName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("hotel_id");
                }
            }
        }

        return -1;
    }

    private int nextAvailableHotelId(Connection connection, int startingFrom) throws SQLException {
        int candidate = startingFrom;
        while (hotelIdExists(connection, candidate)) {
            candidate++;
        }
        return candidate;
    }

    private boolean hotelIdExists(Connection connection, int hotelId) throws SQLException {
        String sql = "SELECT 1 FROM hotels WHERE hotel_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, hotelId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private void ensureStarterRooms(Connection connection, int hotelId, double basePrice) throws SQLException {
        if (hasAnyRoomForHotel(connection, hotelId)) {
            return;
        }

        insertRoom(connection, hotelId, "Standard", basePrice);
        insertRoom(connection, hotelId, "Deluxe", basePrice + 2200);
        insertRoom(connection, hotelId, "Suite", basePrice + 5200);
    }

    private boolean hasAnyRoomForHotel(Connection connection, int hotelId) throws SQLException {
        String sql = "SELECT 1 FROM rooms WHERE hotel_id = ? AND ROWNUM = 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, hotelId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private void insertRoom(Connection connection, int hotelId, String roomType, double price) throws SQLException {
        String sql = "INSERT INTO rooms (room_id, type, price, status, hotel_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, nextRoomId(connection));
            statement.setString(2, roomType);
            statement.setDouble(3, price);
            statement.setString(4, "Available");
            statement.setInt(5, hotelId);
            statement.executeUpdate();
        }
    }

    private int nextRoomId(Connection connection) throws SQLException {
        String sql = "SELECT NVL(MAX(room_id), 100) + 1 AS next_room_id FROM rooms";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("next_room_id");
            }
        }

        return 101;
    }

    private String buildHotelListSql(boolean hasRatingColumn) {
        String ratingProjection = hasRatingColumn ? "NVL(rating, 4.0)" : "4.0";
        return "SELECT hotel_id, hotel_name, address, " + ratingProjection + " AS rating "
                + "FROM hotels ORDER BY hotel_name";
    }

    private String buildHotelCatalogSql(boolean hasRatingColumn) {
        String ratingProjection = hasRatingColumn ? "NVL(h.rating, 4.0)" : "4.0";
        return "SELECT h.hotel_id, h.hotel_name, h.address, " + ratingProjection + " AS rating, "
                + "MIN(CASE WHEN r.status = 'Available' THEN r.price END) AS starting_price, "
                + "SUM(CASE WHEN r.status = 'Available' THEN 1 ELSE 0 END) AS available_rooms, "
                + "COUNT(DISTINCT CASE WHEN r.status = 'Available' THEN r.type END) AS room_types "
                + "FROM hotels h "
                + "LEFT JOIN rooms r ON h.hotel_id = r.hotel_id "
                + "GROUP BY h.hotel_id, h.hotel_name, h.address, " + ratingProjection + " "
                + "ORDER BY h.hotel_name";
    }

    private boolean hasColumn(Connection connection, String tableName, String columnName) throws SQLException {
        DatabaseMetaData metadata = connection.getMetaData();
        try (ResultSet columns = metadata.getColumns(null, null, tableName, columnName)) {
            return columns.next();
        }
    }
}
