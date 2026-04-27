package ui;

import dao.BookingDAO;
import model.BookingRecord;

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

public class UserBookingsFrame extends JFrame {
    private final int userId;
    private final DefaultTableModel tableModel;

    public UserBookingsFrame(int userId) {
        this.userId = userId;

        setTitle("My Bookings");
        setSize(1000, 650);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(UIStyle.createHeader("My Bookings"), BorderLayout.NORTH);
        add(UIStyle.createSidebarPanel(
                () -> UIStyle.switchFrame(this, new Dashboard(userId)),
                () -> UIStyle.switchFrame(this, new UserProfileFrame(userId)),
                null,
                () -> { },
                null,
                () -> UIStyle.switchFrame(this, new UserLoginFrame())
        ), BorderLayout.WEST);

        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout());
        card.add(UIStyle.createSectionTitle("Your Booking History"), BorderLayout.NORTH);

        String[] columns = {"Booking ID", "Hotel Name", "Room ID", "People", "Check-in", "Check-out", "Amount", "Payment Status"};
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
        card.add(scrollPane, BorderLayout.CENTER);

        JLabel footer = new JLabel("Showing bookings for your account only.", JLabel.LEFT);
        UIStyle.styleDetailsLabel(footer);
        footer.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 4, 0, 0));
        card.add(footer, BorderLayout.SOUTH);

        JPanel page = UIStyle.createPagePanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        UIStyle.setFixedCardWidth(card, 1080);
        page.add(card, gbc);
        add(page, BorderLayout.CENTER);

        loadBookings();
    }

    private void loadBookings() {
        try {
            List<BookingRecord> bookings = new BookingDAO().getBookingsByUser(userId);
            tableModel.setRowCount(0);

            for (BookingRecord booking : bookings) {
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
        } catch (SQLException e) {
            UIStyle.showDatabaseError(this, "Unable to load your bookings.", e);
        }
    }
}
