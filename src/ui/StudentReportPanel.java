package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Arc2D;

/**
 * StudentReportPanel - Displays detailed student performance report with charts
 * Integrated into the Dashboard's Reports section
 */
public class StudentReportPanel extends JPanel {
    private static final Color BACKGROUND = new Color(255, 253, 246);
    private static final Color TEXT_PRIMARY = new Color(26, 26, 26);
    private static final Color TEXT_SECONDARY = new Color(55, 55, 55);
    private static final Color ACCENT = new Color(220, 180, 100);
    private static final Color BORDER_COLOR = new Color(0, 0, 0, 12);
    private static final Color COLOR_ACADEMICS = new Color(52, 152, 219);
    private static final Color COLOR_CODING = new Color(46, 204, 113);
    private static final Color COLOR_CLUBS = new Color(155, 89, 182);

    public StudentReportPanel() {
        initializePanel();
    }

    private void initializePanel() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 26, 28, 28));

        JPanel scrollContent = new JPanel();
        scrollContent.setOpaque(false);
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));

        // Title
        scrollContent.add(buildReportTitle());
        scrollContent.add(Box.createVerticalStrut(20));

        // Performance Metrics Charts
        scrollContent.add(buildPerformanceChartsSection());
        scrollContent.add(Box.createVerticalStrut(18));

        // Monthly Progress Chart
        scrollContent.add(buildMonthlyProgressChart());
        scrollContent.add(Box.createVerticalStrut(18));

        // Overall Stats
        scrollContent.add(buildOverallStatsSection());
        scrollContent.add(Box.createVerticalStrut(18));

        // Quick Guide
        scrollContent.add(buildQuickGuideSection());
        scrollContent.add(Box.createVerticalGlue());

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JComponent buildReportTitle() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel title = new JLabel("📊 Your Performance Report");
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(TEXT_PRIMARY);

        titlePanel.add(title);
        return titlePanel;
    }

    private JComponent buildPerformanceChartsSection() {
        RoundedPanel card = new RoundedPanel(28, new Color(255, 250, 237), BORDER_COLOR, 0, 0);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(24, 26, 24, 26));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        JLabel titleLabel = new JLabel("📈 Performance Areas");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(20));

        // Three performance gauges in a row
        JPanel gaugesPanel = new JPanel();
        gaugesPanel.setOpaque(false);
        gaugesPanel.setLayout(new GridLayout(1, 3, 20, 0));
        gaugesPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        gaugesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        gaugesPanel.add(buildPerformanceGauge("Academics", 72, COLOR_ACADEMICS));
        gaugesPanel.add(buildPerformanceGauge("Coding", 86, COLOR_CODING));
        gaugesPanel.add(buildPerformanceGauge("Clubs", 45, COLOR_CLUBS));

        card.add(gaugesPanel);
        return card;
    }

    private JComponent buildPerformanceGauge(String label, int percentage, Color color) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                int centerX = width / 2;
                int centerY = height / 2 - 20;
                int radius = 50;

                // Background circle
                g2d.setColor(new Color(200, 200, 200, 30));
                g2d.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

                // Progress arc
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                Arc2D arc = new Arc2D.Float(centerX - radius, centerY - radius, radius * 2, radius * 2,
                        90, -(percentage * 3.6f), Arc2D.OPEN);
                g2d.draw(arc);

                // Center percentage text
                g2d.setColor(TEXT_PRIMARY);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
                FontMetrics fm = g2d.getFontMetrics();
                String percentText = percentage + "%";
                int textX = centerX - fm.stringWidth(percentText) / 2;
                int textY = centerY + fm.getAscent() / 2;
                g2d.drawString(percentText, textX, textY);

                // Label below
                g2d.setColor(TEXT_SECONDARY);
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 14));
                fm = g2d.getFontMetrics();
                int labelX = centerX - fm.stringWidth(label) / 2;
                g2d.drawString(label, labelX, height - 10);
            }
        };
    }

    private JComponent buildMonthlyProgressChart() {
        RoundedPanel card = new RoundedPanel(28, new Color(255, 250, 237), BORDER_COLOR, 0, 0);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(24, 26, 24, 26));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        JLabel titleLabel = new JLabel("📅 Monthly Progress Trend");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(15));

        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                drawBarChart(g2d, getWidth(), getHeight());
            }
        };
        chartPanel.setOpaque(false);
        chartPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        chartPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(chartPanel);
        return card;
    }

    private void drawBarChart(Graphics2D g2d, int width, int height) {
        int[] data = {28, 22, 35, 24, 33, 21, 31};
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul"};
        
        int padding = 40;
        int chartWidth = width - 2 * padding;
        int chartHeight = height - 60;
        int barWidth = chartWidth / data.length - 10;
        int maxValue = 40;

        // Draw grid lines
        g2d.setColor(new Color(200, 200, 200, 30));
        g2d.setStroke(new BasicStroke(1));
        for (int i = 0; i <= 4; i++) {
            int y = padding + (chartHeight / 4) * i;
            g2d.drawLine(padding, y, width - padding, y);
        }

        // Draw bars
        for (int i = 0; i < data.length; i++) {
            int barHeight = (data[i] * chartHeight) / maxValue;
            int x = padding + i * (barWidth + 10);
            int y = padding + chartHeight - barHeight;

            // Bar background
            Color barColor = new Color(52 + i * 10, 152 - i * 10, 219 - i * 15);
            g2d.setColor(barColor);
            g2d.fillRoundRect(x, y, barWidth, barHeight, 8, 8);

            // Bar value text
            g2d.setColor(TEXT_PRIMARY);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2d.drawString(String.valueOf(data[i]), x + barWidth / 2 - 8, y - 5);

            // Month label
            g2d.setColor(TEXT_SECONDARY);
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2d.drawString(months[i], x + barWidth / 2 - 12, height - 15);
        }
    }

    private JComponent buildOverallStatsSection() {
        RoundedPanel card = new RoundedPanel(28, new Color(255, 250, 237), BORDER_COLOR, 0, 0);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(24, 26, 24, 26));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel titleLabel = new JLabel("🎯 Overall Performance Stats");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(15));

        // Stats grid
        JPanel statsPanel = new JPanel();
        statsPanel.setOpaque(false);
        statsPanel.setLayout(new GridLayout(2, 2, 20, 15));
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        statsPanel.add(buildStatCard("Rank", "12th", "Out of 150", new Color(255, 107, 107)));
        statsPanel.add(buildStatCard("Percentile", "92%", "Top performers", new Color(52, 152, 219)));
        statsPanel.add(buildStatCard("Overall Score", "81%", "Excellent", new Color(46, 204, 113)));
        statsPanel.add(buildStatCard("Status", "Active", "Updated today", new Color(155, 89, 182)));

        card.add(statsPanel);
        return card;
    }

    private JComponent buildStatCard(String title, String value, String subtitle, Color accentColor) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Left accent bar
                g2d.setColor(accentColor);
                g2d.fillRoundRect(0, 0, 6, getHeight(), 3, 3);

                // Border
                g2d.setColor(new Color(200, 200, 200, 40));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(12, 15, 12, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleLabel.setForeground(TEXT_SECONDARY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        valueLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(3));
        panel.add(valueLabel);
        panel.add(Box.createVerticalStrut(2));
        panel.add(subtitleLabel);

        return panel;
    }

    private JComponent buildQuickGuideSection() {
        RoundedPanel card = new RoundedPanel(28, new Color(245, 250, 255), new Color(100, 150, 255, 20), 2, 1);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 22, 20, 22));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JLabel titleLabel = new JLabel("💡 Quick Tips to Improve");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 100, 200));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(12));

        String[] tips = {
            "✓ Focus on Coding - Your current score is 86%. Keep it up!",
            "✓ Join Clubs - Increase participation to improve overall ranking",
            "✓ Update Achievements - Regularly add new projects and certificates"
        };

        for (String tip : tips) {
            JLabel tipLabel = new JLabel(tip);
            tipLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            tipLabel.setForeground(TEXT_SECONDARY);
            tipLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(tipLabel);
            card.add(Box.createVerticalStrut(6));
        }

        return card;
    }
}
