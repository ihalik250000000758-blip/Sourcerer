package sourcerer.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import sourcerer.model.Tile;

public class TileButton extends JButton {

    private final Tile tile;
    private boolean used;
    private int order;

    public TileButton(Tile tile) {
        super(tile.getSymbol());
        this.tile = tile;
        setToolTipText(tile.getTooltip());
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Force a minimum height so layouts can't squish the text
        setMinimumSize(new Dimension(50, 60));
    }

    public boolean isUsed() { return used; }
    public Tile getTile() { return tile; }

    public void setUsed(boolean used, int order) {
        this.used = used;
        this.order = order;
        setEnabled(!used);
        repaint();
    }

    private Color textColor() {
        switch (tile.getCategory()) {
            case "DataType": return new Color(0x66FF66);
            case "Operator": return new Color(0xFF88FF);
            case "Syntax":   return new Color(0x66CCFF);
            case "Keyword":  return new Color(0xFFAA55);
            case "Function": return new Color(0x55FFFF);
            default:         return Color.WHITE;
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

        // 1. Draw dark background
        g2.setColor(new Color(0x1C1738));
        g2.fillRect(0, 0, w, h);

        // 2. Draw board image
        BufferedImage board = Assets.image("ui/board_tile.png");
        if (board != null) {
            Assets.threeSlice(g2, board, 0, 0, board.getWidth(), board.getHeight(), 16, 16, 0, 0, w, h);
        } else {
            g2.setStroke(new BasicStroke(hover ? 3.5f : 2.5f));
            g2.setColor(hover ? Theme.TEAL : new Color(0x4A3A7A));
            g2.drawRect(1, 1, w - 2, h - 2);
        }

        // 3. Draw Text (Bulletproof Centering)
        int size = 22; 
        Font f = Theme.mono(Font.BOLD, size);
        FontMetrics fm = g2.getFontMetrics(f);
        
        while (fm.stringWidth(getText()) > w - 20 && size > 10) {
            size--;
            f = Theme.mono(Font.BOLD, size);
            fm = g2.getFontMetrics(f);
        }
        g2.setFont(f);

        String text = getText();
        FontRenderContext frc = g2.getFontRenderContext();
        Rectangle2D bounds = f.getStringBounds(text, frc);
        
        float tx = (float) ((w - bounds.getWidth()) / 2);
        float ty = (float) ((h - bounds.getHeight()) / 2 - bounds.getY());

        // Shadow
        g2.setColor(new Color(0, 0, 0, 200));
        g2.drawString(text, tx + 1, ty + 2);
        
        // Text
        g2.setColor(used ? new Color(0xAAAAAA) : textColor());
        g2.drawString(text, tx, ty);

        // Pick order badge
        if (used && order > 0) {
            g2.setColor(Theme.TEAL_DARK);
            g2.fillOval(w - 30, 4, 22, 22);
            g2.setColor(Color.WHITE);
            g2.setFont(Theme.sans(Font.BOLD, 12));
            String n = String.valueOf(order);
            g2.drawString(n, w - 19 - g2.getFontMetrics().stringWidth(n) / 2, 20);
        }
        g2.dispose();
    }
}