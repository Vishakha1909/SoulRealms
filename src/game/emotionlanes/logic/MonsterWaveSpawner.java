package game.emotionlanes.logic;

import game.core.world.Position;
import game.emotionlanes.factory.EmotionLanesMonsterFactory;
import game.emotionlanes.model.LaneUnit;
import game.emotionlanes.model.UnitType;
import game.emotionlanes.terrain.TerrainEffectManager;
import game.emotionlanes.world.EmotionLanesWorldData;

public class MonsterWaveSpawner {

    private final TerrainEffectManager terrain;
    private final EmotionLanesWorldData data;

    public MonsterWaveSpawner(TerrainEffectManager terrain, EmotionLanesWorldData data) {
        this.terrain = terrain;
        this.data = data;
    }

    /** Spawn one monster per lane if round is a multiple of interval. */
    public void spawnWaveIfDue(int round, int interval, LanesState state, int monsterLevel) {
        if (interval <= 0) return;
        if (round % interval != 0) return;

        Position[] spawns = data.getMonsterSpawns();
        if (spawns == null || spawns.length < 3) return;

        for (int lane = 0; lane < 3; lane++) {
            Position spawn = chooseFreeSpawnForLane(spawns[lane], state);
            if (spawn == null) continue;

            String id = "M" + state.nextMonsterId();
            LaneUnit m = new LaneUnit(id, UnitType.MONSTER, spawn);

            m.attachMonster(EmotionLanesMonsterFactory.randomForLevel(monsterLevel, spawn, lane));
            state.getMonsters().add(m);
            terrain.onSpawn(m);
        }
    }

    private Position chooseFreeSpawnForLane(Position preferred, LanesState state) {
        if (!monsterOn(state, preferred)) return preferred;

        // swap within the lane's 2 columns
        int r = preferred.row;
        int c = preferred.col;

        int altC = c;
        if (c == 0) altC = 1;
        else if (c == 1) altC = 0;
        else if (c == 3) altC = 4;
        else if (c == 4) altC = 3;
        else if (c == 6) altC = 7;
        else if (c == 7) altC = 6;

        Position alt = new Position(r, altC);
        if (!monsterOn(state, alt)) return alt;

        return null;
    }

    private boolean monsterOn(LanesState state, Position p) {
        for (LaneUnit m : state.getMonsters()) {
            if (m.isAlive() && m.getPos().equals(p)) return true;
        }
        return false;
    }
}
