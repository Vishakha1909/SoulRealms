package game.emotionlanes.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.core.world.Position;
import game.core.world.World;
import game.emotionlanes.model.LaneUnit;
import game.emotionlanes.terrain.TerrainEffectManager;

public class TurnManager {

    private final Random rng = new Random();
    private final TerrainEffectManager terrain;

    public TurnManager(TerrainEffectManager terrain) {
        this.terrain = terrain;
    }

    // lanes: [0,1], [3,4], [6,7]
    private int laneIndex(int col) {
        if (col == 0 || col == 1) return 0;
        if (col == 3 || col == 4) return 1;
        if (col == 6 || col == 7) return 2;
        return -1;
    }

    // ---------------- HERO MOVE ----------------
    public boolean tryMoveHero(World world, LanesState state, LaneUnit hero, char dir) {
        Position cur = hero.getPos();
        Position next = step(cur, dir);
        if (next == null) return false;

        if (!world.getTile(next).isAccessible()) return false;
        if (terrain.isObstacle(next)) return false; // cannot enter obstacle

        // no hero-on-hero stacking
        if (isOccupiedByHero(state, next)) return false;

        // cannot move past nearest monster in that lane (heroes move UP)
        if (!heroMoveLegalWrtMonsters(state, next)) return false;

        Position old = hero.getPos();
        hero.setPos(next);
        terrain.onMove(hero, old, next);

        return true;
    }

    // ---------------- TELEPORT ----------------
    public boolean tryTeleportHero(World world, LanesState state, LaneUnit hero, LaneUnit targetHero) {
        if (hero == null || targetHero == null) return false;
        if (!hero.isAlive() || !targetHero.isAlive()) return false;
        if (hero == targetHero) return false;

        int heroLane = laneIndex(hero.getPos().col);
        int targetLane = laneIndex(targetHero.getPos().col);
        if (heroLane == -1 || targetLane == -1) return false;
        if (heroLane == targetLane) return false; // must be across lanes

        Position[] candidates = new Position[] {
            targetHero.getPos().up(),
            targetHero.getPos().down(),
            targetHero.getPos().left(),
            targetHero.getPos().right()
        };

        for (int i = 0; i < candidates.length; i++) {
            Position dest = candidates[i];
            if (!world.getTile(dest).isAccessible()) continue;
            if (terrain.isObstacle(dest)) continue;
            if (laneIndex(dest.col) != targetLane) continue;
            if (isOccupiedByHero(state, dest)) continue;

            // cannot teleport ahead of ally (ahead = smaller row)
            if (dest.row < targetHero.getPos().row) continue;

            // cannot teleport past monsters in that lane
            if (!heroMoveLegalWrtMonsters(state, dest)) continue;

            Position old = hero.getPos();
            hero.setPos(dest);
            terrain.onMove(hero, old, dest);

            return true;
        }

        return false;
    }

