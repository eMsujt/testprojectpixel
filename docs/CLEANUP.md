# Module Consolidation Tracker

Tracks every duplicate-class consolidation. Canonical home is always `skyblock-core`
(`com.skyblock.core.*`). See `STATUS.md` for the full duplicate inventory and
`ROADMAP.md` Phase 5 for the overall consolidation goal.

**Legend:** ✅ Done · 🔄 In progress · ⏳ Pending

---

## Completed

| Domain | Canonical class | Duplicates removed | PR / commit |
|--------|-----------------|-------------------|-------------|
| Auction House Manager | `com.skyblock.core.auction.AuctionHouseManager` | 9 → 1 (`auction`, `auctions`, `auctionhouse`, `auction_house`, `core` ×2, `economy`, `plugin` ×2) | 9f504bba |
| Rarity enum | `com.skyblock.core.model.Rarity` | 3 → 1 (`items/Rarity`, `items/ItemRarity`, `core/RarityType`) | 2e713bba |
| Item builder utility | `com.skyblock.core.util.ItemBuilder` | 4 → 1 (`plugin.gui`, `plugin.item`, `plugin.items`, `core.item`) | 01eebf00 |
| Collection / CollectionCategory enum | `com.skyblock.core.model.Collection`, `com.skyblock.core.model.CollectionCategory`, `com.skyblock.core.util.CollectionRegistry` | 4+ variants across `collection`, `collections`, `core`, `plugin` | Sentinel |
| Menu abstract base | `com.skyblock.core.menu.Menu` | 3 → 1 (`MenuManager.SkyBlockMenu` inner class, `plugin.gui.Menu`, `core` variant) | Vega |
| SkillManager / SkillsManager | `com.skyblock.core.skills.SkillManager` | 8 → 1 (`skills.SkillManager`, `skills.SkillsManager`, `core.skills.SkillsManager`, `core.skill.SkillManager`, `plugin.skills.SkillManager`, `plugin.skills.SkillsManager`, `plugin.managers.SkillsManager`, `plugin.manager.SkillManager`) | Vega |

---

## Completed (continued)

| Domain | Canonical class | Duplicates removed | PR / commit |
|--------|-----------------|-------------------|-------------|
| CollectionManager | `com.skyblock.core.manager.CollectionManager` | 3 skyblock-core duplicates → `@Deprecated` stubs; 5 command classes and 2 plugin entry points updated to use canonical + `com.skyblock.core.model.Collection` | Oracle |
| EnchantmentManager | `com.skyblock.core.manager.EnchantmentManager` | All `EnchantmentManager`/`EnchantManager` duplicates → 1 canonical; remaining variants replaced with `@Deprecated` stubs delegating to canonical | #2512 / 64852fe2 |
| CraftingManager / RecipeManager | `com.skyblock.core.crafting.CraftingManager` | 3 → 1 (`plugin` stub, `core` stub, `core.crafting` survivor); added `registerRecipes(JavaPlugin)` from plugin stub; all callers updated | #2510 / 3c333a9e |
| Dead-module pruning (SkillType / SkillXPTable) | `com.skyblock.core.skills.SkillManager.SkillType` | Migrated all callers off deprecated `com.skyblock.skills.SkillType` and `com.skyblock.skills.SkillXPTable`; deprecated re-export variants removed | #2511 / c8fa794d |
| QuestManager / QuestsManager | `com.skyblock.core.manager.QuestManager` | All `QuestManager`/`QuestsManager` duplicates → 1 canonical; remaining variants replaced with `@Deprecated` stubs delegating to canonical | #2513 |
| EconomyManager / CoinManager / MoneyManager / PurseManager | `com.skyblock.core.manager.EconomyManager` | All `EconomyManager`/`CoinManager`/`MoneyManager`/`PurseManager` duplicates → 1 canonical; remaining variants replaced with `@Deprecated` stubs delegating to canonical | #2514 |
| AbilityManager / AbilityHandler / SpecialAbilityManager | `com.skyblock.core.manager.AbilityManager` | All `AbilityManager`/`AbilityHandler`/`SpecialAbilityManager` duplicates → 1 canonical; remaining variants replaced with `@Deprecated` stubs delegating to canonical | #2515 |
| DungeonManager / DungeonsManager | `com.skyblock.core.manager.DungeonManager` | 7 duplicates → 1 canonical (896 lines, merging all APIs); all 6 `DungeonManager` duplicates and `DungeonsManager` replaced with `@Deprecated` delegating stubs | #2517 |
| Stat / StatType / PlayerStat / CombatStat | `com.skyblock.core.stat.Stat` | 6 duplicates → 1 canonical (26 constants, full metadata); `PlayerStat`, `plugin.items.StatType`, `combat.CombatStat`, `core.combat.StatManager.CombatStat`, `core.stats.StatsManager.StatType`, `core.stat.StatManager.StatType` replaced with `@Deprecated` stubs or removed; 20+ caller files migrated | In progress |

---

## Pending

| Domain | Canonical target | Known duplicates | Notes |
|--------|-----------------|-----------------|-------|
| BankManager | `com.skyblock.core.bank.BankManager` | `bank`, `banking`, `economy`, `core`, `plugin` ×2 | ~7 variants; `economy` module is most complete |
| BazaarManager | `com.skyblock.core.bazaar.BazaarManager` | `bazaar`, `economy`, `core`, `plugin` ×2 | ~7 variants; standalone `bazaar` module richest |
| IslandManager | `com.skyblock.core.island.IslandManager` | `islands`, `island`, `core`, `plugin` ×3 | ~7 variants; `islands` module most complete |
| MinionManager | `com.skyblock.core.minions.MinionManager` | `minions`, `minion`, `skyblock-minions`, `core`, `plugin` ×4 | ~9 variants; largest duplication surface |
| ProfileManager | `com.skyblock.core.profile.ProfileManager` | `profiles`, `profile`, `playerdata`, `core`, `plugin` | Iterator.next() ordering bug tracked in ROADMAP |
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
