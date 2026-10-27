package sourcerer.model;

/** Represents Java syntax mistakes. Its web hits a little harder than a normal bite. */
public class SyntaxSpider extends Bug {

    public SyntaxSpider() {
        this(1);
    }

    public SyntaxSpider(int campaignLevel) {
        super("Syntax Spider", scaledHp(40, campaignLevel), 8, "spider");
    }

    private static int scaledHp(int baseHp, int campaignLevel) {
        return baseHp + Math.max(0, campaignLevel - 1) * 25;
    }

    @Override
    public String attack(Player target) {
        int dealt = target.takeDamage(getAttackPower() + 2);
        return "Syntax Spider spins a Web of Typos! You take " + dealt + " damage.";
    }
}
