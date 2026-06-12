package com.skyblock.core.forge;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton tracking enchantment levels applied by each player.
 *
 * <p>Enchantment data is stored in memory only; it is not persisted across
 * server restarts in this implementation.</p>
 *
 * <p>Not thread-safe; access from the main server thread only.</p>
 */
public final class EnchantingManager {

    private static final EnchantingManager INSTANCE = new EnchantingManager();

    /** Per-player enchantment levels keyed by enchantment name (lower-case). */
    private final Map<UUID, Map<String, Integer>> enchantments = new HashMap<>();

    private EnchantingManager() {}

    public static EnchantingManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Enchantment tracking
    // -------------------------------------------------------------------------

    /**
     * Sets the enchantment level for {@code enchantName} on the given player.
     *
     * @param playerId    the player's UUID
     * @param enchantName the enchantment identifier (e.g. "sharpness", "fortune")
     * @param level       the enchantment level (must be >= 0)
     */
    public void setEnchantment(UUID playerId, String enchantName, int level) {
        if (playerId == null || enchantName == null || enchantName.isEmpty() || level < 0) {
            return;
        }
        String key = enchantName.toLowerCase();
        if (level == 0) {
            Map<String, Integer> entry = enchantments.get(playerId);
            if (entry != null) {
                entry.remove(key);
            }
            return;
        }
        enchantments.computeIfAbsent(playerId, k -> new HashMap<>()).put(key, level);
    }

    /**
     * Returns the enchantment level the player has for the given enchantment.
     *
     * @param playerId    the player's UUID
     * @param enchantName the enchantment identifier
     * @return enchantment level, or 0 if none recorded
     */
    public int getEnchantmentLevel(UUID playerId, String enchantName) {
        if (playerId == null || enchantName == null) {
            return 0;
        }
        Map<String, Integer> entry = enchantments.get(playerId);
        if (entry == null) {
            return 0;
        }
        return entry.getOrDefault(enchantName.toLowerCase(), 0);
    }

    /**
     * Returns an unmodifiable view of all enchantments for the given player.
     *
     * @param playerId the player's UUID
     * @return map of enchantment name to level; empty if none recorded
     */
    public Map<String, Integer> getAllEnchantments(UUID playerId) {
        if (playerId == null) {
            return Collections.emptyMap();
        }
        Map<String, Integer> entry = enchantments.get(playerId);
        return entry != null ? Collections.unmodifiableMap(entry) : Collections.emptyMap();
    }

    /**
     * Resets all enchantments for the given player.
     *
     * @param playerId the player's UUID
     */
    public void resetEnchantments(UUID playerId) {
        enchantments.remove(playerId);
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
        enchantments.remove(playerId);
    }
}
