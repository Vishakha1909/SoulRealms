package game.emotionlanes.ui;

import java.util.Map;

import game.core.ui.Renderer;
import game.core.world.Position;
import game.emotionlanes.world.EmotionLanesWorldData;

public class EmotionLanesRenderer implements Renderer {

    private final EmotionLanesWorldData data;
    private final char[][] glyph;

    // interior width of each cell (not counting the side borders)
    private static final int CELL_INNER = 5; // tweak: 5 or 7 looks nice

    public EmotionLanesRenderer(EmotionLanesWorldData data) {
        this.data = data;
        this.glyph = data.getGlyphLayer();
    }

    public void render(Map<Position, String> heroTokens,
                       Map<Position, String> monsterTokens) {

        // crude clear
        for (int i = 0; i < 20; i++) System.out.println();

        System.out.println("========================================");
        System.out.println("   Emotion Lanes: Defense of the Core   ");
        System.out.println("      (Legends of Valor variant)        ");
        System.out.println("========================================");

        int rows = glyph.length;
        int cols = glyph[0].length;

        printBorder(cols);

        for (int r = 0; r < rows; r++) {
            // Terrain line
            StringBuilder t = new StringBuilder();
            t.append("|");
            for (int c = 0; c < cols; c++) {
                t.append(center(String.valueOf(glyph[r][c]), CELL_INNER)).append("|");
            }
            System.out.println(t.toString());

            // Occupant line (heroes/monsters)
            StringBuilder o = new StringBuilder();
            o.append("|");
            for (int c = 0; c < cols; c++) {
                Position p = new Position(r, c);
                String occ = occupantLabel(p, heroTokens, monsterTokens);
                o.append(center(occ, CELL_INNER)).append("|");
            }
            System.out.println(o.toString());

            printBorder(cols);
        }

        printLegend();
    }

    @Override
    public void render() {

    }

    private void printBorder(int cols) {
        StringBuilder sb = new StringBuilder();
        sb.append("+");
        for (int i = 0; i < cols; i++) {
            for (int k = 0; k < CELL_INNER; k++) sb.append("-");
            sb.append("+");
        }
        System.out.println(sb.toString());
    }

    private String occupantLabel(Position p,
                                 Map<Position, String> heroTokens,
                                 Map<Position, String> monsterTokens) {
        String h = heroTokens != null ? heroTokens.get(p) : null;
        String m = monsterTokens != null ? monsterTokens.get(p) : null;

        if (h == null && m == null) return "";
        if (h != null && m != null) return h + "/" + m;
        return (h != null) ? h : m;
    }

    // centers text in width (simple, terminal-safe)
    private String center(String s, int width) {
        if (s == null) s = "";
        if (s.length() > width) s = s.substring(0, width);

        int padTotal = width - s.length();
        int padLeft = padTotal / 2;
        int padRight = padTotal - padLeft;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < padLeft; i++) sb.append(" ");
        sb.append(s);
        for (int i = 0; i < padRight; i++) sb.append(" ");
        return sb.toString();
    }

    private void printLegend() {
        System.out.println();
        System.out.println("Legend (terrain):");
        System.out.println("  N  Nexus (Core / Rift)");
        System.out.println("  I  Impassable lane wall");
        System.out.println("  P  Plain ground");
        System.out.println("  B  Bush / Overthinking Fog");
        System.out.println("  C  Cave / Shadowed Memory");
        System.out.println("  K  Koulou / Ego Spire");
        System.out.println();
        System.out.println("Legend (occupants):");
        System.out.println("  H1, H2, H3  Heroes");
        System.out.println("  M1, M2, M3  Monsters");
        System.out.println("  H1/M1       Hero and monster share a tile");
        System.out.println("----------------------------------------");
    }
}
