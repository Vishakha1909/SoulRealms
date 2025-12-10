package game.emotionwar.model;

import game.core.model.Monster;
import game.core.model.Stats;

public class EmotionMonster extends Monster {
    private EmotionType emotionType;

    public EmotionMonster(String name,
                          EmotionType emotionType,
                          int level,
                          Stats stats,
                          int baseDamage,
                          int defense,
                          double dodgeChance) {
        super(name, level, stats, baseDamage, defense, dodgeChance);
        this.emotionType = emotionType;
    }

    public EmotionType getEmotionType() {
        return emotionType;
    }

    @Override
    public String toString() {
        return getName() + " [" + emotionType + ", Lvl " + level + "] " + stats;
    }
}
