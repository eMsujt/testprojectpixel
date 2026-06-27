# Master 1:1 Roadmap

The single plan for making this server 1:1 with Hypixel SkyBlock. Worked top-down,
one PR per item, each build-verified. Detail lives in `MENU_FIDELITY.md` (UI) and
`SYSTEM_GAPS.md` (gameplay). Legend: `[x]` done · `[~]` partial · `[ ]` todo ·
`[!]` blocked/needs-decision.

---

## Recently landed (2026-06-26/27)
- **Full 1:1 data audit (PRs #4390–#4409)** — values now wiki-sourced for 22 systems:
  pet XP (was 20–95× too fast), minion action rate (~20× too fast), per-rarity reforge
  stat tables, bracketed bank interest, skill XP curves (Fishing/Alchemy→50, Foraging→54),
  Reputation tiers (5, no negatives), Garden 46-tier milestones, Bazaar instant-sell tax +
  3-tier fee model, real 18 Trophy Fish, 9 Rift zones, Mayor perk names + 372-day cycle,
  HOTM/enchant/bestiary values. Wiki re-verification confirmed Museum (skill categories)
  and Reputation are 1:1. See `SYSTEM_GAPS.md`.
- **Functional NPC placement (PRs #4410–#4412)** — `/setnpclocation <npc>` (tab-complete,
  `skyblock.admin`) places a feature NPC that opens its menu on right-click: banker→Bank,
  auction_master→AH, bazaar→Bazaar, museum→Museum, blacksmith→Reforge, pet_sitter→Pets,
  wardrobe→Wardrobe, guide→hub. Persisted to `npc_locations.yml`, respawn-on-enable,
  2-block anti-stacking, block-centre snap. Removed the old dead NPC ArmorStand path +
  redundant `NpcType` enum (the live `/npc list|shop|buy` registry stays).
- **Reforge now costs coins (PR #4413)** — the anvil was reforging **free**; it now charges
  the rarity-scaled cost (250→25k) and rejects if unaffordable. Divine price 25k→15k. Random
  roll kept (wiki: basic reforges are random). Rarity lore-reader centralised as
  `Rarity.fromItem`. **Menu anti-dupe confirmed a general layered system** (default-cancel +
  `MenuItemListener` marker guard; only Crafting/Reforge interactive — pending dupe playtest).
- **Mining XP curve (PR #4416)** — `MiningManager` used a fabricated `50·n²` curve (level 50 at
  ~125k XP); now uses the canonical skill table (`SkyBlockXP.STANDARD_CUMULATIVE`, L50 = 55.17M).
- **Pet stats per-pet (PR #4417)** — pet stat bonuses were category-blanket (every Combat pet =
  +0.5 Strength, etc.), wrong for many pets. Now sourced per-pet from NEU `petnums.json` (56 pets,
  real multi-stat); all of a pet's stats apply. Pet *abilities* still don't fire (see Phase 1).
  Both Mining + Pet are combat-affecting — **playtest pending**.
- **UI 1:1 audit (PRs #4419–#4421)** — swept ~28 secondary menus against wiki `{{UI}}` pages.
  Removed **5 dead duplicate menus** (#4419); stripped color codes from **11 window titles** +
  corrected labels (Runecrafting Table→Runic Pedestal, Garden→Desk, Trophy Fish→Trophy Fishing,
  Accessory Bag Go-Back→"To Your Bags") (#4420); fixed **Melody's Harp** to the real 11 songs /
  +26 Int (#4421). The audit also found several menus are **structurally** different from Hypixel
  (not just mislabelled) — see the Phase 0 rewrite backlog below.

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
- [ ] **Secondary-menu structural rewrites (from the 2026-06-27 `{{UI}}` audit)** — these menus
      are functionally different from their Hypixel counterparts and need rebuilding, not
      relabelling (titles already fixed in #4420):
  - **Enchant Item** — real GUI is an item-input interface (open slot + Gray Dye prompt +
    Enchantment Table + Bookshelf Power + Enchantments Guide + Hopper sort), not a book-grid browser.
  - **Runic Pedestal** — rune-fusion station (Item To Upgrade / Rune to Sacrifice / End Portal
    apply / Barrier centre / Cauldron Rune Removal), not a rune-count grid.
  - **Garden "Desk"** — Configure Plots / Garden Upgrades / SkyMart / Milestones / Skins / Garden
    Time / Set Speed per Crop / Farming Toolkit, not the current visitor-queue + crop grid.
  - **Island Management** — Island Category / Name / Ranks / Properties / Alex's Island / Guests,
    not an upgrades panel.
  - **Minion** — single-minion window (Ideal Layout, Fuel, Automated Shipping, 15 storage slots,
    Collect All, Quick-Upgrade, Pickup), not a 12-minion grid.
  - **Essence Shop** — hub of per-essence sub-shops with real "Forbidden ___"/essence perks, not a
    flat balance+perk list.
  - **Backpack / Ender Chest** — real container storage at the tier's slot count (incl. Jumbo = 45)
    with page navigation, not read-only paper-list renderings.
  - **Fast Travel** — destination set/order, name colours (mostly green), and footer (Island
    Browser / Go Back / Advanced Mode) differ from the wiki layout.
  - **Fairy Souls** — per-island soul counts are wrong (sourced: Hub 80, Spider's Den 19, Crimson
    Isle 29, Deep Caverns 21, Park 12, Farming Islands 20, Dwarven Mines 15, Rift 1) — a data fix.
  - **CombatMenu / StatsMenu** — no standalone Hypixel equivalent (stats live in the profile menu);
    decide keep-as-QoL vs remove.

## Phase 1 — Combat fidelity (gear actually affects stats)  ✅ (core complete)
The chokepoint is `EquipmentListener.recompute`; everything must feed it. All major
gear/progression stat sources + the damage-enchant bucket now feed combat; the
remaining items below are polish/refinements.
- [x] Armor full-set bonuses → stats
- [x] Accessory stats → stats (highest-tier-per-family de-dup)
- [x] **Reforges → stats** — the Reforge Anvil now stamps the reforge on the item
      via PDC (and renames it, e.g. `Fierce Hyperion`); `recompute` reads each gear
      piece's reforge and adds its stats. Replaced the fragile per-slot map.
      **Rarity scaling done** (#4397: per-rarity stat tables Common→Mythic) and
      **reforging now charges the rarity-scaled coin cost** (#4413, was free).
      Refinements remaining: reforge-stone slot + item-type reforge pools.
- [~] **Enchants** — damage enchants now apply **item-based**: `CombatListener` reads
      the held weapon's lore and multiplies via one additive bucket — Sharpness
      (all mobs) + Smite/Bane/Ender Slayer (by mob family) at +5%/level, plus the
      conditional **Execute/Prosecute** (scale with target HP) and **Giant Killer**
      (capped), all with exact values from the bundled 1:1 data. Refinements: First
      Strike (first-hit state) and on-hit effects (Thunderlord/Life Steal/Looting…).
      **Armor stat-enchants now feed `recompute`**: each armor piece's lore is parsed
      for Growth (+15 ❤ Health/level) and Protection (+4 ❈ Defense/level I–V, +5 VI–VII,
      exact Hypixel values) and added to the stat totals.
- [~] Full damage stack — the **additive enchant-multiplier bucket is now applied**
      (in `CombatListener`); True Defense, effective-HP, and the +15% melee bonus
      still TODO
- [~] Pets → stats: the active pet's **base stats now apply per-pet** (real multi-stat from NEU,
      #4417) via `StatManager.addBonus`, preserved across `recompute`. Still TODO: the bespoke
      **pet abilities** (held/active, e.g. fall-damage immunity, fortune procs) — 39 are defined
      as display-only data and none fire yet; and per-instance **rarity** scaling of pet stats.

## Phase 2 — Progression loops (activity → reward → power)
- [~] De-dup double-XP — **Fishing + Farming + Foraging fixed**: `CompactListeners`
      duplicated the skill XP that `FishingListener`/`SkillListener` already grant
      (verified `SkillListener`'s crop/log maps cover the same blocks, so no XP loss);
      removed the `CompactListeners` duplicates and moved the missing Foraging level-up
      title onto `SkillListener`. **Combat** is still double-counted (CompactListeners
      grants per-hit XP, SkillListener per-kill) — left as-is because per-kill relies on
      a finite mob→XP map, so dropping per-hit would zero combat XP for unlisted mobs.
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

## Phase 3 — Economy wiring (trading touches coins + inventory)  ← ACTIVE
- [~] Bazaar — **instant buy/sell now functional**: a per-product view (`BazaarProductMenu`)
      charges/credits coins (`EconomyManager`) and moves real items in/out of the
      inventory, priced from the live order book with a base-price fallback so it
      works without liquidity. Limit-order placement (buy/sell offers) still TODO.
- [x] Auction House — **BIN + live bidding + claims now functional**. BIN debits the
      buyer, delivers the item, pays the seller; listing **escrows** the item (no
      dupe). **Live bidding** escrows each bid and refunds the outbid leader; expiry
      (`processExpired`) settles auctions. **`AuctionClaimMenu`** ("Your Auctions &
      Claims") collects coins/items and cancels your listings. **Custom bid-amount
      entry** now works via the reusable chat-input system (`ChatInputManager`):
      a "Custom Bid" button prompts in chat, parses k/m/b, and re-validates against
      the live minimum before placing. Remaining polish: search/rarity filters.
- [x] Bank interest auto-timer — every bank account earns interest automatically on
      each SkyBlock **season rollover** (driven by the new calendar day-timer below)
- [x] Coins-on-death tuned to Hypixel — **half the purse, combat deaths only** (matches
      the Bank Information tooltip); environmental deaths (fall/lava/drown) keep the purse
- [ ] Drain claim escrow back to players (Bazaar/AH claim flow)

## Phase 4 — Content rosters (grow toward Hypixel sizes)
- [ ] Collections → 70+ (Mithril, Gemstone, Hard Stone, Glacite, …)
- [ ] Minions → 60+ types, real per-type tier tables, crafting-to-obtain
- [ ] Bazaar products → 150+ (enchanted tiers, essences, gemstones, …)
- [~] NPC shops → 25+ with real catalogs — **10 shops** now (added Farm Merchant,
      Builder, Weaponsmith, Armorsmith, Alchemist); all reachable via `/npcshop <id>`
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
- [x] Calendar advances on a real ~20-min-day timer (24000-tick repeating task in
      `onEnable`); season rollover triggers bank interest
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
