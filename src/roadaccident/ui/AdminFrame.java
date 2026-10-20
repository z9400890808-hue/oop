package roadaccident.ui;

import roadaccident.dao.AdminDashboardDAO;
import roadaccident.util.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class AdminFrame extends JFrame {

    static {
        AppTheme.applyTheme();
    }

    private final JLabel pendingCountLabel =
            new JLabel("0", SwingConstants.CENTER);

    private final JLabel ambulanceCountLabel =
            new JLabel("0", SwingConstants.CENTER);

    private final JLabel hospitalCountLabel =
            new JLabel("0", SwingConstants.CENTER);

    private final JLabel policeCountLabel =
            new JLabel("0", SwingConstants.CENTER);

    private final JLabel approvedCountLabel =
            new JLabel("0", SwingConstants.CENTER);

    private final JLabel rejectedCountLabel =
            new JLabel("0", SwingConstants.CENTER);

    private final DefaultTableModel tableModel =
            createUserTableModel();

    private final JTable pendingTable =
            new JTable(tableModel);

    private JScrollPane userTableScroll;

    private String displayedUserRole;

    private final AdminDashboardDAO dashboardDAO =
            new AdminDashboardDAO();

    public AdminFrame() {
        setTitle("Road Accident Management - Admin Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);

        buildUI();
        refreshDashboard();
    }

    private void buildUI() {
        JPanel mainPanel =
                new JPanel(new BorderLayout(15, 15));

        mainPanel.setBorder(
                new EmptyBorder(20, 20, 20, 20)
        );

        mainPanel.setBackground(
                new Color(245, 248, 252)
        );

        JLabel title = new JLabel(
                "ADMIN DASHBOARD"
        );

        title.setFont(
                new Font("SansSerif", Font.BOLD, 26)
        );

        title.setForeground(
                new Color(27, 54, 93)
        );

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

        JPanel topPanel =
                new JPanel(new BorderLayout());

        topPanel.setBackground(
                new Color(245, 248, 252)
        );

        JPanel actionPanel =
                new JPanel(new FlowLayout(
                        FlowLayout.RIGHT,
                        10,
                        0
                ));

        actionPanel.setBackground(
                new Color(245, 248, 252)
        );
        actionPanel.add(refreshButton);
        actionPanel.add(logoutButton);

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(actionPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel =
                new JPanel(new BorderLayout(15, 15));

        centerPanel.setBackground(
                new Color(245, 248, 252)
        );

        centerPanel.add(
                createStatisticsPanel(),
                BorderLayout.NORTH
        );

        pendingTable.setRowHeight(28);
        pendingTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        TableLayout.wrapCellsAndFitRows(pendingTable);

        userTableScroll = new JScrollPane(pendingTable);
        userTableScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        userTableScroll.setBorder(
                BorderFactory.createTitledBorder("Pending Registrations")
        );

        centerPanel.add(userTableScroll, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel approvalActionPanel =
                new JPanel(new FlowLayout(
                        FlowLayout.CENTER,
                        15,
                        8
                ));
        approvalActionPanel.setBackground(new Color(245, 248, 252));

        JButton approveButton =
                new JButton("Approve Selected User");
        JButton rejectButton =
                new JButton("Reject Selected User");
        approveButton.addActionListener(
                e -> updateSelectedUser("APPROVED")
        );
        rejectButton.addActionListener(
                e -> updateSelectedUser("REJECTED")
        );
        approvalActionPanel.add(approveButton);
        approvalActionPanel.add(rejectButton);

        mainPanel.add(approvalActionPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private DefaultTableModel createUserTableModel() {
        return new DefaultTableModel(
                new String[]{
                        "ID",
                        "Name",
                        "Phone",
                        "Role",
                        "Zone",
                        "Details",
                        "Registered On"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void logoutToHome() {
        HomeFrame homeFrame = new HomeFrame();
        homeFrame.setVisible(true);
        dispose();
    }

    private JPanel createStatisticsPanel() {
        JPanel statsPanel =
                new JPanel(new GridLayout(2, 3, 12, 12));

        statsPanel.setBackground(
                new Color(245, 248, 252)
        );

        statsPanel.add(
                createCountCard(
                        "Pending Registrations",
                        pendingCountLabel,
                        new Color(245, 158, 11)
                )
        );

        JPanel ambulanceCard =
                createCountCard(
                        "Ambulance Drivers",
                        ambulanceCountLabel,
                        new Color(22, 163, 74)
                );
        makeCardClickable(
                ambulanceCard,
                () -> loadUsersByRole("AMBULANCE_DRIVER")
        );
        statsPanel.add(ambulanceCard);

        JPanel hospitalCard =
                createCountCard(
                        "Hospital Staff",
                        hospitalCountLabel,
                        new Color(37, 99, 235)
                );
        makeCardClickable(
                hospitalCard,
                () -> loadUsersByRole("HOSPITAL_STAFF")
        );
        statsPanel.add(hospitalCard);

        JPanel policeCard =
                createCountCard(
                        "Police Officers",
                        policeCountLabel,
                        new Color(124, 58, 237)
                );
        makeCardClickable(
                policeCard,
                () -> loadUsersByRole("POLICE")
        );
        statsPanel.add(policeCard);

        statsPanel.add(
                createCountCard(
                        "Approved Users",
                        approvedCountLabel,
                        new Color(5, 150, 105)
                )
        );

        statsPanel.add(
                createCountCard(
                        "Rejected Users",
                        rejectedCountLabel,
                        new Color(220, 38, 38)
                )
        );

        return statsPanel;
    }

    private JPanel createCountCard(
            String title,
            JLabel countLabel,
            Color color) {

        JPanel card =
                new JPanel(new BorderLayout());

        card.setBackground(Color.WHITE);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                color,
                                2
                        ),
                        new EmptyBorder(10, 10, 10, 10)
                )
        );

        JLabel titleLabel = new JLabel(
                title,
                SwingConstants.CENTER
        );

        titleLabel.setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );

        countLabel.setFont(
                new Font("SansSerif", Font.BOLD, 28)
        );

        countLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(countLabel, BorderLayout.CENTER);

        return card;
    }

    private void makeCardClickable(
            JPanel card,
            Runnable action) {
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                action.run();
            }
        });
        for (Component child : card.getComponents()) {
            child.setCursor(
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            );
            child.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent event) {
                    action.run();
                }
            });
        }
    }

    private void refreshDashboard() {
        try {
            pendingCountLabel.setText(
                    String.valueOf(
                            dashboardDAO.getCountByStatus(
                                    "PENDING"
                            )
                    )
            );

            approvedCountLabel.setText(
                    String.valueOf(
                            dashboardDAO.getCountByStatus(
                                    "APPROVED"
                            )
                    )
            );

            rejectedCountLabel.setText(
                    String.valueOf(
                            dashboardDAO.getCountByStatus(
                                    "REJECTED"
                            )
                    )
            );

            ambulanceCountLabel.setText(
                    String.valueOf(
                            dashboardDAO.getCountByRole(
                                    "AMBULANCE_DRIVER"
                            )
                    )
            );

            hospitalCountLabel.setText(
                    String.valueOf(
                            dashboardDAO.getCountByRole(
                                    "HOSPITAL_STAFF"
                            )
                    )
            );

            policeCountLabel.setText(
                    String.valueOf(
                            dashboardDAO.getCountByRole(
                                    "POLICE"
                            )
                    )
            );

            displayedUserRole = null;
            loadPendingUsers();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load dashboard data.\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    private void loadPendingUsers()
            throws SQLException {

        displayedUserRole = null;
        tableModel.setRowCount(0);
        pendingTable.clearSelection();
        userTableScroll.setBorder(
                BorderFactory.createTitledBorder("Pending Registrations")
        );

        try (Connection connection =
                     DatabaseConnection.getConnection();
             ResultSet result =
                     dashboardDAO.getPendingUsers(
                             connection
                     )) {

            while (result.next()) {
                addUserRow(result);
            }
        }
    }

    private void loadUsersByRole(String role) {
        try {
            displayedUserRole = role;
            tableModel.setRowCount(0);
            pendingTable.clearSelection();
            userTableScroll.setBorder(
                    BorderFactory.createTitledBorder(
                            "Approved " + formatRole(role)
                    )
            );

            try (Connection connection =
                         DatabaseConnection.getConnection();
                 ResultSet result =
                         dashboardDAO.getApprovedUsersByRole(
                                 connection,
                                 role
                         )) {
                while (result.next()) {
                    addUserRow(result);
                }
            }

            if (tableModel.getRowCount() == 0) {
                userTableScroll.setBorder(
                        BorderFactory.createTitledBorder(
                                "Approved "
                                        + formatRole(role)
                                        + " (no accounts found)"
                        )
                );
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load " + formatRole(role).toLowerCase(Locale.ROOT)
                            + " details.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void addUserRow(ResultSet result)
            throws SQLException {
        String role = result.getString("role");
        tableModel.addRow(new Object[]{
                result.getInt("user_id"),
                result.getString("full_name"),
                result.getString("phone_number"),
                formatRole(role),
                formatZone(result.getString("zone")),
                getRoleDetails(result, role),
                result.getTimestamp("created_at")
        });
    }

    private String getRoleDetails(
            ResultSet result,
            String role) throws SQLException {
        String normalizedRole = normalizeRole(role);

        if ("AMBULANCE_DRIVER".equals(normalizedRole)) {
            return "Licence: "
                    + safeValue(
                            result.getString(
                                    "driving_licence_number"
                            )
                    )
                    + " | Ambulance: "
                    + safeValue(
                            result.getString(
                                    "ambulance_number"
                            )
                    );
        }

        if ("HOSPITAL_STAFF".equals(normalizedRole)) {
            return "Hospital: "
                    + safeValue(
                            result.getString(
                                    "hospital_name"
                            )
                    )
                    + " | Designation: "
                    + safeValue(
                            result.getString(
                                    "designation"
                            )
                    );
        }

        if ("POLICE".equals(normalizedRole)) {
            return "Station: "
                    + safeValue(
                            result.getString(
                                    "police_station"
                            )
                    )
                    + " | Badge: "
                    + safeValue(
                            result.getString(
                                    "badge_number"
                            )
                    );
        }

        return "-";
    }

    private String safeValue(String value) {
        return value == null || value.isBlank()
                ? "-"
                : value;
    }

    private String formatRole(String role) {
        String normalizedRole = normalizeRole(role);

        if ("AMBULANCE_DRIVER".equals(normalizedRole)) {
            return "Ambulance Driver";
        }

        if ("HOSPITAL_STAFF".equals(normalizedRole)) {
            return "Hospital Staff";
        }

        if ("POLICE".equals(normalizedRole)) {
            return "Police";
        }

        return role;
    }

    private String normalizeRole(String role) {
        return role == null
                ? ""
                : role.trim()
                        .toUpperCase(Locale.ROOT)
                        .replace(' ', '_');
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

    private void updateSelectedUser(String status) {
        if (displayedUserRole != null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Approval actions are only available for pending registrations.",
                    "Approved User",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        int selectedRow = pendingTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select a pending registration first.",
                    "No User Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int userId = (int) tableModel.getValueAt(
                selectedRow,
                0
        );

        String name = (String) tableModel.getValueAt(
                selectedRow,
                1
        );

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Do you want to "
                        + status.toLowerCase()
                        + " registration for:\n"
                        + name + "?",
                "Confirm Action",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean updated =
                    dashboardDAO.updateRegistrationStatus(
                            userId,
                            status
                    );

            if (updated) {
                JOptionPane.showMessageDialog(
                        this,
                        "Registration "
                                + status.toLowerCase()
                                + " successfully."
                );

                refreshDashboard();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not update registration.\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new AdminFrame().setVisible(true)
        );
    }
}