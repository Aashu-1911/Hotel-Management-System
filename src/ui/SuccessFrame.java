package ui;

import dao.UserDAO;
import dao.UserDAO.UserProfileDetails;
import model.Hotel;
import model.Room;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SuccessFrame extends JFrame {
    public SuccessFrame(int bookingId, int userId, Hotel hotel, Room room, int numberOfPeople, double totalAmount, String paymentId) {
        setTitle("Booking Success");
        setSize(900, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        add(UIStyle.createHeader("Booking Confirmed"), BorderLayout.NORTH);

        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout());
        card.add(UIStyle.createSectionTitle("Booking Bill"), BorderLayout.NORTH);

        UserProfileDetails profile = loadUserProfile(userId);
        String customerName = nonEmpty(profile == null ? null : profile.getName(), "Not Provided");
        String customerEmail = nonEmpty(profile == null ? null : profile.getEmail(), "Not Provided");
        String customerContact = nonEmpty(profile == null ? null : profile.getContactNumber(), "Not Provided");

        JPanel billPanel = new JPanel(new GridLayout(10, 2, 14, 14));
        billPanel.setOpaque(false);
        addRow(billPanel, "Booking ID", String.valueOf(bookingId));
        addRow(billPanel, "User ID", String.valueOf(userId));
        addRow(billPanel, "Customer Name", customerName);
        addRow(billPanel, "Contact Number", customerContact);
        addRow(billPanel, "Email", customerEmail);
        addRow(billPanel, "Hotel", hotel.getHotelName());
        addRow(billPanel, "Room Number", String.valueOf(room.getRoomId()));
        addRow(billPanel, "Guests", String.valueOf(numberOfPeople));
        addRow(billPanel, "Total Amount", "Rs. " + totalAmount);
        addRow(billPanel, "Payment ID", paymentId);
        card.add(billPanel, BorderLayout.CENTER);

        String billText = buildBillText(bookingId, userId, customerName, customerContact, customerEmail,
            hotel, room, numberOfPeople, totalAmount, paymentId);
        JButton downloadBillButton = UIStyle.createButton("Download Bill");
        downloadBillButton.addActionListener(e -> downloadBill(billText, bookingId));

        JPanel actionPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 16));
        actionPanel.setOpaque(false);
        actionPanel.add(downloadBillButton);
        card.add(actionPanel, BorderLayout.SOUTH);

        JPanel page = UIStyle.createPagePanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        UIStyle.setFixedCardWidth(card, 640);
        page.add(card, gbc);
        add(page, BorderLayout.CENTER);
    }

    private void addRow(JPanel panel, String label, String value) {
        JLabel labelComponent = UIStyle.createLabel(label);
        JLabel valueComponent = new JLabel(value);
        UIStyle.styleDetailsLabel(valueComponent);
        panel.add(labelComponent);
        panel.add(valueComponent);
    }

    private String buildBillText(int bookingId, int userId, String customerName, String customerContact,
                                 String customerEmail, Hotel hotel, Room room,
                                 int numberOfPeople, double totalAmount, String paymentId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Hotel Booking System - Booking Bill\n"
                + "Generated On: " + LocalDateTime.now().format(formatter) + "\n"
                + "----------------------------------------\n"
                + "Booking ID : " + bookingId + "\n"
                + "User ID    : " + userId + "\n"
                + "Customer Name : " + customerName + "\n"
                + "Contact Number: " + customerContact + "\n"
                + "Email         : " + customerEmail + "\n"
                + "Hotel      : " + hotel.getHotelName() + "\n"
                + "Room Number: " + room.getRoomId() + "\n"
                + "Guests     : " + numberOfPeople + "\n"
                + "Total Amount: Rs. " + String.format("%.2f", totalAmount) + "\n"
                + "Payment ID : " + paymentId + "\n"
                + "----------------------------------------\n";
    }

    private UserProfileDetails loadUserProfile(int userId) {
        try {
            return new UserDAO().getUserProfileDetails(userId);
        } catch (SQLException e) {
            return null;
        }
    }

    private String nonEmpty(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim();
    }

    private void downloadBill(String billText, int bookingId) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Bill");
        chooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        chooser.setSelectedFile(new File("booking_bill_" + bookingId + ".txt"));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = chooser.getSelectedFile();
        if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
            selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
        }

        try (FileWriter writer = new FileWriter(selectedFile)) {
            writer.write(billText);
            UIStyle.showInfo(this, "Bill downloaded successfully to:\n" + selectedFile.getAbsolutePath());
        } catch (IOException e) {
            UIStyle.showError(this, "Unable to save bill. Please try again.");
        }
    }
}
