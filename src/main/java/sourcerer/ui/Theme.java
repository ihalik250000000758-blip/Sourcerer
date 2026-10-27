package sourcerer.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import javax.swing.JLabel;

/**
 * Colours and fonts in one place. Change them here and every screen follows.
 */
public final class Theme {

    public static final Color BG_DARK = new Color(0x0D0A18);
    public static final Color PANEL_DARK = new Color(0x161127);
    public static final Color PURPLE = new Color(0x2E2057);
    public static final Color VIOLET = new Color(0x7A5CC8);
    public static final Color TEAL = new Color(0x3FB8AF);
    public static final Color TEAL_DARK = new Color(0x1E6F73);
    public static final Color CYAN_LIGHT = new Color(0xA6E3E0);
    public static final Color EMBER = new Color(0xE8793A);
    public static final Color PARCHMENT = new Color(0xDCCFA8);
    public static final Color PARCHMENT_DARK = new Color(0xA8996F);
    public static final Color INK = new Color(0x2B2238);
    public static final Color HP_RED = new Color(0xC8404A);
    public static final Color MP_BLUE = new Color(0x4A7BD0);
    public static final Color XP_GOLD = new Color(0xE5B94E);
    public static final Color GOOD = new Color(0x5ED39A);
    public static final Color BAD = new Color(0xF0636B);
    public static final Color TEXT = new Color(0xECE6F8);
    public static final Color TEXT_MUTED = new Color(0x9A91BC);

    public static final Color GLASS = new Color(16, 11, 32, 205);

    // =====================================================================
    // CUSTOM FONT LOADING
    // =====================================================================
    
    // 1. SOCIETAL DISEASES (for Titles - ALL CAPS only to avoid trial watermarks!)
    private static final Font TITLE_FONT = loadFont("/assets/fonts/sourcerer_font.otf");
    
    // 2. Metamorphous (for Buttons, Menus, and Body text)
    private static final Font UI_FONT = loadFont("/assets/fonts/metamorphous_font.ttf");

    private static Font loadFont(String path) {
        try (InputStream in = Theme.class.getResourceAsStream(path)) {
            if (in != null) {
                Font f = Font.createFont(Font.TRUETYPE_FONT, in);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
                return f;
            } else {
                System.err.println("[Theme] Font file not found on classpath: " + path);
            }
        } catch (Exception e) {
            System.err.println("[Theme] Could not load font: " + path + " (" + e.getMessage() + ")");
        }
        return null;
    }

    private Theme() {
    }

    // =====================================================================
    // FONT ACCESSORS
    // =====================================================================

    /** Used for BIG titles like "SOURCERER CRUSADE" */
    public static Font serif(int style, int size) {
        if (TITLE_FONT != null) {
            return TITLE_FONT.deriveFont(style, size);
        }
        return new Font(Font.SERIF, style, size);
    }

    /** Used for buttons, menus, and body text (Metamorphous - clean and readable) */
    public static Font sans(int style, int size) {
        if (UI_FONT != null) {
            return UI_FONT.deriveFont(style, size);
        }
        return new Font(Font.SANS_SERIF, style, size);
    }

    /** Used for code blocks (keeps standard monospace so tiles align perfectly) */
    public static Font mono(int style, int size) {
        return new Font(Font.MONOSPACED, style, size);
    }

    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }
}