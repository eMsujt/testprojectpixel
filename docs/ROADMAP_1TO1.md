# Master 1:1 Roadmap

The single plan for making this server 1:1 with Hypixel SkyBlock. Worked top-down,
one PR per item, each build-verified. Detail lives in `MENU_FIDELITY.md` (UI) and
`SYSTEM_GAPS.md` (gameplay). Legend: `[x]` done · `[~]` partial · `[ ]` todo ·
`[!]` blocked/needs-decision.

---

## Phase 0 — UI / Menus  ✅ (complete)
- [x] SkyBlock Menu + all reachable submenus matched to wiki slots (16 menus)
- [x] Secondary menus: Catacombs Gate, Museum (8 cats), Kuudra, Forge, Sacks,
      Dungeon Classes, Commissions, Chocolate Factory, Storage hub
- [x] Consistency: black borders + **full black backgrounds** everywhere
- [x] XP/reward feedback → action bar (no chat spam)
- [x] Scoreboard sidebar + tab banner 1:1
- [~] Per-menu pixel-perfect for UNVERIFIABLE menus — pending user's concrete
      examples (wiki documents no slots for them)

## Phase 1 — Combat fidelity (gear actually affects stats)  ← ACTIVE
The chokepoint is `EquipmentListener.recompute`; everything must feed it.
- [x] Armor full-set bonuses → stats
- [x] Accessory stats → stats (highest-tier-per-family de-dup)
- [ ] **Reforges → stats** — stamp reforge on the item (PDC) + write its stats to
      lore so the lore-scraper picks them up (replaces the fragile per-slot map)
- [ ] **Enchants** — register the effect listeners, store enchants on the item,
      implement the missing effect math
- [ ] Full damage stack — additive enchant-multiplier bucket, True Defense,
      effective-HP, the +15% melee bonus
- [ ] Pet abilities (held/active) actually fire

## Phase 2 — Progression loops (activity → reward → power)
- [ ] De-dup double-XP (Mining/Fishing/Combat accrue to two managers)
- [x] Skill level-up perks → real stats — `SkillManager.getStatBonuses` wired into
      `recompute`; refreshes on join, gear change, **and any inventory click**, so a
      mid-session skill-up reflects on the next inventory interaction (responsive)
- [x] Fairy-soul stat rewards → real stats — `FairySoulManager.getStatBonuses` wired
      into `recompute` (same refresh triggers)
- [~] Collection tier rewards — **coin reward now granted on tier-up** (the dead
      `grantTierUpRewards` is wired into `addItems`); recipe/sack/minion-slot
      unlocks still inert
- [ ] SkyBlock Leveling: wire all XP sources (bestiary, quests, minions, slayer…)
- [ ] Fishing/Foraging XP feed the canonical SkillManager (not siloed managers)

## Phase 3 — Economy wiring (trading touches coins + inventory)
- [ ] Bazaar GUI: buy/sell + order placement; debit/credit coins; move items
- [ ] Auction House: debit coins on bid/BIN, charge fees, remove/return items
- [ ] Bank interest auto-timer (currently only on `/bank interest`)
- [ ] Coins-on-death tuned toward Hypixel (currently 5%)
- [ ] Drain claim escrow back to players (Bazaar/AH claim flow)

## Phase 4 — Content rosters (grow toward Hypixel sizes)
- [ ] Collections → 70+ (Mithril, Gemstone, Hard Stone, Glacite, …)
- [ ] Minions → 60+ types, real per-type tier tables, crafting-to-obtain
- [ ] Bazaar products → 150+ (enchanted tiers, essences, gemstones, …)
- [ ] NPC shops → 25+ with real catalogs
- [ ] Enchantments → full Hypixel set incl. ultimates
- [ ] Reforge stones → real items + recipes/drops

## Phase 5 — Gameplay spawning / instances
- [ ] Slayer: real boss entities, abilities, RNG meter, correct drops, interactive menu
- [ ] Dungeons: real instance (rooms, doors, secrets, bosses, class abilities, blessings)
- [ ] Sea creatures: correct mobs, HP, drops, boss creatures
- [ ] Garden pests + visitors actually spawn; Farming Fortune → StatManager
- [ ] HOTM powder earned by mining; Crystal Hollows nucleus loop; gemstone slots

## Phase 6 — Worlds (largest effort)
- [!] Real generated areas (Hub, Spider's Den, End, Dwarven Mines, Crystal Hollows,
      Garden, Crimson Isle, Rift, Dungeon Hub) — needs schematic/worldgen pipeline
- [ ] Fairy souls placed in-world (~240 real coordinates)
- [ ] Gemstone slots in gear

## Phase 7 — Events / timers
- [ ] Calendar advances on a real ~20-min-day timer
- [ ] Add missing events (Dark Auction, Mining/Fishing Festival, Mythological Ritual)
- [ ] Mayor election on the real cycle, tied to the calendar
- [x] Current mayor shown in Calendar and Events

## Phase 8 — Known blocked / out-of-scope here
- [!] **Tab columns** — needs a player-list packet layer; no packet lib supports
      the `paper-api 26.x` target. Banner done.
- [!] **Enchant Item / Runic Pedestal** functional item-input — needs in-game
      playtesting before trusting with items.

---

### Working order
Phase 1 → 2 → 3 give the biggest "feels like Hypixel" return for the least risk and
are doable without worldgen. Phase 4 is steady additive content. Phases 5–6 are the
big builds. Each item ships as its own green PR; this file is updated as items land.
