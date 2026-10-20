package roadaccident.ui;

import roadaccident.dao.PoliceDashboardDAO;
import roadaccident.model.TimelineEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class CheckStatusFrame extends JFrame {

    static {
        AppTheme.applyTheme();
    }

    private final JFrame returnFrame;
    private final JTextField idField = new JTextField(12);
    private final JTextArea timelineArea = createTextArea();
    private final PoliceDashboardDAO policeDashboardDAO =
            new PoliceDashboardDAO();

    public CheckStatusFrame() {
        this(null);
    }

    public CheckStatusFrame(JFrame returnFrame) {
        this.returnFrame = returnFrame;

        setTitle("Check Accident Status");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(760, 650);
        setMinimumSize(new Dimension(620, 520));
        setLocationRelativeTo(null);

        buildUI();
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent event) {
                showReturnFrame();
            }
        });
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(new EmptyBorder(16, 18, 16, 18));

        JButton backButton = new JButton("Back");
        backButton.addActionListener(event -> dispose());

        JLabel title = new JLabel(
                "Check Accident Status",
                SwingConstants.CENTER
        );
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        JPanel header = new JPanel(new BorderLayout());
        header.add(backButton, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        searchPanel.add(new JLabel("Accident ID:"));
        searchPanel.add(idField);

        JButton checkButton = new JButton("Check Status");
        checkButton.addActionListener(event -> checkStatus());
        idField.addActionListener(event -> checkStatus());
        searchPanel.add(checkButton);
        content.add(searchPanel, BorderLayout.NORTH);

        JScrollPane timelineScroll = new JScrollPane(timelineArea);
        timelineScroll.setBorder(
                BorderFactory.createTitledBorder("Incident Timeline")
        );
        timelineScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        content.add(timelineScroll, BorderLayout.CENTER);

        mainPanel.add(content, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private static JTextArea createTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textArea.setBorder(new EmptyBorder(8, 8, 8, 8));
        return textArea;
    }

    private void checkStatus() {
        String idText = idField.getText().trim();
        if (idText.isEmpty()) {
            showWarning("Enter an accident ID.");
            return;
        }

        int accidentId;
        try {
            accidentId = Integer.parseInt(idText);
            if (accidentId <= 0) {
                showWarning("Accident ID must be a positive number.");
                return;
            }
        } catch (NumberFormatException exception) {
            showWarning("Accident ID must be a number.");
            return;
        }

        timelineArea.setText("");
        try {
            showTimeline(accidentId);
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load accident status.\n" + exception.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            exception.printStackTrace();
        }
    }

    private void showTimeline(int accidentId) throws SQLException {
        StringBuilder timeline = new StringBuilder();
        for (TimelineEvent event : policeDashboardDAO.getTimeline(accidentId)) {
            if (timeline.length() > 0) {
                timeline.append("\n\n");
            }
            timeline.append(event);
        }

        if (timeline.length() == 0) {
            timeline.append("No accident found for ID ACC-")
                    .append(accidentId)
                    .append('.');
        }
        timelineArea.setText(timeline.toString());
        timelineArea.setCaretPosition(0);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Check Accident Status",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void showReturnFrame() {
        if (returnFrame != null) {
            returnFrame.setVisible(true);
            returnFrame.toFront();
        } else {
            new HomeFrame().setVisible(true);
        }
    }
}
