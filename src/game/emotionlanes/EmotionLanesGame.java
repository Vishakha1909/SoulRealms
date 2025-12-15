package game.emotionlanes;

import java.util.Map;
import java.util.Scanner;

import game.core.game.Game;
import game.core.world.Position;
import game.emotionlanes.logic.LanesState;
import game.emotionlanes.logic.SpawnManager;
import game.emotionlanes.logic.TokenMapper;
import game.emotionlanes.logic.TurnManager;
import game.emotionlanes.model.LaneUnit;
import game.emotionlanes.ui.EmotionLanesRenderer;
import game.emotionlanes.world.EmotionLanesWorldBuilder;
import game.emotionlanes.world.EmotionLanesWorldData;

public class EmotionLanesGame implements Game {

    private final Scanner sc = new Scanner(System.in);

    private EmotionLanesWorldData data;
    private LanesState state;
    private EmotionLanesRenderer renderer;

    private final SpawnManager spawns = new SpawnManager();
    private final TokenMapper tokens = new TokenMapper();
    private final TurnManager turns = new TurnManager();

    @Override
    public void init() {
        data = EmotionLanesWorldBuilder.buildDefaultWorld();
        state = new LanesState(data.getWorld(), data.getGlyphLayer());
        

        renderer = new EmotionLanesRenderer(data); // uses glyphLayer internally

        spawns.spawnDefault(state, data);
    }

    @Override
public void run() {
    boolean running = true;

    while (running) {
        renderer.render(tokens.heroTokens(state), tokens.monsterTokens(state));

        if (checkWinLose()) break;

        System.out.println("=== HERO PHASE ===");

        for (int i = 0; i < state.getHeroes().size(); i++) {
            LaneUnit h = state.getHeroes().get(i);
            if (!h.isAlive()) continue;

            boolean done = false;
            while (!done) {
                renderer.render(tokens.heroTokens(state), tokens.monsterTokens(state));
                System.out.println("Hero turn: " + h.getId() + " at " + h.getPos());
                System.out.println("1) Move  (W/A/S/D)");
                System.out.println("2) Skip");
                System.out.print("> ");

                String choice = sc.nextLine().trim().toUpperCase();
                if ("2".equals(choice)) {
                    done = true;
                } else if ("1".equals(choice)) {
                    System.out.print("Direction (W/A/S/D) or X cancel: ");
                    String mv = sc.nextLine().trim().toUpperCase();
                    if (mv.length() == 0) continue;
                    if ("X".equals(mv)) continue;

                    char dir = mv.charAt(0);
                    boolean moved = turns.tryMoveUnit(data.getWorld(), h, dir);
                    if (!moved) {
                        System.out.println("Blocked / invalid move.");
                    } else {
                        done = true; // 1 action only
                    }
                } else {
                    System.out.println("Invalid.");
                }
            }
        }

        System.out.println("=== MONSTER PHASE ===");
        turns.monstersAdvance(data.getWorld(), state.getMonsters());

        printEngagements();
    }

    System.out.println("Back to main menu.");
}


    private LaneUnit pickHero(String pick) {
        if ("1".equals(pick)) return state.getHeroes().get(0);
        if ("2".equals(pick)) return state.getHeroes().get(1);
        if ("3".equals(pick)) return state.getHeroes().get(2);
        return null;
    }

    private void printEngagements() {
        for (LaneUnit h : state.getHeroes()) {
            for (LaneUnit m : state.getMonsters()) {
                if (h.isAlive() && m.isAlive() &&
                    h.getPos().equals(m.getPos())) {
                    System.out.println("⚔  " + h.getId() + " is engaged with " + m.getId() + "!");
                }
            }
        }
    }

    private boolean checkWinLose() {
        int topRow = 0;
        int bottomRow = data.getWorld().getRows() - 1;

        // win if any hero reaches top nexus row
        for (LaneUnit h : state.getHeroes()) {
            if (h.isAlive() && h.getPos().row == topRow) {
                System.out.println("✅ You sealed the Rift! (Hero reached Monster Nexus)");
                return true;
            }
        }

        // lose if any monster reaches bottom nexus row
        for (LaneUnit m : state.getMonsters()) {
            if (m.isAlive() && m.getPos().row == bottomRow) {
                System.out.println("💀 The Core is breached! (Monster reached Hero Nexus)");
                return true;
            }
        }
        return false;
    }
}
