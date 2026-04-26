package main;

import ui.UserLoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Use the default Swing look and feel if the system theme is unavailable.
            }

            new UserLoginFrame().setVisible(true);
        });
    }
}
