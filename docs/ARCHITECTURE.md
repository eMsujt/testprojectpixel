# Architecture

How this repository is laid out and where each feature lives. Start here when you
need to find the canonical implementation of a system before changing or extending it.

> **One rule above all:** every gameplay system has exactly one canonical
> implementation, and it lives in the `skyblock-core` module under
> `com.skyblock.core.*`. The many same-named top-level directories (`auction` /
> `auctions` / `auctionhouse`, `skills`, `pets`, …) are legacy feature modules being
> consolidated into `skyblock-core`. Do **not** add new state or logic to them.
> See [`CLEANUP.md`](CLEANUP.md) for the consolidation tracker and
> [`STATUS.md`](STATUS.md) for the full duplicate inventory.

## Build layout

This is a Maven multi-module project. The parent [`pom.xml`](../pom.xml) aggregates
every module; the deployable plugin JAR is produced by `skyblock-core`.

| Module | Role |
|--------|------|
| `api` | Shared API interfaces (`skyblock-api`) — `IModule`, `SkyBlockAPI`, provider plumbing. No gameplay logic. |
| `skyblock-core` | **The plugin.** Wires every manager, command, listener, and menu; produces the shaded JAR loaded by Paper via `SkyBlockPlugin#onEnable`. |
| *(feature modules)* | Legacy per-feature modules listed in the parent `<modules>` block. Retained while their classes are migrated into `skyblock-core`; new code should not depend on them. |

## Canonical core managers

All gameplay state is owned by a canonical set of singleton managers under
[`com.skyblock.core.manager`](../skyblock-core/src/main/java/com/skyblock/core/manager).
Each is a process-wide singleton — obtain it via its static `getInstance()` accessor.
These are the single source of truth for their domain; commands, listeners, and menus
delegate to them rather than holding their own state. (Managers in this package are not
thread-safe; synchronize externally if accessed off the main thread.)

| Manager | Responsibility |
|---------|----------------|
| `AccessoryManager` | Accessory rarities, magical power, and tuning points |
| `BankManager` | Per-player bank balance and interest tiers |
| `BazaarManager` | Bazaar order book — instant buy/sell and buy/sell orders |
| `CollectionManager` | Per-item collection tiers and unlock progress |
| `DungeonManager` | Dungeon runs and class progression |
| `EconomyManager` | Coin balances (purse and bank) with deposit/withdraw |
| `EnchantmentManager` | Per-player enchantment levels and the enchant table |
| `EventManager` | Server-wide bonus events |
| `FishingManager` | Fishing progression, sea-creature spawn pools, and loot rolls |
| `GardenManager` | Garden plots, crop milestones, and farming contests |
| `IslandManager` | Per-player island creation and management |
| `MiningManager` | Mining progression and speed bonuses |
| `MinionManager` | Placed minion management and resource production |
| `MuseumManager` | Museum donation flow and per-category completion |
| `PartyManager` | Party invites, membership, and leader transfer |
| `PetManager` | Pet collections, active pets, and XP curves |
| `QuestManager` | Quest definitions and objective tracking |
| `ReforgeManager` | Item reforges and reforge stones |
| `ShopManager` | NPC shops with buy/sell pricing |
| `SkillManager` | Per-player skill XP and levels |
| `SlayerManager` | Slayer quests, boss spawning, and tier escalation |

Two related managers follow the same singleton contract but live in their own feature
sub-packages:

| Manager | Package | Responsibility |
|---------|---------|----------------|
| `AuctionHouseManager` | `com.skyblock.core.auction.manager` | BIN listings and ascending-auction bidding |
| `CraftingManager` | `com.skyblock.core.crafting.manager` | Custom SkyBlock recipes and crafting history |

> Note: `com.skyblock.core.manager.CraftingManager` is a **deprecated stub** kept only
> for backward compatibility — use `com.skyblock.core.crafting.manager.CraftingManager`.

## Where each feature lives

Beyond the managers above, the rest of each system's code (models, menus, commands,
listeners) is organized into feature sub-packages under `com.skyblock.core.*`. To find
a feature, look for its package by name:

| Feature area | Canonical package(s) under `com.skyblock.core` |
|--------------|------------------------------------------------|
| Economy & banking | `economy`, `bank`, `bazaar`, `auction` |
| Profiles & player data | `profile`, `player`, `persistence`, `storage` |
| Items & crafting | `item`, `items`, `crafting`, `forge`, `itemforge`, `reforge`, `repair` |
| Enchanting | `enchant`, `enchanting`, `gemstone` |
| Skills & gathering | `skills`, `mining`, `foraging`, `farming`, `fishing`, `garden`, `jacob` |
| Combat & progression | `combat`, `slayer`, `dungeon`, `kuudra`, `bestiary`, `stat`, `stats`, `magicfind` |
| Collections & accessories | `collections`, `accessory`, `talisman`, `sack` |
| Minions & islands | `minion`; islands are owned by `IslandManager` (no dedicated feature package), with teleport plumbing in `warp` / `warps` |
| Pets & cosmetics | `pet`, `wardrobe`, `armor`, `armorset` |
| Social | `party`, `guild`, `friend`, `coop`, `trade`, `chat`, `mail`, `mailbox` |
| World & seasons | `calendar`, `season`, `mayor`, `contest`, `event`, `crimson`, `crimsonisle`, `crystalhollows`, `rift` |
| Progression trees | `hotm`, `level`, `fairysoul`, `fairy`, `achievement`, `quest` |
| Heart of the Mountain & mining world | `hotm`, `crystalhollows`, `essence` |
| UI & feedback | `menu`, `hud`, `scoreboard`, `bossbar`, `title`, `notification` |
| Infrastructure | `command`, `listener`, `manager`, `config`, `storage`, `persistence`, `cooldown`, `util`, `model` |
| Misc systems | `bestiary`, `museum`, `alchemy`, `brewing`, `booster`, `vault`, `reward`, `chocolate`, `trophy`, `trophyfish`, `leaderboard` |

> The legacy top-level directories (e.g. `auction/`, `auctions/`, `skills/`, `pets/`)
> mirror these names but are the **pre-consolidation** copies. When in doubt, the
> version under `skyblock-core/src/main/java/com/skyblock/core/` is canonical.

## Related docs

- [`ROADMAP.md`](ROADMAP.md) — prioritized feature roadmap and phase plan.
- [`STATUS.md`](STATUS.md) — full inventory of duplicate classes across modules.
- [`CLEANUP.md`](CLEANUP.md) — consolidation tracker (what's been merged, what's pending).
- [`../README.md`](../README.md) — build, install, and command reference.
