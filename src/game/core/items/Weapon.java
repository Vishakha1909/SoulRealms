package game.core.items;

public class Weapon extends Item {
    private int damage;
    private int requiredLevel;
    private int hands; // 1 or 2

    public Weapon(String name, int price, int requiredLevel, int damage, int hands) {
        super(name, price,requiredLevel);
        this.requiredLevel = requiredLevel;
        this.damage = damage;
        this.hands = hands;
    }

    public int getDamage() {
        return damage;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public int getHands() {
        return hands;
    }

    @Override
    public String toString() {
        return getName() + " (DMG " + damage +
               ", lvl " + requiredLevel +
               ", " + hands + "-hand)";
    }
}
