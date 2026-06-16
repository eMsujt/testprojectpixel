# Project Status

> Audit of every module and class in the SkyBlock repository.
> Generated 2026-06-14. Updated 2026-06-16 to reflect post-cleanup consolidations (rounds 17-49: stub removal, pom.xml pruning, 12 menu GUI consolidations, 23 command class stubs deprecated, 4 listener package consolidations and full plugin/listeners/ sweep, ItemBuilder/SkullItemUtil deep sweep, Skill/Stat/Rarity/Collection enum collapse, additional pom.xml dead-module pruning, plugin.util/plugin.managers zero-caller stub sweep, persistence/DataManager consolidation, config-loader consolidation, zero-caller stub sweeps across plugin.items/combat/enchantment/model/data, duplicate listener sweeps across plugin.items/combat/enchantment/world, plugin.combat DamageListener/CombatDamageListener/CombatListener deletion, Menu/GUI base class consolidation, BazaarManager/BazaarHandler stub fixes and zero-caller deletions, plugin.gui/menu/world/event zero-caller stub sweep, sub-package layout enforcement for items/combat/skills modules, ShopManager/NpcShopManager zero-caller stub deletion, sub-package layout enforcement for islands/minions/pets modules — round 41, sub-package layout enforcement for collections module — round 42, BazaarManager/BazaarHandler full consolidation pass — round 43 #2628, CollectionManager/CollectionsManager consolidation — round 44 #2632, AuctionManager/AuctionHouseManager consolidation — round 44 #2633, BankManager deep-pass consolidation — round 45 #2637, IslandManager deep-pass consolidation and 7 stub delegation gaps fixed — rounds 45-47 #2636 / #2640, MinionManager deep-pass consolidation — round 46 #2638, PetManager deep-pass consolidation — round 47 #2639, AccessoryRarity enum consolidation — round 48 #2645, QuestManager/EnchantmentManager/NPCManager duplicate deletion sweep — rounds 48-49 #2642, dead-module pruning post-round-48 — round 49 #2643, abstract Menu/InventoryGUI base class consolidation — round 50 #2650, persistence-helper class consolidation — round 51 #2651, SkyBlockPlugin wiring fix and top-level menu consolidation — round 52 #2652/#2654, skills-domain package layout normalization — round 53 #2660, economy-domain package layout normalization — round 53 #2662, core-module package layout normalization — round 53 #2661, stale-import fix sweep post-round-92 (no changes needed) + skills/collections package-layout standardization + dead-code deletion of 8 orphan files — round 93 #2772/#2773, package layout standardization for gui/pets/auction/minions/bazaar/shop modules — round 94 #2777/#2778/#2779/#2780/#2781/#2782, shop-module standardization completion + stale-import sweep (round-94 follow-up, stale references identified for round-96 remediation) — round 95, stale-import fix sweep for round-94/95 package-layout standardizations (3 references fixed) — round 96 #2785, core-module internal package standardization: audit and reorganize com.skyblock.core misplaced classes into canonical sub-packages — round 97 #2789, stale-import fix sweep and dead-module pruning — round 98 #2787/#2788, economy/items/combat module package-layout stub consolidations: SkillManager/SkillsManager zero-caller stub deletion, ShopManager deprecated stub deletion, BazaarManager/BazaarCommand/BazaarMenu deprecated stub deletion, and stale CustomItemManager import fix — round 99 #2792/#2793/#2794/#2795, IslandManager/IslandHandler consolidation (7 copies → 1 canonical at com.skyblock.core.island.manager, 9 references across 7 files updated) + AuctionHouseManager audit (9 copies, no file changes needed, already sole implementation) + 4 zero-caller @Deprecated stub deletions — round 101 #2803/#2804, stale-import fix sweep (round-101 follow-up, no file changes needed) + IslandGenerator zero-caller stub deletion + EnchantManager/EnchantmentManager/EnchantHandler consolidation (canonical at com.skyblock.core.enchant.manager.EnchantmentManager, @Deprecated stubs at old locations) — round 102 #2806/#2807, stale-import fix sweep (round-104 ItemBuilder/SkullItemUtil follow-up, no changes needed) + zero-caller ItemBuilder/SkullUtil/SkullBuilder stub deletion — round 105, Menu/AbstractMenu/GuiBuilder base class consolidation (all duplicate abstract base classes collapsed into com.skyblock.core.menu.Menu, zero-caller stubs deleted) + stale-import sweep (no changes needed) — round 106 #2817, SkillManager delegation stub getInstance() fix + Collection/CollectionType/Collections enum audit (no file changes needed) + Skill/SkillType/Skills enum consolidation + zero-caller @Deprecated stub deletion for rounds 102–106 — round 107 #2819/#2820, SkillManager/SkillsManager consolidation into canonical com.skyblock.core.skills.manager.SkillManager — round 119 #2860, zero-caller @Deprecated stub deletion: SkillManager stub at com.skyblock.core.skills.manager + CollectionManager stub at com.skyblock.core.collections.manager + PetManager stub at com.skyblock.pets.manager — round 120 #2861/#2862, AuctionHouseManager consolidation: grepped every module for AuctionHouseManager/AuctionManager, identified canonical com.skyblock.core.manager.AuctionHouseManager as sole survivor, deleted @Deprecated forwarding stub at com.skyblock.core.auction.manager after confirming zero non-stub callers — round 121 #2863/#2865, MinionManager stub deletion at com.skyblock.core.minion.manager + IslandManager stub audit (no file changes) + ProfileManager stale-import fix in SkyBlockPlugin.java — round 122 #2870/#2871, Skill/Stat/Rarity/Collection enum duplicate consolidation: FishRarity and RabbitRarity duplicate enum definitions removed from FishingManager/FishingCommand/ChocolateFactory-related files and replaced with canonical com.skyblock.core.model.Rarity enum — round 123 #2875, CollectionsMenu border fix: extracted fillBorder() from build() to cover top row, bottom row, left column, and right column borders using getRows() for dynamic row count — round 126 #2887, ShopManager/NpcShopManager/ShopHandler consolidation: grepped every module, confirmed canonical at com.skyblock.core.manager.ShopManager, updated all 6 import sites (NPCListener, NpcCommand, NpcManager, SkyBlockPlugin, ShopListener, ShopManagerTest), @Deprecated placeholder left at old com.skyblock.core.shop.manager.ShopManager — round 127, auction module and profile/player package layout standardization — round 136 #2916/#2915, dead-module pruning and quests/slayer/dungeon/fishing/farming package layout standardization — rounds 136–137 #2918/#2919/#2920/#2921).
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
| Skill / SkillType enum | `com.skyblock.core.model.Skill` | ✅ consolidated (#2745) |
| Stat / StatType / PlayerStat / CombatStat | `com.skyblock.core.model.Stat` | ✅ consolidated (Oracle) |
| QuestManager / QuestsManager | `com.skyblock.core.manager.QuestManager` | ✅ consolidated (#2513) |
| EconomyManager / CoinManager / MoneyManager / PurseManager | `com.skyblock.core.manager.EconomyManager` | ✅ consolidated (#2514) |
| AbilityManager / AbilityHandler / SpecialAbilityManager | `com.skyblock.core.manager.AbilityManager` | ✅ consolidated (#2515) |
| DungeonManager / DungeonsManager | `com.skyblock.core.manager.DungeonManager` | ✅ consolidated (#2517) |
| BazaarManager / BazaarHandler | `com.skyblock.core.manager.BazaarManager` | ✅ consolidated (#2537 / #2541) |
| BankManager / BankingManager / BankHandler | `com.skyblock.core.manager.BankManager` | ✅ consolidated (#2538) |
| IslandManager / IslandHandler / IslandService | `com.skyblock.core.island.manager.IslandManager` | ✅ consolidated (#2539 / #2591 / #2803) |
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

- **Ongoing consolidation has eliminated 23 duplicate manager surfaces, 12 duplicate menu GUI surfaces, 23 duplicate command class stubs, 4 duplicate listener package pairs, 5 utility/enum/dead-code sweeps (rounds 29-33), 5 additional dead-code/config/persistence sweeps (rounds 34-36), 1 Menu/GUI base class sweep (rounds 37-38), 1 BazaarManager/BazaarHandler stub fix sweep (round 39), 2 sub-package layout enforcements for islands/minions/pets modules (round 41), 2 manager consolidations for CollectionManager/CollectionsManager and AuctionManager/AuctionHouseManager (round 44 #2632 / #2633), deep-pass consolidations + stub delegation gap fixes for BankManager, IslandManager, MinionManager, and PetManager (rounds 45-47 #2637 / #2636 / #2640 / #2638 / #2639), AccessoryRarity enum consolidation (round 48 #2645), QuestManager/EnchantmentManager/NPCManager duplicate deletion sweep + pom.xml dead-module pruning (rounds 48-49 #2642 / #2643), abstract Menu/InventoryGUI base class consolidation (round 50 #2650), persistence-helper class consolidation (round 51 #2651), SkyBlockPlugin wiring fix + top-level menu consolidation + zero-caller stub deletion (round 52 #2654 / #2652), skills-domain/economy-domain/core-module package layout normalization (round 53 #2660 / #2662 / #2661), pets-domain/items-domain package layout normalization + ShopManager/NpcShopManager consolidation + BazaarOrder stub deletion (round 54 #2666 / #2667), GUI-domain package layout normalization (round 55 #2670), and combat/quests-domain package layout normalization + AuctionHouseManager/ShopManager/BazaarManager/CollectionManager wiring into SkyBlockPlugin (round 56 #2671 / #2673).** Canonical managers for
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
  Round 50 collapsed all duplicate abstract base classes (`Menu`, `InventoryGUI`, `GuiMenu`, `InventoryMenu`, `BaseMenu`) across every module into `com.skyblock.core.menu.Menu`; all concrete menu subclasses updated to extend the canonical base (#2650).
  Round 51 consolidated remaining duplicate persistence-helper classes (`PlayerDataManager`, `DataManager`, `PersistenceHelper`, `StorageManager`, and `YamlPlayerStorage` variants) not already folded into `com.skyblock.core.persistence.DataManager`; dead stubs deleted (#2651).
  Round 52 re-wired every canonical manager registration in `SkyBlockPlugin.onEnable` and restored the missing `FarmingListener` registration exposed by the rounds 50-51 consolidation (#2654); collapsed the remaining duplicate top-level menu classes (`SkyBlockMenu`, `SkyBlockMainMenu`, `MainMenu`, and analogues) into `com.skyblock.core.menu.Menu` (#2652); and deleted 6 zero-caller `@Deprecated` stub/delegate classes outright (`core.combat.CombatListener`, `core.skill.SkillListener`, `core.collections.CollectionCommand`, `core.ability.AbilityManager`, `core.island.IslandUpgradeCommand`, and `crafting.CraftingManager`) (pending PR).
  Round 53 normalized package layouts across the skills-domain (all classes in every skills module moved into correct `.manager`/`.listener`/`.command`/`.model` sub-packages) (#2660), economy-domain (all classes across bazaar, auction, bank, and shop modules moved into correct sub-packages; flat-package strays eliminated) (#2662), and core-module (`PlayerData` and other misplaced types in `skyblock-core` root relocated to canonical sub-packages) (#2661).
  Round 54 normalized package layouts for the pets-domain and items-domain (all classes across `skyblock-pets` and `skyblock-items` modules moved into correct `.manager`/`.listener`/`.command`/`.model` sub-packages) (#2666); consolidated `ShopManager`/`NpcShopManager` by updating `NPCShopListener` and `ShopListener` to delegate to `ShopManager.getInstance().getShop()` and removing duplicate `shops.yml`-parsing logic (#2667); and deleted the remaining zero-caller `@Deprecated` `BazaarOrder` and orphaned Bazaar stubs after confirming the canonical `com.skyblock.core.manager.BazaarManager` is the sole implementation (pending PR).
  Round 55 normalized the GUI-domain package layout (all classes in every GUI-domain module moved into correct `.menu`/`.command`/`.listener`/`.util` sub-packages; `StorageMenu`, `PotionBagMenu`, `QuiverMenu`, and `FishingBagMenu` migrated into `com.skyblock.plugin.gui.menu`; flat-package strays eliminated) (#2670).
  Round 56 normalized the combat-domain and quests-domain package layouts (all classes in every combat and quests module moved into correct `.manager`/`.listener`/`.command`/`.model` sub-packages; misplaced strays deleted or restubbed) (#2671); and wired `AuctionHouseManager`, `ShopManager`, `BazaarManager`, and `CollectionManager` into `SkyBlockPlugin.onEnable` (`load()`) and `onDisable` (`save()`), with `ShopManager` promoted to a proper import (#2673).
  Round 57 completed a deep-pass consolidation of all 9 `AuctionHouseManager`/`AuctionManager` duplicate implementations across `auction`, `auctions`, `auctionhouse`, `auction_house`, `core.*`, and `plugin.*` modules into the single canonical `com.skyblock.core.manager.AuctionHouseManager`; all callers and `SkyBlockPlugin` registrations updated (#2672).
  Round 58 completed a deep-pass consolidation of all 7 `@Deprecated` `PetManager`/`PetsManager` stub implementations across `pets`, `pet`, `core.*`, and `plugin.*` modules into the single canonical `com.skyblock.core.manager.PetManager`; all callers and `SkyBlockPlugin` registrations updated (#2680).
  Round 59 completed deep-pass consolidations of: all 3 `@Deprecated` `SkillManager`/`SkillsManager` stub implementations across `skills`, `core.*`, and `plugin.*` modules into the single canonical `com.skyblock.core.manager.SkillManager` (#2684); all `CollectionManager`/`CollectionsManager` duplicates across `collections`, `collection`, `core.*`, and `plugin.*` modules into the single canonical `com.skyblock.core.manager.CollectionManager` (#2682); and all 12 `MinionManager`/`MinionHandler`/`MinionService` duplicate implementations across `minion`, `minions`, `core.*`, and `plugin.*` modules into the single canonical `com.skyblock.core.manager.MinionManager` (#2681).
  Round 60 completed deep-pass consolidations of: all 4 `ShopManager`/`NpcShopManager` duplicate/stub files (`NPCShopListener`, `plugin.npc.NpcShopMenu`, `plugin.shop.NpcShopMenu`, `plugin.shop.ShopListener`) deleted outright and canonical `com.skyblock.plugin.shop.listener.ShopListener` wired into `SkyBlockPlugin.onEnable` (#2685); and the two remaining orphaned `BazaarManager`/`BazaarHandler` model stubs (`com.skyblock.bazaar.model.BazaarOrder` and `ProductCatalog`) confirmed zero-caller and deleted, leaving `com.skyblock.core.manager.BazaarManager` as the sole implementation (#2686).
  Round 61 completed: all remaining duplicate `IslandManager`/`SkyBlockIslandManager` implementations across `islands`, `core.*`, and `plugin.*` modules consolidated into the single canonical `com.skyblock.core.manager.IslandManager` with all callers and `SkyBlockPlugin` registrations updated (#2689); all duplicate `Skill`, `Stat`, `Rarity`, and `Collection` enum/registry classes across every module consolidated into one canonical set in `com.skyblock.core.model`, with all callers updated and orphaned duplicates deleted (#2690); all classes across every `com.skyblock.combat` module moved into correct `.command`/`.listener`/`.model`/`.calculator`/`.manager` sub-packages, 2 live callers migrated from deprecated root-level stubs to canonical sub-package imports, all 15 `@Deprecated` stub files deleted, and the zero-caller `CombatStat`, `CollectionCategory`, and `CollectionRegistry` root-level stubs removed as a post-review follow-up (#2691).
  Round 62 completed the post-review follow-up to Forge's combat-domain normalization: `CombatStat`, `CollectionCategory`, and `CollectionRegistry` zero-caller `@Deprecated` root-level stub classes (flagged by Oracle in the round-61 PR review) deleted outright from `com.skyblock.combat` (#2694).
  Round 63 completed the SkyBlock main-menu GUI consolidation: all duplicate main-menu GUI classes (`SkyBlockMenu`, `SkyBlockMainMenu`, `MainSkyBlockMenu`, `SkyBlockInventoryMenu`, and near-duplicate variants) across every module consolidated into the single canonical `com.skyblock.plugin.gui.menu.SkyBlockMenu`; all callers, registrations, and `SkyBlockPlugin.onEnable` references updated; orphaned copies deleted (#2695).
  Round 64 completed a deep-pass consolidation of all `BankManager`/`BankingManager`/`BankingService` duplicate implementations across every module into the single canonical `com.skyblock.core.manager.BankManager`; all callers, imports, and `SkyBlockPlugin` registrations updated; orphaned duplicate copies deleted (#2697).
  Round 65 completed a thorough final-pass audit of every class named `BazaarManager`, `BazaarHandler`, or `BazaarService` across every module; all remaining stubs and duplicates deleted outright; `com.skyblock.core.manager.BazaarManager` confirmed as the sole implementation (#2698).
  Round 66 completed thorough final-pass audits of `CollectionManager`/`CollectionsManager`, `SkillManager`/`SkillsManager`, and `PetManager`/`PetsManager` across every module: 2 remaining `@Deprecated` stubs deleted for CollectionManager (`core/collections/CollectionsCommand.java` and `skyblock-core/command/CollectionCommand.java`) (#2700); 2 orphaned `@Deprecated` stubs deleted for SkillManager (#2701); 6 remaining `@Deprecated` stubs (`PetsCommand.java` ×2, `PetCommand.java` ×2, `PetAbility.java`, `PetsMenu.java`) deleted for PetManager (#2703); all three canonical managers confirmed as sole implementations.
  Round 67 extended `SkyBlockPlugin.java` `onEnable` to properly instantiate and wire all canonical managers in dependency order (`SkillManager` → `CollectionManager` → `MinionManager` → `PetManager` → `AuctionHouseManager` → `BankManager` → `BazaarManager` → `ShopManager` → `IslandManager`); `onDisable` teardown updated to match (#2706).
  Round 68 normalized the minion-domain and items-domain package layouts: every class across every module containing minion-related classes (`MinionManager`, `MinionListener`, `MinionCommand`, and related model types) moved into correct `.manager`/`.listener`/`.command`/`.model` sub-packages, flat-package strays deleted, and `com.skyblock.core.manager.MinionManager` confirmed as sole manager implementation (#2707); every item-related class not already in `com.skyblock.core.util` similarly reorganized into correct sub-packages with flat-package strays eliminated (#2709).
  Round 69 normalized the economy-domain, island-domain, and guild-domain package layouts: every class across every module containing bank, bazaar, or auction-related classes moved into correct `.manager`/`.listener`/`.command`/`.model` sub-packages with flat-package strays eliminated and canonical `BankManager`, `BazaarManager`, and `AuctionHouseManager` confirmed as sole implementations (#2710); every class across every module containing island-related and guild-related classes (`IslandManager`, `IslandListener`, `IslandCommand`, `GuildManager`, `GuildListener`, `GuildCommand`) similarly moved into correct sub-packages with flat-package strays deleted (#2711).
  Round 70 normalized the skills-domain and pets-domain package layouts: every class across every module containing skill-related classes (`SkillManager`, `SkillListener`, `SkillCommand`, `SkillLeaderboard`, and related model types) moved into correct `.manager`/`.listener`/`.command`/`.model` sub-packages, flat-package strays deleted, and `com.skyblock.core.manager.SkillManager` confirmed as sole manager implementation (#2713); every class across every module containing pet-related classes (`PetManager`, `PetListener`, `PetCommand`, `PetItem`, `PetType`, and related model types) similarly reorganized into correct sub-packages with `com.skyblock.core.manager.PetManager` confirmed as sole implementation (#2714).
  Round 71 fixed the skills-domain normalization gap flagged by Oracle in the round-70 PR review: every orphan `SkillsListener`, `SkillsXPListener`, and `SkillListener` stub missed by the round-70 pass located and deleted outright, with `com.skyblock.core.manager.SkillManager` confirmed as the sole skills-domain implementation (#2715); and normalized the GUI/menu-domain package layout: every class across every module containing GUI or menu-related classes (`MenuManager`, `GuiManager`, `InventoryClickListener`, and all concrete menu subclasses) moved into correct `.menu`/`.listener`/`.manager`/`.util` sub-packages, flat-package strays eliminated, and `com.skyblock.core.menu.Menu` confirmed as the sole abstract base with all subclasses in proper sub-packages (#2717).
  Round 72 normalized the shop-domain and collections-domain package layouts: every class across every module containing shop or NPC-shop-related classes (`ShopManager`, `ShopListener`, `ShopCommand`, `NpcShopManager`, `NPCShopListener`, and related model types) moved into correct `.manager`/`.listener`/`.command`/`.model` sub-packages, flat-package strays eliminated, and `com.skyblock.core.manager.ShopManager` confirmed as sole manager implementation (#2718); every class across every module containing collection-related classes (`CollectionManager`, `CollectionListener`, `CollectionCommand`, and related model types) similarly reorganized, 21 orphan/duplicate files deleted (deprecated stubs in `skyblock-core.collection`/`skyblock-core.collections`, the entire `collections` module, and the `core.command.CollectionsCommand` duplicate), and `com.skyblock.core.manager.CollectionManager` confirmed as sole manager implementation (#2720).
  Round 73 normalized the config-domain package layout: every class across every module containing config-related classes (`ConfigManager`, `SkyBlockConfig`, `PluginConfig`, and related loader/watcher types) moved into correct `.config`/`.loader`/`.model` sub-packages, flat-package strays eliminated, and `com.skyblock.core.config.ConfigManager` confirmed as sole config-domain implementation with no remaining duplicate loaders or config-parser stubs (#2725); and deleted the orphan `AuctionCommand.java` stub — the repo already had a single canonical `AuctionHouseManager` with no duplicate `AuctionManager` or `AuctionHandler` classes, and the never-instantiated `AuctionCommand` was the sole remaining consolidation artifact, removed outright (#2726).
  Round 75 completed a thorough final-pass consolidation of `SkillManager`/`SkillsManager`: every class named `SkillManager` or `SkillsManager` across every module audited, all remaining duplicate implementations and `@Deprecated` delegation stubs deleted outright, and `com.skyblock.core.manager.SkillManager` confirmed as the sole implementation with no remaining orphan stubs (#2729).
  Round 76 completed thorough final-pass consolidations of `CollectionManager`/`CollectionsManager` (all remaining duplicate implementations and `@Deprecated` delegation stubs deleted, `com.skyblock.core.manager.CollectionManager` confirmed as sole implementation (#2728)) and `BazaarManager` (orphan `com.skyblock.plugin.economy.BazaarCategoryMenu` deleted, `com.skyblock.core.manager.BazaarManager` confirmed as sole implementation with all callers referencing it directly (#2730)).
  Round 77 completed a thorough final-pass consolidation of `MinionManager`: `plugin.minion.MinionManager` (location-based duplicate) and `plugin.minion.MinionPlacementListener` (duplicate of `plugin.listener.MinionPlacementListener`) deleted outright; `com.skyblock.core.manager.MinionManager` confirmed as sole implementation (#2731).
  Round 78 completed a thorough final-pass consolidation of `IslandManager`: every class named `IslandManager` across every module audited; all remaining duplicate implementations and `@Deprecated` delegation stubs deleted outright; `com.skyblock.core.manager.IslandManager` confirmed as sole implementation with no remaining orphan stubs (#2733).
  Round 79 completed a thorough final-pass consolidation of shared utility classes: every `ItemBuilder.java`, `SkullItemUtil.java`, and `MenuUtil.java` across every module audited; all non-canonical duplicate implementations deleted outright; canonical classes in `com.skyblock.core.util` confirmed as sole implementations with no remaining orphan copies (#2736).
  Round 80 completed a thorough final-pass audit of every class implementing `CommandExecutor` across every module; all non-canonical duplicate executor implementations deleted outright; canonical command classes in `com.skyblock.core.command` confirmed as sole implementations with no remaining orphan stubs (#2737).
  Round 81 completed a thorough final-pass consolidation of `ShopManager`/`NpcShopManager`: every class named `ShopManager` or `NpcShopManager` across every module audited; all remaining duplicate implementations and `@Deprecated` delegation stubs deleted outright; `com.skyblock.core.manager.ShopManager` confirmed as sole implementation with no remaining orphan stubs (#2734).
  Round 82 completed a thorough final-pass consolidation of `ProfileManager`: every class named `ProfileManager` across every module audited; the `profiles` module's zero-caller `@Deprecated` `ProfileManager` (a pure delegation stub to the canonical) deleted outright; `com.skyblock.core.manager.ProfileManager` confirmed as sole implementation with no remaining orphan stubs (#2741).
  Round 83 completed a thorough final-pass consolidation of the `Skill`/`SkillType` enum: every `Skill` and `SkillType` enum definition across every module audited; canonical `com.skyblock.core.model.Skill` created by merging the 12-skill `SkillManager.SkillType` inner enum and the 11-skill `Skills` enum; all 18 callers updated to import from `com.skyblock.core.model.Skill`; duplicate inner enums and orphaned `SkillType` variants deleted outright; one canonical enum definition confirmed with no remaining orphan copies (#2745).
  Round 84 completed a thorough final-pass consolidation of the `Stat`/`StatType` enum: every `Stat`, `StatType`, `PlayerStat`, and `CombatStat` enum definition across every module audited; canonical `com.skyblock.core.model.Stat` created with 26 constants and full metadata; `PlayerStat`, `plugin.items.StatType`, `combat.CombatStat`, `core.combat.StatManager.CombatStat`, `core.stats.StatsManager.StatType`, and `core.stat.StatManager.StatType` replaced with `@Deprecated` stubs or removed; 20+ caller files migrated to import from `com.skyblock.core.model.Stat`; one canonical enum definition confirmed with no remaining orphan copies (Oracle).
  Round 85 completed a thorough final-pass consolidation of the `ItemType`/`Material` wrapper enum: every `ItemType` enum definition across every module audited; all duplicate `ItemType` variants and `@Deprecated` delegation stubs deleted outright; canonical `com.skyblock.core.model.ItemType` confirmed as sole implementation with no remaining orphan copies (#2747).
  Round 86 completed a thorough final-pass consolidation of `EconomyManager`/`CurrencyManager`: every class named `EconomyManager` or `CurrencyManager` across every module audited; all duplicate implementations and `@Deprecated` delegation stubs deleted outright; canonical `com.skyblock.core.manager.EconomyManager` confirmed as sole implementation with no remaining orphan copies (#2752).
  Round 87 completed a thorough final-pass consolidation of `QuestManager`/`QuestHandler`: every class named `QuestManager` or `QuestHandler` across every module audited; all duplicate implementations and `@Deprecated` delegation stubs deleted outright; canonical `com.skyblock.core.manager.QuestManager` confirmed as sole implementation with no remaining orphan copies (#2753).
  Round 88 completed a thorough final-pass consolidation of `CollectionManager`/`CollectionsManager`: every class named `CollectionManager` or `CollectionsManager` across every module audited; all duplicate implementations and `@Deprecated` delegation stubs deleted outright; canonical `com.skyblock.core.manager.CollectionManager` confirmed as sole implementation with no remaining orphan copies (#2759).
  Round 89 completed a thorough final-pass consolidation of `PetManager`/`PetsManager`: every class named `PetManager` or `PetsManager` across every module audited; all duplicate implementations and `@Deprecated` delegation stubs deleted outright; canonical `com.skyblock.core.manager.PetManager` confirmed as sole implementation with no remaining orphan copies (#2762).
  Round 90 completed a thorough final-pass consolidation of `AuctionHouseManager`/`AuctionManager`/`AuctionHandler`: every class named `AuctionHouseManager`, `AuctionManager`, or `AuctionHandler` across every module audited; all stale import references introduced by the consolidation fixed across every `.java` file; all duplicate implementations and `@Deprecated` delegation stubs deleted outright; canonical `com.skyblock.core.manager.AuctionHouseManager` confirmed as sole implementation with no remaining orphan copies (#2763).
  Round 91 completed a thorough final-pass consolidation of all GUI/Menu classes: every class implementing or extending the plugin's `Menu` base across every module audited; all duplicate `GuiManager`, `SkyBlockMenuManager`, and concrete menu subclasses in `com.skyblock.plugin.gui.menu.*` and other non-canonical packages deleted outright; canonical `com.skyblock.core.menu.Menu` confirmed as sole abstract base with all concrete menus in `com.skyblock.core.menu.*`; `com.skyblock.core.menu.MenuManager` confirmed as sole manager with no remaining orphan copies (#2764).
  Round 92 completed a thorough final-pass consolidation of all Bukkit command classes: every class implementing `CommandExecutor` or `TabCompleter` across every module audited; all 37 dead duplicate command/tab-completer files in `skyblock-core`, `core`, and `plugin` modules deleted outright (all had zero callers outside their own class); canonical command classes in `com.skyblock.core.command.*` confirmed as sole implementations with no remaining orphan copies (#2770).
  Round 93 completed: a stale-import fix sweep across all `.java` files for import references introduced by the round-92 Bukkit command-class consolidation found no stale imports (no file changes required); skills and collections module package layouts standardized — every misplaced command, listener, GUI, manager, model, and util class moved into the correct sub-package, flat-package strays eliminated, and canonical `com.skyblock.core.manager.SkillManager` and `com.skyblock.core.manager.CollectionManager` confirmed as sole implementations (#2772); dead-code deletion sweep removed 8 orphan `.java` files not caught in rounds 75–92: three `@Deprecated` `ProfileManager` delegation stubs with no callers (`core.profile`, `plugin.managers`, `profile` modules), a `DungeonsManager` delegation stub, `SkyBlockItemStack`/`SkyBlockItemManager` stubs (canonical replacements already exist), and two additional zero-caller orphan files (#2773).
  Round 94 completed package layout standardization across six modules: all Menu/GUI implementation classes in the `gui` module moved into `com.skyblock.gui.{menu,listener,model,util}` sub-packages with `@Deprecated` stubs at old locations (#2777); all Pet-related classes in the `pets` module moved into `com.skyblock.pets.{command,listener,gui,manager,model,util}` (#2778); all Auction-related classes in the `auction` module moved into `com.skyblock.auction.{command,listener,gui,manager,model}` (#2779); all Minion-related classes in the `minions` module moved into `com.skyblock.minions.{command,listener,gui,manager,model,util}` with 8 flat `com.skyblock.plugin.minion.*` files and `MinionPlacementListener` converted to `@Deprecated` stubs (#2780); all Bazaar-related classes moved into `com.skyblock.core.bazaar.{manager,command,gui}` with `@Deprecated` empty stubs at old `com.skyblock.core.{manager,command,menu}` locations and all callers updated (#2781); all Shop/NPC-shop-related classes moved into `com.skyblock.core.shop.{manager,command,listener,gui}` with `FarmerShop` migrated into `com.skyblock.plugin.shop.gui` and `@Deprecated` stubs at old locations (#2782).
  Round 95 completed: the round-94 pending shop-module standardization PR (#2782) was merged, closing the last open item from round 94 (`ShopManager` → `com.skyblock.core.shop.manager`, `ShopMenu` → `com.skyblock.core.shop.gui`, `FarmerShop` → `com.skyblock.plugin.shop.gui`, `@Deprecated` stubs at old locations); a stale-import fix sweep across every `.java` file for references introduced by the round-94 package-layout standardizations (gui #2777, pets #2778, auction #2779, minions #2780, bazaar #2781, shop #2782) identified stale references across multiple modules — remediation assigned to round-96.
  Round 96 completed: a stale-import fix sweep across every `.java` file for import references introduced by the round-94/95 package-layout standardizations; 3 stale references fixed — `com.skyblock.core.dungeon.DungeonCommand` → `com.skyblock.core.dungeon.command.DungeonCommand` and `com.skyblock.core.manager.DungeonManager` → `com.skyblock.core.dungeon.manager.DungeonManager` in `SkyBlockPlugin` (#2785).
  Round 97 completed: audit of every class in `com.skyblock.core` — 9 flat-package files replaced with `@Deprecated` stubs and 8 stale import references across 4 caller files updated to point to canonical `{domain}/manager/` and `{domain}/listener/` sub-packages (#2789).
  Round 98 completed: stale-import fix sweep across every `.java` file for import references introduced by the round-97 core-module restructuring — all stale references fixed, callers now import from canonical sub-package paths (#2787); dead-module pruning of the parent `pom.xml` — every dead or empty module entry accumulated through rounds 75–96 removed after verifying against its `src/main/java` tree (#2788).
  Round 99 completed: economy/items/combat module package-layout stub consolidations — zero-caller `com.skyblock.core.skills.command.SkillCommand` and duplicate `com.skyblock.core.skill.SkillLevelManager` deleted outright; deprecated stub `com.skyblock.core.manager.ShopManager` deleted (all callers already used `com.skyblock.core.shop.manager.ShopManager`); three deprecated stub files (`com.skyblock.core.manager.BazaarManager`, `com.skyblock.core.command.BazaarCommand`, `com.skyblock.core.menu.BazaarMenu`) deleted (all callers already used canonical `com.skyblock.core.bazaar.*` implementations); 3 stale `import com.skyblock.core.items.CustomItemManager` references updated to `import com.skyblock.core.items.manager.CustomItemManager` in `SkyBlockItemStack.java`, `SkyBlockItemManager.java`, and `SkyBlockPlugin.java` (#2792/#2793/#2794/#2795).
  Round 100 completed: PetManager/PetsManager stub consolidation — canonical `PetManager` moved to `com.skyblock.core.pets.manager`, all callers updated to the new import path, deprecated stubs at old locations removed (#2798); BankManager/BankingManager/BankHandler stub consolidation — duplicate `com.skyblock.core.manager.BankManager` and its old test deleted, `BankManagerTest` created at `com.skyblock.core.bank.manager`, no caller import updates required (#2799); MinionManager/MinionsManager/MinionHandler stub consolidation — canonical `MinionManager` confirmed at `com.skyblock.core.minion.manager`, all 13 callers migrated to the new import, old `com.skyblock.core.manager.MinionManager` replaced with a `@Deprecated` delegation stub (#2800).
  Round 101 completed: IslandManager/IslandHandler consolidation — all 7 duplicate implementations across every module consolidated into one canonical class at `com.skyblock.core.island.manager.IslandManager`, all 9 references across 7 caller files updated to the new import path, old `com.skyblock.core.manager.IslandManager` replaced with a `@Deprecated` delegation stub (#2803); AuctionHouseManager/AuctionManager/AuctionHandler audit — all 9 duplicate implementations confirmed already eliminated, canonical `com.skyblock.core.manager.AuctionHouseManager` verified as sole implementation with no remaining orphan copies, no file changes required; zero-caller `@Deprecated` stub deletion — 4 stubs with zero live callers deleted outright (`core.manager.IslandManager`, `core.manager.AbilityManager`, `core.manager.CollectionManager`, and test-dir `AuctionHouseManagerTest`) (#2804).
  Round 102 completed: stale-import fix sweep for the round-101 IslandManager consolidation and AuctionHouseManager audit — no stale imports found, no file changes required; zero-caller `@Deprecated` stub deletion — `com.skyblock.core.island.IslandGenerator` (delegated to `com.skyblock.core.island.util.IslandGenerator` with no callers) deleted outright (#2807); EnchantManager/EnchantmentManager/EnchantHandler consolidation — all duplicate implementations consolidated into one canonical class at `com.skyblock.core.enchant.manager.EnchantmentManager`, old `com.skyblock.core.manager.EnchantmentManager` and `SkyBlockEnchantListener` replaced with `@Deprecated` empty stubs, all existing callers already used canonical implementations (#2806).
  Round 103 completed: QuestManager/QuestsManager/QuestHandler consolidation — all duplicate implementations consolidated into one canonical class at `com.skyblock.core.quests.manager.QuestManager`, old stubs replaced with `@Deprecated` delegation stubs pointing to canonical (#2808); CraftingManager/RecipeManager consolidation — all duplicate implementations consolidated into one canonical class at `com.skyblock.core.manager.CraftingManager`, all callers updated to the new import path (#2756); stale-import fix sweep for the round-102 EnchantmentManager and QuestManager consolidations — stale references remediated across all modules; zero-caller `@Deprecated` stub deletion — stubs left over from the round-102 QuestManager and EnchantManager consolidations with zero live callers deleted outright.
  Round 104 completed: duplicate plugin command class consolidation — 5 plugin-level duplicate command classes (`ProfileCommand`, `IslandCommand`, `QuestCommand`, `TradingCommand`, `DungeonCommand`) deleted outright and `SkyBlockPlugin.java` updated to wire all 5 commands to their canonical rich implementations in `com.skyblock.core.<domain>.command.*` (#2813); zero-caller `@Deprecated` stub command deletion — `com.skyblock.core.dungeon.DungeonCommand` (a zero-body shell superseded by `com.skyblock.core.dungeon.command.DungeonCommand`) deleted outright (#2814).
  Round 105 completed: stale-import fix sweep for the round-104 ItemBuilder and SkullItemUtil consolidations — no stale imports found, no file changes required; zero-caller `@Deprecated` stub deletion — `ItemBuilder` duplicate stubs outside `com.skyblock.core.util` and `SkullItemUtil`/`SkullUtil`/`SkullBuilder` variant stubs in non-canonical packages deleted outright.
  Round 106 completed: Menu/AbstractMenu/GuiBuilder base class consolidation — all duplicate abstract base class implementations (`Menu`, `BaseMenu`, `InventoryMenu`, `AbstractMenu`, `GuiBuilder`) across every module collapsed into one canonical abstract at `com.skyblock.core.menu.Menu`; all concrete menu subclasses updated to `extends com.skyblock.core.menu.Menu`, zero-caller stubs deleted outright (#2817); stale-import fix sweep for the round-105 Menu/BaseMenu/GuiBuilder consolidation — no stale imports found, no file changes required.
  Round 107 completed: SkillManager delegation stub `getInstance()` fix — `com.skyblock.core.manager.SkillManager` stub had a broken `getInstance()` returning `null`; fixed to delegate correctly to `com.skyblock.core.skills.SkillManager.getInstance()` so all deprecated-stub callers resolve the singleton without NPE; Collection/CollectionType/Collections enum consolidation audit — swept every module for duplicate enum definitions, canonical `com.skyblock.core.model.Collection` confirmed as sole implementation, no file changes required; Skill/SkillType/Skills enum consolidation — all remaining duplicate `Skill`, `SkillType`, and `Skills` enum and registry-class implementations across all modules collapsed into canonical `com.skyblock.core.model.Skill`, all callers migrated, orphan copies deleted (#2819); zero-caller `@Deprecated` stub deletion for rounds 102–106 follow-up — `com.skyblock.core.enchant.SkyBlockEnchantListener`, `com.skyblock.core.menu.ShopMenu`, `com.skyblock.core.menu.DungeonMenu`, and `com.skyblock.core.manager.SkillManager` stubs deleted outright (#2820).
  Round 108 completed: Stat/StatType/SkyBlockStat/SkyBlockStats enum consolidation — all duplicate `Stat`, `StatType`, `SkyBlockStat`, and `SkyBlockStats` enum and registry-class implementations across every module collapsed into one canonical enum at `com.skyblock.core.model.Stat`; all callers migrated, orphan copies deleted (#2822); BazaarHandler/BazaarManager duplicate consolidation — all remaining duplicate `BazaarHandler` and `BazaarManager` implementations audited, `@Deprecated` delegation stubs placed at old locations pointing to canonical `com.skyblock.core.bazaar.manager.BazaarManager`, all callers already on the canonical import path — no import changes required (#2823); NpcShopManager consolidation — all duplicate `NpcShopManager` implementations across all modules consolidated into one canonical class at `com.skyblock.core.shop.manager.NpcShopManager`, old stubs replaced with `@Deprecated` delegation stubs, all callers updated to new import path (#2824); CollectionManager consolidation — all remaining duplicate `CollectionManager` and `CollectionsManager` implementations across all modules consolidated into one canonical class at `com.skyblock.core.manager.CollectionManager`, `@Deprecated` delegation stubs placed at all old locations, all callers migrated to canonical import path (#2825).
  Round 109 completed: SkillManager/SkillsManager consolidation — all duplicate `SkillManager` and `SkillsManager` implementations across all modules consolidated into one canonical class at `com.skyblock.core.manager.SkillManager`; `@Deprecated` delegation stub placed at old `com.skyblock.core.skills.manager.SkillManager` path; all 25 callers updated to import from the canonical package (#2825); PetManager/PetsManager consolidation audit — every class named `PetManager` or `PetsManager` across every module audited; canonical `com.skyblock.core.pets.manager.PetManager` confirmed as sole implementation with all callers already on the canonical import path — no file changes required.
  Round 110 completed: MinionManager `@Deprecated` stub deletion — all 9 zero-caller `@Deprecated` stub files left over from the round-110 MinionManager consolidation deleted outright (`MinionTickScheduler`, `MinionTickTask`, `MinionInventoryMenu`, `Minion`, `CobblestoneMinion`, and 4 additional stub classes); canonical `com.skyblock.core.minion.manager.MinionManager` confirmed as sole implementation with no remaining orphan stubs (#2828).
  Round 111 completed: BankManager/BankingManager/BankHandler consolidation and stub deletion — all `BankManager`/`BankingManager`/`BankHandler` duplicate implementations across all modules consolidated into one canonical class at `com.skyblock.core.manager.BankManager`; `BankType` and `BankTier` enum fields (`displayName`, `interestRate`, `isShared()`) restored to the delegation stub to match canonical values so all stub callers compile correctly; sole zero-caller stub (`com.skyblock.core.bank.manager.BankManager`) deleted outright after the enum fix; canonical `com.skyblock.core.manager.BankManager` confirmed as sole implementation with no remaining orphan stubs (#2829 / #2830).
  Round 112 completed: gui module sub-package layout standardization — all `.java` files in the `gui` module moved into canonical sub-packages (`com.skyblock.gui.{menu,util,listener,builder}`); flat-package strays eliminated; all callers updated to new import paths (#2836).
  Round 113 completed: pets module sub-package layout standardization — all `.java` files in the `pets` module moved into canonical sub-packages (`com.skyblock.pets.{command,listener,gui,manager,model}`); flat-package strays eliminated; all callers updated to new import paths (#2835).
  Round 114 completed: skills module sub-package layout standardization — all `.java` files currently in flat or misplaced packages under the `skills` module moved into canonical sub-packages (`com.skyblock.skills.{command,listener,gui,manager,model,util}`); flat-package strays eliminated; all callers updated to new import paths (#2834).
  Round 115 completed: EnchantType empty stub deletion — zero-constant, zero-caller `com.skyblock.enchanting.EnchantType` stub left over from the enchanting module standardization deleted outright; canonical `com.skyblock.enchanting.model.EnchantType` confirmed as sole implementation (#2841); /skills command executor binding verified — `SkyBlockPlugin` already correctly registers `SkillsCommand` as the executor for the `/skills` Bukkit command, no file changes required; duplicate event-listener consolidation — all redundant `PlayerJoinListener`, `PlayerQuitListener`, and `PlayerMoveListener` implementations across every module consolidated; `ProfileJoinListener`, `PlayerJoinSetupListener`, and `PlayerJoinQuitListener` deleted outright with all registrations removed from `SkyBlockPlugin` — `ProfileManager` handles all join/quit lifecycle logic canonically (#2842).
  Round 116 completed: hub-menu consolidation — all duplicate SkyBlock main hub-menu implementations across every module found and consolidated; `SkyblockHubCommand` confirmed as sole canonical hub-menu entry point; duplicate `HubMenu`/`SkyBlockMainMenu` implementations deleted outright or replaced with `@Deprecated` delegation stubs; all registrations in `SkyBlockPlugin` updated (#2846); async player-data load/save fix — missing `onQuit` handler added to `com.skyblock.plugin.profile.PlayerDataManager` that builds a YAML snapshot on the main thread (safe, no I/O), evicts the `core.manager.PlayerDataManager` cache entry, then writes to disk asynchronously; player data now correctly persisted on disconnect (#2845).
  Round 117 completed: items module class relocation — `ItemBuilder` and `SkullItemUtil` moved from flat `com.skyblock.core.util` into `com.skyblock.items.util`; originals at old locations replaced with `@Deprecated` forwarding stubs; `skyblock-items` dependency wired into `pom.xml` for caller modules; combat module package standardization completion — all combat module `.java` files confirmed in correct sub-packages (`calculator`, `manager`, `model`); deprecated `engine/CombatEngine.java` forwarding stub (zero callers) deleted outright; canonical sub-package layout fully enforced (#2849).
  Round 118 completed: ShopManager/NpcShopManager consolidation — `NpcManager.ShopItem` inner record consolidated into `ShopManager.ShopEntry`; `ShopItem` record and `findItem()` method deleted; `withdraw(UUID, long)` overload confirmed compatible with `entry.buyPrice()` returning `long`; all remaining duplicate `ShopManager`/`NpcShopManager` implementations across every module eliminated; canonical `com.skyblock.core.manager.ShopManager` confirmed as sole implementation (#2853). BazaarManager/BazaarHandler consolidation — all duplicate implementations across every module consolidated into one canonical class; all callers updated to import from `com.skyblock.core.manager.BazaarManager`; `@Deprecated` stub placed at old `com.skyblock.core.bazaar.manager` location (#2854). ItemBuilder/SkullItemUtil `@Deprecated` forwarding stub deletion — both stubs left in `com.skyblock.core.util` by the round-117 relocation deleted outright; all 36 callers confirmed migrated to `com.skyblock.items.util.*` (#2852). Items module class relocation completion — all remaining `.java` files in flat or misplaced packages under `skyblock-items` relocated into canonical sub-packages beyond the initial `ItemBuilder`/`SkullItemUtil` move; flat-package strays eliminated (#2848). Core module internal package standardization — full audit of `com.skyblock.core` completed; every misplaced manager, listener, command, and GUI class moved into canonical sub-packages; flat-package strays eliminated (#2851).
  Round 119 completed: SkillManager/SkillsManager consolidation — all duplicate `SkillManager` and `SkillsManager` implementations across every module consolidated into one canonical class at `com.skyblock.core.skills.manager.SkillManager`; `@Deprecated` stub placed at old `com.skyblock.core.manager.SkillManager` location; all callers migrated to canonical import path (#2860).
  Round 120 completed: zero-caller `@Deprecated` stub deletion — `SkillManager` stub at `com.skyblock.core.skills.manager` left by the round-119 consolidation deleted outright (#2861); `CollectionManager` stub at `com.skyblock.core.collections.manager` and `PetManager` stub at `com.skyblock.pets.manager` both deleted outright after confirming zero live callers (#2862).
  Round 121 completed: AuctionHouseManager consolidation — grepped every module for classes named `AuctionHouseManager` or `AuctionManager`; identified `com.skyblock.core.manager.AuctionHouseManager` as the canonical sole survivor; `@Deprecated` forwarding stub at `com.skyblock.core.auction.manager.AuctionHouseManager` deleted outright after confirming zero non-stub callers remain across the entire repo (#2863 / #2865).
  Round 123 completed: abstract Menu/InventoryGUI/BaseMenu base class audit — grepped every module for all abstract classes named `Menu`, `InventoryGUI`, `BaseMenu`, `AbstractMenu`, or any variant; confirmed exactly one abstract base class exists in the entire repo at `com.skyblock.core.menu.Menu`; all concrete menu subclasses across every module already extend the single canonical base; no InventoryGUI, BaseMenu, or AbstractMenu duplicates remain — no file changes required.
  Round 122 completed: MinionManager `@Deprecated` stub deletion — stub at `com.skyblock.core.minion.manager` deleted outright after confirming zero non-stub callers; now-empty `manager` directory removed; two stale `{@link}` references in `CobblestoneMinion.java` updated to canonical class (#2870); IslandManager `@Deprecated` stub audit (round-121 follow-up) — grepped all modules for remaining IslandManager stubs, confirmed zero stubs or non-stub callers at old locations, no file changes required; ProfileManager stale-import fix — three stale `com.skyblock.core.manager.ProfileManager` import references in `SkyBlockPlugin.java` corrected to the canonical `com.skyblock.core.profile.manager.ProfileManager` (#2871).
  Round 123 completed: Skill/Stat/Rarity/Collection enum duplicate consolidation — grepped every module for duplicate `Skill`, `Stat`, `Rarity`, and `Collection` enum definitions; found and removed `FishRarity` and `RabbitRarity` duplicate enum definitions in `FishingManager`, `FishingCommand`, and `ChocolateFactory`-related files; replaced all 4 affected files with canonical `com.skyblock.core.model.Rarity` enum; orphaned `FishRarity` and `RabbitRarity` classes deleted outright; canonical `com.skyblock.core.model.{Skill,Stat,Rarity,Collection}` enums confirmed as sole definitions across all modules (#2875).
  Round 124 completed: ShopMenu/NpcShopMenu/ShopGUI menu consolidation — grepped every module for classes named `ShopMenu`, `NpcShopMenu`, or `ShopGUI`; identified `com.skyblock.gui.menu.ShopMenu` as the canonical sole survivor; all duplicate implementations across every module consolidated into the canonical class; `@Deprecated` stubs at old locations deleted outright after confirming zero non-stub callers remain across the entire repo (#2874).
  Round 125 completed: CollectionsMenu canonical implementation — grepped every module for classes named `CollectionsMenu`, `CollectionMenu`, or `CollectionGUI`; wrote canonical implementation at `com.skyblock.core.menu.CollectionsMenu` with `fillBorder()` helper covering top/bottom rows and left/right column borders, `event.setCancelled(true)` click handlers on all five category icon slots matching the `SkillsMenu` pattern, and `CollectionsMenuTest` mirroring the `SkillsMenuTest` structure; all duplicate implementations consolidated into the canonical class. BankMenu canonical implementation — wrote canonical `BankMenu` at `com.skyblock.core.menu.BankMenu`; confirmed zero duplicate `BankMenu`/`BankingMenu`/`BankGUI` implementations across the entire repo.
  Round 128 completed: PetManager test relocation — `PetManagerTest` relocated from `pets/src/test/java/com/skyblock/pets/manager/PetManagerTest.java` to `skyblock-core/src/test/java/com/skyblock/core/manager/PetManagerTest.java`; package declaration updated from `com.skyblock.pets.manager` to `com.skyblock.core.manager`; fully-qualified `com.skyblock.core.manager.PetManager` references simplified to simple name `PetManager` now that the test lives in the same package as the canonical class (#2891).
  Round 129 completed: BazaarManager/BazaarHandler consolidation audit — grepped every module for classes named `BazaarManager` or `BazaarHandler`; confirmed canonical `com.skyblock.core.manager.BazaarManager` as sole implementation with no duplicate implementations remaining; `@Deprecated` delegation stubs identified for deletion by Forge in round 130 — no file changes required.
  Round 130 completed: SkillManager/SkillsManager consolidation audit — grepped every module for classes named `SkillManager` or `SkillsManager`; confirmed canonical `com.skyblock.core.skills.manager.SkillManager` as sole implementation with no duplicate implementations remaining — no file changes required.
  Round 132 completed: command class consolidation — grepped every module for classes implementing `CommandExecutor` or `TabCompleter`; all remaining duplicate command implementations across every module consolidated; canonical command classes in `com.skyblock.core.<domain>.command.*` confirmed as sole implementations (#2899); 7 `@Deprecated` `plugin.commands` stub classes (MayorCommand, KuudraCommand, SlayerCommand, HOTMCommand, GardenCommand, FishingCommand, and 1 additional) deleted from `com.skyblock.plugin.commands.*` with all 7 imports in `SkyBlockPlugin.java` updated to point to canonical `com.skyblock.core.*` command classes with manager instances injected via constructor; `HubCommand.java` retained (not in deletion scope) (Vega / #2899).
  Round 133 completed: IslandManager/SkyBlockIslandManager/PlayerIslandManager consolidation — grepped every module for classes named `IslandManager`, `SkyBlockIslandManager`, or `PlayerIslandManager`; all remaining duplicate implementations consolidated into one canonical class at `com.skyblock.core.island.manager.IslandManager`; all callers and `SkyBlockPlugin` registrations updated to the canonical import path; orphaned duplicate copies deleted outright (#2905).
  Round 134 completed: combat module package layout standardization — audited all source files in the combat module; all misplaced classes moved into canonical sub-packages (`calculator`, `manager`, `model`); `CombatEngine` and `DamageCalculator` confirmed present at `com.skyblock.core.combat.calculator`; `CombatEngineTest` and `DamageCalculatorTest` added at `com.skyblock.core.combat.calculator` proving canonical classes are present and functional; `DamageType.TRUE` confirmed with `reducedByDefense=false` (#2910).
  Round 135 completed: items and enchantments module package layout standardization — audited all source files in `com.skyblock.items` and `com.skyblock.enchantments`; `com.skyblock.core.util.ItemBuilder` established as canonical location (moved from the items module); all 35 callers updated to the new import; `items.util.ItemBuilder`, `items.model.SkyBlockItem`, and `items.manager.*` stubs deleted; enchantments module classes reorganized into correct `.manager`/`.model`/`.command`/`.listener` sub-packages; flat-package strays eliminated (Vega / #2911).
  Round 136 completed: auction module package layout standardization — canonical `com.skyblock.core.auction` package layout established with `command/AuctionHouseCommand` (pre-existing), `manager/AuctionHouseManager`, and `gui/AuctionHouseMenu`; all callers updated to the new import paths; old locations replaced with `@Deprecated` forwarding stubs; flat-package strays eliminated (Forge / #2916); profile and player module package layout standardization — `plugin.profile.PlayerDataManager` corrected to reference `com.skyblock.core.player.manager.PlayerDataManager` (6 occurrences fixed — previously pointed at empty deprecated stub at `com.skyblock.core.manager`); all callers migrated to canonical import path (Vega / #2915).
  Round 137 completed: dead-module pruning post-rounds 127–136 — every `<module>` entry in the root `pom.xml` enumerated; all dead/empty modules accumulated through rounds 127–136 removed from the build (#2918); quests module package layout standardization — all source files in `com.skyblock.quests` / `com.skyblock.core.quests` audited; `QuestsMenu` moved to canonical `com.skyblock.core.quest.gui.QuestsMenu` co-located with `command/QuestCommand` and `manager/QuestManager`; `SkyBlockMainMenu` updated to import the new location; old flat-package stub removed (#2919); slayer and dungeon module package layout standardization — canonical `SlayerCommand` placed in `command/` and `SlayerManager` in `manager/`; dungeon module reorganized into `.command`/`.manager`/`.model` sub-packages; flat-package strays eliminated (#2920); fishing and farming module package layout standardization — `command/FishingCommand` and `manager/{FishingManager,TrophyFishManager,TrophyFishingManager}` placed in canonical sub-packages; farming module classes organized into `.command`/`.manager`/`.listener` sub-packages; `@Deprecated` annotations applied to all old locations; flat-package strays eliminated (#2921).
  Round 139 completed: menu/listener dedup completion — grepped every module for remaining duplicate menu (`*Menu`/`*GUI`) and event-listener (`*Listener`) implementations; all surviving duplicates consolidated into their canonical `com.skyblock.core.menu.*` / `com.skyblock.core.listener.*` locations; old locations replaced with `@Deprecated` forwarding stubs or deleted outright where zero callers remained; all callers and `SkyBlockPlugin` registrations confirmed on the canonical import paths (Vega); accessories/talisman module package layout standardization — `AccessoryManager` moved from the root package into the canonical `com.skyblock.accessories.manager` sub-package; no external references required updating (Forge); EconomyManager/CurrencyManager consolidation audit — canonical `com.skyblock.core.economy.manager.EconomyManager` confirmed as sole implementation with all callers on the canonical import path, no file changes required (Forge); NetworthManager/NetworthCalculator consolidation audit — canonical `NetworthManager` confirmed as sole implementation, no file changes required.
  Round 140 completed: NetworthManager/NetworthCalculator final consolidation — grepped every module for classes named `NetworthManager` or `NetworthCalculator`; canonical `com.skyblock.core.networth.NetworthManager` confirmed as sole implementation with all callers on the canonical import path; no remaining duplicate implementations or `@Deprecated` stubs found — no file changes required; EconomyManager/CurrencyManager final consolidation — grepped every module for classes named `EconomyManager` or `CurrencyManager`; canonical `com.skyblock.core.economy.manager.EconomyManager` confirmed as sole implementation with all callers on the canonical import path; no remaining duplicate implementations or `@Deprecated` stubs found — no file changes required.
  Round 141 completed: AuctionHouseManager consolidation — grepped every module for classes named `AuctionHouseManager` or `AuctionManager`; all remaining duplicate implementations consolidated into one canonical class at `com.skyblock.core.manager.AuctionHouseManager`; all callers updated to the canonical import path; `@Deprecated` stubs at old locations deleted outright after confirming zero non-stub callers remain (Forge); CollectionManager consolidation — grepped every module for classes named `CollectionManager`; all remaining duplicate implementations consolidated into one canonical class at `com.skyblock.core.manager.CollectionManager`; all callers updated to the canonical import path; `@Deprecated` stubs at old locations deleted outright after confirming zero non-stub callers remain (Oracle).
  Round 142 completed: AuctionHouseManager deprecated-shell deletion — the empty `@Deprecated`, memberless, reference-free shell `com.skyblock.core.manager.AuctionHouseManager` left over from the round-141 consolidation deleted outright after confirming zero non-stub callers remain; the live canonical implementation lives at `com.skyblock.core.auction.manager.AuctionHouseManager` (Forge / #2933).
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
