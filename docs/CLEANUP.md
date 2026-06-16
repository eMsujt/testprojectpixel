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
| Stat / StatType / PlayerStat / CombatStat | `com.skyblock.core.model.Stat` | 6 duplicates → 1 canonical (26 constants, full metadata); `PlayerStat`, `plugin.items.StatType`, `combat.CombatStat`, `core.combat.StatManager.CombatStat`, `core.stats.StatsManager.StatType`, `core.stat.StatManager.StatType` replaced with `@Deprecated` stubs or removed; 20+ caller files migrated | Oracle |
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

## Completed (abstract Menu/InventoryGUI base class and persistence-helper consolidation — rounds 50-51)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| Menu / InventoryGUI / GuiMenu abstract base classes | `com.skyblock.core.menu.Menu` | All duplicate abstract base classes (`Menu`, `InventoryGUI`, `GuiMenu`, `InventoryMenu`, `BaseMenu`) across every module collapsed into `com.skyblock.core.menu.Menu`; all concrete menu subclasses updated to `extends com.skyblock.core.menu.Menu` | #2650 |
| PlayerDataManager / DataManager / PersistenceHelper duplicates | `com.skyblock.core.persistence.DataManager` | Remaining duplicate persistence-helper classes (`PlayerDataManager`, `DataManager`, `PersistenceHelper`, `StorageManager`, `YamlPlayerStorage` variants) not already folded into the canonical `DataManager` consolidated; dead stubs deleted | #2651 |

---

## Completed (SkyBlockPlugin wiring fix, top-level menu consolidation, and zero-caller @Deprecated stub deletion — round 52)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| SkyBlockPlugin onEnable wiring + FarmingListener regression | `com.skyblock.plugin.SkyBlockPlugin` | Re-wired every canonical manager registration in `onEnable`; restored missing `FarmingListener` registration and fixed additional listener gaps exposed by the rounds 50-51 consolidation | #2654 |
| SkyBlock top-level menu class consolidation | `com.skyblock.core.menu.Menu` | Remaining duplicate top-level menu classes (`SkyBlockMenu`, `SkyBlockMainMenu`, `MainMenu`, and analogues) collapsed; all concrete subclasses now extend the single canonical base | #2652 |
| Zero-caller `@Deprecated` stub/delegate class deletion sweep | *(deleted)* | 6 zero-caller `@Deprecated` stub/delegate classes deleted outright: `core.combat.CombatListener`, `core.skill.SkillListener`, `core.collections.CollectionCommand`, `core.ability.AbilityManager`, `core.island.IslandUpgradeCommand`, and `crafting.CraftingManager` | pending |

---

## Completed (skills-domain, economy-domain, and core-module package layout normalization — round 53)

| Domain | Work done | PR / commit |
|--------|-----------|-------------|
| `com.skyblock.skills` / `skyblock-skills` package layout | All classes in every skills-domain module moved into correct sub-packages: manager classes → `.manager`, listener classes → `.listener`, command classes → `.command`, model/enum types → `.model`; misplaced strays deleted or restubbed | #2660 |
| `com.skyblock.economy` / bazaar, auction, bank, shop modules package layout | All classes across economy-domain modules moved into correct sub-packages: manager classes → `.manager`, command classes → `.command`, listener classes → `.listener`, model types → `.model`; flat-package strays eliminated | #2662 |
| `com.skyblock.core` core-module package layout | Misplaced classes in `skyblock-core` root corrected: manager classes not under `.manager` moved in, util classes not under `.util` moved in; `PlayerData` and any other misplaced types relocated to canonical sub-packages | #2661 |

---

## Completed (pets-domain/items-domain package layout normalization and ShopManager/BazaarManager consolidation — round 54)

| Domain | Work done | PR / commit |
|--------|-----------|-------------|
| `com.skyblock.pet` / `skyblock-pets` package layout | All classes in every pets-domain module moved into correct sub-packages: manager classes → `.manager`, listener classes → `.listener`, command classes → `.command`, model/enum types → `.model`; flat-package strays eliminated | #2666 |
| `com.skyblock.item` / `skyblock-items` package layout | All classes in every items-domain module moved into correct sub-packages: manager classes → `.manager`, listener classes → `.listener`, command classes → `.command`, model/enum types → `.model`; flat-package strays eliminated | #2666 |
| ShopManager / NpcShopManager | `com.skyblock.core.manager.ShopManager` | `NPCShopListener` and `ShopListener` both updated to delegate to `ShopManager.getInstance().getShop()` instead of loading `shops.yml` independently; duplicate YAML-parsing logic removed from both listeners | #2667 |
| BazaarManager / BazaarHandler zero-caller stub deletion | *(deleted)* | Two remaining zero-caller `@Deprecated` stubs (`com.skyblock.bazaar.BazaarOrder` and the second orphaned stub) deleted after confirming no callers exist outside the canonical `com.skyblock.core.manager.BazaarManager` | pending |

---

## Completed (GUI-domain and combat/quests-domain package layout normalization + manager wiring — rounds 55–56)

| Domain | Work done | PR / commit |
|--------|-----------|-------------|
| `com.skyblock.gui` / `skyblock-gui` package layout | All classes in every GUI-domain module moved into correct sub-packages: menu classes → `.menu`, command classes → `.command`, listener classes → `.listener`, model/util types → `.util`; `StorageMenu`, `PotionBagMenu`, `QuiverMenu`, and `FishingBagMenu` migrated into `com.skyblock.plugin.gui.menu`; flat-package strays eliminated | #2670 |
| `com.skyblock.combat` / `com.skyblock.quest` package layout | All classes in every combat-domain and quests-domain module moved into correct sub-packages: manager classes → `.manager`, listener classes → `.listener`, command classes → `.command`, model/enum types → `.model`; misplaced strays deleted or restubbed | #2671 |
| AuctionHouseManager, ShopManager, BazaarManager, CollectionManager wiring | All four managers wired into `SkyBlockPlugin.onEnable` (`load()`) and `onDisable` (`save()`); `ShopManager` promoted from fully-qualified reference to a proper import; `AuctionHouseManager`, `BazaarManager`, and `CollectionManager` instantiated and registered in canonical order | #2673 |

---

## Completed (AuctionHouseManager deep-consolidation sweep — round 57)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| AuctionHouseManager / AuctionManager (~9 copies) | `com.skyblock.core.manager.AuctionHouseManager` | All 9 duplicate `AuctionHouseManager` / `AuctionManager` implementations across `auction`, `auctions`, `auctionhouse`, `auction_house`, `core.*`, and `plugin.*` modules deleted or replaced with delegating stubs pointing to the single canonical class; every import, registration call, and `SkyBlockPlugin.onEnable` reference updated to `com.skyblock.core.manager.AuctionHouseManager` | #2672 |

---

## Completed (PetManager/PetsManager deep-consolidation sweep — round 58)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| PetManager / PetsManager (7 `@Deprecated` stubs) | `com.skyblock.core.manager.PetManager` | All 7 `@Deprecated` `PetManager`/`PetsManager` stub implementations across `pets`, `pet`, `core.*`, and `plugin.*` modules deleted or replaced with delegating stubs pointing to the single canonical class; every import, registration call, and `SkyBlockPlugin.onEnable` reference updated to `com.skyblock.core.manager.PetManager` | #2680 |

