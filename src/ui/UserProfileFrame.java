package ui;

import dao.UserDAO;
import dao.UserDAO.UserProfileDetails;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;

public class UserProfileFrame extends JFrame {
    private final int userId;

    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField contactField = new JTextField();
    private final JTextField emergencyContactField = new JTextField();
    private final JTextField addressField = new JTextField();
    private final JTextField cityField = new JTextField();
    private final JTextField stateField = new JTextField();
    private final JTextField pincodeField = new JTextField();
    private final JLabel profileCompletionLabel = new JLabel("Profile completion: 0%", JLabel.LEFT);
    private final JLabel summaryLabel = new JLabel("Welcome! Keep your profile updated for smoother bookings.", JLabel.LEFT);

    public UserProfileFrame(int userId) {
        this.userId = userId;

        setTitle("My Profile");
        setSize(1000, 650);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(UIStyle.createHeader("My Profile"), BorderLayout.NORTH);
        add(UIStyle.createSidebarPanel(
                () -> UIStyle.switchFrame(this, new Dashboard(userId)),
                () -> { },
                null,
                () -> UIStyle.switchFrame(this, new UserBookingsFrame(userId)),
                null,
                () -> UIStyle.switchFrame(this, new UserLoginFrame())
        ), BorderLayout.WEST);

            styleInputs();

        JPanel card = UIStyle.createCardPanel();
            card.setLayout(new BorderLayout(0, 14));
        card.add(UIStyle.createSectionTitle("Account Details"), BorderLayout.NORTH);

            summaryLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 17));
            summaryLabel.setForeground(UIStyle.TEXT_MUTED);

            JPanel summaryPanel = new JPanel(new BorderLayout());
            summaryPanel.setOpaque(false);
            summaryPanel.add(summaryLabel, BorderLayout.CENTER);

            JPanel formPanel = createFormPanel();

            JPanel centerPanel = new JPanel(new BorderLayout(0, 12));
            centerPanel.setOpaque(false);
            centerPanel.add(summaryPanel, BorderLayout.NORTH);
            centerPanel.add(formPanel, BorderLayout.CENTER);
            card.add(centerPanel, BorderLayout.CENTER);

            JPanel actionPanel = new JPanel(new BorderLayout());
            actionPanel.setOpaque(false);
            profileCompletionLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 15));
            profileCompletionLabel.setForeground(UIStyle.PRIMARY_BLUE_DARK);
            actionPanel.add(profileCompletionLabel, BorderLayout.WEST);

            JButton saveButton = UIStyle.createButton("Save Profile");
            saveButton.addActionListener(e -> saveProfile());

            JPanel rightButtons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));
            rightButtons.setOpaque(false);
            rightButtons.add(saveButton);
            actionPanel.add(rightButtons, BorderLayout.EAST);
            card.add(actionPanel, BorderLayout.SOUTH);

        JPanel page = UIStyle.createPagePanel();
        GridBagConstraints pageGbc = new GridBagConstraints();
        pageGbc.insets = new Insets(10, 10, 10, 10);
        pageGbc.fill = GridBagConstraints.BOTH;
        pageGbc.weightx = 1;
        pageGbc.weighty = 1;
        UIStyle.setFixedCardWidth(card, 920);
        page.add(card, pageGbc);
        add(page, BorderLayout.CENTER);

        loadProfile();
    }

    private void styleInputs() {
        UIStyle.styleField(nameField);
        UIStyle.styleField(emailField);
        UIStyle.styleField(contactField);
        UIStyle.styleField(emergencyContactField);
        UIStyle.styleField(addressField);
        UIStyle.styleField(cityField);
        UIStyle.styleField(stateField);
        UIStyle.styleField(pincodeField);
        emailField.setEditable(false);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int row = 0;
        addField(panel, gbc, row++, "User ID", new JLabel(String.valueOf(userId)));
        addField(panel, gbc, row++, "Full Name", nameField);
        addField(panel, gbc, row++, "Email", emailField);
        addField(panel, gbc, row++, "Contact Number", contactField);
        addField(panel, gbc, row++, "Emergency Contact", emergencyContactField);
        addField(panel, gbc, row++, "Address", addressField);

        JPanel locationRow = new JPanel(new GridLayout(1, 3, 8, 0));
        locationRow.setOpaque(false);
        locationRow.add(cityField);
        locationRow.add(stateField);
        locationRow.add(pincodeField);
        addField(panel, gbc, row++, "City / State / Pincode", locationRow);

        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, java.awt.Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.28;
        panel.add(UIStyle.createLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.72;
        panel.add(field, gbc);
    }

    private void loadProfile() {
        try {
            UserProfileDetails profile = new UserDAO().getUserProfileDetails(userId);
            if (profile == null) {
                UIStyle.showWarning(this, "Profile details are not available.");
                return;
            }

            nameField.setText(safe(profile.getName()));
            emailField.setText(safe(profile.getEmail()));
            contactField.setText(safe(profile.getContactNumber()));
            emergencyContactField.setText(safe(profile.getEmergencyContact()));
            addressField.setText(safe(profile.getAddressLine()));
            cityField.setText(safe(profile.getCity()));
            stateField.setText(safe(profile.getState()));
            pincodeField.setText(safe(profile.getPincode()));
            refreshCompletionLabel();
        } catch (SQLException e) {
            UIStyle.showDatabaseError(this, "Unable to load profile.", e);
        }
    }

    private void saveProfile() {
        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();
        String pincode = pincodeField.getText().trim();

        if (name.isEmpty()) {
            UIStyle.showWarning(this, "Name is required.");
            return;
        }

        if (!contact.isEmpty() && !contact.matches("^[0-9+\\- ]{7,15}$")) {
            UIStyle.showWarning(this, "Contact number should be 7 to 15 digits or symbols (+,-,space).");
            return;
        }

        if (!pincode.isEmpty() && !pincode.matches("^[0-9]{4,10}$")) {
            UIStyle.showWarning(this, "Pincode must contain 4 to 10 digits.");
            return;
        }

        UserProfileDetails updated = new UserProfileDetails();
        updated.setUserId(userId);
        updated.setName(name);
        updated.setContactNumber(contactField.getText());
        updated.setEmergencyContact(emergencyContactField.getText());
        updated.setAddressLine(addressField.getText());
        updated.setCity(cityField.getText());
        updated.setState(stateField.getText());
        updated.setPincode(pincodeField.getText());

        try {
            new UserDAO().updateUserProfileDetails(updated);
            refreshCompletionLabel();
            UIStyle.showInfo(this, "Profile updated successfully.");
        } catch (SQLException e) {
            UIStyle.showDatabaseError(this, "Unable to update profile.", e);
        }
    }

    private void refreshCompletionLabel() {
        int total = 7;
        int filled = 0;

        if (!nameField.getText().trim().isEmpty()) {
            filled++;
        }
        if (!contactField.getText().trim().isEmpty()) {
            filled++;
        }
        if (!emergencyContactField.getText().trim().isEmpty()) {
            filled++;
        }
        if (!addressField.getText().trim().isEmpty()) {
            filled++;
        }
        if (!cityField.getText().trim().isEmpty()) {
            filled++;
        }
        if (!stateField.getText().trim().isEmpty()) {
            filled++;
        }
        if (!pincodeField.getText().trim().isEmpty()) {
            filled++;
        }

        int percentage = (filled * 100) / total;
        profileCompletionLabel.setText("Profile completion: " + percentage + "%");
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
