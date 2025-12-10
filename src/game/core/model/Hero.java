package game.core.model;

import game.core.items.Armor;
import game.core.items.Weapon;

public abstract class Hero extends Character {

    protected int gold;
    protected int experience;

    protected Weapon mainHand;
    protected Weapon offHand;

    protected Armor armor;

    protected Inventory inventory;

    public Hero(String name, int level, Stats stats, int gold) {
        super(name, level, stats);
        this.gold = gold;
        this.experience = 0;
        this.inventory = new Inventory();
    }

    public Inventory getInventory() {
        return inventory;
    }


    public int getGold() {
        return gold;
    }

    public void addGold(int delta) {
        gold += delta;
        if (gold < 0) gold = 0;
    }

    public boolean spendGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }

    public Weapon getMainHand() {
        return mainHand;
    }

    public Weapon getOffHand() {
        return offHand;
    }

    public int getWeaponDamage() {
        int dmg = 0;
        if (mainHand != null) {
            dmg += mainHand.getDamage();
        }
        if (offHand != null) {
            dmg += offHand.getDamage();
        }
        return dmg;
    }

    public void equipWeapon(Weapon w) {
        if (w == null) return;

        if (w.getRequiredLevel() > level) {
            System.out.println(getName() + " is not high enough level to use " + w.getName());
            return;
        }

        if (w.getHands() == 2) {
            // two-handed weapon occupies both hands
            this.mainHand = w;
            this.offHand = null;
            System.out.println(getName() + " equips two-handed " + w.getName() + " (both hands).");
        } else {
            // 1-handed weapon
            if (mainHand == null) {
                mainHand = w;
                System.out.println(getName() + " equips " + w.getName() + " in main hand.");
            } else if (offHand == null) {
                // can dual-wield
                offHand = w;
                System.out.println(getName() + " equips " + w.getName() + " in off hand.");
            } else {
                // both hands already full → replace main hand (simple rule)
                System.out.println(getName() + " already has two 1-handed weapons; replacing main hand.");
                mainHand = w;
            }
        }
    }

    public Armor getArmor() {
        return armor;
    }

    @Override 
    public int getAttackDamage() {
        return getWeaponDamage();
    }


    public void equipArmor(Armor armor) {
        this.armor = armor;
    }

    @Override
    public int getDefense() {
        int def = 0;
        if (armor != null) {
            def += armor.getDamageReduction();
        }
        return def;
    }

    @Override
    public double getDodgeChance() {
        // 1% per 5 AGI, capped at 40%
        double chance = (stats.getAgility() / 5) * 0.01;
        if (chance > 0.40) chance = 0.40;
        return chance;
    }

    // ---------- Leveling ----------

    public void addExperience(int xp) {
        experience += xp;
        while (experience >= xpToNextLevel()) {
            experience -= xpToNextLevel();
            levelUp();
        }
    }

    protected int xpToNextLevel() {
        return level * 50; // simple formula
    }

    protected void levelUp() {
        level++;
        System.out.println(name + " reached level " + level + "!");

        // tweak growth numbers as you like
        stats.increase(10, 5, 3, 2, 2);
        restoreFull();
        System.out.println("  New stats: " + stats);
    }

    @Override
    public String toString() {
        return name + " (Lv " + level + ") " +
               "HP=" + hp + "/" + stats.getMaxHp() +
               " MP=" + mp + "/" + stats.getMaxMp() +
               " Gold=" + gold;
    }
}
