# Project Status

> Audit of every module and class in the SkyBlock repository.
> Generated 2026-06-14. Counts: **67 declared Maven modules** (`pom.xml`), **577 `.java` source files**.

## How to read this document

The project is a Maven multi-module Paper plugin (see [`README.md`](../README.md)). Two
things stand out from a full pass over the tree and should frame everything below:

1. **The same domain is implemented many times over.** Most gameplay systems exist as
   several parallel implementations: as a standalone leaf module (e.g. `bank/`,
   `banking/`, `economy/`), as a package inside the `core/` module, and again as a
   package inside the `skyblock-core/` module. The names are inconsistent (`auction` vs
   `auctions` vs `auctionhouse` vs `auction_house`; `Hotm` vs `HOTM`; `Npc` vs `NPC`).
2. **There are three overlapping "application" modules.** `core/`, `plugin/`, and
   `skyblock-core/` each contain a `SkyBlockPlugin`/`SkyblockPlugin` entry point and their
   own manager+command stacks. Per the README the **deployable JAR is `skyblock-core`**;
   `core/` and `plugin/` appear to be earlier or parallel iterations.

Status legend: ✅ canonical / actively wired · 🔁 duplicate or overlapping variant ·
🧩 model/enum/support type only.

---

## Application & API modules

| Module | Classes | Status |
|--------|---------|--------|
| `api` | `IModule`, `SkyBlockAPI`, `SkyBlockAPIProvider` | ✅ Shared API surface (`skyblock-api`). |
| `skyblock-core` | Main plugin — see [breakdown](#skyblock-core-breakdown) below | ✅ Deployable JAR; wires all managers/commands. |
| `core` | `SkyBlockPlugin` + 38 classes across `auction`, `auctionhouse`, `bank`, `bazaar`, `collections`, `dungeon`, `garden`, `guild`, `hotm`, `island`, `items`, `kuudra`, `listeners`, `mayor`, `menu`, `pets`, `profile`, `skills`, `title`, `warp` | 🔁 Overlaps `skyblock-core`; earlier iteration. |
| `plugin` | `SkyBlockPlugin` + commands/listeners/managers — see [breakdown](#plugin-breakdown) below | 🔁 Third app stack; overlaps both above. |
| `playerdata` | `PlayerProfile` | 🧩 Standalone profile model. |

## Economy & banking

| Module | Classes | Status |
|--------|---------|--------|
| `economy` | `CoinManager`, `BankManager`, `AuctionHouseManager`, `BazaarManager`, `CurrencyType` | ✅ Most complete economy module. |
| `bank` | `BankManager` | 🔁 Bank-only variant. |
| `banking` | `BankManager`, `BankingManager`, `BankTier` | 🔁 Declared module; overlaps `bank`/`economy`. |
| `bazaar` | `BazaarManager`, `BazaarOrder`, `ProductCategory` | ✅ Standalone bazaar. |

## Auction House

| Module | Classes | Status |
|--------|---------|--------|
| `auction` | `AuctionManager` | 🔁 |
| `auctions` | `AuctionManager`, `AuctionType` | 🔁 |
| `auctionhouse` | `AuctionHouseManager` | 🔁 |
| `auction_house` | `AuctionType` | 🔁 Enum-only fragment. |

> Four overlapping auction modules. `core` and `skyblock-core` each add two more
> auction packages (`auction`, `auctionhouse`). Strong consolidation candidate.

## Islands

| Module | Classes | Status |
|--------|---------|--------|
| `islands` | `Island`, `IslandManager`, `IslandUpgradeManager` | ✅ Most complete. |
| `island` | `IslandManager`, `IslandUpgrade` | 🔁 |

## Skills & gathering

| Module | Classes | Status |
|--------|---------|--------|
| `skills` | `SkillManager`, `SkillsManager`, `SkillType`, `SkillXPTable` | ✅ |
| `farming` | `FarmingManager`, `CropType` | ✅ |
| `fishing` | `FishingManager`, `FishingListener`, `FishingDrop`, `FishTreasure`, `FishingZone`, `TrophyFishType` | ✅ Richest gathering module. |
| `foraging` | `ForagingManager`, `ForagingLocation` | ✅ |
| `mining` | `MiningManager`, `MiningLocation`, `HotmPerk` | ✅ |
| `garden` | `CropType` | 🧩 Enum-only fragment. |

## Combat, slayers & dungeons

| Module | Classes | Status |
|--------|---------|--------|
| `combat` | `CombatEngine`, `CombatManager`, `CombatStat`, `CombatStats`, `DamageCalculator`, `DamageType` | ✅ |
| `slayer` | `SlayerManager`, `SlayerBossManager`, `SlayerType` | ✅ |
| `slayers` | `SlayerManager`, `SlayerQuest`, `SlayerType` | 🔁 |
| `skyblock-slayer` | `SlayerManager` | 🔁 |
| `dungeons` | `DungeonManager`, `DungeonClass`, `DungeonFloor`, `DungeonRoom`, `DungeonScoreManager` | ✅ Most complete dungeon module. |
| `dungeon` | `DungeonManager` | 🔁 |
| `skyblock-dungeons` | `DungeonManager` | 🔁 |

## Items, enchanting, forging & reforging

| Module | Classes | Status |
|--------|---------|--------|
| `items` | `ItemManager`, `SkyBlockItem`, `ItemRarity`, `Rarity` | ✅ |
| `enchanting` | `EnchantingManager`, `EnchantmentRegistry`, `SkyBlockEnchantment`, `EnchantType` | ✅ |
| `enchantments` | `EnchantmentManager`, `SkyBlockEnchantment` | 🔁 |
| `enchants` | `EnchantManager` | 🔁 |
| `forging` | `ForgingManager`, `ForgeSlot` | ✅ |
| `item_forge` | `ItemForgeManager` | 🔁 |
| `reforging` | `ReforgingManager` | ✅ |
| `crafting` | `CraftingManager`, `SkyBlockCraftingManager`, `CraftingRecipe`, `RecipeType` | ✅ |
| `talismans` | `Talisman` | 🧩 Model-only. |
| `accessories` | `AccessoryManager`, `AccessoryRarity` | ✅ |

## Pets & minions

| Module | Classes | Status |
|--------|---------|--------|
| `pets` | `PetManager`, `Pet`, `PetAbility`, `PetTier`, `PetType` | ✅ |
| `minions` | `MinionManager`, `MinionInstance`, `MinionTier`, `MinionType` | ✅ |
| `minion` | `MinionManager` | 🔁 |
| `skyblock-minions` | `MinionManager` | 🔁 |

## Profiles

| Module | Classes | Status |
|--------|---------|--------|
| `profiles` | `ProfileManager`, `SkyBlockProfile`, `GameMode`, `ProfileMode` | ✅ |
| `profile` | `ProfileManager` | 🔁 |
| `playerdata` | `PlayerProfile` | 🔁 (see app modules). |

## Collections, quests & progression

| Module | Classes | Status |
|--------|---------|--------|
| `collections` | `CollectionManager`, `CollectionsManager`, `CollectionRegistry`, `CollectionCategory`, `CollectionTier` | ✅ |
| `collection` | `CollectionManager` | 🔁 |
| `quests` | `QuestManager`, `QuestRegistry`, `QuestType`, `QuestObjectiveType` | ✅ |
| `achievements` | `AchievementManager`, `AchievementType` | ✅ |
| `leaderboards` | `LeaderboardManager` | ✅ |
| `bestiary` | `BestiaryManager`, `BestiaryFamily` | ✅ |
| `hotm` | `HotmManager` | ✅ Heart of the Mountain. |
| `fairysouls` | `FairySoulManager` | ✅ |

## NPCs, shops & social

| Module | Classes | Status |
|--------|---------|--------|
| `npc` | `NpcManager`, `NpcType` | 🔁 |
| `npcs` | `NPCManager`, `NpcManager` | 🔁 Inconsistent casing within module. |
| `shop` | `ShopManager` | ✅ |
| `guilds` | `GuildManager`, `Guild` | ✅ |
| `guild` | `GuildRank` | 🧩 Enum-only fragment. |
| `party` | `PartyManager` | ✅ |
| `trades` | `TradeManager`, `TradeCommand` | ✅ |
| `trading` | `TradingManager` | 🔁 |

## Storage, cosmetics & world

| Module | Classes | Status |
|--------|---------|--------|
| `backpacks` | `BackpackManager` | ✅ |
| `wardrobe` | `WardrobeManager` | ✅ |
| `museum` | `MuseumManager` | ✅ |
| `housing` | `HousingManager` | ✅ |
| `calendar` | `CalendarManager` | ✅ |
| `scoreboard` | `ScoreboardManager`, `ScoreboardSection` | ✅ |
| `stats` | `StatsManager`, `PlayerStat` | ✅ |
| `events` | `EventManager`, `SkyBlockEvent` | ✅ |
| `alchemy` | `AlchemyManager`, `BrewingIngredient` | ✅ |
| `brewing` | `BrewingManager` | 🔁 Overlaps `alchemy`. |

---

## `skyblock-core` breakdown

The deployable module. Entry points: `SkyBlockPlugin`, `SkyblockPlugin` (duplicate
casing), plus `PlayerDataManager`. It re-implements nearly every domain as its own
package under `com.skyblock.core.*`. Notable patterns:

- **Manager+Command (+Listener) per feature** across ~90 packages: `ability`, `accessory`,
  `achievement`, `alchemy`, `armor`/`armorset`, `auction`/`auctionhouse`, `backpack`,
  `bank`/`banking`, `bazaar`, `bestiary`, `booster`, `bossbar`, `calendar`, `chat`,
  `chocolate`, `collection`/`collections`, `combat`, `coop`, `cooldown`, `crafting`,
  `crimson`/`crimsonisle`, `crystalhollows`, `dungeon`/`dungeons`, `economy`,
  `enchant`/`enchanting`/`enchantment`, `essence`, `event`, `fairy`/`fairysoul`/`magic`,
  `farming`, `fishing` (+`trophyfish`/`trophy`), `foraging`, `forge`/`itemforge`,
  `friend`, `garden`, `gemstone`, `guild`, `hotm`, `hub`, `island`, `item`/`items`,
  `kuudra`, `leaderboard`, `level`, `magicfind`, `mail`/`mailbox`, `mayor`, `menu`,
  `mining`, `minion`/`minions`, `mob`, `museum`, `notification`, `npc`, `party`,
  `pet`/`pets`, `profile`, `quest`/`quests`, `reforge`, `repair`, `reward`, `rift`,
  `run`, `sack`, `scoreboard`, `season`, `shop`, `skill`/`skills`, `slayer`, `stat`/`stats`,
  `storage`, `talisman`, `title`, `trade`, `vault`, `wardrobe`, `warp`/`warps`.
- **Internal duplication** even within this module: `enchant`/`enchanting`/`enchantment`,
  `collection`/`collections`, `skill`/`skills`, `stat`/`stats`, `pet`/`pets`,
  `fairy`/`fairysoul`/`magic`, `crimson`/`crimsonisle`, `trophy`/`trophyfish`,
  `command`/`commands`, `warp`/`warps` all coexist with parallel manager classes.
- **Support/infrastructure**: `gui/GuiBuilder`, `item/ItemBuilder`, `storage/YamlPlayerStorage`,
  `listener/CoreListeners`, `menu/MenuManager`+`MenuListener`.

This module is where ongoing per-feature work (stats getters, YAML persistence, history
tracking) is landing across its many manager variants.

## `plugin` breakdown

A self-contained Bukkit plugin stack under `com.skyblock.plugin.*`, overlapping
`skyblock-core`:

- **`SkyBlockPlugin`** entry point.
- **`managers/`** (25): `AuctionHouseManager`, `BankManager`, `BazaarManager`,
  `CollectionsManager`, `CooldownManager`, `CraftingManager`, `DungeonManager`,
  `EnchantingManager`, `EventManager`, `FishingManager`, `GardenManager`, `HOTMManager`,
  `IslandManager`, `KuudraManager`, `MayorManager`, `NetworkManager`, `PetsManager`,
  `ProfileManager`, `QuestManager`, `SkillsManager`, `SlayerManager`, `TimeManager`,
  `TradingManager`, `WarpManager`, `WeatherManager`.
- **`commands/`** (28) and **`command/`** (sub-packaged, 15) — two parallel command trees
  with overlapping commands (`HOTMCommand`/`HotmCommand`, `Trade`/`Trading`,
  `Profile`/`SkyblockQuest`, etc.).
- **`listeners/`**: `HubClickListener`, `QuestProgressListener`, `TimeListener`,
  `WeatherListener`. **`menu/`**: `SkyblockMenuCommand`.

---

## Summary of findings

- **Heavy duplication is the dominant architectural issue.** Most domains have 2–4
  parallel implementations spread across leaf modules, `core/`, `plugin/`, and
  `skyblock-core/`. This multiplies the surface for every feature change (e.g. a stats
  getter must be added to 4–6 manager variants).
- **Naming is inconsistent** — singular vs plural module names (`auction`/`auctions`,
  `collection`/`collections`, `minion`/`minions`) and casing (`Hotm`/`HOTM`, `Npc`/`NPC`,
  `SkyBlockPlugin`/`SkyblockPlugin`).
- **Several "modules" are fragments** — single enum or model classes (`auction_house`,
  `garden`, `guild`, `talismans`) that exist only to back a richer sibling module.
- **`skyblock-core` is the canonical target** per the README and is where active
  development is concentrated; `core/` and `plugin/` are overlapping legacy stacks.
- **Consolidation roadmap** for these duplicates is tracked separately in
  [`ROADMAP.md`](./ROADMAP.md).
</content>
</invoke>
