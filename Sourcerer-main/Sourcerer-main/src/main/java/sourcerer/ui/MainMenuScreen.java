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
        
        setBackdrop(Biomes.optional("mainmenubg.png"));
        
        setLayout(new BorderLayout(0, 0));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 30));
        leftPanel.setOpaque(false);
        leftPanel.add(new TitleArt());
        add(leftPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 100));

        rightPanel.add(Box.createVerticalGlue());
        
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

    private static class TitleArt extends JComponent {

        private double phase = 0;
        private final Timer timer;

        TitleArt() {
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
            
            // KEEP serif for the main title
            Font f = Theme.serif(Font.BOLD, 85); 
            
            drawGlowText(g2, "SOURCERER", f, 20, 110);
            drawGlowText(g2, "CRUSADE", f, 20, 215);
            
            // CHANGED to sans for the tagline to avoid watermark
            g2.setFont(Theme.sans(Font.BOLD, 24));
            g2.setColor(Color.WHITE);
            g2.drawString("Learn Python by Playing", 25, 275);
            
            g2.dispose();
        }

        private void drawGlowText(Graphics2D g2, String text, Font font, int x, int baseline) {
            FontRenderContext frc = g2.getFontRenderContext();
            GlyphVector gv = font.createGlyphVector(frc, text);
            
            if (gv == null || gv.getVisualBounds().isEmpty()) {
                g2.setFont(font);
                g2.setColor(Color.WHITE);
                g2.drawString(text, x, baseline);
                return;
            }

            Shape shape = gv.getOutline(x, baseline);
            
            double pulse = 0.5 + 0.5 * Math.sin(phase);
            int glowAlpha = (int) (30 + 25 * pulse); 
            float glowWidth = (float) (6 + 3 * pulse); 
            
            for (float r = glowWidth; r >= 2; r -= 1.5f) {
                g2.setStroke(new BasicStroke(r, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(255, 255, 255, glowAlpha)); 
                g2.draw(shape);
            }
            
            g2.setColor(Color.WHITE);
            g2.fill(shape);
        }
    }
}