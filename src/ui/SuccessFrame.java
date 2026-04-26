package ui;

import model.Hotel;
import model.Room;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;

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

        JPanel billPanel = new JPanel(new GridLayout(7, 2, 14, 14));
        billPanel.setOpaque(false);
        addRow(billPanel, "Booking ID", String.valueOf(bookingId));
        addRow(billPanel, "User ID", String.valueOf(userId));
        addRow(billPanel, "Hotel", hotel.getHotelName());
        addRow(billPanel, "Room Number", String.valueOf(room.getRoomId()));
        addRow(billPanel, "Guests", String.valueOf(numberOfPeople));
        addRow(billPanel, "Total Amount", "Rs. " + totalAmount);
        addRow(billPanel, "Payment ID", paymentId);
        card.add(billPanel, BorderLayout.CENTER);

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
}
