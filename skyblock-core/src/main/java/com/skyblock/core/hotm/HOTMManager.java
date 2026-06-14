package com.skyblock.core.hotm;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's Heart of the Mountain perk levels.
 *
 * <p>Perk levels are stored as an {@code int[]} indexed by {@link HOTMPerk#ordinal()}.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class HOTMManager {

    /** Every upgradeable perk in the Heart of the Mountain tree. */
    public enum HOTMPerk {
        MINING_SPEED(50, "Mining Speed"),
        MINING_SPEED_BOOST(1, "Mining Speed Boost"),
        PICKOBULUS(1, "Pickobulus"),
        MINING_FORTUNE(50, "Mining Fortune"),
        DAILY_POWDER(100, "Daily Powder"),
        EFFICIENT_MINER(100, "Efficient Miner"),
        QUICK_FORGE(20, "Quick Forge"),
        TITANIUM_INSANITY(50, "Titanium Insanity"),
        LUCK_OF_THE_CAVE(45, "Luck of the Cave"),
        POWDER_BUFF(50, "Powder Buff"),
        MINING_MADNESS(1, "Mining Madness"),
        SKY_MALL(1, "Sky Mall"),
        GOBLIN_KILLER(1, "Goblin Killer"),
        STAR_POWDER(1, "Star Powder"),
        MOLE(200, "Mole"),
        PROFESSIONAL(140, "Professional"),
        LONESOME_MINER(45, "Lonesome Miner"),
        GREAT_EXPLORER(20, "Great Explorer"),
        FORTUNATE(20, "Fortunate"),
        MINING_EXPERIENCE_BOOST(100, "Mining Experience Boost"),
        SEASONED_MINEMAN(100, "Seasoned Mineman"),
        ANOMALOUS_DESIRE(20, "Anomalous Desire");

        /** Maximum level for this perk. */
        public final int maxLevel;
        private final String displayName;

        HOTMPerk(int maxLevel, String displayName) {
            this.maxLevel = maxLevel;
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Per-level powder cost for each HOTM node.
     * Index 0 = cost to upgrade to level 1, index N-1 = cost to upgrade to level N.
     * Single-entry arrays represent one-time unlock costs.
     */
    public static final Map<String, int[]> NODE_POWDER_COSTS;

    static {
        Map<String, int[]> m = new LinkedHashMap<>();
        m.put("MINING_SPEED",              buildPowderCosts(50,  3000, 2.0));
        m.put("MINING_SPEED_BOOST",        new int[]{20000});
        m.put("PICKOBULUS",                new int[]{1200});
        m.put("MINING_FORTUNE",            buildPowderCosts(50,  3000, 2.0));
        m.put("DAILY_POWDER",              buildPowderCosts(100, 1200, 1.8));
        m.put("EFFICIENT_MINER",           buildPowderCosts(100, 1200, 1.8));
        m.put("QUICK_FORGE",               buildPowderCosts(20,  7500, 2.2));
        m.put("TITANIUM_INSANITY",         buildPowderCosts(50,  4000, 2.1));
        m.put("LUCK_OF_THE_CAVE",          buildPowderCosts(45,  2000, 2.0));
        m.put("POWDER_BUFF",               buildPowderCosts(50,  3000, 2.0));
        m.put("MINING_MADNESS",            new int[]{2000});
        m.put("SKY_MALL",                  new int[]{2000});
        m.put("GOBLIN_KILLER",             new int[]{2000});
        m.put("STAR_POWDER",               new int[]{2000});
        m.put("MOLE",                      buildPowderCosts(200, 1000, 1.4));
        m.put("PROFESSIONAL",              buildPowderCosts(140, 2000, 1.7));
        m.put("LONESOME_MINER",            buildPowderCosts(45,  3500, 2.0));
        m.put("GREAT_EXPLORER",            buildPowderCosts(20,  4000, 2.2));
        m.put("FORTUNATE",                 buildPowderCosts(20,  4000, 2.2));
        m.put("MINING_EXPERIENCE_BOOST",   buildPowderCosts(100, 2000, 1.8));
        m.put("SEASONED_MINEMAN",          buildPowderCosts(100, 2000, 1.8));
        m.put("ANOMALOUS_DESIRE",          buildPowderCosts(20,  4000, 2.2));
        NODE_POWDER_COSTS = Collections.unmodifiableMap(m);
    }

    /**
     * Static metadata for each HOTM perk.
     * Each int[] is {@code {maxLevel, bonusPerLevel}} where {@code bonusPerLevel}
     * is the primary stat increase per upgrade (0 for toggle/ability perks).
     */
    public static final Map<String, int[]> PERK_DATA;

    static {
        Map<String, int[]> p = new LinkedHashMap<>();
        // multi-level perks — {maxLevel, bonusPerLevel}
        p.put("MINING_SPEED",            new int[]{50,  20}); // +20 Mining Speed/level
        p.put("MINING_FORTUNE",          new int[]{50,   5}); // +5 Mining Fortune/level
        p.put("DAILY_POWDER",            new int[]{100,  1}); // +1 daily Mithril Powder/level
        p.put("EFFICIENT_MINER",         new int[]{100,  1}); // +0.1% extra block chance/level (×10 scaled)
        p.put("QUICK_FORGE",             new int[]{20,   5}); // -0.5% forge time/level (×10 scaled)
        p.put("TITANIUM_INSANITY",       new int[]{50,   2}); // +2 bonus rolls/level
        p.put("LUCK_OF_THE_CAVE",        new int[]{45,   1}); // +1% luck/level
        p.put("POWDER_BUFF",             new int[]{50,   1}); // +1% Mithril Powder/level
        p.put("MOLE",                    new int[]{200,  1}); // +1 cumulative block/level
        p.put("PROFESSIONAL",            new int[]{140,  4}); // +4 Mining Speed when mining ores/level
        p.put("LONESOME_MINER",          new int[]{45,   5}); // +5 bonus stats in Crystal Hollows/level
        p.put("GREAT_EXPLORER",          new int[]{20,   3}); // +3% chest find chance/level
        p.put("FORTUNATE",               new int[]{20,   4}); // +4 Gemstone Mining Fortune/level
        p.put("MINING_EXPERIENCE_BOOST", new int[]{100,  1}); // +1 bonus Mining XP/level
        p.put("SEASONED_MINEMAN",        new int[]{100,  1}); // +1 Mining Wisdom/level
        p.put("ANOMALOUS_DESIRE",        new int[]{20,   2}); // +2% Gemstone Crystal chance/level
        // ability/toggle perks — {maxLevel, 0}
        p.put("MINING_SPEED_BOOST",      new int[]{1,    0}); // active ability: +200% Mining Speed for 10 s
        p.put("PICKOBULUS",              new int[]{3,    0}); // active ability: AOE pickaxe throw
        p.put("MINING_MADNESS",          new int[]{1,    0}); // toggle: double Mining Speed & Fortune
        p.put("SKY_MALL",               new int[]{1,    0}); // toggle: random daily perk
        p.put("GOBLIN_KILLER",           new int[]{1,    0}); // toggle: Goblins drop Mithril Powder
        p.put("STAR_POWDER",             new int[]{1,    0}); // toggle: Mithril Golems drop Mithril Powder
        p.put("MANIACAL_MINER",          new int[]{1,    0}); // passive: mining grants Haste II
        p.put("VEIN_SEEKER",             new int[]{1,    0}); // toggle: highlight ore veins
        PERK_DATA = Collections.unmodifiableMap(p);
    }

    private static int[] buildPowderCosts(int levels, int base, double scale) {
        int[] costs = new int[levels];
        for (int i = 0; i < levels; i++) {
            costs[i] = (int) Math.round(base * Math.pow(i + 1, scale));
        }
        return costs;
    }

    private static final HOTMManager INSTANCE = new HOTMManager();

    /** Per-player perk levels; absent entries default to all-zeros. */
    private final Map<UUID, int[]> playerPerks = new HashMap<>();
    /** Per-player HOTM tree tier (1–7). */
    private final Map<UUID, Integer> hotmTier = new HashMap<>();
    /** Per-player Mithril Powder balance. */
    private final Map<UUID, Long> mithrilPowder = new HashMap<>();
    /** Per-player Gemstone Powder balance. */
    private final Map<UUID, Long> gemstonePowder = new HashMap<>();
    /** Per-player HOTM event history. */
    private final Map<UUID, List<String>> hotmHistory = new HashMap<>();

    private HOTMManager() {
    }

    /**
     * Returns the single shared {@code HOTMManager} instance.
     *
     * @return the singleton instance
     */
    public static HOTMManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the current level of a perk for the given player.
     *
     * @param playerId the player to look up
     * @param perk     the perk to query
     * @return the current level, {@code 0} if not unlocked
     */
    public int getLevel(UUID playerId, HOTMPerk perk) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(perk, "perk");
        int[] levels = playerPerks.get(playerId);
        return levels == null ? 0 : levels[perk.ordinal()];
    }

    /**
     * Sets the level of a perk for the given player.
     *
     * @param playerId the player to update
     * @param perk     the perk to set
     * @param level    the new level (clamped to {@code [0, perk.maxLevel]})
     * @throws IllegalArgumentException if {@code level} is negative
     */
    public void setLevel(UUID playerId, HOTMPerk perk, int level) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(perk, "perk");
        if (level < 0) {
            throw new IllegalArgumentException("level must not be negative");
        }
        int clamped = Math.min(level, perk.maxLevel);
        int[] levels = playerPerks.computeIfAbsent(playerId, id -> new int[HOTMPerk.values().length]);
        levels[perk.ordinal()] = clamped;
    }

    /**
     * Upgrades a perk by one level, up to its maximum.
     *
     * @param playerId the player to upgrade
     * @param perk     the perk to upgrade
     * @return the new level after the upgrade, or {@code -1} if already at max
     */
    public int upgrade(UUID playerId, HOTMPerk perk) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(perk, "perk");
        int current = getLevel(playerId, perk);
        if (current >= perk.maxLevel) {
            return -1;
        }
        int[] levels = playerPerks.computeIfAbsent(playerId, id -> new int[HOTMPerk.values().length]);
        levels[perk.ordinal()] = current + 1;
        return current + 1;
    }

    /**
     * Returns a copy of all perk levels for the given player.
     *
     * @param playerId the player to look up
     * @return array of perk levels indexed by {@link HOTMPerk#ordinal()}, all-zeros if no data
     */
    public int[] getAllLevels(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int[] levels = playerPerks.get(playerId);
        if (levels == null) {
            return new int[HOTMPerk.values().length];
        }
        return Arrays.copyOf(levels, levels.length);
    }

    /**
     * Resets all perk levels for the given player to zero.
     *
     * @param playerId the player to reset
     */
    public void reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int[] levels = playerPerks.get(playerId);
        if (levels != null) {
            Arrays.fill(levels, 0);
        }
    }

    /**
     * Returns the player's current Mithril Powder balance.
     *
     * @param playerId the player to look up
     * @return the balance, {@code 0} if none recorded
     */
    public long getMithrilPowder(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return mithrilPowder.getOrDefault(playerId, 0L);
    }

    /**
     * Adds Mithril Powder to a player's balance.
     *
     * @param playerId the player to credit
     * @param amount   the amount to add (must be non-negative)
     */
    public void addMithrilPowder(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        mithrilPowder.merge(playerId, amount, Long::sum);
    }

    /**
     * Deducts Mithril Powder from a player's balance.
     *
     * @param playerId the player to debit
     * @param amount   the amount to deduct (must be non-negative)
     * @return {@code true} if deducted successfully, {@code false} if insufficient balance
     */
    public boolean spendMithrilPowder(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        long current = getMithrilPowder(playerId);
        if (current < amount) return false;
        mithrilPowder.put(playerId, current - amount);
        return true;
    }

    /**
     * Returns the player's current Gemstone Powder balance.
     *
     * @param playerId the player to look up
     * @return the balance, {@code 0} if none recorded
     */
    public long getGemstonePowder(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return gemstonePowder.getOrDefault(playerId, 0L);
    }

    /**
     * Adds Gemstone Powder to a player's balance.
     *
     * @param playerId the player to credit
     * @param amount   the amount to add (must be non-negative)
     */
    public void addGemstonePowder(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        gemstonePowder.merge(playerId, amount, Long::sum);
    }

    /**
     * Deducts Gemstone Powder from a player's balance.
     *
     * @param playerId the player to debit
     * @param amount   the amount to deduct (must be non-negative)
     * @return {@code true} if deducted successfully, {@code false} if insufficient balance
     */
    public boolean spendGemstonePowder(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        long current = getGemstonePowder(playerId);
        if (current < amount) return false;
        gemstonePowder.put(playerId, current - amount);
        return true;
    }

    /**
     * Returns the player's current HOTM tree tier.
     *
     * @param playerId the player to look up
     * @return the tier (1–7), {@code 1} if not set
     */
    public int getHotmTier(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return hotmTier.getOrDefault(playerId, 1);
    }

    /**
     * Sets the player's HOTM tree tier.
     *
     * @param playerId the player to update
     * @param tier     the new tier (clamped to {@code [1, 7]})
     */
    public void setHotmTier(UUID playerId, int tier) {
        Objects.requireNonNull(playerId, "playerId");
        hotmTier.put(playerId, Math.max(1, Math.min(7, tier)));
    }

    /**
     * Records a HOTM event summary for the given player.
     *
     * @param playerId the player to record for
     * @param summary  a short description of the event
     */
    public void recordHotmEvent(UUID playerId, String summary) {
        Objects.requireNonNull(playerId, "playerId");
        hotmHistory.computeIfAbsent(playerId, id -> new ArrayList<>()).add(summary);
    }

    /**
     * Returns the HOTM event history for the given player.
     *
     * @param playerId the player to look up
     * @return list of event summaries, empty if none recorded
     */
    public List<String> getHotmHistory(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return hotmHistory.getOrDefault(playerId, Collections.emptyList());
    }

    /**
     * Returns the full HOTM event history map for all players.
     *
     * @return unmodifiable view of the history map
     */
    public Map<UUID, List<String>> getAllHotmHistory() {
        return Collections.unmodifiableMap(hotmHistory);
    }

    /**
     * Removes all HOTM data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        hotmTier.remove(playerId);
        mithrilPowder.remove(playerId);
        gemstonePowder.remove(playerId);
        hotmHistory.remove(playerId);
        return playerPerks.remove(playerId) != null;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "hotm.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        playerPerks.clear();
        hotmTier.clear();
        mithrilPowder.clear();
        gemstonePowder.clear();
        hotmHistory.clear();
        HOTMPerk[] perks = HOTMPerk.values();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                int[] levels = new int[perks.length];
                boolean hasData = false;
                for (HOTMPerk perk : perks) {
                    String path = key + "." + perk.name();
                    if (cfg.contains(path)) {
                        levels[perk.ordinal()] = cfg.getInt(path, 0);
                        hasData = true;
                    }
                }
                if (hasData) {
                    playerPerks.put(uuid, levels);
                }
                String tierPath = key + ".hotm_tier";
                if (cfg.contains(tierPath)) {
                    hotmTier.put(uuid, Math.max(1, Math.min(7, cfg.getInt(tierPath, 1))));
                }
                String powderPath = key + ".mithril_powder";
                if (cfg.contains(powderPath)) {
                    mithrilPowder.put(uuid, cfg.getLong(powderPath, 0L));
                }
                String gemstonePath = key + ".gemstone_powder";
                if (cfg.contains(gemstonePath)) {
                    gemstonePowder.put(uuid, cfg.getLong(gemstonePath, 0L));
                }
                String historyPath = key + ".hotm_history";
                if (cfg.contains(historyPath)) {
                    List<String> entries = cfg.getStringList(historyPath);
                    if (!entries.isEmpty()) {
                        hotmHistory.put(uuid, new ArrayList<>(entries));
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "hotm.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, int[]> entry : playerPerks.entrySet()) {
            String key = entry.getKey().toString();
            int[] levels = entry.getValue();
            for (HOTMPerk perk : HOTMPerk.values()) {
                int level = levels[perk.ordinal()];
                if (level != 0) {
                    cfg.set(key + "." + perk.name(), level);
                }
            }
        }
        for (Map.Entry<UUID, Integer> entry : hotmTier.entrySet()) {
            cfg.set(entry.getKey().toString() + ".hotm_tier", entry.getValue());
        }
        for (Map.Entry<UUID, Long> entry : mithrilPowder.entrySet()) {
            if (entry.getValue() != 0) {
                cfg.set(entry.getKey().toString() + ".mithril_powder", entry.getValue());
            }
        }
        for (Map.Entry<UUID, Long> entry : gemstonePowder.entrySet()) {
            if (entry.getValue() != 0) {
                cfg.set(entry.getKey().toString() + ".gemstone_powder", entry.getValue());
            }
        }
        for (Map.Entry<UUID, List<String>> entry : hotmHistory.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                cfg.set(entry.getKey().toString() + ".hotm_history", entry.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save hotm.yml", e);
        }
    }
}
