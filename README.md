# SkyBlock

> **A faithful Hypixel SkyBlock recreation for Paper 1.21+ servers — skills, dungeons, economy, and beyond.**

[![Build](https://github.com/eMsujt/testprojectpixel/actions/workflows/build.yml/badge.svg)](https://github.com/eMsujt/testprojectpixel/actions/workflows/build.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://adoptium.net/)
[![Paper](https://img.shields.io/badge/Paper-1.21%2B-brightgreen.svg)](https://papermc.io/)

A Hypixel-SkyBlock-style Minecraft plugin built on Paper. Implemented as a Maven multi-module project targeting Paper 1.21+.

## Feature Overview

SkyBlock recreates the core Hypixel SkyBlock progression loop as a server-side Paper plugin:

- **Private islands** — create, upgrade, and visit personal islands.
- **Skills & collections** — level Combat, Mining, Farming, Foraging, Fishing, and more, unlocking collection tiers as you gather resources.
- **Economy** — coins/purse, personal and co-op banking, NPC shops, the Bazaar order book, and the Auction House.
- **Combat & dungeons** — custom mobs and stats, item/set abilities with mana, Slayer quests, and Catacombs floors F1–F7 plus Master Mode.
- **Gear progression** — custom enchantments, reforges, the Forge, pets, talismans/accessories with magical power, and the wardrobe.
- **End-game zones** — Crimson Isle (Kuudra & faction reputation), Crystal Hollows/Dwarven Mines with Heart of the Mountain, and The Rift.
- **Minions & automation** — placeable minions with fuel, upgrades, and hopper auto-sell.

See the [Feature Managers](#feature-managers) table below for the authoritative manager registry, and [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) for the module map.

## Documentation

All project documentation lives under [`docs/`](docs/README.md) — see the
[documentation index](docs/README.md) for the full list. Start with
[`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) and read
[`CONTRIBUTING.md`](CONTRIBUTING.md) before submitting changes.

## Prerequisites

| Requirement | Version |
|-------------|---------|
| Java | 25+ |
| Paper server | 1.21+ |
| Maven | 3.8+ |

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

## Installation

1. Build the project (see above).
2. Copy `skyblock-core/target/skyblock-core-1.0.0-SNAPSHOT.jar` into your Paper server's `plugins/` directory.
3. Start or restart the server.
4. The plugin registers automatically via `SkyblockPlugin#onEnable`.

## Module Structure

The parent POM (`pom.xml`) aggregates all modules; `skyblock-core` is the deployable
plugin JAR and the canonical home for everything under `com.skyblock.core.*`. The full
**module map**, the canonical manager registry, and guidance on where to put a change
live in [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

## Feature Managers

Most gameplay state is owned by a single authoritative manager under
**`com.skyblock.core.manager`** (a few large systems keep their own feature
sub-package — e.g. `com.skyblock.core.auction.manager.AuctionHouseManager`,
`com.skyblock.core.crafting.manager.CraftingManager`,
`com.skyblock.core.profile.manager.ProfileManager`). Import these directly — never a
copy. See [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) for the package-layout
conventions and the one-canonical-home rule, and
[`docs/FEATURES.md`](docs/FEATURES.md) for the full per-manager feature list.

| Manager | Responsibility |
|---------|----------------|
| `AccessoryManager` / `AccessoryBagManager` | Talismans/accessories and the magical-power bag |
| `AttributeManager` | Crimson Isle attribute shards and per-attribute levelling |
| `BankManager` | Personal/co-op bank balance, interest, deposits/withdrawals |
| `BazaarManager` | Buy/sell order book, instant buy/sell pricing, order fulfillment |
| `BestiaryManager` | Mob-kill tracking and bestiary tiers |
| `CalendarManager` | SkyBlock calendar, seasons, and scheduled events |
| `CarnivalManager` | Carnival event games and rewards |
| `CollectionManager` | Resource collections and tier-unlock thresholds |
| `CrimsonIsleManager` | Kuudra tiers and Mage/Barbarian faction reputation |
| `CrystalHollowsManager` | Dwarven Mines / Crystal Hollows zones, gemstones, powder |
| `DragonManager` | Ender Dragon fights and contribution rewards |
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
| `HotmManager` | Heart of the Mountain tree — perk nodes, tiers, powder |
| `IslandManager` | Private island creation, upgrades, and visiting |
| `ItemAbilityManager` | Weapon/armor item and full-set abilities with mana costs |
| `MayorManager` | Mayor elections and active perks |
| `MiningManager` | Mining XP, speed, fortune, and ores |
| `MinionManager` | Minion types/tiers, fuel, upgrade slots, hopper auto-sell |
| `MuseumManager` | Museum donations and completion rewards |
| `NetworthManager` | Player net-worth valuation across items and currencies |
| `PartyManager` | Party invites, membership, and leadership |
| `PetManager` | Pet ownership, levelling, and active-pet perks |
| `PotionManager` | Brewing-stand recipes, potion levels/durations, splash potions |
| `QuestManager` | Quest tracking and completion |
| `ReforgeManager` / `RepairManager` | Reforge stones/stats and item repair |
| `ReputationManager` | Faction reputation tracking |
| `RiftManager` | The Rift dimension state and currency |
| `SackManager` | Sack storage and auto-pickup |
| `ShopManager` | NPC shop catalogues and transactions |
| `SkillManager` | Skill levels and XP |
| `SlayerManager` | Slayer quest tiers, boss spawn cost, and rewards |
| `StorageManager` | Personal storage and backpack pages |
| `TradeManager` | Peer-to-peer trading sessions |
| `WardrobeManager` | Named armor outfits and full-set swapping |

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
| `/pet` | — | Manage your pets and active-pet perks |
| `/profile` | — | View your SkyBlock profile |
| `/quest` | — | Manage your SkyBlock quests |
| `/reforge` | — | View and apply item reforges |
| `/run` | — | Track your dungeon run statistics |
| `/skyblock-level` | `/sblevel` | View and manage your SkyBlock level and XP |
| `/slay` | `/slayer` | Manage Slayer quests and view boss info |
| `/skills` | — | View your SkyBlock skill levels and XP |
| `/stat` | — | View your effective SkyBlock player stats |
| `/stats` | — | View and manage your SkyBlock player stats menu |
| `/title` | — | Manage your SkyBlock title |
| `/trade` | — | Trade items with another player |
| `/trophyfish` | — | View trophy fish catch counts and drop chances |
| `/trophyfishing` | — | Open the Trophy Fishing GUI (all 22 trophy fish types) |
| `/vault` | — | Manage your personal vault balance and tier |
| `/wardrobe` | — | View and manage your saved armor outfits |
| `/warp` | — | Teleport to a named warp point |

## Development

```bash
# Compile only (no tests)
mvn compile

# Run tests
mvn test

# Build a single module (e.g. skyblock-core)
mvn package -pl skyblock-core -am -DskipTests
```

The project targets **Java 25** (`maven.compiler.release=25`). All modules inherit from the parent POM; dependency versions are managed there. The Paper API is declared `<scope>provided</scope>` and must be supplied by the server at runtime.
