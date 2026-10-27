package sourcerer.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 * Horizontal HP / MP / XP bar with a "HP 80/100" caption.
 * The frame is the skull-ended sprite assets/ui/bar_frame.png (95x17); the coloured fill is drawn
 * underneath it, inside the frame's hollow part. Falls back to a plain rounded bar if the PNG is missing.
 */
public class StatBar extends JComponent {

    // geometry of assets/ui/bar_frame.png, in source pixels
    private static final int SRC_W = 95;
    private static final int SRC_H = 17;
    private static final int CAP_LEFT = 8;
    private static final int CAP_RIGHT = 20;
    private static final int HOLE_TOP = 4;
    private static final int HOLE_H = 9;

    private final String label;
    private final Color fill;
    private int value = 0;
    private int max = 1;

    public StatBar(String label, Color fill) {
        this.label = label;
        this.fill = fill;
        setPreferredSize(new Dimension(220, 26));
        setMinimumSize(new Dimension(100, 22));
        setOpaque(false);
    }

    public void setValues(int value, int max) {
        this.value = Math.max(0, value);
        this.max = Math.max(1, max);
        repaint();
    }

    public int getValue() {
        return value;
    }

    public int getMax() {
        return max;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        BufferedImage frame = Assets.image("ui/bar_frame.png");
        int tx0;
        int tw;
        if (frame != null) {
            double k = h / (double) SRC_H;
            int hx = (int) Math.round(CAP_LEFT * k);
            int hy = (int) Math.round(HOLE_TOP * k);
            int hw = w - hx - (int) Math.round(CAP_RIGHT * k);
            int hh = (int) Math.round(HOLE_H * k);
            g2.setColor(new Color(0x0C0916));
            g2.fillRect(hx, hy, hw, hh);
            int fw = (int) (hw * (value / (double) max));
            if (fw > 0) {
                g2.setPaint(new GradientPaint(0, hy, fill.brighter(), 0, hy + hh, fill.darker()));
                g2.fillRect(hx, hy, fw, hh);
                g2.setColor(new Color(255, 255, 255, 60));
                g2.fillRect(hx, hy, fw, Math.max(1, hh / 5));
            }
            Assets.threeSlice(g2, frame, 0, 0, SRC_W, SRC_H, CAP_LEFT, CAP_RIGHT, 0, 0, w, h);
            tx0 = hx;
            tw = hw;
        } else {
            RoundRectangle2D track = new RoundRectangle2D.Float(1, 1, w - 3, h - 3, h, h);
            g2.setColor(new Color(0x120E24));
            g2.fill(track);
            int fw = (int) ((w - 4) * (value / (double) max));
            if (fw > 0) {
                g2.setClip(track);
                g2.setPaint(new GradientPaint(0, 0, fill.brighter(), 0, h, fill.darker()));
                g2.fillRect(2, 2, fw, h - 4);
                g2.setClip(null);
            }
            g2.setColor(Theme.PARCHMENT_DARK);
            g2.setStroke(new BasicStroke(2f));
            g2.draw(track);
            tx0 = 0;
            tw = w;
        }

        String text = label + " " + value + "/" + max;
        g2.setFont(Theme.sans(java.awt.Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        int tx = tx0 + (tw - fm.stringWidth(text)) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.setColor(new Color(0, 0, 0, 190));
        g2.drawString(text, tx + 1, ty + 1);
        g2.setColor(Color.WHITE);
        g2.drawString(text, tx, ty);
        g2.dispose();
    }
}
