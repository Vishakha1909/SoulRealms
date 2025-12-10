package game.core.items;

public class Spell extends Item {

    private int damage;
    private int manaCost;
    private SpellType type;

    public Spell(String name,
                 int price,
                 int requiredLevel,
                 int damage,
                 int manaCost,
                 SpellType type) {
        super(name, price, requiredLevel);
        this.damage = damage;
        this.manaCost = manaCost;
        this.type = type;
    }

    public int getDamage() {
        return damage;
    }

    public int getManaCost() {
        return manaCost;
    }

    public SpellType getType() {
        return type;
    }

    @Override
    public String toString() {
        return name + " [" + type + "] dmg=" + damage +
               " mpCost=" + manaCost +
               " lvl=" + requiredLevel +
               " price=" + price;
    }
}
