# SkyBlock Plugin — Feature Roadmap

Prioritized by player-facing impact and dependency order. Items within a phase can be parallelized; phases themselves are sequential — later phases depend on earlier foundations.

> **Structural note (cut from all phases):** The repo has 2–4 duplicate Manager/Command classes for nearly every system (`core`, `plugin`, `skyblock-core`, and sometimes a standalone module). Before each feature below is "complete", the duplicate variants must be reconciled into a single canonical implementation. See `docs/STATUS.md` for the full inventory.

---

## Phase 1 — Economy & Core Infrastructure

*Goal: a stable, playable economic loop. Everything else is blocked on a working profile + item + coin system.*

- [ ] **Profile system** — consolidate the four `ProfileManager` variants; enforce deterministic active-profile lookup (Iterator.next() bug fixed by Vega — verify all variants)
- [ ] **SkyBlock item system** — canonical `SkyBlockItem` with NBT storage for rarity, stats, reforge, enchants, and UUID; `ItemManager` returns tagged `ItemStack`
- [ ] **Coin economy** — single `EconomyManager` backed by profile balance; remove duplicate `economy`/`banking`/`bank` managers; wire all spend/earn paths through it
- [ ] **Bank** — deposit/withdraw with per-profile interest tick (0.2 % daily compounding); `/bank history` already merged — confirm all three command variants show it
- [ ] **Auction House** — BIN and bid listing; expiry/claim flow; price-history log per item type; search + filter by category and rarity
- [ ] **Bazaar** — buy-order and sell-order matching; order fulfillment on the next server tick after a counter-order is placed; per-product price graph (5-item moving average)
- [ ] **YAML persistence audit** — every manager that already calls `load()`/`save()` must be verified to round-trip all fields; add `load`/`save` to any manager still missing them
- [ ] **Scoreboard** — live sidebar: SkyBlock date, purse, bits, networth, and zone; consolidate `ScoreboardManager`/`SkyBlockScoreboard` duplicates

---

## Phase 2 — World Zones & Gathering Skills

*Goal: players can explore zones, gather resources, and level the seven gathering skills.*

- [ ] **World generation** — void Private Island (schematic paste on first join); static Hub world with spawn point; per-zone worlds for Deep Caverns, Gold Mine, Coal Mine, Spiders' Den, Blazing Fortress, The End, Crimson Isle, Crystal Hollows, and Garden
- [ ] **Zone mob spawning** — `CustomMobManager` populates each zone with its canonical mobs; per-mob health pool, damage, and loot table; respawn timers per zone
- [ ] **Skill XP formulas** — correct cumulative XP thresholds for all 13 skills (Farming/Mining/Combat/Foraging/Fishing/Enchanting/Alchemy/Carpentry/Runecrafting/Social/Taming/Dungeoneering/Catacombs)
- [ ] **Skill bonus unlocks** — each skill level grants its documented stat bonus (e.g. Farming 1 = +2 Farming Fortune; Mining 1 = +1 Mining Speed); fire notification on level-up
- [ ] **Farming** — custom crop XP rates per crop; `FarmingFortune` stat multiplies double-drop rate; garden-plot crop boosted separately (Phase 4)
- [ ] **Mining** — ore XP by tier; Mining Speed stat reduces break time; pristine/magic-find drops from Crystal Hollows ores
- [ ] **Foraging** — log XP by wood type; Foraging Fortune stat; Treecapitator enchant interaction
- [ ] **Fishing** — sea-creature spawn by fishing power and zone; trophy-fish catch rates from `TrophyFishManager`; SkyBlock-specific fishing loot tables
- [ ] **Collections** — `CollectionListener` increments count on item pick-up; unlock recipes/items/perks at collection-tier thresholds; consolidate four `Collection*` class pairs
- [ ] **NPC shops** — each zone NPC sells its canonical item set; `NPCManager` maps NPC entity to shop inventory; click opens inventory GUI

---

## Phase 3 — Progression Systems

*Goal: the four major progression loops are playable end-to-end.*

### 3A — Slayer
- [ ] **Slayer boss spawning** — `SlayerManager.startQuest()` actually spawns the custom boss entity with correct health and abilities for each tier (Zombie/Spider/Wolf/Enderman/Blaze)
- [ ] **Slayer XP and tier unlock** — XP gates per tier (Zombie T1 = 5 kills, T2 = 20…); correct XP reward per tier kill
- [ ] **Slayer drop tables** — drop Slayer-exclusive items (RNGesus token, Shard, special armor pieces) at correct rates per tier

