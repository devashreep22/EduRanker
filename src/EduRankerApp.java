import ui.LoginFrame;

import javax.swing.*;

public class EduRankerApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
