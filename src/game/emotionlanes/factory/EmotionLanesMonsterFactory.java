package game.emotionlanes.factory;

import game.core.model.Stats;
import game.core.world.Position;
import game.emotionlanes.model.EmotionLanesMonster;
import game.emotionwar.factory.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Uses LaneMonster which extends core Monster class.
 */
public class EmotionLanesMonsterFactory {
    private static final List<EmotionLanesMonster> MONSTERS = new ArrayList<EmotionLanesMonster>();
    private static final Random RNG = new Random();

    /**
     * Loads monsters from a data file.
     * Format: name,level,maxHp,maxMp,str,dex,agi,baseDamage,defense,dodge
     * 
     * @param path Path to monster data file
     */
    public static void loadMonsters(String path) {
        for (String line : FileUtils.readLines(path)) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            String[] p = line.split(",");
            if (p.length < 10) continue;

            String name  = p[0].trim();
            int level    = Integer.parseInt(p[1].trim());
            int maxHp    = Integer.parseInt(p[2].trim());
            int maxMp    = Integer.parseInt(p[3].trim());
            int str      = Integer.parseInt(p[4].trim());
            int dex      = Integer.parseInt(p[5].trim());
            int agi      = Integer.parseInt(p[6].trim());
            int baseDmg  = Integer.parseInt(p[7].trim());
            int defense  = Integer.parseInt(p[8].trim());
            double dodge = Double.parseDouble(p[9].trim());

            Stats stats = new Stats(maxHp, maxMp, str, dex, agi);
            EmotionLanesMonster m = new EmotionLanesMonster(name, level, stats, baseDmg, defense, dodge,
                    new Position(0, 0), 0);
            MONSTERS.add(m);
        }
    }

    /**
     * Gets a random monster of the specified level.
     * 
     * @param level Desired monster level
     * @param position Position for the new monster
     * @param laneId Lane ID for the new monster
     * @return Cloned monster at that level with position and lane
     */
    public static EmotionLanesMonster randomForLevel(int level, Position position, int laneId) {
        List<EmotionLanesMonster> candidates = new ArrayList<EmotionLanesMonster>();
        for (EmotionLanesMonster m : MONSTERS) {
            if (m.getLevel() == level) {
                candidates.add(m);
            }
        }
        
        if (candidates.isEmpty()) {
            throw new IllegalStateException("No monsters defined for level " + level);
        }
        
        return cloneMonster(candidates.get(RNG.nextInt(candidates.size())), position, laneId);
    }

    /**
     * Creates a copy of a monster (for cloning when needed).
     * 
     * @param proto Original monster
     * @param position Position for the new monster
     * @param laneId Lane ID for the new monster
     * @return New monster with copied stats
     */
    private static EmotionLanesMonster cloneMonster(EmotionLanesMonster proto, Position position, int laneId) {
        Stats s = proto.getStats();
        Stats copyStats = new Stats(s.getMaxHp(), s.getMaxMp(),
                s.getStrength(), s.getDexterity(), s.getAgility());
        return new EmotionLanesMonster(proto.getName(), proto.getLevel(),
                copyStats, proto.getBaseDamage(), proto.getDefense(), proto.getRawDodgeChance(),
                position, laneId);
    }
}
