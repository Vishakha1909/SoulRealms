package game.emotionlanes.model;

import game.core.model.Hero;
import game.core.model.Monster;
import game.core.world.Position;

/**
 * Wrapper model representing a unit positioned on the Emotion Lanes board.
 *
 * A LaneUnit tracks:
 * - identity (H1/H2/H3 or M1/M2/M3)
 * - current Position
 * - alive/dead state
 * - a payload reference to a core Hero or Monster
 * - temporary terrain bonuses (STR/DEX/AGI)
 */

public class LaneUnit {
    private final String id; // "H1", "M2" etc
    private final UnitType type;
    private Position pos;
    private boolean alive = true;

    // Payload from EmotionWar:
    private Hero hero;
    private Monster monster;
    private Position homeNexus;

    public void setHero(Hero hero) { this.hero = hero; }
    public void setMonster(Monster monster) { this.monster = monster; }

    // Temporary terrain buffs (apply on enter, remove on leave)
    private int bonusStr = 0;
    private int bonusDex = 0;
    private int bonusAgi = 0;

    public LaneUnit(String id, UnitType type, Position start) {
        this.id = id;
        this.type = type;
        this.pos = start;
    }

    
    public void setHomeNexus(Position p) { this.homeNexus = p; }
    public Position getHomeNexus() { return homeNexus; }


    public String getId() { return id; }
    public UnitType getType() { return type; }

    public Position getPos() { return pos; }
    public void setPos(Position p) { this.pos = p; }

    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }

    // ---- Payload attach ----
    public void attachHero(Hero h) { this.hero = h; }
    public void attachMonster(Monster m) { this.monster = m; }
    public Hero getHero() { return hero; }
    public Monster getMonster() { return monster; }

    // ---- Buff API ----
    public void clearBuffs() { bonusStr = 0; bonusDex = 0; bonusAgi = 0; }
    public void addStrBuff(int d) { bonusStr += d; }
    public void addDexBuff(int d) { bonusDex += d; }
    public void addAgiBuff(int d) { bonusAgi += d; }

    public int getBonusStr() { return bonusStr; }
    public int getBonusDex() { return bonusDex; }
    public int getBonusAgi() { return bonusAgi; }

    // ---- Effective combat values for Lanes ----
    // We reuse EmotionWar stats if attached.
    public int effectiveAttack() {
        if (hero != null) {
            // Hero.getAttackDamage already includes weapons; add STR buff as extra
            return hero.getAttackDamage() + bonusStr;
        }
        if (monster != null) {
            return monster.getAttackDamage() + bonusStr;
        }
        return 5 + bonusStr;
    }

    public int effectiveDefense() {
        if (hero != null) return hero.getDefense();
        if (monster != null) return monster.getDefense();
        return 1;
    }

    public double effectiveDodge() {
        double base;
        if (hero != null) base = hero.getDodgeChance();
        else if (monster != null) base = monster.getDodgeChance();
        else base = 0.05;

        // Cave gives agility buff; turn that into small dodge bonus
        double bonus = (bonusAgi * 0.01); // +1% per AGI buff point
        double out = base + bonus;
        if (out > 0.60) out = 0.60;
        return out;
    }

    public void takeHit(int rawDamage) {
        int dmg = rawDamage - effectiveDefense();
        if (dmg < 1) dmg = 1;

        if (hero != null) {
            hero.takeDamage(dmg);
            if (!hero.isAlive()) alive = false;
            return;
        }
        if (monster != null) {
            monster.takeDamage(dmg);
            if (!monster.isAlive()) alive = false;
            return;
        }
        // fallback
        alive = false;
    }

    public String hpString() {
        if (hero != null) return hero.getHp() + "/" + hero.getStats().getMaxHp();
        if (monster != null) return monster.getHp() + "/" + monster.getStats().getMaxHp();
        return alive ? "?" : "0";
    }
}
