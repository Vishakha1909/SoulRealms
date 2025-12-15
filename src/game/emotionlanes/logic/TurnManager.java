package game.emotionlanes.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.core.world.Position;
import game.core.world.World;
import game.emotionlanes.model.LaneUnit;

public class TurnManager {

    private final Random rng = new Random();

    // Lane separators must match your builder: walls at cols 2 and 5.
    // Lanes are [0,1], [3,4], [6,7].
    private static final int[] WALL_COLS = {2, 5};

    // ---------- Public API (what EmotionLanesGame calls) ----------

    public boolean tryMoveHero(World world, LanesState state, LaneUnit hero, char dir) {
        Position next = step(hero.getPos(), dir);
        if (next == null) return false;

        // blocked tile?
        if (!world.getTile(next).isAccessible()) return false;

        // cannot move onto another hero
        if (isOccupiedByHero(state, next)) return false;

        // cannot move "behind" a monster in that lane (heroes move UP)
        if (!isHeroPositionLegalWrtMonsters(state, hero, next)) return false;

        // move
        hero.setPos(next);

        // engagement check (battle if sharing tile)
        resolveEngagementIfAny(state, hero);

        return true;
    }

    public boolean tryTeleportHero(World world, LanesState state, LaneUnit hero, LaneUnit targetHero) {
        if (hero == null || targetHero == null) return false;
        if (!hero.isAlive() || !targetHero.isAlive()) return false;
        if (hero == targetHero) return false;

        int heroLane = laneIndex(hero.getPos().col);
        int targetLane = laneIndex(targetHero.getPos().col);
        if (heroLane == -1 || targetLane == -1) return false;

        // must teleport across lanes (not same lane)
        if (heroLane == targetLane) return false;

        // destination must be adjacent to targetHero (4-neighborhood)
        // and ALSO must land in the target lane (so it feels like "switching lanes")
        List<Position> candidates = new ArrayList<Position>();
        candidates.add(targetHero.getPos().up());
        candidates.add(targetHero.getPos().down());
        candidates.add(targetHero.getPos().left());
        candidates.add(targetHero.getPos().right());

        for (int i = 0; i < candidates.size(); i++) {
            Position dest = candidates.get(i);

            // must be in target lane columns
            if (laneIndex(dest.col) != targetLane) continue;

            if (!world.getTile(dest).isAccessible()) continue;
            if (isOccupiedByHero(state, dest)) continue;

            // cannot teleport "ahead of" the target hero:
            // heroes move UP, so "ahead" = smaller row than target
            if (dest.row < targetHero.getPos().row) continue;

            // cannot teleport behind a monster in that lane
            if (!isHeroPosLegalInLane(state, dest)) continue;

            hero.setPos(dest);
            resolveEngagementIfAny(state, hero);
            return true;
        }

        return false;
    }

    public void monstersAct(World world, LanesState state) {
        List<LaneUnit> monsters = state.getMonsters();

        for (int i = 0; i < monsters.size(); i++) {
            LaneUnit m = monsters.get(i);
            if (!m.isAlive()) continue;

            // if engaged with a hero, fight instead of moving
            LaneUnit engagedHero = heroOnSameTile(state, m.getPos());
            if (engagedHero != null) {
                fight(engagedHero, m);
                continue;
            }

            // monsters move DOWN (toward hero nexus)
            Position down = m.getPos().down();
            if (isMonsterMoveValid(world, state, m, down)) {
                m.setPos(down);
                // if this move engages a hero, fight immediately
                engagedHero = heroOnSameTile(state, m.getPos());
                if (engagedHero != null) fight(engagedHero, m);
                continue;
            }

            // fallback: attempt sideways inside lane (don’t cross walls)
            Position left = m.getPos().left();
            Position right = m.getPos().right();

            if (rng.nextBoolean()) {
                if (isMonsterMoveValid(world, state, m, left)) {
                    m.setPos(left);
                } else if (isMonsterMoveValid(world, state, m, right)) {
                    m.setPos(right);
                }
            } else {
                if (isMonsterMoveValid(world, state, m, right)) {
                    m.setPos(right);
                } else if (isMonsterMoveValid(world, state, m, left)) {
                    m.setPos(left);
                }
            }

            // fight if engagement after sideways
            engagedHero = heroOnSameTile(state, m.getPos());
            if (engagedHero != null) fight(engagedHero, m);
        }
    }

    // ---------- Combat / engagement ----------

    private void resolveEngagementIfAny(LanesState state, LaneUnit heroJustMoved) {
        LaneUnit m = monsterOnSameTile(state, heroJustMoved.getPos());
        if (m != null) {
            fight(heroJustMoved, m);
        }
    }

