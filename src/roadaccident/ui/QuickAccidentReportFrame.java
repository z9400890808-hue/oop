
package roadaccident.ui;

import roadaccident.model.Accident;
import roadaccident.dao.AccidentDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class QuickAccidentReportFrame extends JFrame {

    static {
        AppTheme.applyTheme();
    }

    // Location
    private static final String[] LOCATIONS = {
            "KOZHIKODE ZONE",
            "WESTHILL ZONE",
            "EASTHILL ZONE"
    };

    // Accident types
    private static final String[] ACCIDENT_TYPES = {
            "Collision",
            "Hit and Run",
            "Rollover",
            "Pedestrian Involved",
            "Motorcycle",
            "Multi-Vehicle Pileup",
            "Other"
    };

    // Accident severity
    private static final String[] SEVERITIES = {
            "Minor",
            "Moderate",
            "Serious",
            "Critical"
    };

    // Form components
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField dateField;
    private JTextField timeField;
    private JComboBox<String> locationBox;

    private JComboBox<String> typeBox;
    private JComboBox<String> severityBox;

    private JSpinner victimSpinner;

    private JTextArea descriptionArea;

    // Constructor
    public QuickAccidentReportFrame() {

        setTitle("Report Accident");

        setSize(560, 780);

        setMinimumSize(new Dimension(520, 600));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Place window in the center of the screen
        setLocationRelativeTo(null);

        // Use BorderLayout
        setLayout(new BorderLayout());

        // Add different parts of the window
        add(buildHeader(), BorderLayout.NORTH);
        add(buildFormScroll(), BorderLayout.CENTER);
        add(buildButtonBar(), BorderLayout.SOUTH);
    }

    // --------------------------------------------------
    // Header
    // --------------------------------------------------

    private JPanel buildHeader() {

        JPanel header = new JPanel();

        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        header.setBorder(new EmptyBorder(25, 30, 15, 30));

        JLabel title = new JLabel("Report an Accident");

        title.setFont(new Font("Arial", Font.BOLD, 24));

        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "Fill in the details so help can be dispatched quickly."
        );

        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));

        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);

        header.add(Box.createRigidArea(new Dimension(0, 8)));

        header.add(subtitle);

        return header;
    }

    // --------------------------------------------------
    // Form
    // --------------------------------------------------

    private JScrollPane buildFormScroll() {

        // Create text fields
        nameField = new JTextField();

        phoneField = new JTextField();

        // Automatically set current local date
        dateField = new JTextField(
                LocalDate.now().toString()
        );

        // Automatically set current local time
        timeField = new JTextField(
                LocalTime.now().toString().substring(0, 5)
        );

        // Location
        locationBox = new JComboBox<>(LOCATIONS);

        // Accident type
        typeBox = new JComboBox<>(ACCIDENT_TYPES);

        // Accident severity
        severityBox = new JComboBox<>(SEVERITIES);

        // Create spinner
        victimSpinner = new JSpinner(
                new SpinnerNumberModel(1, 0, 999, 1)
        );

        // Create description area
        descriptionArea = new JTextArea(4, 20);

        descriptionArea.setLineWrap(true);

        descriptionArea.setWrapStyleWord(true);

        // Scroll pane for description
        JScrollPane descriptionScroll =
                new JScrollPane(descriptionArea);

        descriptionScroll.setPreferredSize(
                new Dimension(200, 100)
        );

        // Main form panel
        JPanel form = new JPanel();

        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        form.setBorder(new EmptyBorder(10, 30, 10, 30));

        // Add form fields

        addRow(
                form,
                row(
                        block("YOUR NAME", nameField),
                        block("PHONE NUMBER", phoneField)
                )
        );

        addRow(
                form,
                row(
                        block("DATE", dateField),
                        block("TIME", timeField)
                )
        );

        addRow(
                form,
                block("LOCATION", locationBox)
        );

        addRow(
                form,
                row(
                        block("ACCIDENT TYPE", typeBox),
                        block("SEVERITY", severityBox)
                )
        );

        addRow(
                form,
                block("NUMBER OF VICTIMS", victimSpinner)
        );

        addRow(
                form,
                block("DESCRIPTION", descriptionScroll)
        );

        // Add form to scroll pane
        JScrollPane scroll = new JScrollPane(form);

        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        return scroll;
    }

    // --------------------------------------------------
    // Create one field block
    // --------------------------------------------------

    private JPanel block(String labelText, JComponent field) {

        JPanel panel = new JPanel();

        panel.setLayout(
                new BoxLayout(panel, BoxLayout.Y_AXIS)
        );

        JLabel label = new JLabel(labelText);

        label.setFont(
                new Font("Arial", Font.BOLD, 12)
        );

        panel.add(label);

        panel.add(Box.createRigidArea(
                new Dimension(0, 5)
        ));

        panel.add(field);

        return panel;
    }

    // --------------------------------------------------
    // Create a row
    // --------------------------------------------------

    private JPanel row(JComponent... items) {

        JPanel panel = new JPanel();

        panel.setLayout(
                new GridLayout(1, items.length, 14, 0)
        );

        for (JComponent item : items) {
            panel.add(item);
        }

        return panel;
    }

    // --------------------------------------------------
    // Add row to form
    // --------------------------------------------------

    private void addRow(JPanel form, JComponent row) {

        form.add(row);

        form.add(
                Box.createRigidArea(
                        new Dimension(0, 16)
                )
        );
    }

    // --------------------------------------------------
    // Buttons
    // --------------------------------------------------

    private JPanel buildButtonBar() {

        JPanel panel = new JPanel();

        panel.setLayout(
                new GridLayout(1, 2, 14, 0)
        );

        panel.setBorder(
                new EmptyBorder(15, 30, 25, 30)
        );

        // Create Back button
        JButton backButton = new JButton("Back");

        // Create Submit button
        JButton submitButton =
                new JButton("Submit Report");

        // Set button size
        backButton.setPreferredSize(
                new Dimension(150, 45)
        );

        submitButton.setPreferredSize(
                new Dimension(150, 45)
        );

        // Back button event
        backButton.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        goHome();
                    }
                }
        );

        // Submit button event
        submitButton.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        submitReport();
                    }
                }
        );

        // Add buttons
        panel.add(backButton);

        panel.add(submitButton);

        // Pressing Enter will submit the form
        getRootPane().setDefaultButton(submitButton);

        return panel;
    }

    // --------------------------------------------------
    // Submit Accident Report
    // --------------------------------------------------

    private void submitReport() {

        // Get values from the form

        String name =
                nameField.getText().trim();

        String phone =
                phoneField.getText()
                        .trim()
                        .replaceAll("[\\s-]", "");

        String date =
                dateField.getText().trim();

        String time =
                timeField.getText().trim();

        String description =
                descriptionArea.getText().trim();

        // Check name
        if (name.isEmpty()) {

            fail(
                    "Please enter your name.",
                    nameField
            );

            return;
        }

        // Check phone number
        if (!phone.matches("\\+?[0-9]{10}")) {

            fail(
                    "Please enter a valid phone number.",
                    phoneField
            );

            return;
        }

        // No date validation
        // Date is automatically taken from LocalDate.now()

        // No time validation
        // Time is automatically taken from LocalTime.now()

        // Check description
        if (description.isEmpty()) {

            fail(
                    "Please describe what happened.",
                    descriptionArea
            );

            return;
        }

        // Create Accident object
        Accident accident = new Accident(

                name,

                phone,

                date,

                time,

                (String) locationBox.getSelectedItem(),

                (String) typeBox.getSelectedItem(),

                (String) severityBox.getSelectedItem(),

                (Integer) victimSpinner.getValue(),

                description
        );

        // Save accident
        int accidentId;
        try {
            accidentId = saveAccident(accident);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "The accident report could not be saved.\n"
                            + ex.getMessage(),
                    "Report Submission Failed",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Show success message
        JOptionPane.showMessageDialog(
                this,
                "Accident reported successfully.\n\n"
                        + "Accident ID: " + accidentId
                        + "\nType: " + accident.getAccidentType()
                        + "\nSeverity: " + accident.getSeverity()
                        + "\nLocation: " + accident.getLocation()
                        + "\n\nPlease remember your Accident ID.",
                "Report Submitted",
                JOptionPane.INFORMATION_MESSAGE
        );

        // Go back to home page
        goHome();
    }

    // --------------------------------------------------
    // Save Accident
    // --------------------------------------------------

    private int saveAccident(Accident accident) throws SQLException {

        return AccidentDAO.save(accident);
    }

    // --------------------------------------------------
    // Display Error Message
    // --------------------------------------------------

    private void fail(
            String message,
            JComponent target
    ) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Check the form",
                JOptionPane.WARNING_MESSAGE
        );

        // Move cursor to the incorrect field
        target.requestFocusInWindow();
    }

    // --------------------------------------------------
    // Go to Home Page
    // --------------------------------------------------

    private void goHome() {

        // Create HomeFrame
        HomeFrame home = new HomeFrame();

        // Show HomeFrame
        home.setVisible(true);

        // Close current window
        dispose();
    }
}