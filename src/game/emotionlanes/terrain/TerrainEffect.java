package game.emotionlanes.terrain;

import game.emotionlanes.model.LaneUnit;

public interface TerrainEffect {
    void apply(LaneUnit u, boolean allowMonsterBuffs);
    void remove(LaneUnit u, boolean allowMonsterBuffs);
}
