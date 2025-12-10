package game.core.model;

import java.util.ArrayList;
import java.util.List;

import game.core.items.Item;
import game.core.items.Potion;

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
        StringBuilder sb = new StringBuilder();
        for (Item i : items) {
            sb.append(i.getName()).append(", ");
        }
        return sb.toString();
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
