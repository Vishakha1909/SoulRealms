package game.emotionlanes.world;

import game.core.world.Position;
import game.core.world.Tile;
import game.core.world.TileCategory;
import game.core.world.World;

/**
 * Builds an 8x8 board that visually looks like the Valor example:
 *
 *  - 'N'  on row 0 and row 7 in lane columns  (nexus)
 *  - 'I'  at columns 2 and 5 on all rows      (lane walls)
 *  - 'B'  (Bush) on row 1 in lane cells
 *  - 'C'  (Cave) on row 2 in lane cells
 *  - 'K'  (Koulou) on row 3 in lane cells
 *  - 'P'  (Plain) on rows 4,5,6 in lane cells
 *
 * Lanes are column pairs [0,1], [3,4], [6,7].
 */
public class EmotionLanesWorldBuilder {

    private static final int ROWS = 8;
    private static final int COLS = 8;
    private static final int[] WALL_COLS = {2, 5};

    public static EmotionLanesWorldData buildDefaultWorld() {
        Tile[][] tiles = new Tile[ROWS][COLS];
        char[][] glyph = new char[ROWS][COLS];

        int r, c;

        // 1) Base init: COMMON tiles, Plain glyph 'P'
        for (r = 0; r < ROWS; r++) {
            for (c = 0; c < COLS; c++) {
                tiles[r][c] = new Tile(TileCategory.COMMON);
                glyph[r][c] = 'P';
            }
        }

        // 2) Lane walls / impassable columns: 2 and 5 → '#'/BLOCKED + glyph 'I'
        for (r = 0; r < ROWS; r++) {
            for (int i = 0; i < WALL_COLS.length; i++) {
                c = WALL_COLS[i];
                tiles[r][c] = new Tile(TileCategory.BLOCKED);
                glyph[r][c] = 'I';
            }
        }

        // 3) lanes: [0,1], [3,4], [6,7]
        int[][] laneCols = new int[][]{
                {0, 1}, {3, 4}, {6, 7}
        };

        // 4) Top and bottom nexus rows: 'N'
        for (int lane = 0; lane < laneCols.length; lane++) {
            int[] colsForLane = laneCols[lane];
            for (int k = 0; k < colsForLane.length; k++) {
                c = colsForLane[k];
                glyph[0][c] = 'N';           // monster nexus row
                glyph[ROWS - 1][c] = 'N';   // hero nexus row
            }
        }

        // 5) Inside lane rows with B/C/K/P pattern
        for (r = 1; r <= 6; r++) {
            for (int lane = 0; lane < laneCols.length; lane++) {
                int[] colsForLane = laneCols[lane];
                for (int k = 0; k < colsForLane.length; k++) {
                    c = colsForLane[k];

                    // skip walls – they already have 'I'
                    if (glyph[r][c] == 'I') continue;

                    if (r == 1) {
                        glyph[r][c] = 'B';
                    } else if (r == 2) {
                        glyph[r][c] = 'C';
                    } else if (r == 3) {
                        glyph[r][c] = 'K';
                    } else {
                        glyph[r][c] = 'P';
                    }
                }
            }
        }

        // 6) world (your constructor World(Tile[][], Position))
        Position start = new Position(ROWS - 1, 3); // bottom mid lane
        World world = new World(tiles, start);

        // 7) Spawn positions for 3 heroes / 3 monsters (just data holders)
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
}
