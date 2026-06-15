# Module Consolidation Tracker

Tracks every duplicate-class consolidation. Canonical home is always `skyblock-core`
(`com.skyblock.core.*`). See `STATUS.md` for the full duplicate inventory and
`ROADMAP.md` Phase 5 for the overall consolidation goal.

**Legend:** ✅ Done · 🔄 In progress · ⏳ Pending

---

## Completed

| Domain | Canonical class | Duplicates removed | PR / commit |
|--------|-----------------|-------------------|-------------|
| Auction House Manager | `com.skyblock.core.auction.AuctionHouseManager` | 9 → 1 (`auction`, `auctions`, `auctionhouse`, `auction_house`, `core` ×2, `economy`, `plugin` ×2) | 9f504bba / #2543 / #2593 |
| Rarity enum | `com.skyblock.core.model.Rarity` | 3 → 1 (`items/Rarity`, `items/ItemRarity`, `core/RarityType`) | 2e713bba |
| Item builder / SkullUtil | `com.skyblock.core.util.ItemBuilder`, `com.skyblock.core.util.SkullItemUtil` | 4 → 1 ItemBuilder (`plugin.gui`, `plugin.item`, `plugin.items`, `core.item`); SkullUtil stubs already in place | #2549 / 01eebf00 |
| Collection / CollectionCategory enum | `com.skyblock.core.model.Collection`, `com.skyblock.core.model.CollectionCategory`, `com.skyblock.core.util.CollectionRegistry` | 4+ variants across `collection`, `collections`, `core`, `plugin` | Sentinel |
| Menu abstract base | `com.skyblock.core.menu.Menu` | 3 → 1 (`MenuManager.SkyBlockMenu` inner class, `plugin.gui.Menu`, `core` variant) | Vega |
| SkillManager / SkillsManager | `com.skyblock.core.skills.SkillManager` | 8 → 1 (`skills.SkillManager`, `skills.SkillsManager`, `core.skills.SkillsManager`, `core.skill.SkillManager`, `plugin.skills.SkillManager`, `plugin.skills.SkillsManager`, `plugin.managers.SkillsManager`, `plugin.manager.SkillManager`) | #2545 |

---

## Completed (continued)

