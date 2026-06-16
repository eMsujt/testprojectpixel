# SkyBlock

A Hypixel-SkyBlock-style Minecraft plugin built on Paper. Implemented as a Maven multi-module project targeting Paper 1.21+.

## Documentation

All project documentation lives under [`docs/`](docs/):

| Document | What it covers |
|----------|----------------|
| [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) | How the repo is laid out — the canonical `com.skyblock.core.*` package, the **module map**, the manager registry, and where to put a change. **Read this first.** |
| [`docs/ROADMAP.md`](docs/ROADMAP.md) | Planned features and milestones. |
| [`docs/STATUS.md`](docs/STATUS.md) | Implementation status and the duplicate inventory. |
| [`docs/CLEANUP.md`](docs/CLEANUP.md) | Consolidation history. |
| [`CONTRIBUTING.md`](CONTRIBUTING.md) | How to build, test, and submit changes. |

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

The parent POM (`pom.xml`) aggregates all modules; `skyblock-core` is the deployable
plugin JAR and the canonical home for everything under `com.skyblock.core.*`. The full
**module map**, the canonical manager registry, and guidance on where to put a change
live in [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

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