### 3B — Dungeons
- [ ] **Catacombs class system** — Healer / Mage / Berserk / Archer / Tank; class XP pool separate from Dungeoneering; stat bonuses per class level
- [ ] **Dungeon room generation** — schematic-based room placer for each floor; puzzle rooms (Tic-Tac-Toe, Bomb Defuse, Trivia, Creeper Beams, Water Board, Blaze, Three Weirdos); secret locations per room
- [ ] **Floor boss rooms** — Bonzo (F1), Scarf (F2), The Professor (F3), Thorn (F4), Livid (F5), Sadan (F6), Necron (F7); master-mode variants with boosted stats
- [ ] **Dungeon scoring** — time bonus, damage dealt, skill score, speed tier, explorer score; S+ detection; chest tier by score
- [ ] **Essence drops** — Undead/Spider/Dragon/Gold/Diamond/Ice/Wither Essence drop from bosses and rooms; `EssenceManager` upgrades item tier on `essence upgrade` call
- [ ] **Catacombs leaderboard** — fastest F7 time, highest score; shown via `/leaderboard dungeon`

### 3C — Pets
- [ ] **Pet XP thresholds** — per-rarity XP table (Common/Uncommon/Rare/Epic/Legendary); level 1–100 scaling
- [ ] **Pet stat scaling** — each pet's stats grow linearly with level; apply to player stat sheet while pet is active
- [ ] **Pet abilities** — passive and active abilities fire on the correct game events (e.g. Elephant ability on crop break, Griffin ability on mob kill)
- [ ] **Pet leveling GUI** — show current XP, XP to next level, and candy slots in `/pets` inventory

### 3D — Minions
- [ ] **Minion placement** — right-click air/ground with minion item places minion entity; enforce slot cap from `IslandManager` upgrade level
- [ ] **Minion tick system** — each placed minion generates its resource at tier-based interval (T1 = slowest); stores in internal storage up to capacity; fires `MinionFullEvent` when full
- [ ] **Minion upgrades** — fuel items (Enchanted Lava Bucket, Catalyst, etc.) double/triple speed; storage upgrades (Small/Medium/Large Storage) increase capacity
- [ ] **Minion GUI** — right-click placed minion opens upgrade/collect/fuel inventory

---

## Phase 4 — Advanced Systems

*Goal: high-end content loop is functional; the "end-game" is reachable.*

- [ ] **Custom enchantments** — all SkyBlock enchants with effect implementations: Telekinesis (auto-pickup), Cultivating (crop XP), Dedication (combat XP), Scavenger, Looting I–IV, First Strike, Giant Killer, Thunderlord, Sharpness I–VII, Power I–VII, Luck of the Sea, Angler, Expertise, Corruption, Soul Eater, Rainbow (and all others in `EnchantingManager`)
- [ ] **Reforge system** — each reforge stone applies a named set of stat deltas per rarity; `ReforgeManager.applyReforge()` patches item NBT and lore; Anvil GUI for applying
- [ ] **Armor set detection** — `ArmorSetManager` checks all four equipment slots each time armor changes; applies set bonus to stat sheet while full set is worn; removes on any slot change
- [ ] **Item abilities** — active abilities (right-click) consume mana and fire ability effect; passive abilities always active while in hand or armor slot
- [ ] **HOTM tree** — node unlock order enforced (unlock parent before child); each node applies its documented powder/speed/fortune/token bonus; `HOTMManager.unlockNode()` validates cost
- [ ] **Crystal Hollows** — zone-specific crystal nucleus, sub-zones (Jungle, Precursor Remnants, Goblin Holdout, etc.), crystal completion rewards
- [ ] **Kuudra tiers** — Normal / Hot / Burning / Fiery / Infernal; material gathering pre-fight phase; boss ability rotation; key crafting via Crimson Isle materials
- [ ] **Crimson Isle** — Mage/Barbarian faction reputation; faction quests and rewards; Vanquisher spawning; Kuudra key fragments
- [ ] **Garden** — plot progression (unlocking tiers 1–9); visitor system (visitors arrive on a timer, offer trades); Jacob's Contest scheduler and medal rewards; Farming Weight calculation
- [ ] **Pest system** — pests spawn in Garden plots; pest kills count toward Bestiary; `PestManager` tracks active infestation per plot; Vaporizer item clears pests
- [ ] **Trophy Fishing** — zone-specific catch tables (Mist, Lava, Crystal Hollows); bait item modifiers; `TrophyFishManager` persists trophy collection; Bronze/Silver/Gold/Diamond tiers
- [ ] **Sacks** — each sack type holds a specific item category up to capacity; auto-pickup from `CollectionListener`; `SackManager` merges with Collection count on pickup
- [ ] **Accessory bag** — deduplicate accessories by family (highest tier wins); compute combined magic-power stat; update player stat sheet on bag change
- [ ] **Jacob's Contest** — 3-crop rotation scheduled every 20 min SkyBlock time; farming weight threshold for Gold/Platinum/Diamond medals; medal-to-bit conversion

