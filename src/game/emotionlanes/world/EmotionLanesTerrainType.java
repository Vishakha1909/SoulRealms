package game.emotionlanes.world;

/**
 * Terrain 'emotion features' on the Emotion Lanes board.
 * Gameplay will give buffs based on these.
 */
public enum EmotionLanesTerrainType {
    PLAIN,      // neutral ground
    FOG,        // overthinking fog (bush) – DEX buff
    SHADOW,     // shadowed memories (cave) – AGI buff
    EGO,        // ego spire (koulou) – STR buff
    OBSTACLE    // trauma shard / rock – blocks tile
}
