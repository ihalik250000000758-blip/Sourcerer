package sourcerer.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * The 7 level biomes. One per level, in the same order as LevelBank.
 *
 * =====================  HOW TO SWAP IN YOUR REAL BIOME ART  =====================
 *  Drop one PNG per level into  assets/biomes/  using the exact names in the BIOMES table below:
 *      level1.png ... level7.png
 *  Recommended size: 1600 x 900 (16:9). Anything works - the image is scaled to cover the whole
 *  screen and centre-cropped, then darkened slightly so the UI on top stays readable.
 *  The same PNG is also cropped into the round level node on the map.
 *
 *  Until a PNG exists, a generated placeholder scene (gradient sky + silhouettes) is drawn instead.
 *  Set SHOW_PLACEHOLDER_LABEL to false to hide the small "PLACEHOLDER" caption on those scenes.
 *
 *  Optional extras (also in assets/biomes/): world_map.png (behind the level map), menu_bg.png (main menu).
 * ================================================================================
 */
public final class Biomes {

    /** Paints something behind a screen's widgets. */
    public interface Backdrop {
        void paint(Graphics2D g, int w, int h);
    }

    /** Set to false once you no longer want the placeholder caption in the corner. */
    public static final boolean SHOW_PLACEHOLDER_LABEL = true;

    private static final class Biome {
        final String name;
        final String file;
        final Color top;
        final Color bottom;
        final Color accent;
        final int style;

        Biome(String name, String file, int top, int bottom, int accent, int style) {
            this.name = name;
            this.file = file;
            this.top = new Color(top);
            this.bottom = new Color(bottom);
            this.accent = new Color(accent);
            this.style = style;
        }
    }

    // ---- BIOME TABLE: index = level id - 1.  REPLACE the PNG named in the 2nd column with your art. ----
    private static final Biome[] BIOMES = {
            // Level 1  Variables      - a dusk village at the edge of the wild
            new Biome("Hollow Village", "level1.png", 0x2B2250, 0x0E0B1C, 0xE8A04A, 0),
            // Level 2  Data Types     - dark woods, fireflies between the trunks
            new Biome("Gloomwood", "level2.png", 0x10303A, 0x050E12, 0x6FE0B0, 1),
            // Level 3  Input & Output - a moonlit crossroads with a lone signpost
            new Biome("Moonlit Crossroads", "level3.png", 0x1D2A5C, 0x080C1F, 0xBFD6FF, 2),
            // Level 4  Conditionals   - broken ruins, a fork in the path
            new Biome("Forked Ruins", "level4.png", 0x3A2036, 0x120A14, 0xE0675A, 3),
            // Level 5  Loops          - a mist-covered marsh that circles back on itself
            new Biome("Ouroboros Marsh", "level5.png", 0x1F3A2E, 0x08140F, 0x9BE86A, 4),
            // Level 6  Lists          - candle-lit catacomb archive, shelf after shelf
            new Biome("Archive Catacombs", "level6.png", 0x2E2440, 0x0C0813, 0xC9A7FF, 5),
            // Level 7  Functions      - the Spellforge citadel, towers lit from within
            new Biome("Spellforge Citadel", "level7.png", 0x3B1D3F, 0x0F0714, 0xFFB347, 6),
    };

    private static final Map<String, BufferedImage> RENDERED = new HashMap<>();

    private Biomes() {
    }

    private static Biome of(int levelId) {
        int i = Math.max(1, Math.min(BIOMES.length, levelId)) - 1; // tutorial (id 0) uses biome 1
        return BIOMES[i];
    }

    public static String name(int levelId) {
        return of(levelId).name;
    }

    public static Color accent(int levelId) {
        return of(levelId).accent;
    }

    /** Full-screen backdrop for a level: your PNG if present, otherwise the generated placeholder. */
    public static Backdrop backdrop(int levelId) {
        return (g, w, h) -> g.drawImage(render(levelId, w, h, true), 0, 0, null);
    }

    /** Backdrop from assets/biomes/<file>, or null (caller keeps its gradient) when the file is absent. */
    public static Backdrop optional(String file) {
        if (Assets.image("biomes/" + file) == null) {
            return null;
        }
        return (g, w, h) -> {
            BufferedImage img = Assets.image("biomes/" + file);
            if (img != null) {
                drawCover(g, img, w, h);
            }
        };
    }

