package ui;

import model.DashboardData;
import model.User;
import service.SubmissionService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;
import java.util.Map;

/**
 * Modern Dashboard Panel with Charts, Summary Cards, and Tables
 * Displays comprehensive student performance data with visualizations
 * Fetches real data from Supabase backend
 */
public class ModernDashboardPanel extends JPanel {
    private final DashboardData data;
    private final User user;
    private final Color primaryColor = new Color(76, 175, 80); // Green
    private final Color secondaryColor = new Color(33, 150, 243); // Blue
    private final Color accentColor = new Color(255, 152, 0); // Orange
    private final Color warningColor = new Color(244, 67, 54); // Red
    private final Color successColor = new Color(76, 175, 80); // Green
    private final Color backgroundColor = new Color(240, 245, 250);
    private final Color cardBackground = Color.WHITE;
    
    private Map<String, Integer> submissionStats;
    private Map<String, Integer> achievementCounts;
    private List<Map<String, String>> submissions;

    public ModernDashboardPanel(DashboardData data, User user) {
        this.data = data;
        this.user = user;
        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Load data from Supabase
        loadData();
        
        add(buildScrollableContent(), BorderLayout.CENTER);
    }

    public ModernDashboardPanel(DashboardData data) {
        this(data, null);
    }

    private void loadData() {
        if (user != null && user.prn != null) {
            submissionStats = SubmissionService.getSubmissionStats(user.prn);
            achievementCounts = SubmissionService.getAchievementCounts(user.prn);
            submissions = SubmissionService.getSubmissions(user.prn);
        } else {
            // Fallback to defaults
            submissionStats = new HashMap<>();
            submissionStats.put("total", 13);
            submissionStats.put("approved", 10);
            submissionStats.put("pending", 2);
            submissionStats.put("rejected", 1);
            
            achievementCounts = new HashMap<>();
            achievementCounts.put("projects", 5);
            achievementCounts.put("certificates", 8);
            achievementCounts.put("workshops", 3);
            
            submissions = new ArrayList<>();
        }
    }

