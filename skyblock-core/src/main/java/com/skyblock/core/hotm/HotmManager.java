package com.skyblock.core.hotm;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's Heart of the Mountain perk levels.
 *
 * <p>Perk levels are stored as a {@code int[]} indexed by {@link HotmPerk#ordinal()}.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class HotmManager {

    /** Every upgradeable perk in the Heart of the Mountain tree. */
    public enum HotmPerk {
        MINING_SPEED(50, "Mining Speed"),
        MINING_SPEED_BOOST(1, "Mining Speed Boost"),
        PICKOBULUS(3, "Pickobulus"),
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
        SEASONED_MINEMAN(       100, "Seasoned Mineman"),
        ANOMALOUS_DESIRE(        20, "Anomalous Desire"),
        VEIN_SEEKER(              1, "Vein Seeker");

        /** Maximum level for this perk. */
        public final int maxLevel;
        private final String displayName;

        HotmPerk(int maxLevel, String displayName) {
            this.maxLevel = maxLevel;
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private static final HotmManager INSTANCE = new HotmManager();

    /** Per-player perk levels; absent entries default to all-zeros. */
    private final Map<UUID, int[]> playerPerks = new HashMap<>();
    /** Per-player HOTM event history. */
    private final Map<UUID, List<String>> hotmHistory = new HashMap<>();

    private HotmManager() {
    }

    /**
     * Returns the single shared {@code HotmManager} instance.
     *
     * @return the singleton instance
     */
    public static HotmManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the current level of a perk for the given player.
     *
     * @param playerId the player to look up
     * @param perk     the perk to query
     * @return the current level, {@code 0} if not unlocked
     */
    public int getLevel(UUID playerId, HotmPerk perk) {
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
    public void setLevel(UUID playerId, HotmPerk perk, int level) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(perk, "perk");
        if (level < 0) {
            throw new IllegalArgumentException("level must not be negative");
        }
        int clamped = Math.min(level, perk.maxLevel);
        int[] levels = playerPerks.computeIfAbsent(playerId, id -> new int[HotmPerk.values().length]);
        levels[perk.ordinal()] = clamped;
    }

    /**
     * Upgrades a perk by one level, up to its maximum.
     *
     * @param playerId the player to upgrade
     * @param perk     the perk to upgrade
     * @return the new level after the upgrade, or {@code -1} if already at max
     */
    public int upgrade(UUID playerId, HotmPerk perk) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(perk, "perk");
        int current = getLevel(playerId, perk);
        if (current >= perk.maxLevel) {
            return -1;
        }
        int[] levels = playerPerks.computeIfAbsent(playerId, id -> new int[HotmPerk.values().length]);
        levels[perk.ordinal()] = current + 1;
        return current + 1;
    }

    /**
     * Returns a copy of all perk levels for the given player.
     *
     * @param playerId the player to look up
     * @return array of perk levels indexed by {@link HotmPerk#ordinal()}, all-zeros if no data
     */
    public int[] getAllLevels(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int[] levels = playerPerks.get(playerId);
        if (levels == null) {
            return new int[HotmPerk.values().length];
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
     * Records an HOTM event summary for the given player.
     *
     * @param playerId the player
     * @param summary  a human-readable description of the event
     */
    public void recordHotmEvent(UUID playerId, String summary) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(summary, "summary");
        hotmHistory.computeIfAbsent(playerId, id -> new ArrayList<>()).add(summary);
    }

    /**
     * Returns the HOTM event history for the given player.
     *
     * @param playerId the player to look up
     * @return unmodifiable list of history entries, empty if none
     */
    public List<String> getHotmHistory(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return Collections.unmodifiableList(hotmHistory.getOrDefault(playerId, Collections.emptyList()));
    }

    /**
     * Removes all HOTM data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
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
        HotmPerk[] perks = HotmPerk.values();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                int[] levels = new int[perks.length];
                boolean hasData = false;
                for (HotmPerk perk : perks) {
                    String path = key + "." + perk.name();
                    if (cfg.contains(path)) {
                        levels[perk.ordinal()] = cfg.getInt(path, 0);
                        hasData = true;
                    }
                }
                if (hasData) {
                    playerPerks.put(uuid, levels);
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
            for (HotmPerk perk : HotmPerk.values()) {
                int level = levels[perk.ordinal()];
                if (level != 0) {
                    cfg.set(key + "." + perk.name(), level);
                }
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save hotm.yml", e);
        }
    }
}
