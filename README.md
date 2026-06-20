# SkyBlock Core

> **A faithful Hypixel SkyBlock recreation for Paper 1.21+ servers — skills, dungeons, economy, and beyond.**

[![Build](https://github.com/eMsujt/testprojectpixel/actions/workflows/build.yml/badge.svg)](https://github.com/eMsujt/testprojectpixel/actions/workflows/build.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://adoptium.net/)
[![Paper](https://img.shields.io/badge/Paper-1.21%2B-brightgreen.svg)](https://papermc.io/)

A Hypixel-SkyBlock-style Minecraft plugin built on Paper. Implemented as a Maven multi-module project targeting Paper 1.21+, it recreates the full SkyBlock progression loop — from private islands and skills to dungeons, the economy, and end-game zones — as a self-contained server-side plugin.

---

## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Building](#building)
- [Installation](#installation)
- [Module Structure](#module-structure)
- [Feature Managers](#feature-managers)
- [Commands](#commands)
- [Development](#development)
- [Documentation](#documentation)
- [Contributing](#contributing)
- [License](#license)

---

## Features

### Skills & Progression
- **12 skills** — Combat, Mining, Farming, Foraging, Fishing, Enchanting, Alchemy, Taming, Social, Carpentry, Runecrafting, and Dungeoneering, each with authentic Hypixel XP tables and level caps.
- **Collections** — per-resource collection tiers gate crafting recipes; all 200+ collection items tracked per-player.
- **SkyBlock Level** — cumulative XP across every progression system rolls into a single SkyBlock level with tier rewards.
- **Fairy Souls** — discoverable fairy souls grant permanent stat bonuses via Tia the Fairy exchange.

### Private Islands
- **Island creation** — each player gets a personal schematic island; co-op members share the same island.
- **Minions** — 20+ minion types (Combat, Farming, Mining, Fishing, Foraging …) with fuel slots, upgrade slots, storage, and hopper auto-sell.
- **Upgrades** — island size upgrades, visitor system, and warp point management.

### Economy
- **Coins / purse** — server-wide coin economy managed by `EconomyManager`.
- **Bank** — personal and co-op bank accounts with interest, deposits, and withdrawals (`BankManager` / `BankingManager`).
- **Bazaar** — live buy/sell order book with instant buy/sell pricing and automated order fulfillment.
- **Auction House** — BIN listings and bid-based auctions; all standard SkyBlock auction types.
- **NPC shops** — NPC-driven shop catalogues (`ShopManager`, `NPCShopManager`) with configurable stock and prices.
- **Sacks** — auto-pickup sacks with configurable per-resource storage limits.

### Combat & Slayer
- **Custom mobs** — all SkyBlock mobs with configurable HP, damage, and loot tables.
- **SkyBlock damage formula** — Strength, Crit Damage, Defense, and Effective HP computed via `StatManager` and applied in `CombatListener`.
- **Item/set abilities** — weapon and armor abilities with mana costs managed by `ItemAbilityManager`.
- **Slayer quests** — Zombie, Spider, Wolf, Enderman, Blaze, and Vampire tiers with boss spawn costs, corruption, and RNG-meter rewards.
- **Bestiary** — per-mob kill tracking with milestone bonuses.

### Dungeons
- **Catacombs** — floors F1–F7 and Master Mode M1–M7, with room generation, mob scaling, and puzzle rooms.
- **Dungeon classes** — Healer, Mage, Berserk, Archer, Tank — each with class-specific perks and stat scaling.
- **Dungeon score** — skill score, speed score, exploration, and bonus score feeding into S/S+ calculation.
- **Essence system** — Undead, Wither, Dragon, Spider, Crimson, Wither Essence shop upgrades.
- **Experimentation Table** — Superpairs, Chronomatron, and Ultrasequencer enchanting minigames.

### Gear & Accessories
- **Custom enchantments** — 50+ SkyBlock-exclusive enchantments with tiered levels.
- **Reforges** — reforge stones with per-rarity stat bonuses; Blacksmith and Reforgeable items.
- **Forge** — Dwarven and Molten-tier forge recipes with real processing times.
- **Pets** — pet ownership, passive XP levelling (1–100), and active-pet perk injection into stats.
- **Talismans / Accessories** — equipped talismans aggregate stat bonuses scaled by rarity; Magical Power calculated across the bag.
- **Wardrobe** — up to 9 named armor outfits for instant full-set swapping.
- **Runes** — rune application to items and active-rune visual/stat effects.

### End-game Zones
- **Dwarven Mines & Crystal Hollows** — Heart of the Mountain perk tree (HOTM), powder mining, gemstone nodes, and Dwarven commissions.
- **Crimson Isle** — Kuudra boss tiers (Infernal to Molten), Mage vs. Barbarian faction reputation, and attribute shards.
- **The Rift** — parallel dimension with Rift-specific currency and progression state.
- **Jerry's Workshop** — seasonal event with gifts, snow minions, and snow cannon.
- **Carnival** — rotational carnival event games and prize rewards.

### Social & Quality of Life
- **Parties** — invite, kick, leader transfer, and shared dungeon sessions.
- **Guilds** — create/manage player guilds with membership and rank management.
- **Friends** — friend list with online/offline tracking.
- **Mail** — asynchronous player-to-player mail with item attachment.
- **Leaderboards** — configurable skill and stat leaderboards.
- **Mayor** — election cycles with active mayor perks affecting server-wide gameplay.
- **SkyBlock Calendar** — in-game seasons and scheduled events (Spooky Festival, Season of Jerry, …).
- **Notifications** — action bar, boss bar, title, and scoreboard display layers.

### Additional Systems
- **Garden** — dedicated farming island with Jacob's Contest integration, crop milestones, visitors, and pest extermination.
- **Trophy Fishing** — 22 unique trophy fish types with weighted drop-chance tables and catch tracking.
- **Museum** — item donation system with completion-tier rewards.
- **Chocolate Factory** — idle-production system with upgrades.
- **Net Worth** — real-time valuation of a player's full inventory, pets, and currencies.
- **Potions** — custom brewing recipes with SkyBlock-specific durations and splash variants.
- **Bingo** — seasonal bingo cards with goal tracking and prize tiers.

See the [Feature Managers](#feature-managers) table for the authoritative manager registry, and [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) for the full module map.

---

## Prerequisites

| Requirement | Version |
|-------------|---------|
| Java | 25+ |
| Paper server | 1.21+ |
| Maven | 3.8+ |

---

## Building

```bash
# Clone the repository
git clone <repo-url>
cd <repo-dir>

# Build all modules and produce shaded JARs
mvn clean package -DskipTests

# The deployable plugin JAR is produced by skyblock-core:
# skyblock-core/target/skyblock-core-1.0.0-SNAPSHOT.jar
```

---

## Installation

1. Build the project (see above).
2. Copy `skyblock-core/target/skyblock-core-1.0.0-SNAPSHOT.jar` into your Paper server's `plugins/` directory.
3. Start or restart the server.
4. The plugin registers automatically via `SkyblockPlugin#onEnable`.

---

## Module Structure

The parent POM (`pom.xml`) aggregates all modules:

| Module | Purpose |
|--------|---------|
| `api` | Shared API interfaces and events |
| `enchantments` | Custom enchantment definitions |
| `items` | Custom item types and builders |
| `dungeons` | Catacombs floor logic and dungeon rooms |
| `foraging` | Foraging skill and log/sapling mechanics |
| `farming` | Farming skill, crop, and Garden logic |
| `storage` | Backpack, vault, and personal storage |
| `progression` | Skill XP, collections, and HOTM tree |
| `social` | Party, guild, friends, mail, and trade |
| `combat-extras` | Slayer, bestiary, and trophy fishing |
| `world` | Island generation, warps, and zone management |
| `plugin` | Thin plugin bootstrap (delegates to `skyblock-core`) |
| `skyblock-core` | **Deployable plugin JAR** — all managers, commands, menus |

`skyblock-core` is the canonical home for everything under `com.skyblock.core.*`. The full package-layout conventions and the one-canonical-home rule live in [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

---

## Feature Managers

Most gameplay state is owned by a single authoritative manager under
**`com.skyblock.core.manager`** (a few large systems keep their own feature
sub-package — e.g. `com.skyblock.core.auction.manager.AuctionHouseManager`,
`com.skyblock.core.crafting.manager.CraftingManager`,
`com.skyblock.core.profile.manager.ProfileManager`). Import these directly — never
a copy. See [`docs/FEATURES.md`](docs/FEATURES.md) for the full per-manager feature list.

| Manager | Responsibility |
|---------|----------------|
| `AccessoryManager` / `AccessoryBagManager` | Talismans/accessories and the magical-power bag |
| `AccessoriesManager` | Accessory stat aggregation layer (complements `AccessoryBagManager`) |
| `ActionBarManager` | Action bar XP, health, mana, and event display |
| `AlchemyManager` | Alchemy skill XP and potion brewing recipes |
| `AttributeManager` | Crimson Isle attribute shards and per-attribute levelling |
| `BackpackManager` | Backpack tiers (Small 9 / Medium 18 / Large 27 / Jumbo 36) |
| `BankManager` / `BankingManager` | Personal/co-op bank balance, interest, deposits/withdrawals |
| `BazaarManager` | Buy/sell order book, instant buy/sell pricing, order fulfillment |
| `BestiaryManager` | Mob-kill tracking and bestiary tiers |
| `BingoManager` | Bingo card generation, goal tracking, and completion rewards |
| `CalendarManager` | SkyBlock calendar, seasons, and scheduled events |
| `CarnivalManager` | Carnival event games and rewards |
| `CarpentryManager` | Carpentry skill XP and crafting recipes |
| `ChatManager` | Chat channel switching (Global / Party / Guild / Trade) |
| `ChocolateFactoryManager` | Idle Chocolate Factory production, upgrades, and prestige |
| `CollectionManager` | Resource collections and tier-unlock thresholds |
| `CollectionRewardManager` | Collection tier reward distribution |
| `CombatManager` | Combat XP, kill counts, and death tracking |
| `CommissionManager` | Dwarven Mines commissions — assignment, tracking, and rewards |
| `CrimsonIsleManager` | Kuudra tiers and Mage/Barbarian faction reputation |
| `CrystalHollowsManager` | Dwarven Mines / Crystal Hollows zones, gemstones, powder |
| `DojoManager` | Dojo challenge scores and progress tracking |
| `DragonManager` | Ender Dragon fights and contribution rewards |
| `DungeonClassManager` | Dungeon class selection and class-specific perks |
| `DungeonManager` | Catacombs floors F1–F7 + Master Mode, dungeon classes |
| `DungeonStatsManager` | Dungeon run statistics, scores, and completion history |
| `EconomyManager` | Coins / purse balance |
| `EnchantmentManager` / `EnchantingManager` | Custom enchantments and the enchant table |
| `EnderChestManager` | Per-player Ender Chest contents and GUI |
| `EssenceManager` | Essence currencies and shop upgrades |
| `EssenceShopManager` | Essence shop perk purchases and upgrade tiers |
| `EventManager` | Server-wide event scheduling and lifecycle |
| `ExperimentationTableManager` | Superpairs / Chronomatron / Ultrasequencer minigames |
| `FairySoulManager` | Fairy soul discovery and exchange bonuses |
| `FishingManager` | Sea creatures, fishing XP, and treasure |
| `ForgeManager` | Forge item recipes and processing slots |
| `FortuneManager` | Mining and farming fortune bonus calculations |
| `GardenManager` | Jacob's farming contests, plot unlocks, crop milestones |
| `HarpManager` | Melody's Harp song progress and Intelligence bonuses |
| `HOTMManager` | Heart of the Mountain tree — perk nodes, tiers, powder |
| `IslandManager` | Private island creation, upgrades, and visiting |
| `ItemAbilityManager` | Weapon/armor item and full-set abilities with mana costs |
| `ItemStatManager` | Per-item stat computation and lore display |
| `JacobFarmingManager` | Jacob's farming event participation and crop rewards |
| `JacobManager` | Jacob's farming contest scheduling and participation rewards |
| `JacobsContestManager` | Jacob's contest crop-specific scheduling and medal tracking |
| `JerryWorkshopManager` | Jerry's Workshop gifts, snow minions, and snow cannon |
| `KuudraManager` | Kuudra boss tier management and contribution tracking |
| `ManaManager` | Mana pool, regeneration rate, and overflow cost |
| `MayorManager` | Mayor elections and active perks |
| `MiningManager` | Mining XP, speed, fortune, ores, and powder |
| `MinionManager` | Minion types/tiers, fuel, upgrade slots, hopper auto-sell |
| `MobManager` | Custom mob registration, stats, and spawn management |
| `MuseumManager` | Museum donations and completion rewards |
| `NetworthManager` | Player net-worth valuation across items and currencies |
| `NetherwartIslandManager` | Netherwart Island zone and crop-specific mechanics |
| `NPCShopManager` | Static NPC shop catalogues (Hypixel Hub shops) |
| `PartyManager` | Party invites, membership, and leadership |
| `PestManager` | Garden pest spawning, tracking, and extermination rewards |
| `PetManager` / `PetsManager` | Pet ownership, levelling, and active-pet perks |
| `PlayerDataManager` | Per-player data loading, saving, and persistence |
| `PotionManager` | Brewing-stand recipes, potion levels/durations, splash potions |
| `QuestManager` | Quest tracking and completion |
| `ReforgeManager` / `RepairManager` | Reforge stones/stats and item repair |
| `ReputationManager` | Faction reputation tracking |
| `RiftManager` | The Rift dimension state and currency |
| `RunecraftingManager` | Runecrafting skill XP and rune creation recipes |
| `RuneManager` | Rune item types, application, and active-rune effects |
| `RunManager` | Dungeon run lifecycle, scoring, and completion |
| `SackManager` | Sack storage and auto-pickup |
| `ScoreboardManager` | Sidebar scoreboard display and layout |
| `ShopManager` | NPC shop catalogues and transactions |
| `SkillManager` | Skill levels and XP |
| `SkyblockLevelManager` | SkyBlock level XP accumulation and tier rewards |
| `SkyBlockEventManager` | SkyBlock event scheduling and zone-wide broadcast |
| `SlayerManager` | Slayer quest tiers, boss spawn cost, and rewards |
| `StatManager` | Effective player stat calculation and display |
| `StorageManager` | Personal storage and backpack pages |
| `TabListManager` | Tab list header/footer display |
| `TalismanManager` / `TalismanBagManager` | Talisman bag GUI and stat aggregation |
| `TamingManager` | Taming skill XP and pet passive bonuses |
| `TradeManager` | Peer-to-peer trading sessions |
| `TrophyFishManager` | Trophy fish catch tracking and drop-chance tables |
| `WardrobeManager` | Named armor outfits and full-set swapping |
| `WarpManager` | Warp point creation, storage, and teleportation |

---

## Commands

| Command | Alias | Description |
|---------|-------|-------------|
| `/accessory` | — | Manage accessory rarities and stat multipliers |
| `/accessories` | — | View and manage your accessory bag |
| `/accessorybag` | `/ab`, `/bag` | Open the Accessory Bag GUI |
| `/ability` | — | View and manage item abilities |
| `/achievement` | — | View your SkyBlock achievements |
| `/alchemy` | — | Brew potions using the SkyBlock alchemy system |
| `/auctionhouse` | `/ah`, `/auction` | Manage Auction House listings (BIN and bid-based) |
| `/backpack` | — | Create and manage personal item backpacks |
| `/bank` | — | Personal and co-op bank deposit/withdraw/balance |
| `/banking` | `/coins` | Open the Banking GUI |
| `/bazaar` | `/bz`, `/bazar` | Open the Bazaar order book |
| `/bazaarmenu` | — | Open the Bazaar GUI |
| `/bestiary` | `/bst` | Track your mob kill counts and bestiary tiers |
| `/booster` | — | Activate XP or coin boosters |
| `/calendar` | — | View the SkyBlock calendar and events |
| `/chat` | — | Switch your active chat channel |
| `/chocolatefactory` | `/cf`, `/chocolate` | Open the Chocolate Factory idle-production menu |
| `/collection` | `/col` | View your collection progress |
| `/collections` | — | View your SkyBlock collection progress |
| `/collectionsmenu` | — | Open the Collections GUI |
| `/combat` | — | View and manage your combat stats |
| `/commission` | `/commissions` | Manage King's Commissions (list, generate, claim) |
| `/compost` | — | View and manage Garden Composter organic matter, fuel, and compost |
| `/cooldown` | — | View and manage active skill/ability cooldowns |
| `/coop` | — | Manage your co-op island group |
| `/crafting` | — | Browse SkyBlock crafting recipes and track history |
| `/crimson` | — | View and manage Crimson Isle faction and reputation |
| `/crimsonisle` | — | Open the Crimson Isle overview menu |
| `/crystalhollows` | — | View and manage Crystal Hollows zones and crystal progress |
| `/dailyreward` | — | Claim your daily coin reward or check time until next claim |
| `/dojo` | — | Open the Dojo challenge scores menu |
| `/dungeon` | `/da` | Enter and manage Catacombs dungeon runs |
| `/dungeonclass` | — | View and select your dungeon class |
| `/dungeons` | `/catacombs` | Open the Catacombs menu |
| `/enchant` | — | Open the enchanting table GUI |
| `/enchanting` | — | View and apply SkyBlock enchantments |
| `/enderchest` | `/ec` | Open your SkyBlock Ender Chest |
| `/essence` | — | View and manage your SkyBlock essence balances |
| `/essenceshop` | — | Open the Essence Shop to upgrade perks |
| `/event` | — | View and join active SkyBlock events |
| `/fairysoul` | `/fairysouls` | View and manage your fairy soul collection |
| `/fishing` | — | View fishing stats and trophy fish catches |
| `/foraging` | — | View foraging level, XP, and log chop counts |
| `/foragingmenu` | — | Open the Foraging GUI |
| `/forge` | `/blacksmith`, `/skyforge` | Forge items using the SkyBlock forge |
| `/friend` | — | Manage your SkyBlock friend list |
| `/garden` | — | View and manage your Garden plot, visitors, and crop upgrades |
| `/gemstone` | — | View and manage your gemstone collection |
| `/guild` | — | Create and manage player guilds |
| `/harp` | — | View Melody's Harp song progress and Intelligence bonus |
| `/hotm` | `/hotmtree` | View and manage Heart of the Mountain perk tree |
| `/hub` | `/spawn` | Teleport to the configured hub |
| `/island` | `/is` | Open your SkyBlock island menu |
| `/jacobscontest` | — | Open Jacob's Farming Contest menu |
| `/jerryworkshop` | — | View Jerry's Workshop gifts, snow minions, and snow cannon |
| `/kuudra` | — | Kuudra boss tiers and contribution tracking |
| `/leaderboard` | `/lb` | View player leaderboards |
| `/mail` | — | Send, read, and manage player mail messages |
| `/mailbox` | — | Mailbox system: list, read, delete, send |
| `/mayor` | — | View the active mayor and vote for candidates |
| `/menu` | — | Open the SkyBlock main menu |
| `/mining` | — | View mining level, XP, speed, powder, zones, and HOTM info |
| `/miningcommission` | — | Open the King's Commissions mining menu |
| `/miningzone` | `/mz` | View or change your current mining zone |
| `/minion` | `/minions` | Manage your placed minions, fuel, and upgrades |
| `/minionsmenu` | — | Open the Minions GUI |
| `/museum` | — | Donate items and view your museum collection progress |
| `/npc` | — | Interact with SkyBlock NPCs and their shops |
| `/npcshop` | — | Browse and buy from static NPC shops via GUI |
| `/party` | — | Manage your party invites and membership |
| `/pet` | `/pets` | Manage your pets and active-pet perks |
| `/profile` | — | View your SkyBlock profile |
| `/quest` | — | Manage your SkyBlock quests |
| `/reforge` | — | View and apply item reforges |
| `/repair` | — | Repair items in hand or entire inventory (`hand`/`all`) |
| `/rift` | — | View and manage your Rift dimension progress |
| `/run` | — | Track your dungeon run statistics |
| `/runes` | — | Open the Runes menu |
| `/runecrafting` | — | View your Runecrafting skill level and rune collection |
| `/sack` | — | View the contents of your sacks |
| `/season` | — | View or set the current SkyBlock season (op-only set/advance) |
| `/shop` | — | List NPC shops and their buy/sell prices |
| `/skyblock` | `/sb`, `/sky` | Open the SkyBlock main menu |
| `/skyblock-level` | `/sblevel` | View and manage your SkyBlock level and XP |
| `/slay` | — | Manage Slayer quests and view boss info |
| `/slayer` | `/sl` | Manage Slayer quests and view boss info |
| `/skills` | — | View your SkyBlock skill levels and XP |
| `/stat` | — | View your effective SkyBlock player stats |
| `/stats` | — | View and manage your SkyBlock player stats menu |
| `/storage` | — | View and manage your personal storage pages |
| `/talisman` | `/ta` | Open the Talisman Bag GUI |
| `/title` | — | Manage your SkyBlock title |
| `/trade` | — | Trade items with another player |
| `/trophyfish` | — | View trophy fish catch counts and drop chances |
| `/trophyfishing` | — | Open the Trophy Fishing GUI (all 22 trophy fish types) |
| `/vault` | — | Manage your personal vault balance and tier |
| `/wardrobe` | `/wd` | View and manage your saved armor outfits |
| `/warp` | `/warps` | Teleport to a named warp point |

---

## Development

```bash
# Compile only (no tests)
mvn compile

# Run tests (skyblock-core module only — the authoritative check)
mvn test -pl skyblock-core -am

# Build a single module
mvn package -pl skyblock-core -am -DskipTests
```

The project targets **Java 25** (`maven.compiler.release=25`). All modules inherit from the parent POM; dependency versions are managed there. The Paper API is declared `<scope>provided</scope>` and must be supplied by the server at runtime.

---

## Documentation

All project documentation lives under [`docs/`](docs/README.md):

| Document | Purpose |
|----------|---------|
| [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) | Module map, package conventions, one-canonical-home rule |
| [`docs/FEATURES.md`](docs/FEATURES.md) | Per-manager feature detail |
| [`docs/ROADMAP.md`](docs/ROADMAP.md) | Planned features and milestones |
| [`docs/STATUS.md`](docs/STATUS.md) | Current implementation status |
| [`docs/CLEANUP.md`](docs/CLEANUP.md) | Technical debt and cleanup tracking |

---

## Contributing

Read [`CONTRIBUTING.md`](CONTRIBUTING.md) before submitting changes. Key points:

- One canonical home per concept — never duplicate a manager or command class.
- `skyblock-core` is the authoritative module; all gameplay logic lives there.
- Run `mvn test -pl skyblock-core -am` before opening a PR.

---

## License

This project is licensed under the [MIT License](LICENSE).
