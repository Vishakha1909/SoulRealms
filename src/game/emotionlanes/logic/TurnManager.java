package game.emotionlanes.logic;

import java.util.List;
import java.util.Random;

import game.core.world.Position;
import game.core.world.World;
import game.emotionlanes.model.LaneUnit;

public class TurnManager {

    private final Random rng = new Random();

    public boolean tryMoveUnit(World world, LaneUnit u, char dir) {
        Position cur = u.getPos();
        Position next = cur;

        if (dir == 'W') next = cur.up();
        else if (dir == 'S') next = cur.down();
        else if (dir == 'A') next = cur.left();
        else if (dir == 'D') next = cur.right();
        else return false;

        // IMPORTANT: We do NOT move the shared "partyPosition" in World.
        // We just validate accessibility using world.getTile(next).
        if (!world.getTile(next).isAccessible()) {
            return false;
        }

        u.setPos(next);
        return true;
    }

    public void monstersAdvance(World world, List<LaneUnit> monsters) {
        for (LaneUnit m : monsters) {
            if (!m.isAlive()) continue;

            // monsters try to go DOWN (toward hero nexus row)
            Position next = m.getPos().down();
            if (world.getTile(next).isAccessible()) {
                m.setPos(next);
            } else {
                // tiny fallback: try sideways random to stay in lane-ish
                if (rng.nextBoolean()) {
                    Position left = m.getPos().left();
                    if (world.getTile(left).isAccessible()) m.setPos(left);
                } else {
                    Position right = m.getPos().right();
                    if (world.getTile(right).isAccessible()) m.setPos(right);
                }
            }
        }
    }
}
