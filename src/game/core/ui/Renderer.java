package game.core.ui;

import game.core.world.Position;

import java.util.Map;

public interface Renderer {

    public void render(Map<Position, String> heroTokens,
                       Map<Position, String> monsterTokens);

    public void render();
}
