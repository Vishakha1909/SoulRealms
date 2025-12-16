package game.emotionlanes;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

import game.core.items.Armor;
import game.core.items.Item;
import game.core.items.Potion;
import game.core.items.Spell;
import game.core.items.Weapon;
import game.emotionlanes.model.UnitType;
import game.emotionlanes.logic.Difficulty;
import game.emotionlanes.logic.RoundSystem;
import game.emotionlanes.logic.MonsterWaveSpawner;



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

    private int round = 1;
    private Difficulty difficulty = Difficulty.MEDIUM;
    private RoundSystem roundSystem;
    private MonsterWaveSpawner waveSpawner;


    @Override
    public void init() {
        data = EmotionLanesWorldBuilder.buildDefaultWorld();
        state = new LanesState(data.getWorld(), data.getGlyphLayer(), data.getHeroSpawns());
        renderer = new EmotionLanesRenderer(data);

        // Allow monster buffs? set to true for 'hard mode'
        terrain = new TerrainEffectManager(data.getGlyphLayer(), true);
        turns = new TurnManager(terrain);

        roundSystem = new RoundSystem(terrain, data);
        waveSpawner = new MonsterWaveSpawner(terrain, data);


        // Nexus market stock from EmotionWar item files
        market = new NexusMarketService();

        // Read heroes/monsters from EmotionWar data files
        spawns.spawnFromEmotionWarData(state, data, terrain, sc);
    }

    @Override
public void run() {
    difficulty = pickDifficulty();

    boolean running = true;

    while (running) {

        // start of round: respawn dead heroes
        roundSystem.startOfRoundRespawns(state);

        // HERO PHASE header screen
        
        renderer.render(
            tokens.heroTokens(state),
            tokens.monsterTokens(state),
            "ROUND " + round + "  |  HERO PHASE"
        );

        if (checkWinLose()) break;

        // HERO PHASE (each hero takes exactly one action)
        for (int i = 0; i < state.getHeroes().size(); i++) {
            LaneUnit h = state.getHeroes().get(i);
            if (!h.isAlive()) continue;

            boolean ok = heroTurn(h);              // this re-renders and shows menus
            if (!ok) { running = false; break; }

            if (checkWinLose()) { running = false; break; }
        }
        if (!running) break;
                
        // ================= MONSTER PHASE =================

// monsters act ONCE and return log
List<String> monsterLog = turns.monstersAct(data.getWorld(), state);

// render board WITHOUT legend so log fits on screen
renderer.renderNoLegend(
    tokens.heroTokens(state),
    tokens.monsterTokens(state),
    "ROUND " + round + "  |  MONSTER PHASE"
);

// print log UNDER board
System.out.println();
System.out.println("=== MONSTER PHASE LOG ===");
if (monsterLog.isEmpty()) {
    System.out.println("No monster actions.");
} else {
    for (String s : monsterLog) System.out.println(s);
}
System.out.println("========================");
pauseTiny();


        if (checkWinLose()) break;

        // end of round: regen alive heroes
        roundSystem.endOfRoundRegen(state);

        // end of round: spawn waves every N rounds
        int interval = difficulty.getSpawnEveryRounds();
        int monsterLevel = 1 + (round / 6);
        waveSpawner.spawnWaveIfDue(round, interval, state, monsterLevel);

        round++;
    }

    System.out.println("Back to main menu.");
}


