package ui;

import model.DashboardData;
import model.User;
import service.SubmissionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class ModernDashboardPanel extends JPanel {
    private final DashboardData data;
    private final User user;
    private final Map<String, Integer> submissionStats;
    private final Map<String, Integer> achievementCounts;
    private final List<Map<String, String>> submissions;

    public ModernDashboardPanel(DashboardData data, User user) {
        this.data = data == null ? new DashboardData() : data;
        this.user = user;
        this.submissionStats = user == null ? Map.of("total", 0, "approved", 0, "pending", 0, "rejected", 0) : SubmissionService.getSubmissionStats(user);
        this.achievementCounts = user == null ? Map.of("projects", 0, "certificates", 0, "workshops", 0) : SubmissionService.getAchievementCounts(user);
        this.submissions = user == null ? java.util.Collections.emptyList() : SubmissionService.getSubmissions(user.prn);

        setLayout(new BorderLayout());
        setOpaque(false);
        add(buildScrollView(), BorderLayout.CENTER);
    }

    private JComponent buildScrollView() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(0, 0, 0, 0));

        content.add(buildHeaderCard());
        content.add(Box.createVerticalStrut(18));
        content.add(buildStatsRow());
        content.add(Box.createVerticalStrut(18));
        content.add(buildProgressCard());
        content.add(Box.createVerticalStrut(18));
        content.add(buildAchievementsCard());
        content.add(Box.createVerticalStrut(18));
        content.add(buildSubmissionsCard());

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JComponent buildHeaderCard() {
        JPanel card = createCard(new BorderLayout(0, 12));

        JLabel title = new JLabel("Student Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(new Color(28, 36, 46));

        JTextArea summary = new JTextArea(
                "Student: " + data.studentName + "\n"
                        + "Class: " + data.className + "\n"
                        + "Rank: #" + data.rank + " of " + data.totalStudents + "\n"
                        + "Percentile: " + data.percentile + "th percentile\n\n"
                        + data.primaryGuideTitle + ": " + data.primaryGuideText + "\n"
                        + data.secondaryGuideTitle + ": " + data.secondaryGuideText
        );
        summary.setEditable(false);
        summary.setOpaque(false);
        summary.setLineWrap(true);
        summary.setWrapStyleWord(true);
        summary.setFont(new Font("SansSerif", Font.PLAIN, 15));
        summary.setForeground(new Color(80, 88, 96));

        card.add(title, BorderLayout.NORTH);
        card.add(summary, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setOpaque(false);
        row.add(buildMetricCard("Approved", String.valueOf(submissionStats.getOrDefault("approved", 0)), new Color(54, 179, 126)));
        row.add(buildMetricCard("Pending", String.valueOf(submissionStats.getOrDefault("pending", 0)), new Color(228, 163, 56)));
        row.add(buildMetricCard("Rejected", String.valueOf(submissionStats.getOrDefault("rejected", 0)), new Color(208, 91, 91)));
        row.add(buildMetricCard("Total Uploads", String.valueOf(submissionStats.getOrDefault("total", 0)), new Color(70, 120, 210)));
        return row;
    }

    private JComponent buildMetricCard(String label, String value, Color color) {
        JPanel card = createCard(new BorderLayout(0, 8));
        JLabel labelView = new JLabel(label);
        labelView.setFont(new Font("SansSerif", Font.PLAIN, 14));
        labelView.setForeground(new Color(104, 112, 120));

        JLabel valueView = new JLabel(value);
        valueView.setFont(new Font("SansSerif", Font.BOLD, 30));
        valueView.setForeground(color);

        card.add(labelView, BorderLayout.NORTH);
        card.add(valueView, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildProgressCard() {
        JPanel card = createCard(new BorderLayout(0, 16));
        JLabel title = new JLabel("Verified Progress");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel bars = new JPanel();
        bars.setOpaque(false);
        bars.setLayout(new BoxLayout(bars, BoxLayout.Y_AXIS));
        bars.add(buildProgressRow("Academics", data.academicsProgress, new Color(70, 120, 210)));
        bars.add(Box.createVerticalStrut(12));
        bars.add(buildProgressRow("Coding", data.codingProgress, new Color(54, 179, 126)));
        bars.add(Box.createVerticalStrut(12));
        bars.add(buildProgressRow("Clubs", data.clubsProgress, new Color(228, 163, 56)));

        card.add(title, BorderLayout.NORTH);
        card.add(bars, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildProgressRow(String label, int value, Color color) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);

        JLabel labelView = new JLabel(label);
        labelView.setPreferredSize(new Dimension(120, 24));
        labelView.setFont(new Font("SansSerif", Font.BOLD, 14));

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(Math.max(0, Math.min(100, value)));
        bar.setStringPainted(true);
        bar.setForeground(color);
        bar.setBackground(new Color(236, 240, 244));

        row.add(labelView, BorderLayout.WEST);
        row.add(bar, BorderLayout.CENTER);
        return row;
    }

    private JComponent buildAchievementsCard() {
        JPanel card = createCard(new GridLayout(1, 3, 14, 0));
        card.add(buildAchievementCell("Projects", achievementCounts.getOrDefault("projects", 0)));
        card.add(buildAchievementCell("Certificates", achievementCounts.getOrDefault("certificates", 0)));
        card.add(buildAchievementCell("Workshops", achievementCounts.getOrDefault("workshops", 0)));
        return card;
    }

    private JComponent buildAchievementCell(String label, int value) {
        JPanel cell = new JPanel();
        cell.setOpaque(false);
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));

        JLabel valueView = new JLabel(String.valueOf(value));
        valueView.setFont(new Font("SansSerif", Font.BOLD, 28));
        valueView.setForeground(new Color(28, 36, 46));
        valueView.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelView = new JLabel(label + " approved");
        labelView.setFont(new Font("SansSerif", Font.PLAIN, 14));
        labelView.setForeground(new Color(104, 112, 120));
        labelView.setAlignmentX(Component.LEFT_ALIGNMENT);

        cell.add(valueView);
        cell.add(Box.createVerticalStrut(6));
        cell.add(labelView);
        return cell;
    }

    private JComponent buildSubmissionsCard() {
        JPanel card = createCard(new BorderLayout(0, 12));

        JLabel title = new JLabel("Uploaded Work");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"Title", "Type", "Status", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Map<String, String> submission : submissions) {
            String rawDate = submission.getOrDefault("date", "");
            String date = rawDate.contains("T") ? rawDate.substring(0, rawDate.indexOf('T')) : rawDate;
            model.addRow(new Object[]{
                    submission.getOrDefault("title", ""),
                    submission.getOrDefault("type", ""),
                    submission.getOrDefault("status", ""),
                    date
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setFillsViewportHeight(true);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 2) {
                    String status = value == null ? "" : value.toString().toLowerCase();
                    if ("approved".equals(status)) {
                        component.setForeground(new Color(54, 179, 126));
                    } else if ("rejected".equals(status)) {
                        component.setForeground(new Color(208, 91, 91));
                    } else {
                        component.setForeground(new Color(228, 163, 56));
                    }
                } else {
                    component.setForeground(new Color(28, 36, 46));
                }
                return component;
            }
        };
        for (int column = 0; column < table.getColumnCount(); column++) {
            table.getColumnModel().getColumn(column).setCellRenderer(renderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 226, 234)));

        card.add(title, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private JPanel createCard(LayoutManager layout) {
        JPanel card = new JPanel(layout);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 234)),
                new EmptyBorder(18, 18, 18, 18)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        return card;
    }
}
