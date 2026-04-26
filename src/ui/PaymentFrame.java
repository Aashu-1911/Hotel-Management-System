package ui;

import dao.BookingDAO;
import model.Booking;
import model.Hotel;
import model.Room;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Random;

public class PaymentFrame extends JFrame {
    private final JFrame bookingForm;
    private final int userId;
    private final Hotel hotel;
    private final Room room;
    private final int numberOfPeople;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final double totalAmount;

    public PaymentFrame(JFrame dashboard, JFrame bookingForm, int userId, Hotel hotel, Room room,
                        int numberOfPeople, LocalDate checkIn, LocalDate checkOut, double totalAmount) {
        this.bookingForm = bookingForm;
        this.userId = userId;
        this.hotel = hotel;
        this.room = room;
        this.numberOfPeople = numberOfPeople;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalAmount = totalAmount;

        setTitle("Razorpay Payment - Test Mode");
        setSize(900, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        add(UIStyle.createHeader("Razorpay Test Payment"), BorderLayout.NORTH);

        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout());
        card.add(UIStyle.createSectionTitle("Payment Summary"), BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel(new java.awt.GridLayout(5, 1, 12, 12));
        detailsPanel.setOpaque(false);
        JLabel hotelLabel = new JLabel("Hotel: " + hotel.getHotelName(), JLabel.CENTER);
        JLabel roomLabel = new JLabel("Room: " + room.getRoomId() + " (" + room.getType() + ")", JLabel.CENTER);
        JLabel peopleLabel = new JLabel("Guests: " + numberOfPeople, JLabel.CENTER);
        JLabel amountLabel = new JLabel("Amount: Rs. " + totalAmount, JLabel.CENTER);
        JLabel modeLabel = new JLabel("Mode: Test Payment", JLabel.CENTER);
        UIStyle.styleDetailsLabel(hotelLabel);
        UIStyle.styleDetailsLabel(roomLabel);
        UIStyle.styleDetailsLabel(peopleLabel);
        UIStyle.styleDetailsLabel(amountLabel);
        UIStyle.styleDetailsLabel(modeLabel);
        detailsPanel.add(hotelLabel);
        detailsPanel.add(roomLabel);
        detailsPanel.add(peopleLabel);
        detailsPanel.add(amountLabel);
        detailsPanel.add(modeLabel);
        card.add(detailsPanel, BorderLayout.CENTER);

        JButton payButton = UIStyle.createButton("Pay Now");
        JPanel actionPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 22));
        actionPanel.setOpaque(false);
        actionPanel.add(payButton);
        card.add(actionPanel, BorderLayout.SOUTH);
        payButton.addActionListener(e -> processPayment(payButton));

        JPanel page = UIStyle.createPagePanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        UIStyle.setFixedCardWidth(card, 560);
        page.add(card, gbc);
        add(page, BorderLayout.CENTER);
    }

    private void processPayment(JButton payButton) {
        payButton.setEnabled(false);

        JDialog processingDialog = new JDialog(this, "Payment", true);
        processingDialog.add(new JLabel("Processing Payment...", JLabel.CENTER), BorderLayout.CENTER);
        processingDialog.setSize(300, 130);
        processingDialog.setLocationRelativeTo(this);

        Timer timer = new Timer(2000, e -> {
            processingDialog.dispose();
            String paymentId = "pay_" + (100000 + new Random().nextInt(900000));
            saveBooking(paymentId);
        });
        timer.setRepeats(false);
        timer.start();
        processingDialog.setVisible(true);
    }

    private void saveBooking(String paymentId) {
        Booking booking = new Booking(
                userId,
                hotel.getHotelId(),
                room.getRoomId(),
            numberOfPeople,
                checkIn,
                checkOut,
                totalAmount,
                "Paid",
                paymentId,
                true
        );

        try {
            int bookingId = new BookingDAO().createPaidBooking(booking);
            UIStyle.showInfo(this, "Payment successful!");
            new SuccessFrame(bookingId, userId, hotel, room, numberOfPeople, totalAmount, paymentId).setVisible(true);
            bookingForm.dispose();
            dispose();
        } catch (SQLException e) {
            UIStyle.showDatabaseError(this, "Payment succeeded, but booking could not be saved.", e);
        }
    }
}
