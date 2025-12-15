package game.emotionlanes.logic;

import java.util.ArrayList;
import java.util.List;

import game.core.world.World;
import game.emotionlanes.model.LaneUnit;

public class LanesState {

    private final World world;
    private final char[][] glyphLayer;

    private final List<LaneUnit> heroes = new ArrayList<LaneUnit>();
    private final List<LaneUnit> monsters = new ArrayList<LaneUnit>();

    public LanesState(World world, char[][] glyphLayer) {
        this.world = world;
        this.glyphLayer = glyphLayer;
    }

    public World getWorld() { return world; }
    public char[][] getGlyphLayer() { return glyphLayer; }

    public List<LaneUnit> getHeroes() { return heroes; }
    public List<LaneUnit> getMonsters() { return monsters; }
}
