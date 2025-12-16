package game.emotionlanes.logic;

import java.util.List;

import game.core.items.Potion;
import game.core.items.Weapon;
import game.core.model.Hero;
import game.emotionwar.factory.DataPaths;
import game.emotionwar.factory.EmotionItemFactory;

/**
 * Provides initial starter items / loadouts for heroes in Emotion Lanes.
 *
 * Used to ensure each hero begins with a playable baseline inventory
 * (e.g., starter weapon, potions), without hardcoding that logic in the game loop.
 */

public class StarterKitService {

    private final List<Weapon> weapons;
    private final List<Potion> potions;

    public StarterKitService() {
        weapons = EmotionItemFactory.loadWeapons(DataPaths.WEAPONS);
        potions = EmotionItemFactory.loadPotions(DataPaths.POTIONS);
    }

    /** Give a basic kit: 1 lowest-level weapon + 2 potions. */
    public void giveStarterKit(Hero h) {
        if (h == null) return;

        Weapon w = pickCheapestUsableWeapon(h);
        if (w != null) {
            h.getInventory().add(w);
            h.equipWeapon(w);
        }

        Potion p = pickAnyPotion();
        if (p != null) {
            // give two copies (cheap + effective)
            h.getInventory().add(p);
            h.getInventory().add(p);
        }
    }

    private Weapon pickCheapestUsableWeapon(Hero h) {
        Weapon best = null;
        for (Weapon w : weapons) {
            if (w.getRequiredLevel() > h.getLevel()) continue;
            if (best == null || w.getPrice() < best.getPrice()) best = w;
        }
        return best;
    }

    private Potion pickAnyPotion() {
        if (potions == null || potions.isEmpty()) return null;
        return potions.get(0);
    }
}