    // super simple deterministic fight (fast + safe)
    // (If you want, you can swap this later to call EmotionWar Battle using payloads)
    private void fight(LaneUnit hero, LaneUnit monster) {
        if (hero == null || monster == null) return;
        if (!hero.isAlive() || !monster.isAlive()) return;

        System.out.println("⚔  ENGAGEMENT: " + hero.getId() + " vs " + monster.getId());

        // one mini-round of trading blows until one dies
        while (hero.isAlive() && monster.isAlive()) {
            monster.takeDamage(hero.getAtk());
            if (!monster.isAlive()) break;
            hero.takeDamage(monster.getAtk());
        }

        if (!hero.isAlive()) {
            System.out.println("💀 " + hero.getId() + " fell.");
        }
        if (!monster.isAlive()) {
            System.out.println("✅ " + monster.getId() + " defeated.");
        }
    }

    // ---------- Legality rules ----------

    private boolean isMonsterMoveValid(World world, LanesState state, LaneUnit monster, Position next) {
        if (next == null) return false;

        // blocked tile?
        if (!world.getTile(next).isAccessible()) return false;

        // cannot move onto another monster
        if (isOccupiedByMonster(state, next)) return false;

        // cannot move behind a hero in that lane (monsters move DOWN)
        if (!isMonsterPositionLegalWrtHeroes(state, monster, next)) return false;

        return true;
    }

    // HERO RULE: cannot move to any row that is "ahead" of the nearest alive monster in that lane.
    // heroes move UP => smaller row is more ahead.
    private boolean isHeroPositionLegalWrtMonsters(LanesState state, LaneUnit hero, Position next) {
        int lane = laneIndex(next.col);
        if (lane == -1) return false;

        int nearestMonsterRow = nearestMonsterRowInLane(state, lane);
        if (nearestMonsterRow == Integer.MAX_VALUE) return true; // no monsters in that lane

        // illegal if hero would be ABOVE (ahead of) the monster
        // i.e., trying to go behind/past it
        return next.row >= nearestMonsterRow;
    }

    // used by teleport too (just checks lane blockade)
    private boolean isHeroPosLegalInLane(LanesState state, Position next) {
        int lane = laneIndex(next.col);
        if (lane == -1) return false;

        int nearestMonsterRow = nearestMonsterRowInLane(state, lane);
        if (nearestMonsterRow == Integer.MAX_VALUE) return true;
        return next.row >= nearestMonsterRow;
    }

    // MONSTER RULE: cannot move to any row that is "past" the nearest alive hero in that lane.
    // monsters move DOWN => larger row is more past.
    private boolean isMonsterPositionLegalWrtHeroes(LanesState state, LaneUnit monster, Position next) {
        int lane = laneIndex(next.col);
        if (lane == -1) return false;

        int nearestHeroRow = nearestHeroRowInLane(state, lane);
        if (nearestHeroRow == Integer.MIN_VALUE) return true; // no heroes in that lane

        // illegal if monster would be BELOW (past) the hero
        return next.row <= nearestHeroRow;
    }

    // ---------- Occupancy helpers ----------

    private boolean isOccupiedByHero(LanesState state, Position p) {
        for (LaneUnit h : state.getHeroes()) {
            if (h.isAlive() && h.getPos().equals(p)) return true;
        }
        return false;
    }

    private boolean isOccupiedByMonster(LanesState state, Position p) {
        for (LaneUnit m : state.getMonsters()) {
            if (m.isAlive() && m.getPos().equals(p)) return true;
        }
        return false;
    }

    private LaneUnit heroOnSameTile(LanesState state, Position p) {
        for (LaneUnit h : state.getHeroes()) {
            if (h.isAlive() && h.getPos().equals(p)) return h;
        }
        return null;
    }

    private LaneUnit monsterOnSameTile(LanesState state, Position p) {
        for (LaneUnit m : state.getMonsters()) {
            if (m.isAlive() && m.getPos().equals(p)) return m;
        }
        return null;
    }

    // ---------- Lane helpers ----------

    private int laneIndex(int col) {
        // lane0: 0-1, lane1: 3-4, lane2: 6-7
        if (col == 0 || col == 1) return 0;
        if (col == 3 || col == 4) return 1;
        if (col == 6 || col == 7) return 2;
        return -1;
    }

    private int nearestMonsterRowInLane(LanesState state, int lane) {
        int best = Integer.MAX_VALUE;
        for (LaneUnit m : state.getMonsters()) {
            if (!m.isAlive()) continue;
            int ml = laneIndex(m.getPos().col);
            if (ml != lane) continue;
            if (m.getPos().row < best) best = m.getPos().row;
        }
        return best;
    }

    private int nearestHeroRowInLane(LanesState state, int lane) {
        int best = Integer.MIN_VALUE;
        for (LaneUnit h : state.getHeroes()) {
            if (!h.isAlive()) continue;
            int hl = laneIndex(h.getPos().col);
            if (hl != lane) continue;
            if (h.getPos().row > best) best = h.getPos().row;
        }
        return best;
    }

    private Position step(Position cur, char dir) {
        if (cur == null) return null;
        if (dir == 'W') return cur.up();
        if (dir == 'S') return cur.down();
        if (dir == 'A') return cur.left();
        if (dir == 'D') return cur.right();
        return null;
    }
}
