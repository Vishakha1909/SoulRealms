package game.emotionwar.factory;

import game.core.items.*;
import java.util.ArrayList;
import java.util.List;

public class EmotionItemFactory {

    public static List<Weapon> loadWeapons(String path) {
        List<Weapon> result = new ArrayList<>();
        for (String line : FileUtils.readLines(path)) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            // name,price,level,damage,hands
            String[] p = line.split(",");
            if (p.length < 5) continue;
            String name = p[0].trim();
            int price = Integer.parseInt(p[1].trim());
            int level = Integer.parseInt(p[2].trim());
            int damage = Integer.parseInt(p[3].trim());
            int hands = Integer.parseInt(p[4].trim());
            result.add(new Weapon(name, price, level, damage, hands));
        }
        return result;
    }

    public static List<Armor> loadArmors(String path) {
        List<Armor> result = new ArrayList<>();
        for (String line : FileUtils.readLines(path)) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            // name,price,level,damageReduction
            String[] p = line.split(",");
            if (p.length < 4) continue;
            String name = p[0].trim();
            int price = Integer.parseInt(p[1].trim());
            int level = Integer.parseInt(p[2].trim());
            int reduction = Integer.parseInt(p[3].trim());
            result.add(new Armor(name, price, level, reduction));
        }
        return result;
    }

    public static List<Potion> loadPotions(String path) {
        List<String> lines = FileUtils.readLines(path);
        List<Potion> potions = new ArrayList<Potion>();
        for (String line : lines) {
            if (line.startsWith("#") || line.trim().isEmpty()) continue;
            String[] parts = line.split(",");
            // name, price, reqLvl, amount, stat
            String name = parts[0].trim();
            int price = Integer.parseInt(parts[1].trim());
            int lvl   = Integer.parseInt(parts[2].trim());
            int amount = Integer.parseInt(parts[4].trim());
            Potion.Stat stat = Potion.Stat.valueOf(parts[3].trim().toUpperCase());
            potions.add(new Potion(name, price, lvl, amount, stat));
        }
        return potions;
    }

    public static List<Spell> loadSpells(String path) {
        List<String> lines = FileUtils.readLines(path);
        List<Spell> spells = new ArrayList<Spell>();
        for (String line : lines) {
            if (line.startsWith("#") || line.trim().isEmpty()) continue;
            // name, price, reqLvl, damage, manaCost, type
            String[] parts = line.split(",");
            String name = parts[0].trim();
            int price = Integer.parseInt(parts[1].trim());
            int lvl   = Integer.parseInt(parts[2].trim());
            int dmg   = Integer.parseInt(parts[3].trim());
            int mp    = Integer.parseInt(parts[4].trim());
            SpellType type = SpellType.valueOf(parts[5].trim().toUpperCase());
            spells.add(new Spell(name, price, lvl, dmg, mp, type));
        }
        return spells;
    }
}
