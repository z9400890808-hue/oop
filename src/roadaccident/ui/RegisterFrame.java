package roadaccident.ui;

import javax.swing.*;

import roadaccident.dao.RegistrationDAO;
import roadaccident.model.Registration;

import java.awt.*;
import java.sql.SQLException;

public class RegisterFrame extends JFrame {

    static {
        AppTheme.applyTheme();
    }

    private final JTextField nameField = new JTextField(20);
    private final JTextField phoneField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JPasswordField confirmPasswordField =
            new JPasswordField(20);

    private final JComboBox<String> roleBox =
            new JComboBox<>(new String[]{
                    "Select role",
                    "Ambulance Driver",
                    "Hospital Staff",
                    "Police"
            });

    private final JComboBox<String> zoneBox =
            new JComboBox<>(new String[]{
                    "Select zone",
                    "Kozhikode",
                    "Westhill",
                    "Easthill"
            });

    private final JPanel rolePanel = new JPanel(new CardLayout());

    private final JTextField licenceField = new JTextField(20);
    private final JTextField ambulanceField = new JTextField(20);
    private final JTextField hospitalField = new JTextField(20);
    private final JTextField designationField = new JTextField(20);
    private final JTextField stationField = new JTextField(20);
    private final JTextField badgeField = new JTextField(20);

    public RegisterFrame() {
        setTitle("New Registration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(520, 720);
        setLocationRelativeTo(null);

        JPanel form = new JPanel();
        form.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Create an Account");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(title);
        form.add(Box.createVerticalStrut(18));

        addField(form, "Full Name", nameField);
        addField(form, "Phone Number / Login ID", phoneField);
        addField(form, "Password", passwordField);
        addField(form, "Confirm Password", confirmPasswordField);
        addField(form, "Role", roleBox);
        addField(form, "Zone", zoneBox);

        makeRolePanels();
        form.add(rolePanel);

        roleBox.addActionListener(e -> showRolePanel());

        JCheckBox consent = new JCheckBox(
                "I confirm that the information is correct."
        );
        consent.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(Box.createVerticalStrut(12));
        form.add(consent);

       JButton registerButton = new JButton("Submit Registration");
registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

registerButton.addActionListener(e -> {

    if (!validateForm(consent)) {
        return;
    }

    Registration registration = new Registration(
            nameField.getText().trim(),
            phoneField.getText().trim(),
            new String(passwordField.getPassword()),
            roleBox.getSelectedItem().toString(),
            zoneBox.getSelectedItem().toString(),
            licenceField.getText().trim(),
            ambulanceField.getText().trim(),
            hospitalField.getText().trim(),
            designationField.getText().trim(),
            stationField.getText().trim(),
            badgeField.getText().trim()
    );

    try {
        RegistrationDAO registrationDAO =
                new RegistrationDAO();

        boolean registered =
                registrationDAO.register(registration);

        if (registered) {
            JOptionPane.showMessageDialog(
                    this,
                    "Registration submitted successfully.\n"
                            + "Status: Pending Admin Approval."
            );

            clearForm();

        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "This phone number is already registered.\n"
                            + "Please log in using your existing account.",
                    "Registration Failed",
                    JOptionPane.WARNING_MESSAGE
            );
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(
                this,
                "Database error: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );

        ex.printStackTrace();
    }
});

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            new HomeFrame().setVisible(true);
            dispose();
        });

        form.add(Box.createVerticalStrut(10));
        form.add(backButton);
        form.add(Box.createVerticalStrut(8));
        form.add(registerButton);

        JScrollPane formScrollPane = new JScrollPane(form);
        formScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        add(formScrollPane);
        showRolePanel();
    }

