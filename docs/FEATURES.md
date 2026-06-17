# Features

> One-line description of every implemented feature manager under
> **`com.skyblock.core.manager`**. Each manager is the single authoritative
> owner of its gameplay state — import it directly, never a copy. For the
> package-layout rules and module map see [`ARCHITECTURE.md`](ARCHITECTURE.md).

| Manager | Description |
|---------|-------------|
| `AccessoryManager` | Tracks the rarity of each accessory (talisman) a player owns. |
| `AccessoryBagManager` | Per-player accessory bag contents and the magical-power stat bonuses they grant. |
| `AttributeManager` | Crimson Isle attribute shards and per-attribute levelling. |
| `BankManager` | Per-player bank balance, interest, and deposits/withdrawals. |
| `BazaarManager` | Bazaar order book with instant buy/sell pricing and order fulfillment. |
| `BestiaryManager` | Per-player mob-kill counts and bestiary tier progression. |
| `BingoManager` | Per-player Bingo event card progress, goals/tiers, and bingo-point rewards. |
| `CalendarManager` | SkyBlock in-game calendar, seasons, and scheduled events. |
| `CarnivalManager` | Per-player Carnival hub state, games, and rewards. |
| `CollectionManager` | Per-player resource collection progress and tier-unlock thresholds. |
| `CrimsonIsleManager` | Crimson Isle coordinator — Kuudra tiers and faction questlines. |
| `CrystalHollowsManager` | Dwarven Mines / Crystal Hollows zones, gemstones, and powder. |
| `DragonManager` | End dragon fights: summoning eyes, dragon spawns, and contribution rewards. |
| `DungeonClassManager` | Permanent passive stat bonuses each dungeon class grants. |
| `DungeonManager` | Catacombs floors (F1–F7 + Master Mode) and dungeon runs. |
| `EconomyManager` | Player coin balances (purse and bank). |
| `EnchantingManager` | Enchantments for the enchanting-table skill system. |
| `EnchantmentManager` | Per-player custom enchantment tracking. |
| `EssenceManager` | Per-player essence balances per essence type and shop upgrades. |
| `EventManager` | Per-player SkyBlock event participation and scores. |
| `ExperimentationTableManager` | Superpairs / Chronomatron / Ultrasequencer minigames. |
| `FairySoulManager` | Fairy souls scattered across each island and exchange bonuses. |
| `FishingManager` | Fishing skill progression, sea creatures, and loot rolls. |
| `ForgeManager` | Crimson Isle forge item recipes and processing slots. |
| `GardenManager` | Garden plot levels, Jacob's farming contests, and crop milestones. |
| `HotmManager` | Heart of the Mountain progression: perk nodes, tiers, and powder. |
| `IslandManager` | Per-player private islands: creation, upgrades, and visiting. |
| `ItemAbilityManager` | Item and full-set abilities with mana costs. |
| `MayorManager` | Mayor candidates, election cycle/voting, and the active mayor's perks. |
| `MiningManager` | Mining skill progression, speed, and fortune bonuses. |
| `MinionManager` | Per-player minions: types/tiers, fuel, upgrade slots, and auto-sell. |
| `MobManager` | Registry for SkyBlock custom mobs. |
| `MuseumManager` | Per-player Museum donation progress and rewards. |
| `NetworthManager` | Estimates item and player coin net worth from base value plus modifiers. |
| `PartyManager` | Player parties: invites, membership, and leadership. |
| `PetManager` | Per-player pet collections, active pets, and pet XP/levelling. |
| `PotionManager` | Brewing-stand potions and active potion effects. |
| `QuestManager` | Per-player quest tracking and completion. |
| `ReforgeManager` | Item reforges and reforge-stat application. |
| `RepairManager` | Durability-based coin repair costs for tools and armor. |
| `ReputationManager` | Crimson Isle faction-reputation tracking. |
| `RiftManager` | Per-player Rift dimension state and currency. |
| `RuneManager` | Runes applied to items, including rune levels and visuals. |
| `SackManager` | Per-player Sack item storage. |
| `ShopManager` | NPC shop registry with buy/sell pricing and transactions. |
| `SkillManager` | Per-player skill XP and levels for every skill. |
| `SlayerManager` | Slayer quests: boss spawn costs, tiers, and slayer XP/rewards. |
| `StatManager` | Per-player stats per stat type. |
| `StorageManager` | Personal storage and backpack pages. |
| `TradeManager` | Active peer-to-peer player trade sessions. |
| `TrophyFishManager` | Trophy fishing catches and tiers. |
| `WardrobeManager` | Per-player wardrobe of named armor outfits with full-set swapping. |

See the [root README](../README.md) for the project overview and command list.
