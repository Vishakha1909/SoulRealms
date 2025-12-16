package game.emotionlanes.ui;

import java.util.Map;

import game.core.world.Position;
import game.emotionlanes.world.EmotionLanesWorldData;

public class EmotionLanesRenderer {

    private final EmotionLanesWorldData data;

    // ANSI colors
    private static final String RESET   = "\u001B[0m";
    private static final String RED     = "\u001B[31m";
    private static final String GREEN   = "\u001B[32m";
    private static final String YELLOW  = "\u001B[33m";
    private static final String BLUE    = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String CYAN    = "\u001B[36m";
    private static final String WHITE   = "\u001B[37m";
    private static final String ORANGE  = "\u001B[41m";

    // cell width
    private static final int CELL_WIDTH = 6;

    // lane wall columns (inaccessible)
    // NOTE: thick dividers are after these cols in the ASCII view
    private static final int[] WALL_COLS = {2, 5};

    public EmotionLanesRenderer(EmotionLanesWorldData data) {
        this.data = data;
    }

    public void render(Map<Position, String> heroTokens,
                       Map<Position, String> monsterTokens) {

        clearSoft();

        System.out.println("==================================================");
        System.out.println("   Emotion Lanes: Defense of the Core");
        System.out.println("      (Legends of Valor variant)");
        System.out.println("==================================================");

        printBoard(heroTokens, monsterTokens);
        printLegend();
    }

    private void clearSoft() {
        for (int i = 0; i < 18; i++) System.out.println();
    }

    private void printBoard(Map<Position, String> heroTokens,
                            Map<Position, String> monsterTokens) {

        char[][] glyph = data.getGlyphLayer();
        int rows = glyph.length;
        int cols = glyph[0].length;

        printHorizontalBorder(cols);

        for (int r = 0; r < rows; r++) {
            StringBuilder terrainLine = new StringBuilder();
            terrainLine.append("||"); // left border (thick)

            for (int c = 0; c < cols; c++) {
                terrainLine.append(padCell(terrainSymbol(r, c)));

                // separators
                if (c == cols - 1) terrainLine.append("||");        // right border (thick)
                else if (isLaneWallCol(c)) terrainLine.append("||"); // thick divider after wall col
                else terrainLine.append("|");
            }

            StringBuilder occLine = new StringBuilder();
            occLine.append("||");

            for (int c = 0; c < cols; c++) {
                Position p = new Position(r, c);
                occLine.append(padCell(occupantLabel(p, heroTokens, monsterTokens)));

                if (c == cols - 1) occLine.append("||");
                else if (isLaneWallCol(c)) occLine.append("||");
                else occLine.append("|");
            }

            System.out.println(terrainLine.toString());
            System.out.println(occLine.toString());
            printHorizontalBorder(cols);
        }
    }

    private String terrainSymbol(int r, int c) {
        char g = data.getGlyphLayer()[r][c];
        return colorTerrain(g);
    }

    /**
     * IMPORTANT: This border must match the vertical separators used in the rows.
     * Rows use:
     *   || at left edge
     *   || after col 2 and 5
     *   || at right edge
     *
     * So the horizontal border must also insert an extra "+" at those boundaries.
     */
    private void printHorizontalBorder(int cols) {
        StringBuilder sb = new StringBuilder();

        // left thick boundary
        sb.append("+");
        for (int w = 0; w < CELL_WIDTH; w++) sb.append("-");
        // after each cell, add + and (if thick boundary) add another +segment
        for (int c = 0; c < cols; c++) {
            // we already printed one segment before loop; handle remaining segments cleanly
            if (c == 0) continue; // first cell already accounted

            sb.append("+");
            for (int w = 0; w < CELL_WIDTH; w++) sb.append("-");

            // insert an extra "+" boundary after wall columns to visually thicken
            if (isLaneWallCol(c - 1)) {
                sb.append("+");
            }
        }

        // close right edge: always one final "+"
        sb.append("+");
        System.out.println(sb.toString());
    }

    private String occupantLabel(Position p,
                                 Map<Position, String> heroes,
                                 Map<Position, String> monsters) {

        String h = heroes != null ? heroes.get(p) : null;
        String m = monsters != null ? monsters.get(p) : null;

        if (h == null && m == null) return "";
        if (h != null && m != null) return colorToken(h) + "/" + colorToken(m);
        return (h != null) ? colorToken(h) : colorToken(m);
    }

    private String colorToken(String t) {
        if (t == null) return "";
        if (t.startsWith("H")) return CYAN + t + RESET;
        if (t.startsWith("M")) return RED + t + RESET;
        return t;
    }

    private String colorTerrain(char g) {
        if (g == 'N') return YELLOW + "N" + RESET;         // nexus
        if (g == 'I') return WHITE + "I" + RESET;          // wall
        if (g == 'P') return "P";                          // plain
        if (g == 'B') return GREEN + "B" + RESET;          // bush
        if (g == 'C') return BLUE + "C" + RESET;           // cave
        if (g == 'K') return MAGENTA + "K" + RESET;        // koulou
        if (g == 'O') return ORANGE + "O" + RESET;         // obstacle
        return "?";
    }

    // ANSI-safe padding
    private static final String ANSI_REGEX = "\\u001B\\[[;\\d]*m";

    private int visibleLen(String s) {
        if (s == null) return 0;
        return s.replaceAll(ANSI_REGEX, "").length();
    }

    private String padCell(String s) {
        if (s == null) s = "";

        boolean hasAnsi = s.matches(".*" + ANSI_REGEX + ".*");
        if (hasAnsi && !s.endsWith(RESET)) s = s + RESET;

        int vlen = visibleLen(s);

        if (vlen > CELL_WIDTH) {
            String plain = s.replaceAll(ANSI_REGEX, "");
            plain = plain.substring(0, CELL_WIDTH);
            return plain;
        }

        StringBuilder sb = new StringBuilder(s);
        while (visibleLen(sb.toString()) < CELL_WIDTH) sb.append(" ");
        return sb.toString();
    }

    private boolean isLaneWallCol(int c) {
        return (c == WALL_COLS[0] || c == WALL_COLS[1]);
    }

    private void printLegend() {
        System.out.println();
        System.out.println("Legend (terrain):");
        System.out.println("  N  Nexus (Core / Rift)");
        System.out.println("  I  Impassable wall");
        System.out.println("  P  Plain");
        System.out.println("  B  Bush / Overthinking Fog");
        System.out.println("  C  Cave / Shadowed Memory");
        System.out.println("  K  Koulou / Ego Spire");
        System.out.println("  O  Obstacle (can be removed by hero)");
        System.out.println("--------------------------------------------------");
        System.out.println();
        System.out.println("Legend (units):");
        System.out.println("  " + CYAN + "H1" + RESET + ", " + CYAN + "H2" + RESET + ", " + CYAN + "H3" + RESET + "  Heroes");
        System.out.println("  " + RED + "M1" + RESET + ", " + RED + "M2" + RESET + ", " + RED + "M3" + RESET + "  Monsters");
        System.out.println("  " + CYAN + "H1" + RESET + "/" + RED + "M1" + RESET + "  both on same tile");
        System.out.println("--------------------------------------------------");
    }
}