| Domain | Canonical class | Duplicates removed | PR / commit |
|--------|-----------------|-------------------|-------------|
| CollectionManager | `com.skyblock.core.manager.CollectionManager` | 3 skyblock-core duplicates → `@Deprecated` stubs; 5 command classes and 2 plugin entry points updated to use canonical + `com.skyblock.core.model.Collection` | #2550 |
| EnchantmentManager | `com.skyblock.core.manager.EnchantmentManager` | All `EnchantmentManager`/`EnchantManager` duplicates → 1 canonical; remaining variants replaced with `@Deprecated` stubs delegating to canonical | #2512 / 64852fe2 |
| CraftingManager / RecipeManager | `com.skyblock.core.crafting.CraftingManager` | 3 → 1 (`plugin` stub, `core` stub, `core.crafting` survivor); added `registerRecipes(JavaPlugin)` from plugin stub; all callers updated | #2510 / 3c333a9e |
| Dead-module pruning (SkillType / SkillXPTable) | `com.skyblock.core.skills.SkillManager.SkillType` | Migrated all callers off deprecated `com.skyblock.skills.SkillType` and `com.skyblock.skills.SkillXPTable`; deprecated re-export variants removed | #2511 / c8fa794d |
| QuestManager / QuestsManager | `com.skyblock.core.manager.QuestManager` | All `QuestManager`/`QuestsManager` duplicates → 1 canonical; remaining variants replaced with `@Deprecated` stubs delegating to canonical | #2513 |
| EconomyManager / CoinManager / MoneyManager / PurseManager | `com.skyblock.core.manager.EconomyManager` | All `EconomyManager`/`CoinManager`/`MoneyManager`/`PurseManager` duplicates → 1 canonical; remaining variants replaced with `@Deprecated` stubs delegating to canonical | #2514 |
| AbilityManager / AbilityHandler / SpecialAbilityManager | `com.skyblock.core.manager.AbilityManager` | All `AbilityManager`/`AbilityHandler`/`SpecialAbilityManager` duplicates → 1 canonical; remaining variants replaced with `@Deprecated` stubs delegating to canonical | #2515 |
| DungeonManager / DungeonsManager | `com.skyblock.core.manager.DungeonManager` | 7 duplicates → 1 canonical (896 lines, merging all APIs); all 6 `DungeonManager` duplicates and `DungeonsManager` replaced with `@Deprecated` delegating stubs | #2517 |
| Stat / StatType / PlayerStat / CombatStat | `com.skyblock.core.stat.Stat` | 6 duplicates → 1 canonical (26 constants, full metadata); `PlayerStat`, `plugin.items.StatType`, `combat.CombatStat`, `core.combat.StatManager.CombatStat`, `core.stats.StatsManager.StatType`, `core.stat.StatManager.StatType` replaced with `@Deprecated` stubs or removed; 20+ caller files migrated | In progress |
| BazaarManager / BazaarHandler | `com.skyblock.core.manager.BazaarManager` | All `BazaarManager`/`BazaarHandler` duplicates → 1 canonical; ~7 variants replaced with `@Deprecated` stubs delegating to canonical; standalone `bazaar` module richest impl preserved | #2537 / #2541 / #2628 |
| BankManager / BankingManager / BankHandler | `com.skyblock.core.manager.BankManager` | All `BankManager`/`BankingManager`/`BankHandler` duplicates → 1 canonical; ~7 variants replaced with `@Deprecated` stubs delegating to canonical; `economy` module most complete impl preserved | #2538 |
| IslandManager / IslandHandler / IslandService | `com.skyblock.core.manager.IslandManager` | All `IslandManager`/`IslandHandler`/`IslandService` duplicates → 1 canonical; ~7 variants replaced with `@Deprecated` stubs delegating to canonical; `islands` module most complete impl preserved | #2539 / #2591 |
| MinionManager / MinionsManager | `com.skyblock.core.manager.MinionManager` | All `MinionManager`/`MinionsManager` duplicates → 1 canonical; ~9 variants replaced with `@Deprecated` stubs delegating to canonical; largest duplication surface resolved | #2540 / #2590 |
| PetManager / PetsManager | `com.skyblock.core.manager.PetManager` | All `PetManager`/`PetsManager` duplicates → 1 canonical; variants replaced with `@Deprecated` stubs delegating to canonical; `pets` module most complete impl preserved | #2592 |
| ShopManager / NpcShopManager / ShopHandler | `com.skyblock.core.manager.ShopManager` | All `ShopManager`/`NpcShopManager`/`ShopHandler` duplicates → 1 canonical; variants replaced with `@Deprecated` stubs delegating to canonical | #2544 |
| ProfileManager / PlayerProfileManager | `com.skyblock.core.manager.ProfileManager` | ~7 duplicates across `profiles`, `profile`, `playerdata`, `core`, `plugin` → 1 canonical; all variants replaced with `@Deprecated` stubs delegating to canonical | #2547 |
| Dead-module pruning (`auction`, `dungeon`) | *(removed from `pom.xml`)* | Both modules contained only `@Deprecated` delegation stubs with no unique logic; removed from parent `pom.xml` to eliminate dead build surface | #2548 |
| Dead-module pruning (`stats`, `minion`) | *(removed from `pom.xml`)* | Both modules contained only `@Deprecated` stub classes (`StatsManager`, `PlayerStat`, `MinionManager`) with zero callers outside their own module directory; removed from parent `pom.xml` | #2554 |
| Zero-caller `@Deprecated` stub removal | *(class files deleted)* | 16 `@Deprecated` stub files with zero live callers deleted outright; 11 stubs with live callers retained as delegating bridges | #2553 |

---

## Completed (Menu GUI consolidations — rounds 20-24)

