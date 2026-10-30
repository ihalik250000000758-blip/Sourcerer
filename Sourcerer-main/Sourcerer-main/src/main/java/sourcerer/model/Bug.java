package sourcerer.model;

/**
 * INHERITANCE: parent class holding the traits every enemy shares
 * (HP, attack power, sprite id). Subclasses add their own abilities.
 */
public abstract class Bug {

    private final String name;
    private final int maxHp;
    private int hp;
    private final int attackPower;
    private final String spriteId;

    protected Bug(String name, int maxHp, int attackPower, String spriteId) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attackPower = attackPower;
        this.spriteId = spriteId;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAttackPower() { return attackPower; }
    public String getSpriteId() { return spriteId; }
    public boolean isDefeated() { return hp <= 0; }

    public void takeDamage(int amount) {
        hp = Math.max(0, hp - amount);
    }

    /** Attacks the player. Subclasses override this with their special ability. @return a log message. */
    public String attack(Player target) {
        int dealt = target.takeDamage(attackPower);
        return name + " bites you for " + dealt + " damage.";
    }
}

// =========================================================================
// LEVEL ENEMIES
// These are package-private (no 'public' keyword) so they can live in this
// file. Battle.java can still see and use them because it's in the same package.
// =========================================================================

class GreyBat extends Bug {
    public GreyBat() {
        super("Grey Bat", 40, 8, "level1");
    }
    
    @Override
    public String attack(Player target) {
        int dealt = target.takeDamage(getAttackPower());
        return "The Grey Bat swoops down! You take " + dealt + " damage.";
    }
}

class Blobfish extends Bug {
    public Blobfish() {
        super("Blobfish", 50, 10, "level2");
    }

    @Override
    public String attack(Player target) {
        int dealt = target.takeDamage(getAttackPower());
        return "The Blobfish flops on you! You take " + dealt + " damage.";
    }
}

class ShadowFox extends Bug {
    public ShadowFox() {
        super("Shadow Fox", 55, 11, "level3");
    }

    @Override
    public String attack(Player target) {
        int dealt = target.takeDamage(getAttackPower());
        return "The Shadow Fox bites! You take " + dealt + " damage.";
    }
}

class LeafSpider extends Bug {
    public LeafSpider() {
        super("Leaf Spider", 60, 12, "level4");
    }

    @Override
    public String attack(Player target) {
        int dealt = target.takeDamage(getAttackPower());
        return "The Leaf Spider spins a web! You take " + dealt + " damage.";
    }
}

class TopHatTiger extends Bug {
    public TopHatTiger() {
        super("Top-Hat Tiger", 70, 14, "level5");
    }

    @Override
    public String attack(Player target) {
        int dealt = target.takeDamage(getAttackPower());
        return "The Tiger swipes with its cane! You take " + dealt + " damage.";
    }
}

class PurpleBat extends Bug {
    public PurpleBat() {
        super("Purple Bat", 80, 16, "level6");
    }

    @Override
    public String attack(Player target) {
        int dealt = target.takeDamage(getAttackPower());
        target.drainMana(5);
        return "The Purple Bat bites! You take " + dealt + " damage and lose 5 MP.";
    }
}

class DemonSpider extends Bug {
    public DemonSpider() {
        super("Demon Spider", 120, 20, "level7");
    }

    @Override
    public String attack(Player target) {
        int dealt = target.takeDamage(getAttackPower() + 5);
        return "The Demon Spider strikes with dark magic! You take " + dealt + " damage.";
    }
}