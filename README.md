# SkyBlock

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

- **Private islands** — create, upgrade, and visit personal islands.
- **Skills & collections** — level Combat, Mining, Farming, Foraging, Fishing, and more, unlocking collection tiers as you gather resources.
- **Economy** — coins/purse, personal and co-op banking, NPC shops, the Bazaar order book, and the Auction House.
- **Combat & dungeons** — custom mobs and stats, item/set abilities with mana, Slayer quests, and Catacombs floors F1–F7 plus Master Mode.
- **Gear progression** — custom enchantments, reforges, the Forge, pets, talismans/accessories with magical power, and the wardrobe.
- **End-game zones** — Crimson Isle (Kuudra & faction reputation), Crystal Hollows/Dwarven Mines with Heart of the Mountain, and The Rift.
- **Minions & automation** — placeable minions with fuel, upgrades, and hopper auto-sell.

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
| `AttributeManager` | Crimson Isle attribute shards and per-attribute levelling |
| `BankManager` / `BankingManager` | Personal/co-op bank balance, interest, deposits/withdrawals |
| `BazaarManager` | Buy/sell order book, instant buy/sell pricing, order fulfillment |
| `BestiaryManager` | Mob-kill tracking and bestiary tiers |
| `BingoManager` | Bingo card generation, goal tracking, and completion rewards |
| `CalendarManager` | SkyBlock calendar, seasons, and scheduled events |
| `CarnivalManager` | Carnival event games and rewards |
| `CollectionManager` | Resource collections and tier-unlock thresholds |
| `CommissionManager` | Dwarven Mines commissions — assignment, tracking, and rewards |
| `CrimsonIsleManager` | Kuudra tiers and Mage/Barbarian faction reputation |
| `CrystalHollowsManager` | Dwarven Mines / Crystal Hollows zones, gemstones, powder |
| `DragonManager` | Ender Dragon fights and contribution rewards |
| `DungeonClassManager` | Dungeon class selection and class-specific perks |
| `DungeonManager` | Catacombs floors F1–F7 + Master Mode, dungeon classes |
| `EconomyManager` | Coins / purse balance |
| `EnchantmentManager` / `EnchantingManager` | Custom enchantments and the enchant table |
| `EssenceManager` | Essence currencies and shop upgrades |
| `EventManager` | Server-wide event scheduling and lifecycle |
| `ExperimentationTableManager` | Superpairs / Chronomatron / Ultrasequencer minigames |
| `FairySoulManager` | Fairy soul discovery and exchange bonuses |
| `FishingManager` | Sea creatures, fishing XP, and treasure |
| `ForgeManager` | Forge item recipes and processing slots |
| `GardenManager` | Jacob's farming contests, plot unlocks, crop milestones |
| `HarpManager` | Melody's Harp song progress and Intelligence bonuses |
| `HotmManager` | Heart of the Mountain tree — perk nodes, tiers, powder |
| `IslandManager` | Private island creation, upgrades, and visiting |
| `ItemAbilityManager` | Weapon/armor item and full-set abilities with mana costs |
| `JacobManager` | Jacob's farming contest scheduling and participation rewards |
| `JerryWorkshopManager` | Jerry's Workshop gifts, snow minions, and snow cannon |
| `KuudraManager` | Kuudra boss tier management and contribution tracking |
| `MayorManager` | Mayor elections and active perks |
| `MiningManager` | Mining XP, speed, fortune, and ores |
| `MinionManager` | Minion types/tiers, fuel, upgrade slots, hopper auto-sell |
| `MobManager` | Custom mob registration, stats, and spawn management |
| `MuseumManager` | Museum donations and completion rewards |
| `NetworthManager` | Player net-worth valuation across items and currencies |
| `PartyManager` | Party invites, membership, and leadership |
| `PestManager` | Garden pest spawning, tracking, and extermination rewards |
| `PetManager` / `PetsManager` | Pet ownership, levelling, and active-pet perks |
| `PotionManager` | Brewing-stand recipes, potion levels/durations, splash potions |
| `QuestManager` | Quest tracking and completion |
| `ReforgeManager` / `RepairManager` | Reforge stones/stats and item repair |
| `ReputationManager` | Faction reputation tracking |
| `RiftManager` | The Rift dimension state and currency |
| `RuneManager` | Rune item types, application, and active-rune effects |
| `SackManager` | Sack storage and auto-pickup |
| `ShopManager` | NPC shop catalogues and transactions |
| `SkillManager` | Skill levels and XP |
| `SkyblockLevelManager` | SkyBlock level XP accumulation and tier rewards |
| `SlayerManager` | Slayer quest tiers, boss spawn cost, and rewards |
| `StatManager` | Effective player stat calculation and display |
| `StorageManager` | Personal storage and backpack pages |
| `TradeManager` | Peer-to-peer trading sessions |
| `TrophyFishManager` | Trophy fish catch tracking and drop-chance tables |
| `WardrobeManager` | Named armor outfits and full-set swapping |

