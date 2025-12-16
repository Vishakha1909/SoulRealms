# 🕯️ Soul Realms Arcade

A terminal-based Java game arcade featuring multiple role-playing strategy games built on a shared object-oriented engine.

Compile
-------

### Mac / Linux / WSL

mkdir -p out  
javac -d out $(find src -name "*.java")   `

### Windows (PowerShell)

mkdir out  
javac -d out src/app/*.java src/game/core/game/*.java src/game/core/model/*.java src/game/core/items/*.java src/game/core/world/*.java src/game/core/battle/*.java src/game/core/market/*.java src/game/emotionwar/*.java src/game/emotionwar/model/*.java src/game/emotionwar/factory/*.java src/game/emotionwar/world/*.java src/game/emotionwar/ui/*.java src/game/emotionwar/logic/*.java src/game/emotionlanes/world/*.java src/game/emotionlanes/ui/*.java src/game/emotionlanes/model/*.java  src/game/emotionlanes/factory/*.java src/game/emotionlanes/logic/*.java src/game/emotionlanes/*.java src/game/emotionlanes/terrain/*.java src/game/emotionlanes/market/*.java 

Run
---

java -cp out app.Main   `

1\. Overview
------------

**Soul Realms Arcade** is a Java-based terminal game collection built using strong **Object-Oriented Design principles**.

The project contains multiple playable games that share a **common reusable core engine**, while each game implements its own rules, world logic, and UI.

The new game in this arcade is:

### ⭐ **Emotion Lanes: Defense of the Core**

(A strategy game inspired by _Legends of Valor_)

A secondary game, **Emotion War**, is also included to demonstrate engine reuse.

This project emphasizes:

*   Clean OO architecture
    
*   Separation of engine vs game logic
    
*   Extendable systems (terrain, combat, inventory, AI)
    
*   A fully playable turn-based loop
    

2\. Emotion Lanes: Defense of the Core
--------------------------------------

Emotion Lanes is a **lane-based strategy RPG** where heroes defend their emotional core against invading monsters.

The world is divided into **three vertical lanes**, separated by impassable walls.Heroes advance **upward**, monsters advance **downward**.

Victory conditions:

*   **Win**: Any hero reaches the Monster Nexus (top row)
    
*   **Lose**: Any monster reaches the Hero Nexus (bottom row)
    

3\. World & Terrain
-------------------

The board is rendered entirely in ASCII with ANSI colors.

### Terrain Types

*   N – Nexus (Hero Core / Monster Rift)
    
*   I – Impassable wall
    
*   P – Plain
    
*   B – Bush (stat buffs)
    
*   C – Cave (stat buffs)
    
*   K – Koulou (stat buffs)
    
*   O – Obstacle (blocks movement, can be removed by heroes)
    

Terrain tiles apply **automatic buffs/debuffs** when units enter or leave them.

Obstacle tiles:

*   Block heroes and monsters
    
*   Heroes may spend a turn to remove them
    
*   Become Plain after removal
    

Terrain is **randomly distributed each run**, while Nexus and walls remain fixed.

4\. Turn Structure
------------------

The game proceeds in **rounds**, each with two phases:

### 4.1 Hero Phase

*   Each living hero takes **exactly one action**
    
*   Dynamic menu based on context:
    
    *   Move
        
    *   Teleport (across lanes)
        
    *   Attack (if enemy in range)
        
    *   Cast Spell (if enemy in range)
        
    *   Use Potion
        
    *   Change Weapon
        
    *   Change Armor
        
    *   Inventory (free action)
        
    *   Clear Obstacle (if adjacent)
        
    *   Recall to Nexus
        
    *   Market (only at Hero Nexus)
        
    *   Skip
        

Attack and spell options appear **only when an enemy is in range**(range = same tile or adjacent tile in same lane).

### 4.2 Monster Phase

*   Monsters automatically act
    
*   They:
    
    *   Attack heroes in range
        
    *   Otherwise move toward the Hero Nexus
        
*   A **Monster Phase Log** shows:
    
    *   Which monsters moved
        
    *   Which monsters attacked
        
*   Player presses Enter to proceed to the next round
    

5\. Combat System
-----------------

Combat is turn-based and uses shared engine logic.

### Attack Rules

*   Range-limited (current tile or adjacent)
    
*   Dodge chance based on agility
    
*   Damage reduced by armor
    

### Spells

*   Cost MP
    
*   Deal magic damage
    
*   Scale with hero dexterity
    
*   May be dodged
    

### Potions

*   Heal HP / MP or boost stats
    
*   Single-use
    
*   Stored per-hero
    

6\. Inventory & Equipment
-------------------------

Each hero has a **private inventory**.

### Equipment Slots

*   Main hand weapon
    
*   Off hand weapon (for dual wield)
    
*   OR one 2-handed weapon
    
*   Armor slot
    

### Weapon Rules

*   Two 1-handed weapons → dual wield
    
*   One 2-handed weapon → replaces both hands
    
*   Level requirements enforced
    
*   Equipping does **not** remove items from inventory
    

Armor:

*   Reduces incoming damage
    
*   Level requirements enforced
    

7\. Market System
-----------------

Markets are available **only at the Hero Nexus**.

*   Each hero shops individually
    
*   Buy:
    
    *   Weapons
        
    *   Armor
        
    *   Potions
        
    *   Spells
        
*   Gold is tracked **per hero**
    
*   Level restrictions apply
    

8\. Death, Respawn & Progression
--------------------------------

*   When a hero dies:
    
    *   They respawn at their Nexus at the **start of the next round**
        
    *   HP and MP are fully restored
        
*   End of round:
    
    *   Living heroes regenerate a small amount of HP/MP
        
*   Monster waves spawn periodically based on difficulty
    

9\. Controls & UI
-----------------

*   Fully keyboard-driven
    
*   ANSI-colored board
    
*   Dynamic menus
    
*   Phase banners:
    
    *   ROUND X | HERO PHASE
        
    *   ROUND X | MONSTER PHASE
        
*   Logs and pauses ensure no information is lost offscreen
    

10\. Emotion War (Secondary Game)
---------------------------------

Emotion War is a classic **exploration-based RPG** included to demonstrate engine reuse.

It features:

*   Free-roaming map
    
*   Emotion-themed regions
    
*   Turn-based party combat
    
*   Markets, leveling, spells, and inventory
    

Emotion War and Emotion Lanes share:

*   Items
    
*   Characters
    
*   Combat rules
    
*   Inventory system
    
*   Market logic

11\. Design Patterns Used
-------------------------

*   **Strategy** – terrain effects, combat calculations
    
*   **Factory** – heroes, monsters, items loaded from data
    
*   **State** – round/phase control
    
*   **Model–View–Controller (MVC)** – game logic vs renderer
    
*   **Single Responsibility** – clear separation of concerns
    
*   **Composition over Inheritance** – stats, equipment, effects
    

12\. Summary
------------

**Soul Realms Arcade** demonstrates:

*   Strong OO design
    
*   Clean separation of reusable engine and game-specific logic
    
*   Two fully playable games
    
*   Dynamic UI and strategic gameplay
    
*   Extendable systems for future games
    

Emotion Lanes serves as the **primary showcase** of advanced turn-based strategy and system interaction.

-----

## GAME 2

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