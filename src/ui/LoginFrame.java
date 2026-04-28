package ui;

import model.DashboardData;
import model.User;
import service.AuthService;
import service.DashboardService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final JTextField prnField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JLabel statusLabel;

    public LoginFrame() {
        setTitle("EduRanker Login");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(520, 700);
        setLocationRelativeTo(null);
        JPanel root = createBackgroundPanel();
        root.setLayout(new GridBagLayout());

        JPanel form = new RoundedPanel(34, new Color(255, 251, 239));
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(34, 34, 34, 34));

        JLabel brand = new JLabel("EduRanker");
        brand.setFont(new Font("SansSerif", Font.BOLD, 28));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to open your AI-powered dashboard");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitle.setForeground(new Color(90, 84, 68));
        subtitle.setBorder(new EmptyBorder(6, 0, 24, 0));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel prnLabel = buildFieldLabel("PRN");
        prnField = buildTextField();
        prnField.setText("student01");

        JLabel passwordLabel = buildFieldLabel("Password");
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(231, 215, 170), 1),
                new EmptyBorder(12, 14, 12, 14)
        ));

        loginButton = new JButton("Open Dashboard");
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setBackground(new Color(33, 33, 37));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(new EmptyBorder(14, 18, 14, 18));
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.addActionListener(event -> login());

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(new Color(170, 85, 35));
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(brand);
        form.add(subtitle);
        form.add(prnLabel);
        form.add(Box.createVerticalStrut(8));
        form.add(prnField);
        form.add(Box.createVerticalStrut(18));
        form.add(passwordLabel);
        form.add(Box.createVerticalStrut(8));
        form.add(passwordField);
        form.add(Box.createVerticalStrut(24));
        form.add(loginButton);
        form.add(Box.createVerticalStrut(12));
        form.add(statusLabel);

        root.add(form);
        setContentPane(root);
    }

    private JPanel createBackgroundPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint paint = new GradientPaint(
                        0, 0, new Color(255, 236, 170),
                        getWidth(), getHeight(), new Color(255, 214, 78)
                );
                g2.setPaint(paint);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 65));
                g2.fillOval(-80, 50, 320, 320);
                g2.fillOval(260, 420, 240, 240);
                g2.dispose();
            }
        };
    }

    private JLabel buildFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(new Color(52, 50, 44));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField buildTextField() {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(231, 215, 170), 1),
                new EmptyBorder(12, 14, 12, 14)
        ));
        return field;
    }

    private void login() {
        String prn = prnField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (prn.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Enter both PRN and password.");
            return;
        }

        loginButton.setEnabled(false);
        statusLabel.setText("Connecting to Supabase...");

        SwingWorker<Object[], Void> worker = new SwingWorker<Object[], Void>() {
            @Override
            protected Object[] doInBackground() {
                User user = AuthService.login(prn, password);
                if (user == null) {
                    return new Object[]{null, null};
                }
                DashboardData data = null;
                if (!isTeacher(user)) {
                    data = DashboardService.loadDashboard(user);
                }
                return new Object[]{user, data};
            }

            @Override
            protected void done() {
                loginButton.setEnabled(true);
                try {
                    Object[] result = get();
                    User user = (User) result[0];
                    DashboardData data = (DashboardData) result[1];
                    if (user == null) {
                        statusLabel.setText("Login failed. Check PRN, password, or table columns.");
                        return;
                    }
                    openDashboard(user, data);
                    dispose();
                } catch (Exception exception) {
                    statusLabel.setText("Unable to load dashboard: " + exception.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void openDashboard(User user, DashboardData data) {
        JFrame frame;
        if (isTeacher(user)) {
            frame = new TeacherDashboardFrame(user.prn);
        } else {
            frame = new DashboardFrame(user, data);
        }
        frame.setVisible(true);
    }

    private boolean isTeacher(User user) {
        return user != null && user.role != null && user.role.equalsIgnoreCase("teacher");
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color fillColor;

        private RoundedPanel(int radius, Color fillColor) {
            this.radius = radius;
            this.fillColor = fillColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 18));
            g2.fillRoundRect(8, 10, getWidth() - 16, getHeight() - 12, radius, radius);
            g2.setColor(fillColor);
            g2.fillRoundRect(0, 0, getWidth() - 16, getHeight() - 16, radius, radius);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }
}
