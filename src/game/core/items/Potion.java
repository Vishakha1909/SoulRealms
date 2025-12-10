package game.core.items;

import game.core.model.Character;
import game.core.model.Stats;

public class Potion extends Item {

    // What the potion affects
    public static enum Stat {
        HP,
        MP,
        STRENGTH,
        DEXTERITY,
        AGILITY
    }

    private int amount;
    private Stat stat;

    public Potion(String name,
                  int price,
                  int requiredLevel,
                  int amount,
                  Stat stat) {
        super(name, price, requiredLevel);
        this.amount = amount;
        this.stat = stat;
    }

    public int getAmount() {
        return amount;
    }

    public Stat getStat() {
        return stat;
    }

    // Apply potion effect to a character (hero)
    public void applyTo(Character target) {
        Stats s = target.getStats();

        switch (stat) {
            case HP:
                target.healHp(amount);
                break;

            case MP:
                target.healMp(amount);
                break;

            case STRENGTH:
                s.increase(0, 0, amount, 0, 0);
                break;

            case DEXTERITY:
                s.increase(0, 0, 0, amount, 0);
                break;

            case AGILITY:
                s.increase(0, 0, 0, 0, amount);
                break;
        }

        System.out.println(target.getName() + " uses " + name +
                "! (+" + amount + " " + stat + ")");
    }

    @Override
    public String toString() {
        return name + " [+" + amount + " " + stat +
               ", lvl " + requiredLevel +
               ", price " + price + "]";
    }
}