private Difficulty pickDifficulty() {
    System.out.println("Choose difficulty:");
    System.out.println("1) EASY   (spawn every 6 rounds)");
    System.out.println("2) MEDIUM (spawn every 4 rounds)");
    System.out.println("3) HARD   (spawn every 2 rounds)");
    System.out.print("> ");
    String s = sc.nextLine().trim();
    if ("1".equals(s)) return Difficulty.EASY;
    if ("3".equals(s)) return Difficulty.HARD;
    return Difficulty.MEDIUM;
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

private void showInventory(LaneUnit laneHero) {
    if (laneHero.getHero() == null) return;

    System.out.println("=== INVENTORY: " + laneHero.getHero().getName() + " ===");
    System.out.println(laneHero.getHero().getInventory());
    pauseTiny();
}

    private boolean heroTurn(LaneUnit h) {
    while (true) {
        
        renderer.render(
        tokens.heroTokens(state),
        tokens.monsterTokens(state),
        "ROUND " + round + "  |  HERO PHASE"
    );


        boolean atHeroNexusRow = (h.getPos().row == data.getWorld().getRows() - 1);

        // ENGAGEMENT: in-range monster (same lane + distance <= 1)
        LaneUnit engaged = turns.engagedMonsterForHero(state, h);
        boolean engagedNow = (engaged != null && engaged.isAlive());

        boolean canClearObstacle = turns.hasAdjacentObstacle(data.getGlyphLayer(), h.getPos());

        System.out.println("Hero turn: " + h.getId() + ": " + h.getHero().getName()
                + " HP: " + h.hpString()
                + " MP: " + h.getHero().getMp()
                + " Gold: " + h.getHero().getGold()
                + " at " + h.getPos()
                + "  (buffs: STR+" + h.getBonusStr()
                + " DEX+" + h.getBonusDex()
                + " AGI+" + h.getBonusAgi() + ")");

        if (engagedNow) {
            System.out.println("!!! IN RANGE of " + engaged.getId() + " HP " + engaged.hpString());
        }

        // ---- Build menu dynamically (stable mapping) ----
        int opt = 1;

        final int moveOpt = opt++;
        final int tpOpt   = opt++;

        Integer clearOpt  = null;
        if (canClearObstacle) clearOpt = opt++;

        Integer attackOpt = null;
        Integer spellOpt  = null;
        if (engagedNow) {
            attackOpt = opt++;
            spellOpt  = opt++;
        }

        final int potionOpt = opt++;
        final int invOpt    = opt++;
        final int weaponOpt = opt++;
        final int armorOpt  = opt++;

        Integer marketOpt = null;
        if (atHeroNexusRow) marketOpt = opt++;

        final int recallOpt = opt++;
        final int skipOpt   = opt++;

        // ---- Print menu ----
        System.out.println(moveOpt + ") Move (W/A/S/D)");
        System.out.println(tpOpt   + ") Teleport (across lanes)");

        if (clearOpt != null) {
            System.out.println(clearOpt + ") Clear Obstacle (adjacent O -> becomes P)");
        }

        if (attackOpt != null) {
            System.out.println(attackOpt + ") Attack");
            System.out.println(spellOpt  + ") Cast Spell");
        }

        System.out.println(potionOpt + ") Use Potion");
        System.out.println(invOpt    + ") Inventory (free)");
        System.out.println(weaponOpt + ") Change Weapon");
        System.out.println(armorOpt  + ") Change Armor");

        if (marketOpt != null) {
            System.out.println(marketOpt + ") Market (Hero Nexus)");
        }

        System.out.println(recallOpt + ") Recall (return to your Nexus)");
        System.out.println(skipOpt   + ") Skip");
        System.out.println("Q) Quit to menu");
        System.out.print("> ");

        String choice = sc.nextLine().trim().toUpperCase();
        if ("Q".equals(choice)) return false;

        // ---- Inventory (free action) ----
        if (choice.equals(String.valueOf(invOpt))) {
            showInventory(h);
            continue;
        }

        // ---- Skip ----
        if (choice.equals(String.valueOf(skipOpt))) return true;

        // ---- Market (MUST consume action) ----
        if (marketOpt != null && choice.equals(String.valueOf(marketOpt))) {
            market.openForHero(h.getHero(), sc);
            return true;
        }

        // ---- Potion (consume) ----
        if (choice.equals(String.valueOf(potionOpt))) {
            usePotion(h);
            return true;
        }

        // ---- Change Weapon (consume) ----
        if (choice.equals(String.valueOf(weaponOpt))) {
            changeWeapon(h);
            return true;
        }

        // ---- Change Armor (consume) ----
        if (choice.equals(String.valueOf(armorOpt))) {
            changeArmor(h);
            return true;
        }

        // ---- Attack / Spell (consume) ----
        if (attackOpt != null && choice.equals(String.valueOf(attackOpt))) {
            if (!engagedNow) {
                System.out.println("No monster in range.");
                pauseTiny();
                continue;
            }
            turns.heroAttack(h, engaged);
            return true;
        }

        if (spellOpt != null && choice.equals(String.valueOf(spellOpt))) {
            if (!engagedNow) {
                System.out.println("No monster in range.");
                pauseTiny();
                continue;
            }
            castSpell(h, engaged);
            return true;
        }

        // ---- Clear obstacle (consume) ----
        if (clearOpt != null && choice.equals(String.valueOf(clearOpt))) {
            System.out.print("Clear obstacle direction (W/A/S/D) or X cancel: ");
            String mv = sc.nextLine().trim().toUpperCase();
            if (mv.length() == 0) continue;
            if ("X".equals(mv)) continue;

            boolean ok = turns.tryRemoveObstacleAdjacent(h, mv.charAt(0));
            if (!ok) {
                System.out.println("No obstacle in that direction.");
                pauseTiny();
                continue;
            }
            return true;
        }

        // ---- Recall (consume) ----
        if (choice.equals(String.valueOf(recallOpt))) {
            boolean ok = turns.recallHero(state, h);
            if (!ok) {
                System.out.println("Recall failed.");
                pauseTiny();
                continue;
            }
            return true;
        }

        // ---- Move (consume) ----
        if (choice.equals(String.valueOf(moveOpt))) {
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
            return true;
        }

        // ---- Teleport (consume) ----
        if (choice.equals(String.valueOf(tpOpt))) {
            LaneUnit target = pickOtherHero(h);
            if (target == null) continue;

            boolean ok = turns.tryTeleportHero(data.getWorld(), state, h, target);
            if (!ok) {
                System.out.println("Teleport failed.");
                pauseTiny();
                continue;
            }
            return true;
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
                System.out.println("You sealed the Rift! (Hero reached Monster Nexus)");
                return true;
            }
        }

        // lose: any monster reaches hero nexus row
        for (LaneUnit m : state.getMonsters()) {
            if (m.isAlive() && m.getPos().row == bottomRow) {
                System.out.println("The Core is breached! (Monster reached Hero Nexus)");
                return true;
            }
        }
        return false;
    }

    private void changeWeapon(LaneUnit laneHero) {
        if (laneHero.getHero() == null) return;

        List<Item> items = laneHero.getHero().getInventory().getItems();
        List<Weapon> weapons = new ArrayList<Weapon>();
        for (Item it : items) if (it instanceof Weapon) weapons.add((Weapon) it);

        if (weapons.isEmpty()) {
            System.out.println("No weapons in inventory.");
            pauseTiny();
            return;
        }

        System.out.println("Current weapons equipped:");
        System.out.println("  Main: " + (laneHero.getHero().getMainHand() == null ? "(none)" : laneHero.getHero().getMainHand()));
        System.out.println("  Off : " + (laneHero.getHero().getOffHand() == null ? "(none)" : laneHero.getHero().getOffHand()));
        System.out.println();

        System.out.println("Choose weapon to equip:");
        for (int i = 0; i < weapons.size(); i++) {
            System.out.println((i + 1) + ") " + weapons.get(i));
        }
        System.out.println("0) Cancel");
        System.out.print("> ");

        String s = sc.nextLine().trim();
        int idx;
        try { idx = Integer.parseInt(s); }
        catch (NumberFormatException e) { System.out.println("Invalid."); pauseTiny(); return; }

        if (idx == 0) return;
        if (idx < 1 || idx > weapons.size()) { System.out.println("Invalid."); pauseTiny(); return; }

        Weapon chosen = weapons.get(idx - 1);
        laneHero.getHero().equipWeapon(chosen); // does level + hand rules inside
        pauseTiny();
    }

    private void changeArmor(LaneUnit laneHero) {
    if (laneHero.getHero() == null) return;

    List<Item> items = laneHero.getHero().getInventory().getItems();
    List<Armor> armors = new ArrayList<Armor>();
    for (Item it : items) if (it instanceof Armor) armors.add((Armor) it);

    if (armors.isEmpty()) {
        System.out.println("No armor in inventory.");
        pauseTiny();
        return;
    }

    System.out.println("Current armor equipped:");
    System.out.println("  Armor: " + (laneHero.getHero().getArmor() == null ? "(none)" : laneHero.getHero().getArmor().getName()));
    System.out.println();

    System.out.println("Choose armor to equip:");
    for (int i = 0; i < armors.size(); i++) {
        Armor a = armors.get(i);
        System.out.println((i + 1) + ") " + a.getName()
                + " (DR " + a.getDamageReduction()
                + ", lvl " + a.getRequiredLevel() + ")");
    }
    System.out.println("0) Cancel");
    System.out.print("> ");

    String s = sc.nextLine().trim();
    int idx;
    try { idx = Integer.parseInt(s); }
    catch (NumberFormatException e) { System.out.println("Invalid."); pauseTiny(); return; }

    if (idx == 0) return;
    if (idx < 1 || idx > armors.size()) { System.out.println("Invalid."); pauseTiny(); return; }

    Armor chosen = armors.get(idx - 1);

    // REQUIRED: enforce level (same idea as weapon)
    if (chosen.getRequiredLevel() > laneHero.getHero().getLevel()) {
        System.out.println(laneHero.getHero().getName()
                + " is not high enough level to equip " + chosen.getName() + ".");
        pauseTiny();
        return;
    }

    laneHero.getHero().equipArmor(chosen); // do NOT remove from inventory
    System.out.println(laneHero.getHero().getName() + " equips " + chosen.getName() + ".");
    pauseTiny();
}


}
