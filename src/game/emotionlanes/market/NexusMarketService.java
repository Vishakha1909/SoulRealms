package game.emotionlanes.market;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import game.core.items.Armor;
import game.core.items.Item;
import game.core.items.Potion;
import game.core.items.Spell;
import game.core.items.Weapon;
import game.core.market.Market;
import game.core.model.Hero;

import game.emotionwar.factory.DataPaths;
import game.emotionwar.factory.EmotionItemFactory;

public class NexusMarketService {

    private final Market market;

    public NexusMarketService() {
        List<Weapon> weapons  = EmotionItemFactory.loadWeapons(DataPaths.WEAPONS);
        List<Armor> armors    = EmotionItemFactory.loadArmors(DataPaths.ARMORS);
        List<Potion> potions  = EmotionItemFactory.loadPotions(DataPaths.POTIONS);
        List<Spell> spells    = EmotionItemFactory.loadSpells(DataPaths.SPELLS);

        List<Item> stock = new ArrayList<Item>();
        stock.addAll(weapons);
        stock.addAll(armors);
        stock.addAll(potions);
        stock.addAll(spells);

        this.market = new Market(stock);
    }

    public void openForHero(Hero h, Scanner sc) {
        System.out.println("Nexus Market: " + h.getName() + " (Gold: " + h.getGold() + ")");
        market.open(h, sc);
    }
}
