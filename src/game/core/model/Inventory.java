package game.core.model;

import java.util.ArrayList;
import java.util.List;

import game.core.items.Armor;
import game.core.items.Item;
import game.core.items.Potion;
import game.core.items.Spell;
import game.core.items.Weapon;

public class Inventory {

    private final List<Item> items;

    public Inventory() {
        this.items = new ArrayList<Item>();
    }

    public List<Item> getItems() {
        return items;
    }

    public void add(Item item) {
        items.add(item);
    }

    public boolean remove(Item item) {
        return items.remove(item);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int size() {
        return items.size();
    }

    @Override
public String toString() {
    if (items.isEmpty()) return "(empty)";

    StringBuilder weapons = new StringBuilder();
    StringBuilder armors  = new StringBuilder();
    StringBuilder potions = new StringBuilder();
    StringBuilder spells  = new StringBuilder();

    for (Item it : items) {
        if (it instanceof Weapon) {
            weapons.append("  - ").append(it).append("\n");
        } else if (it instanceof Armor) {
            armors.append("  - ").append(it).append("\n");
        } else if (it instanceof Potion) {
            potions.append("  - ").append(it).append("\n");
        } else if (it instanceof Spell) {
            spells.append("  - ").append(it).append("\n");
        }
    }

    StringBuilder out = new StringBuilder();

    if (weapons.length() > 0) {
        out.append("Weapons:\n").append(weapons);
    }
    if (armors.length() > 0) {
        out.append("Armor:\n").append(armors);
    }
    if (potions.length() > 0) {
        out.append("Potions:\n").append(potions);
    }
    if (spells.length() > 0) {
        out.append("Spells:\n").append(spells);
    }

    return out.toString();
}


    public List<Potion> getPotions() {
        List<Potion> potions = new ArrayList<Potion>();
        for (int i = 0; i < items.size(); i++) {
            Item it = items.get(i);
            if (it instanceof Potion) {
                potions.add((Potion) it);
            }
        }
        return potions;
    }
}
