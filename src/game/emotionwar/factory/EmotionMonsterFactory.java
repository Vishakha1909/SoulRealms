package game.emotionwar.factory;

import game.core.model.Stats;
import game.emotionwar.model.EmotionMonster;
import game.emotionwar.model.EmotionType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EmotionMonsterFactory {
    private static final Map<EmotionType, List<EmotionMonster>> MONSTERS_BY_EMOTION =
            new EnumMap<EmotionType, List<EmotionMonster>>(EmotionType.class);
    private static final Random RNG = new Random();

    static {
        for (EmotionType e : EmotionType.values()) {
            MONSTERS_BY_EMOTION.put(e, new ArrayList<EmotionMonster>());
        }
    }

    public static void loadMonstersForEmotion(String path, EmotionType emotionType) {
        List<EmotionMonster> list = MONSTERS_BY_EMOTION.get(emotionType);
        for (String line : FileUtils.readLines(path)) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            // name,level,maxHp,maxMp,str,dex,agi,baseDamage,defense,dodge
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
            EmotionMonster m = new EmotionMonster(name, emotionType, level, stats,
                    baseDmg, defense, dodge);
            list.add(m);
        }
    }

    public static void loadAllDefaultMonsters() {
        loadMonstersForEmotion(DataPaths.MONSTER_WRATH,    EmotionType.WRATH);
        loadMonstersForEmotion(DataPaths.MONSTER_FEAR,     EmotionType.FEAR);
        loadMonstersForEmotion(DataPaths.MONSTER_SORROW,   EmotionType.SORROW);
        loadMonstersForEmotion(DataPaths.MONSTER_ANXIETY,  EmotionType.ANXIETY);
        loadMonstersForEmotion(DataPaths.MONSTER_ENVY,     EmotionType.ENVY);
        loadMonstersForEmotion(DataPaths.MONSTER_DESIRE,   EmotionType.DESIRE);
        loadMonstersForEmotion(DataPaths.MONSTER_PRIDE, EmotionType.PRIDE);
    }

    public static EmotionMonster randomForEmotion(EmotionType emotionType) {
        List<EmotionMonster> list = MONSTERS_BY_EMOTION.get(emotionType);
        if (list == null || list.isEmpty()) {
            throw new IllegalStateException("No monsters defined for " + emotionType);
        }
        return cloneMonster(list.get(RNG.nextInt(list.size())));
    }

    public static List<EmotionMonster> mixedEncounter() {
        List<EmotionMonster> encounter = new ArrayList<EmotionMonster>();
        EmotionType[] types = {EmotionType.WRATH, EmotionType.FEAR, EmotionType.ENVY};
        for (EmotionType t : types) {
            List<EmotionMonster> list = MONSTERS_BY_EMOTION.get(t);
            if (list != null && !list.isEmpty()) {
                encounter.add(randomForEmotion(t));
            }
        }
        return encounter;
    }

    private static EmotionMonster cloneMonster(EmotionMonster proto) {
        Stats s = proto.getStats();
        Stats copyStats = new Stats(s.getMaxHp(), s.getMaxMp(),
                s.getStrength(), s.getDexterity(), s.getAgility());
        return new EmotionMonster(proto.getName(), proto.getEmotionType(), proto.getLevel(),
                copyStats, proto.getBaseDamage(), proto.getDefense(), proto.getDodgeChance());
    }
}