---

## Completed (ShopManager/NpcShopManager and BazaarManager/BazaarHandler deep-consolidation sweeps — round 60)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| ShopManager / NpcShopManager | `com.skyblock.core.manager.ShopManager` | All 4 duplicate/stub files (`NPCShopListener`, `plugin.npc.NpcShopMenu`, `plugin.shop.NpcShopMenu`, `plugin.shop.ShopListener`) deleted outright; canonical `com.skyblock.plugin.shop.listener.ShopListener` wired into `SkyBlockPlugin.onEnable`; every import and registration call updated to `com.skyblock.core.manager.ShopManager` | #2685 |
| BazaarManager / BazaarHandler | `com.skyblock.core.manager.BazaarManager` | No `BazaarHandler` implementation existed; two remaining orphaned duplicate model classes (`com.skyblock.bazaar.model.BazaarOrder` and `ProductCatalog`) confirmed zero-caller and deleted outright; canonical `com.skyblock.core.manager.BazaarManager` is now the sole implementation | #2686 |

---

## Completed (combat-domain stub deletion follow-up — round 62)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| `com.skyblock.combat` root-level `@Deprecated` stubs | `com.skyblock.combat.<sub-package>` | Deleted `CombatStat`, `CollectionCategory`, and `CollectionRegistry` zero-caller `@Deprecated` root-level stub classes (flagged by Oracle in the round-61 PR review); no live callers existed in any module | #2694 |

---

## Completed (IslandManager/SkyBlockIslandManager consolidation, Skill/Stat/Rarity/Collection enum consolidation, and combat-domain package layout normalization — round 61)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| IslandManager / SkyBlockIslandManager | `com.skyblock.core.manager.IslandManager` | All remaining duplicate `IslandManager`/`SkyBlockIslandManager` implementations grepped and deleted; every import, registration call, and `SkyBlockPlugin.onEnable` reference updated to `com.skyblock.core.manager.IslandManager`; delegation stubs in `islands` and `plugin.*` modules replaced with direct canonical references | #2689 |
| Skill / Stat / Rarity / Collection enum/registry duplicates | `com.skyblock.core.model.*` | All duplicate `Skill`, `Stat`, `Rarity`, and `Collection` enum and registry classes across every module consolidated into one canonical set in `com.skyblock.core.model`; all callers updated to import the canonical classes; orphaned duplicate copies deleted outright | #2690 |
| Combat-domain package layout (`com.skyblock.combat.*`) | `com.skyblock.combat.<sub-package>` | All classes across every `com.skyblock.combat` module moved into correct `.command`/`.listener`/`.model`/`.calculator`/`.manager` sub-packages; 2 live callers migrated from deprecated root-level stubs (`DamageCalculator`, `CombatDamageCalculator`) to canonical sub-package imports; all 15 `@Deprecated` stub files deleted; `@Deprecated` `CombatStat`, `CollectionCategory`, and `CollectionRegistry` root-level stubs (zero callers confirmed) deleted as round-61 PR-review follow-up | #2691 |

---

## Completed (SkillManager/SkillsManager, CollectionManager/CollectionsManager, MinionManager/MinionHandler/MinionService deep-consolidation sweeps — round 59)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| SkillManager / SkillsManager (3 `@Deprecated` stubs) | `com.skyblock.core.manager.SkillManager` | All 3 `@Deprecated` `SkillManager`/`SkillsManager` stub implementations across `skills`, `core.*`, and `plugin.*` modules deleted or replaced with delegating stubs pointing to the single canonical class; every import, registration call, and `SkyBlockPlugin.onEnable` reference updated to `com.skyblock.core.manager.SkillManager` | #2684 |
| CollectionManager / CollectionsManager | `com.skyblock.core.manager.CollectionManager` | All duplicate `CollectionManager`/`CollectionsManager` implementations across `collections`, `collection`, `core.*`, and `plugin.*` modules deleted or replaced with delegating stubs pointing to the single canonical class; every import and registration call updated to `com.skyblock.core.manager.CollectionManager` | #2682 |
| MinionManager / MinionHandler / MinionService | `com.skyblock.core.manager.MinionManager` | All 12 duplicate `MinionManager`/`MinionHandler`/`MinionService` implementations across `minion`, `minions`, `core.*`, and `plugin.*` modules deleted or replaced with delegating stubs pointing to the single canonical class; every import, registration call, and `SkyBlockPlugin.onEnable` reference updated to `com.skyblock.core.manager.MinionManager` | #2681 |

---

## Completed (SkyBlock main-menu GUI consolidation — round 63)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| SkyBlockMenu / SkyBlockMainMenu / MainSkyBlockMenu / SkyBlockInventoryMenu | `com.skyblock.plugin.gui.menu.SkyBlockMenu` | All duplicate SkyBlock main-menu GUI classes (`SkyBlockMenu`, `SkyBlockMainMenu`, `MainSkyBlockMenu`, `SkyBlockInventoryMenu`, and near-duplicate variants) across every module consolidated into the single canonical `com.skyblock.plugin.gui.menu.SkyBlockMenu`; all callers, registrations, and `SkyBlockPlugin.onEnable` references updated; orphaned duplicate copies deleted outright | #2695 |

---

## Completed (BankManager/BankingManager/BankingService deep-consolidation sweep — round 64)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| BankManager / BankingManager / BankingService | `com.skyblock.core.manager.BankManager` | All duplicate `BankManager`/`BankingManager`/`BankingService` implementations across every module consolidated into the single canonical `com.skyblock.core.manager.BankManager`; all callers, imports, and `SkyBlockPlugin` registrations updated; orphaned duplicate copies deleted outright | #2697 |

---

## Completed (BazaarManager/BazaarHandler/BazaarService thorough final-pass consolidation — round 65)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| BazaarManager / BazaarHandler / BazaarService | `com.skyblock.core.manager.BazaarManager` | Thorough final-pass audit of every class named `BazaarManager`, `BazaarHandler`, or `BazaarService` across every module; all remaining stubs and duplicates deleted outright; canonical `com.skyblock.core.manager.BazaarManager` confirmed as sole implementation | #2698 |

---

## Completed (CollectionManager/CollectionsManager, SkillManager/SkillsManager, and PetManager/PetsManager thorough final-pass consolidation — round 66)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| CollectionManager / CollectionsManager | `com.skyblock.core.manager.CollectionManager` | Thorough final-pass audit of every class named `CollectionManager` or `CollectionsManager` across every module; deleted 2 remaining `@Deprecated` stubs outright: `core/collections/CollectionsCommand.java` (referenced non-existent `CollectionsManager`) and `skyblock-core/command/CollectionCommand.java` (delegated to canonical `collection.command.CollectionCommand`); canonical `com.skyblock.core.manager.CollectionManager` confirmed as sole implementation | #2700 |
| SkillManager / SkillsManager | `com.skyblock.core.manager.SkillManager` | Thorough final-pass audit of every class named `SkillManager` or `SkillsManager` across every module; 2 remaining orphaned `@Deprecated` stubs deleted outright; canonical `com.skyblock.core.manager.SkillManager` confirmed as sole implementation | #2701 |
| PetManager / PetsManager | `com.skyblock.core.manager.PetManager` | Thorough final-pass audit of every class named `PetManager` or `PetsManager` across every module; 6 remaining `@Deprecated` stubs (`PetsCommand.java` ×2, `PetCommand.java` ×2, `PetAbility.java`, `PetsMenu.java`) deleted outright; canonical `com.skyblock.core.manager.PetManager` confirmed as sole implementation | #2703 |