    // ---------------- MONSTER PHASE ----------------
    public List<String>  monstersAct(World world, LanesState state) {
    List<LaneUnit> monsters = state.getMonsters();
    List<String> log = new ArrayList<>();

    for (int i = 0; i < monsters.size(); i++) {
        LaneUnit m = monsters.get(i);
        if (!m.isAlive()) continue;

        // 1) if any hero in attack range, attack (use your helper)
        LaneUnit target = heroInAttackRange(state, m);
        if (target != null) {
            log.add(m.getId() + " attacks " + target.getId() + " at " + target.getPos());
            monsterAttack(target, m);
            continue;
        }

        // 2) otherwise move forward/down (or sideways fallback)
        Position old = m.getPos();

        Position down = old.down();
        if (monsterMoveValid(world, state, down)) {
            m.setPos(down);
            terrain.onMove(m, old, down);
            log.add(m.getId() + " moves DOWN: " + old + " -> " + down);
        } else {
            Position left = old.left();
            Position right = old.right();

            Position chosen = rng.nextBoolean() ? left : right;
            Position alt    = (chosen == left) ? right : left;

            if (monsterMoveValid(world, state, chosen)) {
                m.setPos(chosen);
                terrain.onMove(m, old, chosen);
                log.add(m.getId() + " moves SIDE: " + old + " -> " + chosen);
            } else if (monsterMoveValid(world, state, alt)) {
                m.setPos(alt);
                terrain.onMove(m, old, alt);
                log.add(m.getId() + " moves SIDE: " + old + " -> " + alt);
            } else {
                log.add(m.getId() + " is STUCK at " + old);
            }
        }

        // 3) after moving, if now in range, attack immediately (optional but feels good)
        LaneUnit afterMoveTarget = heroInAttackRange(state, m);
        if (afterMoveTarget != null) {
            log.add(m.getId() + " now attacks " + afterMoveTarget.getId() + " at " + afterMoveTarget.getPos());
            monsterAttack(afterMoveTarget, m);
        }
    }

    System.out.println("=========================");
    return log;
}

private LaneUnit heroInAttackRange(LanesState state, LaneUnit monster) {
    if (monster == null || !monster.isAlive()) return null;

    Position mp = monster.getPos();
    int lane = laneIndex(mp.col);
    if (lane == -1) return null;

    for (LaneUnit h : state.getHeroes()) {
        if (!h.isAlive()) continue;
        if (laneIndex(h.getPos().col) != lane) continue;

        if (inAttackRange(mp, h.getPos())) return h; // your existing helper
    }
    return null;
}



    // ---------------- OBSTACLES ----------------
    public boolean hasAdjacentObstacle(char[][] glyph, Position p) {
        if (glyph == null || p == null) return false;

        Position[] adj = new Position[]{ p.up(), p.down(), p.left(), p.right() };
        for (int i = 0; i < adj.length; i++) {
            Position q = adj[i];
            if (q == null) continue;
            if (q.row < 0 || q.row >= glyph.length) continue;
            if (q.col < 0 || q.col >= glyph[0].length) continue;
            if (glyph[q.row][q.col] == 'O') return true;
        }
        return false;
    }

    /** Removes an adjacent obstacle in the chosen direction (W/A/S/D). Counts as the hero’s action. */
    public boolean tryRemoveObstacleAdjacent(LaneUnit hero, char dir) {
        if (hero == null || !hero.isAlive()) return false;
        Position target = step(hero.getPos(), dir);
        if (target == null) return false;

        boolean ok = terrain.removeObstacle(target);
        if (ok) System.out.println("🧱 Obstacle removed at " + target + "!");
        return ok;
    }

    // ---------------- ENGAGEMENT + COMBAT ----------------
    public LaneUnit engagedMonsterForHero(LanesState state, LaneUnit hero) {
        if (hero == null || !hero.isAlive()) return null;

        Position hp = hero.getPos();
        int lane = laneIndex(hp.col);
        if (lane == -1) return null;

        for (LaneUnit m : state.getMonsters()) {
            if (!m.isAlive()) continue;
            if (laneIndex(m.getPos().col) != lane) continue;

            if (inAttackRange(hp, m.getPos())) return m;
        }
        return null;
    }

    private boolean inAttackRange(Position a, Position b) {
        int dr = Math.abs(a.row - b.row);
        int dc = Math.abs(a.col - b.col);
        return (dr + dc) <= 1;   // current OR 4-neighbor
    }

    // Monsters attack heroes if a hero is IN RANGE (same lane + distance <= 1)
    private LaneUnit heroInRangeForMonster(LanesState state, LaneUnit monster) {
        if (monster == null || !monster.isAlive()) return null;

        Position mp = monster.getPos();
        int lane = laneIndex(mp.col);
        if (lane == -1) return null;

        for (LaneUnit h : state.getHeroes()) {
            if (!h.isAlive()) continue;
            if (laneIndex(h.getPos().col) != lane) continue;

            if (inAttackRange(mp, h.getPos())) return h;
        }
        return null;
    }



