package game.emotionlanes.model;

import game.core.world.Position;

public class LaneUnit {
    private final String id; // "H1", "M2"
    private final UnitType type;
    private Position pos;
    private boolean alive = true;

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
}