---

## Completed (SkyBlockPlugin.onEnable canonical manager wiring — round 67)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| SkyBlockPlugin / onEnable wiring | `com.skyblock.plugin.SkyBlockPlugin` | Extended `SkyBlockPlugin.java` `onEnable` to properly instantiate and wire all canonical managers in dependency order (`SkillManager` → `CollectionManager` → `MinionManager` → `PetManager` → `AuctionHouseManager` → `BankManager` → `BazaarManager` → `ShopManager` → `IslandManager`); all managers confirmed registered and `onDisable` teardown updated to match | #2706 |

---

## Completed (minion-domain and items-domain package layout normalization — round 68)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| MinionManager / MinionListener / MinionCommand | `com.skyblock.core.manager.MinionManager` | Normalized minion-domain package layout: every class across every module containing minion-related classes (`MinionManager`, `MinionListener`, `MinionCommand`, and related model types) moved into correct `.manager`/`.listener`/`.command`/`.model` sub-packages; flat-package strays deleted; canonical `com.skyblock.core.manager.MinionManager` confirmed as sole manager implementation | #2707 |
| ItemManager / ItemBuilder / ItemUtil | `com.skyblock.core.util` | Normalized items-domain package layout: every class across every module containing item-related classes (excluding those already in `com.skyblock.core.util`) moved into correct `.manager`/`.listener`/`.command`/`.model`/`.util` sub-packages; flat-package strays eliminated; canonical item utilities in `com.skyblock.core.util` confirmed as sole implementations | #2709 |

---

## Completed (economy-domain, island-domain, and guild-domain package layout normalization — round 69)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| BankManager / BazaarManager / AuctionManager | `com.skyblock.core.manager.BankManager`, `com.skyblock.core.manager.BazaarManager`, `com.skyblock.core.manager.AuctionHouseManager` | Normalized economy-domain package layout: every class across every module containing bank, bazaar, or auction-related classes moved into correct `.manager`/`.listener`/`.command`/`.model` sub-packages; flat-package strays eliminated; canonical manager implementations confirmed as sole implementations | #2710 |
| IslandManager / GuildManager | `com.skyblock.core.manager.IslandManager`, `com.skyblock.core.guild.GuildManager` | Normalized island-domain and guild-domain package layout: every class across every module containing island-related classes (`IslandManager`, `IslandListener`, `IslandCommand`) and guild-related classes (`GuildManager`, `GuildListener`, `GuildCommand`) moved into correct `.manager`/`.listener`/`.command`/`.model` sub-packages; flat-package strays deleted; canonical implementations confirmed | #2711 |

---

## Completed (pets-domain and skills-domain package layout normalization — round 70)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| SkillManager / SkillListener / SkillCommand | `com.skyblock.core.manager.SkillManager` | Normalized skills-domain package layout: every class across every module containing skill-related classes (`SkillManager`, `SkillListener`, `SkillCommand`, `SkillLeaderboard`, and related model types) moved into correct `.manager`/`.listener`/`.command`/`.model` sub-packages; flat-package strays deleted; canonical `com.skyblock.core.manager.SkillManager` confirmed as sole manager implementation | #2713 |
| PetManager / PetListener / PetCommand | `com.skyblock.core.manager.PetManager` | Normalized pets-domain package layout: every class across every module containing pet-related classes (`PetManager`, `PetListener`, `PetCommand`, `PetItem`, `PetType`, and related model types) moved into correct `.manager`/`.listener`/`.command`/`.model` sub-packages; flat-package strays deleted; canonical `com.skyblock.core.manager.PetManager` confirmed as sole manager implementation | #2714 |

---

## Completed (skills-domain normalization gap fix and GUI/menu-domain package layout normalization — round 71)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| SkillsListener / SkillsXPListener / SkillListener orphans | `com.skyblock.core.manager.SkillManager` | Located and deleted every orphan `SkillsListener`, `SkillsXPListener`, and `SkillListener` stub missed by the round-70 pass (flagged by Oracle in the round-70 PR review); canonical `com.skyblock.core.manager.SkillManager` confirmed as the sole skills-domain implementation with no remaining duplicate listener or XP-listener stubs | #2715 |
| MenuManager / GuiManager / InventoryClickListener / GUI menus | `com.skyblock.core.menu.Menu` | Normalized GUI/menu-domain package layout: every class across every module containing GUI or menu-related classes (`MenuManager`, `GuiManager`, `InventoryClickListener`, and all concrete menu subclasses) moved into correct `.menu`/`.listener`/`.manager`/`.util` sub-packages; flat-package strays eliminated; canonical `com.skyblock.core.menu.Menu` confirmed as sole abstract base with all subclasses in proper sub-packages | #2717 |

---

## Completed (config-domain package layout normalization and AuctionHouseManager/AuctionCommand cleanup — round 73)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| ConfigManager / SkyBlockConfig / PluginConfig | `com.skyblock.core.config.ConfigManager` | Normalized config-domain package layout: every class across every module containing config-related classes (`ConfigManager`, `SkyBlockConfig`, `PluginConfig`, and related loader/watcher types) moved into correct `.config`/`.loader`/`.model` sub-packages; flat-package strays eliminated; canonical `com.skyblock.core.config.ConfigManager` confirmed as sole config-domain implementation with no remaining duplicate loaders or config-parser stubs | #2725 |
| AuctionHouseManager / AuctionCommand | `com.skyblock.core.manager.AuctionHouseManager` | Deleted orphan `AuctionCommand.java` (the only remaining consolidation artifact): the repo already had a single canonical `AuctionHouseManager` with no duplicate `AuctionManager` or `AuctionHandler` classes; the never-instantiated `AuctionCommand` stub was the sole remaining orphan and was removed outright; canonical `com.skyblock.core.manager.AuctionHouseManager` confirmed as sole implementation | #2726 |

---

## Completed (shop-domain and collections-domain package layout normalization — round 72)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| ShopManager / NpcShopManager / ShopListener / ShopCommand | `com.skyblock.core.manager.ShopManager` | Normalized shop-domain package layout: every class across every module containing shop or NPC-shop-related classes (`ShopManager`, `ShopListener`, `ShopCommand`, `NpcShopManager`, `NPCShopListener`, and related model types) moved into correct `.manager`/`.listener`/`.command`/`.model` sub-packages; flat-package strays eliminated; canonical `com.skyblock.core.manager.ShopManager` confirmed as sole manager implementation | #2718 |
| CollectionManager / CollectionListener / CollectionCommand | `com.skyblock.core.manager.CollectionManager` | Normalized collections-domain package layout: every class across every module containing collection-related classes (`CollectionManager`, `CollectionListener`, `CollectionCommand`, and related model types) moved into correct `.manager`/`.listener`/`.command`/`.model` sub-packages; 21 orphan/duplicate files deleted (deprecated stubs in `skyblock-core.collection`/`skyblock-core.collections`, the entire `collections` module, and `core.command.CollectionsCommand` duplicate); canonical `com.skyblock.core.manager.CollectionManager` confirmed as sole manager implementation | #2720 |

