package game.emotionwar.world;

import game.core.world.Position;
import game.core.world.Tile;
import game.core.world.TileCategory;
import game.core.world.World;
import game.emotionwar.model.EmotionType;

import java.util.*;

public class EmotionWorldBuilder {

    private static final int ROWS = 10;
    private static final int COLS = 10;

    public static EmotionWorldData buildDefaultWorld() {

        Tile[][] tiles = new Tile[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                tiles[r][c] = new Tile(TileCategory.COMMON);  // base neutral
            }
        }

        EmotionType[][] emotions = new EmotionType[ROWS][COLS];

        // 1) RANDOM 2×2 ZONE PLACEMENT FOR ALL EMOTIONS
        List<EmotionType> all = Arrays.asList(
                EmotionType.WRATH,
                EmotionType.DESIRE,
                EmotionType.FEAR,
                EmotionType.SORROW,
                EmotionType.ANXIETY,
                EmotionType.ENVY,
                EmotionType.PRIDE
        );

        Random rng = new Random();
        Set<String> usedCells = new HashSet<>();

        for (EmotionType emo : all) {
            boolean placed = false;
            int attempts = 0;

            while (!placed && attempts < 200) {
                attempts++;
                int r = rng.nextInt(ROWS - 1);
                int c = rng.nextInt(COLS - 1);

                // Check overlap by checking the 4 cells
                String a = r + "," + c;
                String b = r + "," + (c + 1);
                String d = (r + 1) + "," + c;
                String e = (r + 1) + "," + (c + 1);

                if (usedCells.contains(a) || usedCells.contains(b)
                        || usedCells.contains(d) || usedCells.contains(e)) {
                    continue; // try again
                }

                // Mark as used
                usedCells.add(a);
                usedCells.add(b);
                usedCells.add(d);
                usedCells.add(e);

                // Place emotion
                emotions[r][c] = emo;
                emotions[r][c + 1] = emo;
                emotions[r + 1][c] = emo;
                emotions[r + 1][c + 1] = emo;

                placed = true;
            }
        }

        // 2) RANDOM BLOCKED TILES
        int blockedCount = 10; // adjust as you like
        for (int i = 0; i < blockedCount; i++) {
            int r = rng.nextInt(ROWS);
            int c = rng.nextInt(COLS);
            tiles[r][c].setCategory(TileCategory.BLOCKED);
        }

        // 3) RANDOM FRACTURE TILES (SPECIAL)
        int fractureCount = 3;
        for (int i = 0; i < fractureCount; i++) {
            int r = rng.nextInt(ROWS);
            int c = rng.nextInt(COLS);
            tiles[r][c].setCategory(TileCategory.SPECIAL);
        }

        // 4) MARKET (guarantee at least 1 safe spot)
        while (true) {
            int r = rng.nextInt(ROWS);
            int c = rng.nextInt(COLS);
            if (tiles[r][c].getCategory() == TileCategory.COMMON) {
                tiles[r][c].setCategory(TileCategory.MARKET);
                break;
            }
        }

        // 5) START POSITION (must not be blocked)
        Position start;
        while (true) {
            int r = rng.nextInt(ROWS);
            int c = rng.nextInt(COLS);
            if (tiles[r][c].getCategory() != TileCategory.BLOCKED) {
                start = new Position(r, c);
                break;
            }
        }

        // Build world
        World world = new World(tiles, start);
        return new EmotionWorldData(world, emotions);
    }
}