package game.core.model;

public class Stats {

    private int maxHp;
    private int maxMp;
    private int strength;
    private int dexterity;
    private int agility;

    public Stats(int maxHp, int maxMp, int strength, int dexterity, int agility) {
        this.maxHp = maxHp;
        this.maxMp = maxMp;
        this.strength = strength;
        this.dexterity = dexterity;
        this.agility = agility;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getMaxMp() {
        return maxMp;
    }

    public int getStrength() {
        return strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    public int getAgility() {
        return agility;
    }

    /**
     * Used when a character levels up.
     */
    public void increase(int hpDelta, int mpDelta,
                         int strDelta, int dexDelta, int agiDelta) {
        maxHp += hpDelta;
        maxMp += mpDelta;
        strength += strDelta;
        dexterity += dexDelta;
        agility += agiDelta;
    }

    @Override
    public String toString() {
        return "HP=" + maxHp +
               ", MP=" + maxMp +
               ", STR=" + strength +
               ", DEX=" + dexterity +
               ", AGI=" + agility;
    }
}
