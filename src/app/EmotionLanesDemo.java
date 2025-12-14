package app;

import java.util.HashMap;
import java.util.Map;

import game.core.world.Position;
import game.emotionlanes.ui.EmotionLanesRenderer;
import game.emotionlanes.world.EmotionLanesWorldBuilder;
import game.emotionlanes.world.EmotionLanesWorldData;

public class EmotionLanesDemo {
    public static void main(String[] args) {
        EmotionLanesWorldData data = EmotionLanesWorldBuilder.buildDefaultWorld();
        System.out.println("DEBUG lookup H1:");
        EmotionLanesRenderer renderer = new EmotionLanesRenderer(data);

        Map<Position, String> heroes   = new HashMap<Position, String>();
        Map<Position, String> monsters = new HashMap<Position, String>();

        // One hero per lane at bottom
        heroes.put(new Position(7, 0), "H1");
        heroes.put(new Position(7, 3), "H2");
        heroes.put(new Position(7, 6), "H3");

        // One monster per lane at top
        monsters.put(new Position(0, 1), "M1");
        monsters.put(new Position(0, 4), "M2");
        monsters.put(new Position(0, 7), "M3");

        System.out.println("DEBUG lookup H1: " + heroes.get(new Position(7,0)));
System.out.println("DEBUG lookup M1: " + monsters.get(new Position(0,1)));


        renderer.render(heroes, monsters);
    }
}
