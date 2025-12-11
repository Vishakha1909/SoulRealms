package game.emotionlanes.world;

import game.core.world.World;
import game.core.world.Position;

/**
 * Bundle returned by EmotionLanesWorldBuilder.
 * Keeps the core World plus extra layers needed only by Emotion Lanes.
 */
public class EmotionLanesWorldData {

    private final World world;
    private final EmotionLanesTerrainType[][] terrainLayer;
    private final NexusType[][] nexusLayer;

    // Optional convenience: fixed spawn points for 3 heroes / 3 monsters.
    private final Position[] heroSpawns;
    private final Position[] monsterSpawns;

    public EmotionLanesWorldData(World world,
                                 EmotionLanesTerrainType[][] terrainLayer,
                                 NexusType[][] nexusLayer,
                                 Position[] heroSpawns,
                                 Position[] monsterSpawns) {
        this.world = world;
        this.terrainLayer = terrainLayer;
        this.nexusLayer = nexusLayer;
        this.heroSpawns = heroSpawns;
        this.monsterSpawns = monsterSpawns;
    }

    public World getWorld() {
        return world;
    }

    public EmotionLanesTerrainType[][] getTerrainLayer() {
        return terrainLayer;
    }

    public NexusType[][] getNexusLayer() {
        return nexusLayer;
    }

    public Position[] getHeroSpawns() {
        return heroSpawns;
    }

    public Position[] getMonsterSpawns() {
        return monsterSpawns;
    }
}