---

## Phase 5 — Social, Polish & Secondary Content

*Goal: multiplayer features and quality-of-life that make the server feel complete.*

- [ ] **Co-op profiles** — profile stores list of co-op UUIDs; all listed players share island and bank; invite/kick via `/profile coop invite <player>`
- [ ] **Mayor elections** — 5-day election cycle (SkyBlock time); candidate perks listed; winning mayor's perks apply server-wide via `MayorManager.applyPerks()`; Minister perk also active
- [ ] **Chocolate Factory** — rabbit collection (Common–Mythic); production rate scales with total rabbits; prestige resets for multiplier; `ChocolateFactoryManager` tick every real-world hour
- [ ] **Rift dimension** — separate void world; Timecharm mechanic (Rift time ≠ server time); Rift-exclusive mobs and items; `RiftManager` handles dimension switch and timer
- [ ] **Booster Cookie** — 4-day buff granting +10 % Skill XP, +25 % Magic Find, +10 % Pet XP, and AH/Bazaar slot bonuses; `CooldownManager` tracks remaining duration per player
- [ ] **SkyBlock Level** — aggregate score from skills, slayer, dungeon progress, collections, HOTM, bestiary, minions placed, fairy souls, misc achievements; displayed in `/profile` and scoreboard
- [ ] **Leaderboard** — per-category rankings (SkyBlock Level, each Skill, Slayer XP, Dungeon fastest time, Fishing trophies); updated async every 5 minutes
- [ ] **Guild system** — invite/kick/ranks (Member/Officer/Guild Master); guild XP from member skill gains; guild quests; `/guild` subcommands consolidated from three `GuildManager` variants
- [ ] **Housing** — plot build permissions; visitor mode toggle; housing warp via `/visit <player>`; block palette restrictions
- [ ] **Notification system** — action bar flash on level-up, collection tier, achievement; boss bar for dungeon HP and active slayer; chat broadcast for S+/server first
- [ ] **Action bar HUD** — real-time `❤ HP/MAX_HP  ✦ MANA/MAX_MANA  🛡 DEF` display updated every 2 ticks; `HUDManager` renders per-player
- [ ] **Bestiary milestones** — kill-count tiers per mob family grant permanent Magic Find, Health, and Speed bonuses; `BestiaryManager` awards bonus on threshold cross
- [ ] **Fairy Souls** — 240 soul locations (stored as config); collecting all 5 in a milestone group grants permanent HP; `/fairysouls` shows count and next reward
- [ ] **Museum** — donate SkyBlock items to museum categories; donation count contributes to SkyBlock Level; special rewards at collection milestones
- [ ] **Daily reward** — escalating coin/item rewards for consecutive login streaks; reset on missed day; `DailyRewardManager` already exists — verify streak logic and item table
- [ ] **Chat channels** — All/Party/Guild/Co-op; `/chat <channel>` routes messages; color-coded prefixes; `ChatManager` filters profanity list
- [ ] **Module consolidation** — merge all duplicate Manager/Command pairs into the `skyblock-core` canonical stack; delete orphaned standalone modules and `plugin` variants; update `SkyBlockPlugin.onEnable()` to register only the consolidated classes

---

## Out of Scope (intentional deviations from Hypixel)

- Network-layer features (SkyBlock Network, housing servers, co-located lobbies) — single-server Paper plugin only
- Real-money cosmetics or rank perks (MVP++, etc.) — no monetization layer
- Replay system — not feasible on Paper without a dedicated replay mod
