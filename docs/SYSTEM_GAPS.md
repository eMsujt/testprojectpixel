# SkyBlock System Gaps — what isn't 1:1 with Hypixel

Companion to `MENU_FIDELITY.md`. The menus are matched; this tracks the **gameplay
systems** behind them. Based on a full code audit (Aug 2024).

## Headline
The plugin is a **broad, data-model-rich skeleton that is gameplay-poor**. Almost
every system has well-formed enums, persistence, commands and menus — but the core
SkyBlock loop (*do activity → earn currency/progress → spend → gain power that
affects the activity*) is **unwired at one or more links**.

Three recurring root causes:
1. **Methods with no production call-sites** — reward/effect code exists but is only
   reachable from tests or OP debug commands (skill perks, collection rewards, HOTM
   perks, slayer boss spawn, pest/visitor spawn).
2. **Duplicate/competing managers** — one wired, one dead (Mining/Fishing/Combat XP
   double-counts; two Jacob managers; two trophy-fish enums; `AccessoriesManager`
   and `ItemAbilityManager` are dead).
3. **No real custom worlds** — only the Private Island is generated; every other
   area (Hub, Crystal Hollows, Garden, dungeon floors, Crimson Isle, Rift) is a
   warp string or an in-memory enum, so anything location-based (fairy souls,
   gemstones, sea creatures, secrets) can't actually be found by playing.

## The single biggest combat gap
`EquipmentListener` feeds the combat stat store **only** by regex-scraping stat
lines from worn armor + held item lore. So **enchants, reforges, and accessories
are catalogued but never affect combat** — they live in side maps nothing reads
back. Reaching combat now: **pet bonuses**, **accessory tuning**, **armor set
bonuses**, **per-accessory stats** (highest-tier-per-family de-dup), **skill-level
bonuses**, **fairy-soul bonuses**, and **reforge stats** (stamped on the item via
PDC at the anvil, read per gear piece) — all via `recompute`. **Damage enchants**
(Sharpness + Smite/Bane/Ender Slayer) now apply too — item-based, read from the
weapon's lore in `CombatListener` — incl. the conditional Execute/Prosecute and
the capped Giant Killer. Remaining: on-hit/effect enchants (Thunderlord, Life Steal,
Looting…), First Strike, and armor stat-enchants (Growth/Protection → `recompute`).

## Status by system

| System | Status | Key gaps |
|---|---|---|
| **Item identity** (texture/name/lore/rarity/PDC) | **1:1** | ~8,355 real NEU items; nothing to fix |
| **Stat system + base melee formula** | **1:1 core** | full damage stack missing the enchant/ability multiplier buckets, True Defense + effective-HP unapplied |
| **Skills** | PARTIAL | XP curves exact; perks placeholder + **never applied**; XP double-counted |
| **Collections** | PARTIAL | thresholds right; **tier-up coins now paid** (recipe/sack unlocks still inert); ~52/70+; fishing *does* increment via `CollectionListener.onFish` (by vanilla caught item) |
| **Slayers** | STUB | **boss never spawns**; no abilities/RNG meter; menu non-interactive; drops wrong |
| **Dungeons/Catacombs** | STUB | **no instance/bosses/abilities/puzzles**; score is a typed arg; class XP math is the only real part |
| **Mining (HOTM/powder/CH)** | PARTIAL | perks inert; **powder never earned**; tier cap 7≠10; Crystal Hollows fake; no gemstone slots |
| **Farming (Garden/Jacob)** | PARTIAL | no Garden island; 9 plots≠24; pests/visitors never spawn; **Farming Fortune disconnected** |
| **Fishing** | PARTIAL | sea creatures = wrong vanilla mobs, no HP/drops; trophy fishing faked; treasure/collection unlinked |
| **Enchantments** | PARTIAL | **damage enchants now apply** item-based (Sharpness/Smite/Bane/Ender Slayer + Execute/Prosecute + Giant Killer, weapon-lore-read in `CombatListener`); on-hit/effect enchants + First Strike + armor stat-enchants still TODO |
| **Reforges + stones** | PARTIAL | reforge stats **now reach combat** (PDC-stamped on the item, read in `recompute`); stones/item-type pools/rarity-scaling still TODO |
| **Accessories / MP / Maxwell** | PARTIAL | per-accessory stats **now apply** (highest-tier-per-family de-dup) + tuning; MP→stat curve absent; ~50 accessories vs 600+ |
| **Pets** | PARTIAL | 68/~70 roster + real XP; **0 abilities fire**; pet items unequippable |
| **Armor set bonuses** | PARTIAL | flat set bonuses **now applied** to combat; dynamic/conditional perks (Superior +5% all, dungeon-only, etc.) still flavor |
| **Bazaar** | PARTIAL | **instant buy/sell now functional** (coins + inventory via `BazaarProductMenu`; base-price fallback when the book is empty); limit-order placement + bigger roster (~62/150+) still TODO |
| **Auction House** | PARTIAL | good model; **no coin/inventory wiring**; expiry task now started (this PR) |
| **Bank** | PARTIAL (best economy) | interest only on `/bank interest`, no auto-timer; tiers approximate |
| **Minions** | PARTIAL | real production loop; ~52/60+ types; generic per-tier tables; no crafting-to-obtain |
| **NPC shops** | PARTIAL (best-wired) | real coin deduction + item delivery; ~10/25+ shops, tiny catalogs |
| **World / islands** | STUB | only Private Island is a real world; everything else warp/enum |
| **Fairy Souls** | PARTIAL | 227/~240, **no real coordinates** (admin-placed); rewards simplified |
| **Calendar/events** | PARTIAL | **no real-time advance**; 5 events, **Dark Auction missing** |
| **Bestiary / Museum / Quests / SB Leveling** | PARTIAL | small rosters, inert rewards, generic content, incomplete XP sources |
| **Coins on death** | not 1:1 | 5% of purse vs Hypixel's ~50% |

## Priority fixes (high leverage, roughly safe)
1. **Combat wiring** (biggest fidelity win): feed reforge + accessory-MP + armor-set
   stats into `EquipmentListener.recompute`. Balance-sensitive — playtest.
2. **Reward wiring**: call the existing (dead) skill-perk and collection-tier reward
   paths from the live listeners. Watch the XP double-count first.
3. **De-dup managers**: collapse Mining/Fishing/Combat double-XP; pick one Jacob /
   trophy-fish / accessories manager and delete the dead twin.
4. **Auto-timers**: bank interest, calendar real-time advance.
5. **Content rosters**: grow collections (→70+), minions (→60+), bazaar products
   (→150+), NPC shops (→25+) toward Hypixel.

## Things that need real worlds (largest effort, do last)
Slayer/Dungeon/Kuudra boss instances, Crystal Hollows + Garden + Rift worlds,
in-world fairy souls & gemstones. These need schematic/worldgen work, not just
manager wiring.
