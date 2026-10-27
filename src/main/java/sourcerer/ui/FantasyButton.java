package sourcerer.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import javax.swing.ButtonModel;
import javax.swing.JButton;

/**
 * Menu button drawn from the pixel-art sprite sheet assets/ui/button.png
 * (4 states side by side, 96x24 each: normal, hover, pressed, disabled).
 * The pointed end caps keep their shape and the middle stretches to any width.
 * Falls back to a flat hexagon if the PNG is missing.
 */
public class FantasyButton extends JButton {

    private static final int FRAME_W = 96;
    private static final int FRAME_H = 24;
    private static final int CAP = 19;

    public FantasyButton(String text) {
        this(text, 280, 56);
    }

    public FantasyButton(String text, int width, int height) {
        super(text);
        setPreferredSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setFont(Theme.sans(java.awt.Font.BOLD, height >= 50 ? 22 : 17));        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth();
        // Layouts such as BorderLayout.EAST stretch buttons; keep the art at its intended height, centred.
        int h = Math.min(getHeight(), getPreferredSize().height);
        g2.translate(0, (getHeight() - h) / 2);
        ButtonModel m = getModel();
        int state = !isEnabled() ? 3 : m.isPressed() ? 2 : m.isRollover() ? 1 : 0;
        Color text = state == 3 ? new Color(0x7C74A0)
                : state == 1 ? Color.WHITE
                : state == 2 ? new Color(0xB9AEE0) : new Color(0xE4DCFA);

        BufferedImage sheet = Assets.image("ui/button.png");
        int pressOffset = state == 2 ? 1 : 0;
        if (sheet != null) {
            Assets.threeSlice(g2, sheet, state * FRAME_W, 0, FRAME_W, FRAME_H, CAP, CAP,
                    0, pressOffset, w, h - 2);
        } else {
            paintFallback(g2, w, h, state);
        }

        java.awt.Font font = getFont();
        FontMetrics fm = g2.getFontMetrics(font);
        int maxText = w - 2 * (int) (CAP * h / (double) FRAME_H) - 4;
        maxText = Math.max(maxText, w / 2);
        while (fm.stringWidth(getText()) > maxText && font.getSize() > 10) {
            font = font.deriveFont((float) (font.getSize() - 1));
            fm = g2.getFontMetrics(font);
        }
        g2.setFont(font);
        int tx = (w - fm.stringWidth(getText())) / 2;
        int ty = (h - 2 - fm.getHeight()) / 2 + fm.getAscent() + pressOffset;
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(getText(), tx + 1, ty + 2);
        g2.setColor(text);
        g2.drawString(getText(), tx, ty);
        g2.dispose();
    }

    private void paintFallback(Graphics2D g2, int w, int h, int state) {
        int c = Math.min(16, h / 3);
        Polygon shape = new Polygon(
                new int[]{c, w - c - 2, w - 2, w - c - 2, c, 1},
                new int[]{1, 1, h / 2, h - 2, h - 2, h / 2}, 6);
        Color[] top = {new Color(0x3A2C70), new Color(0x4E3E96), new Color(0x2A2050), new Color(0x1C1838)};
        g2.setPaint(new GradientPaint(0, 0, top[state], 0, h, top[state].darker()));
        g2.fillPolygon(shape);
        g2.setColor(state == 1 ? Theme.TEAL : Theme.VIOLET);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawPolygon(shape);
    }
}
