package game.emotionlanes.logic;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

import game.core.world.Position;
import game.emotionlanes.factory.EmotionLanesMonsterFactory;
import game.emotionlanes.model.EmotionLanesMonster;
import game.emotionlanes.model.LaneUnit;
import game.emotionlanes.model.UnitType;
import game.emotionlanes.terrain.TerrainEffectManager;
import game.emotionlanes.world.EmotionLanesWorldData;

import game.emotionwar.factory.EmotionHeroFactory;
import game.emotionwar.model.EmotionHero;

/**
 * Responsible for populating an Emotion Lanes run with heroes and monsters.
 *
 * Responsibilities:
 *  - Load hero/monster definitions from Emotion War data sources (data-driven content).
 *  - Create LaneUnit wrappers and place them on the correct spawn positions.
 *  - Apply initial terrain effects via TerrainEffectManager when units spawn.
 */

public class SpawnManager {

    private final Random rng = new Random();

    // set this to your actual lanes monster data file
    private static final String LANES_MONSTER_DATA_PATH = "data/monsters/monsters_lanes.txt";

    public void spawnFromEmotionWarData(LanesState state,
                                       EmotionLanesWorldData data,
                                       TerrainEffectManager terrain,
                                       Scanner sc) {

        // -------- HEROES (keep EXACTLY as you had) --------
        List<EmotionHero> allHeroes = EmotionHeroFactory.loadAllDefaultHeroes();
        if (allHeroes == null || allHeroes.isEmpty()) {
            throw new IllegalStateException("No EmotionWar heroes loaded (data files missing?)");
        }

        EmotionHero h1 = pickHero(allHeroes, sc, "Pick HERO for lane 1 (H1): ");
        allHeroes.remove(h1);
        EmotionHero h2 = pickHero(allHeroes, sc, "Pick HERO for lane 2 (H2): ");
        allHeroes.remove(h2);
        EmotionHero h3 = pickHero(allHeroes, sc, "Pick HERO for lane 3 (H3): ");
        allHeroes.remove(h3);

        Position[] hs = data.getHeroSpawns();
        LaneUnit u1 = new LaneUnit("H1", UnitType.HERO, hs[0]); u1.attachHero(h1);
        LaneUnit u2 = new LaneUnit("H2", UnitType.HERO, hs[1]); u2.attachHero(h2);
        LaneUnit u3 = new LaneUnit("H3", UnitType.HERO, hs[2]); u3.attachHero(h3);

        u1.setHomeNexus(hs[0]);
        u2.setHomeNexus(hs[1]);
        u3.setHomeNexus(hs[2]);


        state.getHeroes().add(u1);
        state.getHeroes().add(u2);
        state.getHeroes().add(u3);

        // initial starter kit for heroes
        StarterKitService kit = new StarterKitService();
        kit.giveStarterKit(h1);
        kit.giveStarterKit(h2);
        kit.giveStarterKit(h3);


        // -------- MONSTERS  --------
        // Load lanes monsters from file ONCE
        // If your factory doesn't guard against double-load, it's still OK to only call this once in init,
        // but calling here also works if it's idempotent.
        EmotionLanesMonsterFactory.loadMonsters(LANES_MONSTER_DATA_PATH);

        Position[] ms = data.getMonsterSpawns();

        // Pick starting monster level (you can scale later)
        int startLevel = 1;

        LaneUnit m1 = new LaneUnit("M1", UnitType.MONSTER, ms[0]);
        m1.attachMonster(randomMonster(startLevel, ms[0], 0));

        LaneUnit m2 = new LaneUnit("M2", UnitType.MONSTER, ms[1]);
        m2.attachMonster(randomMonster(startLevel, ms[1], 1));

        LaneUnit m3 = new LaneUnit("M3", UnitType.MONSTER, ms[2]);
        m3.attachMonster(randomMonster(startLevel, ms[2], 2));

        state.getMonsters().add(m1);
        state.getMonsters().add(m2);
        state.getMonsters().add(m3);

        // -------- Apply terrain buffs on spawn (keep) --------
        terrain.onSpawn(u1);
        terrain.onSpawn(u2);
        terrain.onSpawn(u3);
        terrain.onSpawn(m1);
        terrain.onSpawn(m2);
        terrain.onSpawn(m3);
    }

    

    private EmotionHero pickHero(List<EmotionHero> all, Scanner sc, String prompt) {
        while (true) {
            System.out.println(prompt);
            for (int i = 0; i < all.size(); i++) {
                EmotionHero h = all.get(i);
                System.out.println("  " + (i + 1) + ") " + h.getName()
                        + " (Lvl " + h.getLevel() + ", " + h.getHeroClass() + ")");
            }
            System.out.print("> ");
            String line = sc.nextLine().trim();
            try {
                int idx = Integer.parseInt(line) - 1;
                if (idx >= 0 && idx < all.size()) return all.get(idx);
            } catch (NumberFormatException e) {}
            System.out.println("Invalid choice.");
        }
    }

    private EmotionLanesMonster randomMonster(int level, Position spawn, int laneId) {
        return EmotionLanesMonsterFactory.randomForLevel(level, spawn, laneId);
    }
}
