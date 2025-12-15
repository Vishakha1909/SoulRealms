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
            LaneUnit h = new LaneUnit("H" + (i + 1), UnitType.HERO, hs[i]);
            // slight variety
            if (i == 0) { h.setHp(36); h.setAtk(7); h.setDef(3); }
            if (i == 1) { h.setHp(30); h.setAtk(9); h.setDef(2); }
            if (i == 2) { h.setHp(28); h.setAtk(10); h.setDef(1); }
            state.getHeroes().add(h);
        }

        // M1/M2/M3
        for (int i = 0; i < ms.length; i++) {
            LaneUnit m = new LaneUnit("M" + (i + 1), UnitType.MONSTER, ms[i]);
            // monsters are slightly tanky so engagements matter
            m.setHp(26);
            m.setAtk(8);
            m.setDef(1);
            state.getMonsters().add(m);
        }
    }
}
