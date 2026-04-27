package ui;

import dao.BookingDAO;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
                null,
                () -> UIStyle.switchFrame(this, new ViewBookingsFrame(hotelId)),
                () -> { },
                () -> UIStyle.switchFrame(this, new HotelLoginFrame())
        ), BorderLayout.WEST);
        UIStyle.styleField(bookingIdField);
            bookingIdField.setColumns(14);
            bookingIdField.setPreferredSize(new Dimension(280, 44));

        JPanel card = UIStyle.createCardPanel();
            card.setLayout(new BorderLayout(0, 16));
        card.add(UIStyle.createSectionTitle("Release Room"), BorderLayout.NORTH);

            JLabel helper = new JLabel("Hotel ID " + hotelId + " admin can checkout only bookings from this hotel.");
        helper.setFont(UIStyle.BODY_FONT);
        helper.setForeground(UIStyle.TEXT_MUTED);

            JLabel tip = new JLabel("Use a Booking ID from 'View Hotel Bookings' to release the room.");
            tip.setFont(UIStyle.BODY_FONT);
            tip.setForeground(UIStyle.TEXT_MUTED);

            JPanel infoPanel = new JPanel(new GridLayout(2, 1, 4, 4));
            infoPanel.setOpaque(false);
            infoPanel.add(helper);
            infoPanel.add(tip);

            JPanel formPanel = UIStyle.createCardPanel();
            formPanel.setLayout(new GridBagLayout());
            formPanel.setOpaque(true);

            GridBagConstraints formGbc = new GridBagConstraints();
            formGbc.insets = new Insets(8, 8, 8, 8);
            formGbc.fill = GridBagConstraints.HORIZONTAL;
            formGbc.anchor = GridBagConstraints.WEST;

            formGbc.gridx = 0;
            formGbc.gridy = 0;
            formGbc.weightx = 0;
            formPanel.add(UIStyle.createLabel("Booking ID"), formGbc);

            formGbc.gridx = 1;
            formGbc.weightx = 1;
            formPanel.add(bookingIdField, formGbc);

            JLabel hintLabel = new JLabel("Example: 23");
            hintLabel.setFont(UIStyle.BODY_FONT);
            hintLabel.setForeground(UIStyle.TEXT_MUTED);
            formGbc.gridx = 1;
            formGbc.gridy = 1;
            formGbc.weightx = 1;
            formPanel.add(hintLabel, formGbc);

        JButton checkoutButton = UIStyle.createButton("Checkout");
        checkoutButton.addActionListener(e -> checkoutCustomer());

        JButton viewBookingsButton = UIStyle.createDarkButton("View Hotel Bookings");
        viewBookingsButton.addActionListener(e -> UIStyle.switchFrame(this, new ViewBookingsFrame(hotelId)));

            JButton dashboardButton = UIStyle.createDarkButton("Back To Dashboard");
            dashboardButton.addActionListener(e -> UIStyle.switchFrame(this, new HotelDashboard(hotelId)));

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
            actions.setOpaque(false);
            actions.add(viewBookingsButton);
            actions.add(checkoutButton);
            actions.add(dashboardButton);

            formGbc.gridx = 1;
            formGbc.gridy = 2;
            formGbc.weightx = 1;
            formPanel.add(actions, formGbc);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
            center.add(infoPanel, BorderLayout.NORTH);
        center.add(formPanel, BorderLayout.CENTER);
        card.add(center, BorderLayout.CENTER);

        JPanel page = UIStyle.createPagePanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
            UIStyle.setFixedCardWidth(card, 980);
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
