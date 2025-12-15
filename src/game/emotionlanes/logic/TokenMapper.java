package game.emotionlanes.logic;

import java.util.HashMap;
import java.util.Map;

import game.core.world.Position;
import game.emotionlanes.model.LaneUnit;

public class TokenMapper {

    public Map<Position, String> heroTokens(LanesState state) {
        Map<Position, String> m = new HashMap<Position, String>();
        for (LaneUnit u : state.getHeroes()) {
            if (u.isAlive()) m.put(u.getPos(), u.getId());
        }
        return m;
    }

    public Map<Position, String> monsterTokens(LanesState state) {
        Map<Position, String> m = new HashMap<Position, String>();
        for (LaneUnit u : state.getMonsters()) {
            if (u.isAlive()) m.put(u.getPos(), u.getId());
        }
        return m;
    }
}
