package dao;

import model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CustomerDAO {
    public int insertCustomer(Connection connection, Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (name, phone, email) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"})) {
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getPhone());
            statement.setString(3, customer.getEmail());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException("Customer insert failed. No generated ID returned.");
    }

    public int insertCustomer(Customer customer) throws SQLException {
        try (Connection connection = db.DBConnection.getConnection()) {
            return insertCustomer(connection, customer);
        }
    }
}
