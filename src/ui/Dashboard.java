package ui;

import dao.HotelDAO;
import dao.RoomDAO;
import model.HotelCatalog;
import model.Room;

import javax.swing.JButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Dashboard extends JFrame {
    private final HotelDAO hotelDAO = new HotelDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final int userId;

    private final JPanel hotelsCardsContainer = new JPanel();
    private final JTextField searchHotelField = new JTextField();
    private final JComboBox<String> locationFilterCombo = new JComboBox<>();
    private final JComboBox<String> ratingFilterCombo = new JComboBox<>(new String[]{
            "All Ratings", "4.5 and above", "4.0 and above", "3.5 and above"
    });
    private final JComboBox<String> availabilityFilterCombo = new JComboBox<>(new String[]{
            "All Hotels", "Available Rooms Only"
    });

    private List<HotelCatalog> allHotels = new ArrayList<>();
    private Integer selectedHotelId = null;

    public Dashboard() {
        this(-1);
    }

    public Dashboard(int userId) {
        this.userId = userId;

        setTitle("Multi-Hotel Booking System - Dashboard");
        setSize(1000, 650);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(UIStyle.createHeader("Customer Dashboard"), BorderLayout.NORTH);
        add(UIStyle.createSidebarPanel(
                () -> { },
            () -> openUserProfile(),
            null,
            () -> openUserBookings(),
            null,
                () -> UIStyle.switchFrame(this, new UserLoginFrame())
        ), BorderLayout.WEST);

        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout());
        card.add(UIStyle.createSectionTitle("Discover Hotels"), BorderLayout.NORTH);

        JPanel filterPanel = createFilterPanel();

        hotelsCardsContainer.setOpaque(false);
        hotelsCardsContainer.setLayout(new BoxLayout(hotelsCardsContainer, BoxLayout.Y_AXIS));

        JScrollPane hotelsScrollPane = new JScrollPane(hotelsCardsContainer);
        hotelsScrollPane.setBorder(UIStyle.createCardBorder());
        hotelsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        hotelsScrollPane.setPreferredSize(new Dimension(980, 540));
        hotelsScrollPane.setMinimumSize(new Dimension(880, 380));

        JPanel contentPanel = new JPanel(new BorderLayout(0, 14));
        contentPanel.setOpaque(false);
        contentPanel.add(filterPanel, BorderLayout.NORTH);
        contentPanel.add(hotelsScrollPane, BorderLayout.CENTER);
        card.add(contentPanel, BorderLayout.CENTER);

        JButton bookRoomButton = UIStyle.createButton("Book Selected Hotel");
        JButton viewRoomsButton = UIStyle.createButton("View Available Rooms");
        JButton clearFilterButton = UIStyle.createDarkButton("Clear Filters");
        JButton refreshButton = UIStyle.createDarkButton("Refresh Hotels");
        JButton exitButton = UIStyle.createDarkButton("Exit");

        bookRoomButton.addActionListener(e -> openBookingForm(selectedHotelId));
        viewRoomsButton.addActionListener(e -> showAvailableRooms());
        clearFilterButton.addActionListener(e -> resetFilters());
        refreshButton.addActionListener(e -> loadHotels());
        exitButton.addActionListener(e -> System.exit(0));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 14, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(bookRoomButton);
        buttonPanel.add(viewRoomsButton);
        buttonPanel.add(clearFilterButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exitButton);

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
        UIStyle.setFixedCardWidth(card, 1140);
        card.setMinimumSize(new Dimension(980, 720));
        page.add(card, gbc);
        add(page, BorderLayout.CENTER);

        registerFilterListeners();
        loadHotels();
    }

    private JPanel createFilterPanel() {
        UIStyle.styleField(searchHotelField);
        UIStyle.styleComboBox(locationFilterCombo);
        UIStyle.styleComboBox(ratingFilterCombo);
        UIStyle.styleComboBox(availabilityFilterCombo);

        JPanel panel = new JPanel(new GridLayout(2, 4, 10, 10));
        panel.setOpaque(false);
        panel.add(UIStyle.createLabel("Search by Hotel Name"));
        panel.add(UIStyle.createLabel("Location"));
        panel.add(UIStyle.createLabel("Rating"));
        panel.add(UIStyle.createLabel("Availability"));
        panel.add(searchHotelField);
        panel.add(locationFilterCombo);
        panel.add(ratingFilterCombo);
        panel.add(availabilityFilterCombo);
        return panel;
    }

    private void registerFilterListeners() {
        DocumentListener searchListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilters();
            }
        };

        searchHotelField.getDocument().addDocumentListener(searchListener);
        locationFilterCombo.addActionListener(e -> applyFilters());
        ratingFilterCombo.addActionListener(e -> applyFilters());
        availabilityFilterCombo.addActionListener(e -> applyFilters());
    }

    private void openBookingForm(Integer preferredHotel) {
        if (userId <= 0) {
            UIStyle.showWarning(this, "Please login as a customer before booking.");
            UIStyle.switchFrame(this, new UserLoginFrame());
            return;
        }

        UIStyle.switchFrame(this, new BookingForm(this, userId, preferredHotel));
    }

    private void showAvailableRooms() {
        try {
            List<Room> rooms = roomDAO.getAvailableRooms();
            String[] columns = {"Room ID", "Hotel ID", "Type", "Price", "Status"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (Room room : rooms) {
                model.addRow(new Object[]{
                        room.getRoomId(),
                        room.getHotelId(),
                        room.getType(),
                        room.getPrice(),
                        room.getStatus()
                });
            }

            JTable table = new JTable(model);
            UIStyle.styleTable(table);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(UIStyle.createCardBorder());
            scrollPane.setPreferredSize(new Dimension(760, 360));
            JOptionPane.showMessageDialog(this, scrollPane, "Available Rooms", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException e) {
            UIStyle.showDatabaseError(this, "Unable to load rooms.", e);
        }
    }

    private void loadHotels() {
        try {
            allHotels = hotelDAO.getHotelCatalog();
            refreshLocationFilterOptions();
            applyFilters();
        } catch (SQLException e) {
            UIStyle.showDatabaseError(this, "Unable to load hotels.", e);
        }
    }

    private void refreshLocationFilterOptions() {
        String previousSelection = (String) locationFilterCombo.getSelectedItem();
        Set<String> locations = new TreeSet<>();

        for (HotelCatalog hotel : allHotels) {
            if (hotel.getAddress() != null && !hotel.getAddress().trim().isEmpty()) {
                locations.add(hotel.getAddress().trim());
            }
        }

        locationFilterCombo.removeAllItems();
        locationFilterCombo.addItem("All Locations");
        for (String location : locations) {
            locationFilterCombo.addItem(location);
        }

        if (previousSelection != null) {
            locationFilterCombo.setSelectedItem(previousSelection);
        }
        if (locationFilterCombo.getSelectedItem() == null) {
            locationFilterCombo.setSelectedIndex(0);
        }
    }

    private void applyFilters() {
        hotelsCardsContainer.removeAll();
        String query = searchHotelField.getText().trim().toLowerCase();
        String selectedLocation = (String) locationFilterCombo.getSelectedItem();
        double minRating = getSelectedMinRating();
        boolean onlyAvailable = "Available Rooms Only".equals(availabilityFilterCombo.getSelectedItem());

        List<JPanel> matchedCards = new ArrayList<>();
        for (HotelCatalog hotel : allHotels) {
            if (!query.isEmpty() && !hotel.getHotelName().toLowerCase().contains(query)) {
                continue;
            }

            if (selectedLocation != null && !"All Locations".equals(selectedLocation)
                    && !selectedLocation.equalsIgnoreCase(hotel.getAddress())) {
                continue;
            }

            if (hotel.getRating() < minRating) {
                continue;
            }

            if (onlyAvailable && hotel.getAvailableRooms() <= 0) {
                continue;
            }

            matchedCards.add(createHotelCard(hotel));
        }

        if (matchedCards.isEmpty()) {
            JLabel emptyLabel = new JLabel("No hotels match your search/filter. Try different filters.", JLabel.CENTER);
            emptyLabel.setFont(UIStyle.BODY_FONT);
            emptyLabel.setForeground(UIStyle.TEXT_MUTED);
            emptyLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 8, 14, 8));
            hotelsCardsContainer.add(emptyLabel);
        } else {
            for (int i = 0; i < matchedCards.size(); i += 2) {
                JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
                row.setOpaque(false);
                row.setAlignmentX(JPanel.LEFT_ALIGNMENT);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 310));
                row.setPreferredSize(new Dimension(0, 292));
                row.setMinimumSize(new Dimension(0, 292));
                row.add(matchedCards.get(i));

                if (i + 1 < matchedCards.size()) {
                    row.add(matchedCards.get(i + 1));
                } else {
                    JPanel placeholder = new JPanel();
                    placeholder.setOpaque(false);
                    row.add(placeholder);
                }

                hotelsCardsContainer.add(row);
                hotelsCardsContainer.add(Box.createVerticalStrut(14));
            }

            hotelsCardsContainer.add(Box.createVerticalGlue());
        }

        hotelsCardsContainer.revalidate();
        hotelsCardsContainer.repaint();
    }

    private double getSelectedMinRating() {
        String value = (String) ratingFilterCombo.getSelectedItem();
        if ("4.5 and above".equals(value)) {
            return 4.5;
        }
        if ("4.0 and above".equals(value)) {
            return 4.0;
        }
        if ("3.5 and above".equals(value)) {
            return 3.5;
        }
        return 0.0;
    }

    private JPanel createHotelCard(HotelCatalog hotel) {
        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout(0, 10));
        card.setPreferredSize(new Dimension(420, 286));
        card.setMinimumSize(new Dimension(360, 270));

        if (selectedHotelId != null && selectedHotelId == hotel.getHotelId()) {
            card.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createLineBorder(UIStyle.PRIMARY_BLUE_DARK, 2),
                    javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 16)
            ));
        }

        JLabel nameLabel = new JLabel(hotel.getHotelName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setForeground(UIStyle.TEXT_DARK);

        JLabel locationLabel = new JLabel(hotel.getAddress());
        locationLabel.setFont(UIStyle.BODY_FONT);
        locationLabel.setForeground(UIStyle.TEXT_MUTED);

        JLabel ratingLabel = new JLabel("Rating: " + String.format("%.1f/5", hotel.getRating()));
        ratingLabel.setFont(UIStyle.BODY_FONT);
        ratingLabel.setForeground(new Color(176, 124, 0));

        JPanel topPanel = new JPanel(new GridLayout(3, 1, 4, 4));
        topPanel.setOpaque(false);
        topPanel.add(nameLabel);
        topPanel.add(locationLabel);
        topPanel.add(ratingLabel);

        JPanel metricsPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        metricsPanel.setOpaque(false);
        metricsPanel.add(UIStyle.createLabel("Starting Price"));
        metricsPanel.add(createMetricValue(hotel.getStartingPrice() == null
                ? "N/A"
                : ("Rs. " + String.format("%.2f", hotel.getStartingPrice()))));
        metricsPanel.add(UIStyle.createLabel("Available Rooms"));
        metricsPanel.add(createMetricValue(String.valueOf(hotel.getAvailableRooms())));
        metricsPanel.add(UIStyle.createLabel("Room Types"));
        metricsPanel.add(createMetricValue(String.valueOf(hotel.getRoomTypes())));
        metricsPanel.add(UIStyle.createLabel("Hotel ID"));
        metricsPanel.add(createMetricValue(String.valueOf(hotel.getHotelId())));

        JButton bookNowButton = UIStyle.createButton("Book Now");
        bookNowButton.addActionListener(e -> {
            selectedHotelId = hotel.getHotelId();
            openBookingForm(hotel.getHotelId());
        });

        JPanel actionsPanel = new JPanel(new GridLayout(1, 1, 0, 0));
        actionsPanel.setOpaque(false);
        actionsPanel.add(bookNowButton);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(metricsPanel, BorderLayout.CENTER);
        card.add(actionsPanel, BorderLayout.SOUTH);
        return card;
    }

    private JLabel createMetricValue(String value) {
        JLabel label = new JLabel(value);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(UIStyle.TEXT_DARK);
        return label;
    }

    private void resetFilters() {
        searchHotelField.setText("");
        if (locationFilterCombo.getItemCount() > 0) {
            locationFilterCombo.setSelectedIndex(0);
        }
        ratingFilterCombo.setSelectedIndex(0);
        availabilityFilterCombo.setSelectedIndex(0);
        selectedHotelId = null;
        applyFilters();
    }

    private void openUserProfile() {
        if (userId <= 0) {
            UIStyle.showWarning(this, "Please login to view profile.");
            return;
        }

        UIStyle.switchFrame(this, new UserProfileFrame(userId));
    }

    private void openUserBookings() {
        if (userId <= 0) {
            UIStyle.showWarning(this, "Please login to view bookings.");
            return;
        }

        UIStyle.switchFrame(this, new UserBookingsFrame(userId));
    }
}
