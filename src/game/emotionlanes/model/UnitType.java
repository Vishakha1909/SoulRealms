package game.emotionlanes.model;

/**
 * Enum identifying whether a LaneUnit is a HERO or a MONSTER.
 *
 * Used to simplify rule checks (movement constraints, buffs, targeting)
 * without relying on fragile string prefixes.
 */

public enum UnitType {
    HERO, MONSTER
}


