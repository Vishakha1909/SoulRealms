package app;

import java.util.Scanner;

import game.core.game.Game;
import game.emotionlanes.EmotionLanesGame;
import game.emotionwar.EmotionWarGame;

public class ArcadeApp {

    private final Scanner scanner = new Scanner(System.in);
    private final ConsoleUI ui = new ConsoleUI(scanner);

    public void run() {
        boolean running = true;

        while (running) {
            ui.clear();
            ui.printHeader("SOUL REALMS ARCADE");

            System.out.println("1) Emotion War (Open World)");
            System.out.println("2) Emotion Lanes (Defense of the Core)");
            System.out.println("3) Quit");

            int choice = ui.readInt("> ", 1, 3);

            if (choice == 1) {
                playGame(new EmotionWarGame());
            } else if (choice == 2) {
                playGame(new EmotionLanesGame());
            } else {
                running = false;
            }
        }

        System.out.println("Bye.");
    }

    private void playGame(Game game) {
        ui.clear();
        game.init();
        game.run();
        ui.pause();
    }
}
