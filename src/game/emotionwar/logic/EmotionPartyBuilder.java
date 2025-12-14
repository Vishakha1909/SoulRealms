package game.emotionwar.logic;

import game.core.model.Hero;
import game.emotionwar.model.EmotionHero;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmotionPartyBuilder {

    public List<Hero> buildParty(List<EmotionHero> allHeroes, Scanner scanner, String gameType) {
        List<Hero> party = new ArrayList<Hero>();

        System.out.println("========================================");
        System.out.println("   Soul Realms: Choose Your Fragments   ");
        System.out.println("========================================");
        int count;
        if(gameType.equals("Lanes")){
            System.out.println("You shall journey with 3 heroes.");
            count = 3;
        } else {
            System.out.println("You may journey with up to 3 heroes.");
            int maxHeroes = 3;
            count = promptIntBetween(scanner,
                    "How many heroes will walk the Realms? (1-3): ",
                    1, maxHeroes);
        }

        System.out.println();
        System.out.println("Available heroes:");
        for (int i = 0; i < allHeroes.size(); i++) {
            EmotionHero h = allHeroes.get(i);
            System.out.println("[" + i + "] " + h.getName()
                    + " [" + h.getHeroClass() + "] "
                    + "Lvl " + h.getLevel() + " | " + h.getStats());
        }

        while (party.size() < count) {
            int idx = promptIntBetween(scanner,
                    "Choose hero index for slot " + (party.size() + 1) + ": ",
                    0, allHeroes.size() - 1);
            EmotionHero chosen = allHeroes.get(idx);
            if (party.contains(chosen)) {
                System.out.println("That hero is already in your party. Choose someone else.");
                continue;
            }
            party.add(chosen);
            System.out.println(chosen.getName() + " joins your inner battle.");
        }

        System.out.println();
        System.out.println("Your party is formed:");
        for (Hero h : party) {
            System.out.println("  - " + h);
        }
        System.out.println("----------------------------------------");

        return party;
    }

    private int promptIntBetween(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }
}
