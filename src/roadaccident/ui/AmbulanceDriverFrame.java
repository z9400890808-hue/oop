package roadaccident.ui;

import roadaccident.dao.AmbulanceDriverDAO;
import roadaccident.model.AccidentAlert;
import roadaccident.model.Hospital;
import roadaccident.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class AmbulanceDriverFrame extends JFrame {

    static {
        AppTheme.applyTheme();
    }

    private final User driverUser;
    private final AmbulanceDriverDAO ambulanceDriverDAO =
            new AmbulanceDriverDAO();

    private final DefaultTableModel accidentReportsTableModel =
            new DefaultTableModel(
                    new String[]{
                            "Accident ID",
                            "Location",
                            "Reporter Phone",
                            "Severity",
                            "Victims",
                            "Description",
                            "Status",
                            "Pickup Time"
                    },
                    0
            ) {
                @Override
                public boolean isCellEditable(
                        int modelRowIndex,
                        int modelColumnIndex
                ) {
                    return false;
                }
            };

    private final DefaultTableModel availableHospitalsTableModel =
            new DefaultTableModel(
                    new String[]{
                            "Hospital ID",
                            "Hospital",
                            "Zone",
                            "Available Beds",
                            "Phone"
                    },
                    0
            ) {
                @Override
                public boolean isCellEditable(
                        int modelRowIndex,
                        int modelColumnIndex
                ) {
                    return false;
                }
            };

    private final JTable accidentReportsTable =
            new JTable(accidentReportsTableModel);
    private final JTable availableHospitalsTable =
            new JTable(availableHospitalsTableModel);

    public AmbulanceDriverFrame(User authenticatedDriver) {
        this.driverUser = authenticatedDriver;

        setTitle("Ambulance Driver Dashboard");
        setSize(1100, 720);
        setMinimumSize(new Dimension(800, 520));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel dashboardPanel = new JPanel(new BorderLayout(16, 16));
        dashboardPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        );

        JLabel welcomeMessageLabel = new JLabel(
                "Welcome " + driverUser.getFullName()
                        + " | Zone: " + driverUser.getZone(),
                SwingConstants.LEFT
        );

        JButton refreshDashboardButton = new JButton("Refresh");
        refreshDashboardButton.addActionListener(
                e -> refreshDashboard()
        );

        JButton logoutDashboardButton = new JButton("Logout");
        logoutDashboardButton.addActionListener(e -> logoutToHome());

        JPanel dashboardHeader = new JPanel(new BorderLayout(12, 0));
        dashboardHeader.add(welcomeMessageLabel, BorderLayout.CENTER);

        JPanel dashboardHeaderActions =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        dashboardHeaderActions.add(refreshDashboardButton);
        dashboardHeaderActions.add(logoutDashboardButton);
        dashboardHeader.add(dashboardHeaderActions, BorderLayout.EAST);

        accidentReportsTable.setRowHeight(26);
        accidentReportsTable.setAutoCreateRowSorter(true);
        TableLayout.wrapCellsAndFitRows(accidentReportsTable);
        JScrollPane accidentReportsScrollPane =
                new JScrollPane(accidentReportsTable);
        accidentReportsScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        accidentReportsScrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Accident Reports in Your Zone"
                )
        );

        availableHospitalsTable.setRowHeight(26);
        availableHospitalsTable.setAutoCreateRowSorter(true);
        availableHospitalsTable.getColumnModel().getColumn(0).setMinWidth(0);
        availableHospitalsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        availableHospitalsTable.getColumnModel().getColumn(0)
                .setPreferredWidth(0);
        TableLayout.wrapCellsAndFitRows(availableHospitalsTable);
        JScrollPane availableHospitalsScrollPane =
                new JScrollPane(availableHospitalsTable);
        availableHospitalsScrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        availableHospitalsScrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Hospitals with Available Beds in Your Zone"
                )
        );

        JSplitPane dashboardDataSplitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                accidentReportsScrollPane,
                availableHospitalsScrollPane
        );
        dashboardDataSplitPane.setResizeWeight(0.58);
        dashboardDataSplitPane.setContinuousLayout(true);

        JButton pickupVictimButton =
                new JButton("Victim Picked Up - Inform Hospital");
        pickupVictimButton.addActionListener(e -> pickUpVictim());

        JPanel dashboardActionPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 8, 0)
        );
        dashboardActionPanel.add(pickupVictimButton);

        dashboardPanel.add(dashboardHeader, BorderLayout.NORTH);
        dashboardPanel.add(dashboardDataSplitPane, BorderLayout.CENTER);
        dashboardPanel.add(dashboardActionPanel, BorderLayout.SOUTH);

        setContentPane(dashboardPanel);
        refreshDashboard();
    }

    private void refreshDashboard() {
        try {
            List<AccidentAlert> accidentAlerts =
                    ambulanceDriverDAO.getAccidentsByZone(
                            driverUser.getZone()
                    );
            List<Hospital> availableHospitals =
                    ambulanceDriverDAO.getHospitalsByZone(
                            driverUser.getZone()
                    );

            accidentReportsTableModel.setRowCount(0);
            for (AccidentAlert accidentAlert : accidentAlerts) {
                accidentReportsTableModel.addRow(new Object[]{
                        accidentAlert.getAccidentId(),
                        accidentAlert.getLocation(),
                        accidentAlert.getReporterPhone(),
                        accidentAlert.getSeverity(),
                        accidentAlert.getVictimCount(),
                        accidentAlert.getDescription(),
                        accidentAlert.getStatus(),
                        accidentAlert.getPickupTime()
                });
            }

            availableHospitalsTableModel.setRowCount(0);
            for (Hospital availableHospital : availableHospitals) {
                availableHospitalsTableModel.addRow(new Object[]{
                        availableHospital.getHospitalId(),
                        availableHospital.getHospitalName(),
                        availableHospital.getZone(),
                        availableHospital.getAvailableBeds(),
                        availableHospital.getPhoneNumber()
                });
            }
        } catch (SQLException | IllegalArgumentException dashboardLoadException) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load dashboard data.\n"
                            + dashboardLoadException.getMessage(),
                    "Dashboard Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void pickUpVictim() {
        int selectedAccidentViewRow =
                accidentReportsTable.getSelectedRow();
        int selectedHospitalViewRow =
                availableHospitalsTable.getSelectedRow();

        if (selectedAccidentViewRow < 0) {
            showSelectionWarning("Select an accident report first.");
            return;
        }

        if (selectedHospitalViewRow < 0) {
            showSelectionWarning("Select a hospital with available beds.");
            return;
        }

        int selectedAccidentModelRow =
                accidentReportsTable.convertRowIndexToModel(
                        selectedAccidentViewRow
                );
        int selectedHospitalModelRow =
                availableHospitalsTable.convertRowIndexToModel(
                        selectedHospitalViewRow
                );

        int selectedAccidentId =
                (Integer) accidentReportsTableModel.getValueAt(
                        selectedAccidentModelRow,
                        0
                );
        int selectedHospitalId =
                (Integer) availableHospitalsTableModel.getValueAt(
                        selectedHospitalModelRow,
                        0
                );

        String selectedHospitalName =
                availableHospitalsTableModel.getValueAt(
                        selectedHospitalModelRow,
                        1
                ).toString();

        int pickupConfirmation = JOptionPane.showConfirmDialog(
                this,
                "Confirm victim pickup for accident ACC-"
                        + selectedAccidentId
                        + " and destination " + selectedHospitalName + "?",
                "Confirm Pickup",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (pickupConfirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Timestamp recordedPickupTime =
                    ambulanceDriverDAO.recordPickupAndNotifyHospital(
                            selectedAccidentId,
                            driverUser.getUserId(),
                            selectedHospitalId
                    );

            JOptionPane.showMessageDialog(
                    this,
                    "Victim pickup recorded.\n"
                            + "Pickup time: " + recordedPickupTime
                            + "\n\nHospital notification:\n"
                            + "Accident ACC-" + selectedAccidentId
                            + ": a victim has been picked up and is being "
                            + "taken to " + selectedHospitalName
                            + ". Pickup time: " + recordedPickupTime,
                    "Pickup Saved",
                    JOptionPane.INFORMATION_MESSAGE
            );
            refreshDashboard();
        } catch (SQLException pickupSaveException) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not record victim pickup.\n"
                            + pickupSaveException.getMessage(),
                    "Pickup Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showSelectionWarning(String warningMessage) {
        JOptionPane.showMessageDialog(
                this,
                warningMessage,
                "Select Items",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void logoutToHome() {
        HomeFrame homeDashboardFrame = new HomeFrame();
        homeDashboardFrame.setVisible(true);
        dispose();
    }
}
