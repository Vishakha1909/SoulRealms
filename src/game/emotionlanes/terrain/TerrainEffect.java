package game.emotionlanes.terrain;

import game.emotionlanes.model.LaneUnit;

/**
 * Strategy interface for terrain-based buffs/debuffs in Emotion Lanes.
 *
 * Implementations encapsulate how a terrain tile modifies a unit when the unit
 * enters the tile, and how to revert that change when the unit leaves.
 *
 * Design Notes:
 * - Strategy Pattern: each terrain type is a separate implementation
 * - Used by TerrainEffectManager to apply/remove effects consistently
 */

public interface TerrainEffect {
    // applying 
    void apply(LaneUnit u, boolean allowMonsterBuffs);

    //removing
    void remove(LaneUnit u, boolean allowMonsterBuffs);
}
