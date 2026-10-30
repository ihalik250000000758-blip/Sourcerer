package sourcerer.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Loads PNGs from the "assets" folder and draws them (nine-slice frames, three-slice bars).
 *
 * Lookup order for assets/ui/button.png:
 *   1. the classpath         ->  /assets/ui/button.png   (put the folder in your resources / src root)
 *   2. the working directory ->  ./assets/ui/button.png  (put the folder next to sourcerer_progress.properties)
 * A missing file is never an error: image() returns null and each caller falls back to its Java2D drawing.
 * That is how the placeholder characters and biomes keep working until your real PNGs are dropped in.
 */
public final class Assets {

    private static final Map<String, BufferedImage> CACHE = new HashMap<>();
    private static final BufferedImage MISSING = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    private Assets() {
    }

    /** @param path relative to the assets folder, e.g. "characters/hero.png". @return the image, or null if absent. */
    public static BufferedImage image(String path) {
        BufferedImage cached = CACHE.get(path);
        if (cached == null) {
            cached = load(path);
            CACHE.put(path, cached == null ? MISSING : cached);
        }
        return cached == MISSING ? null : cached;
    }

    /** Forget everything, so edited PNGs are re-read (handy while swapping art in). */
    public static void reload() {
        CACHE.clear();
    }

    private static BufferedImage load(String path) {
        try (InputStream in = Assets.class.getResourceAsStream("/assets/" + path)) {
            if (in != null) {
                return ImageIO.read(in);
            }
        } catch (Exception ignored) {
            // fall through to the file lookup
        }
        try {
            File f = new File("assets/" + path);
            return f.isFile() ? ImageIO.read(f) : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    /** Multiplies every pixel's colour by {@code c} (alpha kept). White art becomes {@code c}-coloured art. */
    public static BufferedImage tint(BufferedImage src, Color c) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int p = src.getRGB(x, y);
                int a = p >>> 24;
                int r = ((p >> 16) & 255) * c.getRed() / 255;
                int g = ((p >> 8) & 255) * c.getGreen() / 255;
                int b = (p & 255) * c.getBlue() / 255;
                out.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
            }
        }
        return out;
    }

    /** Nearest-neighbour copy of g so pixel art stays crisp. The caller disposes it. */
    public static Graphics2D crisp(Graphics2D g) {
        Graphics2D c = (Graphics2D) g.create();
        c.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        return c;
    }

    /**
     * Draws img stretched to (x,y,w,h). The four corners keep their size (inset source pixels, drawn at
     * scale), the edges and centre stretch. @return false if the area is too small for the corners.
     */
    public static boolean nineSlice(Graphics2D g, BufferedImage img, int x, int y, int w, int h,
                                    int inset, int scale) {
        int c = inset * scale;
        if (w < 2 * c || h < 2 * c) {
            return false;
        }
        int iw = img.getWidth();
        int ih = img.getHeight();
        Graphics2D g2 = crisp(g);
        int[] sx = {0, inset, iw - inset, iw};
        int[] sy = {0, inset, ih - inset, ih};
        int[] dx = {x, x + c, x + w - c, x + w};
        int[] dy = {y, y + c, y + h - c, y + h};
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (col == 1 && row == 1) {
                    continue; // frames have a transparent middle
                }
                g2.drawImage(img, dx[col], dy[row], dx[col + 1], dy[row + 1],
                        sx[col], sy[row], sx[col + 1], sy[row + 1], null);
            }
        }
        g2.dispose();
        return true;
    }

    /**
     * Draws a horizontal strip (src rectangle sx,sy,sw,sh) at (x,y,w,h): the left capL and right capR
     * source pixels keep their proportions, the middle stretches.
     */
    public static void threeSlice(Graphics2D g, BufferedImage img, int sx, int sy, int sw, int sh,
                                  int capL, int capR, int x, int y, int w, int h) {
        double k = h / (double) sh;
        int l = (int) Math.round(capL * k);
        int r = (int) Math.round(capR * k);
        if (w < l + r) {
            l = w / 2;
            r = w - l;
        }
        Graphics2D g2 = crisp(g);
        g2.drawImage(img, x, y, x + l, y + h, sx, sy, sx + capL, sy + sh, null);
        g2.drawImage(img, x + l, y, x + w - r, y + h, sx + capL, sy, sx + sw - capR, sy + sh, null);
        g2.drawImage(img, x + w - r, y, x + w, y + h, sx + sw - capR, sy, sx + sw, sy + sh, null);
        g2.dispose();
    }
}
