package game.emotionlanes.terrain;

import game.emotionlanes.model.LaneUnit;
import game.emotionlanes.model.UnitType;

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
