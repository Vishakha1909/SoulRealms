package game.emotionlanes;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import game.core.items.Item;
import game.core.items.Potion;
import game.core.items.Spell;
import game.emotionlanes.model.UnitType;


import game.core.game.Game;
import game.core.world.Position;
import game.emotionlanes.logic.LanesState;
import game.emotionlanes.logic.SpawnManager;
import game.emotionlanes.logic.TokenMapper;
import game.emotionlanes.logic.TurnManager;
import game.emotionlanes.market.NexusMarketService;
import game.emotionlanes.model.LaneUnit;
import game.emotionlanes.ui.EmotionLanesRenderer;
import game.emotionlanes.world.EmotionLanesWorldBuilder;
import game.emotionlanes.world.EmotionLanesWorldData;
import game.emotionlanes.terrain.TerrainEffectManager;

public class EmotionLanesGame implements Game {

    private final Scanner sc = new Scanner(System.in);

    private EmotionLanesWorldData data;
    private LanesState state;
    private EmotionLanesRenderer renderer;

    private final TokenMapper tokens = new TokenMapper();

    private TerrainEffectManager terrain;
    private TurnManager turns;
    private final SpawnManager spawns = new SpawnManager();
    private NexusMarketService market;

    @Override
    public void init() {
        data = EmotionLanesWorldBuilder.buildDefaultWorld();
        state = new LanesState(data.getWorld(), data.getGlyphLayer());
        renderer = new EmotionLanesRenderer(data);

        // Allow monster buffs? set to true for 'hard mode'
        terrain = new TerrainEffectManager(data.getGlyphLayer(), true);
        turns = new TurnManager(terrain);

        // Nexus market stock from EmotionWar item files
        market = new NexusMarketService();

        // Read heroes/monsters from EmotionWar data files
        spawns.spawnFromEmotionWarData(state, data, terrain, sc);
    }

    @Override
    public void run() {
        boolean running = true;

        while (running) {
            renderer.render(tokens.heroTokens(state), tokens.monsterTokens(state));

            if (checkWinLose()) break;

            // HERO PHASE
            System.out.println("=== HERO PHASE ===");
            for (int i = 0; i < state.getHeroes().size(); i++) {
                LaneUnit h = state.getHeroes().get(i);
                if (!h.isAlive()) continue;

                boolean ok = heroTurn(h);
                if (!ok) { running = false; break; }

                if (checkWinLose()) { running = false; break; }
            }
            if (!running) break;

            // MONSTER PHASE
            System.out.println("=== MONSTER PHASE ===");
            turns.monstersAct(data.getWorld(), state);

            if (checkWinLose()) break;
        }

        System.out.println("Back to main menu.");
    }

    private void usePotion(LaneUnit laneHero) {
    if (laneHero.getHero() == null) return;

    List<Item> items = laneHero.getHero().getInventory().getItems();
    List<Potion> potions = new ArrayList<Potion>();
    for (Item it : items) {
        if (it instanceof Potion) potions.add((Potion) it);
    }

    if (potions.isEmpty()) {
        System.out.println("No potions.");
        pauseTiny();
        return;
    }

    System.out.println("Choose potion:");
    for (int i = 0; i < potions.size(); i++) {
        Potion p = potions.get(i);
        System.out.println((i + 1) + ") " + p.getName() + " (+" + p.getAmount() + " " + p.getStat() + ")");
    }
    System.out.println("0) Cancel");
    System.out.print("> ");
    String s = sc.nextLine().trim();

    int idx;
    try { idx = Integer.parseInt(s); } catch (NumberFormatException e) { return; }
    if (idx <= 0 || idx > potions.size()) return;

    Potion chosen = potions.get(idx - 1);
    chosen.applyTo(laneHero.getHero());
    laneHero.getHero().getInventory().remove(chosen);

    System.out.println("Used " + chosen.getName() + ".");
    pauseTiny();
}

private void castSpell(LaneUnit laneHero, LaneUnit engagedMonster) {
    if (laneHero.getHero() == null) return;
    if (engagedMonster == null || engagedMonster.getMonster() == null) {
        System.out.println("No engaged monster to target.");
        pauseTiny();
        return;
    }

    List<Item> items = laneHero.getHero().getInventory().getItems();
    List<Spell> spells = new ArrayList<Spell>();
    for (Item it : items) {
        if (it instanceof Spell) spells.add((Spell) it);
    }

    if (spells.isEmpty()) {
        System.out.println("No spells.");
        pauseTiny();
        return;
    }

    System.out.println("Choose spell:");
    for (int i = 0; i < spells.size(); i++) {
        Spell sp = spells.get(i);
        System.out.println((i + 1) + ") " + sp.getName() + " (DMG " + sp.getDamage() + ", MP " + sp.getManaCost() + ")");
    }
    System.out.println("0) Cancel");
    System.out.print("> ");
    String s = sc.nextLine().trim();

    int idx;
    try { idx = Integer.parseInt(s); } catch (NumberFormatException e) { return; }
    if (idx <= 0 || idx > spells.size()) return;

    Spell spell = spells.get(idx - 1);

    if (!laneHero.getHero().spendMp(spell.getManaCost())) {
        System.out.println("Not enough MP.");
        pauseTiny();
        return;
    }

    // treat spell as 'hero attack value', letting LaneUnit.takeHit handle defense
    int raw = spell.getDamage() + laneHero.getBonusDex();
    engagedMonster.takeHit(raw);

    System.out.println("✨ " + laneHero.getId() + " casts " + spell.getName() + "!");
    if (!engagedMonster.isAlive()) System.out.println("✅ " + engagedMonster.getId() + " defeated.");
    pauseTiny();
}