    private JComponent buildScrollableContent() {
        JPanel content = new JPanel();
        content.setBackground(backgroundColor);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Title
        content.add(buildTitle());
        content.add(Box.createVerticalStrut(20));

        // Student Info Section
        content.add(buildStudentInfoSection());
        content.add(Box.createVerticalStrut(20));

        // Performance Summary Cards
        content.add(buildPerformanceSummaryCards());
        content.add(Box.createVerticalStrut(20));

        // Achievements Summary
        content.add(buildAchievementsSummary());
        content.add(Box.createVerticalStrut(20));

        // Progress Bars Section
        content.add(buildProgressBarsSection());
        content.add(Box.createVerticalStrut(20));

        // Charts Row
        content.add(buildChartsRow());
        content.add(Box.createVerticalStrut(20));

        // Submission Status
        content.add(buildSubmissionStatusSection());
        content.add(Box.createVerticalStrut(20));

        // Submissions Table
        content.add(buildSubmissionsTable());
        content.add(Box.createVerticalStrut(20));

        // Insights Section
        content.add(buildInsightsSection());

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JComponent buildTitle() {
        JLabel title = new JLabel("📊 Student Performance Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(new Color(33, 33, 33));
        return title;
    }

    private JComponent buildStudentInfoSection() {
        JPanel section = new JPanel();
        section.setBackground(cardBackground);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel studentName = new JLabel("Student: Saniya");
        studentName.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel prn = new JLabel("PRN: 101");
        prn.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JLabel dept = new JLabel("Department: Computer Engineering");
        dept.setFont(new Font("SansSerif", Font.PLAIN, 16));

        section.add(studentName);
        section.add(Box.createVerticalStrut(8));
        section.add(prn);
        section.add(Box.createVerticalStrut(8));
        section.add(dept);

        return section;
    }

    private JComponent buildPerformanceSummaryCards() {
        JPanel cardsPanel = new JPanel();
        cardsPanel.setBackground(backgroundColor);
        cardsPanel.setLayout(new GridLayout(1, 4, 20, 0));
        cardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        cardsPanel.add(buildSummaryCard("CGPA", "8.5", primaryColor, "📊"));
        cardsPanel.add(buildSummaryCard("Overall Score", "82", secondaryColor, "🎯"));
        cardsPanel.add(buildSummaryCard("Rank", "3", accentColor, "🏆"));
        cardsPanel.add(buildSummaryCard("Status", "Excellent", new Color(156, 39, 176), "⭐"));

        return cardsPanel;
    }

    private JComponent buildSummaryCard(String label, String value, Color color, String icon) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawSummaryCard(g, label, value, color, icon);
            }
        };
    }

    private void drawSummaryCard(Graphics g, String label, String value, Color color, String icon) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Gradient background
        GradientPaint gradient = new GradientPaint(0, 0, color, width, height, 
                new Color(color.getRed(), color.getGreen(), color.getBlue(), 180));
        g2.setPaint(gradient);
        g2.fillRoundRect(0, 0, width, height, 20, 20);

        // Icon
        g2.setFont(new Font("SansSerif", Font.BOLD, 32));
        g2.drawString(icon, 20, 50);

        // Label
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.drawString(label, 20, 75);

        // Value
        g2.setFont(new Font("SansSerif", Font.BOLD, 28));
        g2.drawString(value, 20, 110);

        // Border
        g2.setColor(new Color(255, 255, 255, 100));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(0, 0, width - 1, height - 1, 20, 20);
    }

    private JComponent buildAchievementsSummary() {
        JPanel section = new JPanel();
        section.setBackground(cardBackground);
        section.setLayout(new GridLayout(1, 3, 15, 0));
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        int projects = achievementCounts.getOrDefault("projects", 5);
        int certs = achievementCounts.getOrDefault("certificates", 8);
        int workshops = achievementCounts.getOrDefault("workshops", 3);
        
        section.add(buildAchievementItem("🎯 Projects", projects + " Completed"));
        section.add(buildAchievementItem("📜 Certificates", certs + " Earned"));
        section.add(buildAchievementItem("🏆 Workshops", workshops + " Attended"));

        return section;
    }

    private JComponent buildProgressBarsSection() {
        JPanel section = new JPanel();
        section.setBackground(cardBackground);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("📈 Performance Progress");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(33, 33, 33));
        section.add(title);
        section.add(Box.createVerticalStrut(15));

        // Add progress bars
        section.add(buildProgressBarItem("Academics", 85, new Color(76, 175, 80)));
        section.add(Box.createVerticalStrut(12));
        section.add(buildProgressBarItem("Coding Skills", 78, new Color(33, 150, 243)));
        section.add(Box.createVerticalStrut(12));
        section.add(buildProgressBarItem("Club Activities", 72, new Color(255, 152, 0)));
        section.add(Box.createVerticalStrut(12));
        section.add(buildProgressBarItem("Certifications", 88, new Color(156, 39, 176)));
        section.add(Box.createVerticalStrut(12));
        section.add(buildProgressBarItem("Project Work", 82, new Color(229, 57, 53)));

        return section;
    }

    private JComponent buildProgressBarItem(String label, int percentage, Color barColor) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Label
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("SansSerif", Font.PLAIN, 14));
        labelText.setForeground(new Color(55, 55, 55));
        labelText.setPreferredSize(new Dimension(150, 20));

        // Percentage text
        JLabel percentText = new JLabel(percentage + "%");
        percentText.setFont(new Font("SansSerif", Font.BOLD, 14));
        percentText.setForeground(barColor);
        percentText.setPreferredSize(new Dimension(50, 20));

        // Progress bar component
        JComponent progressBar = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();

                // Background bar
                g2.setColor(new Color(240, 240, 240));
                g2.fillRoundRect(0, (height - 12) / 2, width, 12, 6, 6);

                // Filled bar
                int filledWidth = (width * percentage) / 100;
                GradientPaint gradient = new GradientPaint(0, 0, barColor, 
                        filledWidth, 0, new Color(barColor.getRed(), barColor.getGreen(), 
                        barColor.getBlue(), 150));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, (height - 12) / 2, filledWidth, 12, 6, 6);

                // Border
                g2.setColor(new Color(200, 200, 200));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, (height - 12) / 2, width - 1, 11, 6, 6);
            }
        };
        progressBar.setPreferredSize(new Dimension(200, 30));

        panel.add(labelText, BorderLayout.WEST);
        panel.add(progressBar, BorderLayout.CENTER);
        panel.add(percentText, BorderLayout.EAST);

        return panel;
    }

    private JComponent buildAchievementItem(String title, String desc) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descLabel.setForeground(new Color(128, 128, 128));

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(descLabel);

        return panel;
    }

    private JComponent buildChartsRow() {
        JPanel chartsPanel = new JPanel();
        chartsPanel.setBackground(backgroundColor);
        chartsPanel.setLayout(new GridLayout(1, 2, 15, 0));
        chartsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        chartsPanel.add(buildBarChart());
        chartsPanel.add(buildPieChart());

        return chartsPanel;
    }

    private JComponent buildBarChart() {
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBarChart(g);
            }
        };
        chartPanel.setBackground(cardBackground);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        chartPanel.setPreferredSize(new Dimension(400, 300));
        return chartPanel;
    }

    private void drawBarChart(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int barWidth = 60;
        int startX = 50;
        int startY = height - 80;
        int maxHeight = 150;

        String[] labels = {"Projects", "Certificates", "Workshops"};
        int[] values = {5, 8, 3};
        Color[] colors = {primaryColor, secondaryColor, accentColor};

        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2.drawString("Achievements Distribution", 50, 30);

        for (int i = 0; i < labels.length; i++) {
            int barHeight = (values[i] * maxHeight) / 8;
            int x = startX + (i * 120);
            int y = startY - barHeight;

            g2.setColor(colors[i]);
            g2.fillRect(x, y, barWidth, barHeight);

            g2.setColor(new Color(128, 128, 128));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.drawString(values[i] + "", x + 20, startY + 25);
            g2.drawString(labels[i], x + 5, startY + 40);
        }
    }

    private JComponent buildPieChart() {
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawPieChart(g);
            }
        };
        chartPanel.setBackground(cardBackground);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        chartPanel.setPreferredSize(new Dimension(400, 300));
        return chartPanel;
    }

    private void drawPieChart(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int pieX = 60;
        int pieY = 50;
        int pieDiameter = 150;

        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2.setColor(new Color(33, 33, 33));
        g2.drawString("Submission Status", 60, 30);

        // Calculate percentages from real data
        int total = submissionStats.getOrDefault("total", 1);
        int approved = submissionStats.getOrDefault("approved", 10);
        int pending = submissionStats.getOrDefault("pending", 2);
        int rejected = submissionStats.getOrDefault("rejected", 1);
        
        int approvedAngle = (approved * 360) / total;
        int pendingAngle = (pending * 360) / total;
        int rejectedAngle = (rejected * 360) / total;

        // Approved slice
        g2.setColor(primaryColor);
        g2.fillArc(pieX, pieY, pieDiameter, pieDiameter, 0, approvedAngle);

        // Pending slice
        g2.setColor(accentColor);
        g2.fillArc(pieX, pieY, pieDiameter, pieDiameter, approvedAngle, pendingAngle);

        // Rejected slice
        g2.setColor(new Color(244, 67, 54));
        g2.fillArc(pieX, pieY, pieDiameter, pieDiameter, approvedAngle + pendingAngle, rejectedAngle);

        // Legend
        int legendX = pieX + pieDiameter + 40;
        int legendY = pieY + 20;
        drawLegendItem(g2, legendX, legendY, primaryColor, "Approved (" + approved + ")");
        drawLegendItem(g2, legendX, legendY + 30, accentColor, "Pending (" + pending + ")");
        drawLegendItem(g2, legendX, legendY + 60, new Color(244, 67, 54), "Rejected (" + rejected + ")");
    }

    private void drawLegendItem(Graphics2D g2, int x, int y, Color color, String label) {
        g2.setColor(color);
        g2.fillRect(x, y - 8, 12, 12);
        g2.setColor(new Color(33, 33, 33));
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.drawString(label, x + 18, y + 4);
    }

    private JComponent buildSubmissionStatusSection() {
        JPanel section = new JPanel();
        section.setBackground(cardBackground);
        section.setLayout(new GridLayout(1, 4, 15, 0));
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        int total = submissionStats.getOrDefault("total", 13);
        int approved = submissionStats.getOrDefault("approved", 10);
        int pending = submissionStats.getOrDefault("pending", 2);
        int rejected = submissionStats.getOrDefault("rejected", 1);
        
        section.add(buildStatusCard("Total Submissions", String.valueOf(total), new Color(33, 150, 243)));
        section.add(buildStatusCard("Approved", String.valueOf(approved), new Color(76, 175, 80)));
        section.add(buildStatusCard("Pending", String.valueOf(pending), new Color(255, 152, 0)));
        section.add(buildStatusCard("Rejected", String.valueOf(rejected), new Color(244, 67, 54)));

        return section;
    }

    private JComponent buildStatusCard(String label, String value, Color color) {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("SansSerif", Font.PLAIN, 12));
        labelText.setForeground(new Color(128, 128, 128));

        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("SansSerif", Font.BOLD, 32));
        valueText.setForeground(color);

        card.add(labelText);
        card.add(Box.createVerticalStrut(8));
        card.add(valueText);

        return card;
    }

    private JComponent buildSubmissionsTable() {
        String[] columns = {"Title", "Type", "Status", "Date"};
        
        // Use real data from Supabase if available
        Object[][] rows;
        if (submissions != null && !submissions.isEmpty()) {
            rows = new Object[submissions.size()][4];
            for (int i = 0; i < submissions.size(); i++) {
                Map<String, String> sub = submissions.get(i);
                rows[i][0] = sub.getOrDefault("title", "");
                rows[i][1] = sub.getOrDefault("type", "");
                rows[i][2] = sub.getOrDefault("status", "");
                rows[i][3] = sub.getOrDefault("date", "").split("T")[0]; // Format date
            }
        } else {
            // Fallback to sample data
            rows = new Object[][]{
                    {"AI Project Demo", "project", "approved", "2026-04-15"},
                    {"Python Certification", "certificate", "approved", "2026-04-10"},
                    {"Web Dev Workshop", "workshop", "pending", "2026-04-20"},
                    {"Mobile App", "project", "approved", "2026-03-28"},
                    {"Advanced Java Course", "certificate", "pending", "2026-04-25"},
                    {"Data Science Workshop", "workshop", "approved", "2026-03-15"},
                    {"Blockchain Project", "project", "rejected", "2026-02-28"},
                    {"AWS Certification", "certificate", "approved", "2026-04-01"},
                    {"Cloud Computing Workshop", "workshop", "approved", "2026-03-10"},
                    {"React JS Project", "project", "approved", "2026-04-05"}
            };
        }

        DefaultTableModel model = new DefaultTableModel(rows, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setRowHeight(32);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setShowGrid(true);
        table.setGridColor(new Color(240, 240, 240));

        // Custom cell renderer
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (column == 2) { // Status column
                    String status = (value != null ? value.toString() : "").toLowerCase();
                    if ("approved".equals(status)) {
                        c.setForeground(primaryColor);
                    } else if ("pending".equals(status)) {
                        c.setForeground(accentColor);
                    } else if ("rejected".equals(status)) {
                        c.setForeground(new Color(244, 67, 54));
                    }
                    ((JLabel) c).setFont(new Font("SansSerif", Font.BOLD, 12));
                }

                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        table.setBackground(cardBackground);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224)),
                BorderFactory.createEmptyBorder()
        ));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(backgroundColor);
        tablePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        JLabel tableTitle = new JLabel("📋 Recent Submissions");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        return tablePanel;
    }

    private JComponent buildInsightsSection() {
        JPanel section = new JPanel();
        section.setBackground(cardBackground);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("💡 Key Insights");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        section.add(title);
        section.add(Box.createVerticalStrut(12));

        JTextArea insights = new JTextArea(
                "✓ Current ranking is excellent with 8.5 CGPA\n" +
                "✓ 77% of submissions have been approved (10/13)\n" +
                "✓ Strong performance in projects (5 completed)\n" +
                "✓ Good certification track record (8 earned)\n" +
                "✓ Regular workshop participation (3 attended)\n" +
                "✓ Keep improving pending submissions to maintain rank"
        );
        insights.setFont(new Font("SansSerif", Font.PLAIN, 13));
        insights.setEditable(false);
        insights.setOpaque(false);
        insights.setLineWrap(true);
        insights.setWrapStyleWord(true);
        insights.setForeground(new Color(55, 55, 55));

        section.add(insights);

        return section;
    }
}
