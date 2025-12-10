package game.emotionwar.logic;

import game.core.battle.Battle;
import game.core.model.Hero;
import game.core.model.Monster;
import game.core.world.Position;
import game.core.world.TileCategory;
import game.core.world.World;
import game.emotionwar.factory.EmotionMonsterFactory;
import game.emotionwar.model.EmotionMonster;
import game.emotionwar.model.EmotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class EmotionEncounterManager {

    private final World world;
    private final EmotionType[][] emotionLayer;
    private final List<Hero> party;
    private final Random rng;
    private final Scanner scanner;

    public EmotionEncounterManager(World world,
                                   EmotionType[][] emotionLayer,
                                   List<Hero> party,
                                   Random rng,
                                   Scanner scanner) {
        this.world = world;
        this.emotionLayer = emotionLayer;
        this.party = party;
        this.rng = rng;
        this.scanner = scanner;
    }

    /**
     * Handle encounters after movement.
     *
     * @return true if party survived, false if they died.
     */
    public boolean handleTileEvent() {
        Position pos = world.getPartyPosition();
        TileCategory cat = world.getTile(pos).getCategory();
        EmotionType emo = emotionAt(pos);

        int partySize = party.size();
        if (partySize <= 0) return true;

        // COMMON emotion tiles: chance of single-emotion encounter
        if (cat == TileCategory.COMMON && emo != null) {
            if (rng.nextDouble() < 0.30) {
                System.out.println("The air thickens with " + emo + "...");
                List<Monster> monsters = buildEmotionEncounter(emo, partySize);
                return startBattle(monsters);
            }
            return true;
        }

        // SPECIAL tiles (fractures): stronger mixed encounter, only after average level >= 3
        if (cat == TileCategory.SPECIAL) {
            int avgLevel = averagePartyLevel();
            if (avgLevel >= 3 && rng.nextDouble() < 0.60) {
                System.out.println("A fracture in your soul churns violently...");
                List<Monster> monsters = buildMixedEncounter(partySize);
                return startBattle(monsters);
            }
            return true;
        }

        // Other tiles: nothing happens
        return true;
    }

    private List<Monster> buildEmotionEncounter(EmotionType emo, int count) {
        List<Monster> result = new ArrayList<Monster>();
        for (int i = 0; i < count; i++) {
            EmotionMonster m = EmotionMonsterFactory.randomForEmotion(emo);
            result.add(m);
        }
        return result;
    }

    private List<Monster> buildMixedEncounter(int count) {
        // Start from factory's mixedEncounter pool and adapt to party size
        List<EmotionMonster> pool = EmotionMonsterFactory.mixedEncounter();
        List<Monster> result = new ArrayList<Monster>();
        if (pool.isEmpty()) {
            // fallback: just spawn random fear
            for (int i = 0; i < count; i++) {
                result.add(EmotionMonsterFactory.randomForEmotion(EmotionType.FEAR));
            }
            return result;
        }
        for (int i = 0; i < count; i++) {
            EmotionMonster m = pool.get(i % pool.size());
            result.add(m);
        }
        return result;
    }

    private boolean startBattle(List<Monster> monsters) {
        Battle b = new Battle(party, monsters, scanner);
        return b.start();
    }

    private EmotionType emotionAt(Position p) {
        if (emotionLayer == null) return null;
        if (p.row < 0 || p.row >= emotionLayer.length ||
                p.col < 0 || p.col >= emotionLayer[0].length) {
            return null;
        }
        return emotionLayer[p.row][p.col];
    }

    private int averagePartyLevel() {
        if (party.isEmpty()) return 1;
        int sum = 0;
        for (int i = 0; i < party.size(); i++) {
            sum += party.get(i).getLevel();
        }
        return Math.max(1, sum / party.size());
    }
}