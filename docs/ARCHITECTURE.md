# Architecture

> Map of how this repository is laid out: the canonical core-manager package,
> the Maven module map, and where each feature actually lives. Read this first
> to find the right file before adding code. For the full duplicate inventory
> see [`STATUS.md`](./STATUS.md); for consolidation history see
> [`CLEANUP.md`](./CLEANUP.md); for the feature plan see [`ROADMAP.md`](./ROADMAP.md).

## The one rule

**`skyblock-core` is the deployable plugin and the canonical home for everything.**
`mvn clean package` produces `skyblock-core/target/skyblock-core-1.0.0-SNAPSHOT.jar`,
which is the JAR you drop into a Paper server (see [`../README.md`](../README.md)).
All canonical code lives under the `com.skyblock.core.*` package inside that module.

The repo grew several parallel implementations of nearly every system — as a
standalone leaf module (`bank/`, `banking/`, `economy/`), as a package in `core/`,
and again in `skyblock-core/`. The `core/` and `plugin/` modules are earlier or
parallel iterations. **Do not add code to them, and do not create a new module or
package for a concept that already exists.** When a duplicate is found, the survivor
is the `com.skyblock.core.*` class; the others become `@Deprecated` delegating stubs
or are deleted.

## Canonical manager registry

Most gameplay state is owned by a single authoritative manager. The bulk of these
live in **`com.skyblock.core.manager`** (see that package's `package-info.java`);
a few keep their own feature sub-package. Import these directly — never a copy.

| Domain | Canonical class |
|--------|-----------------|
| Accessories | `com.skyblock.core.manager.AccessoryManager` |
| Bank | `com.skyblock.core.manager.BankManager` |
| Bazaar | `com.skyblock.core.manager.BazaarManager` |
| Collections | `com.skyblock.core.manager.CollectionManager` |
| Dungeons | `com.skyblock.core.manager.DungeonManager` |
| Economy (coins / purse) | `com.skyblock.core.manager.EconomyManager` |
| Enchantments | `com.skyblock.core.manager.EnchantmentManager` |
| Events | `com.skyblock.core.manager.EventManager` |
| Fishing | `com.skyblock.core.manager.FishingManager` |
| Garden | `com.skyblock.core.manager.GardenManager` |
| Mining | `com.skyblock.core.manager.MiningManager` |
| Minions | `com.skyblock.core.manager.MinionManager` |
| Museum | `com.skyblock.core.manager.MuseumManager` |
| Party | `com.skyblock.core.manager.PartyManager` |
| Pets | `com.skyblock.core.manager.PetManager` |
| Quests | `com.skyblock.core.manager.QuestManager` |
| Reforges | `com.skyblock.core.manager.ReforgeManager` |
| NPC shops | `com.skyblock.core.manager.ShopManager` |
| Skills | `com.skyblock.core.manager.SkillManager` |
| Slayers | `com.skyblock.core.manager.SlayerManager` |
| Auction House | `com.skyblock.core.auction.manager.AuctionHouseManager` |
| Crafting | `com.skyblock.core.crafting.manager.CraftingManager` |
| Islands | `com.skyblock.core.island.manager.IslandManager` |

Shared value types are also single-sourced: `com.skyblock.core.model.Rarity`,
`com.skyblock.core.model.Stat`, `com.skyblock.core.model.Skill`,
`com.skyblock.core.model.Collection`, and tuning constants in
`com.skyblock.core.config.Constants`. UI base class is `com.skyblock.core.menu.Menu`;
shared item helpers are `com.skyblock.core.util.ItemBuilder` / `SkullItemUtil`.

> Managers in `com.skyblock.core.manager` are singletons and are **not**
> thread-safe; synchronize externally if accessed off the main server thread.

## Package layout inside `skyblock-core`

Each feature owns a sub-package under `com.skyblock.core.<feature>`, conventionally
split into `manager` (state + logic), `command` (player commands), `gui` (menus),
`listener` (Bukkit events), and `model` (data types). Examples:
`com.skyblock.core.bank.{command,manager,model}`,
`com.skyblock.core.dungeon.{command,gui,manager,model}`,
`com.skyblock.core.collections.{command,gui,listener,manager}`.
`com.skyblock.core.SkyBlockPlugin` is the `onEnable` entry point that wires the
managers and registers commands.

## Module map

The parent `pom.xml` aggregates the modules below. The leaf modules are mostly
pre-consolidation variants kept building until their last unique code is migrated
into `skyblock-core`; new work should target `skyblock-core`.

| Module | Role |
|--------|------|
| `api` | Shared API interfaces (`skyblock-api`) |
| `skyblock-core` | **Deployable plugin JAR** — canonical managers, commands, listeners |
| `economy` / `banking` / `bank` | Coin economy and banking (variants) |
| `island` / `islands` | Void-world island generation (variants) |
| `auction` / `auctionhouse` / `auctions` / `auction_house` | Auction House (variants) |
| `bazaar` | Buy/sell order bazaar |
| `slayer` / `slayers` | Slayer quests (variants) |
| `dungeon` / `dungeons` | Dungeon runs (variants) |
| `skills` | Skill XP and leveling |
| `collection` / `collections` | Item collection tracking (variants) |
| `minion` / `minions` | Placed minions (variants) |
| `pets` | Pet system |
| `crafting` / `forging` / `item_forge` / `reforging` | Crafting, forge, reforges |
| `enchanting` / `enchants` / `enchantments` | Custom enchantments (variants) |
| `fishing` / `foraging` / `farming` / `mining` / `garden` | Gathering skills |
| `combat` / `alchemy` / `brewing` / `hotm` | Combat, potions, Heart of the Mountain |
| `party` / `guild` / `guilds` / `trade` / `trades` / `trading` | Social and trading |
| `museum` / `bestiary` / `achievements` / `quests` / `calendar` | Progression and tracking |
| `accessories` / `talismans` / `backpacks` / `wardrobe` / `fairysouls` | Items and storage |
| `npc` / `npcs` / `shop` / `housing` / `leaderboards` / `scoreboard` | World and UI |
| `profile` / `profiles` / `playerdata` / `stats` | Player data |
| `core` / `plugin` | Legacy/parallel app modules — **do not extend** |

> Module names are intentionally inconsistent (`auction` vs `auctions` vs
> `auctionhouse` vs `auction_house`; `npc` vs `npcs`) because they are duplicate
> lineages. The canonical class is always the `com.skyblock.core.*` one regardless
> of which module a copy sits in.

## Where to put a change

1. **Adding to an existing feature?** Find its `com.skyblock.core.<feature>`
   sub-package in `skyblock-core` and edit there.
2. **New gameplay state?** Add a manager in `com.skyblock.core.manager` (or the
   feature sub-package if it owns commands/GUIs/listeners) and wire it in
   `SkyBlockPlugin`.
3. **Found a duplicate?** Consolidate into the `com.skyblock.core.*` survivor and
   record it in [`CLEANUP.md`](./CLEANUP.md) — never add a parallel copy.