    private boolean heroTurn(LaneUnit h) {
    while (true) {
        renderer.render(tokens.heroTokens(state), tokens.monsterTokens(state));

        boolean atHeroNexusRow = (h.getPos().row == data.getWorld().getRows() - 1);

        // ENGAGEMENT: monster on same tile
        LaneUnit engaged = turns.engagedMonsterForHero(state, h);
        boolean engagedNow = (engaged != null && engaged.isAlive());

        System.out.println("Hero turn: " + h.getId() + ": " + h.getHero().getName()
                + " HP: " + h.hpString() + " MP: " + h.getHero().getMp() + " Gold: " + h.getHero().getGold()
                + " at " + h.getPos()
                + "  (buffs: STR+" + h.getBonusStr()
                + " DEX+" + h.getBonusDex()
                + " AGI+" + h.getBonusAgi() + ")");

        if (engagedNow) {
            System.out.println("⚠ ENGAGED with " + engaged.getId()
                    + " HP " + engaged.hpString());
        }

        System.out.println("1) Move (W/A/S/D)");
        System.out.println("2) Teleport (across lanes)");

        int opt = 3;

        // If engaged: allow Attack + Spell
        int attackOpt = -1;
        int spellOpt = -1;
        if (engagedNow) {
            attackOpt = opt++;
            System.out.println(attackOpt + ") Attack");

            spellOpt = opt++;
            System.out.println(spellOpt + ") Cast Spell");
        }

        // Potion always available (even when not engaged)
        int potionOpt = opt++;
        System.out.println(potionOpt + ") Use Potion");

        // Market only at hero nexus row
        int marketOpt = -1;
        if (atHeroNexusRow) {
            marketOpt = opt++;
            System.out.println(marketOpt + ") Market (Hero Nexus)");
        }

        int skipOpt = opt++;
        System.out.println(skipOpt + ") Skip");
        System.out.println("Q) Quit to menu");
        System.out.print("> ");

        String choice = sc.nextLine().trim().toUpperCase();
        if ("Q".equals(choice)) return false;

        // Skip
        if (choice.equals(String.valueOf(skipOpt))) return true;

        // Market
        if (marketOpt != -1 && choice.equals(String.valueOf(marketOpt))) {
            if (h.getHero() == null) {
                System.out.println("No Hero payload attached (should not happen).");
                pauseTiny();
                continue;
            }
            market.openForHero(h.getHero(), sc);
            return true; // counts as the action
        }

        // Potion
        if (choice.equals(String.valueOf(potionOpt))) {
            usePotion(h);
            return true; // counts as the action
        }

        // Attack
        if (attackOpt != -1 && choice.equals(String.valueOf(attackOpt))) {
            if (!engagedNow) {
                System.out.println("No engaged monster.");
                pauseTiny();
                continue;
            }
            turns.heroAttack(h, engaged);   // single hit (not full fight)
            return true;                    // action consumed
        }

        // Spell
        if (spellOpt != -1 && choice.equals(String.valueOf(spellOpt))) {
            if (!engagedNow) {
                System.out.println("No engaged monster.");
                pauseTiny();
                continue;
            }
            castSpell(h, engaged);          // your method (spell hit)
            return true;                    // action consumed
        }

        // Move
        if ("1".equals(choice)) {
            System.out.print("Direction (W/A/S/D) or X cancel: ");
            String mv = sc.nextLine().trim().toUpperCase();
            if (mv.length() == 0) continue;
            if ("X".equals(mv)) continue;

            char dir = mv.charAt(0);
            boolean moved = turns.tryMoveHero(data.getWorld(), state, h, dir);
            if (!moved) {
                System.out.println("Blocked / illegal move.");
                pauseTiny();
                continue;
            }
            return true; // action consumed
        }

        // Teleport
        if ("2".equals(choice)) {
            LaneUnit target = pickOtherHero(h);
            if (target == null) continue;

            boolean ok = turns.tryTeleportHero(data.getWorld(), state, h, target);
            if (!ok) {
                System.out.println("Teleport failed.");
                pauseTiny();
                continue;
            }
            return true; // action consumed
        }

        System.out.println("Invalid.");
        pauseTiny();
    }
}


    private LaneUnit pickOtherHero(LaneUnit current) {
        System.out.println("Teleport target hero:");
        for (int i = 0; i < state.getHeroes().size(); i++) {
            LaneUnit h = state.getHeroes().get(i);
            if (!h.isAlive()) continue;
            if (h == current) continue;
            System.out.println((i + 1) + ") " + h.getId() + " at " + h.getPos());
        }
        System.out.println("0) Cancel");
        System.out.print("> ");
        String s = sc.nextLine().trim();
        int idx;
        try { idx = Integer.parseInt(s); } catch (NumberFormatException e) { return null; }
        if (idx <= 0) return null;
        idx = idx - 1;
        if (idx < 0 || idx >= state.getHeroes().size()) return null;

        LaneUnit chosen = state.getHeroes().get(idx);
        if (!chosen.isAlive() || chosen == current) return null;
        return chosen;
    }

    private void pauseTiny() {
        System.out.print("(press Enter) ");
        sc.nextLine();
    }

    private boolean checkWinLose() {
        int topRow = 0;
        int bottomRow = data.getWorld().getRows() - 1;

        // win: any hero reaches monster nexus row
        for (LaneUnit h : state.getHeroes()) {
            if (h.isAlive() && h.getPos().row == topRow) {
                System.out.println("✅ You sealed the Rift! (Hero reached Monster Nexus)");
                return true;
            }
        }

        // lose: any monster reaches hero nexus row
        for (LaneUnit m : state.getMonsters()) {
            if (m.isAlive() && m.getPos().row == bottomRow) {
                System.out.println("💀 The Core is breached! (Monster reached Hero Nexus)");
                return true;
            }
        }
        return false;
    }
}
