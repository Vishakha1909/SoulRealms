package game.core.model;

public abstract class Monster extends Character {

    protected int baseDamage;
    protected int defense;
    protected double dodgeChance; // 0.0–1.0

    public Monster(String name, int level, Stats stats,
                   int baseDamage, int defense, double dodgeChance) {
        super(name, level, stats);
        this.baseDamage = baseDamage;
        this.defense = defense;
        this.dodgeChance = dodgeChance;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public int getRawDefense() {
        return defense;
    }

    public double getRawDodgeChance() {
        return dodgeChance;
    }

    @Override
    public int getAttackDamage() {
        // could also scale with STR or level if you want
        return baseDamage + stats.getStrength();
    }

    @Override
    public int getDefense() {
        return defense;
    }

    @Override
    public double getDodgeChance() {
        return dodgeChance;
    }

    @Override
    public String toString() {
        return name + " (Lv " + level + ") " +
               "HP=" + hp + "/" + stats.getMaxHp();
    }
}
