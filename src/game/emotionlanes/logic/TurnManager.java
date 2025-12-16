package game.emotionlanes.logic;

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

    public boolean tryMoveHero(World world, LanesState state, LaneUnit hero, char dir) {
        Position cur = hero.getPos();
        Position next = step(cur, dir);
        if (next == null) return false;

        if (!world.getTile(next).isAccessible()) return false;

        // no hero-on-hero stacking
        if (isOccupiedByHero(state, next)) return false;

        // cannot move past the nearest monster in that lane (heroes move UP)
        if (!heroMoveLegalWrtMonsters(state, next)) return false;

        Position old = hero.getPos();
        hero.setPos(next);
        terrain.onMove(hero, old, next);

        return true;
    }

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

    // ---- Monster phase: if engaged, attack; else move down ----
    public void monstersAct(World world, LanesState state) {
        List<LaneUnit> monsters = state.getMonsters();

        for (int i = 0; i < monsters.size(); i++) {
            LaneUnit m = monsters.get(i);
            if (!m.isAlive()) continue;

            LaneUnit engagedHero = heroOn(state, m.getPos());
            if (engagedHero != null) {
                monsterAttack(engagedHero, m);
                continue;
            }

            Position down = m.getPos().down();
            if (monsterMoveValid(world, state, down)) {
                Position old = m.getPos();
                m.setPos(down);
                terrain.onMove(m, old, down);
            } else {
                // fallback sideways
                Position left = m.getPos().left();
                Position right = m.getPos().right();

                Position chosen = rng.nextBoolean() ? left : right;
                Position alt    = (chosen == left) ? right : left;

                if (monsterMoveValid(world, state, chosen)) {
                    Position old = m.getPos();
                    m.setPos(chosen);
                    terrain.onMove(m, old, chosen);
                } else if (monsterMoveValid(world, state, alt)) {
                    Position old = m.getPos();
                    m.setPos(alt);
                    terrain.onMove(m, old, alt);
                }
            }

            // if it moved onto a hero, attack immediately
            LaneUnit nowEngaged = heroOn(state, m.getPos());
            if (nowEngaged != null) {
                monsterAttack(nowEngaged, m);
            }
        }
    }

    // ---- Engagement helpers ----
    public LaneUnit engagedMonsterForHero(LanesState state, LaneUnit hero) {
        return monsterOn(state, hero.getPos());
    }

    // ---- Combat: one action, not a full loop ----
    public void heroAttack(LaneUnit hero, LaneUnit monster) {
        if (hero == null || monster == null) return;
        if (!hero.isAlive() || !monster.isAlive()) return;

        System.out.println("⚔ " + hero.getId() + " attacks " + monster.getId() + "!");

        if (rng.nextDouble() < monster.effectiveDodge()) {
            System.out.println(monster.getId() + " dodges!");
            return;
        }

        monster.takeHit(hero.effectiveAttack());

        if (!monster.isAlive()) {
            System.out.println("✅ " + monster.getId() + " defeated.");
        } else {
            System.out.println(monster.getId() + " HP: " + monster.hpString());
        }
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

        if (!hero.isAlive()) {
            System.out.println("💀 " + hero.getId() + " fell.");
        } else {
            System.out.println(hero.getId() + " HP: " + hero.hpString());
        }
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


    // ---- Rules: cannot move past monsters (heroes move UP) ----
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

        // cannot move to a row ABOVE the nearest monster row
        return dest.row >= nearestMonsterRow;
    }

    // Monsters can share with heroes, but not with other monsters
    private boolean monsterMoveValid(World world, LanesState state, Position dest) {
        if (!world.getTile(dest).isAccessible()) return false;
        for (LaneUnit m : state.getMonsters()) {
            if (m.isAlive() && m.getPos().equals(dest)) return false;
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
        if (dir == 'W' || dir == 'w') return cur.up();
        if (dir == 'S' || dir == 's') return cur.down();
        if (dir == 'A' || dir == 'A') return cur.left();
        if (dir == 'D' || dir == 'd') return cur.right();
        return null;
    }
}