---

## Completed (SkillManager/SkillsManager final consolidation — round 75)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| SkillManager / SkillsManager | `com.skyblock.core.manager.SkillManager` | Thorough final-pass consolidation of every class named `SkillManager` or `SkillsManager` across every module; all remaining duplicate implementations and `@Deprecated` delegation stubs deleted outright; canonical `com.skyblock.core.manager.SkillManager` confirmed as sole implementation with no remaining orphan stubs | #2729 |

---

## Completed (CollectionManager/CollectionsManager and BazaarManager final consolidation — round 76)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| CollectionManager / CollectionsManager | `com.skyblock.core.manager.CollectionManager` | Thorough final-pass consolidation of every class named `CollectionManager` or `CollectionsManager` across every module; all remaining duplicate implementations and `@Deprecated` delegation stubs deleted outright; canonical `com.skyblock.core.manager.CollectionManager` confirmed as sole implementation | #2728 |
| BazaarManager | `com.skyblock.core.manager.BazaarManager` | Thorough final-pass consolidation of every class named `BazaarManager` across every module; orphan `com.skyblock.plugin.economy.BazaarCategoryMenu` deleted; canonical `com.skyblock.core.manager.BazaarManager` confirmed as sole implementation with all callers referencing it directly | #2730 |

---

## Completed (MinionManager final consolidation — round 77)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| MinionManager | `com.skyblock.core.manager.MinionManager` | Thorough final-pass consolidation of every class named `MinionManager` across every module; `plugin.minion.MinionManager` (location-based duplicate) and `plugin.minion.MinionPlacementListener` (duplicate of `plugin.listener.MinionPlacementListener`) deleted outright; canonical `com.skyblock.core.manager.MinionManager` confirmed as sole implementation | #2731 |

---

## Completed (IslandManager final consolidation — round 78)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| IslandManager | `com.skyblock.core.manager.IslandManager` | Thorough final-pass consolidation of every class named `IslandManager` across every module; all remaining duplicate implementations and `@Deprecated` delegation stubs deleted outright; canonical `com.skyblock.core.manager.IslandManager` confirmed as sole implementation with no remaining orphan stubs | #2733 |

---

## Completed (utility class and command executor consolidation — rounds 79–80)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| ItemBuilder / SkullItemUtil / MenuUtil | `com.skyblock.core.util.ItemBuilder`, `com.skyblock.core.util.SkullItemUtil`, `com.skyblock.core.util.MenuUtil` | Thorough final-pass consolidation of every `ItemBuilder.java`, `SkullItemUtil.java`, and `MenuUtil.java` across every module; all non-canonical duplicate implementations deleted outright; canonical classes in `com.skyblock.core.util` confirmed as sole implementations with no remaining orphan copies | #2736 |
| Bukkit CommandExecutor duplicates | canonical per-command classes in `com.skyblock.core.command` | Thorough final-pass audit of every class implementing `CommandExecutor` across every module; all non-canonical duplicate executor implementations deleted outright; canonical command classes in `com.skyblock.core.command` confirmed as sole implementations with no remaining orphan stubs | #2737 |

---

## Completed (ShopManager/NpcShopManager final consolidation — round 81)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| ShopManager / NpcShopManager | `com.skyblock.core.manager.ShopManager` | Thorough final-pass consolidation of every class named `ShopManager` or `NpcShopManager` across every module; all remaining duplicate implementations and `@Deprecated` delegation stubs deleted outright; canonical `com.skyblock.core.manager.ShopManager` confirmed as sole implementation with no remaining orphan stubs | #2734 |

---

## Completed (ProfileManager final consolidation — round 82)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| ProfileManager | `com.skyblock.core.manager.ProfileManager` | Thorough final-pass consolidation of every class named `ProfileManager` across every module; the `profiles` module's zero-caller `@Deprecated` `ProfileManager` (a pure delegation stub to the canonical) deleted outright; all remaining `ProfileManager` references confirmed to point to `com.skyblock.core.manager.ProfileManager`; canonical class confirmed as sole implementation with no remaining orphan stubs | #2741 |

---

## Completed (Skill/SkillType enum consolidation — round 83)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| Skill / SkillType enum | `com.skyblock.core.model.Skill` | Thorough final-pass consolidation of every `Skill`/`SkillType` enum definition across every module; created canonical `com.skyblock.core.model.Skill` merging the 12-skill `SkillManager.SkillType` inner enum and the 11-skill `Skills` enum definitions; all 18 callers across every module updated to import from `com.skyblock.core.model.Skill`; duplicate inner enums and orphaned `SkillType` variants deleted outright; one canonical `enum Skill` definition confirmed with no remaining orphan copies | #2745 |

---

## Completed (ItemType enum consolidation — round 85)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| ItemType / Material wrapper enum | `com.skyblock.core.model.ItemType` | Thorough final-pass consolidation of every `ItemType` and `Material` wrapper enum definition across every module; all duplicate `ItemType` variants and `@Deprecated` delegation stubs deleted outright; canonical `com.skyblock.core.model.ItemType` confirmed as sole implementation with no remaining orphan copies | #2747 |

---

## Completed (EconomyManager consolidation — round 86)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| EconomyManager / CurrencyManager | `com.skyblock.core.manager.EconomyManager` | Thorough final-pass consolidation of every class named `EconomyManager` or `CurrencyManager` across every module; all duplicate implementations and `@Deprecated` delegation stubs deleted outright; canonical `com.skyblock.core.manager.EconomyManager` confirmed as sole implementation with no remaining orphan copies | #2752 |

---

## Completed (QuestManager consolidation — round 87)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| QuestManager / QuestHandler | `com.skyblock.core.manager.QuestManager` | Thorough final-pass consolidation of every class named `QuestManager` or `QuestHandler` across every module; all duplicate implementations and `@Deprecated` delegation stubs deleted outright; canonical `com.skyblock.core.manager.QuestManager` confirmed as sole implementation with no remaining orphan copies | #2753 |

---

## Completed (CollectionManager consolidation — round 88)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| CollectionManager / CollectionsManager | `com.skyblock.core.manager.CollectionManager` | Thorough final-pass consolidation of every class named `CollectionManager` or `CollectionsManager` across every module; all duplicate implementations and `@Deprecated` delegation stubs deleted outright; canonical `com.skyblock.core.manager.CollectionManager` confirmed as sole implementation with no remaining orphan copies | #2759 |

---

## Completed (PetManager consolidation — round 89)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| PetManager / PetsManager | `com.skyblock.core.manager.PetManager` | Thorough final-pass consolidation of every class named `PetManager` or `PetsManager` across every module; all duplicate implementations and `@Deprecated` delegation stubs deleted outright; canonical `com.skyblock.core.manager.PetManager` confirmed as sole implementation with no remaining orphan copies | #2762 |

