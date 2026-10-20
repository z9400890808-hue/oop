package roadaccident.ui;

import roadaccident.dao.RegistrationDAO;
import roadaccident.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;

public class LoginFrame extends JFrame {

    static {
        AppTheme.applyTheme();
    }

    private final JTextField phoneField =
            new JTextField(20);

    private final JPasswordField passwordField =
            new JPasswordField(20);

    public LoginFrame() {
        setTitle("Road Accident Management System - Login");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(460, 370);
        setLocationRelativeTo(null);
        setResizable(false);

        buildUI();
    }

    private void buildUI() {
        JPanel mainPanel =
                new JPanel(new BorderLayout());

        mainPanel.setBackground(
                new Color(245, 248, 252)
        );

        mainPanel.setBorder(
                new EmptyBorder(30, 45, 30, 45)
        );

        JPanel formPanel = new JPanel();

        formPanel.setBackground(Color.WHITE);

        formPanel.setBorder(
                new EmptyBorder(25, 30, 25, 30)
        );

        formPanel.setLayout(
                new BoxLayout(
                        formPanel,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel title =
                new JLabel(
                        "SYSTEM LOGIN",
                        SwingConstants.CENTER
                );

        title.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        25
                )
        );

        title.setForeground(
                new Color(27, 54, 93)
        );

        title.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JLabel subtitle =
                new JLabel(
                        "Login using your phone number and password",
                        SwingConstants.CENTER
                );

        subtitle.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        subtitle.setForeground(
                new Color(90, 100, 115)
        );

        subtitle.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JLabel phoneLabel =
                new JLabel(
                        "Phone Number / User ID"
                );

        phoneLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        13
                )
        );

        phoneLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        phoneField.setPreferredSize(new Dimension(300, 34));
        phoneField.setMaximumSize(
                new Dimension(
                        300,
                        34
                )
        );

        phoneField.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JLabel passwordLabel =
                new JLabel("Password");

        passwordLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        13
                )
        );

        passwordLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        passwordField.setPreferredSize(new Dimension(300, 34));
        passwordField.setMaximumSize(
                new Dimension(
                        300,
                        34
                )
        );

        passwordField.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JButton loginButton =
                new JButton("Login");

        loginButton.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        14
                )
        );

        loginButton.setBackground(new Color(37, 99, 180));

        loginButton.setForeground(Color.WHITE);

        loginButton.setFocusPainted(false);

        loginButton.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JButton registerButton =
                new JButton("New Registration");

        registerButton.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        registerButton.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JButton homeButton =
                new JButton("Back to Home");

        homeButton.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        homeButton.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        loginButton.addActionListener(
                e -> loginUser()
        );

        passwordField.addActionListener(
                e -> loginUser()
        );

        registerButton.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });

        homeButton.addActionListener(e -> {
            new HomeFrame().setVisible(true);
            dispose();
        });

        formPanel.add(homeButton);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(title);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(subtitle);

        formPanel.add(Box.createVerticalStrut(25));

        formPanel.add(phoneLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(phoneField);

        formPanel.add(Box.createVerticalStrut(16));

        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(passwordField);

        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(loginButton);

        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(registerButton);

        mainPanel.add(
                formPanel,
                BorderLayout.CENTER
        );

        setContentPane(mainPanel);
    }

    private void loginUser() {
        String phoneNumber =
                phoneField.getText().trim();

        char[] passwordChars =
                passwordField.getPassword();

        try {
            if (phoneNumber.isEmpty()
                    || passwordChars.length == 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Enter phone number and password.",
                        "Login",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            RegistrationDAO registrationDAO =
                    new RegistrationDAO();

            User user = registrationDAO.login(
                    phoneNumber,
                    new String(passwordChars)
            );

            if (user == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Login failed.\n"
                                + "Check your phone number, password, "
                                + "or approval status.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );

                passwordField.setText("");
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Welcome, " + user.getFullName()
                            + "\nRole: "
                            + formatRole(user.getRole())
                            + "\nZone: "
                            + formatZone(user.getZone()),
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

            openDashboardByRole(user);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database operation failed.\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();

        } finally {
            Arrays.fill(passwordChars, '\0');
        }
    }

    private void openDashboardByRole(User user) {
        String role = user.getRole();

        if ("ADMIN".equals(role)) {
            AdminFrame adminFrame =
                    new AdminFrame();

            adminFrame.setVisible(true);

            dispose();
            return;
        }

        if ("AMBULANCE_DRIVER".equals(role)) {
            AmbulanceDriverFrame driverFrame =
                    new AmbulanceDriverFrame(user);

            driverFrame.setVisible(true);

            dispose();
            return;
        }

        if ("HOSPITAL_STAFF".equals(role)) {
            HospitalStaffFrame hospitalFrame =
                    new HospitalStaffFrame(user);

            hospitalFrame.setVisible(true);

            dispose();
            return;
        }

        if ("POLICE".equals(role)) {
            PoliceFrame policeFrame =
                    new PoliceFrame(user);

            policeFrame.setVisible(true);

            dispose();
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Unknown user role: " + role,
                "Login Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private String formatRole(String role) {
        if ("ADMIN".equals(role)) {
            return "Administrator";
        }

        if ("AMBULANCE_DRIVER".equals(role)) {
            return "Ambulance Driver";
        }

        if ("HOSPITAL_STAFF".equals(role)) {
            return "Hospital Staff";
        }

        if ("POLICE".equals(role)) {
            return "Police";
        }

        return role;
    }

    private String formatZone(String zone) {
        if ("KOZHIKODE".equals(zone)) {
            return "Kozhikode";
        }

        if ("WESTHILL".equals(zone)) {
            return "Westhill";
        }

        if ("EASTHILL".equals(zone)) {
            return "Easthill";
        }

        return zone == null ? "-" : zone;
    }
}