---

## Commands

| Command | Alias | Description |
|---------|-------|-------------|
| `/accessories` | — | View and manage your accessory bag |
| `/accessorybag` | — | Open the Accessory Bag GUI |
| `/achievement` | — | View your SkyBlock achievements |
| `/alchemy` | — | Brew potions using the SkyBlock alchemy system |
| `/auctionhouse` | `/ah` | Manage Auction House listings (BIN and bid-based) |
| `/backpack` | — | Create and manage personal item backpacks |
| `/bank` | — | Personal and co-op bank deposit/withdraw/balance |
| `/bazaar` | — | Open the Bazaar order book |
| `/bestiary` | — | Track your mob kill counts and bestiary tiers |
| `/booster` | — | Activate XP or coin boosters |
| `/chat` | — | Switch your active chat channel |
| `/collection` | `/col`, `/collections` | View your SkyBlock collection progress |
| `/combat` | — | View and manage your combat stats |
| `/coop` | — | Manage your co-op island group |
| `/crafting` | — | Browse SkyBlock crafting recipes and track history |
| `/crimson` | — | View and manage Crimson Isle faction and reputation |
| `/crystalhollows` | — | View and manage Crystal Hollows zones and crystal progress |
| `/dungeon` | — | Enter and manage Catacombs dungeon runs |
| `/dungeonclass` | — | View and select your dungeon class |
| `/enchanting` | — | View and apply SkyBlock enchantments |
| `/essence` | — | View and manage your SkyBlock essence balances |
| `/event` | — | View and join active SkyBlock events |
| `/fairysoul` | `/fairysouls` | View and manage your fairy soul collection |
| `/fishing` | — | View fishing stats and trophy fish catches |
| `/foraging` | — | View foraging level, XP, and log chop counts |
| `/forge` | — | Forge items using the SkyBlock forge |
| `/friend` | — | Manage your SkyBlock friend list |
| `/garden` | — | View and manage your Garden plot, visitors, and crop upgrades |
| `/guild` | — | Create and manage player guilds |
| `/harp` | — | View Melody's Harp song progress and Intelligence bonus |
| `/hotm` | `/hotmtree` | View and manage Heart of the Mountain perk tree |
| `/jerryworkshop` | — | View Jerry's Workshop gifts, snow minions, and snow cannon |
| `/kuudra` | — | Kuudra boss tiers and contribution tracking |
| `/leaderboard` | `/lb` | View player leaderboards |
| `/mail` | `/mailbox` | Send, read, and manage player mail messages |
| `/mayor` | — | View the active mayor and vote for candidates |
| `/menu` | — | Open the SkyBlock main menu |
| `/minion` | `/minions` | Manage your placed minions, fuel, and upgrades |
| `/museum` | — | Donate items and view your museum collection progress |
| `/party` | — | Manage your party invites and membership |
| `/pet` | `/pets` | Manage your pets and active-pet perks |
| `/profile` | — | View your SkyBlock profile |
| `/quest` | — | Manage your SkyBlock quests |
| `/reforge` | — | View and apply item reforges |
| `/rift` | — | View and manage your Rift dimension progress |
| `/run` | — | Track your dungeon run statistics |
| `/sack` | — | View the contents of your sacks |
| `/skyblock-level` | `/sblevel` | View and manage your SkyBlock level and XP |
| `/slay` | `/slayer` | Manage Slayer quests and view boss info |
| `/skills` | — | View your SkyBlock skill levels and XP |
| `/stat` | — | View your effective SkyBlock player stats |
| `/storage` | — | View and manage your personal storage pages |
| `/stats` | — | View and manage your SkyBlock player stats menu |
| `/title` | — | Manage your SkyBlock title |
| `/trade` | — | Trade items with another player |
| `/trophyfish` | — | View trophy fish catch counts and drop chances |
| `/trophyfishing` | — | Open the Trophy Fishing GUI (all 22 trophy fish types) |
| `/vault` | — | Manage your personal vault balance and tier |
| `/wardrobe` | — | View and manage your saved armor outfits |
| `/warp` | — | Teleport to a named warp point |

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
