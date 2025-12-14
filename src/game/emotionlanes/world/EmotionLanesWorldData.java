package game.emotionlanes.world;

import game.core.world.Position;
import game.core.world.World;

/**
 * Simple data holder for Emotion Lanes.
 * We only care about:
 *  - World (for rows/cols and movement)
 *  - glyphLayer: char[][] with N/I/P/B/C/K
 *  - hero / monster spawn positions
 */
public class EmotionLanesWorldData {

    private final World world;
    private final char[][] glyphLayer;
    private final Position[] heroSpawns;
    private final Position[] monsterSpawns;

    public EmotionLanesWorldData(World world,
                                 char[][] glyphLayer,
                                 Position[] heroSpawns,
                                 Position[] monsterSpawns) {
        this.world = world;
        this.glyphLayer = glyphLayer;
        this.heroSpawns = heroSpawns;
        this.monsterSpawns = monsterSpawns;
    }

    public World getWorld() {
        return world;
    }

    public char[][] getGlyphLayer() {
        return glyphLayer;
    }

    public Position[] getHeroSpawns() {
        return heroSpawns;
    }

    public Position[] getMonsterSpawns() {
        return monsterSpawns;
    }
}
