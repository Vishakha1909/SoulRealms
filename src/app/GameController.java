package app;

import game.core.items.*;
import game.core.market.Market;
import game.core.model.Hero;
import game.core.world.Position;
import game.core.world.TileCategory;
import game.core.world.World;
import game.emotionlanes.world.EmotionLanesWorldBuilder;
import game.emotionlanes.world.EmotionLanesWorldData;
import game.emotionwar.factory.DataPaths;
import game.emotionwar.factory.EmotionHeroFactory;
import game.emotionwar.factory.EmotionItemFactory;
import game.emotionwar.factory.EmotionMonsterFactory;
import game.emotionwar.logic.EmotionEncounterManager;
import game.emotionwar.logic.EmotionPartyBuilder;
import game.emotionwar.model.EmotionHero;
import game.emotionwar.model.EmotionHeroType;
import game.emotionwar.model.EmotionType;
import game.emotionwar.ui.EmotionWarRenderer;
import game.emotionwar.world.EmotionWorldBuilder;
import game.emotionwar.world.EmotionWorldData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class GameController {
    private World world;
    private EmotionType[][] emotionLayer;
    private final List<Hero> party = new ArrayList<Hero>();
    private Market sanctuaryMarket;
    private EmotionWarRenderer renderer;
    private EmotionEncounterManager encounterManager;

    private final Scanner scanner = new Scanner(System.in);
    private final Random rng = new Random();
    private final String gameType;
    private boolean running;


    public GameController(String gameType){
        this.gameType = gameType;
    }

    public void init() {
        // WORLD
        if (gameType.equals("Lanes")){
            EmotionLanesWorldData worldData = EmotionLanesWorldBuilder.buildDefaultWorld();
            this.world = worldData.getWorld();
        } else {
            EmotionWorldData worldData = EmotionWorldBuilder.buildDefaultWorld();
            this.world = worldData.getWorld();
            this.emotionLayer = worldData.getEmotionLayer();
        }

        // MONSTERS
        EmotionMonsterFactory.loadAllDefaultMonsters();

        // ITEMS
        List<Weapon> weapons  = EmotionItemFactory.loadWeapons(DataPaths.WEAPONS);
        List<Armor>  armors   = EmotionItemFactory.loadArmors(DataPaths.ARMORS);
        List<Potion> potions  = EmotionItemFactory.loadPotions(DataPaths.POTIONS);
        List<Spell>  spells   = EmotionItemFactory.loadSpells(DataPaths.SPELLS);

        // HEROES – build party
        List<EmotionHero> allHeroes = EmotionHeroFactory.loadAllDefaultHeroes();
        if (allHeroes.isEmpty()) {
            EmotionHero hero = new EmotionHero(
                    "Kael",
                    EmotionHeroType.RESOLUTE,
                    1,
                    new game.core.model.Stats(150, 50, 30, 15, 15),
                    300
            );
            party.add(hero);
        } else {
            EmotionPartyBuilder builder = new EmotionPartyBuilder();
            party.addAll(builder.buildParty(allHeroes, scanner, gameType));
        }

        // STARTER GEAR
        for (Hero h : party) {
            if (!weapons.isEmpty()) {
                h.equipWeapon(weapons.get(0));
            }
            if (!armors.isEmpty()) {
                h.equipArmor(armors.get(0));
            }
            if (!potions.isEmpty()) {
                h.getInventory().add(potions.get(0));
            }
            if (!spells.isEmpty()) {
                h.getInventory().add(spells.get(0));
            }
        }

        // MARKET STOCK
        List<Item> stock = new ArrayList<Item>();
        if (weapons.size() > 1) stock.add(weapons.get(1));
        if (armors.size() > 1)  stock.add(armors.get(1));
        stock.addAll(potions);
        stock.addAll(spells);
        sanctuaryMarket = new Market(stock);

        // UI & encounter manager
        renderer = new EmotionWarRenderer(world, emotionLayer, party);
        encounterManager = new EmotionEncounterManager(world, emotionLayer, party, rng, scanner);
    }

    public void run(){
        if (gameType.equals("Lanes")){
            runLanes();
        } else {
            runGrid();
        }
    }

    private void runLanes(){
        this.running = true;
        while (running) {
            //TODO render each hero in the lane and show tile types
            renderer.render();
            System.out.println("[W/A/S/D] move  [I]nspect [V]iew inventory  [U]se potion [H]elp  [Q]uit");
            System.out.print("> ");

            Position current = world.getPartyPosition();
            Position next = getPlayerMove(current);

            if (!world.moveTo(next)) {
                // bumped into wall or invalid
                pause();
                continue;
            }

            // After movement, handle tile events
            Position pos = world.getPartyPosition();
            TileCategory cat = world.getTile(pos).getCategory();

            if (cat == TileCategory.MARKET) {
                System.out.println("You find a quiet Sanctuary Shrine.");
                openMarketForParty();
            } else { //TODO if monster at space engage, else nothing
                //TODO send tile type to monster encounter
                boolean survived = encounterManager.handleTileEvent();
                if (!survived) {
                    running = false;
                }
            }
            //TODO Move monster for lanes game, handle if land on player
        }
        System.out.println("Game over.");
    }

    private void runGrid() {
        this.running = true;
        while (running) {
            renderer.render();
            System.out.println("[W/A/S/D] move  [I]nspect [V]iew inventory  [U]se potion [H]elp  [Q]uit");
            System.out.print("> ");

            Position current = world.getPartyPosition();
            Position next = getPlayerMove(current);

            if (!world.moveTo(next)) {
                // bumped into wall or invalid
                pause();
                continue;
            }

            // After movement, handle tile events
            Position pos = world.getPartyPosition();
            TileCategory cat = world.getTile(pos).getCategory();

            if (cat == TileCategory.MARKET) {
                System.out.println("You find a quiet Sanctuary Shrine.");
                openMarketForParty();
            } else {
                boolean survived = encounterManager.handleTileEvent();
                if (!survived) {
                    running = false;
                }
            }
        }
        System.out.println("Game over.");
    }

    private Position getPlayerMove(Position current){
        String input = scanner.nextLine().trim().toUpperCase();
        switch (input) {
            case "W":
                return current.up();
            case "S":
                return current.down();
            case "A":
                return current.left();
            case "D":
                return current.right();
            case "I":
                inspectParty();
            case "V":
                showInventoryMenu();
            case "U":
                usePotionOutsideBattle();
            case "H":
                if (gameType.equals("Lanes")) {
                    showHelpLanes();
                } else {
                    showHelpGrid();
                }
                pause();
            case "Q":
                System.out.println("Leaving the Soul Realms...");
                //TODO print game stats
                this.running = false;
            default:
                System.out.println("Unknown command.");
                pause();
                return current;
        }
    }

    private void inspectParty() {
        System.out.println("Party info:");
        for (Hero h : party) {
            System.out.println("  " + h.toString());
        }
        pause();
    }

    private Hero chooseHero(String prompt) {
        if (party.isEmpty()) {
            System.out.println("No heroes in party.");
            return null;
        }
        System.out.println(prompt);
        for (int i = 0; i < party.size(); i++) {
            Hero h = party.get(i);
            System.out.println("  " + (i + 1) + ") " + h.getName() +
                    " (Lvl " + h.getLevel() + ", HP " + h.getHp() + "/" + h.getStats().getMaxHp() +
                    ", MP " + h.getMp() + "/" + h.getStats().getMaxMp() + ")");
        }
        System.out.print("> ");
        String line = scanner.nextLine().trim();
        try {
            int idx = Integer.parseInt(line) - 1;
            if (idx >= 0 && idx < party.size()) {
                return party.get(idx);
            }
        } catch (NumberFormatException e) {
            // ignore
        }
        System.out.println("Cancelled.");
        return null;
    }

    private void showInventoryMenu() {
        Hero hero = chooseHero("View inventory for which hero?");
        if (hero == null) {
            pause();
            return;
        }

        System.out.println("Inventory for " + hero.getName() + ":");

        if (hero.getInventory().getItems().isEmpty()) {
            System.out.println("  (empty)");
        } else {
            List<game.core.items.Item> items = hero.getInventory().getItems();
            for (int i = 0; i < items.size(); i++) {
                game.core.items.Item it = items.get(i);
                System.out.println("  " + (i + 1) + ") " + it);
            }
        }

        System.out.println("Equipped:");
        System.out.println("  Main hand: " + (hero.getMainHand() != null ? hero.getMainHand().getName() : "none"));
        System.out.println("  Off hand : " + (hero.getOffHand() != null ? hero.getOffHand().getName() : "none"));
        System.out.println("  Armor    : " + (hero.getArmor()  != null ? hero.getArmor().getName()  : "none"));

        pause();
    }

    private void usePotionOutsideBattle() {
        Hero hero = chooseHero("Use a potion on which hero?");
        if (hero == null) {
            pause();
            return;
        }

        List<game.core.items.Potion> potions = hero.getInventory().getPotions();
        if (potions.isEmpty()) {
            System.out.println(hero.getName() + " has no potions.");
            pause();
            return;
        }

        System.out.println("Potions for " + hero.getName() + ":");
        for (int i = 0; i < potions.size(); i++) {
            game.core.items.Potion p = potions.get(i);
            System.out.println("  " + (i + 1) + ") " + p);
        }
        System.out.println("  0) Cancel");
        System.out.print("> ");

        String line = scanner.nextLine().trim();
        int choice;
        try {
            choice = Integer.parseInt(line);
        } catch (NumberFormatException e) {
            System.out.println("Cancelled.");
            pause();
            return;
        }
        if (choice <= 0 || choice > potions.size()) {
            System.out.println("Cancelled.");
            pause();
            return;
        }

        game.core.items.Potion chosen = potions.get(choice - 1);
        chosen.applyTo(hero); // reuse the same logic as in battle
        hero.getInventory().remove(chosen);
        System.out.println("Used " + chosen.getName() + " on " + hero.getName() + ".");
        System.out.println("Now at HP " + hero.getHp() + "/" + hero.getStats().getMaxHp() +
                ", MP " + hero.getMp() + "/" + hero.getStats().getMaxMp());

        pause();
    }

    private void openMarketForParty() {
        boolean shopping = true;
        while (shopping) {
            System.out.println("Who do you want to shop for?");
            for (int i = 0; i < party.size(); i++) {
                Hero h = party.get(i);
                System.out.println("[" + i + "] " + h.getName() +
                        " (Lv " + h.getLevel() + ", Gold " + h.getGold() + ")");
            }
            System.out.println("[X] Leave market");
            System.out.print("> ");

            String input = scanner.nextLine().trim().toUpperCase();
            if ("X".equals(input)) {
                shopping = false;
            } else {
                try {
                    int idx = Integer.parseInt(input);
                    if (idx < 0 || idx >= party.size()) {
                        System.out.println("Invalid hero index.");
                    } else {
                        sanctuaryMarket.open(party.get(idx), scanner);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a hero number or X.");
                }
            }
        }
    }

    private void showHelpGrid() {
        System.out.println("--------------- HELP / INSTRUCTIONS ---------------");
        System.out.println("Setting:");
        System.out.println("  You are traveling through inner Soul Realms, each one");
        System.out.println("  dominated by a single emotion (W/D/F/S/A/E/P).");
        System.out.println("  Your party are aspects of the self trying to restore balance.");
        System.out.println();
        System.out.println("Exploration controls:");
        System.out.println("  W / A / S / D  - Move up / left / down / right");
        System.out.println("  I              - Inspect party (HP/MP, stats, gold, equipment)");
        System.out.println("  V              - View inventory per hero");
        System.out.println("  U              - Use potion on a hero outside of battle");
        System.out.println("  H              - Show this help screen");
        System.out.println("  Q              - Quit to end the run");
        System.out.println();
        System.out.println("Map tiles:");
        System.out.println("  @  - Your party");
        System.out.println("  M  - Sanctuary Market (buy/sell items, change gear)");
        System.out.println("  #  - Inaccessible rock / blocked tile");
        System.out.println("  .  - Neutral ground");
        System.out.println("  W  - Wrath realm");
        System.out.println("  D  - Desire realm");
        System.out.println("  F  - Fear realm");
        System.out.println("  S  - Sorrow realm");
        System.out.println("  A  - Anxiety realm");
        System.out.println("  E  - Envy realm");
        System.out.println("  P  - Pride realm");
        System.out.println("  (Some neutral-looking tiles are hidden Fractures that");
        System.out.println("   can trigger harder mixed-emotion battles at higher levels.)");
        System.out.println();
        System.out.println("Markets (Sanctuary):");
        System.out.println("  When you stand on M, you can choose which hero to shop for.");
        System.out.println("  For that hero you can:");
        System.out.println("    - Buy items they are high enough level to use");
        System.out.println("    - Sell items from their inventory");
        System.out.println("    - Equip weapons and armor");
        System.out.println();
        System.out.println("Battles:");
        System.out.println("  Each round, you choose which hero acts, then the monsters act.");
        System.out.println("  On a hero's turn you can:");
        System.out.println("    1) Attack       - Weapon attack on a chosen monster.");
        System.out.println("    2) Cast Spell   - Spend MP to cast a learned spell.");
        System.out.println("    3) Use Potion   - Consume a potion from that hero's inventory.");
        System.out.println("    4) Skip         - End that hero's turn without acting.");
        System.out.println();
        System.out.println("  Heroes and monsters can dodge attacks based on their dodge stat.");
        System.out.println("  After each round, you may heal using potions or rest at markets.");
        System.out.println();
        System.out.println("Death & Rewards:");
        System.out.println("  - If all heroes fall, the run ends.");
        System.out.println("  - If you win a battle, surviving heroes share gold and XP.");
        System.out.println("---------------------------------------------------");
    }

    private void showHelpLanes() {
        System.out.println("--------------- HELP / INSTRUCTIONS ---------------");
        System.out.println("Setting:");
        System.out.println("  You are traveling through inner Soul Realms, each one");
        System.out.println("  dominated by a single emotion (W/D/F/S/A/E/P).");
        System.out.println("  Your party are aspects of the self trying to restore balance by defeating the monster's nexus and protecting your home.");
        System.out.println();
        System.out.println("Exploration controls:");
        System.out.println("  W / A / S / D  - Move up / left / down / right");
        System.out.println("  I              - Inspect party (HP/MP, stats, gold, equipment)");
        System.out.println("  V              - View inventory per hero");
        System.out.println("  U              - Use potion on a hero outside of battle");
        System.out.println("  H              - Show this help screen");
        System.out.println("  Q              - Quit to end the run");
        System.out.println();
        System.out.println("Map tiles:");
        System.out.println("  @  - Your party");
        System.out.println("  M  - Sanctuary Market (buy/sell items, change gear)");
        System.out.println("  #  - Inaccessible rock / blocked tile");
        //TODO Describe how the new tiles look
        System.out.println("  (Some neutral-looking tiles are hidden Fractures that");
        System.out.println("   can trigger harder mixed-emotion battles at higher levels.)");
        System.out.println();
        System.out.println("Markets (Sanctuary):");
        System.out.println("  When you stand on M, you can choose which hero to shop for.");
        System.out.println("  For that hero you can:");
        System.out.println("    - Buy items they are high enough level to use");
        System.out.println("    - Sell items from their inventory");
        System.out.println("    - Equip weapons and armor");
        System.out.println();
        System.out.println("Battles:");
        System.out.println("  Each round, you choose which hero acts, then the monsters act.");
        System.out.println("  On a hero's turn you can:");
        System.out.println("    1) Attack       - Weapon attack on a chosen monster.");
        System.out.println("    2) Cast Spell   - Spend MP to cast a learned spell.");
        System.out.println("    3) Use Potion   - Consume a potion from that hero's inventory.");
        System.out.println("    4) Skip         - End that hero's turn without acting.");
        System.out.println();
        System.out.println("  Heroes and monsters can dodge attacks based on their dodge stat.");
        System.out.println("  After each round, you may heal using potions or rest at markets.");
        System.out.println();
        System.out.println("Death & Rewards:");
        System.out.println("  - If all heroes fall, the run ends.");
        System.out.println("  - If you win a battle, surviving heroes share gold and XP.");
        System.out.println("  - Reach the monster nexus at all three lanes to stop the assault and win.");
        System.out.println("  - If monsters reach the hero's home nexus in all three lanes, you have failed and loose.");
        System.out.println("---------------------------------------------------");
    }

    private void pause() {
        System.out.println();
        System.out.println("(Press Enter to continue)");
        scanner.nextLine();
    }
}
