package com.skyblock.core.bestiary;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton tracking how many times each player has killed each mob type.
 *
 * <p>Kill counts are stored in memory only; they are not persisted across
 * server restarts in this implementation.</p>
 *
 * <p>Not thread-safe; access from the main server thread only.</p>
 */
public final class BestiaryManager {

    private static final BestiaryManager INSTANCE = new BestiaryManager();

    /** Per-player kill counts keyed by mob type name (lower-case). */
    private final Map<UUID, Map<String, Integer>> kills = new HashMap<>();

    private BestiaryManager() {}

    public static BestiaryManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Kill tracking
    // -------------------------------------------------------------------------

    /**
     * Records one kill of {@code mobType} for {@code playerId}.
     *
     * @param playerId the killer's UUID
     * @param mobType  the mob type identifier (e.g. "zombie", "skeleton")
     */
    public void recordKill(UUID playerId, String mobType) {
        if (playerId == null || mobType == null || mobType.isEmpty()) {
            return;
        }
        String key = mobType.toLowerCase();
        kills.computeIfAbsent(playerId, k -> new HashMap<>())
             .merge(key, 1, Integer::sum);
    }

    /**
     * Returns the number of kills the player has for the given mob type.
     *
     * @param playerId the player's UUID
     * @param mobType  the mob type identifier
     * @return kill count, or 0 if none recorded
     */
    public int getKills(UUID playerId, String mobType) {
        if (playerId == null || mobType == null) {
            return 0;
        }
        Map<String, Integer> entry = kills.get(playerId);
        if (entry == null) {
            return 0;
        }
        return entry.getOrDefault(mobType.toLowerCase(), 0);
    }

    /**
     * Returns an unmodifiable view of all kill counts for the given player.
     *
     * @param playerId the player's UUID
     * @return map of mob type to kill count; empty if no kills recorded
     */
    public Map<String, Integer> getAllKills(UUID playerId) {
        if (playerId == null) {
            return Collections.emptyMap();
        }
        Map<String, Integer> entry = kills.get(playerId);
        return entry != null ? Collections.unmodifiableMap(entry) : Collections.emptyMap();
    }

    /**
     * Resets all kill counts for the given player.
     *
     * @param playerId the player's UUID
     */
    public void resetKills(UUID playerId) {
        kills.remove(playerId);
    }

    // -------------------------------------------------------------------------
    // Cleanup
    // -------------------------------------------------------------------------

    /**
     * Removes all state for the given player.
     *
     * @param playerId the player's UUID
     */
    public void remove(UUID playerId) {
        kills.remove(playerId);
    }
}
