package ui;

import model.Submission;
import model.User;
import service.SubmissionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentProgressPanel extends JPanel {
    private final User user;
    private final Runnable onBack;
    private final DefaultTableModel tableModel;
    private final JTable submissionTable;
    private final JProgressBar academicProgressBar;
    private final JProgressBar projectsProgressBar;
    private final JProgressBar certificatesProgressBar;
    private final JLabel rankLabel;
    private final JLabel statusLabel;
    private final JLabel approvedLabel;
    private final JLabel pendingLabel;
    private final JLabel totalLabel;
    private final JLabel lastRefreshLabel;
    private final JButton viewDetailsButton;

    public StudentProgressPanel(User user, Runnable onBack) {
        this.user = user;
        this.onBack = onBack;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        tableModel = new DefaultTableModel(new Object[]{"Title", "Type", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        academicProgressBar = createProgressBar();
        projectsProgressBar = createProgressBar();
        certificatesProgressBar = createProgressBar();
        rankLabel = new JLabel("Your Rank: #-- (based on approved data)");
        rankLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        statusLabel = new JLabel("Loading submissions...");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        approvedLabel = new JLabel("Approved: 0");
        approvedLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pendingLabel = new JLabel("Pending: 0");
        pendingLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        totalLabel = new JLabel("Total items: 0");
        totalLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lastRefreshLabel = new JLabel("Last refreshed: --");
        lastRefreshLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lastRefreshLabel.setForeground(new Color(95, 95, 95));

        submissionTable = new JTable(tableModel);
        viewDetailsButton = new JButton("View Details");
        add(buildContent(), BorderLayout.CENTER);
        refreshDashboard();
    }

    private JComponent buildContent() {
        GradientPanel background = new GradientPanel();
        background.setLayout(new BorderLayout(18, 18));
        background.setBorder(new EmptyBorder(18, 18, 18, 18));

        RoundedPanel shell = new RoundedPanel(28, new Color(255, 253, 246), new Color(0, 0, 0, 24), 8, 8);
        shell.setLayout(new BorderLayout(18, 18));
        shell.setBorder(new EmptyBorder(22, 22, 22, 22));

        JLabel titleLabel = new JLabel("My Progress Dashboard");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        titleLabel.setForeground(new Color(34, 34, 34));

        shell.add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(18, 18));
        contentPanel.setOpaque(false);
        contentPanel.add(buildMetricsPanel(), BorderLayout.NORTH);
        contentPanel.add(buildTablePanel(), BorderLayout.CENTER);

        shell.add(contentPanel, BorderLayout.CENTER);
        shell.add(buildActionPanel(), BorderLayout.SOUTH);
        background.add(shell, BorderLayout.CENTER);
        return background;
    }

    private JComponent buildMetricsPanel() {
        JPanel container = new JPanel(new GridLayout(1, 2, 18, 18));
        container.setOpaque(false);

        RoundedPanel progressCard = new RoundedPanel(24, new Color(255, 255, 255), new Color(0, 0, 0, 18), 0, 0);
        progressCard.setLayout(new GridLayout(3, 1, 14, 14));
        progressCard.setBorder(new EmptyBorder(18, 18, 18, 18));
        progressCard.add(createLabeledProgress("Academic Progress (CGPA)", academicProgressBar));
        progressCard.add(createLabeledProgress("Projects Completed", projectsProgressBar));
        progressCard.add(createLabeledProgress("Certificates Earned", certificatesProgressBar));

        RoundedPanel rankCard = new RoundedPanel(24, new Color(255, 255, 255), new Color(0, 0, 0, 18), 0, 0);
        rankCard.setLayout(new BoxLayout(rankCard, BoxLayout.Y_AXIS));
        rankCard.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel rankTitle = new JLabel("Your Rank");
        rankTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        rankTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        rankLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        approvedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        pendingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        lastRefreshLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        rankCard.add(rankTitle);
        rankCard.add(Box.createVerticalStrut(16));
        rankCard.add(rankLabel);
        rankCard.add(Box.createVerticalStrut(14));
        rankCard.add(statusLabel);
        rankCard.add(Box.createVerticalStrut(10));
        rankCard.add(approvedLabel);
        rankCard.add(Box.createVerticalStrut(6));
        rankCard.add(pendingLabel);
        rankCard.add(Box.createVerticalStrut(6));
        rankCard.add(totalLabel);
        rankCard.add(Box.createVerticalStrut(10));
        rankCard.add(lastRefreshLabel);

        container.add(progressCard);
        container.add(rankCard);
        return container;
    }

    private JPanel createLabeledProgress(String label, JProgressBar progressBar) {
        JPanel wrapper = new JPanel(new BorderLayout(8, 8));
        wrapper.setOpaque(false);
        JLabel sectionLabel = new JLabel(label);
        sectionLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        wrapper.add(sectionLabel, BorderLayout.NORTH);
        wrapper.add(progressBar, BorderLayout.CENTER);
        return wrapper;
    }

    private JComponent buildTablePanel() {
        RoundedPanel tableCard = new RoundedPanel(24, new Color(255, 255, 255), new Color(0, 0, 0, 18), 0, 0);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel tableTitle = new JLabel("Uploaded Items");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        tableTitle.setBorder(new EmptyBorder(0, 0, 12, 0));
        tableCard.add(tableTitle, BorderLayout.NORTH);

        submissionTable.setFillsViewportHeight(true);
        submissionTable.setRowHeight(32);
        submissionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        submissionTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        submissionTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        submissionTable.getSelectionModel().addListSelectionListener(e -> updateSelectionButtons(e));
        submissionTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && submissionTable.getSelectedRow() != -1) {
                    showSubmissionDetails();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(submissionTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableCard.add(scrollPane, BorderLayout.CENTER);
        return tableCard;
    }

    private JComponent buildActionPanel() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttons.setOpaque(false);

        JButton backButton = new JButton("Back");
        JButton refreshButton = new JButton("Refresh");
        JButton uploadProjectButton = new JButton("Upload Project");
        JButton uploadCertificateButton = new JButton("Upload Certificate");

        styleActionButton(backButton);
        styleSecondaryButton(refreshButton);
        styleSecondaryButton(viewDetailsButton);
        styleActionButton(uploadProjectButton);
        styleActionButton(uploadCertificateButton);

        backButton.addActionListener(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });
        refreshButton.addActionListener(e -> refreshDashboard());
        viewDetailsButton.addActionListener(e -> showSubmissionDetails());
        viewDetailsButton.setEnabled(false);
        uploadProjectButton.addActionListener(e -> openUploadDialog("Project"));
        uploadCertificateButton.addActionListener(e -> openUploadDialog("Certificate"));

        buttons.add(backButton);
        buttons.add(refreshButton);
        buttons.add(viewDetailsButton);
        buttons.add(uploadProjectButton);
        buttons.add(uploadCertificateButton);
        return buttons;
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setBackground(new Color(255, 255, 255));
        button.setForeground(new Color(51, 51, 51));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        button.setPreferredSize(new Dimension(120, 38));
    }

    private void updateSelectionButtons(ListSelectionEvent event) {
        if (!event.getValueIsAdjusting()) {
            viewDetailsButton.setEnabled(submissionTable.getSelectedRow() != -1);
        }
    }

    private void showSubmissionDetails() {
        int selectedRow = submissionTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }

        String title = (String) tableModel.getValueAt(selectedRow, 0);
        String type = (String) tableModel.getValueAt(selectedRow, 1);
        String status = (String) tableModel.getValueAt(selectedRow, 2);

        JOptionPane.showMessageDialog(this,
                "Title: " + title + "\nType: " + type + "\nStatus: " + status,
                "Submission Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void styleActionButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(new Color(251, 214, 69));
        button.setForeground(new Color(34, 34, 34));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 18, 12, 18));
    }

    private JProgressBar createProgressBar() {
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(0, 40));
        progressBar.setBackground(new Color(243, 243, 243));
        progressBar.setForeground(new Color(251, 214, 69));
        return progressBar;
    }

    private void openUploadDialog(String defaultType) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select project or certificate file");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileNameExtensionFilter("PDF / Image Files", "pdf", "png", "jpg", "jpeg", "gif"));

        int chooserResult = chooser.showOpenDialog(this);
        if (chooserResult != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = chooser.getSelectedFile();
        String title = JOptionPane.showInputDialog(this, "Enter title:", "Submission Title", JOptionPane.PLAIN_MESSAGE);
        if (title == null || title.isBlank()) {
            return;
        }

        String description = JOptionPane.showInputDialog(this, "Enter short description:", "Submission Description", JOptionPane.PLAIN_MESSAGE);
        if (description == null) {
            return;
        }

        String[] options = {"Project", "Certificate", "Workshop"};
        String type = (String) JOptionPane.showInputDialog(this, "Select type:", "Submission Type",
                JOptionPane.PLAIN_MESSAGE, null, options, defaultType);
        if (type == null || type.isBlank()) {
            return;
        }

        Submission submission = new Submission(title.trim(), type, "", "pending");
        submission.description = description.trim();
        new UploadWorker(submission, selectedFile).execute();
    }

    private void refreshDashboard() {
        new LoadSubmissionsWorker().execute();
    }

    private void updateDashboard(List<Submission> submissions) {
        tableModel.setRowCount(0);
        int approvedProjects = 0;
        int approvedCertificates = 0;
        int pending = 0;

        for (Submission item : submissions) {
            tableModel.addRow(new Object[]{item.title, item.type, item.status});
            if (item.isApproved()) {
                if ("Project".equalsIgnoreCase(item.type)) {
                    approvedProjects++;
                } else if ("Certificate".equalsIgnoreCase(item.type)) {
                    approvedCertificates++;
                }
            } else {
                pending++;
            }
        }

        int totalApproved = approvedProjects + approvedCertificates;
        int totalSubmissions = submissions.size();
        int cgpaValue = Math.min(100, approvedProjects * 10 + approvedCertificates * 5);
        academicProgressBar.setValue(cgpaValue);
        academicProgressBar.setString(String.format("%.1f / 10.0", cgpaValue / 10.0));

        projectsProgressBar.setValue(approvedProjects);
        projectsProgressBar.setMaximum(10);
        projectsProgressBar.setString(approvedProjects + " approved");

        certificatesProgressBar.setValue(approvedCertificates);
        certificatesProgressBar.setMaximum(10);
        certificatesProgressBar.setString(approvedCertificates + " approved");

        int rank = Math.max(1, 100 - totalApproved);
        rankLabel.setText("Your Rank: #" + rank + " (based on approved data)");
        statusLabel.setText("Loaded " + totalSubmissions + " items, " + totalApproved + " approved");
        approvedLabel.setText("Approved: " + totalApproved);
        pendingLabel.setText("Pending: " + pending);
        totalLabel.setText("Total items: " + totalSubmissions);
        lastRefreshLabel.setText("Last refreshed: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm")));
    }

    private class LoadSubmissionsWorker extends SwingWorker<List<Submission>, Void> {
        @Override
        protected List<Submission> doInBackground() {
            statusLabel.setText("Fetching submissions...");
            return SubmissionService.fetchSubmissions(user);
        }

        @Override
        protected void done() {
            try {
                updateDashboard(get());
            } catch (Exception e) {
                statusLabel.setText("Failed to load data.");
                JOptionPane.showMessageDialog(StudentProgressPanel.this,
                        "Unable to fetch submissions from the server.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class UploadWorker extends SwingWorker<Boolean, Void> {
        private final Submission submission;
        private final File file;

        public UploadWorker(Submission submission, File file) {
            this.submission = submission;
            this.file = file;
        }

        @Override
        protected Boolean doInBackground() {
            statusLabel.setText("Uploading submission...");
            return SubmissionService.uploadSubmission(submission, user, file);
        }

        @Override
        protected void done() {
            try {
                boolean success = get();
                if (success) {
                    JOptionPane.showMessageDialog(StudentProgressPanel.this,
                            "Submission sent successfully. It will appear as pending until approved.", "Upload Complete", JOptionPane.INFORMATION_MESSAGE);
                    refreshDashboard();
                } else {
                    statusLabel.setText("Upload failed.");
                    JOptionPane.showMessageDialog(StudentProgressPanel.this,
                            "Failed to upload submission. Please try again.", "Upload Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                statusLabel.setText("Upload failed.");
                JOptionPane.showMessageDialog(StudentProgressPanel.this,
                        "Unexpected error during upload.", "Upload Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            StudentProgressPanel panel = new StudentProgressPanel(new User(), null);
            JFrame frame = new JFrame("EduRanker Student Progress");
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setSize(980, 700);
            frame.setMinimumSize(new Dimension(940, 660));
            frame.setLocationRelativeTo(null);
            frame.setContentPane(panel);
            frame.setVisible(true);
        });
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint paint = new GradientPaint(0, 0, new Color(255, 238, 170), getWidth(), getHeight(), new Color(255, 220, 104));
            g2.setPaint(paint);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(255, 255, 255, 80));
            g2.fillOval(-100, -20, 380, 380);
            g2.fillOval(getWidth() - 280, getHeight() - 260, 300, 300);
            g2.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color fillColor;
        private final Color shadowColor;
        private final int shadowX;
        private final int shadowY;

        private RoundedPanel(int radius, Color fillColor, Color shadowColor, int shadowX, int shadowY) {
            this.radius = radius;
            this.fillColor = fillColor;
            this.shadowColor = shadowColor;
            this.shadowX = shadowX;
            this.shadowY = shadowY;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(shadowColor);
            g2.fillRoundRect(shadowX, shadowY, getWidth() - shadowX, getHeight() - shadowY, radius, radius);
            g2.setColor(fillColor);
            g2.fillRoundRect(0, 0, getWidth() - Math.max(shadowX, 0), getHeight() - Math.max(shadowY, 0), radius, radius);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }
}