---

## Completed (AuctionHouseManager consolidation — round 90)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| AuctionHouseManager / AuctionManager / AuctionHandler | `com.skyblock.core.manager.AuctionHouseManager` | Thorough final-pass consolidation of every class named `AuctionHouseManager`, `AuctionManager`, or `AuctionHandler` across every module; all stale import references introduced by the consolidation fixed across every `.java` file; all duplicate implementations and `@Deprecated` delegation stubs deleted outright; canonical `com.skyblock.core.manager.AuctionHouseManager` confirmed as sole implementation with no remaining orphan copies | #2763 |

---

## Completed (GUI/Menu class consolidation — round 91)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| Menu abstract base / MenuManager / all concrete menus | `com.skyblock.core.menu.Menu`, `com.skyblock.core.menu.MenuManager` | Thorough final-pass consolidation of every class implementing or extending the plugin's `Menu` base across every module; all duplicate `GuiManager`, `SkyBlockMenuManager`, and concrete menu subclasses in `com.skyblock.plugin.gui.menu.*` and other non-canonical packages audited; all duplicate implementations and `@Deprecated` delegation stubs deleted outright; canonical `com.skyblock.core.menu.Menu` confirmed as sole abstract base with all concrete menus in `com.skyblock.core.menu.*` and `com.skyblock.core.menu.MenuManager` confirmed as sole manager; no remaining orphan copies | #2764 |

---

## Completed (Bukkit command-class consolidation — round 92)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| Bukkit CommandExecutor / TabCompleter command classes | `com.skyblock.core.command.*` | Thorough final-pass consolidation of every class implementing `CommandExecutor` or `TabCompleter` across every module; all 37 dead duplicate command/tab-completer files in `skyblock-core`, `core`, and `plugin` modules audited; all had zero callers outside their own class and were deleted outright; canonical command classes in `com.skyblock.core.command.*` confirmed as sole implementations with no remaining orphan copies | #2770 |

---

## Completed (stale-import fix sweep + dead-code deletion — round 93)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| Stale imports (round-92 follow-up) | N/A | Swept every `.java` file for stale import references introduced by the round-92 Bukkit command-class consolidation; no stale imports found — no file changes required | — |
| Package layout (skills + collections modules) | `com.skyblock.core.manager.SkillManager`, `com.skyblock.core.manager.CollectionManager` | Standardized package layout in the `skills` and `collections` modules: moved every misplaced command, listener, GUI, manager, model, and util class into the correct sub-package; flat-package strays eliminated; canonical managers confirmed as sole implementations | #2772 |
| Dead-code sweep (rounds 75–92 follow-up) | N/A | Deleted 8 dead `.java` files whose bodies contained only empty constructors, `@Deprecated` annotations with no callers, or pure no-op stubs: three `ProfileManager` delegation stubs (`core.profile`, `plugin.managers`, `profile` modules), `DungeonsManager` delegation stub, `SkyBlockItemStack`/`SkyBlockItemManager` stubs (canonical replacements already exist), and two additional zero-caller orphan files not caught in earlier sweeps | #2773 |

---

## Completed (package layout standardization — round 94)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| gui module package layout (`com.skyblock.gui`) | `com.skyblock.gui.{menu,listener,model,util}` | All Menu/GUI implementation classes moved into correct sub-packages; flat-package strays eliminated; `@Deprecated` stubs left at old locations | #2777 |
| pets module package layout (`com.skyblock.pets`) | `com.skyblock.pets.{command,listener,gui,manager,model,util}` | All Pet-related classes moved into correct sub-packages; flat-package strays eliminated; `@Deprecated` stubs left at old locations | #2778 |
| auction module package layout (`com.skyblock.auction`) | `com.skyblock.auction.{command,listener,gui,manager,model}` | All Auction-related classes moved into correct sub-packages; flat-package strays eliminated; `@Deprecated` stubs left at old locations | #2779 |
| minions module package layout (`com.skyblock.minions`) | `com.skyblock.minions.{command,listener,gui,manager,model,util}` | All Minion-related classes moved into correct sub-packages; flat-package strays eliminated; `@Deprecated` stubs left at old locations | #2780 |
| bazaar module package layout (`com.skyblock.core.bazaar`) | `com.skyblock.core.bazaar.{manager,command,gui}` | All Bazaar-related classes moved into correct sub-packages; `@Deprecated` empty stubs left at old `com.skyblock.core.{manager,command,menu}` locations; all callers updated | #2781 |
| shop module package layout (`com.skyblock.core.shop`) | `com.skyblock.core.shop.{manager,command,listener,gui}` | All Shop/NPC-shop-related classes moved into correct sub-packages; `@Deprecated` stubs left at old locations; `FarmerShop` migrated into `com.skyblock.plugin.shop.gui` | #2782 |

---

