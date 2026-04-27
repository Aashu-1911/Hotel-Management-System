package dao;

import db.DBConnection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public static class UserProfileDetails {
        private int userId;
        private String name;
        private String email;
        private String contactNumber;
        private String emergencyContact;
        private String addressLine;
        private String city;
        private String state;
        private String pincode;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getContactNumber() {
            return contactNumber;
        }

        public void setContactNumber(String contactNumber) {
            this.contactNumber = contactNumber;
        }

        public String getEmergencyContact() {
            return emergencyContact;
        }

        public void setEmergencyContact(String emergencyContact) {
            this.emergencyContact = emergencyContact;
        }

        public String getAddressLine() {
            return addressLine;
        }

        public void setAddressLine(String addressLine) {
            this.addressLine = addressLine;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }
    }

    public void registerUser(String name, String email, String password) throws SQLException {
        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, password);
            statement.executeUpdate();
        }
    }

    public int loginUser(String email, String password) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE email = ? AND password = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("user_id");
                }
            }
        }

        return -1;
    }

    public String[] getUserProfile(int userId) throws SQLException {
        UserProfileDetails details = getUserProfileDetails(userId);
        if (details == null) {
            return null;
        }

        return new String[]{
                details.getName(),
                details.getEmail()
        };
    }

    public UserProfileDetails getUserProfileDetails(int userId) throws SQLException {
        String sql = "SELECT user_id, name, email, "
                + "NVL(contact_number, '') AS contact_number, "
                + "NVL(emergency_contact, '') AS emergency_contact, "
                + "NVL(address_line, '') AS address_line, "
                + "NVL(city, '') AS city, "
                + "NVL(state, '') AS state, "
                + "NVL(pincode, '') AS pincode "
                + "FROM users WHERE user_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ensureProfileColumns(connection);
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    UserProfileDetails details = new UserProfileDetails();
                    details.setUserId(resultSet.getInt("user_id"));
                    details.setName(resultSet.getString("name"));
                    details.setEmail(resultSet.getString("email"));
                    details.setContactNumber(resultSet.getString("contact_number"));
                    details.setEmergencyContact(resultSet.getString("emergency_contact"));
                    details.setAddressLine(resultSet.getString("address_line"));
                    details.setCity(resultSet.getString("city"));
                    details.setState(resultSet.getString("state"));
                    details.setPincode(resultSet.getString("pincode"));
                    return details;
                }
            }
        }

        return null;
    }

    public void updateUserProfileDetails(UserProfileDetails details) throws SQLException {
        String sql = "UPDATE users SET name = ?, contact_number = ?, emergency_contact = ?, "
                + "address_line = ?, city = ?, state = ?, pincode = ? WHERE user_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ensureProfileColumns(connection);

            statement.setString(1, normalize(details.getName()));
            statement.setString(2, normalize(details.getContactNumber()));
            statement.setString(3, normalize(details.getEmergencyContact()));
            statement.setString(4, normalize(details.getAddressLine()));
            statement.setString(5, normalize(details.getCity()));
            statement.setString(6, normalize(details.getState()));
            statement.setString(7, normalize(details.getPincode()));
            statement.setInt(8, details.getUserId());
            statement.executeUpdate();
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private void ensureProfileColumns(Connection connection) throws SQLException {
        ensureColumn(connection, "CONTACT_NUMBER", "VARCHAR2(20)");
        ensureColumn(connection, "EMERGENCY_CONTACT", "VARCHAR2(100)");
        ensureColumn(connection, "ADDRESS_LINE", "VARCHAR2(180)");
        ensureColumn(connection, "CITY", "VARCHAR2(80)");
        ensureColumn(connection, "STATE", "VARCHAR2(80)");
        ensureColumn(connection, "PINCODE", "VARCHAR2(12)");
    }

    private void ensureColumn(Connection connection, String columnName, String dataType) throws SQLException {
        if (hasColumn(connection, "USERS", columnName)) {
            return;
        }

        String alterSql = "ALTER TABLE users ADD (" + columnName + " " + dataType + ")";
        try (PreparedStatement statement = connection.prepareStatement(alterSql)) {
            statement.executeUpdate();
        }
    }

    private boolean hasColumn(Connection connection, String tableName, String columnName) throws SQLException {
        DatabaseMetaData metadata = connection.getMetaData();
        try (ResultSet columns = metadata.getColumns(null, null, tableName, columnName)) {
            if (columns.next()) {
                return true;
            }
        }

        try (ResultSet columns = metadata.getColumns(null, null, tableName.toLowerCase(), columnName.toLowerCase())) {
            return columns.next();
        }
    }
}
