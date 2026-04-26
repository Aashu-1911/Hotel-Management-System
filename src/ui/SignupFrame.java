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

public class SignupFrame extends JFrame {
    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    public SignupFrame() {
        setTitle("Customer Signup");
        setSize(900, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(UIStyle.createHeader("Create Customer Account"), BorderLayout.NORTH);

        UIStyle.styleField(nameField);
        UIStyle.styleField(emailField);
        UIStyle.styleField(passwordField);

        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout());
        card.add(UIStyle.createSectionTitle("Signup"), BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 16, 16));
        formPanel.setOpaque(false);
        formPanel.add(UIStyle.createLabel("Name"));
        formPanel.add(nameField);
        formPanel.add(UIStyle.createLabel("Email"));
        formPanel.add(emailField);
        formPanel.add(UIStyle.createLabel("Password"));
        formPanel.add(passwordField);

        JButton signupButton = UIStyle.createButton("Signup");
        JButton loginButton = UIStyle.createDarkButton("Back to Login");
        signupButton.addActionListener(e -> signup());
        loginButton.addActionListener(e -> {
            new UserLoginFrame().setVisible(true);
            dispose();
        });

        formPanel.add(signupButton);
        formPanel.add(loginButton);
        card.add(formPanel, BorderLayout.CENTER);

        JPanel page = UIStyle.createPagePanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        UIStyle.setFixedCardWidth(card, 540);
        page.add(card, gbc);
        add(page, BorderLayout.CENTER);
    }

    private void signup() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            UIStyle.showWarning(this, "All fields are required.");
            return;
        }

        if (!UIStyle.isValidEmail(email)) {
            UIStyle.showWarning(this, "Please enter a valid email address.");
            return;
        }

        if (password.length() < 4) {
            UIStyle.showWarning(this, "Password must be at least 4 characters long.");
            return;
        }

        try {
            new UserDAO().registerUser(name, email, password);
            UIStyle.showInfo(this, "Signup successful. Please login.");
            new UserLoginFrame().setVisible(true);
            dispose();
        } catch (SQLException e) {
            String errorMessage = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
            if (e.getErrorCode() == 1 || errorMessage.contains("unique")) {
                UIStyle.showWarning(this, "Email already exists. Please use another email.");
            } else {
                UIStyle.showDatabaseError(this, "Signup failed.", e);
            }
        }
    }
}
