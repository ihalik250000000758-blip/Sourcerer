package sourcerer.ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.Timer;

public class SpriteComponent extends JComponent {

    /** Set to false because your art is smooth/HD rather than pixel art. */
    private static final boolean PIXEL_ART = false;

    public enum Kind {
        HERO("hero.png"), 
        LEVEL1_BAT("level1_bat_enemy.png"),         // grey bat
        LEVEL2_BLOBFISH("level2blobfish_enemy.png"), // blobfish
        LEVEL3_FOX("level3fox_enemy.png"),           // fox
        LEVEL4_SPIDER("level4Spider_enemy.png"),     // green leaf spider
        LEVEL5_TIGER("level5tiger_enemy.png"),       // tiger
        LEVEL6_BAT("level6_bat_enemy.png"),          // purple bat
        LEVEL7_SPIDER("level7spider_enemy.png");     // demon spider (boss)

        final String file;

        Kind(String file) {
            this.file = file;
        }

        /**
         * PRIMARY MAPPING: level number → sprite.
         * Used by RpgScreen so the sprite is ALWAYS correct per level,
         * even if Battle.java spawns the wrong bug class.
         * Level 0 is the tutorial — it uses the Level 1 bat.
         */
        public static Kind fromLevel(int levelId) {
            switch (levelId) {
                case 1:  return LEVEL1_BAT;
                case 2:  return LEVEL2_BLOBFISH;
                case 3:  return LEVEL3_FOX;
                case 4:  return LEVEL4_SPIDER;
                case 5:  return LEVEL5_TIGER;
                case 6:  return LEVEL6_BAT;
                case 7:  return LEVEL7_SPIDER;
                default: return LEVEL1_BAT;   // tutorial + unknown levels → bat
            }
        }

        /** Kept for any code that still passes a bug's sprite id string. */
        public static Kind fromId(String id) {
            if (id == null) return LEVEL1_BAT;
            switch (id.toLowerCase()) {
                case "level1": return LEVEL1_BAT;
                case "level2": return LEVEL2_BLOBFISH;
                case "level3": return LEVEL3_FOX;
                case "level4": return LEVEL4_SPIDER;
                case "level5": return LEVEL5_TIGER;
                case "level6": return LEVEL6_BAT;
                case "level7": return LEVEL7_SPIDER;
                default:       return LEVEL1_BAT;
            }
        }
    }

    private Kind kind;
    private double phase;
    private int hitTick = -1;
    private String popText = "";
    private Color popColor = Color.WHITE;
    private final Timer timer = new Timer(40, e -> tick());

    public SpriteComponent(Kind kind) {
        this.kind = kind;
        setOpaque(false);
        setPreferredSize(new Dimension(200, 200));
        setMinimumSize(new Dimension(120, 120));
    }

    public void setKind(Kind kind) {
        this.kind = kind;
        repaint();
    }

    public void hit(String text, Color color) {
        popText = text;
        popColor = color;
        hitTick = 0;
    }

    private void tick() {
        phase += 0.15;
        if (hitTick >= 0) {
            hitTick++;
            if (hitTick > 24) hitTick = -1;
        }
        repaint();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        timer.start();
    }

    @Override
    public void removeNotify() {
        timer.stop();
        super.removeNotify();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();

        double shake = (hitTick >= 0 && hitTick < 10) ? ((hitTick % 2 == 0) ? 9 : -9) * (1 - hitTick / 10.0) : 0;
        double bob = Math.sin(phase) * 4;

        g2.setColor(new Color(0, 0, 0, 70));
        g2.fill(new Ellipse2D.Double(w * 0.25, h * 0.88, w * 0.5, h * 0.07));

        Graphics2D s = (Graphics2D) g2.create();
        double sc = Math.min(w, h) / 200.0;
        s.translate(w / 2.0 + shake, h / 2.0 + bob);
        s.scale(sc, sc);
        s.translate(-100, -100);

        BufferedImage art = Assets.image("characters/" + kind.file);
        if (art != null) {
            drawArt(s, art);
        } else {
            switch (kind) {
                case HERO: drawHero(s); break;
                default:   drawSpider(s); break;
            }
        }
        s.dispose();

        if (hitTick >= 0) {
            float a = Math.max(0f, 1f - hitTick / 24f);
            g2.setComposite(AlphaComposite.SrcOver.derive(a));
            g2.setFont(Theme.serif(Font.BOLD, 30));
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(popText)) / 2;
            int ty = (int) (h * 0.3) - hitTick * 2;
            g2.setColor(Color.BLACK);
            g2.drawString(popText, tx + 2, ty + 2);
            g2.setColor(popColor);
            g2.drawString(popText, tx, ty);
        }
        g2.dispose();
    }

    private void drawArt(Graphics2D g, BufferedImage art) {
        double k = Math.min(190.0 / art.getWidth(), 190.0 / art.getHeight());
        int dw = (int) Math.round(art.getWidth() * k);
        int dh = (int) Math.round(art.getHeight() * k);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, PIXEL_ART && k >= 1
                ? RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
                : RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(art, 100 - dw / 2, 178 - dh, dw, dh, null);
    }

    // ---------- PLACEHOLDER ART (only shown if a PNG is missing) ----------

    private void drawHero(Graphics2D g) {
        for (int side = -1; side <= 1; side += 2) {
            Path2D wing = new Path2D.Float();
            wing.moveTo(100 + side * 30, 104);
            wing.curveTo(100 + side * 70, 90, 100 + side * 92, 50, 100 + side * 88, 36);
            wing.curveTo(100 + side * 100, 80, 100 + side * 96, 120, 100 + side * 70, 150);
            wing.curveTo(100 + side * 56, 140, 100 + side * 40, 132, 100 + side * 30, 124);
            wing.closePath();
            g.setColor(new Color(240, 168, 80, 200));
            g.fill(wing);
            g.setColor(new Color(150, 90, 30));
            g.setStroke(new BasicStroke(2f));
            g.draw(wing);
        }
        g.setColor(new Color(0x3B4EA8));
        g.fillPolygon(new Polygon(new int[]{70, 130, 152, 48}, new int[]{98, 98, 176, 176}, 4));
        g.setColor(new Color(0xFFF3C4));
        g.fill(new Ellipse2D.Double(68, 52, 64, 58));
        g.setColor(Color.BLACK);
        g.fill(new Ellipse2D.Double(80, 70, 14, 18));
        g.fill(new Ellipse2D.Double(106, 70, 14, 18));
    }

    private void drawSpider(Graphics2D g) {
        g.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int side = -1; side <= 1; side += 2) {
            for (int i = 0; i < 4; i++) {
                Path2D leg = new Path2D.Float();
                leg.moveTo(100 + side * 30, 112 + i * 6);
                leg.lineTo(100 + side * (64 + i * 4), 55 + i * 22);
                leg.lineTo(100 + side * (90 - i * 2), 120 + i * 17);
                g.setColor(new Color(0x2A2438));
                g.draw(leg);
            }
        }
        g.setColor(new Color(0xFFF1E0));
        g.fill(new Ellipse2D.Double(42, 62, 116, 98));
        g.setColor(new Color(0x15121C));
        g.fill(new Ellipse2D.Double(76, 90, 20, 26));
        g.fill(new Ellipse2D.Double(104, 90, 20, 26));
    }
}