    /** A w x h picture of the level's biome (used inside the round nodes on the level map). */
    public static BufferedImage thumbnail(int levelId, int w, int h) {
        return render(levelId, w, h, false);
    }

    // ------------------------------------------------------------------ rendering

    private static BufferedImage render(int levelId, int w, int h, boolean label) {
        w = Math.max(1, w);
        h = Math.max(1, h);
        String key = Math.max(1, levelId) + ":" + w + "x" + h + (label ? "L" : "");
        BufferedImage out = RENDERED.get(key);
        if (out == null) {
            if (RENDERED.size() > 40) {
                RENDERED.clear(); // window was resized a lot
            }
            out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = out.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Biome b = of(levelId);
            BufferedImage art = Assets.image("biomes/" + b.file);
            if (art != null) {
                drawCover(g, art, w, h);
            } else {
                paintPlaceholder(g, b, Math.max(1, levelId), w, h, label);
            }
            g.dispose();
            RENDERED.put(key, out);
        }
        return out;
    }

    /** Scales img to cover w x h (centre-cropped). Upscaled pixel art stays crisp. */
    private static void drawCover(Graphics2D g, BufferedImage img, int w, int h) {
        double s = Math.max(w / (double) img.getWidth(), h / (double) img.getHeight());
        int dw = (int) Math.ceil(img.getWidth() * s);
        int dh = (int) Math.ceil(img.getHeight() * s);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, s >= 2
                ? RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
                : RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(img, (w - dw) / 2, (h - dh) / 2, dw, dh, null);
        g2.dispose();
    }

    // ---- PLACEHOLDER SCENES (delete this whole section once all 7 PNGs exist) ----

    private static void paintPlaceholder(Graphics2D g, Biome b, int id, int w, int h, boolean label) {
        Random r = new Random(id * 7919L);
        g.setPaint(new GradientPaint(0, 0, b.top, 0, h, b.bottom));
        g.fillRect(0, 0, w, h);

        // moon / glow
        double mx = w * (0.2 + 0.6 * r.nextDouble());
        double my = h * (0.14 + 0.16 * r.nextDouble());
        double mr = Math.min(w, h) * 0.07;
        g.setColor(alpha(b.accent, 40));
        g.fill(new Ellipse2D.Double(mx - mr * 2.4, my - mr * 2.4, mr * 4.8, mr * 4.8));
        g.setColor(alpha(b.accent, 150));
        g.fill(new Ellipse2D.Double(mx - mr, my - mr, mr * 2, mr * 2));

        // three silhouette layers, far to near
        for (int layer = 0; layer < 3; layer++) {
            Color c = mix(b.bottom, b.top, 0.55 - layer * 0.22);
            int base = (int) (h * (0.30 + layer * 0.075));
            drawLayer(g, b.style, layer, w, h, base, c, b.accent, r);
        }

        // ground fog
        g.setPaint(new GradientPaint(0, h * 0.7f, alpha(b.accent, 0), 0, h, alpha(b.accent, 55)));
        g.fillRect(0, (int) (h * 0.7), w, h);

        if (label && SHOW_PLACEHOLDER_LABEL) {
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            FontMetrics fm = g.getFontMetrics();
            String s = "PLACEHOLDER BIOME " + id + " - " + b.name + "  |  add assets/biomes/" + b.file;
            g.setColor(new Color(255, 255, 255, 70));
            g.drawString(s, w - fm.stringWidth(s) - 14, h - 10);
        }
    }

