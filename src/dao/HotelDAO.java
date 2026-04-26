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
    public List<Hotel> getAllHotels() throws SQLException {
        List<Hotel> hotels = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(buildHotelListSql(hasColumn(connection, "HOTELS", "RATING")));
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

        return hotels;
    }

    public List<HotelCatalog> getHotelCatalog() throws SQLException {
        List<HotelCatalog> hotels = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(buildHotelCatalogSql(hasColumn(connection, "HOTELS", "RATING")));
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

        return hotels;
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
