package game.emotionlanes.terrain;

import game.emotionlanes.model.LaneUnit;
import game.emotionlanes.model.UnitType;

/**
 * TerrainEffect implementation for Koulou tiles.
 *
 * Represents an ego-spire style buff/debuff for units standing on Koulou tiles.
 * The exact stat changes are handled inside apply/remove.
 */

public class KoulouEffect implements TerrainEffect {
    // Koulou: +STR
    public void apply(LaneUnit u, boolean allowMonsterBuffs) {
        if (u.getType() == UnitType.MONSTER && !allowMonsterBuffs) return;
        u.addStrBuff(3);
    }
    public void remove(LaneUnit u, boolean allowMonsterBuffs) {
        if (u.getType() == UnitType.MONSTER && !allowMonsterBuffs) return;
        u.addStrBuff(-3);
    }
}
