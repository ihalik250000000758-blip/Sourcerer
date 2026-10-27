package sourcerer.ui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.JPanel;

/**
 * Base class for every screen: a vertical gradient (or a backdrop picture), drifting dust motes and a
 * dark vignette. The finished background is cached, so animated sprites on top stay cheap to repaint.
 * Call setBackdrop(...) to put a picture behind the screen (RpgScreen does this with the level's biome).
 */
public class ScreenPanel extends JPanel {

    private final Color top;
    private final Color bottom;
    private final float[] sx = new float[60];
    private final float[] sy = new float[60];
    private final float[] ss = new float[60];
    private Biomes.Backdrop backdrop;
    private BufferedImage cache;

    public ScreenPanel(Color top, Color bottom) {
        this.top = top;
        this.bottom = bottom;
        Random r = new Random(7);
        for (int i = 0; i < sx.length; i++) {
            sx[i] = r.nextFloat();
            sy[i] = r.nextFloat();
            ss[i] = 2 + r.nextFloat() * 5;
        }
        setOpaque(true);
    }

    /** Puts a picture behind this screen (null goes back to the plain gradient). */
    public void setBackdrop(Biomes.Backdrop backdrop) {
        this.backdrop = backdrop;
        cache = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }
        if (cache == null || cache.getWidth() != w || cache.getHeight() != h) {
            cache = buildBackground(w, h);
        }
        g.drawImage(cache, 0, 0, null);
    }

    private BufferedImage buildBackground(int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0, 0, top, 0, h, bottom));
        g2.fillRect(0, 0, w, h);

        if (backdrop != null) {
            backdrop.paint(g2, w, h);
            // scrim: keeps the UI readable whatever the art looks like
            g2.setPaint(new GradientPaint(0, 0, new Color(6, 4, 14, 25), 0, h, new Color(6, 4, 14, 150)));
            g2.fillRect(0, 0, w, h);
        }

        for (int i = 0; i < sx.length; i++) {
            float x = sx[i] * w;
            float y = sy[i] * h;
            float s = ss[i] * 0.8f;
            g2.setColor(new Color(214, 190, 255, 28 + (int) (ss[i] * 8)));
            Path2D star = new Path2D.Float();
            star.moveTo(x, y - s);
            star.quadTo(x, y, x + s, y);
            star.quadTo(x, y, x, y + s);
            star.quadTo(x, y, x - s, y);
            star.quadTo(x, y, x, y - s);
            g2.fill(star);
        }

        // vignette
        float radius = (float) Math.hypot(w, h) * 0.62f;
        g2.setPaint(new RadialGradientPaint(w / 2f, h / 2f, radius, new float[]{0.55f, 1f},
                new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 150)}));
        g2.fillRect(0, 0, w, h);
        g2.dispose();
        return img;
    }
}
