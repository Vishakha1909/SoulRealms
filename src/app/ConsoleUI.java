package app;

import java.util.Scanner;

/**
 * Shared console utilities for all games.
 * Keeps Main and Game classes clean.
 */
public class ConsoleUI {

    private final Scanner scanner;

    public ConsoleUI(Scanner scanner) {
        this.scanner = scanner;
    }

    /* ---------- Screen control ---------- */

    public void clear() {
        for (int i = 0; i < 25; i++) {
            System.out.println();
        }
    }

    public void pause() {
        System.out.println("\nPress ENTER to continue...");
        scanner.nextLine();
    }

    /* ---------- Input helpers ---------- */

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int v = Integer.parseInt(line);
                if (v < min || v > max) {
                    System.out.println("Enter a number between " + min + " and " + max + ".");
                } else {
                    return v;
                }
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
            }
        }
    }

    public boolean confirm(String prompt) {
        System.out.print(prompt + " (y/n): ");
        String s = scanner.nextLine().trim().toLowerCase();
        return s.startsWith("y");
    }

    /* ---------- Headers ---------- */

    public void printHeader(String title) {
        System.out.println("========================================");
        System.out.println(" " + title);
        System.out.println("========================================");
    }
}
