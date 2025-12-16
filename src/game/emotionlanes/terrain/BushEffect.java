package game.emotionlanes.terrain;

import game.emotionlanes.model.LaneUnit;
import game.emotionlanes.model.UnitType;

/**
 * TerrainEffect implementation for Bush tiles.
 *
 * Represents an 'overthinking fog' style buff/debuff for units standing on Bush
 * tiles. The exact stat changes are handled inside apply/remove.
 */

public class BushEffect implements TerrainEffect {
    // Bush: +DEX
    public void apply(LaneUnit u, boolean allowMonsterBuffs) {
        if (u.getType() == UnitType.MONSTER && !allowMonsterBuffs) return;
        u.addDexBuff(3);
    }
    public void remove(LaneUnit u, boolean allowMonsterBuffs) {
        if (u.getType() == UnitType.MONSTER && !allowMonsterBuffs) return;
        u.addDexBuff(-3);
    }
}
