package sourcerer.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MainMenuScreen extends ScreenPanel {

    public MainMenuScreen(GameFrame frame) {
        super(new Color(0x24174F), new Color(0x07050F));
        
        // Loads mainmenubg.png
        setBackdrop(Biomes.optional("mainmenubg.png"));
        
        setLayout(new BorderLayout(0, 0));

        // --- LEFT SIDE: THE TITLE ---
        // FlowLayout.LEFT forces it to the top-left corner
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 30));
        leftPanel.setOpaque(false);
        leftPanel.add(new TitleArt());
        add(leftPanel, BorderLayout.CENTER);

        // --- RIGHT SIDE: THE BUTTONS ---
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        // Push the buttons away from the right edge
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 100));

        rightPanel.add(Box.createVerticalGlue());
        
        // Bigger buttons (380x65)
        rightPanel.add(menuButton("Play", frame::showMap));
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(menuButton("Tutorial", frame::startTutorial));
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(menuButton("Syntax Dictionary", frame::showDictionary));
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(menuButton("Exit", frame::exitGame));
        
        rightPanel.add(Box.createVerticalGlue());
        
        add(rightPanel, BorderLayout.EAST);
    }

    private FantasyButton menuButton(String text, Runnable action) {
        FantasyButton b = new FantasyButton(text, 380, 65);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.addActionListener(e -> action.run());
        return b;
    }

    /** Bold, pure white glowing title with a magical pulsing animation. */
    private static class TitleArt extends JComponent {

        private double phase = 0;
        private final Timer timer;

        TitleArt() {
            // Large enough to hold the big font without clipping
            setPreferredSize(new Dimension(800, 400));
            setOpaque(false);

            timer = new Timer(50, e -> {
                phase += 0.05;
                repaint();
            });
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
            
            // Reduced font size so it fits nicely and doesn't overlap
            Font f = Theme.serif(Font.BOLD, 85); 
            
            // Increased the vertical space between the two lines
            drawGlowText(g2, "SOURCERER", f, 20, 110);
            drawGlowText(g2, "CRUSADE", f, 20, 215);
            
            // Tagline placed cleanly below
            g2.setFont(Theme.serif(Font.BOLD, 24));
            g2.setColor(Color.WHITE);
            g2.drawString("Learn Python by Playing", 25, 275);
            
            g2.dispose();
        }

        private void drawGlowText(Graphics2D g2, String text, Font font, int x, int baseline) {
            FontRenderContext frc = g2.getFontRenderContext();
            GlyphVector gv = font.createGlyphVector(frc, text);
            
            // Safety fallback if the font fails
            if (gv == null || gv.getVisualBounds().isEmpty()) {
                g2.setFont(font);
                g2.setColor(Color.WHITE);
                g2.drawString(text, x, baseline);
                return;
            }

            // Draw the shape at the specified x coordinate
            Shape shape = gv.getOutline(x, baseline);
            
            // Pure white pulsing glow (Toned down so it doesn't blob out)
            double pulse = 0.5 + 0.5 * Math.sin(phase);
            int glowAlpha = (int) (30 + 25 * pulse); // Lower opacity
            float glowWidth = (float) (6 + 3 * pulse); // Thinner glow
            
            for (float r = glowWidth; r >= 2; r -= 1.5f) {
                g2.setStroke(new BasicStroke(r, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(255, 255, 255, glowAlpha)); // Pure white glow
                g2.draw(shape);
            }
            
            // Solid white fill (No gradient, no outline)
            g2.setColor(Color.WHITE);
            g2.fill(shape);
        }
    }
}