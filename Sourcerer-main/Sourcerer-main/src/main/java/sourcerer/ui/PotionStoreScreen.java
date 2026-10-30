package sourcerer.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import sourcerer.model.Player;
import sourcerer.model.Potion;
import sourcerer.model.Progress;

public final class PotionStoreScreen extends ScreenPanel {
    private final Player player;
    private final Progress progress;
    private final JLabel balance = Theme.label("", Theme.sans(Font.BOLD, 24), Theme.XP_GOLD);
    private final JLabel status = Theme.label("", Theme.sans(Font.PLAIN, 16), Color.WHITE);
    private final JLabel[] owned = new JLabel[Potion.values().length];
    private boolean purchaseGuard;

    public PotionStoreScreen(Runnable back, Player player, Progress progress) {
        super(new Color(0x261A4C), new Color(0x08060F));
        this.player = player;
        this.progress = progress;
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(28, 50, 28, 50));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        FantasyButton backButton = new FantasyButton("Back to Map", 180, 44);
        backButton.addActionListener(e -> back.run());
        header.add(backButton, BorderLayout.WEST);
        // Changed serif to sans here
        JLabel title = Theme.label("Potion Store", Theme.serif(Font.BOLD, 38), Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(title, BorderLayout.CENTER);
        balance.setIcon(icon("items/coin.png", 36).getIcon());
        balance.setIconTextGap(8);
        header.add(balance, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel listings = new JPanel(new GridLayout(Potion.values().length, 1, 0, 14));
        listings.setOpaque(false);
        for (Potion potion : Potion.values()) {
            JPanel row = new RoundPanel(new Color(16, 11, 32, 225), Theme.TEAL, 24);
            row.setLayout(new BorderLayout(18, 0));
            row.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
            JLabel details = Theme.label("<html><b>" + potion.getLabel() + "</b> — "
                    + potion.getDescription() + "<br>Price: " + potion.getPrice() + " coins</html>",
                    Theme.sans(Font.PLAIN, 18), Color.WHITE);
            row.add(details, BorderLayout.CENTER);
            JLabel count = Theme.label("", Theme.sans(Font.BOLD, 16), Theme.CYAN_LIGHT);
            owned[potion.ordinal()] = count;
            JPanel left = new JPanel(new BorderLayout(12, 0));
            left.setOpaque(false);
            left.add(icon("items/potion_" + potion.name().toLowerCase() + ".png", 72), BorderLayout.WEST);
            left.add(count, BorderLayout.CENTER);
            row.add(left, BorderLayout.WEST);
            FantasyButton buy = new FantasyButton("Buy", 150, 48);
            buy.addActionListener(e -> buy(potion));
            row.add(buy, BorderLayout.EAST);
            listings.add(row);
        }
        JPanel wrap = new JPanel(new java.awt.GridBagLayout());
        wrap.setOpaque(false);
        java.awt.GridBagConstraints gc = new java.awt.GridBagConstraints();
        gc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        wrap.add(listings, gc);
        add(wrap, BorderLayout.CENTER);
        status.setHorizontalAlignment(SwingConstants.CENTER);
        add(status, BorderLayout.SOUTH);
        refresh();
    }

    private static JLabel icon(String path, int size) {
        JLabel l = new JLabel();
        java.awt.image.BufferedImage img = Assets.image(path);
        if (img != null) {
            l.setIcon(new javax.swing.ImageIcon(img.getScaledInstance(size, size, java.awt.Image.SCALE_REPLICATE)));
        }
        return l;
    }

    public void refresh() {
        balance.setText("Coins: " + player.getCoins());
        for (Potion potion : Potion.values()) {
            owned[potion.ordinal()].setText("Owned: " + player.getPotionCount(potion));
        }
    }

    private void buy(Potion potion) {
        if (purchaseGuard) return;
        purchaseGuard = true;
        try {
            if (!progress.purchasePotion(player, potion)) {
                status.setText("Not enough coins for " + potion.getLabel() + ".");
                return;
            }
            status.setText("Purchased " + potion.getLabel() + ".");
            refresh();
        } catch (IllegalStateException failure) {
            status.setText("Purchase wasn't saved: " + failure.getMessage());
        } finally {
            javax.swing.Timer rearm = new javax.swing.Timer(350, e -> purchaseGuard = false);
            rearm.setRepeats(false);
            rearm.start();
        }
    }
}