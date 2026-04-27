package ui;

import dao.BookingDAO;
import model.BookingRecord;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;

public class HotelDashboard extends JFrame {
    private final int hotelId;
    private final DefaultTableModel bookingTableModel;
    private final JLabel totalBookingsValue = createStatValue();
    private final JLabel totalRevenueValue = createStatValue();
    private final JLabel activeBookingsValue = createStatValue();

    public HotelDashboard(int hotelId) {
        this.hotelId = hotelId;

        setTitle("Hotel Admin Dashboard");
        setSize(1000, 650);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(UIStyle.createHeader("Hotel Admin Dashboard"), BorderLayout.NORTH);
        add(UIStyle.createSidebarPanel(
                () -> { },
                () -> UIStyle.showInfo(this, "Admin Profile\nHotel ID: " + hotelId),
                null,
                () -> UIStyle.switchFrame(this, new ViewBookingsFrame(hotelId)),
                () -> UIStyle.switchFrame(this, new CheckoutFrame(hotelId)),
                () -> UIStyle.switchFrame(this, new HotelLoginFrame())
        ), BorderLayout.WEST);

        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout());
        card.add(UIStyle.createSectionTitle("Hotel Operations Overview"), BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        statsPanel.setOpaque(false);
        statsPanel.add(createStatCard("Total Bookings", totalBookingsValue));
        statsPanel.add(createStatCard("Active Bookings", activeBookingsValue));
        statsPanel.add(createStatCard("Revenue", totalRevenueValue));

        String[] columns = {"Booking ID", "Customer", "Room", "Check-in", "Check-out", "Amount", "Status"};
        bookingTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable recentBookingsTable = new JTable(bookingTableModel);
        UIStyle.styleTable(recentBookingsTable);
        JScrollPane tableScrollPane = new JScrollPane(recentBookingsTable);
        tableScrollPane.setBorder(UIStyle.createCardBorder());
        tableScrollPane.setPreferredSize(new Dimension(980, 360));

        JPanel centerPanel = new JPanel(new BorderLayout(0, 14));
        centerPanel.setOpaque(false);
        centerPanel.add(statsPanel, BorderLayout.NORTH);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        card.add(centerPanel, BorderLayout.CENTER);

        JButton viewBookingsButton = UIStyle.createButton("View All Hotel Bookings");
        JButton checkoutButton = UIStyle.createButton("Checkout Customer");
        JButton refreshButton = UIStyle.createDarkButton("Refresh Dashboard");
        JButton logoutButton = UIStyle.createDarkButton("Logout");

        viewBookingsButton.addActionListener(e -> UIStyle.switchFrame(this, new ViewBookingsFrame(hotelId)));
        checkoutButton.addActionListener(e -> UIStyle.switchFrame(this, new CheckoutFrame(hotelId)));
        refreshButton.addActionListener(e -> loadDashboardData());
        logoutButton.addActionListener(e -> UIStyle.switchFrame(this, new HotelLoginFrame()));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 14, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(viewBookingsButton);
        buttonPanel.add(checkoutButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(logoutButton);

        JPanel actionPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 18));
        actionPanel.setOpaque(false);
        actionPanel.add(buttonPanel);
        card.add(actionPanel, BorderLayout.SOUTH);

        JPanel page = UIStyle.createPagePanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        UIStyle.setFixedCardWidth(card, 1120);
        page.add(card, gbc);
        add(page, BorderLayout.CENTER);

        loadDashboardData();
    }

    private JPanel createStatCard(String label, JLabel valueLabel) {
        JPanel statCard = UIStyle.createCardPanel();
        statCard.setLayout(new BorderLayout(0, 6));
        JLabel title = UIStyle.createLabel(label);
        title.setHorizontalAlignment(JLabel.CENTER);
        valueLabel.setHorizontalAlignment(JLabel.CENTER);
        statCard.add(title, BorderLayout.NORTH);
        statCard.add(valueLabel, BorderLayout.CENTER);
        return statCard;
    }

    private JLabel createStatValue() {
        JLabel value = new JLabel("0");
        value.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 24));
        value.setForeground(UIStyle.PRIMARY_BLUE_DARK);
        return value;
    }

    private void loadDashboardData() {
        try {
            List<BookingRecord> bookings = new BookingDAO().getBookingsByHotel(hotelId);

            bookingTableModel.setRowCount(0);
            double totalRevenue = 0.0;
            int activeBookings = 0;

            for (BookingRecord booking : bookings) {
                totalRevenue += booking.getTotalAmount();
                if (!"checked-out".equalsIgnoreCase(booking.getPaymentStatus())) {
                    activeBookings++;
                }

                bookingTableModel.addRow(new Object[]{
                        booking.getBookingId(),
                        booking.getCustomerName(),
                        booking.getRoomId(),
                        booking.getCheckIn(),
                        booking.getCheckOut(),
                        booking.getTotalAmount(),
                        booking.getPaymentStatus()
                });
            }

            totalBookingsValue.setText(String.valueOf(bookings.size()));
            activeBookingsValue.setText(String.valueOf(activeBookings));
            totalRevenueValue.setText("Rs. " + String.format("%.2f", totalRevenue));
        } catch (SQLException e) {
            UIStyle.showDatabaseError(this, "Unable to load dashboard data.", e);
        }
    }
}
