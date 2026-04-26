package ui;

import dao.UserDAO;

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

public class UserLoginFrame extends JFrame {
    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    public UserLoginFrame() {
        setTitle("Customer Login");
        setSize(900, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(UIStyle.createHeader("Hotel Booking System"), BorderLayout.NORTH);

        UIStyle.styleField(emailField);
        UIStyle.styleField(passwordField);

        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout());
        card.add(UIStyle.createSectionTitle("Customer Login"), BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 16, 16));
        formPanel.setOpaque(false);
        formPanel.add(UIStyle.createLabel("Email"));
        formPanel.add(emailField);
        formPanel.add(UIStyle.createLabel("Password"));
        formPanel.add(passwordField);

        JButton loginButton = UIStyle.createButton("Login");
        JButton signupButton = UIStyle.createDarkButton("Create Account");
        loginButton.addActionListener(e -> login());
        signupButton.addActionListener(e -> {
            new SignupFrame().setVisible(true);
            dispose();
        });

        formPanel.add(loginButton);
        formPanel.add(signupButton);
        card.add(formPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 18));
        footerPanel.setOpaque(false);
        JButton adminLoginButton = UIStyle.createDarkButton("Hotel Admin Login");
        adminLoginButton.addActionListener(e -> {
            new HotelLoginFrame().setVisible(true);
            dispose();
        });
        footerPanel.add(adminLoginButton);
        card.add(footerPanel, BorderLayout.SOUTH);

        JPanel page = UIStyle.createPagePanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        UIStyle.setFixedCardWidth(card, 540);
        page.add(card, gbc);
        add(page, BorderLayout.CENTER);
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            UIStyle.showWarning(this, "Please enter email and password.");
            return;
        }

        if (!UIStyle.isValidEmail(email)) {
            UIStyle.showWarning(this, "Please enter a valid email address.");
            return;
        }

        try {
            int userId = new UserDAO().loginUser(email, password);
            if (userId > 0) {
                new Dashboard(userId).setVisible(true);
                dispose();
            } else {
                UIStyle.showWarning(this, "Invalid email or password.");
            }
        } catch (SQLException e) {
            UIStyle.showDatabaseError(this, "Login failed.", e);
        }
    }
}
