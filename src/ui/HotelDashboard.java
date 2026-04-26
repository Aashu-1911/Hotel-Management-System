package ui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;

public class HotelDashboard extends JFrame {
    private final int hotelId;

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
                () -> UIStyle.showInfo(this, "Customer bookings must be created from Customer Login."),
            () -> UIStyle.switchFrame(this, new ViewBookingsFrame(hotelId)),
            () -> UIStyle.switchFrame(this, new CheckoutFrame(hotelId)),
            () -> UIStyle.switchFrame(this, new HotelLoginFrame())
        ), BorderLayout.WEST);

        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout());
        card.add(UIStyle.createSectionTitle("Hotel Operations"), BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 18, 18));
        buttonPanel.setOpaque(false);

        JButton viewBookingsButton = UIStyle.createButton("View Bookings");
        JButton checkoutButton = UIStyle.createButton("Checkout Customer");
        JButton logoutButton = UIStyle.createDarkButton("Logout");

        viewBookingsButton.addActionListener(e -> UIStyle.switchFrame(this, new ViewBookingsFrame(hotelId)));
        checkoutButton.addActionListener(e -> UIStyle.switchFrame(this, new CheckoutFrame(hotelId)));
        logoutButton.addActionListener(e -> UIStyle.switchFrame(this, new HotelLoginFrame()));

        buttonPanel.add(viewBookingsButton);
        buttonPanel.add(checkoutButton);
        buttonPanel.add(logoutButton);
        card.add(buttonPanel, BorderLayout.CENTER);

        JPanel page = UIStyle.createPagePanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        UIStyle.setFixedCardWidth(card, 520);
        page.add(card, gbc);
        add(page, BorderLayout.CENTER);
    }
}
