package game.emotionwar.factory;

import game.core.model.Stats;
import game.emotionwar.model.EmotionHero;
import game.emotionwar.model.EmotionHeroType;

import java.util.ArrayList;
import java.util.List;

public class EmotionHeroFactory {

    public static List<EmotionHero> loadHeroesFromFile(String path, EmotionHeroType type) {
        List<EmotionHero> heroes = new ArrayList<EmotionHero>();
        for (String line : FileUtils.readLines(path)) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            // name,level,maxHp,maxMp,str,dex,agi,gold
            String[] p = line.split(",");
            if (p.length < 8) continue;

            String name = p[0].trim();
            int level = Integer.parseInt(p[1].trim());
            int maxHp = Integer.parseInt(p[2].trim());
            int maxMp = Integer.parseInt(p[3].trim());
            int str   = Integer.parseInt(p[4].trim());
            int dex   = Integer.parseInt(p[5].trim());
            int agi   = Integer.parseInt(p[6].trim());
            int gold  = Integer.parseInt(p[7].trim());

            Stats stats = new Stats(maxHp, maxMp, str, dex, agi);
            EmotionHero hero = new EmotionHero(name, type, level, stats, gold);
            heroes.add(hero);
        }
        return heroes;
    }

    public static List<EmotionHero> loadAllDefaultHeroes() {
        List<EmotionHero> all = new ArrayList<EmotionHero>();
        all.addAll(loadHeroesFromFile(DataPaths.HERO_RESOLUTE,  EmotionHeroType.RESOLUTE));
        all.addAll(loadHeroesFromFile(DataPaths.HERO_MINDSEER,  EmotionHeroType.MINDSEER));
        all.addAll(loadHeroesFromFile(DataPaths.HERO_SWIFTHEART, EmotionHeroType.SWIFTHEART));
        return all;
    }
}
