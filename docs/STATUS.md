# Project Status

> Audit of every module and class in the SkyBlock repository.
> Generated 2026-06-14. Updated 2026-06-15 to reflect post-cleanup consolidations.
> Counts: **66 declared Maven modules** (`pom.xml`; `auctionhouse` pruned), **577 `.java` source files**.

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
🧩 model/enum/support type only · ✂️ pruned / removed.

---

## Post-cleanup canonical manager registry

The consolidations below are tracked in full in [`CLEANUP.md`](./CLEANUP.md). Every
entry lists the **single canonical class** callers should import; all other variants
carry a `@Deprecated` delegating stub or have been deleted.

| Domain | Canonical class | Status |
|--------|-----------------|--------|
| AuctionHouseManager | `com.skyblock.core.auction.AuctionHouseManager` | ✅ consolidated (#9f504bba) |
| Rarity enum | `com.skyblock.core.model.Rarity` | ✅ consolidated (#2e713bba) |
| ItemBuilder utility | `com.skyblock.core.util.ItemBuilder` | ✅ consolidated (#01eebf00) |
| CollectionManager | `com.skyblock.core.manager.CollectionManager` | ✅ consolidated (Oracle) |
| Collection / CollectionCategory | `com.skyblock.core.model.Collection`, `com.skyblock.core.model.CollectionCategory`, `com.skyblock.core.util.CollectionRegistry` | ✅ consolidated (Sentinel) |
| Menu abstract base | `com.skyblock.core.menu.Menu` | ✅ consolidated (Vega) |
| SkillManager / SkillsManager | `com.skyblock.core.skills.SkillManager` | ✅ consolidated (Vega) |
| EnchantmentManager / EnchantManager | `com.skyblock.core.manager.EnchantmentManager` | ✅ consolidated (#2512 / 64852fe2) |
| CraftingManager / RecipeManager | `com.skyblock.core.crafting.CraftingManager` | ✅ consolidated (#2510 / 3c333a9e) |
| SkillType / SkillXPTable | `com.skyblock.core.skills.SkillManager.SkillType` | ✅ consolidated (#2511 / c8fa794d) |
| QuestManager / QuestsManager | `com.skyblock.core.manager.QuestManager` | ✅ consolidated (#2513) |
| EconomyManager / CoinManager / MoneyManager / PurseManager | `com.skyblock.core.manager.EconomyManager` | ✅ consolidated (#2514) |
| AbilityManager / AbilityHandler / SpecialAbilityManager | `com.skyblock.core.manager.AbilityManager` | ✅ consolidated (#2515) |
| DungeonManager / DungeonsManager | `com.skyblock.core.manager.DungeonManager` | ✅ consolidated (#2517) |
| BazaarManager / BazaarHandler | `com.skyblock.core.manager.BazaarManager` | ✅ consolidated (#2537 / #2541) |
| BankManager / BankingManager / BankHandler | `com.skyblock.core.manager.BankManager` | ✅ consolidated (#2538) |
| IslandManager / IslandHandler / IslandService | `com.skyblock.core.manager.IslandManager` | ✅ consolidated (#2539) |
| MinionManager / MinionsManager | `com.skyblock.core.manager.MinionManager` | ✅ consolidated (#2540) |
| ShopManager / NpcShopManager / ShopHandler | `com.skyblock.core.manager.ShopManager` | ✅ consolidated (#2544) |
| ProfileManager / PlayerProfileManager | `com.skyblock.core.manager.ProfileManager` | ✅ consolidated (#2547) |

Pending consolidations: `SlayerManager`, `EnchantingManager`, `NPCManager`, `GuildManager`,
`TradingManager`, `BrewingManager`. See [`CLEANUP.md`](./CLEANUP.md) for the full pending
table.

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
| `economy` | `CoinManager`, `BankManager`, `AuctionHouseManager`, `BazaarManager`, `CurrencyType` | ✅ Most complete economy module. Canonical `EconomyManager` consolidated into `com.skyblock.core.manager.EconomyManager`. |
| `bank` | `BankManager` | 🔁 `@Deprecated` stub → `com.skyblock.core.manager.BankManager` (#2538). |
| `banking` | `BankManager`, `BankingManager`, `BankTier` | 🔁 `@Deprecated` stubs → `com.skyblock.core.manager.BankManager` (#2538). |
| `bazaar` | `BazaarManager`, `BazaarOrder`, `ProductCategory` | 🔁 `@Deprecated` stub → `com.skyblock.core.manager.BazaarManager` (#2537 / #2541). |

## Auction House

| Module | Classes | Status |
|--------|---------|--------|
| `auction` | `AuctionManager` | 🔁 `@Deprecated` stub delegating to canonical. |
| `auctions` | `AuctionManager`, `AuctionType` | 🔁 `@Deprecated` stub delegating to canonical. |
| `auctionhouse` | *(pruned)* | ✂️ Removed from root `pom.xml`; was a single `@Deprecated` stub. |
| `auction_house` | `AuctionType` | 🔁 Enum-only fragment. |

> Canonical: `com.skyblock.core.auction.AuctionHouseManager` (#9f504bba).

## Islands

| Module | Classes | Status |
|--------|---------|--------|
| `islands` | `Island`, `IslandManager`, `IslandUpgradeManager` | ✅ Most complete. Canonical: `com.skyblock.core.manager.IslandManager` (#2539). |
| `island` | `IslandManager`, `IslandUpgrade` | 🔁 `@Deprecated` stub → `com.skyblock.core.manager.IslandManager` (#2539). |

## Skills & gathering

| Module | Classes | Status |
|--------|---------|--------|
| `skills` | `SkillManager`, `SkillsManager`, `SkillType`, `SkillXPTable` | ✅ Canonical: `com.skyblock.core.skills.SkillManager`. All duplicates consolidated (#2511, Vega). |
| `farming` | `FarmingManager`, `CropType` | ✅ |
| `fishing` | `FishingManager`, `FishingListener`, `FishingDrop`, `FishTreasure`, `FishingZone`, `TrophyFishType` | ✅ Richest gathering module. |
| `foraging` | `ForagingManager`, `ForagingLocation` | ✅ |
| `mining` | `MiningManager`, `MiningLocation`, `HotmPerk` | ✅ |
| `garden` | `CropType` | 🧩 Enum-only fragment. |

## Combat, slayers & dungeons

| Module | Classes | Status |
|--------|---------|--------|
| `combat` | `CombatEngine`, `CombatManager`, `CombatStat`, `CombatStats`, `DamageCalculator`, `DamageType` | ✅ |
| `slayer` | `SlayerManager`, `SlayerBossManager`, `SlayerType` | ✅ Most complete. Pending consolidation. |
| `slayers` | `SlayerManager`, `SlayerQuest`, `SlayerType` | 🔁 Pending consolidation. |
| `skyblock-slayer` | `SlayerManager` | 🔁 Pending consolidation. |
| `dungeons` | `DungeonManager`, `DungeonClass`, `DungeonFloor`, `DungeonRoom`, `DungeonScoreManager` | ✅ Most complete dungeon module. |
| `dungeon` | `DungeonManager` | 🔁 `@Deprecated` stub → `com.skyblock.core.manager.DungeonManager` (#2517). |
| `skyblock-dungeons` | `DungeonManager` | 🔁 `@Deprecated` stub → `com.skyblock.core.manager.DungeonManager` (#2517). |

> Canonical: `com.skyblock.core.manager.DungeonManager` (#2517).

## Items, enchanting, forging & reforging

| Module | Classes | Status |
|--------|---------|--------|
| `items` | `ItemManager`, `SkyBlockItem`, `ItemRarity`, `Rarity` | ✅ Canonical `Rarity` consolidated into `com.skyblock.core.model.Rarity`. |
| `enchanting` | `EnchantingManager`, `EnchantmentRegistry`, `SkyBlockEnchantment`, `EnchantType` | ✅ |
| `enchantments` | `EnchantmentManager`, `SkyBlockEnchantment` | 🔁 `@Deprecated` stub → `com.skyblock.core.manager.EnchantmentManager`. |
| `enchants` | `EnchantManager` | 🔁 `@Deprecated` stub → `com.skyblock.core.manager.EnchantmentManager`. |
| `forging` | `ForgingManager`, `ForgeSlot` | ✅ |
| `item_forge` | `ItemForgeManager` | 🔁 |
| `reforging` | `ReforgingManager` | ✅ |
| `crafting` | `CraftingManager`, `SkyBlockCraftingManager`, `CraftingRecipe`, `RecipeType` | ✅ Canonical: `com.skyblock.core.crafting.CraftingManager` (#2510). |
| `talismans` | `Talisman` | 🧩 Model-only. |
| `accessories` | `AccessoryManager`, `AccessoryRarity` | ✅ |

> Canonical EnchantmentManager: `com.skyblock.core.manager.EnchantmentManager` (#2512).

## Pets & minions

| Module | Classes | Status |
|--------|---------|--------|
| `pets` | `PetManager`, `Pet`, `PetAbility`, `PetTier`, `PetType` | ✅ |
| `minions` | `MinionManager`, `MinionInstance`, `MinionTier`, `MinionType` | ✅ Most complete. Canonical: `com.skyblock.core.manager.MinionManager` (#2540). |
| `minion` | `MinionManager` | 🔁 `@Deprecated` stub → `com.skyblock.core.manager.MinionManager` (#2540). |
| `skyblock-minions` | `MinionManager` | 🔁 `@Deprecated` stub → `com.skyblock.core.manager.MinionManager` (#2540). |

## Profiles

| Module | Classes | Status |
|--------|---------|--------|
| `profiles` | `ProfileManager`, `SkyBlockProfile`, `GameMode`, `ProfileMode` | ✅ Most complete. Canonical: `com.skyblock.core.manager.ProfileManager` (#2547). |
| `profile` | `ProfileManager` | 🔁 `@Deprecated` stub → `com.skyblock.core.manager.ProfileManager` (#2547). |
| `playerdata` | `PlayerProfile` | 🧩 (see app modules). |

> Note: callers are bound to `PlayerProfile` (not `SkyBlockProfile`) — see memory note before converting.

## Collections, quests & progression

| Module | Classes | Status |
|--------|---------|--------|
| `collections` | `CollectionManager`, `CollectionsManager`, `CollectionRegistry`, `CollectionCategory`, `CollectionTier` | ✅ Canonical: `com.skyblock.core.manager.CollectionManager`. |
| `collection` | `CollectionManager` | 🔁 `@Deprecated` stub → `com.skyblock.core.manager.CollectionManager`. |
| `quests` | `QuestManager`, `QuestRegistry`, `QuestType`, `QuestObjectiveType` | ✅ Canonical: `com.skyblock.core.manager.QuestManager` (#2513). |
| `achievements` | `AchievementManager`, `AchievementType` | ✅ |
| `leaderboards` | `LeaderboardManager` | ✅ |
| `bestiary` | `BestiaryManager`, `BestiaryFamily` | ✅ |
| `hotm` | `HotmManager` | ✅ Heart of the Mountain. |
| `fairysouls` | `FairySoulManager` | ✅ |

## NPCs, shops & social

| Module | Classes | Status |
|--------|---------|--------|
| `npc` | `NpcManager`, `NpcType` | 🔁 Pending consolidation. |
| `npcs` | `NPCManager`, `NpcManager` | 🔁 Inconsistent casing within module. Pending consolidation. |
| `shop` | `ShopManager` | ✅ |
| `guilds` | `GuildManager`, `Guild` | ✅ Pending consolidation. |
| `guild` | `GuildRank` | 🧩 Enum-only fragment. |
| `party` | `PartyManager` | ✅ |
| `trades` | `TradeManager`, `TradeCommand` | ✅ Pending consolidation. |
| `trading` | `TradingManager` | 🔁 Pending consolidation. |

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
| `alchemy` | `AlchemyManager`, `BrewingIngredient` | ✅ Richest alchemy/brewing module. Pending consolidation. |
| `brewing` | `BrewingManager` | 🔁 Pending consolidation. |

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
- **Consolidated canonical managers** now live in `com.skyblock.core.manager.*`:
  `QuestManager`, `EconomyManager`, `AbilityManager`, `DungeonManager`,
  `EnchantmentManager`, `CollectionManager`, `BazaarManager`, `BankManager`,
  `IslandManager`, `MinionManager`, `ShopManager`, `ProfileManager`. Facades in
  `com.skyblock.core.skills.*`: `SkillManager`. See the [canonical registry](#post-cleanup-canonical-manager-registry) above.

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

- **Ongoing consolidation has eliminated 20 duplicate surfaces.** Canonical managers for
  AuctionHouseManager, Rarity, ItemBuilder/SkullUtil, CollectionManager, Menu, SkillManager,
  EnchantmentManager, CraftingManager, QuestManager, EconomyManager, AbilityManager,
  DungeonManager, BazaarManager, BankManager, IslandManager, MinionManager, ShopManager,
  and ProfileManager are now single classes with `@Deprecated` delegating stubs replacing
  all duplicates. The `auctionhouse`, `auction`, and `dungeon` leaf modules have been pruned
  or reduced to stubs.
- **6 domains remain unconsolidated** (SlayerManager, EnchantingManager, NPCManager,
  GuildManager, TradingManager, BrewingManager) — tracked in [`CLEANUP.md`](./CLEANUP.md).
- **Naming is inconsistent** — singular vs plural module names (`auction`/`auctions`,
  `collection`/`collections`, `minion`/`minions`) and casing (`Hotm`/`HOTM`, `Npc`/`NPC`,
  `SkyBlockPlugin`/`SkyblockPlugin`).
- **Several "modules" are fragments** — single enum or model classes (`auction_house`,
  `garden`, `guild`, `talismans`) that exist only to back a richer sibling module.
- **`skyblock-core` is the canonical target** per the README and is where active
  development is concentrated; `core/` and `plugin/` are overlapping legacy stacks.
- **Consolidation roadmap** for remaining duplicates is tracked in
  [`CLEANUP.md`](./CLEANUP.md) and the overall goal is documented in
  [`ROADMAP.md`](./ROADMAP.md).
