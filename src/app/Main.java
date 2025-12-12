package app;

import java.util.Arrays;
import java.util.List;
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
                System.out.println("Which game would you like to play?");
                System.out.println("1) Legends of Valor (Lane based, destroy the nexus)");
                System.out.println("2) Legends and Monsters (Open world exploration)");
                System.out.print("> ");
                String gameChoice = getInput(Arrays.asList("1","2"));
                GameController game;
                if (gameChoice.equals("1")) {
                    game = new GameController("Lanes");
                } else {
                    game = new GameController("Grid");
                }
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

    private static String getInput(List<String> options){
        boolean done = false;
        String choice = "";

        Scanner scanner = new Scanner(System.in);
        while (!done) {
            choice = scanner.next();

            for (String option : options) {
                if (option.equals(choice)) {
                    done = true;
                    break;
                }
            }
            if (!done) {
                System.out.println("Please enter a valid choice");
            }
        }
        return choice;
    }
}
