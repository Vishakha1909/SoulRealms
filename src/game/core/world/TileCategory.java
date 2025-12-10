package game.core.world;

/**
 * Generic tile category for the reusable engine.
 * Specific games (like Emotion War) interpret these categories however they want.
 */
public enum TileCategory {
    COMMON,   // normal walkable tile
    MARKET,   // shop
    BLOCKED,  // wall / inaccessible
    SPECIAL,
    INACCESSIBLE   // boss / fracture / whatever the game defines
}
