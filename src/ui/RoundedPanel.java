package ui;

import javax.swing.*;
import java.awt.*;

/**
 * RoundedPanel - Custom JPanel with rounded corners and shadow effects
 */
public class RoundedPanel extends JPanel {
    private final int radius;
    private final Color fillColor;
    private final Color shadowColor;
    private final int shadowX;
    private final int shadowY;

    public RoundedPanel(int radius, Color fillColor, Color shadowColor, int shadowX, int shadowY) {
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
