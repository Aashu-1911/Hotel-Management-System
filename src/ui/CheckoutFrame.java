package ui;

import dao.BookingDAO;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;

public class CheckoutFrame extends JFrame {
    private final int hotelId;
    private final JTextField bookingIdField = new JTextField();

    public CheckoutFrame(int hotelId) {
        this.hotelId = hotelId;

        setTitle("Checkout Customer");
        setSize(900, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        add(UIStyle.createHeader("Checkout Customer"), BorderLayout.NORTH);
        add(UIStyle.createSidebarPanel(
                () -> {
                    UIStyle.switchFrame(this, new HotelDashboard(hotelId));
                },
            () -> UIStyle.showInfo(this, "Admin Profile\nHotel ID: " + hotelId),
                () -> UIStyle.showInfo(this, "Customer bookings must be created from Customer Login."),
                () -> UIStyle.switchFrame(this, new ViewBookingsFrame(hotelId)),
                () -> { },
                () -> UIStyle.switchFrame(this, new HotelLoginFrame())
        ), BorderLayout.WEST);
        UIStyle.styleField(bookingIdField);

        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout());
        card.add(UIStyle.createSectionTitle("Release Room"), BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 16, 16));
        formPanel.setOpaque(false);
        formPanel.add(UIStyle.createLabel("Booking ID"));
        formPanel.add(bookingIdField);

        JButton checkoutButton = UIStyle.createButton("Checkout");
        checkoutButton.addActionListener(e -> checkoutCustomer());
        formPanel.add(new JLabel(""));
        formPanel.add(checkoutButton);
        card.add(formPanel, BorderLayout.CENTER);

        JPanel page = UIStyle.createPagePanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        UIStyle.setFixedCardWidth(card, 520);
        page.add(card, gbc);
        add(page, BorderLayout.CENTER);
    }

    private void checkoutCustomer() {
        String bookingIdText = bookingIdField.getText().trim();

        if (bookingIdText.isEmpty()) {
            UIStyle.showWarning(this, "Please enter booking ID.");
            return;
        }

        try {
            int bookingId = Integer.parseInt(bookingIdText);
            if (bookingId <= 0) {
                UIStyle.showWarning(this, "Booking ID must be greater than zero.");
                return;
            }
            new BookingDAO().checkoutCustomer(bookingId, hotelId);
            UIStyle.showInfo(this, "Checkout complete. Room is now Available.");
            dispose();
        } catch (NumberFormatException e) {
            UIStyle.showWarning(this, "Booking ID must be a number.");
        } catch (SQLException e) {
            UIStyle.showDatabaseError(this, "Checkout failed.", e);
        }
    }
}
