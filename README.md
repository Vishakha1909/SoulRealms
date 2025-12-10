# Soul Realms: Emotion War


### Compile (Mac/Linux/WSL)

mkdir -p out
javac -d out $(find src -name "*.java")

mkdir out
Get-ChildItem -Recurse src -Filter *.java |
  ForEach-Object { $_.FullName } |
  javac -d out @-

### Run
java -cp out app.Main


## 1. Overview

**Soul Realms: Emotion War** is a terminal-based role-playing game implemented in Java using strong **Object-Oriented Design**.

It is based on the *Monsters and Heroes (2025)* assignment specification, but creatively re-themed around **emotional realms inside a fractured mind** instead of classic fantasy monsters. The focus of the project is:

- Clean, modular OO architecture  
- Scalable and reusable core design  
- Data-driven game content (heroes, monsters, weapons, armor, potions, spells)  
- A fully playable loop: explore → fight → shop → level up  

> The assignment emphasizes OO structure and design reasoning more than strict rule copying.  
> This project fulfills all core requirements while adding a deep custom theme.

---

## 2. Theme & Lore

After a catastrophic emotional collapse, your soul fractures into **seven Realms of Emotion**:

- **Wrath (W)** – destructive emotion  
- **Desire (D)** – craving and temptation  
- **Fear (F)** – evasive, unsettling shadows  
- **Sorrow (S)** – draining grief  
- **Anxiety (A)** – jittery, high-dodge nightmares  
- **Envy (E)** – undermining, stealing creatures  
- **Pride (P)** – imposing, high-defense juggernauts  

You control a **party of heroes**, each embodying strengths of the self.  
As you walk through the world of your own mind, you must confront emotional monsters, gather strength, and rebuild balance.

Some tiles in the world hide **fracture points** — unstable regions that trigger **mixed-emotion battles** at higher levels.

Markets are **Sanctuary Shrines**, where you can buy gear, restock potions, and re-center your mental strength.

---

## 3. Project Structure

### 3.1 Core Engine (Theme-Agnostic)

`game.core.*` contains a reusable RPG engine:

- **World & Tiles** (`World`, `Tile`, `TileCategory`, `Position`)
- **Characters** (`Character`, `Hero`, `Monster`)
- **Stats System** (`Stats`, HP/MP/Str/Dex/Def/Agi)
- **Inventory System**
- **Items** (`Weapon`, `Armor`, `Potion`, `Spell`)
- **Market** (buy/sell, per-hero)
- **Battle Engine** (turn-based combat, dodge, spells, potions)

This layer contains **no emotion-specific logic** and can be reused for future games.

---

### 3.2 Emotion War Layer (Theme-Specific)

`game.emotionwar.*` contains:

- **EmotionWarGame** – main loop, movement, menus  
- **EmotionWorldBuilder** – randomized 10×10 world, 7 emotional zones, blocked tiles, hidden fracture tiles  
- **EmotionWorldData** – world + emotion layer  
- **EmotionEncounterManager** – decides fights based on tile/emotion/level, ensures monster count = party size  
- **EmotionWarRenderer** – ASCII + ANSI-colored map renderer  
- **EmotionHero**, **EmotionMonster**, **EmotionType**, **EmotionHeroType**  
- **Factories** (load everything from `/data`):  
  - `EmotionHeroFactory`  
  - `EmotionMonsterFactory`  
  - `EmotionItemFactory`  
- **EmotionPartyBuilder** – lets the player choose 1–3 heroes at the start

---

## 4. Randomized World

Every run generates a **fresh 10×10 world**:

- All **seven emotions** appear as clean **non-overlapping 2×2 zones**
- Random **blocked tiles (#)**
- Random **fracture tiles** (`SPECIAL` internally, looks normal visually)
- Random **market placement**
- Random **starting position** (not blocked)

This guarantees:

- Variety every playthrough  
- A clean, readable, balanced map  
- Full theme coverage  

---

## 5. Controls & UI

### Overworld Controls

- `W/A/S/D` — move  
- `I` — inspect party (stats, HP, MP, gold, equipment)  
- `V` — view inventory (per-hero)  
- `U` — use potion outside battle  
- `H` — help/instructions  
- `Q` — quit  

The renderer shows:

- Full colored map  
- Party icon `@`  
- Market `M`  
- Blocked tiles `#`  
- Emotion tiles (`W, D, F, S, A, E, P`)  
- Status panel  
- Legend panel  

Fracture tiles (`SPECIAL`) **look like normal ground** but behave differently.

---

## 6. Market System

Stepping on an `M` tile opens the **Sanctuary Market**:

- You choose **which hero** is shopping  
- That hero can buy/sell:
  - Weapons (supports 1-hand, dual wield, and 2-hand)  
  - Armor  
  - Potions  
  - Spells  
- Items apply level requirements  
- Gold is **per-hero**, not shared  
- Hero’s inventory is fully isolated (assignment requirement)

---

## 7. Battle System

When entering emotion zones or fracture tiles, the encounter manager may trigger combat.

### Party Size → Monster Count

- 1 hero → 1 monster  
- 2 heroes → 2 monsters  
- 3 heroes → 3 monsters  

### Turn System

Every round:

1. **You pick which hero acts next** (from all living heroes)
2. That hero performs exactly **one** action:
   - **Attack** (physical)
   - **Cast Spell** (magic)
   - **Use Potion**
   - **Change Weapon/Armor** *(optional extension)*
   - **View Info**
   - **Skip**
3. After all heroes have acted, **monsters act**
4. New round begins

### Attack Rules

- Uses `hero.getAttackDamage()` → supports dual wield automatically  
- Monster defense reduces damage  
- Monster may **dodge**

### Magic Rules

- Costs MP  
- Uses `spell.damage + hero.dexterity`  
- Ignores half of target’s defense  
- Monster may **resist** (dodge)

### Potions

- Heal HP/MP or boost stats  
- Consumed after use  
- Only from current hero’s inventory

### Dodge

- Heroes: based on agility  
- Monsters: from data files  
- Applied to both physical and magical attacks

### Victory

- All monsters defeated  
- Surviving heroes receive **gold & XP split evenly**

### Defeat

- All heroes faint → game over

---

## 8. Inventory & Equipment

Each hero has:

- `mainHand` weapon  
- `offHand` weapon (for dual wield)  
- Or a 2-handed weapon  
- `armor` slot  
- A private `Inventory` with items they own  

### Weapon Logic

Fully implemented:

- **1-handed + 1-handed** → dual wield  
- **2-handed** → replaces both hands  
- Automatic main-hand replacement logic  
- Attack damage = sum of equipped hands  

---

