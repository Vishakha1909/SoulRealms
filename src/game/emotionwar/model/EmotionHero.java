package game.emotionwar.model;

import game.core.model.Hero;
import game.core.model.Stats;

public class EmotionHero extends Hero {
    private EmotionHeroType heroClass;

    public EmotionHero(String name,
                       EmotionHeroType heroClass,
                       int level,
                       Stats stats,
                       int gold) {
        super(name, level, stats, gold);
        this.heroClass = heroClass;
    }

    public EmotionHeroType getHeroClass() {
        return heroClass;
    }

    @Override
    public String toString() {
        return getName() + " (" + heroClass + ", Lvl " + level + ") " + stats + " Gold=" + gold;
    }
}
