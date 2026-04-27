package ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingConstants;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;

public class UIStyle {
    public static final Color PRIMARY_BLUE = new Color(20, 120, 168);
    public static final Color PRIMARY_BLUE_DARK = new Color(16, 96, 136);
    public static final Color BACKGROUND = new Color(242, 246, 250);
    public static final Color CARD = Color.WHITE;
    public static final Color SIDEBAR = new Color(15, 23, 42);
    public static final Color SIDEBAR_HOVER = new Color(30, 41, 59);
    public static final Color TEXT_DARK = new Color(18, 25, 38);
    public static final Color TEXT_MUTED = new Color(79, 92, 110);
    public static final Color BORDER = new Color(212, 221, 231);
    public static final Color BUTTON_DARK = new Color(19, 31, 53);

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 30);
    public static final Font SECTION_FONT = new Font("Segoe UI", Font.BOLD, 21);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private static final Deque<JFrame> FRAME_HISTORY = new ArrayDeque<>();

    private UIStyle() {
    }

    public static JPanel createHeader(String text) {
        JPanel header = new JPanel(new java.awt.BorderLayout());
        header.setOpaque(true);
        header.setBackground(PRIMARY_BLUE);
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        header.setPreferredSize(new Dimension(10, 78));

        JButton backButton = new JButton("<");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 22));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(PRIMARY_BLUE_DARK);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(46, 46));
        backButton.addActionListener(e -> goBackFromButton(backButton));

        JLabel title = new JLabel(text, JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(TITLE_FONT);

        JPanel leftPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(backButton);

        JPanel rightSpacer = new JPanel();
        rightSpacer.setOpaque(false);
        rightSpacer.setPreferredSize(new Dimension(46, 46));

        header.add(leftPanel, java.awt.BorderLayout.WEST);
        header.add(title, java.awt.BorderLayout.CENTER);
        header.add(rightSpacer, java.awt.BorderLayout.EAST);
        return header;
    }

    private static void goBackFromButton(Component source) {
        Window window = SwingUtilities.getWindowAncestor(source);
        if (window instanceof JFrame) {
            goBack((JFrame) window);
        }
    }

    public static void goBack(JFrame currentFrame) {
        while (!FRAME_HISTORY.isEmpty()) {
            JFrame previous = FRAME_HISTORY.pop();
            if (previous != null && previous.isDisplayable()) {
                previous.setVisible(true);
                previous.toFront();
                if (currentFrame != null && currentFrame.isDisplayable()) {
                    currentFrame.dispose();
                }
                return;
            }
        }

        showInfo(currentFrame, "No previous page available.");
    }

    public static JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text, JLabel.LEFT);
        label.setFont(SECTION_FONT);
        label.setForeground(TEXT_DARK);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        return label;
    }

    public static JPanel createPagePanel() {
        JPanel panel = new JPanel(new java.awt.GridBagLayout());
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        return panel;
    }

    public static JPanel createContentPanel() {
        JPanel panel = new JPanel(new java.awt.BorderLayout());
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        return panel;
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD);
        panel.setBorder(createCardBorder());
        return panel;
    }

    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(20, 22, 20, 22)
        );
    }

    public static JButton createButton(String text) {
        return createButton(text, PRIMARY_BLUE);
    }

    public static JButton createDarkButton(String text) {
        return createButton(text, BUTTON_DARK);
    }

    public static JPanel createSidebarPanel(Runnable dashboardAction, Runnable bookAction,
                                            Runnable viewAction, Runnable checkoutAction,
                                            Runnable logoutAction) {
        return createSidebarPanel(dashboardAction, null, bookAction, viewAction, checkoutAction, logoutAction);
    }

    public static JPanel createSidebarPanel(Runnable dashboardAction, Runnable profileAction,
                                            Runnable bookAction, Runnable viewAction,
                                            Runnable checkoutAction, Runnable logoutAction) {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR);
        sidebar.setPreferredSize(new Dimension(270, 10));
        sidebar.setBorder(BorderFactory.createEmptyBorder(22, 16, 22, 16));

        JLabel brand = new JLabel("Hotel System");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 24));
        brand.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        sidebar.add(brand);

        addSidebarAction(sidebar, "Dashboard", "icons/dashboard.png", dashboardAction);
        addSidebarAction(sidebar, "Profile", "icons/view.png", profileAction);
        addSidebarAction(sidebar, "Book Room", "icons/booking.png", bookAction);
        addSidebarAction(sidebar, "View Bookings", "icons/view.png", viewAction);
        addSidebarAction(sidebar, "Checkout", "icons/checkout.png", checkoutAction);
        sidebar.add(Box.createVerticalGlue());
        addSidebarAction(sidebar, "Logout", "icons/logout.png", logoutAction);

        return sidebar;
    }

    private static void addSidebarAction(JPanel sidebar, String text, String iconPath, Runnable action) {
        if (action == null) {
            return;
        }
        sidebar.add(createSidebarButton(text, iconPath, action));
        sidebar.add(Box.createVerticalStrut(10));
    }

    public static JButton createSidebarButton(String text, String iconPath, Runnable action) {
        JButton button = new JButton(text, loadIcon(iconPath));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(12);
        button.setBackground(SIDEBAR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(BUTTON_FONT);
        button.setMargin(new Insets(14, 16, 14, 16));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        button.setPreferredSize(new Dimension(236, 54));
        button.setEnabled(action != null);

        if (action != null) {
            button.addActionListener(e -> action.run());
        }

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(SIDEBAR_HOVER);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(SIDEBAR);
            }
        });

        return button;
    }

    private static ImageIcon loadIcon(String path) {
        ImageIcon icon = new ImageIcon(path);
        Image image = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    private static JButton createButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(BUTTON_FONT);
        button.setMargin(new Insets(12, 22, 12, 22));
        button.setPreferredSize(new Dimension(180, 44));

        Color hover = background.equals(PRIMARY_BLUE) ? PRIMARY_BLUE_DARK : new Color(43, 48, 58);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hover);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(background);
            }
        });

        return button;
    }

    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_DARK);
        return label;
    }

    public static void styleField(JTextField field) {
        field.setFont(BODY_FONT);
        field.setForeground(TEXT_DARK);
        field.setBackground(Color.WHITE);
        field.setCaretColor(TEXT_DARK);
        field.setColumns(18);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(10, 11, 10, 11)
        ));
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(BODY_FONT);
        comboBox.setForeground(TEXT_DARK);
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(260, 44));
    }

    public static void styleTable(JTable table) {
        table.setFont(BODY_FONT);
        table.setForeground(TEXT_DARK);
        table.setRowHeight(36);
        table.setGridColor(new Color(232, 236, 240));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setOpaque(true);
        Color stripe = new Color(248, 251, 255);
        Color white = Color.WHITE;
        centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (isSelected) {
                    component.setBackground(new Color(217, 238, 255));
                    component.setForeground(TEXT_DARK);
                } else {
                    component.setBackground(row % 2 == 0 ? white : stripe);
                    component.setForeground(TEXT_DARK);
                }
                return component;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setForeground(TEXT_DARK);
        header.setBackground(new Color(225, 237, 251));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));
    }

    public static void styleDetailsLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(TEXT_MUTED);
    }

    public static void setFixedCardWidth(JComponent component, int width) {
        component.setMaximumSize(new Dimension(width, Short.MAX_VALUE));
        component.setPreferredSize(new Dimension(width, component.getPreferredSize().height));
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Hotel Booking System", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Please Check", JOptionPane.WARNING_MESSAGE);
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showDatabaseError(Component parent, String action, SQLException exception) {
        String message = action + " Please check your database connection and try again.";
        if (exception.getMessage() != null && exception.getMessage().toLowerCase().contains("ojdbc")) {
            message = action + " Oracle JDBC driver is missing from the classpath.";
        }
        showError(parent, message);
    }

    public static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public static void switchFrame(JFrame currentFrame, JFrame nextFrame) {
        if (nextFrame instanceof UserLoginFrame || nextFrame instanceof HotelLoginFrame) {
            FRAME_HISTORY.clear();
        }

        if (currentFrame != null && nextFrame != null && currentFrame != nextFrame) {
            FRAME_HISTORY.push(currentFrame);
        }

        if (nextFrame != null) {
            nextFrame.setVisible(true);
        }
        if (currentFrame != null) {
            currentFrame.setVisible(false);
        }
    }
}
