package game.emotionlanes.logic;

/**
 * Difficulty configuration for Emotion Lanes.
 *
 * Controls high-level tuning knobs such as wave spawn frequency and any
 * difficulty-dependent toggles used by other subsystems.
 */

public enum Difficulty {
    EASY(6),
    MEDIUM(4),
    HARD(2);

    private final int spawnEveryRounds;

    Difficulty(int spawnEveryRounds) {
        this.spawnEveryRounds = spawnEveryRounds;
    }

    public int getSpawnEveryRounds() {
        return spawnEveryRounds;
    }
}
