package game.emotionlanes.logic;

import java.util.ArrayList;
import java.util.List;

import game.core.world.World;
import game.core.world.Position;
import game.emotionlanes.model.LaneUnit;

/**
 * Shared mutable game state container for Emotion Lanes.
 *
 * Responsibilities:
 *  - Provide a single place for systems (TurnManager, spawners, UI) to read/update unit state.
 *  - Keep hero spawn mapping consistent (index i -> hero i’s nexus spawn).
 */

public class LanesState {

    private final World world;
    private final char[][] glyphLayer;

     private final Position[] heroSpawns;

    private final List<LaneUnit> heroes = new ArrayList<LaneUnit>();
    private final List<LaneUnit> monsters = new ArrayList<LaneUnit>();

    private int nextMonsterId = 4;

    public LanesState(World world, char[][] glyphLayer, Position[] heroSpawns) {
        this.world = world;
        this.glyphLayer = glyphLayer;
        this.heroSpawns = heroSpawns;
    }

    public World getWorld() { return world; }
    public char[][] getGlyphLayer() { return glyphLayer; }

    public List<LaneUnit> getHeroes() { return heroes; }
    public List<LaneUnit> getMonsters() { return monsters; }

    public int nextMonsterId() { return nextMonsterId++; }

    public Position[] getHeroSpawns() { return heroSpawns; }
}
