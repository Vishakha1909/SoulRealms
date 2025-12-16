package game.core.model;

public abstract class Character {

    protected String name;
    protected int level;
    protected Stats stats;

    protected int hp;
    protected int mp;

    public Character(String name, int level, Stats stats) {
        this.name = name;
        this.level = level;
        this.stats = stats;
        this.hp = stats.getMaxHp();
        this.mp = stats.getMaxMp();
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public Stats getStats() {
        return stats;
    }

    public int getHp() {
        return hp;
    }

    public int getMp() {
        return mp;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void takeDamage(int amount) {
        hp -= amount;
        if (hp < 0) hp = 0;
    }

    public void restoreFull() {
        hp = stats.getMaxHp();
        mp = stats.getMaxMp();
    }

    /** Raw attack power before defense reduction. */
    public abstract int getAttackDamage();

    /** Flat defense value reducing incoming physical damage. */
    public abstract int getDefense();

    /** Chance (0.0–1.0) to dodge an incoming attack. */
    public abstract double getDodgeChance();

    public void healHp(int amount) {
    hp += amount;
    if (hp > stats.getMaxHp()) {
        hp = stats.getMaxHp();
    }
}

public void healMp(int amount) {
    mp += amount;
    if (mp > stats.getMaxMp()) {
        mp = stats.getMaxMp();
    }
}

/** Spend MP for spells. Returns false if not enough MP. */
public boolean spendMp(int amount) {
    if (mp < amount) return false;
    mp -= amount;
    return true;
}

public void setHp(int v) {
    if (v < 0) v = 0;
    if (v > stats.getMaxHp()) v = stats.getMaxHp();
    hp = v;
}

public void setMp(int v) {
    if (v < 0) v = 0;
    if (v > stats.getMaxMp()) v = stats.getMaxMp();
    mp = v;
}

public void healFlat(int delta) { setHp(hp + delta); }
public void restoreMpFlat(int delta) { setMp(mp + delta); }

}
