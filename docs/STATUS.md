# Project Status

> Audit of every module and class in the SkyBlock repository.
> Generated 2026-06-14. Updated 2026-06-15 to reflect post-cleanup consolidations (rounds 17-49: stub removal, pom.xml pruning, 12 menu GUI consolidations, 23 command class stubs deprecated, 4 listener package consolidations and full plugin/listeners/ sweep, ItemBuilder/SkullItemUtil deep sweep, Skill/Stat/Rarity/Collection enum collapse, additional pom.xml dead-module pruning, plugin.util/plugin.managers zero-caller stub sweep, persistence/DataManager consolidation, config-loader consolidation, zero-caller stub sweeps across plugin.items/combat/enchantment/model/data, duplicate listener sweeps across plugin.items/combat/enchantment/world, plugin.combat DamageListener/CombatDamageListener/CombatListener deletion, Menu/GUI base class consolidation, BazaarManager/BazaarHandler stub fixes and zero-caller deletions, plugin.gui/menu/world/event zero-caller stub sweep, sub-package layout enforcement for items/combat/skills modules, ShopManager/NpcShopManager zero-caller stub deletion, sub-package layout enforcement for islands/minions/pets modules — round 41, sub-package layout enforcement for collections module — round 42, BazaarManager/BazaarHandler full consolidation pass — round 43 #2628, CollectionManager/CollectionsManager consolidation — round 44 #2632, AuctionManager/AuctionHouseManager consolidation — round 44 #2633, BankManager deep-pass consolidation — round 45 #2637, IslandManager deep-pass consolidation and 7 stub delegation gaps fixed — rounds 45-47 #2636 / #2640, MinionManager deep-pass consolidation — round 46 #2638, PetManager deep-pass consolidation — round 47 #2639, AccessoryRarity enum consolidation — round 48 #2645, QuestManager/EnchantmentManager/NPCManager duplicate deletion sweep — rounds 48-49 #2642, dead-module pruning post-round-48 — round 49 #2643).
> Counts: **~63 declared Maven modules** (`pom.xml`; `auctionhouse`, `stats`, `minion`, `skills` pruned), **~554 `.java` source files** (23+ zero-caller `@Deprecated` stubs deleted: 16 in #2553 + 4 in #2600 + additional in #2608–#2610 + 3 in #2615).

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
| AuctionHouseManager | `com.skyblock.core.auction.AuctionHouseManager` | ✅ consolidated (#9f504bba / #2593) |
| AuctionManager / AuctionHouseManager | `com.skyblock.core.manager.AuctionManager` | ✅ consolidated (#2633) |
| Rarity enum | `com.skyblock.core.model.Rarity` | ✅ consolidated (#2e713bba) |
| ItemBuilder utility | `com.skyblock.core.util.ItemBuilder` | ✅ consolidated (#01eebf00) |
| CollectionManager / CollectionsManager | `com.skyblock.core.manager.CollectionManager` | ✅ consolidated (Oracle / #2632) |
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
| IslandManager / IslandHandler / IslandService | `com.skyblock.core.manager.IslandManager` | ✅ consolidated (#2539 / #2591) |
| MinionManager / MinionsManager | `com.skyblock.core.manager.MinionManager` | ✅ consolidated (#2540 / #2590) |
| PetManager / PetsManager | `com.skyblock.core.manager.PetManager` | ✅ consolidated (#2592) |
| ShopManager / NpcShopManager / ShopHandler | `com.skyblock.core.manager.ShopManager` | ✅ consolidated (#2544) |
| ProfileManager / PlayerProfileManager | `com.skyblock.core.manager.ProfileManager` | ✅ consolidated (#2547) |
| AccessoryRarity / com.skyblock.accessories.AccessoryRarity | `com.skyblock.core.model.AccessoryRarity` | ✅ consolidated (#2645) |
| NPCManager / NpcManager | `com.skyblock.core.npc.NPCManager` | ✅ consolidated (#2642) |

Pending consolidations: `SlayerManager`, `EnchantingManager`, `GuildManager`,
`TradingManager`, `BrewingManager`. See [`CLEANUP.md`](./CLEANUP.md) for the full pending
table.

---

## Post-cleanup canonical menu GUI registry (rounds 20-24)

12 duplicate GUI class families consolidated to single canonical menus under `com.skyblock.core.menu.*`.

| Domain | Canonical class | Status |
|--------|-----------------|--------|
| CollectionsMenu / CollectionMenu / CollectionGui | `com.skyblock.core.menu.CollectionsMenu` | ✅ consolidated (#2527) |
| AuctionHouseMenu / AuctionMenu / BidMenu / AuctionGui | `com.skyblock.core.menu.AuctionHouseMenu` | ✅ consolidated (#2528 / #2566) |
| CraftingMenu / CraftingGui / CraftingTable | `com.skyblock.core.menu.CraftingMenu` | ✅ consolidated (#2529) |
| BazaarMenu / BazaarGui / ShopMenu (bazaar) | `com.skyblock.core.menu.BazaarMenu` | ✅ consolidated (#2530) |
| EnchantingMenu / EnchantmentMenu / EnchantGui | `com.skyblock.core.menu.EnchantingMenu` | ✅ consolidated (#2531) |
| BankMenu / BankingMenu / BankGui | `com.skyblock.core.menu.BankMenu` | ✅ consolidated (#2532 / #2536) |
| MinionMenu / MinionsMenu / MinionGui | `com.skyblock.core.menu.MinionMenu` | ✅ consolidated (#2533) |
| DungeonMenu / DungeonsMenu / DungeonGui | `com.skyblock.core.menu.DungeonMenu` | ✅ consolidated (#2534) |
| IslandMenu / IslandGui / IslandMainMenu | `com.skyblock.core.menu.IslandMenu` | ✅ consolidated (#2535) |
| SkillsMenu / SkillMenu / SkillGui / SkillsGui | `com.skyblock.core.menu.SkillsMenu` | ✅ consolidated (#2523 / #2563) |
| PetsMenu / PetMenu / PetGui / PetsGui | `com.skyblock.core.menu.PetsMenu` | ✅ consolidated (#2522 / #2565) |
| QuestsMenu / QuestMenu / QuestGui / QuestsGui | `com.skyblock.core.menu.QuestsMenu` | ✅ consolidated (#2524 / #2567) |

---

## Post-cleanup canonical command registry (round 25)

23 duplicate command class stubs deprecated across `plugin.command.*`, `plugin.commands.*`, `core.command.*`, and `core.commands.*` prefix packages. Canonical command implementations live in per-domain sub-packages.

| Stub location | Canonical target | Status |
|---------------|-----------------|--------|
| `com.skyblock.plugin.command.collections.CollectionsCommand` | `com.skyblock.plugin.commands.CollectionsCommand` | ✅ `@Deprecated` stub (#2574) |
| `com.skyblock.plugin.command.auctionhouse.AuctionHouseCommand` | `com.skyblock.plugin.commands.AuctionHouseCommand` | ✅ `@Deprecated` stub (#2574) |
| `com.skyblock.plugin.command.enchanting.EnchantingCommand` | `com.skyblock.plugin.commands.EnchantingCommand` | ✅ `@Deprecated` stub (#2574) |
| `com.skyblock.plugin.command.hotm.HOTMCommand` | `com.skyblock.plugin.commands.HOTMCommand` | ✅ `@Deprecated` stub (#2574) |
| `com.skyblock.plugin.command.hotm.HotmCommand` | `com.skyblock.plugin.commands.HOTMCommand` | ✅ `@Deprecated` stub (#2574) |
| `com.skyblock.plugin.command.skills.SkillsCommand` | `com.skyblock.plugin.commands.SkillsCommand` | ✅ `@Deprecated` stub (#2574) |
| `com.skyblock.plugin.command.pets.PetsCommand` | `com.skyblock.plugin.commands.PetsCommand` | ✅ `@Deprecated` stub (#2574) |
| `com.skyblock.plugin.command.slayer.SlayerCommand` | `com.skyblock.plugin.commands.SlayerCommand` | ✅ `@Deprecated` stub (#2574) |
| `com.skyblock.plugin.command.island.IslandCommand` | `com.skyblock.plugin.commands.IslandCommand` | ✅ `@Deprecated` stub (#2574) |
| `com.skyblock.plugin.command.bank.BankCommand` | `com.skyblock.plugin.commands.BankCommand` | ✅ `@Deprecated` stub (#2574) |
| `com.skyblock.plugin.command.kuudra.KuudraCommand` | `com.skyblock.plugin.commands.KuudraCommand` | ✅ `@Deprecated` stub (#2574) |
| `com.skyblock.plugin.command.bazaar.BazaarCommand` | `com.skyblock.plugin.commands.BazaarCommand` | ✅ `@Deprecated` stub (#2574) |
| `com.skyblock.plugin.command.mayor.MayorCommand` | `com.skyblock.plugin.commands.MayorCommand` | ✅ `@Deprecated` stub (#2574) |
| `com.skyblock.plugin.commands.DungeonCommand` | `com.skyblock.plugin.command.dungeon.DungeonCommand` | ✅ `@Deprecated` stub (#2574) |
| `com.skyblock.plugin.commands.FairyCommand` | `com.skyblock.plugin.command.fairy.FairyCommand` | ✅ `@Deprecated` stub (#2574) |
| `com.skyblock.core.command.QuestCommand` | `com.skyblock.core.quest.QuestCommand` | ✅ `@Deprecated` stub (#2574) |
| `com.skyblock.core.command.AuctionCommand` | `com.skyblock.core.auction.AuctionCommand` | ✅ `@Deprecated` stub (#2574) |
| `com.skyblock.core.command.SkyBlockMenuCommand` | `com.skyblock.core.hub.SkyblockHubCommand` | ✅ `@Deprecated` stub (#2574) |
| `com.skyblock.core.command.IslandCommand` | `com.skyblock.core.island.IslandCommand` | ✅ `@Deprecated` stub (#2574) |
| `com.skyblock.core.command.SkyBlockCommand` | `com.skyblock.core.hub.SkyblockHubCommand` | ✅ `@Deprecated` stub (#2574) |
| `com.skyblock.core.command.CollectionCommand` | `com.skyblock.core.collection.CollectionCommand` | ✅ `@Deprecated` stub (#2574) |
| `com.skyblock.core.commands.SkyBlockCommand` | `com.skyblock.core.hub.SkyblockHubCommand` | ✅ `@Deprecated` stub (#2574) |
| `com.skyblock.core.commands.IslandCommand` | `com.skyblock.core.island.IslandCommand` | ✅ `@Deprecated` stub (#2574) |

Command registration in `SkyBlockPlugin.java` fixed to import canonical `com.skyblock.plugin.command.menu.SkyblockMenuCommand` (#2575). Zero-caller stubs from this round are being removed by Forge.

---

## Post-cleanup sub-package layout registry (round 40)

Zero-caller stub sweep in `plugin.gui`/`plugin.menu`/`plugin.world`/`plugin.event` (#2617) and sub-package layout enforcement across items, combat, and skills modules (#2618, #2620); `ShopManager`/`NpcShopManager` zero-caller stubs deleted (#2619).

| Module | Work done | Status |
|--------|-----------|--------|
| `plugin.gui` / `plugin.menu` / `plugin.world` / `plugin.event` zero-caller stubs | Remaining `@Deprecated` zero-caller stub classes deleted outright | ✅ swept (#2617) |
| `com.skyblock.core.items` / `com.skyblock.core.combat` sub-package layout | All command and listener classes moved into `command.*` / `listener.*` sub-packages; zero-caller strays deleted | ✅ enforced (#2618) |
| `com.skyblock.core.shop.ShopManager` / `com.skyblock.core.shop.ShopCommand` | Zero-caller deprecated stubs deleted; `com.skyblock.core.manager.ShopManager` and `com.skyblock.core.command.ShopCommand` are sole survivors | ✅ deleted (#2619) |
| `com.skyblock.core.skills` sub-package layout | `SkillCommand` → `skills.command.SkillCommand`; `SkillListener` → `skills.listener.SkillListener`; old `skill.*` entries replaced with `@Deprecated` delegation stubs | ✅ enforced (#2620) |

---

## Post-cleanup sub-package layout registry (round 41)

Sub-package layout enforcement for islands module (#2623) and minions/pets modules (#2624).

| Module | Work done | Status |
|--------|-----------|--------|
| `com.skyblock.core.island` sub-package layout | `IslandCommand` and `IslandUpgradeCommand` moved from `com.skyblock.core.island` into `island.command.*` sub-package; old `island.*` files replaced with `@Deprecated` delegation stubs | ✅ enforced (#2623) |
| `com.skyblock.core.minion` / `com.skyblock.core.pet` / `com.skyblock.core.pets` sub-package layout | `MinionCommand`, `PetCommand` (both `pet` and `pets` packages), and `PetsCommand` moved into their respective `command` sub-packages; old flat-package files replaced with `@Deprecated` delegation stubs | ✅ enforced (#2624) |

---

## Post-cleanup sub-package layout registry (round 42)

Sub-package layout enforcement for collections module (#2626).

| Module | Work done | Status |
|--------|-----------|--------|
| `com.skyblock.core.collection` / `com.skyblock.collection` sub-package layout | All command classes outside `com.skyblock.collection.command` and `com.skyblock.core.collection.command` moved into proper `command.*` sub-packages; old flat-package command files replaced with `@Deprecated` delegation stubs; zero-caller strays deleted | ✅ enforced (#2626) |

---

## Post-cleanup manager deletion sweep and enum consolidation (rounds 48-49)

Duplicate manager implementations deleted outright (no stub retained), AccessoryRarity enum canonicalized, and dead modules pruned from `pom.xml`.

| Domain | Canonical class | Work done | Status |
|--------|-----------------|-----------|--------|
| AccessoryRarity / `com.skyblock.accessories.AccessoryRarity` | `com.skyblock.core.model.AccessoryRarity` | Canonical enum created with `displayName`, `color`, and `magicalPower` fields; all `core.accessory.*` and `core.talisman.*` callers updated; `com.skyblock.accessories.AccessoryRarity` retained as `@Deprecated` stub | ✅ consolidated (#2645) |
| QuestManager / QuestsManager (quests, plugin.managers, core.quests) | `com.skyblock.core.manager.QuestManager` | Duplicate implementations deleted from `quests`, `plugin.managers.*`, and `core.quests` modules; `QuestCommand` and `QuestProgressListener` updated to canonical import path | ✅ deleted (#2642) |
| EnchantmentManager / EnchantManager (enchantments, enchants, core.enchant, core.enchanting) | `com.skyblock.core.manager.EnchantmentManager` | Duplicate implementations deleted from `enchantments`, `enchants`, `core.enchant`, and `core.enchanting` modules; `EnchantmentCommand` updated to single import | ✅ deleted (#2642) |
| NPCManager / NpcManager (npc, npcs) | `com.skyblock.core.npc.NPCManager` | Duplicate `NpcManager`/`NPCManager` implementations and orphaned `NpcType` enum deleted from `npc` and `npcs` modules; `SkyBlockPlugin` registration updated | ✅ deleted (#2642) |
| Dead-module pruning (post-round-48 pom.xml sweep) | *(removed from `pom.xml`)* | Three remaining empty/dead module entries removed from root `pom.xml` after the round-48 deletion sweep left their `src/main/java` trees empty | ✅ pruned (#2643) |

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
| `pets` | `PetManager`, `Pet`, `PetAbility`, `PetTier`, `PetType` | ✅ Most complete. Canonical: `com.skyblock.core.manager.PetManager` (#2592). |
| `minions` | `MinionManager`, `MinionInstance`, `MinionTier`, `MinionType` | ✅ Most complete. Canonical: `com.skyblock.core.manager.MinionManager` (#2540). |
| `minion` | `MinionManager` | ✂️ Removed from root `pom.xml` (#2554); contained only a `@Deprecated` stub with zero callers. |
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
| `stats` | `StatsManager`, `PlayerStat` | ✂️ Removed from root `pom.xml` (#2554); contained only `@Deprecated` stubs with zero callers. |
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
  `IslandManager`, `MinionManager`, `PetManager`, `ShopManager`, `ProfileManager`. Facades in
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

- **Ongoing consolidation has eliminated 23 duplicate manager surfaces, 12 duplicate menu GUI surfaces, 23 duplicate command class stubs, 4 duplicate listener package pairs, 5 utility/enum/dead-code sweeps (rounds 29-33), 5 additional dead-code/config/persistence sweeps (rounds 34-36), 1 Menu/GUI base class sweep (rounds 37-38), 1 BazaarManager/BazaarHandler stub fix sweep (round 39), 2 sub-package layout enforcements for islands/minions/pets modules (round 41), 2 manager consolidations for CollectionManager/CollectionsManager and AuctionManager/AuctionHouseManager (round 44 #2632 / #2633), deep-pass consolidations + stub delegation gap fixes for BankManager, IslandManager, MinionManager, and PetManager (rounds 45-47 #2637 / #2636 / #2640 / #2638 / #2639), AccessoryRarity enum consolidation (round 48 #2645), and QuestManager/EnchantmentManager/NPCManager duplicate deletion sweep + pom.xml dead-module pruning (rounds 48-49 #2642 / #2643).** Canonical managers for
  AuctionHouseManager, Rarity, ItemBuilder/SkullUtil, CollectionManager, Menu, SkillManager,
  EnchantmentManager, CraftingManager, QuestManager, EconomyManager, AbilityManager,
  DungeonManager, BazaarManager, BankManager, IslandManager, MinionManager, PetManager,
  ShopManager, and ProfileManager are now single classes with `@Deprecated` delegating stubs replacing
  all duplicates. Rounds 20-24 added 12 canonical menu GUI classes under `com.skyblock.core.menu.*`
  (CollectionsMenu, AuctionHouseMenu, CraftingMenu, BazaarMenu, EnchantingMenu, BankMenu,
  MinionMenu, DungeonMenu, IslandMenu, SkillsMenu, PetsMenu, QuestsMenu). Round 25 deprecated
  23 command class stubs across `plugin.command.*`, `plugin.commands.*`, `core.command.*`, and
  `core.commands.*` prefix packages, and fixed command registration in `SkyBlockPlugin.java`
  (#2574 / #2575). Rounds 26-28 consolidated `plugin.collection`/`plugin.collections`,
  `plugin.skill`/`plugin.skills`, and `plugin.minion`/`plugin.minions` listener packages (#2579,
  #2580, #2581) and swept the `plugin/listeners/` directory of all remaining stray duplicates
  (#2578). Rounds 29-30 completed deep sweeps of `ItemBuilder`/`ItemStackBuilder` and
  `SkullItemUtil`/`SkullCreator`/`SkullBuilder` duplicates across all modules (#2596 / #2597).
  Round 31 collapsed all duplicate `Skill`, `Stat`, `Rarity`, and `Collection` enum/registry
  definitions into single canonical enums under `com.skyblock.core.*` (#2598). Round 32 pruned
  the `skills` module (and any other empty/stub-only modules) from the root `pom.xml` (#2599).
  Round 33 deleted 4 zero-caller `@Deprecated` stubs from `plugin.managers.*` and
  `plugin.manager.*` and updated 3 stale `{@link}` javadoc references (#2600).
  Round 34 consolidated all persistence/DataManager duplicates into `com.skyblock.core.persistence.DataManager` (#2606) and consolidated all config-loader duplicates (ConfigManager/SkyBlockConfig/PluginConfig) into `com.skyblock.core.config.ConfigManager`, deleting `SkillsConfig`, `SkillXPConfig`, and `SkillLevelUpManager` zero-caller stubs (#2607).
  Round 35 swept zero-caller `@Deprecated` stubs from `plugin.items`, `plugin.combat`, `plugin.enchantment`, `plugin.model`, and `plugin.data` packages (#2608) and swept duplicate listeners from `plugin.items`, `plugin.combat`, `plugin.enchantment`, and `plugin.world` packages, deleting 4 zero-caller stubs (`CollectionListener`, `CollectionTracker`, `CollectionTrackingListener`, `CollectionsListener`) (#2609).
  Round 36 deleted `DamageListener`, `CombatDamageListener`, and `CombatListener` from `plugin.combat` and removed the duplicate `plugin.combat.CombatListener` registration from `SkyBlockPlugin` (#2610).
  Rounds 37-38 deleted `plugin.menu.SkyBlockMenu` and `plugin.gui.menu.SkyBlockMainMenu` (zero-caller deprecated stubs), collapsed `plugin.gui.Menu` into a `@Deprecated` thin wrapper extending `com.skyblock.core.menu.Menu`, and removed the stale listener registration from `SkyBlockPlugin` (#2613).
  Round 39 deleted 3 remaining zero-caller Bazaar stubs (`com.skyblock.core.bazaar.BazaarProduct` placeholder, duplicate `com.skyblock.core.bazaar.BazaarCommand`, and dead `BazaarProduct` re-export) and ensured all remaining `@Deprecated` BazaarManager/BazaarHandler stubs carry delegating constructors or static-factory redirects (#2615).
  Round 40 swept remaining zero-caller `@Deprecated` stubs from `plugin.gui`, `plugin.menu`, `plugin.world`, and `plugin.event` packages (#2617), enforced sub-package layout for `com.skyblock.core.items` and `com.skyblock.core.combat` modules (commands and listeners moved into `command.*`/`listener.*` sub-packages; zero-caller strays deleted) (#2618), deleted the `com.skyblock.core.shop.ShopManager` and `com.skyblock.core.shop.ShopCommand` zero-caller deprecated stubs (#2619), and enforced sub-package layout for the `com.skyblock.core.skills` module (`SkillCommand` → `skills.command.SkillCommand`; `SkillListener` → `skills.listener.SkillListener`) (#2620).
  Round 41 enforced sub-package layout for the `com.skyblock.core.island` module (`IslandCommand` and `IslandUpgradeCommand` moved into `island.command.*`) (#2623) and for the `com.skyblock.core.minion`, `com.skyblock.core.pet`, and `com.skyblock.core.pets` modules (`MinionCommand`, `PetCommand`, `PetsCommand` moved into their respective `command` sub-packages) (#2624).
  Round 42 enforced sub-package layout for the `com.skyblock.core.collection` and `com.skyblock.collection` modules (all command classes moved into proper `command.*` sub-packages; zero-caller strays deleted) (#2626).
  Round 43 completed the BazaarManager/BazaarHandler full consolidation pass, ensuring all duplicate variants across every module carry delegating stubs or are deleted (#2628).
  Round 44 consolidated all `CollectionManager`/`CollectionsManager` duplicates across `collections`, `collection`, `core`, and `plugin` modules into `com.skyblock.core.manager.CollectionManager` (#2632) and consolidated all `AuctionManager`/`AuctionHouseManager` duplicates across `auction`, `auctions`, `auctionhouse`, `core`, and `plugin` modules into `com.skyblock.core.manager.AuctionManager` (#2633).
  Rounds 45-47 completed deep-pass consolidations for `BankManager`/`BankingManager`/`BankHandler` (all remaining delegation stubs wired end-to-end; missing `economy` module methods merged into canonical) (#2637), `IslandManager`/`IslandHandler`/`IslandService` (7 empty-returning stub methods replaced with full delegation chain: plugin stub → `core.manager.IslandManager` → `islands.IslandManager`) (#2636 / #2640), `MinionManager`/`MinionsManager` (minion-tier and minion-type methods merged into canonical) (#2638), and `PetManager`/`PetsManager` (XP table and pet-ability methods merged into canonical) (#2639).
  Round 48 canonicalized the `AccessoryRarity` enum into `com.skyblock.core.model.AccessoryRarity` (#2645) and deleted duplicate manager implementations outright (no stubs retained) for `QuestManager` (from `quests`, `plugin.managers`, and `core.quests`), `EnchantmentManager` (from `enchantments`, `enchants`, `core.enchant`, and `core.enchanting`), and `NPCManager`/`NpcManager` (from `npc` and `npcs`) (#2642).
  Round 49 pruned three additional dead module entries from the root `pom.xml` after the round-48 deletion sweep left their `src/main/java` trees empty (#2643).
  The `auctionhouse`, `auction`, `dungeon`, `stats`, `minion`, and `skills` leaf modules
  have been pruned from the parent `pom.xml`; **23+ zero-caller `@Deprecated` stub files were
  deleted outright** (16 in #2553 + 4 in #2600 + additional in #2608–#2610 + 3 in #2615), reducing the source tree from 577 to ~554 files.
  The parent `pom.xml` now declares **~60 modules** (down from 66 before rounds 20–21, further reduced by dead-module sweeps in rounds 32 and 49).
- **5 domains remain unconsolidated** (SlayerManager, EnchantingManager, GuildManager,
  TradingManager, BrewingManager) — tracked in [`CLEANUP.md`](./CLEANUP.md).
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
