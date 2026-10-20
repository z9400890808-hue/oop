package roadaccident.ui;

import roadaccident.dao.HospitalStaffDAO;
import roadaccident.model.Hospital;
import roadaccident.model.HospitalNotification;
import roadaccident.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class HospitalStaffFrame extends JFrame {

    static {
        AppTheme.applyTheme();
    }

    private final User staffUser;
    private final HospitalStaffDAO hospitalDAO =
            new HospitalStaffDAO();

    private Hospital hospital;

    private final JLabel hospitalNameLabel =
            new JLabel("Hospital: Loading...");

    private final JLabel zoneLabel =
            new JLabel("Zone: -");

    private final JLabel availableBedsLabel =
            new JLabel("0", SwingConstants.CENTER);

    private final JTextField bedCountField =
            new JTextField(8);

    private final DefaultTableModel notificationTableModel =
            new DefaultTableModel(
                    new String[]{
                            "Notification ID",
                            "Accident ID",
                            "Location",
                            "Severity",
                            "Victims",
                            "Pickup Time",
                            "Driver",
                            "Message",
                            "Status"
                    },
                    0
            ) {
                @Override
                public boolean isCellEditable(
                        int row,
                        int column) {
                    return false;
                }
            };

    private final JTable notificationTable =
            new JTable(notificationTableModel);

    public HospitalStaffFrame(User staffUser) {
        this.staffUser = staffUser;

        setTitle("Hospital Staff Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1180, 720);
        setLocationRelativeTo(null);

        buildUI();
        refreshDashboard();
    }

    private void buildUI() {
        JPanel mainPanel =
                new JPanel(new BorderLayout(15, 15));

        mainPanel.setBorder(
                new EmptyBorder(18, 18, 18, 18)
        );

        mainPanel.setBackground(
                new Color(245, 248, 252)
        );

        mainPanel.add(createHeader(), BorderLayout.NORTH);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createHeader() {
        JPanel header =
                new JPanel(new BorderLayout());

        header.setBackground(
                new Color(245, 248, 252)
        );

        JLabel title =
                new JLabel("HOSPITAL STAFF DASHBOARD");

        title.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        24
                )
        );

        JLabel staffInfo =
                new JLabel(
                        "Staff: " + staffUser.getFullName()
                );

        staffInfo.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        JPanel titlePanel =
                new JPanel();

        titlePanel.setLayout(
                new BoxLayout(
                        titlePanel,
                        BoxLayout.Y_AXIS
                )
        );

        titlePanel.setBackground(
                new Color(245, 248, 252)
        );

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(staffInfo);
        titlePanel.add(Box.createVerticalStrut(4));
        JLabel staffZoneInfo =
                new JLabel("Staff zone: " + formatZone(staffUser.getZone()));
        staffZoneInfo.setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );
        titlePanel.add(staffZoneInfo);

        JButton refreshButton =
                new JButton("Refresh");

        refreshButton.addActionListener(
                e -> refreshDashboard()
        );

        JButton logoutButton =
                new JButton("Logout");

        logoutButton.addActionListener(
                e -> logoutToHome()
        );

        JPanel actionPanel =
                new JPanel(new FlowLayout(
                        FlowLayout.RIGHT,
                        8,
                        0
                ));

        actionPanel.setBackground(
                new Color(245, 248, 252)
        );
        actionPanel.add(refreshButton);
        actionPanel.add(logoutButton);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(actionPanel, BorderLayout.EAST);

        return header;
    }

    private void logoutToHome() {
        HomeFrame homeFrame = new HomeFrame();
        homeFrame.setVisible(true);
        dispose();
    }

    private JPanel createCenterPanel() {
        JPanel panel =
                new JPanel(new BorderLayout(12, 12));

        panel.setBackground(
                new Color(245, 248, 252)
        );

        panel.add(createHospitalInfoPanel(), BorderLayout.NORTH);

        notificationTable.setRowHeight(28);
        notificationTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        TableLayout.wrapCellsAndFitRows(notificationTable);

        JScrollPane scrollPane =
                new JScrollPane(notificationTable);
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Ambulance Alerts and Accident Details"
                )
        );

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createHospitalInfoPanel() {
        JPanel panel =
                new JPanel(new GridLayout(1, 2, 12, 12));

        panel.setBackground(
                new Color(245, 248, 252)
        );

        JPanel detailsCard =
                new JPanel();

        detailsCard.setLayout(
                new BoxLayout(
                        detailsCard,
                        BoxLayout.Y_AXIS
                )
        );

        detailsCard.setBackground(Color.WHITE);

        detailsCard.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(37, 99, 235),
                                2
                        ),
                        new EmptyBorder(15, 15, 15, 15)
                )
        );

        hospitalNameLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        18
                )
        );

        zoneLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );
        hospitalNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        zoneLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsCard.add(hospitalNameLabel);
        detailsCard.add(Box.createVerticalStrut(8));
        detailsCard.add(zoneLabel);

        JPanel bedsCard =
                new JPanel();

        bedsCard.setLayout(
                new BoxLayout(
                        bedsCard,
                        BoxLayout.Y_AXIS
                )
        );

        bedsCard.setBackground(Color.WHITE);

        bedsCard.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(22, 163, 74),
                                2
                        ),
                        new EmptyBorder(12, 12, 12, 12)
                )
        );

        JLabel bedTitle =
                new JLabel(
                        "AVAILABLE BEDS",
                        SwingConstants.CENTER
                );

        bedTitle.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        14
                )
        );

        bedTitle.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        availableBedsLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        34
                )
        );

        availableBedsLabel.setForeground(
                new Color(22, 163, 74)
        );

        availableBedsLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        bedsCard.add(bedTitle);
        bedsCard.add(Box.createVerticalStrut(6));
        bedsCard.add(availableBedsLabel);

        panel.add(detailsCard);
        panel.add(bedsCard);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel =
                new JPanel(new BorderLayout(12, 12));

        panel.setBackground(
                new Color(245, 248, 252)
        );

        JPanel bedPanel =
                new JPanel(new FlowLayout(
                        FlowLayout.LEFT,
                        8,
                        5
                ));

        bedPanel.setBackground(
                new Color(245, 248, 252)
        );

        bedPanel.add(
                new JLabel(
                        "Update Available Beds:"
                )
        );

        bedPanel.add(bedCountField);

        JButton updateBedsButton =
                new JButton("Update Beds");

        updateBedsButton.addActionListener(
                e -> updateBedAvailability()
        );

        bedPanel.add(updateBedsButton);

        JPanel actionPanel =
                new JPanel(new FlowLayout(
                        FlowLayout.RIGHT,
                        8,
                        5
                ));

        actionPanel.setBackground(
                new Color(245, 248, 252)
        );

        JButton markReadButton =
                new JButton("Mark Selected Alert as Read");

        markReadButton.addActionListener(
                e -> markSelectedNotificationAsRead()
        );

        actionPanel.add(markReadButton);

        panel.add(bedPanel, BorderLayout.WEST);
        panel.add(actionPanel, BorderLayout.EAST);

        return panel;
    }

    private void refreshDashboard() {
        String registeredHospitalName =
                staffUser.getHospitalName();
        hospitalNameLabel.setText(
                "Hospital: "
                        + (registeredHospitalName == null
                        || registeredHospitalName.isBlank()
                        ? "Not set on staff account"
                        : registeredHospitalName.trim())
        );
        zoneLabel.setText(
                "Zone: " + formatZone(staffUser.getZone())
        );
        hospital = null;

        try {
            if (registeredHospitalName == null
                    || registeredHospitalName.isBlank()) {
                hospitalNameLabel.setText(
                        "Hospital: Not set on staff account"
                );
                availableBedsLabel.setText("-");
                return;
            }

            hospital = hospitalDAO.getHospitalForStaff(
                    registeredHospitalName,
                    staffUser.getZone(),
                    staffUser.getPhoneNumber()
            );

            if (hospital == null) {
                throw new SQLException(
                        "Could not load the hospital record."
                );
            }

            hospitalNameLabel.setText(
                    "Hospital: "
                            + hospital.getHospitalName()
            );

            zoneLabel.setText(
                    "Zone: "
                            + formatZone(
                                    hospital.getZone()
                            )
            );
            availableBedsLabel.setText(
                    String.valueOf(
                            hospital.getAvailableBeds()
                    )
            );

            bedCountField.setText(
                    String.valueOf(
                            hospital.getAvailableBeds()
                    )
            );

            loadNotifications();

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void loadNotifications()
            throws SQLException {

        notificationTableModel.setRowCount(0);

        List<HospitalNotification> notifications =
                hospitalDAO.getHospitalNotifications(
                        hospital.getHospitalId()
                );

        for (HospitalNotification notification : notifications) {
            notificationTableModel.addRow(new Object[]{
                    notification.getNotificationId(),
                    "ACC-" + notification.getAccidentId(),
                    notification.getLocation(),
                    notification.getSeverity(),
                    notification.getVictimCount(),
                    notification.getPickupTime(),
                    notification.getDriverName(),
                    notification.getMessage(),
                    notification.getNotificationStatus()
            });
        }
    }

    private void updateBedAvailability() {
        if (hospital == null) {
            return;
        }

        String input =
                bedCountField.getText().trim();

        try {
            int beds = Integer.parseInt(input);

            if (beds < 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Bed count cannot be negative.",
                        "Invalid Bed Count",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            boolean updated =
                    hospitalDAO.updateAvailableBeds(
                            hospital.getHospitalId(),
                            beds
                    );

            if (updated) {
                JOptionPane.showMessageDialog(
                        this,
                        "Bed availability updated successfully."
                );

                refreshDashboard();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Enter a valid whole number for beds.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void markSelectedNotificationAsRead() {
        int row = notificationTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select an alert first.",
                    "No Alert Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int notificationId =
                (int) notificationTableModel.getValueAt(
                        row,
                        0
                );

        try {
            boolean updated =
                    hospitalDAO.markNotificationAsRead(
                            notificationId
                    );

            if (updated) {
                JOptionPane.showMessageDialog(
                        this,
                        "Alert marked as read."
                );

                refreshDashboard();
            }

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void showDatabaseError(SQLException ex) {
        JOptionPane.showMessageDialog(
                this,
                "Database error:\n"
                        + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );

        ex.printStackTrace();
    }

    private String formatZone(String zone) {
        if (zone == null || zone.isBlank()) {
            return "Not set";
        }

        String normalizedZone = zone.trim()
                .toUpperCase(java.util.Locale.ROOT)
                .replace('_', ' ');
        if (normalizedZone.endsWith(" ZONE")) {
            normalizedZone = normalizedZone.substring(
                    0,
                    normalizedZone.length() - " ZONE".length()
            ).trim();
        }

        if ("KOZHIKODE".equals(normalizedZone)) {
            return "Kozhikode";
        }

        if ("WESTHILL".equals(normalizedZone)) {
            return "Westhill";
        }

        if ("EASTHILL".equals(normalizedZone)) {
            return "Easthill";
        }

        return normalizedZone;
    }
}