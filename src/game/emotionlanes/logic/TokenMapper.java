package game.emotionlanes.logic;

import java.util.HashMap;
import java.util.Map;

import game.core.world.Position;
import game.emotionlanes.model.LaneUnit;

/**
 * Utility that converts current unit positions into renderable token maps.
 *
 * Produces:
 * - Map<Position, String> heroTokens
 * - Map<Position, String> monsterTokens
 */

public class TokenMapper {

    // holding positions of all alive heroes
    public Map<Position, String> heroTokens(LanesState state) {
        Map<Position, String> m = new HashMap<Position, String>();
        for (LaneUnit u : state.getHeroes()) {
            if (u.isAlive()) m.put(u.getPos(), u.getId());
        }
        return m;
    }

    // holding positions of all alive monsters
    public Map<Position, String> monsterTokens(LanesState state) {
        Map<Position, String> m = new HashMap<Position, String>();
        for (LaneUnit u : state.getMonsters()) {
            if (u.isAlive()) m.put(u.getPos(), u.getId());
        }
        return m;
    }
}
