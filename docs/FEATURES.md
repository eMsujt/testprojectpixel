# Feature Managers

Every implemented gameplay system is owned by a single canonical manager. Most
live under **`com.skyblock.core.manager`**; a few large systems keep their own
feature sub-package (noted below). Import the canonical class directly — never a
copy. See [`ARCHITECTURE.md`](ARCHITECTURE.md) for the package-layout
conventions and the one-canonical-home rule.

| Manager | One-line description |
|---------|----------------------|
| `AccessoryManager` | Tracks the rarity of each accessory a player owns and its stat multiplier. |
| `AccessoryBagManager` | Holds each player's accessory bag contents and the passive stat bonuses they provide. |
| `AttributeManager` | Crimson Isle attribute shards and per-attribute levelling via shard fusing. |
| `BankManager` | Per-player bank balance, interest, and deposits/withdrawals. |
| `BazaarManager` | The bazaar order book — buy/sell orders and instant-buy/sell pricing. |
| `BestiaryManager` | Per-mob kill counts, milestone tiers, family completion, and stat bonuses. |
| `BingoManager` | Per-player Bingo event progress over regular and community card goals. |
| `CalendarManager` | The in-game calendar — seasons, day cycle, and recurring scheduled events. |
| `CarnivalManager` | Carnival hub mini-games, tickets/tokens, and rewards. |
| `CollectionManager` | Per-player collection progress and tier-unlock thresholds. |
| `CrimsonIsleManager` | Crimson Isle coordinator tying together Kuudra and faction reputation. |
| `CrystalHollowsManager` | Dwarven Mines / Crystal Hollows zones, crystal counts, and powder balances. |
| `DragonManager` | The End dragon fight — summoning eyes, damage, and completion credit. |
| `DungeonClassManager` | Permanent passive stat bonuses each dungeon class grants as it levels. |
| `DungeonManager` | Catacombs floors F1–F7 plus Master Mode and dungeon runs. |
| `EconomyManager` | Player coin balances (purse and bank). |
| `EnchantingManager` | Canonical enchant registry with level caps, conflicts, and ultimates. |
| `EnchantmentManager` | Per-player enchantment tracking (delegates to `EnchantingManager`). |
| `EssenceManager` | Per-player essence balances, essence-shop perks, and essence-gated unlocks. |
| `EventManager` | Per-player server-event participation and scores. |
| `ExperimentationTableManager` | Superpairs, Chronomatron, and Ultrasequencer minigames. |
| `FairySoulManager` | Fairy soul discovery per island and the permanent stat bonuses earned. |
| `FishingManager` | Fishing skill progression and weighted loot rolls. |
| `ForgeManager` | Forge recipe catalogue and per-player active forge slots. |
| `GardenManager` | Garden plot levels, visitor counts, and crop upgrade levels. |
| `HotmManager` | Heart of the Mountain perk-tree nodes, tiers, and powder balances. |
| `IslandManager` | Per-player private islands — creation, members, upgrades, and visiting. |
| `ItemAbilityManager` | Item and full-set abilities with mana costs and cooldowns. |
| `MayorManager` | Mayor candidates, the election cycle, and active-mayor stat perks. |
| `MiningManager` | Mining skill progression and mining-speed bonuses. |
| `MinionManager` | Per-player minions, fuel, upgrade slots, and hopper auto-sell. |
| `MobManager` | Registry of custom mobs loaded from `mobs.yml` with per-mob stats. |
| `MuseumManager` | Per-player Museum donation progress by category. |
| `NetworthManager` | Estimates item coin networth from base value plus enchants/reforges/stars. |
| `PartyManager` | Player parties — invites, membership, and leadership. |
| `PetManager` | Per-player pet collections, active pets, and pet XP/levelling. |
| `PotionManager` | Brewing-stand recipes and active levelled, timed potion effects. |
| `QuestManager` | Per-player quest tracking and completion. |
| `ReforgeManager` | The reforge catalogue and the active reforge on each held item slot. |
| `RepairManager` | Durability-based coin repair costs for tools and armor. |
| `ReputationManager` | Crimson Isle faction reputation (Mages/Barbarians) and tiers. |
| `RiftManager` | The Rift dimension — areas, Motes currency, timecharms, and Enigma souls. |
| `RuneManager` | Rune registry, rune levels, and applying/removing runes on items. |
| `SackManager` | Sack storage with auto-pickup and per-item capacity tiers. |
| `ShopManager` | NPC shop registry with buy/sell pricing and YAML-backed persistence. |
| `SkillManager` | Per-player skill XP and levels for every skill. |
| `SlayerManager` | Slayer quest tiers, boss spawn cost, and rewards. |
| `StatManager` | Per-player stats — intrinsic base values plus accumulated bonuses. |
| `StorageManager` | Personal-storage coordinator across backpack and storage pages. |
| `TradeManager` | Active peer-to-peer trade sessions. |
| `TrophyFishManager` | Trophy-fishing catches and their Bronze/Silver/Gold/Diamond tiers. |
| `WardrobeManager` | Named armor outfits and full-set swapping. |
