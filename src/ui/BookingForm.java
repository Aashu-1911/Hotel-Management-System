package ui;

import dao.HotelDAO;
import dao.RoomDAO;
import model.Hotel;
import model.Room;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BookingForm extends JFrame {
    private static final double BREAKFAST_RATE_PER_PERSON_PER_NIGHT = 299.0;
    private static final double AIRPORT_PICKUP_FLAT = 999.0;
    private static final double FLEXIBLE_CANCELLATION_FLAT = 499.0;
    private static final double TAX_RATE = 0.12;

    private final JFrame parent;
    private final int userId;
    private final Integer preferredHotelId;

    private final JComboBox<Hotel> hotelComboBox = new JComboBox<>();
    private final JComboBox<Room> roomComboBox = new JComboBox<>();
    private final JTextField checkInField = new JTextField(LocalDate.now().plusDays(1).toString());
    private final JTextField checkOutField = new JTextField(LocalDate.now().plusDays(2).toString());
    private final JSpinner peopleSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
    private final JTextField promoCodeField = new JTextField();

    private final JCheckBox breakfastCheckBox = new JCheckBox("Breakfast included (+Rs. 299 per guest/night)");
    private final JCheckBox airportPickupCheckBox = new JCheckBox("Airport pickup (+Rs. 999 one-time)");
    private final JCheckBox flexibleCancellationCheckBox = new JCheckBox("Flexible cancellation (+Rs. 499 one-time)");

    private final JLabel hotelDetailsLabel = new JLabel("Select a hotel to view details.", JLabel.LEFT);
    private final JLabel selectedRoomDetailsLabel = new JLabel("Room details will appear here.", JLabel.LEFT);
    private final JLabel occupancyNoteLabel = new JLabel("", JLabel.LEFT);

    private final DefaultTableModel roomTypeModel = new DefaultTableModel(
            new String[]{"Room Type", "Available", "From Price", "Recommended Guests"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JLabel subtotalValueLabel = createValueLabel();
    private final JLabel occupancySurchargeValueLabel = createValueLabel();
    private final JLabel addonValueLabel = createValueLabel();
    private final JLabel taxValueLabel = createValueLabel();
    private final JLabel discountValueLabel = createValueLabel();
    private final JLabel totalValueLabel = createValueLabel();
    private final JLabel pricingHintLabel = new JLabel("", JLabel.LEFT);

    private final Map<String, Double> promoCodes = createPromoCodes();
    private String appliedPromoCode = "";
    private double appliedDiscountRate = 0.0;
    private List<Room> currentHotelRooms = new ArrayList<>();

    public BookingForm(JFrame parent) {
        this(parent, -1, null);
    }

    public BookingForm(JFrame parent, int userId) {
        this(parent, userId, null);
    }

    public BookingForm(JFrame parent, int userId, Integer preferredHotelId) {
        this.parent = parent;
        this.userId = userId;
        this.preferredHotelId = preferredHotelId;
        setTitle("Book Room");
        setSize(1000, 700);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        add(UIStyle.createHeader("Book Room"), BorderLayout.NORTH);
        add(UIStyle.createSidebarPanel(
                () -> {
                    UIStyle.switchFrame(this, new Dashboard(userId));
                },
                () -> openUserProfile(),
                null,
                () -> openUserBookings(),
                null,
                () -> {
                    UIStyle.switchFrame(this, new UserLoginFrame());
                }
        ), BorderLayout.WEST);
        styleInputs();

        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout());
        card.add(UIStyle.createSectionTitle("Reservation Studio"), BorderLayout.NORTH);

        JPanel leftPanel = createReservationInputPanel();
        JPanel rightPanel = createHotelInsightsPanel();
        javax.swing.JSplitPane splitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.56);
        splitPane.setBorder(null);
        splitPane.setDividerSize(8);
        splitPane.setOpaque(false);
        card.add(splitPane, BorderLayout.CENTER);

        JPanel summaryPanel = createPricingSummaryPanel();
        card.add(summaryPanel, BorderLayout.SOUTH);

        JPanel page = UIStyle.createPagePanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        UIStyle.setFixedCardWidth(card, 1120);
        page.add(card, gbc);
        add(page, BorderLayout.CENTER);

        hotelComboBox.addActionListener(e -> onHotelChanged());
        roomComboBox.addActionListener(e -> {
            updateSelectedRoomDetails();
            updatePricingView();
        });
        peopleSpinner.addChangeListener(e -> updatePricingView());
        breakfastCheckBox.addActionListener(e -> updatePricingView());
        airportPickupCheckBox.addActionListener(e -> updatePricingView());
        flexibleCancellationCheckBox.addActionListener(e -> updatePricingView());
        addDateChangeListeners();
        loadHotels();
    }

    private JPanel createReservationInputPanel() {
        JPanel leftPanel = UIStyle.createCardPanel();
        leftPanel.setLayout(new BorderLayout(0, 14));
        leftPanel.add(UIStyle.createSectionTitle("Stay Configuration"), BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 14, 14));
        formPanel.setOpaque(false);
        formPanel.add(UIStyle.createLabel("Select Hotel"));
        formPanel.add(hotelComboBox);
        formPanel.add(UIStyle.createLabel("Select Room"));
        formPanel.add(roomComboBox);
        formPanel.add(UIStyle.createLabel("Number of Guests"));
        formPanel.add(peopleSpinner);
        formPanel.add(UIStyle.createLabel("Check-in (yyyy-mm-dd)"));
        formPanel.add(checkInField);
        formPanel.add(UIStyle.createLabel("Check-out (yyyy-mm-dd)"));
        formPanel.add(checkOutField);
        formPanel.add(UIStyle.createLabel("Promo Code"));

        JPanel promoPanel = new JPanel(new BorderLayout(8, 0));
        promoPanel.setOpaque(false);
        promoPanel.add(promoCodeField, BorderLayout.CENTER);
        JButton applyPromoButton = UIStyle.createDarkButton("Apply");
        applyPromoButton.setPreferredSize(new Dimension(96, 40));
        applyPromoButton.addActionListener(e -> applyPromoCode());
        promoPanel.add(applyPromoButton, BorderLayout.EAST);
        formPanel.add(promoPanel);

        JPanel addOnPanel = UIStyle.createCardPanel();
        addOnPanel.setLayout(new GridLayout(4, 1, 8, 8));
        addOnPanel.add(UIStyle.createLabel("Optional Add-ons"));
        addOnPanel.add(breakfastCheckBox);
        addOnPanel.add(airportPickupCheckBox);
        addOnPanel.add(flexibleCancellationCheckBox);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 14));
        contentPanel.setOpaque(false);
        contentPanel.add(formPanel, BorderLayout.NORTH);
        contentPanel.add(addOnPanel, BorderLayout.CENTER);
        leftPanel.add(contentPanel, BorderLayout.CENTER);
        return leftPanel;
    }

    private JPanel createHotelInsightsPanel() {
        JPanel rightPanel = UIStyle.createCardPanel();
        rightPanel.setLayout(new BorderLayout(0, 10));
        rightPanel.add(UIStyle.createSectionTitle("Hotel Intelligence"), BorderLayout.NORTH);

        UIStyle.styleDetailsLabel(hotelDetailsLabel);
        UIStyle.styleDetailsLabel(selectedRoomDetailsLabel);
        occupancyNoteLabel.setFont(UIStyle.BODY_FONT);

        JPanel metaPanel = new JPanel(new GridLayout(3, 1, 6, 6));
        metaPanel.setOpaque(false);
        metaPanel.add(hotelDetailsLabel);
        metaPanel.add(selectedRoomDetailsLabel);
        metaPanel.add(occupancyNoteLabel);

        JTable availabilityTable = new JTable(roomTypeModel);
        UIStyle.styleTable(availabilityTable);
        JScrollPane tableScrollPane = new JScrollPane(availabilityTable);
        tableScrollPane.setBorder(UIStyle.createCardBorder());
        tableScrollPane.setPreferredSize(new Dimension(520, 250));

        JPanel container = new JPanel(new BorderLayout(0, 10));
        container.setOpaque(false);
        container.add(metaPanel, BorderLayout.NORTH);
        container.add(tableScrollPane, BorderLayout.CENTER);
        rightPanel.add(container, BorderLayout.CENTER);
        return rightPanel;
    }

    private JPanel createPricingSummaryPanel() {
        JPanel summaryPanel = UIStyle.createCardPanel();
        summaryPanel.setLayout(new BorderLayout());

        JPanel rowsPanel = new JPanel(new GridLayout(7, 2, 8, 8));
        rowsPanel.setOpaque(false);
        addSummaryRow(rowsPanel, "Room Subtotal", subtotalValueLabel);
        addSummaryRow(rowsPanel, "Occupancy Surcharge", occupancySurchargeValueLabel);
        addSummaryRow(rowsPanel, "Add-ons", addonValueLabel);
        addSummaryRow(rowsPanel, "Taxes (12%)", taxValueLabel);
        addSummaryRow(rowsPanel, "Promo Discount", discountValueLabel);
        addSummaryRow(rowsPanel, "Grand Total", totalValueLabel);

        pricingHintLabel.setFont(UIStyle.BODY_FONT);
        pricingHintLabel.setForeground(UIStyle.TEXT_MUTED);
        rowsPanel.add(new JLabel("Pricing Hint"));
        rowsPanel.add(pricingHintLabel);

        JButton paymentButton = UIStyle.createButton("Proceed to Payment");
        paymentButton.addActionListener(e -> proceedToPayment());

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
        actionPanel.setOpaque(false);
        actionPanel.add(paymentButton);

        summaryPanel.add(rowsPanel, BorderLayout.CENTER);
        summaryPanel.add(actionPanel, BorderLayout.EAST);
        return summaryPanel;
    }

    private void addSummaryRow(JPanel panel, String title, JLabel valueLabel) {
        JLabel titleLabel = UIStyle.createLabel(title);
        panel.add(titleLabel);
        panel.add(valueLabel);
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("Rs. 0.00", SwingConstants.RIGHT);
        label.setFont(UIStyle.BODY_FONT);
        label.setForeground(UIStyle.TEXT_DARK);
        return label;
    }

    private Map<String, Double> createPromoCodes() {
        Map<String, Double> codes = new HashMap<>();
        codes.put("SAVE5", 0.05);
        codes.put("LUXE10", 0.10);
        codes.put("GRAND15", 0.15);
        return codes;
    }

    private void styleInputs() {
        UIStyle.styleField(checkInField);
        UIStyle.styleField(checkOutField);
        UIStyle.styleField(promoCodeField);
        UIStyle.styleComboBox(hotelComboBox);
        UIStyle.styleComboBox(roomComboBox);
        peopleSpinner.setFont(UIStyle.BODY_FONT);
        ((JSpinner.DefaultEditor) peopleSpinner.getEditor()).getTextField().setFont(UIStyle.BODY_FONT);

        breakfastCheckBox.setOpaque(false);
        airportPickupCheckBox.setOpaque(false);
        flexibleCancellationCheckBox.setOpaque(false);
        breakfastCheckBox.setFont(UIStyle.BODY_FONT);
        airportPickupCheckBox.setFont(UIStyle.BODY_FONT);
        flexibleCancellationCheckBox.setFont(UIStyle.BODY_FONT);
    }

    private void addDateChangeListeners() {
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePricingView();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePricingView();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updatePricingView();
            }
        };

        checkInField.getDocument().addDocumentListener(listener);
        checkOutField.getDocument().addDocumentListener(listener);
    }

    private void loadHotels() {
        try {
            List<Hotel> hotels = new HotelDAO().getAllHotels();
            hotelComboBox.removeAllItems();
            for (Hotel hotel : hotels) {
                hotelComboBox.addItem(hotel);
            }

            if (preferredHotelId != null) {
                selectHotelById(preferredHotelId);
            }

            onHotelChanged();
        } catch (SQLException e) {
            UIStyle.showDatabaseError(this, "Unable to load hotels.", e);
        }
    }

    private void selectHotelById(int hotelId) {
        for (int i = 0; i < hotelComboBox.getItemCount(); i++) {
            Hotel hotel = hotelComboBox.getItemAt(i);
            if (hotel != null && hotel.getHotelId() == hotelId) {
                hotelComboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    private void onHotelChanged() {
        loadAvailableRoomsForSelectedHotel();
        updateHotelDetails();
        refreshRoomTypeTable();
        updateSelectedRoomDetails();
        updatePricingView();
    }

    private void loadAvailableRoomsForSelectedHotel() {
        Hotel selectedHotel = (Hotel) hotelComboBox.getSelectedItem();
        roomComboBox.removeAllItems();
        currentHotelRooms = new ArrayList<>();

        if (selectedHotel == null) {
            return;
        }

        try {
            currentHotelRooms = new RoomDAO().getAvailableRoomsByHotel(selectedHotel.getHotelId());
            for (Room room : currentHotelRooms) {
                roomComboBox.addItem(room);
            }
        } catch (SQLException e) {
            UIStyle.showDatabaseError(this, "Unable to load rooms.", e);
        }
    }

    private void updateHotelDetails() {
        Hotel selectedHotel = (Hotel) hotelComboBox.getSelectedItem();
        if (selectedHotel == null) {
            hotelDetailsLabel.setText("Select a hotel to view details.");
            return;
        }

        hotelDetailsLabel.setText("Hotel: " + selectedHotel.getHotelName()
                + " | Location: " + selectedHotel.getAddress()
                + " | Rating: " + String.format("%.1f/5", selectedHotel.getRating()));
    }

    private void refreshRoomTypeTable() {
        roomTypeModel.setRowCount(0);
        if (currentHotelRooms.isEmpty()) {
            return;
        }

        Map<String, Integer> countByType = new TreeMap<>();
        Map<String, Double> minPriceByType = new TreeMap<>();

        for (Room room : currentHotelRooms) {
            countByType.put(room.getType(), countByType.getOrDefault(room.getType(), 0) + 1);
            if (!minPriceByType.containsKey(room.getType()) || room.getPrice() < minPriceByType.get(room.getType())) {
                minPriceByType.put(room.getType(), room.getPrice());
            }
        }

        for (Map.Entry<String, Integer> entry : countByType.entrySet()) {
            String type = entry.getKey();
            roomTypeModel.addRow(new Object[]{
                    type,
                    entry.getValue(),
                    "Rs. " + String.format("%.2f", minPriceByType.get(type)),
                    getCapacityForRoomType(type)
            });
        }
    }

    private void updateSelectedRoomDetails() {
        Room selectedRoom = (Room) roomComboBox.getSelectedItem();
        if (selectedRoom == null) {
            selectedRoomDetailsLabel.setText("Room details will appear here.");
            return;
        }

        int capacity = getCapacityForRoom(selectedRoom);
        selectedRoomDetailsLabel.setText("Selected Room: " + selectedRoom.getType()
                + " | Room #" + selectedRoom.getRoomId()
                + " | Base price: Rs. " + String.format("%.2f", selectedRoom.getPrice())
                + " per night | Recommended capacity: " + capacity);
    }

    private void updatePricingView() {
        PriceBreakdown details = calculatePriceBreakdown();
        if (details == null) {
            subtotalValueLabel.setText("Rs. 0.00");
            occupancySurchargeValueLabel.setText("Rs. 0.00");
            addonValueLabel.setText("Rs. 0.00");
            taxValueLabel.setText("Rs. 0.00");
            discountValueLabel.setText("Rs. 0.00");
            totalValueLabel.setText("Rs. 0.00");
            pricingHintLabel.setText("Select a valid room and dates to preview pricing.");
            occupancyNoteLabel.setForeground(UIStyle.TEXT_MUTED);
            occupancyNoteLabel.setText("Occupancy guidance will appear after room selection.");
            return;
        }

        subtotalValueLabel.setText("Rs. " + String.format("%.2f", details.baseAmount));
        occupancySurchargeValueLabel.setText("Rs. " + String.format("%.2f", details.occupancySurcharge));
        addonValueLabel.setText("Rs. " + String.format("%.2f", details.addonAmount));
        taxValueLabel.setText("Rs. " + String.format("%.2f", details.taxAmount));
        discountValueLabel.setText("- Rs. " + String.format("%.2f", details.discountAmount));
        totalValueLabel.setText("Rs. " + String.format("%.2f", details.totalAmount));

        if (details.people > details.capacity) {
            occupancyNoteLabel.setForeground(new Color(181, 74, 0));
            occupancyNoteLabel.setText("Demand surcharge active for " + (details.people - details.capacity)
                    + " guest(s) above recommended occupancy.");
        } else {
            occupancyNoteLabel.setForeground(new Color(25, 109, 56));
            occupancyNoteLabel.setText("Guest count is within recommended occupancy for this room.");
        }

        String promoPart = appliedDiscountRate > 0
                ? "Promo " + appliedPromoCode + " applied (" + (int) (appliedDiscountRate * 100) + "% off)."
                : "Use promo codes SAVE5, LUXE10, or GRAND15.";
        pricingHintLabel.setText("" + details.nights + " night(s), " + details.people + " guest(s). " + promoPart);
    }

    private void applyPromoCode() {
        String code = promoCodeField.getText().trim().toUpperCase();
        if (code.isEmpty()) {
            appliedPromoCode = "";
            appliedDiscountRate = 0.0;
            UIStyle.showInfo(this, "Promo code cleared.");
            updatePricingView();
            return;
        }

        if (!promoCodes.containsKey(code)) {
            UIStyle.showWarning(this, "Invalid promo code. Try SAVE5, LUXE10, or GRAND15.");
            return;
        }

        appliedPromoCode = code;
        appliedDiscountRate = promoCodes.get(code);
        UIStyle.showInfo(this, "Promo code " + code + " applied successfully.");
        updatePricingView();
    }

    private PriceBreakdown calculatePriceBreakdown() {
        Room selectedRoom = (Room) roomComboBox.getSelectedItem();
        if (selectedRoom == null) {
            return null;
        }

        try {
            LocalDate checkIn = LocalDate.parse(checkInField.getText().trim());
            LocalDate checkOut = LocalDate.parse(checkOutField.getText().trim());
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

            if (nights <= 0) {
                return null;
            }

            int people = (int) peopleSpinner.getValue();
            int capacity = getCapacityForRoom(selectedRoom);

            double baseAmount = nights * selectedRoom.getPrice();
            int extraGuests = Math.max(0, people - 1);
            int extraWithinCapacity = Math.min(extraGuests, Math.max(0, capacity - 1));
            int extraBeyondCapacity = Math.max(0, people - capacity);

            double occupancySurcharge = baseAmount * (0.20 * extraWithinCapacity + 0.45 * extraBeyondCapacity);

            double addonAmount = 0.0;
            if (breakfastCheckBox.isSelected()) {
                addonAmount += BREAKFAST_RATE_PER_PERSON_PER_NIGHT * people * nights;
            }
            if (airportPickupCheckBox.isSelected()) {
                addonAmount += AIRPORT_PICKUP_FLAT;
            }
            if (flexibleCancellationCheckBox.isSelected()) {
                addonAmount += FLEXIBLE_CANCELLATION_FLAT;
            }

            double amountBeforeTax = baseAmount + occupancySurcharge + addonAmount;
            double taxAmount = amountBeforeTax * TAX_RATE;
            double discountAmount = (amountBeforeTax + taxAmount) * appliedDiscountRate;
            double totalAmount = amountBeforeTax + taxAmount - discountAmount;

            return new PriceBreakdown(nights, people, capacity, baseAmount, occupancySurcharge,
                    addonAmount, taxAmount, discountAmount, totalAmount);
        } catch (java.time.format.DateTimeParseException e) {
            return null;
        }
    }

    private int getCapacityForRoom(Room room) {
        return getCapacityForRoomType(room.getType());
    }

    private int getCapacityForRoomType(String roomType) {
        if (roomType == null) {
            return 2;
        }

        String normalized = roomType.trim().toLowerCase();
        switch (normalized) {
            case "single":
                return 1;
            case "double":
                return 2;
            case "deluxe":
            case "executive":
                return 3;
            case "suite":
                return 4;
            default:
                return 2;
        }
    }

    private void proceedToPayment() {
        Hotel selectedHotel = (Hotel) hotelComboBox.getSelectedItem();
        Room selectedRoom = (Room) roomComboBox.getSelectedItem();

        if (userId <= 0) {
            UIStyle.showWarning(this, "Please login before booking.");
            UIStyle.switchFrame(this, new UserLoginFrame());
            return;
        }

        if (checkInField.getText().trim().isEmpty() || checkOutField.getText().trim().isEmpty()) {
            UIStyle.showWarning(this, "Check-in and check-out dates are required.");
            return;
        }

        if (selectedHotel == null) {
            UIStyle.showWarning(this, "Please select a hotel.");
            return;
        }

        if (selectedRoom == null) {
            UIStyle.showWarning(this, "No available room selected for this hotel.");
            return;
        }

        try {
            LocalDate checkIn = LocalDate.parse(checkInField.getText().trim());
            LocalDate checkOut = LocalDate.parse(checkOutField.getText().trim());

            if (!checkOut.isAfter(checkIn)) {
                UIStyle.showWarning(this, "Check-out date must be after check-in date.");
                return;
            }

            int numberOfPeople = (int) peopleSpinner.getValue();
            PriceBreakdown breakdown = calculatePriceBreakdown();
            if (breakdown == null) {
                UIStyle.showWarning(this, "Please select valid room and date details.");
                return;
            }

            // Booking is saved only after the simulated Razorpay payment succeeds.
            new PaymentFrame(parent, this, userId, selectedHotel, selectedRoom,
                    numberOfPeople, checkIn, checkOut, breakdown.totalAmount).setVisible(true);
        } catch (java.time.format.DateTimeParseException e) {
            UIStyle.showWarning(this, "Please enter dates in yyyy-mm-dd format.");
        }
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

    private static class PriceBreakdown {
        private final long nights;
        private final int people;
        private final int capacity;
        private final double baseAmount;
        private final double occupancySurcharge;
        private final double addonAmount;
        private final double taxAmount;
        private final double discountAmount;
        private final double totalAmount;

        private PriceBreakdown(long nights, int people, int capacity, double baseAmount,
                               double occupancySurcharge, double addonAmount, double taxAmount,
                               double discountAmount, double totalAmount) {
            this.nights = nights;
            this.people = people;
            this.capacity = capacity;
            this.baseAmount = baseAmount;
            this.occupancySurcharge = occupancySurcharge;
            this.addonAmount = addonAmount;
            this.taxAmount = taxAmount;
            this.discountAmount = discountAmount;
            this.totalAmount = totalAmount;
        }
    }
}
