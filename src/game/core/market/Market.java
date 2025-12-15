package game.core.market;

import game.core.items.Armor;
import game.core.items.Item;
import game.core.items.Potion;
import game.core.items.Spell;
import game.core.items.Weapon;
import game.core.model.Hero;
import game.core.model.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Market {

    private final List<Item> stock;

    public Market(List<Item> stock) {
        this.stock = stock;
    }

    public void open(Hero hero, Scanner scanner) {
    boolean shopping = true;
    while (shopping) {
        System.out.println("========================================");
        System.out.println(" Sanctuary Market: Shopping for " + hero.getName());
        System.out.println(" Level: " + hero.getLevel() + "   Gold: " + hero.getGold());
        System.out.println(" Inventory: " + hero.getInventory());
        System.out.println("========================================");
        System.out.println("1) Buy weapons");
        System.out.println("2) Buy armor");
        System.out.println("3) Buy potions");
        System.out.println("4) Buy spells");
        System.out.println("5) Equip weapon");
        System.out.println("6) Equip armor");
        System.out.println("7) Leave market");
        System.out.print("> ");

        String input = scanner.nextLine().trim();
        if ("1".equals(input)) {
            buyItemOfType(hero, scanner, Weapon.class);
        } else if ("2".equals(input)) {
            buyItemOfType(hero, scanner, Armor.class);
        } else if ("3".equals(input)) {
            buyItemOfType(hero, scanner, Potion.class);
        } else if ("4".equals(input)) {
            buyItemOfType(hero, scanner, Spell.class);
        } else if ("5".equals(input)) {
            equipWeaponMenu(hero, scanner);
        } else if ("6".equals(input)) {
            equipArmorMenu(hero, scanner);
        } else if ("7".equals(input)) {
            shopping = false;
        } else {
            System.out.println("Please choose 1–7.");
        }
    }
}


    // -------- BUYING --------

    private void buyItemOfType(Hero hero, Scanner scanner, Class<?> clazz) {
    List<Item> available = new ArrayList<Item>();
    for (Item item : stock) {
        if (clazz.isInstance(item) && item.getRequiredLevel() <= hero.getLevel()) {
            available.add(item);
        }
    }

    if (available.isEmpty()) {
        System.out.println("Nothing of that type is suitable for your level yet.");
        return;
    }

    System.out.println("Items you can buy:");
    for (int i = 0; i < available.size(); i++) {
        Item it = available.get(i);
        System.out.println("[" + i + "] " + it.toString());
    }
    System.out.println("[X] Cancel");
    System.out.print("> ");

    String input = scanner.nextLine().trim().toUpperCase();
    if ("X".equals(input)) return;

    int idx;
    try {
        idx = Integer.parseInt(input);
    } catch (NumberFormatException e) {
        System.out.println("Invalid choice.");
        return;
    }
    if (idx < 0 || idx >= available.size()) {
        System.out.println("Invalid index.");
        return;
    }

    Item chosen = available.get(idx);
    if (hero.getGold() < chosen.getPrice()) {
        System.out.println("You don't have enough gold.");
        return;
    }

    hero.spendGold(chosen.getPrice());
    hero.getInventory().add(chosen);
    System.out.println("Purchased " + chosen.getName() + "!");
}


    // -------- EQUIP WEAPON --------

    private void equipWeaponMenu(Hero hero, Scanner scanner) {
        List<Weapon> weapons = new ArrayList<Weapon>();
        for (Item it : hero.getInventory().getItems()) {
            if (it instanceof Weapon) {
                weapons.add((Weapon) it);
            }
        }

        if (weapons.isEmpty()) {
            System.out.println("You have no weapons in your inventory.");
            return;
        }

        System.out.println("Choose a weapon to equip:");
        for (int i = 0; i < weapons.size(); i++) {
            Weapon w = weapons.get(i);
            System.out.println("[" + i + "] " + w.getName() +
                    " (Damage " + w.getDamage() +
                    ", Lvl " + w.getRequiredLevel() + ")");
        }
        System.out.println("[X] Cancel");
        System.out.print("> ");

        String input = scanner.nextLine().trim().toUpperCase();
        if ("X".equals(input)) return;

        int idx;
        try {
            idx = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice.");
            return;
        }
        if (idx < 0 || idx >= weapons.size()) {
            System.out.println("Invalid index.");
            return;
        }

        Weapon chosen = weapons.get(idx);
        if (hero.getLevel() < chosen.getRequiredLevel()) {
            System.out.println("You are not high enough level to equip that.");
            return;
        }

        hero.equipWeapon(chosen);
        System.out.println(hero.getName() + " equips " + chosen.getName() + ".");
    }

    // -------- EQUIP ARMOR --------

    private void equipArmorMenu(Hero hero, Scanner scanner) {
        List<Armor> armors = new ArrayList<Armor>();
        for (Item it : hero.getInventory().getItems()) {
            if (it instanceof Armor) {
                armors.add((Armor) it);
            }
        }

        if (armors.isEmpty()) {
            System.out.println("You have no armor in your inventory.");
            return;
        }

        System.out.println("Choose armor to equip:");
        for (int i = 0; i < armors.size(); i++) {
            Armor a = armors.get(i);
            System.out.println("[" + i + "] " + a.getName() +
                    " (Reduction " + a.getDamageReduction() +
                    ", Lvl " + a.getRequiredLevel() + ")");
        }
        System.out.println("[X] Cancel");
        System.out.print("> ");

        String input = scanner.nextLine().trim().toUpperCase();
        if ("X".equals(input)) return;

        int idx;
        try {
            idx = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice.");
            return;
        }
        if (idx < 0 || idx >= armors.size()) {
            System.out.println("Invalid index.");
            return;
        }

        Armor chosen = armors.get(idx);
        if (hero.getLevel() < chosen.getRequiredLevel()) {
            System.out.println("You are not high enough level to equip that.");
            return;
        }

        hero.equipArmor(chosen);
        System.out.println(hero.getName() + " equips " + chosen.getName() + ".");
    }
}
