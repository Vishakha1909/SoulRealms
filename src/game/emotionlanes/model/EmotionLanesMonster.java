package game.emotionlanes.model;

import game.core.model.Monster;
import game.core.model.Stats;
import game.core.world.Position;

/**
 * Adds position and lane tracking for lane-based gameplay.
 */
public class EmotionLanesMonster extends Monster {
    
    private Position position;
    private final int laneId; // 0 = top, 1 = mid, 2 = bot
    
    public EmotionLanesMonster(String name,
                      int level,
                      Stats stats,
                      int baseDamage,
                      int defense,
                      double dodgeChance,
                      Position position,
                      int laneId) {
        super(name, level, stats, baseDamage, defense, dodgeChance);
        this.position = position;
        this.laneId = laneId;
    }
    
    public Position getPosition() {
        return position;
    }
    
    public void setPosition(Position position) {
        this.position = position;
    }
    
    public int getLaneId() {
        return laneId;
    }

    @Override
    public String toString() {
        return getName() + " (Lvl " + level + ") " + stats;
    }
}