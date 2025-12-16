package game.emotionlanes.logic;

import game.core.model.Hero;
import game.core.world.Position;
import game.emotionlanes.model.LaneUnit;
import game.emotionlanes.terrain.TerrainEffectManager;
import game.emotionlanes.world.EmotionLanesWorldData;

/**
 * Handles round lifecycle mechanics for Emotion Lanes.
 *
 * Responsibilities:
 *  - Start-of-round: respawn dead heroes at their nexus with full HP/MP.
 *  - End-of-round: apply regeneration / upkeep to living heroes (HP/MP regen per rules).
 *  - Ensure terrain effects are refreshed correctly after teleports/respawns via TerrainEffectManager.
 */

public class RoundSystem {

    private final TerrainEffectManager terrain;
    private final EmotionLanesWorldData data;

    public RoundSystem(TerrainEffectManager terrain, EmotionLanesWorldData data) {
        this.terrain = terrain;
        this.data = data;
    }

    /** Start of round: respawn dead heroes at their lane-specific Nexus spawn. */
    public void startOfRoundRespawns(LanesState state) {
        Position[] spawns = data.getHeroSpawns();
        if (spawns == null) return;

        for (int i = 0; i < state.getHeroes().size(); i++) {
            LaneUnit u = state.getHeroes().get(i);
            if (u.isAlive()) continue;

            Hero h = u.getHero();
            if (h == null) continue;

            Position spawn = spawns[Math.min(i, spawns.length - 1)];

            // full restore + mark alive
            h.setHp(h.getStats().getMaxHp());
            h.setMp(h.getStats().getMaxMp());
            u.setAlive(true);

            Position old = u.getPos();
            u.setPos(spawn);

            // make sure terrain buffs refresh correctly
            terrain.onMove(u, old, spawn);
        }
    }

    /** End of round: alive heroes regain 10% HP and 10% MP. */
    public void endOfRoundRegen(LanesState state) {
        for (LaneUnit u : state.getHeroes()) {
            if (!u.isAlive()) continue;

            Hero h = u.getHero();
            if (h == null) continue;

            int maxHp = h.getStats().getMaxHp();
            int maxMp = h.getStats().getMaxMp();

            int hpGain = Math.max(1, (int)Math.ceil(maxHp * 0.10));
            int mpGain = Math.max(1, (int)Math.ceil(maxMp * 0.10));

            h.healFlat(hpGain);
            h.restoreMpFlat(mpGain);
        }
    }
}
