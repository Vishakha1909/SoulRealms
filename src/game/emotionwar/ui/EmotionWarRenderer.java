package game.emotionwar.ui;

import game.core.model.Hero;
import game.core.ui.Renderer;
import game.core.world.Position;
import game.core.world.TileCategory;
import game.core.world.World;
import game.emotionwar.model.EmotionHero;
import game.emotionwar.model.EmotionType;

import java.util.List;
import java.util.Map;

public class EmotionWarRenderer implements Renderer {

    private final World world;
    private final EmotionType[][] emotionLayer;
    private final List<Hero> party;

    // ANSI colors
    private static final String RESET   = "\u001B[0m";
    private static final String RED     = "\u001B[31m";
    private static final String GREEN   = "\u001B[32m";
    private static final String YELLOW  = "\u001B[33m";
    private static final String BLUE    = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String CYAN    = "\u001B[36m";
    private static final String WHITE   = "\u001B[37m";

    public EmotionWarRenderer(World world, EmotionType[][] emotionLayer, List<Hero> party) {
        this.world = world;
        this.emotionLayer = emotionLayer;
        this.party = party;
    }

    @Override
    public void render(Map<Position, String> heroTokens, Map<Position, String> monsterTokens) {
        render();
    }

    public void render() {
        // crude clear
        for (int i = 0; i < 30; i++) {
            System.out.println();
        }

        System.out.println("========================================");
        System.out.println("       Soul Realms: Emotion War         ");
        System.out.println("========================================");

        printColoredMap();

        System.out.println();
        System.out.println("----- STATUS ---------------------------");
        Hero h = party.isEmpty() ? null : party.get(0);
        if (h != null) {
            Position pos = world.getPartyPosition();
            EmotionType emo = emotionAt(pos);
            TileCategory cat = world.getTile(pos).getCategory();

            System.out.println("Front Hero : " + h.getName());
            if (h instanceof EmotionHero) {
                EmotionHero eh = (EmotionHero) h;
                System.out.println("Class      : " + eh.getHeroClass());
            } else {
                System.out.println("Class      : Unknown");
            }
            System.out.println("Level      : " + h.getLevel());
            System.out.println("Stats      : " + h.getStats());
            System.out.println("Gold       : " + h.getGold());
            System.out.println("Position   : " + pos);
            System.out.println("Tile       : " + cat);
            System.out.println("Emotion    : " + (emo != null ? emo : "None"));
        } else {
            System.out.println("No heroes in party.");
        }

        System.out.println();
        System.out.println("----- LEGEND ---------------------------");
        System.out.println(colorForEmotion(EmotionType.WRATH)  + "W" + RESET + " = Wrath");
        System.out.println(colorForEmotion(EmotionType.DESIRE) + "D" + RESET + " = Desire");
        System.out.println(colorForEmotion(EmotionType.FEAR)   + "F" + RESET + " = Fear");
        System.out.println(colorForEmotion(EmotionType.SORROW) + "S" + RESET + " = Sorrow");
        System.out.println(colorForEmotion(EmotionType.ANXIETY)+ "A" + RESET + " = Anxiety");
        System.out.println(colorForEmotion(EmotionType.ENVY)   + "E" + RESET + " = Envy");
        System.out.println(colorForEmotion(EmotionType.PRIDE)  + "P" + RESET + " = Pride");
        System.out.println("# = Blocked");
        System.out.println(YELLOW + "M" + RESET + " = Sanctuary Market");
        System.out.println(WHITE + "@" + RESET + " = You");
        System.out.println(". = Neutral / unaligned");
        System.out.println("(Fracture tiles are hidden among normal tiles)");
        System.out.println("----------------------------------------");
    }

    private void printColoredMap() {
        int rows = world.getRows();
        int cols = world.getCols();
        Position partyPos = world.getPartyPosition();

        // dynamic border based on number of columns
        String border = buildBorder(cols);

        System.out.println();
        System.out.println(border);

        // Column indices (each index is 3 characters wide)
        System.out.print("   "); // space for row index column
        for (int c = 0; c < cols; c++) {
            System.out.printf("%3d", c);
        }
        System.out.println();

        for (int r = 0; r < rows; r++) {
            // Row index (3 characters wide) + left border
            System.out.printf("%3d|", r);
            for (int c = 0; c < cols; c++) {
                Position p = new Position(r, c);
                if (p.row == partyPos.row && p.col == partyPos.col) {
                    System.out.print(" " + WHITE + "@" + RESET + " ");
                } else {
                    TileCategory cat = world.getTile(p).getCategory();
                    EmotionType emo = emotionLayer != null ? emotionLayer[r][c] : null;
                    String symbol = symbolForTile(cat, emo);
                    System.out.print(" " + symbol + " ");
                }
            }
            System.out.println("|");
        }
        System.out.println(border);
    }

    private String buildBorder(int cols) {
        StringBuilder sb = new StringBuilder();
        sb.append("   +");
        for (int c = 0; c < cols; c++) {
            sb.append("---");
        }
        sb.append("+");
        return sb.toString();
    }

    private String symbolForTile(TileCategory cat, EmotionType emo) {
        if (cat == TileCategory.BLOCKED) {
            return "#"; // single char so grid aligns nicely
        }
        if (cat == TileCategory.MARKET) {
            return YELLOW + "M" + RESET;
        }
        // SPECIAL (fracture) tiles are intentionally rendered as normal ground/emotion
        // to keep them visually hidden.
        // COMMON / SPECIAL tiles: show emotion-colored letter if present, else '.'
        if (emo == null) {
            return ".";
        }
        String color = colorForEmotion(emo);
        String letter;
        switch (emo) {
            case WRATH:    letter = "W"; break;
            case FEAR:     letter = "F"; break;
            case SORROW:   letter = "S"; break;
            case ANXIETY:  letter = "A"; break;
            case ENVY:     letter = "E"; break;
            case DESIRE:   letter = "D"; break;
            case PRIDE:    letter = "P"; break;
            default:       letter = "?"; break;
        }
        return color + letter + RESET;
    }

    private String colorForEmotion(EmotionType emo) {
        if (emo == null) return RESET;
        switch (emo) {
            case WRATH:    return RED;
            case FEAR:     return MAGENTA;
            case SORROW:   return BLUE;
            case ANXIETY:  return YELLOW;
            case ENVY:     return GREEN;
            case DESIRE:   return MAGENTA;
            case PRIDE:    return CYAN;
            default:       return RESET;
        }
    }

    private EmotionType emotionAt(Position p) {
        if (emotionLayer == null) return null;
        if (p.row < 0 || p.row >= emotionLayer.length ||
                p.col < 0 || p.col >= emotionLayer[0].length) {
            return null;
        }
        return emotionLayer[p.row][p.col];
    }
}