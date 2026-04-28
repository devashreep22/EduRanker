package ui;

import model.DashboardData;
import model.User;
import service.DashboardService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardFrame extends JFrame {
    private final User user;
    private DashboardData data;
    private String currentView = "Dashboard";

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
        sidebar.add(buildMenuItemButton("Dashboard", "Dashboard".equals(currentView)));
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(buildMenuItemButton("Progress / Projects", "Progress / Projects".equals(currentView)));
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(buildMenuItemButton("Reports", "Reports".equals(currentView)));
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(buildMenuItemButton("AI Mentor", "AI Mentor".equals(currentView)));
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(buildMenuItemButton("Settings", "Settings".equals(currentView)));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(buildRefreshButton());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 0, 0, 12));
        wrapper.add(sidebar, BorderLayout.CENTER);
        return wrapper;
    }

    private JComponent buildMenuItemButton(String label, boolean active) {
        RoundedPanel panel = new RoundedPanel(18,
                active ? new Color(255, 250, 235) : new Color(255, 0, 0, 0),
                new Color(0, 0, 0, active ? 18 : 0), 0, 0);
        panel.setLayout(new BorderLayout());
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        panel.setPreferredSize(new Dimension(210, 62));
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel item = new JLabel(label);
        item.setBorder(new EmptyBorder(0, 18, 0, 12));
        item.setFont(new Font("SansSerif", active ? Font.BOLD : Font.PLAIN, 18));
        item.setForeground(new Color(26, 26, 26));
        panel.add(item, BorderLayout.CENTER);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                switchView(label);
            }
        });

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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(buildCurrentView(), BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildCurrentView() {
        if ("Progress / Projects".equals(currentView)) {
            return wrapMainContent(new StudentProgressPanel(user, () -> switchView("Dashboard")));
        }
        if ("Reports".equals(currentView)) {
            return wrapMainContent(new StudentReportPanel());
        }
        if ("AI Mentor".equals(currentView)) {
            return wrapMainContent(buildPlaceholderView("AI Mentor", "AI mentor tools are next in line. Use the dashboard insights and reports while this section is being connected."));
        }
        if ("Settings".equals(currentView)) {
            return wrapMainContent(buildPlaceholderView("Settings", "Profile and app settings will appear here once the next dashboard settings flow is added."));
        }
        return wrapMainContent(new ModernDashboardPanel(data, user));
    }

    private JComponent wrapMainContent(JComponent content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(28, 26, 28, 28));
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildPlaceholderView(String titleText, String message) {
        RoundedPanel card = new RoundedPanel(28, new Color(255, 255, 255), new Color(0, 0, 0, 14), 0, 0);
        card.setLayout(new BorderLayout(0, 18));
        card.setBorder(new EmptyBorder(28, 28, 28, 28));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));

        JTextArea body = new JTextArea(message);
        body.setEditable(false);
        body.setOpaque(false);
        body.setLineWrap(true);
        body.setWrapStyleWord(true);
        body.setFont(new Font("SansSerif", Font.PLAIN, 16));
        body.setForeground(new Color(75, 75, 75));

        card.add(title, BorderLayout.NORTH);
        card.add(body, BorderLayout.CENTER);
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

    private void switchView(String viewName) {
        if (viewName.equals(currentView)) {
            return;
        }

        currentView = viewName;
        setContentPane(buildContent());
        revalidate();
        repaint();
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
}
