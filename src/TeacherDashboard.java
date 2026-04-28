import ui.TeacherDashboardFrame;

import javax.swing.*;

public class TeacherDashboard {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            String teacherPrn = args.length > 0 ? args[0] : null;
            TeacherDashboardFrame frame = new TeacherDashboardFrame(teacherPrn);
            frame.setVisible(true);
        });
    }
}
