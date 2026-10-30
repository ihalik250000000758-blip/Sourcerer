package sourcerer.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * A framed panel used for every card in the game.
 * The frame style follows the size passed in as "arc" (so existing code did not need to change):
 *   arc >= 26  ornate pixel frame (assets/ui/frame_ornate.png)  - big "hero" panels
 *   arc >= 20  thin line frame, tinted with the border colour   (assets/ui/frame_thin.png)
 *   smaller    plain rounded outline
 * If the frame PNGs are missing it falls back to a rounded outline, so nothing breaks.
 */
public class RoundPanel extends JPanel {

    private static final int ORNATE_INSET = 32;
    private static final int THIN_INSET = 12;
    private static final Map<Color, BufferedImage> TINTED = new HashMap<>();

    private final Color fill;
    private final Color border;
    private final int arc;

    public RoundPanel(Color fill, Color border, int arc) {
        this.fill = fill;
        this.border = border;
        this.arc = arc;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        int r = Math.min(arc, 14);
        g2.setColor(fill);
        g2.fillRoundRect(2, 2, w - 5, h - 5, r, r);

        boolean framed = false;
        if (arc >= 26) {
            BufferedImage ornate = Assets.image("ui/frame_ornate.png");
            framed = ornate != null && Assets.nineSlice(g2, ornate, 0, 0, w, h, ORNATE_INSET, 1);
        }
        if (!framed && arc >= 20) {
            BufferedImage thin = Assets.image("ui/frame_thin.png");
            if (thin != null) {
                BufferedImage tinted = TINTED.computeIfAbsent(border, c -> Assets.tint(thin, c));
                framed = Assets.nineSlice(g2, tinted, 0, 0, w, h, THIN_INSET, 1);
            }
        }
        if (!framed) {
            g2.setColor(border);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, w - 3, h - 3, r, r);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
