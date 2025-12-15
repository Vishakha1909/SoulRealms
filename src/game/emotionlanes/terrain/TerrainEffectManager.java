package game.emotionlanes.terrain;

import java.util.HashMap;
import java.util.Map;

import game.core.world.Position;
import game.emotionlanes.model.LaneUnit;

public class TerrainEffectManager {

    private final char[][] glyphLayer;
    private final boolean allowMonsterBuffs;

    private final Map<Character, TerrainEffect> effects = new HashMap<Character, TerrainEffect>();

    // Track what glyph each unit currently 'has applied' so we can remove correctly.
    private final Map<String, Character> activeGlyphByUnitId = new HashMap<String, Character>();

    public TerrainEffectManager(char[][] glyphLayer, boolean allowMonsterBuffs) {
        this.glyphLayer = glyphLayer;
        this.allowMonsterBuffs = allowMonsterBuffs;

        effects.put(Character.valueOf('B'), new BushEffect());
        effects.put(Character.valueOf('C'), new CaveEffect());
        effects.put(Character.valueOf('K'), new KoulouEffect());
    }

    public void onSpawn(LaneUnit u) {
        applyForCurrentTile(u);
    }

    public void onMove(LaneUnit u, Position oldPos, Position newPos) {
        removeForOldTile(u);
        applyForCurrentTile(u);
    }

    private void removeForOldTile(LaneUnit u) {
        Character g = activeGlyphByUnitId.get(u.getId());
        if (g == null) return;

        TerrainEffect eff = effects.get(g);
        if (eff != null) eff.remove(u, allowMonsterBuffs);

        activeGlyphByUnitId.remove(u.getId());
    }

    private void applyForCurrentTile(LaneUnit u) {
        Position p = u.getPos();
        if (p.row < 0 || p.row >= glyphLayer.length) return;
        if (p.col < 0 || p.col >= glyphLayer[0].length) return;

        char g = glyphLayer[p.row][p.col];

        TerrainEffect eff = effects.get(Character.valueOf(g));
        if (eff != null) {
            eff.apply(u, allowMonsterBuffs);
            activeGlyphByUnitId.put(u.getId(), Character.valueOf(g));
        } else {
            activeGlyphByUnitId.remove(u.getId());
        }
    }
}
