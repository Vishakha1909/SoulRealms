package game.emotionlanes.model;

import game.core.world.Position;

public class LaneUnit {
    private final String id; // "H1", "M2"
    private final UnitType type;
    private Position pos;
    private boolean alive = true;

    // lightweight combat stats for lanes (no dependency on EmotionWar battle)
    private int hp = 30;
    private int atk = 8;
    private int def = 2;

    // optional payload if you later want to attach EmotionWar Hero/Monster objects
    private Object payload;

    public LaneUnit(String id, UnitType type, Position start) {
        this.id = id;
        this.type = type;
        this.pos = start;
    }

    public String getId() { return id; }
    public UnitType getType() { return type; }
    public Position getPos() { return pos; }
    public void setPos(Position p) { this.pos = p; }

    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }

    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; if (this.hp <= 0) { this.hp = 0; this.alive = false; } }

    public int getAtk() { return atk; }
    public void setAtk(int atk) { this.atk = atk; }

    public int getDef() { return def; }
    public void setDef(int def) { this.def = def; }

    public void takeDamage(int dmg) {
        int real = dmg - def;
        if (real < 1) real = 1;
        setHp(hp - real);
    }

    public Object getPayload() { return payload; }
    public void setPayload(Object payload) { this.payload = payload; }
}
