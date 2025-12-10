package app;

import game.core.game.Game;
import game.emotionwar.EmotionWarGame;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("========================================");
            System.out.println("        Soul Realms: Emotion War        ");
            System.out.println("========================================");
            System.out.println("1) Start New Game");
            System.out.println("2) Quit");
            System.out.print("> ");

            String choice = scanner.nextLine().trim();

            if ("1".equals(choice)) {
                Game game = new EmotionWarGame();
                game.init();
                game.run();
                System.out.println();
                System.out.println("Returning to main menu...");
                System.out.println();
            } else if ("2".equals(choice)) {
                running = false;
            } else {
                System.out.println("Please choose 1 or 2.");
            }
        }

        System.out.println("Goodbye.");
        // (We do NOT close scanner here to avoid issues with System.in in some environments.)
    }
}
