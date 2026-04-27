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
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;

public class ViewBookingsFrame extends JFrame {
    private final int hotelId;
    private final DefaultTableModel tableModel;
    private final JLabel totalBookingsValue = new JLabel("0");
    private final JLabel totalRevenueValue = new JLabel("Rs. 0.00");

    public ViewBookingsFrame(int hotelId) {
        this.hotelId = hotelId;

        setTitle("View Hotel Bookings");
        setSize(1100, 700);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        add(UIStyle.createHeader("Hotel Bookings"), BorderLayout.NORTH);
        add(UIStyle.createSidebarPanel(
                () -> {
                    UIStyle.switchFrame(this, new HotelDashboard(hotelId));
                },
                () -> UIStyle.showInfo(this, "Admin Profile\nHotel ID: " + hotelId),
                null,
                () -> { },
                () -> UIStyle.switchFrame(this, new CheckoutFrame(hotelId)),
                () -> UIStyle.switchFrame(this, new HotelLoginFrame())
        ), BorderLayout.WEST);

        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout(0, 12));
        card.add(UIStyle.createSectionTitle("Current and Past Bookings"), BorderLayout.NORTH);

        String[] columns = {"Booking ID", "Customer Name", "Room ID", "People", "Check-in", "Check-out", "Amount", "Payment Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable bookingsTable = new JTable(tableModel);
        UIStyle.styleTable(bookingsTable);
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(UIStyle.createCardBorder());

        JPanel summaryPanel = new JPanel(new java.awt.GridLayout(1, 3, 10, 0));
        summaryPanel.setOpaque(false);

        JLabel scopeLabel = new JLabel("Showing bookings for Hotel ID: " + hotelId);
        scopeLabel.setFont(UIStyle.LABEL_FONT);
        scopeLabel.setForeground(UIStyle.TEXT_DARK);

        totalBookingsValue.setFont(UIStyle.LABEL_FONT);
        totalBookingsValue.setForeground(UIStyle.PRIMARY_BLUE_DARK);
        totalRevenueValue.setFont(UIStyle.LABEL_FONT);
        totalRevenueValue.setForeground(UIStyle.PRIMARY_BLUE_DARK);

        summaryPanel.add(scopeLabel);
        summaryPanel.add(totalBookingsValue);
        summaryPanel.add(totalRevenueValue);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 12));
        centerPanel.setOpaque(false);
        centerPanel.add(summaryPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        card.add(centerPanel, BorderLayout.CENTER);

        JButton refreshButton = UIStyle.createDarkButton("Refresh");
        JButton checkoutButton = UIStyle.createButton("Go To Checkout");
        JButton dashboardButton = UIStyle.createDarkButton("Back To Dashboard");

        refreshButton.addActionListener(e -> loadBookings());
        checkoutButton.addActionListener(e -> UIStyle.switchFrame(this, new CheckoutFrame(hotelId)));
        dashboardButton.addActionListener(e -> UIStyle.switchFrame(this, new HotelDashboard(hotelId)));

        JPanel actions = new JPanel(new java.awt.GridLayout(1, 3, 12, 0));
        actions.setOpaque(false);
        actions.add(refreshButton);
        actions.add(checkoutButton);
        actions.add(dashboardButton);

        JPanel actionWrap = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 14));
        actionWrap.setOpaque(false);
        actionWrap.add(actions);
        card.add(actionWrap, BorderLayout.SOUTH);

        JPanel page = UIStyle.createPagePanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        UIStyle.setFixedCardWidth(card, 1120);
        page.add(card, gbc);
        add(page, BorderLayout.CENTER);

        loadBookings();
    }

    private void loadBookings() {
        try {
            List<BookingRecord> bookings = new BookingDAO().getBookingsByHotel(hotelId);
            tableModel.setRowCount(0);
            double totalRevenue = 0.0;

            for (BookingRecord booking : bookings) {
                totalRevenue += booking.getTotalAmount();
                tableModel.addRow(new Object[]{
                        booking.getBookingId(),
                        booking.getCustomerName(),
                        booking.getRoomId(),
                        booking.getNumberOfPeople(),
                        booking.getCheckIn(),
                        booking.getCheckOut(),
                        booking.getTotalAmount(),
                        booking.getPaymentStatus()
                });
            }

            totalBookingsValue.setText("Total Bookings: " + bookings.size());
            totalRevenueValue.setText("Total Revenue: Rs. " + String.format("%.2f", totalRevenue));
        } catch (SQLException e) {
            UIStyle.showDatabaseError(this, "Unable to load bookings.", e);
        }
    }
}
