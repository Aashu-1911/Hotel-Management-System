package ui;

import dao.HotelAdminDAO;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;

public class HotelLoginFrame extends JFrame {
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    public HotelLoginFrame() {
        setTitle("Hotel Admin Login");
        setSize(900, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(UIStyle.createHeader("Hotel Admin Login"), BorderLayout.NORTH);

        UIStyle.styleField(usernameField);
        UIStyle.styleField(passwordField);

        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout());
        card.add(UIStyle.createSectionTitle("Admin Access"), BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 16, 16));
        formPanel.setOpaque(false);
        formPanel.add(UIStyle.createLabel("Email"));
        formPanel.add(usernameField);
        formPanel.add(UIStyle.createLabel("Password"));
        formPanel.add(passwordField);

        JButton loginButton = UIStyle.createButton("Login");
        JButton backButton = UIStyle.createDarkButton("Customer Login");
        loginButton.addActionListener(e -> login());
        backButton.addActionListener(e -> {
            UIStyle.switchFrame(this, new UserLoginFrame());
        });

        formPanel.add(loginButton);
        formPanel.add(backButton);
        card.add(formPanel, BorderLayout.CENTER);

        JPanel page = UIStyle.createPagePanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        UIStyle.setFixedCardWidth(card, 520);
        page.add(card, gbc);
        add(page, BorderLayout.CENTER);
    }

    private void login() {
        String email = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            UIStyle.showWarning(this, "Please enter email and password.");
            return;
        }

        try {
            int hotelId = new HotelAdminDAO().validateLogin(email, password);
            if (hotelId > 0) {
                UIStyle.switchFrame(this, new HotelDashboard(hotelId));
            } else {
                UIStyle.showWarning(this, "Invalid hotel admin login.");
            }
        } catch (SQLException e) {
            UIStyle.showDatabaseError(this, "Login failed.", e);
        }
    }
}