## Completed (shop-module standardization completion and stale-import sweep — round 95)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| shop module package layout (round-94 follow-up) | `com.skyblock.core.shop.{manager,command,listener,gui}` | PR #2782 merged: all Shop/NPC-shop-related classes confirmed in correct sub-packages (`ShopManager` → `com.skyblock.core.shop.manager`, `ShopMenu` → `com.skyblock.core.shop.gui`, `FarmerShop` → `com.skyblock.plugin.shop.gui`); `@Deprecated` stubs at old `com.skyblock.core.{manager,command,menu}` locations; round-94 pending item closed | #2782 |
| Stale imports (round-94 package-layout follow-up) | N/A | Swept every `.java` file for stale import references introduced by the round-94 package-layout standardizations (gui #2777, pets #2778, auction #2779, minions #2780, bazaar #2781, shop #2782); stale references identified across multiple modules — remediation assigned to round-96 | — |

---

## Completed (stale-import fix sweep — round 96)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| Stale imports (round-94/95 package-layout follow-up) | N/A | Swept every `.java` file for stale import references introduced by the round-94/95 package-layout standardizations (gui #2777, pets #2778, auction #2779, minions #2780, bazaar #2781, shop #2782); fixed 3 stale import references: `com.skyblock.core.dungeon.DungeonCommand` → `com.skyblock.core.dungeon.command.DungeonCommand` and `com.skyblock.core.manager.DungeonManager` → `com.skyblock.core.dungeon.manager.DungeonManager` in `SkyBlockPlugin` | #2785 |

---

## Completed (core-module internal package standardization — round 97)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| `com.skyblock.core` internal package layout | `com.skyblock.core.{manager,command,listener,gui,model,util}` | Audited every class in `com.skyblock.core` and moved any misplaced manager, listener, command, and GUI classes into their canonical sub-packages; flat-package strays eliminated; 9 flat-package files replaced with `@Deprecated` stubs; 8 stale import references across 4 caller files updated to canonical sub-package paths | #2789 |

---

## Completed (stale-import fix sweep and dead-module pruning — round 98)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| Stale imports (round-97 core-module restructuring follow-up) | N/A | Swept every `.java` file for stale import references introduced by the round-97 core-module internal package standardization; all stale references fixed — callers now import from canonical `{domain}/manager/` and `{domain}/listener/` sub-packages | #2787 |
| Dead-module pruning (rounds 75–96 follow-up) | *(removed from `pom.xml`)* | Pruned the parent `pom.xml` of every dead or empty module accumulated through rounds 75–96; each `<module>` entry verified against its `src/main/java` tree before removal | #2788 |

---

## Completed (economy/items/combat module package-layout standardizations and core-module stub consolidations — round 99)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| SkillManager / SkillsManager stub consolidation | `com.skyblock.core.skills.manager.SkillManager` | Zero-caller `com.skyblock.core.skills.command.SkillCommand` deleted; duplicate `com.skyblock.core.skill.SkillLevelManager` deleted (XP-lookup already live in canonical `SkillManager`) | #2792 |
| ShopManager / NpcShopManager stub consolidation | `com.skyblock.core.shop.manager.ShopManager` | Deprecated stub `com.skyblock.core.manager.ShopManager` deleted — all callers already used the canonical `com.skyblock.core.shop.manager.ShopManager` | #2793 |
| BazaarManager / BazaarHandler stub consolidation | `com.skyblock.core.bazaar.manager.BazaarManager` | Three deprecated stub files (`com.skyblock.core.manager.BazaarManager`, `com.skyblock.core.command.BazaarCommand`, `com.skyblock.core.menu.BazaarMenu`) deleted — all callers already used canonical implementations in `com.skyblock.core.bazaar.*` | #2794 |
| Stale imports (economy/items/combat package-layout follow-up) | N/A | Fixed 3 stale `import com.skyblock.core.items.CustomItemManager` references to `import com.skyblock.core.items.manager.CustomItemManager` in `SkyBlockItemStack.java`, `SkyBlockItemManager.java`, and `SkyBlockPlugin.java` | #2795 |

---

## Completed (PetManager, BankManager, and MinionManager stub consolidations — round 100)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| PetManager / PetsManager stub consolidation | `com.skyblock.core.pets.manager.PetManager` | Canonical `PetManager` moved to `com.skyblock.core.pets.manager`; all callers updated to the new import path; deprecated stubs at old locations removed | #2798 |
| BankManager / BankingManager / BankHandler stub consolidation | `com.skyblock.core.bank.manager.BankManager` | Duplicate `com.skyblock.core.manager.BankManager` and its old test deleted; `BankManagerTest` created at `com.skyblock.core.bank.manager`; no caller import updates required (all live callers already used canonical path) | #2799 |
| MinionManager / MinionsManager / MinionHandler stub consolidation | `com.skyblock.core.minion.manager.MinionManager` | Canonical `MinionManager` confirmed at `com.skyblock.core.minion.manager`; all 13 callers migrated to new import; old `com.skyblock.core.manager.MinionManager` replaced with `@Deprecated` delegation stub | #2800 |

---

## Completed (IslandManager/IslandHandler consolidation, AuctionHouseManager audit, and zero-caller stub deletion — round 101)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| IslandManager / IslandHandler duplicate consolidation | `com.skyblock.core.island.manager.IslandManager` | All 7 duplicate implementations across every module consolidated into one canonical class; all 9 references across 7 caller files updated to new import path; old `com.skyblock.core.manager.IslandManager` replaced with `@Deprecated` delegation stub | #2803 |
| AuctionHouseManager / AuctionManager / AuctionHandler audit | `com.skyblock.core.manager.AuctionHouseManager` | Audit of all 9 duplicate implementations confirmed no surviving copies; no file changes required — canonical `com.skyblock.core.manager.AuctionHouseManager` already sole implementation after round-90 consolidation (#2763) | — |
| Zero-caller `@Deprecated` stub deletion | *(class files deleted)* | 4 `@Deprecated` stubs with zero live callers deleted outright: `core.manager.IslandManager`, `core.manager.AbilityManager`, `core.manager.CollectionManager`, and test-dir `AuctionHouseManagerTest` | #2804 |

---

## Completed (stale-import fix sweep, zero-caller stub deletion, and EnchantmentManager consolidation — round 102)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| Stale imports (round-101 IslandManager/AuctionHouseManager follow-up) | N/A | Swept every `.java` file for stale import references introduced by the round-101 IslandManager consolidation and AuctionHouseManager audit; no stale imports found — no file changes required | — |
| Zero-caller `@Deprecated` stub deletion | *(class files deleted)* | 1 `@Deprecated` stub with zero live callers deleted outright: `com.skyblock.core.island.IslandGenerator` (delegated to `com.skyblock.core.island.util.IslandGenerator` with no callers) | #2807 |
| EnchantManager / EnchantmentManager / EnchantHandler duplicate consolidation | `com.skyblock.core.enchant.manager.EnchantmentManager` | All duplicate `EnchantManager`, `EnchantmentManager`, and `EnchantHandler` implementations consolidated into one canonical class at `com.skyblock.core.enchant.manager.EnchantmentManager`; old `com.skyblock.core.manager.EnchantmentManager` and `SkyBlockEnchantListener` replaced with `@Deprecated` empty stubs pointing to canonical; all existing callers already used canonical implementations | #2806 |

---

## Completed (QuestManager, CraftingManager consolidations, and EnchantmentManager/QuestManager follow-up — round 103)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| QuestManager / QuestsManager / QuestHandler duplicate consolidation | `com.skyblock.core.quests.manager.QuestManager` | All duplicate `QuestManager`, `QuestsManager`, and `QuestHandler` implementations consolidated into one canonical class; old stubs replaced with `@Deprecated` delegation stubs pointing to canonical | #2808 |
| CraftingManager / RecipeManager duplicate consolidation | `com.skyblock.core.manager.CraftingManager` | All duplicate `CraftingManager` and `RecipeManager` implementations consolidated into one canonical class at `com.skyblock.core.manager.CraftingManager`; all callers updated to new import path | #2756 |
| Stale imports (round-102 EnchantmentManager and QuestManager follow-up) | N/A | Swept every `.java` file for stale import references introduced by the round-102 EnchantmentManager and QuestManager consolidations; remediated across all modules | — |
| Zero-caller `@Deprecated` stub deletion | *(class files deleted)* | `@Deprecated` stubs left over from the round-102 QuestManager and EnchantManager consolidations with zero live callers deleted outright | — |

---

## Completed (duplicate command class consolidation and zero-caller stub deletion — round 104)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| Duplicate plugin command class consolidation | Rich `com.skyblock.core.<domain>.command.*` implementations | 5 plugin-level duplicate command classes (`ProfileCommand`, `IslandCommand`, `QuestCommand`, `TradingCommand`, `DungeonCommand`) deleted; `SkyBlockPlugin.java` updated to wire all 5 commands to their canonical rich implementations in `com.skyblock.core.<domain>.command.*` | #2813 |
| Zero-caller `@Deprecated` stub deletion | *(class files deleted)* | `@Deprecated` stub command classes left over from the round-104 command consolidation with zero live callers deleted outright: `com.skyblock.core.dungeon.DungeonCommand` (superseded by `com.skyblock.core.dungeon.command.DungeonCommand`) | #2814 |

---

## Completed (stale-import fix sweep and zero-caller stub deletion — round 105)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| Stale imports (round-104 ItemBuilder and SkullItemUtil consolidation follow-up) | N/A | Swept every `.java` file for stale import references introduced by the round-104 ItemBuilder and SkullItemUtil consolidations; no stale imports found — no file changes required | — |
| Zero-caller `@Deprecated` stub deletion | *(class files deleted)* | `@Deprecated` stub files left over from the round-104 ItemBuilder and SkullItemUtil consolidations with zero live callers deleted outright: `ItemBuilder` duplicates outside `com.skyblock.core.util` and `SkullItemUtil`/`SkullUtil`/`SkullBuilder` variants in non-canonical packages | — |

---

## Completed (Menu/AbstractMenu/GuiBuilder base class consolidation and stale-import sweep — round 106)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| Menu / BaseMenu / InventoryMenu / AbstractMenu / GuiBuilder duplicate base classes | `com.skyblock.core.menu.Menu` | All duplicate abstract base class implementations (`Menu`, `BaseMenu`, `InventoryMenu`, `AbstractMenu`, `GuiBuilder`) across every module consolidated into one canonical abstract at `com.skyblock.core.menu.Menu`; all concrete subclasses updated to `extends com.skyblock.core.menu.Menu`; zero-caller stubs deleted outright | #2817 |
| Stale imports (round-105 Menu/BaseMenu/GuiBuilder consolidation follow-up) | N/A | Swept every `.java` file for stale import references introduced by the round-105 Menu/AbstractMenu/GuiBuilder consolidation; no stale imports found — no file changes required | — |

---

## Completed (SkillManager stub fix, Collection enum audit, Skill/SkillType/Skills enum consolidation, and zero-caller stub deletion — round 107)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| SkillManager delegation stub `getInstance()` fix | `com.skyblock.core.skills.SkillManager` | `com.skyblock.core.manager.SkillManager` delegation stub had a broken `getInstance()` that returned `null`; fixed to delegate correctly to `com.skyblock.core.skills.SkillManager.getInstance()` so all `@Deprecated`-stub callers resolve the singleton without NPE | — |
| Collection / CollectionType / Collections enum consolidation audit | `com.skyblock.core.model.Collection` | Swept every module for duplicate `Collection`, `CollectionType`, and `Collections` enum or registry-class definitions; canonical `com.skyblock.core.model.Collection` already sole implementation — no file changes required | — |
| Skill / SkillType / Skills enum consolidation | `com.skyblock.core.model.Skill` | All remaining duplicate `Skill`, `SkillType`, and `Skills` enum or registry-class implementations across all modules collapsed into the canonical `com.skyblock.core.model.Skill`; all callers migrated; orphan copies deleted | #2819 |
| Zero-caller `@Deprecated` stub deletion (rounds 102–106 follow-up) | *(class files deleted)* | All `@Deprecated` stub files left over from rounds 102–106 consolidations with zero live callers deleted outright: `com.skyblock.core.enchant.SkyBlockEnchantListener`, `com.skyblock.core.menu.ShopMenu`, `com.skyblock.core.menu.DungeonMenu`, `com.skyblock.core.manager.SkillManager` | #2820 |

---

## Completed (Stat/StatType/SkyBlockStat/SkyBlockStats enum, BazaarHandler/BazaarManager, NpcShopManager, and CollectionManager consolidations — round 108)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| Stat / StatType / SkyBlockStat / SkyBlockStats enum consolidation | `com.skyblock.core.model.Stat` | All duplicate `Stat`, `StatType`, `SkyBlockStat`, and `SkyBlockStats` enum and registry-class implementations across all modules consolidated into one canonical enum at `com.skyblock.core.model.Stat`; all callers migrated; orphan copies deleted | #2822 |
| BazaarHandler / BazaarManager duplicate consolidation | `com.skyblock.core.bazaar.manager.BazaarManager` | All remaining duplicate `BazaarHandler` and `BazaarManager` implementations across all modules audited; `@Deprecated` delegation stubs at old locations replaced with empty stubs pointing to canonical `com.skyblock.core.bazaar.manager.BazaarManager`; all callers already used canonical path — no import changes required | #2823 |
| NpcShopManager consolidation | `com.skyblock.core.shop.manager.NpcShopManager` | All duplicate `NpcShopManager` implementations across all modules consolidated into one canonical class at `com.skyblock.core.shop.manager.NpcShopManager`; old stubs replaced with `@Deprecated` delegation stubs pointing to canonical; all callers updated to new import path | #2824 |
| CollectionManager consolidation | `com.skyblock.core.manager.CollectionManager` | All remaining duplicate `CollectionManager` and `CollectionsManager` implementations across all modules consolidated into one canonical class at `com.skyblock.core.manager.CollectionManager`; `@Deprecated` delegation stubs placed at all old locations; all callers migrated to canonical import path | #2825 |

---

## Completed (SkillManager/SkillsManager consolidation and PetManager audit — round 109)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| SkillManager / SkillsManager duplicate consolidation | `com.skyblock.core.manager.SkillManager` | All duplicate `SkillManager` and `SkillsManager` implementations across all modules consolidated into one canonical class at `com.skyblock.core.manager.SkillManager`; `@Deprecated` delegation stub placed at old `com.skyblock.core.skills.manager.SkillManager` path; all 25 callers updated to import from canonical package | #2825 |
| PetManager / PetsManager duplicate consolidation audit | `com.skyblock.core.pets.manager.PetManager` | All classes named `PetManager` or `PetsManager` across every module audited; canonical `com.skyblock.core.pets.manager.PetManager` confirmed as sole implementation with all callers already on the canonical import path — no file changes required | — |

---

## Completed (MinionManager stub deletions and BankManager/BankingManager/BankHandler consolidation — rounds 110–111)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| MinionManager `@Deprecated` stub deletion (round-110 follow-up) | `com.skyblock.core.minion.manager.MinionManager` | All 9 zero-caller `@Deprecated` stub files left over from the round-110 MinionManager consolidation deleted outright: `MinionTickScheduler`, `MinionTickTask`, `MinionInventoryMenu`, `Minion`, `CobblestoneMinion`, and 4 additional stub classes; canonical `com.skyblock.core.minion.manager.MinionManager` confirmed as sole implementation | #2828 |
| BankManager / BankingManager / BankHandler consolidation and stub deletion | `com.skyblock.core.manager.BankManager` | All `BankManager`/`BankingManager`/`BankHandler` duplicate implementations across all modules consolidated into one canonical class; `BankType` and `BankTier` enum fields (`displayName`, `interestRate`, `isShared()`) restored to the delegation stub to match canonical values; sole zero-caller stub (`com.skyblock.core.bank.manager.BankManager`) deleted outright | #2829 / #2830 |

---

## Completed (gui/pets/skills module sub-package layout standardization — rounds 112–114)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| gui module package layout (`com.skyblock.gui`) | `com.skyblock.gui.{menu,util,listener,builder}` | All `.java` files in the `gui` module moved into canonical sub-packages; flat-package strays eliminated; all callers updated to new import paths | #2836 |
| pets module package layout (`com.skyblock.pets`) | `com.skyblock.pets.{command,listener,gui,manager,model}` | All `.java` files in the `pets` module moved into canonical sub-packages; flat-package strays eliminated; all callers updated to new import paths | #2835 |
| skills module package layout (`com.skyblock.skills`) | `com.skyblock.skills.{command,listener,gui,manager,model,util}` | All `.java` files currently in flat or misplaced packages under the `skills` module moved into canonical sub-packages; flat-package strays eliminated; all callers updated to new import paths | #2834 |

---

## Completed (EnchantType stub deletion, /skills executor binding, and duplicate event-listener consolidation — round 115)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| EnchantType empty stub deletion | `com.skyblock.enchanting.model.EnchantType` | Empty `com.skyblock.enchanting.EnchantType` stub (zero constants, zero callers) left over from the enchanting module standardization deleted outright; canonical `com.skyblock.enchanting.model.EnchantType` confirmed as sole implementation with all enum constants (SHARPNESS, SMITE, BANE_OF_ARTHROPODS, etc.) present | #2841 |
| /skills command executor binding | `com.skyblock.skills.command.SkillsCommand` | Verified `SkyBlockPlugin` correctly registers `SkillsCommand` as the executor for the `/skills` Bukkit command; no file changes required — binding was already present | — |
| Duplicate event-listener consolidation (PlayerJoinListener / PlayerQuitListener / PlayerMoveListener) | `com.skyblock.plugin.profile.ProfileManager` | All redundant `PlayerJoinListener`, `PlayerQuitListener`, and `PlayerMoveListener` implementations across every module consolidated; `ProfileJoinListener`, `PlayerJoinSetupListener`, and `PlayerJoinQuitListener` deleted outright — `ProfileManager` already handles `getOrCreate` on join and all join/quit lifecycle logic; all registrations removed from `SkyBlockPlugin` | #2842 |

---

## Completed (hub-menu consolidation and async player-data load/save fix — round 116)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| SkyBlock hub-menu consolidation (SkyBlockMenuCommand / SkyBlockHubMenu / HubMenu) | `com.skyblock.core.hub.SkyblockHubCommand` | All duplicate SkyBlock main hub-menu implementations across every module found and consolidated; canonical `SkyblockHubCommand` confirmed as sole implementation; all duplicate `HubMenu`/`SkyBlockMainMenu` classes deleted outright or replaced with `@Deprecated` delegation stubs; all registrations updated in `SkyBlockPlugin` | #2846 |
| Async player-data load/save fix (`PlayerDataManager.onQuit`) | `com.skyblock.plugin.profile.PlayerDataManager` | Added missing `onQuit` handler that snapshots player YAML on the main thread (safe, no I/O), evicts the `core.manager.PlayerDataManager` cache entry, then writes to disk asynchronously via `Bukkit.getScheduler().runTaskAsynchronously`; player data now correctly persisted on disconnect | #2845 |

---

## Completed (items module class relocation and combat module package standardization — round 117)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| Items module class relocation (`ItemBuilder` / `SkullItemUtil`) | `com.skyblock.items.util.ItemBuilder`, `com.skyblock.items.util.SkullItemUtil` | `ItemBuilder` and `SkullItemUtil` moved from flat `com.skyblock.core.util` into `com.skyblock.items.util`; originals at old locations replaced with `@Deprecated` forwarding stubs; `skyblock-items` dependency wired into `pom.xml` for all caller modules | — |
| Combat module package standardization completion | `com.skyblock.combat.{calculator,manager,model}` | All combat module `.java` files confirmed in correct sub-packages; deprecated `engine/CombatEngine.java` forwarding stub (zero callers) deleted outright; canonical sub-package layout fully enforced across `calculator`, `manager`, and `model` sub-packages | #2849 |

---

## Completed (ShopManager/NpcShopManager and BazaarManager/BazaarHandler consolidations, ItemBuilder/SkullItemUtil stub deletion, and core/items module package standardization — round 118)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| ShopManager / NpcShopManager | `com.skyblock.core.manager.ShopManager` | `NpcManager.ShopItem` inner record consolidated into `ShopManager.ShopEntry`; `ShopItem` record and `findItem()` method deleted; `withdraw(UUID, long)` overload confirmed compatible with `entry.buyPrice()` returning `long`; all remaining duplicate implementations across every module consolidated into the single canonical class | #2853 |
| BazaarManager / BazaarHandler | `com.skyblock.core.manager.BazaarManager` | All duplicate `BazaarManager`/`BazaarHandler` implementations across every module consolidated into one canonical class; all callers (`BazaarCommand` and 4 others) updated to import from canonical package; `@Deprecated` stub placed at old `com.skyblock.core.bazaar.manager` location | #2854 |
| ItemBuilder / SkullItemUtil `@Deprecated` forwarding stub deletion | `com.skyblock.items.util.ItemBuilder`, `com.skyblock.items.util.SkullItemUtil` | Both `@Deprecated` forwarding stubs left in `com.skyblock.core.util` by the round-117 items-module relocation deleted outright; all 36 callers confirmed migrated to `com.skyblock.items.util.*` | #2852 |
| Items module class relocation (round-117 completion) | `com.skyblock.items.util.*` | All remaining `.java` files in flat or misplaced packages under the `skyblock-items` module relocated into canonical sub-packages (broader than the initial `ItemBuilder`/`SkullItemUtil` move); flat-package strays eliminated | #2848 |
| Core module internal package standardization | `com.skyblock.core.{manager,listener,command,gui,...}` | Completed full audit of `com.skyblock.core` — every misplaced manager, listener, command, and GUI class moved into canonical sub-packages; flat-package strays eliminated | #2851 |

---

## Completed (SkillManager/SkillsManager consolidation — round 119)

| Domain | Canonical class | Work done | PR / commit |
|--------|-----------------|-----------|-------------|
| SkillManager / SkillsManager | `com.skyblock.core.skills.manager.SkillManager` | Consolidated all duplicate `SkillManager` and `SkillsManager` implementations across every module into one canonical class at `com.skyblock.core.skills.manager.SkillManager`; `@Deprecated` stub placed at old `com.skyblock.core.manager.SkillManager` location; all callers migrated to canonical import path | #2860 |

---

## Completed (zero-caller @Deprecated stub deletion — round 120)

| Domain | Canonical class/package | Work done | PR / commit |
|--------|------------------------|-----------|-------------|
| SkillManager `@Deprecated` stub deletion (round-119 follow-up) | `com.skyblock.core.skills.manager.SkillManager` | `@Deprecated` `SkillManager` stub at `com.skyblock.core.skills.manager` left by the round-119 consolidation deleted outright after confirming zero live callers | #2861 |
| CollectionManager / PetManager `@Deprecated` stub deletion | *(class files deleted)* | `@Deprecated` `CollectionManager` stub at `com.skyblock.core.collections.manager` and `@Deprecated` `PetManager` stub at `com.skyblock.pets.manager` both deleted outright after confirming zero live callers | #2862 |

---

## Pending

| Domain | Canonical target | Known duplicates | Notes |
|--------|-----------------|-----------------|-------|
| SlayerManager | `com.skyblock.core.slayer.SlayerManager` | `slayer`, `slayers`, `skyblock-slayer`, `core`, `plugin` | `slayer` module most complete |
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
