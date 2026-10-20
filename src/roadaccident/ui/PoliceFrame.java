package roadaccident.ui;

import roadaccident.dao.PoliceDashboardDAO;
import roadaccident.model.PoliceIncident;
import roadaccident.model.TimelineEvent;
import roadaccident.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PoliceFrame extends JFrame {

    static {
        AppTheme.applyTheme();
    }

    private final User user;
    private final PoliceDashboardDAO dashboardDAO =
            new PoliceDashboardDAO();
    private final List<PoliceIncident> incidents =
            new ArrayList<>();

    private final DefaultTableModel incidentTableModel =
            new DefaultTableModel(
                    new String[]{
                            "Accident ID",
                            "Reported",
                            "Location",
                            "Severity",
                            "Victims",
                            "Status"
                    },
                    0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
    private final JTable incidentTable = new JTable(incidentTableModel);
    private final JTextArea detailsArea = new JTextArea();
    private final JTextArea timelineArea = new JTextArea();

    public PoliceFrame(User user) {
        this.user = user;

        setTitle("Police Incident Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1180, 760);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        buildUI();
        loadIncidents();
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel title = new JLabel(
                "POLICE INCIDENT DASHBOARD  |  "
                        + user.getFullName()
                        + "  |  Zone: " + formatZone(user.getZone())
        );
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadIncidents());
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logoutToHome());

        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.add(title, BorderLayout.CENTER);
        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerActions.add(refreshButton);
        headerActions.add(logoutButton);
        header.add(headerActions, BorderLayout.EAST);

        incidentTable.setRowHeight(25);
        incidentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        incidentTable.setAutoCreateRowSorter(true);
        TableLayout.wrapCellsAndFitRows(incidentTable);
        incidentTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                showSelectedIncident();
            }
        });

        JScrollPane incidentsPane = new JScrollPane(incidentTable);
        incidentsPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        incidentsPane.setBorder(BorderFactory.createTitledBorder(
                "Accident Reports in Your Zone"
        ));

        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        detailsArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane detailsPane = new JScrollPane(detailsArea);
        detailsPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        detailsPane.setBorder(BorderFactory.createTitledBorder(
                "Selected Incident and Response Details"
        ));

        timelineArea.setEditable(false);
        timelineArea.setLineWrap(true);
        timelineArea.setWrapStyleWord(true);
        timelineArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JScrollPane timelinePane = new JScrollPane(timelineArea);
        timelinePane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        timelinePane.setBorder(BorderFactory.createTitledBorder(
                "Incident Timeline"
        ));

        JSplitPane detailTimeline = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                detailsPane,
                timelinePane
        );
        detailTimeline.setResizeWeight(0.58);

        JSplitPane content = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                incidentsPane,
                detailTimeline
        );
        content.setResizeWeight(0.45);
        content.setContinuousLayout(true);

        JButton relativesButton =
                new JButton("Record Relatives Informed");
        relativesButton.addActionListener(e -> recordRelativeInformed());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER));
        actions.add(relativesButton);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(content, BorderLayout.CENTER);
        mainPanel.add(actions, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }

    private void loadIncidents() {
        try {
            incidents.clear();
            incidents.addAll(
                    dashboardDAO.getIncidentsByZone(user.getZone())
            );

            incidentTableModel.setRowCount(0);
            for (PoliceIncident incident : incidents) {
                incidentTableModel.addRow(new Object[]{
                        "ACC-" + incident.getAccidentId(),
                        incident.getReportedAt(),
                        incident.getLocation(),
                        incident.getSeverity(),
                        incident.getVictimCount(),
                        incident.getStatus()
                });
            }

            if (incidentTableModel.getRowCount() > 0) {
                incidentTable.setRowSelectionInterval(0, 0);
            } else {
                detailsArea.setText("No accident reports in this zone.");
                timelineArea.setText("");
            }
        } catch (SQLException | IllegalArgumentException ex) {
            showError("Could not load accident reports.", ex);
        }
    }

    private void showSelectedIncident() {
        int viewRow = incidentTable.getSelectedRow();
        if (viewRow < 0) {
            return;
        }

        int modelRow = incidentTable.convertRowIndexToModel(viewRow);
        PoliceIncident incident = incidents.get(modelRow);
        detailsArea.setText(
                "Accident ID: ACC-" + incident.getAccidentId()
                        + "\nReporter: " + display(incident.getReporterName())
                        + "\nReporter phone: " + display(incident.getReporterPhone())
                        + "\nAccident date/time: "
                        + display(incident.getAccidentDate()) + " "
                        + display(incident.getAccidentTime())
                        + "\nLocation: " + display(incident.getLocation())
                        + "\nAccident type: " + display(incident.getAccidentType())
                        + "\nSeverity: " + display(incident.getSeverity())
                        + "\nVictims: " + incident.getVictimCount()
                        + "\nDescription: " + display(incident.getDescription())
                        + "\nCurrent status: " + display(incident.getStatus())
                        + "\n\nAmbulance pickup time: "
                        + display(incident.getPickupTime())
                        + "\nDriver: " + display(incident.getDriverName())
                        + "\nAmbulance number: "
                        + display(incident.getAmbulanceNumber())
                        + "\nDestination hospital: "
                        + display(incident.getHospitalName())
                        + "\nHospital phone: "
                        + display(incident.getHospitalPhone())
        );

        try {
            StringBuilder timeline = new StringBuilder();
            for (TimelineEvent event :
                    dashboardDAO.getTimeline(incident.getAccidentId())) {
                if (timeline.length() > 0) {
                    timeline.append("\n\n");
                }
                timeline.append(event);
            }
            timelineArea.setText(timeline.toString());
        } catch (SQLException ex) {
            showError("Could not load the incident timeline.", ex);
        }
    }

    private void recordRelativeInformed() {
        int viewRow = incidentTable.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select an accident report first.",
                    "No Incident Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow = incidentTable.convertRowIndexToModel(viewRow);
        PoliceIncident incident = incidents.get(modelRow);

        JTextField relativeName = new JTextField(20);
        JTextField relativePhone = new JTextField(20);
        JComboBox<String> contactMethod = new JComboBox<>(
                new String[]{"Phone call", "SMS", "In person", "Other"}
        );

        JPanel form = new JPanel(new GridLayout(0, 1, 4, 4));
        form.add(new JLabel("Relative name:"));
        form.add(relativeName);
        form.add(new JLabel("Contact number:"));
        form.add(relativePhone);
        form.add(new JLabel("Contact method:"));
        form.add(contactMethod);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Record Relatives Informed - ACC-" + incident.getAccidentId(),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) {
            return;
        }
        if (relativeName.getText().isBlank()
                || relativePhone.getText().isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Enter the relative's name and contact number.",
                    "Missing Contact Details",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            dashboardDAO.recordRelativeInformed(
                    incident.getAccidentId(),
                    user.getUserId(),
                    relativeName.getText(),
                    relativePhone.getText(),
                    contactMethod.getSelectedItem().toString()
            );
            showSelectedIncident();
            JOptionPane.showMessageDialog(
                    this,
                    "Relative contact recorded in the incident timeline.",
                    "Contact Recorded",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (SQLException ex) {
            showError("Could not record relative contact.", ex);
        }
    }

    private String display(String value) {
        return value == null || value.isBlank() ? "Not recorded" : value;
    }

    private String formatZone(String zone) {
        if (zone == null || zone.isBlank()) {
            return "Not set";
        }
        String formatted = zone.trim()
                .replace('_', ' ')
                .toLowerCase(java.util.Locale.ROOT);
        if (formatted.endsWith(" zone")) {
            formatted = formatted.substring(
                    0,
                    formatted.length() - " zone".length()
            ).trim();
        }
        String[] words = formatted.split("\\s+");
        StringBuilder titleCase = new StringBuilder();
        for (String word : words) {
            if (titleCase.length() > 0) {
                titleCase.append(' ');
            }
            titleCase.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1));
        }
        return titleCase.toString();
    }

    private void showError(String message, Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                message + "\n" + ex.getMessage(),
                "Police Dashboard Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void logoutToHome() {
        new HomeFrame().setVisible(true);
        dispose();
    }
}
