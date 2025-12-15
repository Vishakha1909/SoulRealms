package game.emotionlanes;

import java.util.Scanner;

import game.core.game.Game;
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
        renderer = new EmotionLanesRenderer(data);
        spawns.spawnDefault(state, data);
    }

    @Override
    public void run() {
        boolean running = true;

        while (running) {
            renderer.render(tokens.heroTokens(state), tokens.monsterTokens(state));

            if (checkWinLose()) break;

            // ===== HERO PHASE (each living hero acts once) =====
            System.out.println("=== HERO PHASE ===");
            for (int i = 0; i < state.getHeroes().size(); i++) {
                LaneUnit h = state.getHeroes().get(i);
                if (!h.isAlive()) continue;

                boolean tookAction = heroTurn(h);
                if (!tookAction) {
                    // allow quit
                    running = false;
                    break;
                }

                if (checkWinLose()) {
                    running = false;
                    break;
                }
            }

            if (!running) break;

            // ===== MONSTER PHASE =====
            System.out.println("=== MONSTER PHASE ===");
            turns.monstersAct(data.getWorld(), state);

            if (checkWinLose()) break;
        }

        System.out.println("Back to main menu.");
    }

    private boolean heroTurn(LaneUnit h) {
        while (true) {
            renderer.render(tokens.heroTokens(state), tokens.monsterTokens(state));
            System.out.println("Hero turn: " + h.getId() + " HP " + h.getHp() + " at " + h.getPos());
            System.out.println("1) Move (W/A/S/D)");
            System.out.println("2) Teleport (across lanes)");
            System.out.println("3) Skip");
            System.out.println("Q) Quit to menu");
            System.out.print("> ");

            String choice = sc.nextLine().trim().toUpperCase();
            if ("Q".equals(choice)) return false;

            if ("3".equals(choice)) return true;

            if ("1".equals(choice)) {
                System.out.print("Direction (W/A/S/D) or X cancel: ");
                String mv = sc.nextLine().trim().toUpperCase();
                if (mv.length() == 0) continue;
                if ("X".equals(mv)) continue;

                char dir = mv.charAt(0);
                boolean moved = turns.tryMoveHero(data.getWorld(), state, h, dir);
                if (!moved) {
                    System.out.println("Blocked / illegal move (hero collision or behind monster).");
                    pauseTiny();
                    continue;
                }
                return true; // action spent
            }

            if ("2".equals(choice)) {
                LaneUnit target = pickOtherHero(h);
                if (target == null) continue;

                boolean ok = turns.tryTeleportHero(data.getWorld(), state, h, target);
                if (!ok) {
                    System.out.println("Teleport failed (must be across lanes, adjacent to ally, not ahead, not behind monsters).");
                    pauseTiny();
                    continue;
                }
                return true; // action spent
            }

            System.out.println("Invalid.");
            pauseTiny();
        }
    }

    private LaneUnit pickOtherHero(LaneUnit current) {
        System.out.println("Teleport target hero:");
        for (int i = 0; i < state.getHeroes().size(); i++) {
            LaneUnit h = state.getHeroes().get(i);
            if (!h.isAlive()) continue;
            if (h == current) continue;
            System.out.println((i + 1) + ") " + h.getId() + " at " + h.getPos());
        }
        System.out.println("0) Cancel");
        System.out.print("> ");
        String s = sc.nextLine().trim();
        int idx;
        try {
            idx = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
        if (idx <= 0) return null;
        idx = idx - 1;
        if (idx < 0 || idx >= state.getHeroes().size()) return null;

        LaneUnit chosen = state.getHeroes().get(idx);
        if (!chosen.isAlive() || chosen == current) return null;
        return chosen;
    }

    private void pauseTiny() {
        System.out.print("(press Enter) ");
        sc.nextLine();
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