| Domain | Canonical class | Duplicates removed | PR / commit |
|--------|-----------------|-------------------|-------------|
| CollectionsMenu / CollectionMenu / CollectionGui | `com.skyblock.core.menu.CollectionsMenu` | 3+ duplicates → 1 canonical (54-slot chest, paginated collection categories) | #2527 |
| AuctionHouseMenu / AuctionMenu / BidMenu / AuctionGui | `com.skyblock.core.menu.AuctionHouseMenu` | 3+ duplicates → 1 canonical (54-slot chest, category filter row, 28 listing slots, gray-pane separators) | #2528 / #2566 |
| CraftingMenu / CraftingGui / CraftingTable | `com.skyblock.core.menu.CraftingMenu` | 3+ duplicates → 1 canonical (54-slot chest, 3×3 crafting grid, recipe browser) | #2529 |
| BazaarMenu / BazaarGui / ShopMenu (bazaar) | `com.skyblock.core.menu.BazaarMenu` | 3+ duplicates → 1 canonical (54-slot chest, buy/sell tabs backed by `BazaarManager`) | #2530 |
| EnchantingMenu / EnchantmentMenu / EnchantGui | `com.skyblock.core.menu.EnchantingMenu` | 3+ duplicates → 1 canonical (54-slot chest, enchantment book slots backed by `EnchantmentManager`) | #2531 |
| BankMenu / BankingMenu / BankGui | `com.skyblock.core.menu.BankMenu` | 3+ duplicates → 1 canonical (54-slot chest, purse/bank display, Deposit All, Withdraw All backed by `EconomyManager`); InventoryHolder removed from legacy stubs (#2536) | #2532 / #2536 |
| MinionMenu / MinionsMenu / MinionGui | `com.skyblock.core.menu.MinionMenu` | 3+ duplicates → 1 canonical (54-slot chest, paginated minion list backed by `MinionManager`) | #2533 |
| DungeonMenu / DungeonsMenu / DungeonGui | `com.skyblock.core.menu.DungeonMenu` | 3+ duplicates → 1 canonical (54-slot chest, floor selection backed by `DungeonManager`) | #2534 |
| IslandMenu / IslandGui / IslandMainMenu | `com.skyblock.core.menu.IslandMenu` | 3+ duplicates → 1 canonical (54-slot chest, info + 8 upgrade slots, members, history, close) | #2535 |
| SkillsMenu / SkillMenu / SkillGui / SkillsGui | `com.skyblock.core.menu.SkillsMenu` | 4+ duplicates → 1 canonical (54-slot chest, skull icon per skill backed by `SkillManager`; re-consolidated to fix stubs) | #2523 / #2563 |
| PetsMenu / PetMenu / PetGui / PetsGui | `com.skyblock.core.menu.PetsMenu` | 4+ duplicates → 1 canonical (paginated 54-slot chest, equip/unequip toggle, XP display backed by `PetManager`; re-consolidated to fix stubs) | #2522 / #2565 |
| QuestsMenu / QuestMenu / QuestGui / QuestsGui | `com.skyblock.core.menu.QuestsMenu` | 4+ duplicates → 1 canonical (54-slot chest, active/completed quest display backed by `QuestManager`; re-consolidated to fix stubs) | #2524 / #2567 |

---

## Completed (Command class consolidations — round 25)

| Domain | Canonical class | Stubs deprecated | PR / commit |
|--------|-----------------|-----------------|-------------|
| `plugin.command.*` sub-package commands (CollectionsCommand, AuctionHouseCommand, EnchantingCommand, HOTMCommand, HotmCommand, SkillsCommand, PetsCommand, SlayerCommand, IslandCommand, BankCommand, KuudraCommand, BazaarCommand, MayorCommand) | `com.skyblock.plugin.commands.*` | 13 `@Deprecated` stubs in `plugin.command.*` sub-packages delegating to `plugin.commands.*` canonical implementations | #2574 |
| `plugin.commands.*` reverse stubs (DungeonCommand, FairyCommand) | `com.skyblock.plugin.command.dungeon.*`, `com.skyblock.plugin.command.fairy.*` | 2 `@Deprecated` stubs in `plugin.commands.*` delegating to canonical `plugin.command.*` sub-packages | #2574 |
| `core.command.*` prefix-package duplicates (QuestCommand, AuctionCommand, SkyBlockMenuCommand, IslandCommand, SkyBlockCommand, CollectionCommand) | Domain sub-packages (`com.skyblock.core.quest`, `.auction`, `.island`, `.collection`, `.hub`) | 6 `@Deprecated` stubs in `core.command.*` delegating to canonical per-domain sub-packages | #2574 |
| `core.commands.*` prefix-package duplicates (SkyBlockCommand, IslandCommand) | Domain sub-packages (`com.skyblock.core.hub`, `.island`) | 2 `@Deprecated` stubs in `core.commands.*` delegating to canonical per-domain sub-packages | #2574 |
| Command registration fix (`SkyBlockPlugin.java`) | `com.skyblock.plugin.command.menu.SkyblockMenuCommand` | Removed erroneous `@Deprecated` `plugin.menu.SkyblockMenuCommand` import; wired canonical `SkyBlockMenuManager` | #2575 |

---

## Completed (Listener and handler class consolidations — rounds 26-28)

| Domain | Canonical class/package | Duplicates removed | PR / commit |
|--------|------------------------|-------------------|-------------|
| plugin.collection vs plugin.collections listener packages | `com.skyblock.plugin.collection.*` | `plugin.collections` listener stubs deprecated pointing to `plugin.collection` | #2579 |
| plugin.skill vs plugin.skills listener packages | `com.skyblock.plugin.skill.*` | `plugin.skills` listener stubs deprecated pointing to `plugin.skill` | #2580 |
| plugin.minion vs plugin.minions listener packages | `com.skyblock.plugin.minion.*` (`MinionListener`, `MinionPlacementListener`, `CobblestoneMinion`) | `plugin.minions` listener/utility stubs deprecated pointing to `plugin.minion` counterparts | #2581 |
| Remaining duplicate listener sweep | *(canonical per-domain packages)* | `plugin/listeners/` directory emptied; all stray listener duplicates deprecated or deleted outright | #2578 |

---

## Completed (utility class, enum, and dead-code consolidations — rounds 29-33)

| Domain | Canonical class | Duplicates removed | PR / commit |
|--------|-----------------|-------------------|-------------|
| ItemBuilder (deep sweep) | `com.skyblock.core.util.ItemBuilder` | All remaining `ItemBuilder`/`ItemStackBuilder` duplicates across all modules eliminated; every caller imports canonical | #2596 / fa614b2d |
| SkullItemUtil / SkullCreator / SkullBuilder | `com.skyblock.core.util.SkullItemUtil` | All `SkullCreator`/`SkullBuilder`/`SkullItemUtil` duplicates eliminated; zero-caller `@Deprecated` `plugin.util.SkullItemUtil` stub deleted outright | #2597 / df70948d |
| Skill / Stat / Rarity / Collection enums (deep sweep) | `com.skyblock.core.skills.Skill`, `com.skyblock.core.stat.Stat`, `com.skyblock.core.model.Rarity`, `com.skyblock.core.model.Collection` | All duplicate enum/registry definitions across modules collapsed into single canonical enums; remaining stubs deleted | #2598 / ba91d57e |
| Dead-module pruning (pom.xml sweep) | *(removed from `pom.xml`)* | Every `<module>` entry whose `src/main/java` tree is empty or contains only `@Deprecated` zero-caller stubs removed from root `pom.xml` (e.g. `skills` module) | #2599 / 286a90f7 |
| Zero-caller `@Deprecated` stub sweep (`plugin.util` / `plugin.utils` / `plugin.helper` / `plugin.common` / `plugin.managers` / `plugin.manager`) | *(class files deleted)* | 4 zero-caller `@Deprecated` stubs deleted (`plugin.managers.AuctionHouseManager`, `plugin.managers.CraftingManager`, `plugin.managers.SkillsManager`, `plugin.manager.SkillManager`); 3 stale `{@link}` javadoc references updated | #2600 / d6c14e37 |

---

## Completed (dead-code sweeps, config/persistence consolidations — rounds 34-36)

| Domain | Canonical class | Duplicates removed | PR / commit |
|--------|-----------------|-------------------|-------------|
| Persistence / DataManager | `com.skyblock.core.persistence.DataManager` | All `DataManager`/save/load/query duplicates across modules → 1 canonical; all variants replaced with `@Deprecated` stubs delegating to canonical | #2606 |
| Config-loader (ConfigManager / SkyBlockConfig / PluginConfig) | `com.skyblock.core.config.ConfigManager` | `SkillsConfig`, `SkillXPConfig`, `SkillLevelUpManager` zero-caller stubs deleted; all remaining config-loader variants consolidated to canonical | #2607 |
| Zero-caller `@Deprecated` stub sweep (`plugin.items` / `plugin.combat` / `plugin.enchantment` / `plugin.model` / `plugin.data`) | *(class files deleted)* | Zero-caller `@Deprecated` stubs deleted across `plugin.items`, `plugin.combat`, `plugin.enchantment`, `plugin.model`, and `plugin.data` packages | #2608 |
| Duplicate listener sweep (`plugin.items` / `plugin.combat` / `plugin.enchantment` / `plugin.world`) | *(canonical per-domain packages)* | 4 zero-caller `@Deprecated` listener stubs deleted (`CollectionListener`, `CollectionTracker`, `CollectionTrackingListener`, `CollectionsListener`); 3 remaining listener duplicates deprecated pointing to canonical counterparts | #2609 |
| `plugin.combat` listener stubs (`DamageListener` / `CombatDamageListener` / `CombatListener`) | `com.skyblock.plugin.listener.CombatListener` | `DamageListener`, `CombatDamageListener`, and `CombatListener` deleted from `plugin.combat`; duplicate `plugin.combat.CombatListener` registration removed from `SkyBlockPlugin` (line 155) | #2610 |

---

## Completed (Menu/GUI base class consolidations — rounds 37-38)

| Domain | Canonical class | Duplicates removed | PR / commit |
|--------|-----------------|-------------------|-------------|
| SkyBlockMenu / SkyBlockMainMenu / plugin.gui.Menu (base class) | `com.skyblock.core.menu.Menu` | `plugin.menu.SkyBlockMenu` and `plugin.gui.menu.SkyBlockMainMenu` deleted (zero-caller deprecated stubs); `plugin.gui.Menu` collapsed into `@Deprecated` thin wrapper extending canonical; stale listener registration removed from `SkyBlockPlugin` | #2613 |

---

## Completed (BazaarManager/BazaarHandler stub fixes and zero-caller deletions — round 39)

| Domain | Canonical class | Duplicates removed | PR / commit |
|--------|-----------------|-------------------|-------------|
| BazaarManager / BazaarHandler remaining stubs | `com.skyblock.core.manager.BazaarManager` | 3 zero-caller stubs deleted outright: empty `com.skyblock.core.bazaar.BazaarProduct` placeholder, duplicate `com.skyblock.core.bazaar.BazaarCommand` (superseded by `com.skyblock.core.command.BazaarCommand`), and dead `BazaarProduct` re-export; all remaining `@Deprecated` stubs given delegating constructors or static-factory redirects | #2615 |

---

## Completed (zero-caller stub sweeps and sub-package layout enforcement — round 40)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| Zero-caller stub sweep (`plugin.gui` / `plugin.menu` / `plugin.world` / `plugin.event`) | *(class files deleted)* | Remaining `@Deprecated` zero-caller stub classes in `plugin.gui`, `plugin.menu`, `plugin.world`, and `plugin.event` packages deleted outright | #2617 |
| Sub-package layout enforcement — items and combat modules | `com.skyblock.core.items.command.*`, `com.skyblock.core.combat.command.*` (and `listener.*`) | All command and listener classes outside their canonical `command`/`listener` sub-packages moved or deprecated into proper sub-packages; zero-caller strays deleted | #2618 |
| ShopManager / NpcShopManager zero-caller stub deletion | `com.skyblock.core.manager.ShopManager` | `com.skyblock.core.shop.ShopManager` and `com.skyblock.core.shop.ShopCommand` zero-caller deprecated stubs deleted; `com.skyblock.core.command.ShopCommand` is now the sole command entry | #2619 |
| Sub-package layout enforcement — skills module | `com.skyblock.core.skills.command.SkillCommand`, `com.skyblock.core.skills.listener.SkillListener` | `SkillCommand` and `SkillListener` moved into proper `skills.command` / `skills.listener` sub-packages; old `skill.*` files replaced with `@Deprecated` delegation stubs; zero-caller strays deleted | #2620 |

---

## Completed (sub-package layout enforcement — round 41)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| Sub-package layout enforcement — islands module | `com.skyblock.core.island.command.*` | `IslandCommand` and `IslandUpgradeCommand` moved from `com.skyblock.core.island` into `com.skyblock.core.island.command`; old locations replaced with `@Deprecated` delegation stubs | #2623 |
| Sub-package layout enforcement — minions and pets modules | `com.skyblock.core.minion.command.*`, `com.skyblock.core.pet.command.*`, `com.skyblock.core.pets.command.*` | `MinionCommand`, `PetCommand` (both `pet` and `pets` packages), and `PetsCommand` moved into their respective `command` sub-packages; old flat-package files replaced with `@Deprecated` delegation stubs | #2624 |

---

## Completed (manager consolidations — round 44)

| Domain | Canonical class | Duplicates removed | PR / commit |
|--------|-----------------|-------------------|-------------|
| CollectionManager / CollectionsManager | `com.skyblock.core.manager.CollectionManager` | All `CollectionManager`/`CollectionsManager` duplicates across `collections`, `collection`, `core`, and `plugin` modules → 1 canonical; remaining variants replaced with `@Deprecated` stubs delegating to canonical | #2632 |
| AuctionManager / AuctionHouseManager | `com.skyblock.core.manager.AuctionManager` | All `AuctionManager`/`AuctionHouseManager` duplicates across `auction`, `auctions`, `auctionhouse`, `core`, and `plugin` modules → 1 canonical in `com.skyblock.core.manager`; remaining variants replaced with `@Deprecated` stubs delegating to canonical | #2633 |

---

## Completed (sub-package layout enforcement — round 42)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| Sub-package layout enforcement — collections module | `com.skyblock.core.collection.command.*`, `com.skyblock.collection.command.*` | All command classes outside `com.skyblock.collection.command` and `com.skyblock.core.collection.command` moved into proper `command.*` sub-packages; old flat-package command files replaced with `@Deprecated` delegation stubs; zero-caller strays deleted | #2626 |

---

## Completed (manager consolidation deep-pass and stub delegation fixes — rounds 45-47)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| BankManager / BankingManager / BankHandler | `com.skyblock.core.manager.BankManager` | Deep-pass consolidation: all remaining delegation stubs wired end-to-end; missing `economy` module methods merged into canonical | #2637 |
| IslandManager / IslandHandler / IslandService | `com.skyblock.core.manager.IslandManager` | Deep-pass consolidation: all delegation gaps closed; 7 empty-returning stub methods (`getAllIslandBiomes`, `getIslandUnlocked`, `getIslandLevels`, `getVisitorCounts`, `getAllVisitLog`, `getAllIslandMembers`) replaced with full delegation chain: plugin stub → `core.manager.IslandManager` → `islands.IslandManager` | #2636 / #2640 |
| MinionManager / MinionsManager | `com.skyblock.core.manager.MinionManager` | Deep-pass consolidation: all remaining delegation stubs wired end-to-end; minion-tier and minion-type methods merged into canonical | #2638 |
| PetManager / PetsManager | `com.skyblock.core.manager.PetManager` | Deep-pass consolidation: all remaining delegation stubs wired end-to-end; XP table and pet-ability methods merged into canonical | #2639 |

---

## Completed (manager deletion sweep, enum consolidation, and pom.xml pruning — rounds 48-49)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| AccessoryRarity / com.skyblock.accessories.AccessoryRarity | `com.skyblock.core.model.AccessoryRarity` | Canonical enum created with `displayName`, `color`, and `magicalPower` fields; all `core.accessory.*` and `core.talisman.*` classes updated to import from `com.skyblock.core.model`; `com.skyblock.accessories.AccessoryRarity` retained as `@Deprecated` stub | #2645 |
| QuestManager / QuestsManager / com.skyblock.quests.QuestManager / com.skyblock.plugin.managers.QuestManager / com.skyblock.core.quests.QuestManager | `com.skyblock.core.manager.QuestManager` (via plugin delegation) | Duplicate `QuestManager` implementations deleted from `quests`, `plugin.managers.*`, and `core.quests` modules; callers in `QuestCommand` and `QuestProgressListener` updated to use canonical path | #2642 |
| EnchantmentManager / EnchantManager | `com.skyblock.core.enchanting.EnchantmentManager` | Duplicate implementations deleted from `enchantments` (`EnchantmentManager`), `enchants` (`EnchantManager`), `core.enchant` (`EnchantmentManager`), and `core.enchanting` (`EnchantmentManager`) modules; `EnchantmentCommand` updated to single import | #2642 |
| NPCManager / NpcManager | `com.skyblock.core.npc.NPCManager` | Duplicate `NpcManager` / `NPCManager` implementations deleted from `npc` and `npcs` modules along with orphaned `NpcType` enum; `SkyBlockPlugin` registration updated | #2642 |
| Dead-module pruning (post-round-48 pom.xml sweep) | *(removed from `pom.xml`)* | Three remaining empty/dead module entries removed from root `pom.xml` after the round-48 deletion sweep left their `src/main/java` trees empty | #2643 |

---

## Pending

| Domain | Canonical target | Known duplicates | Notes |
|--------|-----------------|-----------------|-------|
| SlayerManager | `com.skyblock.core.slayer.SlayerManager` | `slayer`, `slayers`, `skyblock-slayer`, `core`, `plugin` | `slayer` module most complete |
| EnchantingManager | `com.skyblock.core.enchanting.EnchantingManager` | `enchanting`, `enchantments`, `enchants`, `core`, `plugin` | `enchanting` module most complete |
| NPCManager | `com.skyblock.core.npc.NPCManager` | `npc` (NpcManager), `npcs` (NPCManager + NpcManager) | Inconsistent casing within `npcs` module |
| GuildManager | `com.skyblock.core.guild.GuildManager` | `guilds`, `guild` (enum-only fragment), `core`, `plugin` | Three variants referenced in ROADMAP Phase 5 |
| TradingManager | `com.skyblock.core.trade.TradeManager` | `trades` (TradeManager), `trading` (TradingManager) | Naming inconsistency: Trade vs Trading |
| BrewingManager | `com.skyblock.core.alchemy.AlchemyManager` | `alchemy` (AlchemyManager), `brewing` (BrewingManager) | `alchemy` is the richer module |

---

## How to execute a consolidation

1. Identify the most feature-complete variant (usually in `skyblock-core` or the
   richest standalone module per `STATUS.md`).
2. Move or merge into `com.skyblock.core.<domain>.<ClassName>` in `skyblock-core`.
3. Replace every other variant with a `@Deprecated` stub delegating to the canonical,
   or delete outright if no external callers remain.
4. Update all `import` statements across `core/`, `plugin/`, and leaf modules.
5. Mark the row ✅ in this table and record the commit hash.
