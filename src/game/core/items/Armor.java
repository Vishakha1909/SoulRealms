package game.core.items;

public class Armor extends Item {
    private int damageReduction;

    public Armor(String name, int price, int requiredLevel, int damageReduction) {
        super(name, price, requiredLevel);
        this.damageReduction = damageReduction;
    }

    public int getDamageReduction() { return damageReduction; }
}
