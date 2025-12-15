package game.emotionlanes.logic;

import game.core.world.Position;
import game.emotionlanes.model.LaneUnit;
import game.emotionlanes.model.UnitType;
import game.emotionlanes.world.EmotionLanesWorldData;

public class SpawnManager {

    public void spawnDefault(LanesState state, EmotionLanesWorldData data) {
        Position[] hs = data.getHeroSpawns();
        Position[] ms = data.getMonsterSpawns();

        // H1/H2/H3
        for (int i = 0; i < hs.length; i++) {
            state.getHeroes().add(new LaneUnit("H" + (i + 1), UnitType.HERO, hs[i]));
        }
        // M1/M2/M3
        for (int i = 0; i < ms.length; i++) {
            state.getMonsters().add(new LaneUnit("M" + (i + 1), UnitType.MONSTER, ms[i]));
        }
    }
}
