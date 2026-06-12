package com.skyblock.enchantments;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

/**
 * Singleton that tracks enchantments applied to items owned by players.
 *
 * <p>Each item is identified by a unique id. An item may carry at most one
 * level per enchantment type. Access the shared instance via
 * {@link #getInstance()}. Not thread-safe; synchronize externally if accessed
 * from multiple threads.</p>
 */
public final class EnchantmentManager {

    private static final EnchantmentManager INSTANCE = new EnchantmentManager();

    private EnchantmentManager() {
    }

    /**
     * Returns the shared manager instance.
     *
     * @return the singleton {@code EnchantmentManager}
     */
    public static EnchantmentManager getInstance() {
        return INSTANCE;
    }

    /** Item unique-id → map of enchantment → level. */
    private final HashMap<UUID, EnumMap<SkyBlockEnchantment, Integer>> itemEnchantments =
            new HashMap<>();

    /**
     * Applies an enchantment at the given level to an item, replacing any
     * previously stored level for that enchantment.
     *
     * @param itemId      the unique id of the item, must not be null
     * @param enchantment the enchantment to apply, must not be null
     * @param level       the level to apply, must be valid for the enchantment
     * @throws IllegalArgumentException if any argument is null or the level is
     *                                  out of range for the enchantment
     */
    public void applyEnchantment(UUID itemId, SkyBlockEnchantment enchantment, int level) {
        if (itemId == null) {
            throw new IllegalArgumentException("itemId must not be null");
        }
        if (enchantment == null) {
            throw new IllegalArgumentException("enchantment must not be null");
        }
        if (!enchantment.isValidLevel(level)) {
            throw new IllegalArgumentException(
                    "invalid level " + level + " for enchantment " + enchantment.name()
                            + " (max " + enchantment.getMaxLevel() + ")");
        }
        itemEnchantments
                .computeIfAbsent(itemId, id -> new EnumMap<>(SkyBlockEnchantment.class))
                .put(enchantment, level);
    }

    /**
     * Removes an enchantment from an item.
     *
     * @param itemId      the unique id of the item
     * @param enchantment the enchantment to remove
     * @return {@code true} if the enchantment was present and has been removed
     */
    public boolean removeEnchantment(UUID itemId, SkyBlockEnchantment enchantment) {
        EnumMap<SkyBlockEnchantment, Integer> map = itemEnchantments.get(itemId);
        if (map == null) {
            return false;
        }
        boolean removed = map.remove(enchantment) != null;
        if (map.isEmpty()) {
            itemEnchantments.remove(itemId);
        }
        return removed;
    }

    /**
     * Returns the level of an enchantment on an item, or {@code 0} if the
     * item does not have that enchantment.
     *
     * @param itemId      the unique id of the item
     * @param enchantment the enchantment to query
     * @return the applied level, or {@code 0} if not present
     */
    public int getLevel(UUID itemId, SkyBlockEnchantment enchantment) {
        EnumMap<SkyBlockEnchantment, Integer> map = itemEnchantments.get(itemId);
        if (map == null) {
            return 0;
        }
        return map.getOrDefault(enchantment, 0);
    }

    /**
     * Returns all enchantments applied to an item as an unmodifiable map.
     *
     * @param itemId the unique id of the item
     * @return an unmodifiable map of enchantment to level; empty if the item
     *         has no enchantments
     */
    public Map<SkyBlockEnchantment, Integer> getEnchantments(UUID itemId) {
        EnumMap<SkyBlockEnchantment, Integer> map = itemEnchantments.get(itemId);
        if (map == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * Returns whether an item has a specific enchantment.
     *
     * @param itemId      the unique id of the item
     * @param enchantment the enchantment to check
     * @return {@code true} if the item has the enchantment at any level
     */
    public boolean hasEnchantment(UUID itemId, SkyBlockEnchantment enchantment) {
        return getLevel(itemId, enchantment) > 0;
    }
}
