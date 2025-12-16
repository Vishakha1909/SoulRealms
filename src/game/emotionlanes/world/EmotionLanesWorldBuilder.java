package game.emotionlanes.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import game.core.world.Position;
import game.core.world.Tile;
import game.core.world.TileCategory;
import game.core.world.World;

public class EmotionLanesWorldBuilder {

    private static final int ROWS = 8;
    private static final int COLS = 8;
    private static final int[] WALL_COLS = {2, 5};

    // lanes are 2 columns each
    private static final int[][] LANE_COLS = {
            {0, 1}, {3, 4}, {6, 7}
    };

    private static final Random RNG = new Random();

    public static EmotionLanesWorldData buildDefaultWorld() {
        Tile[][] tiles = new Tile[ROWS][COLS];
        char[][] glyph = new char[ROWS][COLS];

        // base: all common + plain glyph
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                tiles[r][c] = new Tile(TileCategory.COMMON);
                glyph[r][c] = 'P';
            }
        }

        // walls (inaccessible columns)
        for (int r = 0; r < ROWS; r++) {
            for (int w = 0; w < WALL_COLS.length; w++) {
                int c = WALL_COLS[w];
                tiles[r][c] = new Tile(TileCategory.BLOCKED);
                glyph[r][c] = 'I';
            }
        }

        // nexus rows (top/bottom in lane columns)
        for (int lane = 0; lane < LANE_COLS.length; lane++) {
            for (int k = 0; k < 2; k++) {
                int c = LANE_COLS[lane][k];
                glyph[0][c] = 'N';
                glyph[ROWS - 1][c] = 'N';
            }
        }

        // ---------- PER-LANE RANDOMIZATION ----------
        // Each lane must contain at least one B/C/K/O in interior (rows 1..6)
        for (int lane = 0; lane < 3; lane++) {
            List<Position> laneInterior = laneInteriorPositions(lane);
            Collections.shuffle(laneInterior, RNG);

            // Force one of each special in this lane:
            placeGlyph(tiles, glyph, laneInterior.remove(0), 'B');
            placeGlyph(tiles, glyph, laneInterior.remove(0), 'C');
            placeGlyph(tiles, glyph, laneInterior.remove(0), 'K');
            placeGlyph(tiles, glyph, laneInterior.remove(0), 'O'); // obstacle

            // Fill the rest with weighted random (keep plenty of Plains)
            for (Position p : laneInterior) {
                char g = rollTerrain();
                placeGlyph(tiles, glyph, p, g);
            }
        }

        // build world
        Position start = new Position(ROWS - 1, 3);
        World world = new World(tiles, start);

        Position[] heroSpawns = new Position[]{
                new Position(ROWS - 1, 0),
                new Position(ROWS - 1, 3),
                new Position(ROWS - 1, 6)
        };
        Position[] monsterSpawns = new Position[]{
                new Position(0, 1),
                new Position(0, 4),
                new Position(0, 7)
        };

        return new EmotionLanesWorldData(world, glyph, heroSpawns, monsterSpawns);
    }

    private static List<Position> laneInteriorPositions(int lane) {
        List<Position> out = new ArrayList<Position>();
        int c1 = LANE_COLS[lane][0];
        int c2 = LANE_COLS[lane][1];

        for (int r = 1; r <= 6; r++) {
            out.add(new Position(r, c1));
            out.add(new Position(r, c2));
        }
        return out;
    }

    private static char rollTerrain() {
        // tune as you like; must not be all special
        int x = RNG.nextInt(100);
        if (x < 55) return 'P';
        if (x < 70) return 'B';
        if (x < 85) return 'C';
        if (x < 95) return 'K';
        return 'O';
    }

    private static void placeGlyph(Tile[][] tiles, char[][] glyph, Position p, char g) {
        glyph[p.row][p.col] = g;

        // IMPORTANT: obstacles are handled by glyph + TerrainEffectManager,
        // so keep tiles COMMON (movement checks terrain.isObstacle()).
        tiles[p.row][p.col] = new Tile(TileCategory.COMMON);
    }
}
