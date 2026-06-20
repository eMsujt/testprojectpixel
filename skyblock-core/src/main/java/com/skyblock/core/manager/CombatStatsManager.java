package com.skyblock.core.manager;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking per-player combat attribute stats (strength, crit chance,
 * crit damage, etc.) with SkyBlock-default base values.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class CombatStatsManager {

    /** Default combat attribute values for a new player. */
    private static final Map<String, Double> DEFAULTS;

    static {
        Map<String, Double> d = new LinkedHashMap<>();
        d.put("strength",    5.0);
        d.put("critChance",  30.0);
        d.put("critDamage",  50.0);
        d.put("attackSpeed", 0.0);
        d.put("ferocity",    0.0);
        DEFAULTS = Collections.unmodifiableMap(d);
    }

    private static final CombatStatsManager INSTANCE = new CombatStatsManager();

    private final Map<UUID, Map<String, Double>> playerStats = new HashMap<>();

    private CombatStatsManager() {
    }

    public static CombatStatsManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the value of a combat stat for the given player, falling back to
     * the default if no override exists.
     *
     * @param playerId the player to look up
     * @param stat     the stat key (e.g. "strength", "critChance")
     * @return the stat value, or {@code 0} if the key is unknown
     */
    public double getStat(UUID playerId, String stat) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        Map<String, Double> map = playerStats.get(playerId);
        if (map != null && map.containsKey(stat)) {
            return map.get(stat);
        }
        return DEFAULTS.getOrDefault(stat, 0.0);
    }

    /**
     * Overrides a combat stat for the given player.
     *
     * @param playerId the player to update
     * @param stat     the stat key
     * @param value    the new value
     */
    public void setStat(UUID playerId, String stat, double value) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        playerStats.computeIfAbsent(playerId, id -> new LinkedHashMap<>()).put(stat, value);
    }

    /**
     * Returns an unmodifiable view of all combat stats for the given player,
     * merging defaults with any per-player overrides.
     *
     * @param playerId the player to look up
     * @return merged stat map
     */
    public Map<String, Double> getStats(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<String, Double> merged = new LinkedHashMap<>(DEFAULTS);
        Map<String, Double> overrides = playerStats.get(playerId);
        if (overrides != null) {
            merged.putAll(overrides);
        }
        return Collections.unmodifiableMap(merged);
    }

    /**
     * Removes all per-player overrides, reverting the player to defaults.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had overrides
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerStats.remove(playerId) != null;
    }

    /** Returns the unmodifiable map of SkyBlock combat stat defaults. */
    public static Map<String, Double> getDefaults() {
        return DEFAULTS;
    }
}
