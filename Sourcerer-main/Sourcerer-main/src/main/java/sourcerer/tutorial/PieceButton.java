package sourcerer.tutorial;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import sourcerer.model.TileFactory;
import sourcerer.ui.Assets;
import sourcerer.ui.Theme;

class PieceButton extends JButton {

    private final boolean mono;
    private final Color ink;
    private boolean ghost;
    private boolean locked;

    PieceButton(String text) {
        this(text, false);
    }

    PieceButton(String text, boolean mono) {
        super(text);
        this.mono = mono;
        this.ink = mono ? Theme.INK : inkFor(text);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    void setGhost(boolean g) {
        ghost = g;
        setEnabled(!g);
        setSelected(false);
        repaint();
    }

    void setLocked(boolean l) {
        locked = l;
        repaint();
    }

    static Color inkFor(String token) {
        switch (TileFactory.of(token.trim()).getCategory()) {
            case "Operator":
                return new Color(0xFF88FF); // Bright Pink
            case "Syntax":
                return new Color(0x66CCFF); // Bright Blue
            case "Keyword":
                return new Color(0xFFAA55); // Orange
            case "Function":
                return new Color(0x55FFFF); // Cyan
            default:
                return Color.WHITE;         // Values/Names (White)
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        boolean hover = isEnabled() && getModel().isRollover();
        boolean pressed = isEnabled() && getModel().isPressed();
        boolean sel = isSelected();

        Color base = ghost ? new Color(0x8F8670) : Theme.PARCHMENT;
        if (hover) {
            base = new Color(0xF6EBD0);
        }
        if (pressed) {
            base = Theme.PARCHMENT_DARK;
        }
        if (sel) {
            base = new Color(0xFFF2B8);
        }
        g2.setColor(new Color(0, 0, 0, 90));
        BufferedImage board = Assets.image("ui/board_tile.png");

        // Drop Shadow
        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillRoundRect(3, 5, w - 6, h - 7, 18, 18);

        if (board != null) {
            Assets.threeSlice(g2, board, 0, 0, board.getWidth(), board.getHeight(), 16, 16, 1, 1, w - 6, h - 7);

            if (ghost) {
                g2.setColor(new Color(0, 0, 0, 100));
                g2.fillRoundRect(1, 1, w - 6, h - 7, 18, 18);
            } else if (sel) {
                g2.setColor(new Color(255, 230, 100, 80)); // Gold tint for selection
                g2.fillRoundRect(1, 1, w - 6, h - 7, 18, 18);
            } else if (hover) {
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(1, 1, w - 6, h - 7, 18, 18);
            }

            if (locked) {
                g2.setColor(Theme.GOOD);
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(1, 1, w - 6, h - 7, 18, 18);
            }
        } else {
            g2.setColor(base);
            g2.fillRoundRect(1, 1, w - 6, h - 7, 18, 18);
            Color border = sel ? Theme.XP_GOLD : locked ? Theme.GOOD : hover ? Theme.TEAL
                    : ghost ? new Color(0x5A5240) : new Color(0x4A3A7A);
            g2.setStroke(new BasicStroke(sel || hover ? 3.5f : 2.5f));
            g2.setColor(border);
            g2.drawRoundRect(1, 1, w - 6, h - 7, 18, 18);
        }

        int size = mono ? 20 : 26;
        Font f = mono ? Theme.mono(Font.BOLD, size) : Theme.sans(Font.BOLD, size);
        FontMetrics fm = g2.getFontMetrics(f);
        while (fm.stringWidth(getText()) > w - 28 && size > 10) {
            size--;
            f = mono ? Theme.mono(Font.BOLD, size) : Theme.sans(Font.BOLD, size);
            fm = g2.getFontMetrics(f);
        }
        g2.setFont(f);
        int tx = mono ? 16 : (w - 5 - fm.stringWidth(getText())) / 2;
        int ty = (h - 6 - fm.getHeight()) / 2 + fm.getAscent();

        // Draw a dark shadow first
        g2.setColor(new Color(0, 0, 0, 180));
        g2.drawString(getText(), tx + 1, ty + 2);

        // Draw the bright text on top
        g2.setColor(ghost ? new Color(0xAAAAAA) : ink);
        g2.drawString(getText(), tx, ty);
    }
}
