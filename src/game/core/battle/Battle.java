package game.core.battle;

import game.core.model.Hero;
import game.core.model.Monster;
import game.core.items.Item;
import game.core.items.Potion;
import game.core.items.Spell;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Battle {

    private final List<Hero> party;
    private final List<Monster> monsters;
    private final Scanner scanner;
    private final Random rng = new Random();

    public Battle(List<Hero> heroes,
                  List<? extends Monster> monsters,
                  Scanner scanner) {

        this.party = new ArrayList<Hero>(heroes);
        this.monsters = new ArrayList<Monster>(monsters);
        this.scanner = scanner;
    }

    /**
     * Runs the battle loop.
     *
     * @return true if the party survives, false if they are all defeated.
     */
    public boolean start() {
        System.out.println("----------------------------------------");
        System.out.println("A battle begins!");
        System.out.println("----------------------------------------");

        while (partyAlive() && monstersAlive()) {
            printStatus();
            heroesTurn();        // heroes act in chosen order
            if (!monstersAlive()) {
                break;
            }
            monstersTurn();      // then monsters act
        }

        if (!partyAlive()) {
            System.out.println("Your party has fallen...");
            return false;
        } else {
            System.out.println("The monsters are defeated!");
            awardRewards();
            return true;
        }
    }

    // ---------- Turn phases ----------

    /**
     * One round of hero actions.
     * Player chooses which living hero acts, until each has acted once
     * or the monsters are all dead.
     */
    private void heroesTurn() {
        List<Hero> pending = getAliveHeroes();
        if (pending.isEmpty()) return;

        while (!pending.isEmpty() && monstersAlive()) {
            System.out.println();
            System.out.println("Choose which hero acts this round:");
            for (int i = 0; i < pending.size(); i++) {
                Hero h = pending.get(i);
                System.out.println("  [" + i + "] " + h.getName() +
                        " (HP " + h.getHp() + "/" + h.getStats().getMaxHp() +
                        ", MP " + h.getMp() + "/" + h.getStats().getMaxMp() + ")");
            }
            int heroIdx = promptIntBetween("> ", 0, pending.size() - 1);
            Hero acting = pending.get(heroIdx);

            // Hero action menu
            heroTurn(acting);

            // after one action, that hero is done for this round
            pending.remove(heroIdx);

            if (!monstersAlive()) {
                return;
            }
        }
    }

    /**
     * Single hero's menu for their action.
     * 1 move = exactly 1 thing, so we return after one command.
     */
    private void heroTurn(Hero h) {
        System.out.println();
        System.out.println("It's " + h.getName() + "'s turn.");
        System.out.println("1) Attack");
        System.out.println("2) Cast Spell");
        System.out.println("3) Use Potion");
        System.out.println("4) Skip");
        int choice = promptIntBetween("Choose action: ", 1, 4);

        switch (choice) {
            case 1:
                heroAttack(h);
                break;
            case 2:
                castSpell(h);
                break;
            case 3:
                usePotion(h);
                break;
            case 4:
            default:
                System.out.println(h.getName() + " hesitates and does nothing.");
                break;
        }
    }

    private void heroAttack(Hero h) {
        List<Monster> alive = getAliveMonsters();
        if (alive.isEmpty()) return;

        System.out.println("Choose target:");
        for (int i = 0; i < alive.size(); i++) {
            Monster m = alive.get(i);
            System.out.println("[" + i + "] " + m.getName() +
                    " (HP " + m.getHp() + "/" + m.getStats().getMaxHp() + ")");
        }
        int idx = promptIntBetween("> ", 0, alive.size() - 1);
        Monster target = alive.get(idx);

        // Dodge check
        if (rng.nextDouble() < target.getDodgeChance()) {
            System.out.println(target.getName() + " dodged the attack!");
            return;
        }

        int raw = h.getAttackDamage(); // uses dual-wield logic from Hero
        int dmg = Math.max(0, raw - target.getDefense());
        if (dmg <= 0) {
            System.out.println(h.getName() + "'s attack couldn't pierce " +
                    target.getName() + "'s defenses.");
            return;
        }

        target.takeDamage(dmg);
        System.out.println(h.getName() + " hits " + target.getName() +
                " for " + dmg + " damage.");

        if (!target.isAlive()) {
            System.out.println(target.getName() + " is defeated!");
        }
    }

    private void monstersTurn() {
        System.out.println();
        System.out.println("Monsters strike back!");

        List<Hero> aliveHeroes = getAliveHeroes();

        for (int i = 0; i < monsters.size(); i++) {
            Monster m = monsters.get(i);
            if (!m.isAlive()) continue;
            aliveHeroes = getAliveHeroes();
            if (aliveHeroes.isEmpty()) return;

            Hero target = aliveHeroes.get(rng.nextInt(aliveHeroes.size()));
            System.out.println(m.getName() + " attacks " + target.getName() + ".");

            // Hero dodge check
            if (rng.nextDouble() < target.getDodgeChance()) {
                System.out.println(target.getName() + " dodges the blow!");
                continue;
            }

            int raw = m.getAttackDamage();
            int dmg = Math.max(0, raw - target.getDefense());
            if (dmg <= 0) {
                System.out.println(target.getName() + "'s armor absorbs the hit.");
                continue;
            }

            target.takeDamage(dmg);
            System.out.println(target.getName() + " takes " + dmg + " damage.");

            if (!target.isAlive()) {
                System.out.println(target.getName() + " has fallen!");
            }
        }
    }

    private void usePotion(Hero h) {
        List<Item> items = h.getInventory().getItems();
        List<Potion> potions = new ArrayList<Potion>();
        for (int i = 0; i < items.size(); i++) {
            Item it = items.get(i);
            if (it instanceof Potion) {
                potions.add((Potion) it);
            }
        }

        if (potions.isEmpty()) {
            System.out.println("No potions in " + h.getName() + "'s inventory.");
            return;
        }

        System.out.println("Choose a potion to use on " + h.getName() + ":");
        for (int i = 0; i < potions.size(); i++) {
            Potion p = potions.get(i);
            System.out.println("[" + i + "] " + p.getName() + " (+" + p.getAmount() + " " + p.getStat() + ")");
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
        if (idx < 0 || idx >= potions.size()) {
            System.out.println("Invalid index.");
            return;
        }

        Potion chosen = potions.get(idx);
        chosen.applyTo(h);               // apply to this hero
        h.getInventory().remove(chosen); // consume potion
    }

    private void castSpell(Hero h) {
        List<Item> items = h.getInventory().getItems();
        List<Spell> spells = new ArrayList<Spell>();
        for (int i = 0; i < items.size(); i++) {
            Item it = items.get(i);
            if (it instanceof Spell) {
                spells.add((Spell) it);
            }
        }

        if (spells.isEmpty()) {
            System.out.println("No spells available for " + h.getName() + ".");
            return;
        }

        if (!monstersAlive()) return;

        System.out.println("Choose a spell to cast:");
        for (int i = 0; i < spells.size(); i++) {
            Spell s = spells.get(i);
            System.out.println("[" + i + "] " + s.getName() +
                    " (Damage " + s.getDamage() +
                    ", MP cost " + s.getManaCost() + ")");
        }
        System.out.println("[X] Cancel");
        System.out.print("> ");

        String input = scanner.nextLine().trim().toUpperCase();
        if ("X".equals(input)) return;

        int spellIdx;
        try {
            spellIdx = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice.");
            return;
        }
        if (spellIdx < 0 || spellIdx >= spells.size()) {
            System.out.println("Invalid index.");
            return;
        }

        Spell spell = spells.get(spellIdx);
        if (!h.spendMp(spell.getManaCost())) {
            System.out.println("Not enough MP to cast " + spell.getName() + ".");
            return;
        }

        List<Monster> alive = getAliveMonsters();
        if (alive.isEmpty()) return;

        System.out.println("Choose a target:");
        for (int i = 0; i < alive.size(); i++) {
            Monster m = alive.get(i);
            System.out.println("[" + i + "] " + m.getName() +
                    " (HP " + m.getHp() + "/" + m.getStats().getMaxHp() + ")");
        }
        int targetIdx = promptIntBetween("> ", 0, alive.size() - 1);
        Monster target = alive.get(targetIdx);

        // Dodge check
        if (rng.nextDouble() < target.getDodgeChance()) {
            System.out.println(target.getName() + " resists the spell!");
            return;
        }

        int raw = spell.getDamage() + h.getStats().getDexterity();
        int dmg = Math.max(0, raw - target.getDefense() / 2); // magic ignores half defense
        if (dmg <= 0) {
            System.out.println("The spell fizzles against " + target.getName() + ".");
            return;
        }

        target.takeDamage(dmg);
        System.out.println(h.getName() + " casts " + spell.getName() +
                " on " + target.getName() + " for " + dmg + " damage.");

        if (!target.isAlive()) {
            System.out.println(target.getName() + " is annihilated!");
        }
    }

    // ---------- Rewards ----------

    private void awardRewards() {
        int totalGold = 0;
        int totalXp = 0;

        for (int i = 0; i < monsters.size(); i++) {
            Monster m = monsters.get(i);
            // Simple reward formulas; tweak as desired
            totalGold += m.getLevel() * 20;
            totalXp   += m.getLevel() * 10;
        }

        List<Hero> alive = getAliveHeroes();
        if (alive.isEmpty()) {
            // nobody to receive it, but this shouldn't happen if we call only on victory
            return;
        }

        int goldEach = totalGold / alive.size();
        int xpEach   = totalXp   / alive.size();

        System.out.println();
        System.out.println("Battle rewards:");
        System.out.println("  Total Gold: " + totalGold);
        System.out.println("  Total XP  : " + totalXp);
        System.out.println("  (" + goldEach + " gold and " + xpEach +
                " XP to each surviving hero)");

        for (int i = 0; i < alive.size(); i++) {
            Hero h = alive.get(i);
            h.addGold(goldEach);
            h.addExperience(xpEach);
        }
    }

    // ---------- Helpers ----------

    private void printStatus() {
        System.out.println();
        System.out.println("============== BATTLE STATUS ==============");
        System.out.println("-- Party -----------------------------------");
        for (int i = 0; i < party.size(); i++) {
            Hero h = party.get(i);
            System.out.printf("  %-14s HP %3d/%-3d  MP %3d/%-3d%n",
                    h.getName(),
                    h.getHp(),  h.getStats().getMaxHp(),
                    h.getMp(),  h.getStats().getMaxMp());
        }

        System.out.println("-- Monsters --------------------------------");
        for (int i = 0; i < monsters.size(); i++) {
            Monster m = monsters.get(i);
            System.out.printf("  %-20s HP %3d/%-3d%n",
                    m.getName(),
                    m.getHp(), m.getStats().getMaxHp());
        }
        System.out.println("============================================");
    }

    private boolean partyAlive() {
        for (int i = 0; i < party.size(); i++) {
            if (party.get(i).isAlive()) return true;
        }
        return false;
    }

    private boolean monstersAlive() {
        for (int i = 0; i < monsters.size(); i++) {
            if (monsters.get(i).isAlive()) return true;
        }
        return false;
    }

    private List<Hero> getAliveHeroes() {
        List<Hero> alive = new ArrayList<Hero>();
        for (int i = 0; i < party.size(); i++) {
            Hero h = party.get(i);
            if (h.isAlive()) {
                alive.add(h);
            }
        }
        return alive;
    }

    private List<Monster> getAliveMonsters() {
        List<Monster> alive = new ArrayList<Monster>();
        for (int i = 0; i < monsters.size(); i++) {
            Monster m = monsters.get(i);
            if (m.isAlive()) {
                alive.add(m);
            }
        }
        return alive;
    }

    private int promptIntBetween(String prompt, int min, int max) {
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