# SkyBlock

A Hypixel-SkyBlock-style Minecraft plugin built on Paper. Implemented as a Maven multi-module project targeting Paper 1.21+.

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
4. The plugin registers automatically via `SkyBlockPlugin#onEnable`.

## Module Structure

The parent POM (`pom.xml`) aggregates all modules. Key modules:

| Module | Purpose |
|--------|---------|
| `api` | Shared API interfaces (`skyblock-api`) |
| `skyblock-core` | Main plugin JAR — wires all managers and commands |
| `economy` / `banking` / `bank` | Coin economy and banking |
| `island` / `islands` | Void-world island generation and management |
| `auction` / `auctionhouse` / `auctions` | Auction House (BIN + bid) |
| `bazaar` | Buy/sell order bazaar |
| `slayer` / `slayers` | Slayer quest system |
| `dungeons` / `dungeon` | Dungeon runs |
| `skills` | Skill XP and leveling |
| `collections` / `collection` | Item collection tracking |
| `minions` / `minion` | Placed minion management |
| `pets` | Pet system |
| `crafting` | Custom SkyBlock recipes |
| `enchanting` / `enchants` / `enchantments` | Custom enchantment system |
| `fishing` / `foraging` / `farming` / `mining` | Gathering skills |
| `party` | Party invites and membership |
| `trade` / `trades` / `trading` | Peer-to-peer item trading |
| `wardrobe` | Named armor outfit storage |
| `bestiary` | Per-player mob kill tracking |
| `quests` | Quest lifecycle |
| `achievements` | Achievement tracking |
| `leaderboards` | Player leaderboards |
| `npc` / `npcs` | NPC shops |
| `shop` | General item shop |
| `storage` | Personal paged storage |
| `museum` | Item museum |
| `hotm` | Heart of the Mountain progression |
| `garden` | Garden plots |
| `guild` / `guilds` | Guild management |
| `alchemy` / `brewing` | Potion brewing |
| `reforging` / `forging` / `item_forge` | Item reforging and forge |
| `talismans` / `accessories` | Talisman/accessory bag |
| `fairysouls` | Fairy soul tracking |
| `calendar` | In-game seasonal calendar |
| `housing` | Player housing |
| `scoreboard` | Sidebar scoreboard |
| `stats` | Player stat aggregation |
| `combat` | Combat system hooks |
| `profile` / `profiles` / `playerdata` | Player profile persistence |
| `backpacks` | Portable backpacks |

## Commands

| Command | Alias | Description |
|---------|-------|-------------|
| `/skyblock` | `/sb` | Open the SkyBlock main menu |
| `/island` | — | Create, visit, or manage your island |
| `/islandupgrade` | `/iu` | Purchase island upgrades |
| `/skills` / `/skill` | — | View skill levels and XP |
| `/auction` | — | Auction House (create/view/cancel) |
| `/auctionhouse` | `/ah` | Full Auction House management |
| `/bazaar` | — | Open the Bazaar |
| `/profile` | — | View a player profile |
| `/slay` | — | Slayer quest management |
| `/pets` / `/pet` | — | Pet management |
| `/f` | — | Friend management |
| `/warp` | — | Teleport to warp points |
| `/shop` | — | Browse the item shop |
| `/leaderboard` | `/lb` | View player leaderboards |
| `/bank` / `/banking` | — | Bank deposit/withdraw/balance |
| `/dungeon` | — | Dungeon runs |
| `/forge` | — | Item forging |
| `/collection` | `/col` | Collection progress |
| `/npc` | — | NPC shop interactions |
| `/minion` | — | Minion management |
| `/alchemy` | — | Potion brewing |
| `/enchanting` / `/enchantment` / `/enchant` | — | Enchantment management |
| `/talisman` | — | Talisman/accessory management |
| `/crafting` | — | Custom recipes and history |
| `/quest` | — | Quest management |
| `/achievement` | — | Achievement viewer |
| `/dailyreward` | `/daily` | Claim daily coin reward |
| `/storage` | — | Personal storage pages |
| `/trade` | — | Peer-to-peer trading |
| `/wardrobe` | — | Named armor outfits |
| `/bestiary` | — | Mob kill tracking |
| `/party` | — | Party invites and management |
| `/accessorybag` | `/abag` | Accessory bag management |

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
