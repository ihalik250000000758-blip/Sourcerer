package sourcerer.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import sourcerer.model.Level;
import sourcerer.model.LevelBank;
import sourcerer.model.Progress;

/**
 * World map: a winding trail of numbered level nodes.
 * Locked levels are dimmed until the previous level is completed.
 *
 * Each node shows its level's biome (see Biomes.java) inside the circle, so the biome PNGs you add to
 * assets/biomes/ appear here automatically.
 *
 * WORLD MAP SWAP POINT: to put a painted map behind the trail, add assets/biomes/world_map.png
 * (see the setBackdrop call in the constructor). Without it the dark gradient is used.
 */
public class LevelMapScreen extends ScreenPanel {

    private static final int R = 38;

    private final GameFrame frame;
    private final Progress progress;
    private final MapCanvas canvas = new MapCanvas();
    private final JLabel starsLabel = Theme.label("", Theme.sans(Font.BOLD, 26), Theme.XP_GOLD);
    private final JLabel info = Theme.label(" ", Theme.sans(Font.BOLD, 20), Theme.TEXT);

    public LevelMapScreen(GameFrame frame, Progress progress) {
        super(new Color(0x1E1840), new Color(0x07050F));
        this.frame = frame;
        this.progress = progress;
        setBackdrop(Biomes.optional("world_map.png")); // WORLD MAP SWAP POINT (null = plain gradient)
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        FantasyButton back = new FantasyButton("Main Menu", 180, 44);
        back.addActionListener(e -> frame.showMenu());
        JPanel leftButtons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));
        leftButtons.setOpaque(false);
        leftButtons.add(back);
        FantasyButton store = new FantasyButton("Potion Store", 180, 44);
        store.addActionListener(e -> frame.showStore());
        leftButtons.add(store);
        header.add(leftButtons, BorderLayout.WEST);
        JLabel title = Theme.label("Python Quest Map", Theme.serif(Font.BOLD, 40), Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(title, BorderLayout.CENTER);
        header.add(starsLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);

        info.setHorizontalAlignment(SwingConstants.CENTER);
        info.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        add(info, BorderLayout.SOUTH);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refresh();
            }
        });
        refresh();
    }

    public void refresh() {
        starsLabel.setText("\u2605 " + progress.totalStars() + " / " + (3 * LevelBank.count()));
        info.setText("Click the glowing level to play!");
        canvas.repaint();
    }

    private class MapCanvas extends JComponent {

        private double phase;
        private int hover = -1;
        private int shakeId = -1;
        private int shakeTick;
        private final Timer timer = new Timer(40, e -> {
            phase += 0.15;
            if (shakeTick > 0) {
                shakeTick--;
            }
            repaint();
        });

        MapCanvas() {
            setOpaque(false);
            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int h = hit(e.getX(), e.getY());
                    if (h != hover) {
                        hover = h;
                        if (h > 0) {
                            Level lv = LevelBank.get(h);
                            info.setText("Level " + h + ": " + lv.getTitle() + "  \u2014  " + Biomes.name(h)
                                    + (progress.isUnlocked(h) ? "" : "  (locked)"));
                        } else {
                            info.setText("Click the glowing level to play!");
                        }
                    }
                }
            });
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int h = hit(e.getX(), e.getY());
                    if (h < 0) {
                        return;
                    }
                    if (progress.isUnlocked(h)) {
                        frame.startLevel(h);
                    } else {
                        shakeId = h;
                        shakeTick = 12;
                        info.setText("Level " + h + " is locked - finish Level " + (h - 1) + " first!");
                    }
                }
            });
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

        /** Nodes zig-zag from left to right: i is 0-based. Works for any number of levels. */
        private int px(int i) {
            int n = Math.max(2, LevelBank.count());
            return (int) ((0.08 + 0.84 * i / (n - 1.0)) * getWidth());
        }

        private int py(int i) {
            int n = Math.max(2, LevelBank.count());
            double wave = (i % 2 == 0 ? 0.16 : -0.16) + 0.05 * Math.sin(i * 1.7);
            return (int) ((0.52 + wave) * getHeight());
        }

        /** @return level id (1-based) under the mouse, or -1. */
        private int hit(int x, int y) {
            for (int i = 0; i < LevelBank.count(); i++) {
                int dx = x - px(i);
                int dy = y - py(i);
                if (dx * dx + dy * dy <= (R + 4) * (R + 4)) {
                    return i + 1;
                }
            }
            return -1;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int n = LevelBank.count();

            // trail
            for (int i = 0; i < n - 1; i++) {
                boolean open = progress.isCompleted(i + 1);
                Path2D seg = new Path2D.Float();
                seg.moveTo(px(i), py(i));
                double mid = (px(i) + px(i + 1)) / 2.0;
                seg.curveTo(mid, py(i), mid, py(i + 1), px(i + 1), py(i + 1));
                g2.setStroke(new BasicStroke(18, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(0, 0, 0, open ? 140 : 90));
                g2.draw(seg);
                g2.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        1f, new float[]{4f, 12f}, 0f));
                g2.setColor(open ? Theme.XP_GOLD : new Color(0x6A6290));
                g2.draw(seg);
            }

            int current = progress.currentLevel();
            for (int i = 0; i < n; i++) {
                drawNode(g2, i, i + 1, i + 1 == current);
            }
            g2.dispose();
        }

        private void drawNode(Graphics2D g2, int i, int id, boolean current) {
            boolean unlocked = progress.isUnlocked(id);
            boolean done = progress.isCompleted(id);
            int cx = px(i);
            int cy = py(i);
            if (id == shakeId && shakeTick > 0) {
                cx += (shakeTick % 2 == 0) ? 6 : -6;
            }
            int r = R + (hover == id && unlocked ? 4 : 0);
            Color accent = Biomes.accent(id);
            if (current) {
                r += (int) (Math.sin(phase * 1.5) * 3);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 70));
                g2.fill(new Ellipse2D.Double(cx - r - 14, cy - r - 14, 2 * r + 28, 2 * r + 28));
            }
            g2.setColor(new Color(0, 0, 0, 120));
            g2.fill(new Ellipse2D.Double(cx - r + 2, cy - r + 7, 2 * r, 2 * r));

            // BIOME ICON: shows assets/biomes/level<id>.png (or the placeholder scene) inside the circle
            BufferedImage thumb = Biomes.thumbnail(id, 2 * R, 2 * R);
            Graphics2D clipped = (Graphics2D) g2.create();
            clipped.setClip(new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r));
            clipped.drawImage(thumb, cx - r, cy - r, 2 * r, 2 * r, null);
            if (!unlocked) {
                clipped.setColor(new Color(10, 8, 20, 190));
                clipped.fillRect(cx - r, cy - r, 2 * r, 2 * r);
            } else if (!done && !current) {
                clipped.setColor(new Color(10, 8, 20, 70));
                clipped.fillRect(cx - r, cy - r, 2 * r, 2 * r);
            }
            clipped.dispose();

            if (unlocked) { // soft ring in the biome's own colour
                g2.setStroke(new BasicStroke(9f));
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 90));
                g2.draw(new Ellipse2D.Double(cx - r + 5, cy - r + 5, 2 * r - 10, 2 * r - 10));
            }
            g2.setStroke(new BasicStroke(4f));
            g2.setColor(!unlocked ? new Color(0x4A4566) : done ? Theme.GOOD : current ? Theme.XP_GOLD : Theme.VIOLET);
            g2.draw(new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r));

            if (unlocked) {
                g2.setFont(Theme.sans(Font.BOLD, 34));
                FontMetrics fm = g2.getFontMetrics();
                String s = String.valueOf(id);
                int tx = cx - fm.stringWidth(s) / 2;
                int ty = cy + fm.getAscent() / 2 - 2;
                g2.setColor(new Color(0, 0, 0, 200));
                g2.drawString(s, tx + 2, ty + 2);
                g2.setColor(Color.WHITE);
                g2.drawString(s, tx, ty);
            } else {
                drawLock(g2, cx, cy);
            }

            for (int s = 0; s < 3; s++) {
                boolean on = s < progress.getStars(id);
                drawStar(g2, cx + (s - 1) * 24, cy - r - 14 - (s == 1 ? 6 : 0), 11,
                        on ? Theme.XP_GOLD : new Color(255, 255, 255, 50));
            }

            g2.setFont(Theme.sans(Font.BOLD, 16));
            FontMetrics fm = g2.getFontMetrics();
            String t = LevelBank.get(id).getTitle();
            g2.setColor(new Color(0, 0, 0, 170));
            g2.drawString(t, cx - fm.stringWidth(t) / 2 + 1, cy + r + 22);
            g2.setColor(Theme.TEXT);
            g2.drawString(t, cx - fm.stringWidth(t) / 2, cy + r + 21);

            if (current) {
                int bob = (int) (Math.sin(phase * 2) * 6);
                int ty = cy - r - 62 + bob;
                g2.setColor(Theme.XP_GOLD);
                g2.fillPolygon(new Polygon(new int[]{cx - 14, cx + 14, cx}, new int[]{ty, ty, ty + 18}, 3));
                g2.setFont(Theme.sans(Font.BOLD, 14));
                String p = "PLAY";
                g2.drawString(p, cx - g2.getFontMetrics().stringWidth(p) / 2, ty - 4);
            }
        }

        private void drawLock(Graphics2D g2, int cx, int cy) {
            g2.setColor(new Color(0x8C84AE));
            g2.fill(new RoundRectangle2D.Double(cx - 14, cy - 2, 28, 22, 6, 6));
            g2.setStroke(new BasicStroke(4f));
            g2.drawArc(cx - 9, cy - 18, 18, 22, 0, 180);
        }

        private void drawStar(Graphics2D g2, int cx, int cy, int r, Color c) {
            Polygon p = new Polygon();
            for (int k = 0; k < 10; k++) {
                double ang = -Math.PI / 2 + k * Math.PI / 5;
                double rad = (k % 2 == 0) ? r : r * 0.45;
                p.addPoint((int) (cx + Math.cos(ang) * rad), (int) (cy + Math.sin(ang) * rad));
            }
            g2.setColor(c);
            g2.fillPolygon(p);
        }
    }
}
