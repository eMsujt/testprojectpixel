# SkyBlock Menu Fidelity — Roadmap & Status

The throughline for making every in-game menu 1:1 with Hypixel SkyBlock. Read this
before touching a menu so new work stays consistent with what's already shipped.

## Conventions (keep every menu aligned to these)

- **Background / border:** **black** stained-glass panes (`Material.BLACK_STAINED_GLASS_PANE`)
  — this is Hypixel's standard menu chrome. The `Menu` base auto-frames empty
  border slots in black via `frameEmptyBorder()`, and `drawBorder()` defaults to
  black. Don't hand-roll a gray border; let the base class frame it, or fill the
  whole background black and overlay content.
  - *Exception (intentional):* `LIGHT_GRAY_STAINED_GLASS_PANE` for "Empty X Slot"
    placeholders (e.g. the Equipment menu) — this matches Hypixel and stays.
  - *Tracked:* a handful of menus still use themed borders (red/purple/lime/etc.).
    Hypixel uses themed borders only on a few screens; converging the rest to black
    is a low-priority follow-up (see Obstacles).
- **Slot math:** wiki `(row, col)` is 1-based → 0-based slot `= (row-1)*9 + (col-1)`.
  Inner content uses `contentSlot(i)` / `contentCapacity()` from `Menu`; never place
  looped content on a border column (`slot%9 == 0 || == 8`) or the top/bottom row.
- **Navigation:** a **Go Back** arrow returns to the parent menu (usually the
  SkyBlock Menu); a **Close** barrier closes. Hub menus reachable from the SkyBlock
  Menu use Go Back; standalone command menus may use Close. Match the documented
  Hypixel slot when one exists (e.g. Bank/Profile = 30/31, 6-row hubs ≈ 48/49).
- **Titles:** the exact Hypixel window-title text, no `§l` bold unless Hypixel
  bolds it. Hypixel titles are usually plain (e.g. `Your Skills`, `Select Tier`).
- **Stats / XP:** read live values from the managers (`StatsManager`,
  `SkillsManager`, `PetManager`, …); render XP as a 20-char `━` progress bar with
  "Progress to Level N: X%".

## Status

### Verified / rebuilt 1:1 this session
SkyBlock Menu, Your Skills, Collection, Pets, Your Equipment and Stats, Profile
Management, Personal Bank, SkyBlock Leveling, Calendar and Events, Wardrobe,
Accessory Bag, Slayer, Heart of the Mountain, Bazaar, Auctions Browser, Island
Management, Dungeon Classes, Select Tier (Kuudra), The Forge, Sack of Sacks, Your
Museum, Commissions, Catacombs Gate, Enchant Item (title), Chocolate Factory.
Plus the **scoreboard sidebar** and the **tab banner**.

### Acceptable as-is (wiki documents no fixed slot layout → UNVERIFIABLE)
Bestiary, Trophy Fishing, Stats, Fairy Souls, Dungeons, Dungeon Stats, Crimson
Isle, Crystal Hollows, The Dojo, Jacob's Contest, Mining, Farming, Fishing,
Combat, The Rift, Melody's Harp, Runecrafting, Essence/Essence Shop, NPC Shop,
Reforge Anvil, Alchemy, Garden, Minions. These follow the conventions above but
have no documented slots to match precisely.

## Obstacles / Next up (largest first)

These are real model/feature reworks (touch managers or need new systems), not
pure menu edits — do them deliberately, one PR each, build-verified.

1. **Storage hub** — currently shows one backpack's contents under the title
   "Storage". Real hub = Ender Chest section + 18 backpack-tier slots + page nav +
   section spacers. Either rebuild as the hub or rename to "Backpack".
3. **Runic Pedestal** — currently a rune catalog; real menu is an apply/fuse station
   (item input @19, sacrifice input @25, Apply @31, Rune Removal @44 sub-menu).
   Needs functional item-handling slots.
4. **Mayor** — Hypixel has no standalone Mayor GUI; the mayor lives inside Calendar
   and Events. De-dup `MayorMenu` into `CalendarMenu`.
5. **Foraging** — the area/zone selector is part of a *consistent* set with
   Farming/Mining/Fishing/Combat; a skill-progression rebuild would break that set.
   Decide whether to rebuild all five as skill pages or leave the set as-is.
6. **Enchant Item paradigm** — the live menu is an enchant catalog; Hypixel is a
   place-item-and-enchant screen (functional input slot). `EnchantingTableMenu` is
   dead code and should be removed in this rework.
7. **Auction House controls** — Search/Sort/Rarity/BIN buttons are display-only;
   wire real behavior.
8. **Tab columns** — the Info/Skills/Players widget columns need a player-list
   **packet layer** (ProtocolLib or NMS); only the header/footer banner is done.
9. **Border-color convergence** — migrate the remaining themed borders
   (red/purple/lime/orange/green/blue/etc.) to black per the convention above.

## How a menu fix lands
Branch → edit → `mvn -pl skyblock-core -am package` (green) → PR to `main` →
poll the `compile` check → merge → rebuild `SkyBlock-plugin.jar`.
