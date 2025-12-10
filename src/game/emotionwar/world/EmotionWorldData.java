package game.emotionwar.world;

import game.core.world.World;
import game.emotionwar.model.EmotionType;

public class EmotionWorldData {

    private final World world;
    private final EmotionType[][] emotionLayer;

    public EmotionWorldData(World world, EmotionType[][] emotionLayer) {
        this.world = world;
        this.emotionLayer = emotionLayer;
    }

    public World getWorld() {
        return world;
    }

    public EmotionType[][] getEmotionLayer() {
        return emotionLayer;
    }
}