    private static void drawLayer(Graphics2D g, int style, int layer, int w, int h, int base,
                                  Color c, Color glow, Random r) {
        g.setColor(c);
        int step = 70 + layer * 40;
        switch (style) {
            case 0: // village roofs
                for (int x = -20; x < w; x += step + r.nextInt(40)) {
                    int bw = 40 + r.nextInt(40) + layer * 20;
                    int bh = 30 + r.nextInt(50) + layer * 20;
                    g.fillRect(x, base - bh, bw, bh + h);
                    g.fillPolygon(new Polygon(new int[]{x - 6, x + bw / 2, x + bw + 6},
                            new int[]{base - bh, base - bh - 26 - layer * 6, base - bh}, 3));
                    if (layer == 2 && r.nextInt(3) > 0) {
                        g.setColor(alpha(glow, 200));
                        g.fillRect(x + bw / 3, base - bh + 14, 8, 10);
                        g.setColor(c);
                    }
                }
                break;
            case 1: // pines
                for (int x = -20; x < w; x += step / 2 + r.nextInt(30)) {
                    int th = 80 + r.nextInt(80) + layer * 50;
                    int tw = 34 + layer * 14;
                    g.fillPolygon(new Polygon(new int[]{x, x + tw / 2, x + tw},
                            new int[]{base, base - th, base}, 3));
                    g.fillRect(x + tw / 2 - 3, base, 6, h);
                }
                g.fillRect(0, base, w, h);
                break;
            case 2: // rolling hills + signpost
                int[] xs = new int[w / 8 + 3];
                int[] ys = new int[xs.length];
                double ph = r.nextDouble() * 6;
                for (int i = 0; i < xs.length; i++) {
                    xs[i] = i * 8;
                    ys[i] = base - (int) (Math.sin(i * 0.12 + ph + layer) * 22 + Math.sin(i * 0.05 + layer) * 30);
                }
                xs[xs.length - 1] = w;
                int[] px = java.util.Arrays.copyOf(xs, xs.length + 2);
                int[] py = java.util.Arrays.copyOf(ys, ys.length + 2);
                px[xs.length] = w;
                py[xs.length] = h;
                px[xs.length + 1] = 0;
                py[xs.length + 1] = h;
                g.fillPolygon(px, py, px.length);
                if (layer == 2) {
                    g.fillRect(w / 2 - 3, base - 70, 6, 80);
                    g.fillRect(w / 2 - 30, base - 62, 60, 14);
                }
                break;
            case 3: // broken pillars
                for (int x = 10; x < w; x += step + r.nextInt(50)) {
                    int ph2 = 60 + r.nextInt(130) + layer * 30;
                    int pw = 22 + layer * 10;
                    g.fillPolygon(new Polygon(new int[]{x, x + pw, x + pw, x + pw / 2, x},
                            new int[]{base - ph2 + 14, base - ph2, base + 10, base + 10, base + 10}, 5));
                    g.fillRect(x, base, pw, h);
                }
                g.fillRect(0, base + 8, w, h);
                break;
            case 4: // reeds over still water
                g.fillRect(0, base, w, h);
                for (int x = 0; x < w; x += 9 + r.nextInt(14)) {
                    int rh = 20 + r.nextInt(60) + layer * 20;
                    g.fillRect(x, base - rh, 3, rh);
                    g.fillOval(x - 2, base - rh - 8, 7, 14);
                }
                break;
            case 5: // arches
                for (int x = -10; x < w; x += 110 + layer * 50) {
                    int aw = 80 + layer * 30;
                    int ah = 110 + layer * 40;
                    g.fillRect(x, base - ah, aw, ah + h);
                    g.setColor(alpha(glow, 28 + layer * 14));
                    g.fillRoundRect(x + 14, base - ah + 22, aw - 28, ah, aw, aw);
                    g.setColor(c);
                }
                break;
            default: // citadel towers
                for (int x = 0; x < w; x += step + r.nextInt(50)) {
                    int tw = 36 + layer * 14;
                    int th = 100 + r.nextInt(150) + layer * 40;
                    g.fillRect(x, base - th, tw, th + h);
                    for (int cx = x; cx < x + tw - 6; cx += 12) {
                        g.fillRect(cx, base - th - 8, 7, 9);
                    }
                    if (layer > 0) {
                        g.setColor(alpha(glow, 190));
                        g.fillRect(x + tw / 2 - 3, base - th + 24, 6, 12);
                        g.setColor(c);
                    }
                }
                break;
        }
        g.fillRect(0, base + 40, w, h);
    }

    private static Color alpha(Color c, int a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, a)));
    }

    private static Color mix(Color a, Color b, double t) {
        t = Math.max(0, Math.min(1, t));
        return new Color((int) (a.getRed() + (b.getRed() - a.getRed()) * t),
                (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }
}
