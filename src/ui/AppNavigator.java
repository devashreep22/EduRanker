package ui;

import javax.swing.*;
import java.awt.*;

public final class AppNavigator {
    private AppNavigator() {
    }

    public static void openLoginWindow(Window currentWindow) {
        SwingUtilities.invokeLater(() -> {
            if (currentWindow != null) {
                currentWindow.dispose();
            }
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
