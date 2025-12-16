package game.emotionlanes.logic;

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
