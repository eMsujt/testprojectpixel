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
| DungeonManager | `com.skyblock.core.dungeon.DungeonManager` | `dungeons`, `dungeon`, `skyblock-dungeons`, `core`, `plugin` | `dungeons` module most complete |
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