    private void addField(
            JPanel panel,
            String label,
            JComponent input) {

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        input.setPreferredSize(new Dimension(340, 32));
        input.setMaximumSize(new Dimension(340, 32));
        input.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(fieldLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(input);
        panel.add(Box.createVerticalStrut(10));
    }

    private void makeRolePanels() {
        JPanel empty = new JPanel();
        empty.setBackground(new Color(245, 248, 252));

        JPanel driver = new JPanel();
        driver.setLayout(new BoxLayout(driver, BoxLayout.Y_AXIS));
        driver.setBackground(new Color(245, 248, 252));
        addField(driver, "Driving Licence Number", licenceField);
        addField(driver, "Ambulance Number", ambulanceField);

        JPanel hospital = new JPanel();
        hospital.setLayout(new BoxLayout(hospital, BoxLayout.Y_AXIS));
        hospital.setBackground(new Color(245, 248, 252));
        addField(hospital, "Hospital Name", hospitalField);
        addField(hospital, "Designation", designationField);

        JPanel police = new JPanel();
        police.setLayout(new BoxLayout(police, BoxLayout.Y_AXIS));
        police.setBackground(new Color(245, 248, 252));
        addField(police, "Police Station", stationField);
        addField(police, "Badge / Police ID", badgeField);

        rolePanel.add(empty, "None");
        rolePanel.add(driver, "Ambulance Driver");
        rolePanel.add(hospital, "Hospital Staff");
        rolePanel.add(police, "Police");
        rolePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rolePanel.setPreferredSize(new Dimension(360, 150));
        rolePanel.setMaximumSize(new Dimension(360, 150));
    }

    private void showRolePanel() {
        CardLayout cards = (CardLayout) rolePanel.getLayout();
        String role = (String) roleBox.getSelectedItem();

        if (role == null || role.equals("Select role")) {
            cards.show(rolePanel, "None");
        } else {
            cards.show(rolePanel, role);
        }

        rolePanel.revalidate();
        rolePanel.repaint();
    }

    private boolean validateForm(JCheckBox consent) {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String role = (String) roleBox.getSelectedItem();
        String zone = (String) zoneBox.getSelectedItem();
        char[] password = passwordField.getPassword();
        char[] confirm = confirmPasswordField.getPassword();

        if (name.isEmpty() || phone.isEmpty()
                || password.length == 0 || confirm.length == 0) {
            return error("Fill in all required fields.");
        }

        if (!phone.matches("\\d{10}")) {
            return error("Enter a 10-digit phone number.");
        }

        if (!java.util.Arrays.equals(password, confirm)) {
            return error("Passwords do not match.");
        }

        if (role == null || role.equals("Select role")) {
            return error("Select a role.");
        }

        if (zone == null || zone.equals("Select zone")) {
            return error("Select a zone.");
        }

        if (!consent.isSelected()) {
            return error("Confirm that the information is correct.");
        }

        if (role.equals("Ambulance Driver")
                && (licenceField.getText().trim().isEmpty()
                || ambulanceField.getText().trim().isEmpty())) {
            return error("Enter the driving licence and ambulance numbers.");
        }

        if (role.equals("Hospital Staff")
                && (hospitalField.getText().trim().isEmpty()
                || designationField.getText().trim().isEmpty())) {
            return error("Enter the hospital name and designation.");
        }

        if (role.equals("Police")
                && (stationField.getText().trim().isEmpty()
                || badgeField.getText().trim().isEmpty())) {
            return error("Enter the police station and badge ID.");
        }

        return true;
    }

    private boolean error(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Registration",
                JOptionPane.WARNING_MESSAGE
        );
        return false;
    }
    private void clearForm() {

    nameField.setText("");
    phoneField.setText("");

    passwordField.setText("");
    confirmPasswordField.setText("");

    roleBox.setSelectedIndex(0);
    zoneBox.setSelectedIndex(0);

    licenceField.setText("");
    ambulanceField.setText("");

    hospitalField.setText("");
    designationField.setText("");

    stationField.setText("");
    badgeField.setText("");

    nameField.requestFocusInWindow();
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new RegisterFrame().setVisible(true)
        );
    }
}