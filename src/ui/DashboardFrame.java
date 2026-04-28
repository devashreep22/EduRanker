package ui;

import model.DashboardData;
import model.User;
import service.DashboardService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.List;

public class DashboardFrame extends JFrame {
    private final User user;
    private DashboardData data;

    public DashboardFrame(User user, DashboardData data) {
        this.user = user;
        this.data = data == null ? new DashboardData() : data;

        setTitle("EduRanker Dashboard");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1280, 820);
        setMinimumSize(new Dimension(1100, 760));
        setLocationRelativeTo(null);
        setContentPane(buildContent());
    }

    private JComponent buildContent() {
        GradientPanel root = new GradientPanel();
        root.setLayout(new BorderLayout());
        root.setBorder(new EmptyBorder(24, 24, 24, 24));

        RoundedPanel shell = new RoundedPanel(34, new Color(255, 253, 246), new Color(0, 0, 0, 26), 10, 12);
        shell.setLayout(new BorderLayout());

        shell.add(buildSidebar(), BorderLayout.WEST);
        shell.add(buildMainPanel(), BorderLayout.CENTER);

        root.add(shell, BorderLayout.CENTER);
        return root;
    }

    private JComponent buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(255, 0));
        sidebar.setBorder(new EmptyBorder(24, 22, 24, 18));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brandRow.setOpaque(false);
        brandRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        brandRow.add(new BrandIcon());

        JLabel brandLabel = new JLabel("EduRanker");
        brandLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        brandRow.add(brandLabel);

        sidebar.add(brandRow);
        sidebar.add(Box.createVerticalStrut(36));
        sidebar.add(buildMenuItem("Dashboard", true));
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(buildMenuItem("Progress / Projects", false));
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(buildMenuItem("Reports", false));
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(buildMenuItem("AI Mentor", false));
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(buildMenuItem("Settings", false));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(buildRefreshButton());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 0, 0, 12));
        wrapper.add(sidebar, BorderLayout.CENTER);
        return wrapper;
    }

    private JComponent buildMenuItem(String label, boolean active) {
        RoundedPanel panel = new RoundedPanel(18,
                active ? new Color(255, 250, 235) : new Color(255, 0, 0, 0),
                new Color(0, 0, 0, active ? 18 : 0), 0, 0);
        panel.setLayout(new BorderLayout());
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        panel.setPreferredSize(new Dimension(210, 62));

        JLabel item = new JLabel(label);
        item.setBorder(new EmptyBorder(0, 18, 0, 12));
        item.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 18));
        item.setForeground(new Color(26, 26, 26));
        panel.add(item, BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildRefreshButton() {
        JButton button = new JButton("Refresh data");
        button.setFocusPainted(false);
        button.setBackground(new Color(39, 39, 42));
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(12, 18, 12, 18));
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.addActionListener(event -> refreshDashboard(button));
        return button;
    }

    private JComponent buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(28, 26, 28, 28));

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 0;

        JLabel title = new JLabel(data.headline);
        title.setFont(new Font("SansSerif", Font.BOLD, 34));
        title.setBorder(new EmptyBorder(0, 8, 8, 0));
        main.add(title, BorderLayout.NORTH);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.62;
        content.add(buildGuideAndRankColumn(), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.38;
        content.add(buildProfileColumn(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.62;
        content.add(buildProgressCard(), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.38;
        content.add(buildMonthlyChartCard(), gbc);

        main.add(content, BorderLayout.CENTER);
        return main;
    }

    private JComponent buildGuideAndRankColumn() {
        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));

        JPanel guideRow = new JPanel(new GridLayout(1, 2, 18, 0));
        guideRow.setOpaque(false);
        guideRow.add(buildGuideCard(data.primaryGuideTitle, data.primaryGuideText));
        guideRow.add(buildGuideCard(data.secondaryGuideTitle, data.secondaryGuideText));

        column.add(guideRow);
        column.add(Box.createVerticalStrut(18));
        column.add(buildRankCard());
        return column;
    }

    private JComponent buildGuideCard(String title, String body) {
        RoundedPanel card = new RoundedPanel(28, new Color(252, 248, 227), new Color(0, 0, 0, 12), 0, 0);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(22, 24, 22, 24));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea bodyLabel = new JTextArea(body);
        bodyLabel.setEditable(false);
        bodyLabel.setOpaque(false);
        bodyLabel.setWrapStyleWord(true);
        bodyLabel.setLineWrap(true);
        bodyLabel.setFocusable(false);
        bodyLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        bodyLabel.setForeground(new Color(55, 55, 55));
        bodyLabel.setBorder(new EmptyBorder(14, 0, 0, 0));
        bodyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(bodyLabel);
        return card;
    }

    private JComponent buildRankCard() {
        RoundedPanel card = new RoundedPanel(28, new Color(255, 255, 255), new Color(0, 0, 0, 14), 0, 0);
        card.setBorder(new EmptyBorder(24, 26, 20, 26));
        card.setLayout(new BorderLayout());

        JLabel title = new JLabel("Rank");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        card.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(18, 0, 0, 0));

        JLabel rankValue = new JLabel(String.valueOf(data.rank));
        rankValue.setFont(new Font("SansSerif", Font.BOLD, 96));

        JLabel outOf = new JLabel("out of " + data.totalStudents);
        outOf.setFont(new Font("SansSerif", Font.PLAIN, 24));
        outOf.setForeground(new Color(104, 104, 104));
        outOf.setHorizontalAlignment(SwingConstants.RIGHT);

        content.add(rankValue, BorderLayout.WEST);
        content.add(outOf, BorderLayout.SOUTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildProfileColumn() {
        RoundedPanel card = new RoundedPanel(28, new Color(252, 248, 227), new Color(0, 0, 0, 12), 0, 0);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel identity = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        identity.setOpaque(false);
        identity.add(new AvatarPanel(data.studentName));

        JPanel names = new JPanel();
        names.setOpaque(false);
        names.setLayout(new BoxLayout(names, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(data.studentName);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

        JLabel classLabel = new JLabel(data.className);
        classLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        classLabel.setForeground(new Color(75, 75, 75));

        names.add(Box.createVerticalStrut(6));
        names.add(nameLabel);
        names.add(Box.createVerticalStrut(8));
        names.add(classLabel);

        identity.add(names);

        JLabel currentRank = new JLabel("<html>Current<br>Rank</html>");
        currentRank.setFont(new Font("SansSerif", Font.BOLD, 22));
        currentRank.setForeground(new Color(28, 28, 28));

        header.add(identity, BorderLayout.WEST);
        header.add(currentRank, BorderLayout.EAST);

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(232, 223, 190));
        separator.setBackground(new Color(232, 223, 190));

        JPanel stats = new JPanel();
        stats.setOpaque(false);
        stats.setLayout(new BoxLayout(stats, BoxLayout.Y_AXIS));

        JLabel rankLabel = new JLabel("Current Rank");
        rankLabel.setFont(new Font("SansSerif", Font.PLAIN, 17));

        JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        line.setOpaque(false);
        JLabel rankNumber = new JLabel(String.valueOf(data.rank));
        rankNumber.setFont(new Font("SansSerif", Font.BOLD, 56));
        JLabel percentile = new JLabel("percentile " + data.percentile + "th");
        percentile.setFont(new Font("SansSerif", Font.PLAIN, 18));
        percentile.setForeground(new Color(64, 64, 64));
        line.add(rankNumber);
        line.add(percentile);

        stats.add(rankLabel);
        stats.add(Box.createVerticalStrut(12));
        stats.add(line);

        card.add(header);
        card.add(Box.createVerticalStrut(24));
        card.add(separator);
        card.add(Box.createVerticalStrut(24));
        card.add(stats);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private JComponent buildProgressCard() {
        RoundedPanel card = new RoundedPanel(28, new Color(255, 255, 255), new Color(0, 0, 0, 14), 0, 0);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(22, 26, 26, 26));

        JLabel title = new JLabel("Progress");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(22));
        card.add(buildProgressRow("Academics", data.academicsProgress));
        card.add(Box.createVerticalStrut(18));
        card.add(buildProgressRow("Coding", data.codingProgress));
        card.add(Box.createVerticalStrut(18));
        card.add(buildProgressRow("Clubs", data.clubsProgress));
        return card;
    }

    private JComponent buildProgressRow(String label, int value) {
        JPanel row = new JPanel(new BorderLayout(18, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel nameLabel = new JLabel(label);
        nameLabel.setPreferredSize(new Dimension(160, 32));
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        row.add(nameLabel, BorderLayout.WEST);
        row.add(new ProgressBarPanel(value), BorderLayout.CENTER);
        return row;
    }

    private JComponent buildMonthlyChartCard() {
        RoundedPanel card = new RoundedPanel(28, new Color(255, 255, 255), new Color(0, 0, 0, 14), 0, 0);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(22, 24, 20, 24));

        JLabel title = new JLabel("Progress This Month");
        title.setFont(new Font("SansSerif", Font.PLAIN, 21));
        card.add(title, BorderLayout.NORTH);
        card.add(new MonthlyChartPanel(data.monthlyProgress), BorderLayout.CENTER);
        return card;
    }

    private void refreshDashboard(JButton button) {
        button.setEnabled(false);
        button.setText("Refreshing...");

        SwingWorker<DashboardData, Void> worker = new SwingWorker<DashboardData, Void>() {
            @Override
            protected DashboardData doInBackground() {
                return DashboardService.loadDashboard(user);
            }

            @Override
            protected void done() {
                button.setEnabled(true);
                button.setText("Refresh data");
                try {
                    data = get();
                    setContentPane(buildContent());
                    revalidate();
                    repaint();
                } catch (Exception ignored) {
                    JOptionPane.showMessageDialog(
                            DashboardFrame.this,
                            "Could not refresh dashboard from Supabase.",
                            "Refresh failed",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        };

        worker.execute();
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint paint = new GradientPaint(
                    0, 0, new Color(255, 238, 170),
                    getWidth(), getHeight(), new Color(255, 220, 104)
            );
            g2.setPaint(paint);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(255, 255, 255, 75));
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

    private static class BrandIcon extends JComponent {
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(36, 36);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(31, 31, 36));
            g2.fillOval(0, 0, 36, 36);
            g2.setColor(new Color(252, 212, 75));
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(18, 25, 18, 10);
            g2.drawLine(18, 10, 11, 17);
            g2.drawLine(18, 10, 25, 17);
            g2.dispose();
        }
    }

    private static class AvatarPanel extends JComponent {
        private final String initials;

        private AvatarPanel(String name) {
            this.initials = buildInitials(name);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(82, 82);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(248, 210, 79));
            g2.fill(new Ellipse2D.Double(0, 0, 82, 82));
            g2.setColor(new Color(34, 34, 34));
            g2.setFont(new Font("SansSerif", Font.BOLD, 28));
            FontMetrics metrics = g2.getFontMetrics();
            int textWidth = metrics.stringWidth(initials);
            int x = (82 - textWidth) / 2;
            int y = 44 + (metrics.getAscent() / 2) - 4;
            g2.drawString(initials, x, y);
            g2.dispose();
        }

        private static String buildInitials(String name) {
            if (name == null || name.isBlank()) {
                return "S";
            }
            String[] parts = name.trim().split("\\s+");
            if (parts.length == 1) {
                return parts[0].substring(0, 1).toUpperCase();
            }
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
        }
    }

    private static class ProgressBarPanel extends JComponent {
        private final int progress;

        private ProgressBarPanel(int progress) {
            this.progress = Math.max(0, Math.min(100, progress));
            setPreferredSize(new Dimension(320, 18));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(243, 243, 243));
            g2.fillRoundRect(0, 2, getWidth(), 12, 12, 12);
            g2.setColor(new Color(251, 214, 69));
            int width = (int) (getWidth() * (progress / 100.0));
            g2.fillRoundRect(0, 2, width, 12, 12, 12);
            g2.dispose();
        }
    }

    private static class MonthlyChartPanel extends JComponent {
        private final List<Integer> values;

        private MonthlyChartPanel(List<Integer> values) {
            this.values = values;
            setPreferredSize(new Dimension(280, 180));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int padding = 12;
            int width = getWidth() - padding * 2;
            int height = getHeight() - padding * 2 - 24;
            int originY = padding + height;

            g2.setColor(new Color(235, 235, 235));
            g2.drawLine(padding, originY, padding + width, originY);

            if (values == null || values.size() < 2) {
                g2.dispose();
                return;
            }

            int max = 0;
            for (Integer value : values) {
                if (value != null) {
                    max = Math.max(max, value);
                }
            }
            max = Math.max(max, 40);

            Path2D path = new Path2D.Double();
            for (int index = 0; index < values.size(); index++) {
                double x = padding + (index * (width / (double) (values.size() - 1)));
                double normalized = values.get(index) / (double) max;
                double y = padding + (height - normalized * height);
                if (index == 0) {
                    path.moveTo(x, y);
                } else {
                    double previousX = padding + ((index - 1) * (width / (double) (values.size() - 1)));
                    double controlX = (previousX + x) / 2;
                    double previousNormalized = values.get(index - 1) / (double) max;
                    double previousY = padding + (height - previousNormalized * height);
                    path.curveTo(controlX, previousY, controlX, y, x, y);
                }
            }

            g2.setColor(new Color(248, 206, 44));
            g2.setStroke(new BasicStroke(3.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(path);
            g2.dispose();
        }
    }
}
