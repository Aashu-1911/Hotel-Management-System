package ui;

import dao.BookingDAO;
import model.BookingRecord;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.List;

public class ViewBookingsFrame extends JFrame {
    private final int hotelId;
    private final DefaultTableModel tableModel;

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
                () -> UIStyle.showInfo(this, "Customer bookings must be created from Customer Login."),
                () -> { },
                () -> UIStyle.switchFrame(this, new CheckoutFrame(hotelId)),
                () -> UIStyle.switchFrame(this, new HotelLoginFrame())
        ), BorderLayout.WEST);

        JPanel page = UIStyle.createContentPanel();
        page.setLayout(new BorderLayout());

        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout());
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
        scrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(UIStyle.BORDER));
        card.add(scrollPane, BorderLayout.CENTER);
        page.add(card, BorderLayout.CENTER);
        add(page, BorderLayout.CENTER);

        loadBookings();
    }

    private void loadBookings() {
        try {
            List<BookingRecord> bookings = new BookingDAO().getBookingsByHotel(hotelId);
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
            UIStyle.showDatabaseError(this, "Unable to load bookings.", e);
        }
    }
}