    public void heroAttack(LaneUnit hero, LaneUnit monster) {
        if (hero == null || monster == null) return;
        if (!hero.isAlive() || !monster.isAlive()) return;

        System.out.println("⚔ " + hero.getId() + " attacks " + monster.getId() + "!");

        if (rng.nextDouble() < monster.effectiveDodge()) {
            System.out.println(monster.getId() + " dodges!");
            return;
        }

        monster.takeHit(hero.effectiveAttack());

        if (!monster.isAlive()) System.out.println("✅ " + monster.getId() + " defeated.");
        else System.out.println(monster.getId() + " HP: " + monster.hpString());
    }

    public void monsterAttack(LaneUnit hero, LaneUnit monster) {
        if (hero == null || monster == null) return;
        if (!hero.isAlive() || !monster.isAlive()) return;

        System.out.println("👹 " + monster.getId() + " strikes " + hero.getId() + "!");

        if (rng.nextDouble() < hero.effectiveDodge()) {
            System.out.println(hero.getId() + " dodges!");
            return;
        }

        hero.takeHit(monster.effectiveAttack());

        if (!hero.isAlive()) System.out.println("💀 " + hero.getId() + " fell.");
        else System.out.println(hero.getId() + " HP: " + hero.hpString());
    }

    // ---------------- RULES HELPERS ----------------
    private boolean heroMoveLegalWrtMonsters(LanesState state, Position dest) {
        int lane = laneIndex(dest.col);
        if (lane == -1) return false;

        int nearestMonsterRow = Integer.MAX_VALUE;
        for (LaneUnit m : state.getMonsters()) {
            if (!m.isAlive()) continue;
            if (laneIndex(m.getPos().col) != lane) continue;
            if (m.getPos().row < nearestMonsterRow) nearestMonsterRow = m.getPos().row;
        }
        if (nearestMonsterRow == Integer.MAX_VALUE) return true;

        // cannot move above nearest monster
        return dest.row >= nearestMonsterRow;
    }

    private boolean monsterMoveValid(World world, LanesState state, Position dest) {
        if (!world.getTile(dest).isAccessible()) return false;
        if (terrain.isObstacle(dest)) return false;

        for (LaneUnit m : state.getMonsters()) {
            if (m.isAlive() && m.getPos().equals(dest)) return false; // no stacking monsters
        }
        return true;
    }

    private boolean isOccupiedByHero(LanesState state, Position p) {
        for (LaneUnit h : state.getHeroes()) {
            if (h.isAlive() && h.getPos().equals(p)) return true;
        }
        return false;
    }

    private LaneUnit heroOn(LanesState state, Position p) {
        for (LaneUnit h : state.getHeroes()) {
            if (h.isAlive() && h.getPos().equals(p)) return h;
        }
        return null;
    }

    private LaneUnit monsterOn(LanesState state, Position p) {
        for (LaneUnit m : state.getMonsters()) {
            if (m.isAlive() && m.getPos().equals(p)) return m;
        }
        return null;
    }

    private Position step(Position cur, char dir) {
        if (cur == null) return null;
        if (dir == 'W' || dir == 'w') return cur.up();
        if (dir == 'S' || dir == 's') return cur.down();
        if (dir == 'A' || dir == 'a') return cur.left();   // FIXED
        if (dir == 'D' || dir == 'd') return cur.right();
        return null;
    }

    public boolean recallHero(LanesState state, LaneUnit hero) {
    if (hero == null || !hero.isAlive()) return false;

    Position home = hero.getHomeNexus();
    if (home == null) return false;

    for (LaneUnit h : state.getHeroes()) {
        if (h != hero && h.isAlive() && h.getPos().equals(home)) return false;
    }

    Position old = hero.getPos();
    hero.setPos(home);
    terrain.onMove(hero, old, home);

    // optional: if recalling onto a monster (shouldn't happen with your spawns),
    // you can allow engagement next round instead of auto-fighting.
    return true;
    }
}
