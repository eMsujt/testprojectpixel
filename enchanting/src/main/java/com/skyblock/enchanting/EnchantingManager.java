package com.skyblock.enchanting;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages the enchants applied to SkyBlock items.
 *
 * <p>Items are identified by their unique item id. Enchant levels are
 * validated against each {@link EnchantType}'s maximum level. Not
 * thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class EnchantingManager {

    private final Map<UUID, Map<EnchantType, Integer>> itemEnchants = new HashMap<>();

    /**
     * Applies an enchant to an item, or upgrades it if already applied.
     *
     * @param itemId the unique item id, must not be null
     * @param type   the enchant type, must not be null
     * @param level  the enchant level, must be between 1 and the type's maximum level
     * @throws IllegalArgumentException if an argument is null or the level is out of range
     */
    public void applyEnchant(UUID itemId, EnchantType type, int level) {
        if (itemId == null || type == null) {
            throw new IllegalArgumentException("itemId and type must not be null");
        }
        if (level < 1 || level > type.getMaxLevel()) {
            throw new IllegalArgumentException(
                    "level must be between 1 and " + type.getMaxLevel() + ": " + level);
        }
        itemEnchants.computeIfAbsent(itemId, id -> new EnumMap<>(EnchantType.class))
                .put(type, level);
    }

    /**
     * Removes an enchant from an item.
     *
     * @param itemId the unique item id
     * @param type   the enchant type
     * @return {@code true} if the enchant was applied and has been removed
     */
    public boolean removeEnchant(UUID itemId, EnchantType type) {
        Map<EnchantType, Integer> enchants = itemEnchants.get(itemId);
        if (enchants == null || enchants.remove(type) == null) {
            return false;
        }
        if (enchants.isEmpty()) {
            itemEnchants.remove(itemId);
        }
        return true;
    }

    /**
     * Returns whether the item has the given enchant applied.
     *
     * @param itemId the unique item id
     * @param type   the enchant type
     * @return {@code true} if the enchant is applied
     */
    public boolean hasEnchant(UUID itemId, EnchantType type) {
        Map<EnchantType, Integer> enchants = itemEnchants.get(itemId);
        return enchants != null && enchants.containsKey(type);
    }

    /**
     * Returns the level of an enchant applied to an item.
     *
     * @param itemId the unique item id
     * @param type   the enchant type
     * @return the enchant level, or {@code 0} if the enchant is not applied
     */
    public int getEnchantLevel(UUID itemId, EnchantType type) {
        Map<EnchantType, Integer> enchants = itemEnchants.get(itemId);
        if (enchants == null) {
            return 0;
        }
        return enchants.getOrDefault(type, 0);
    }

    /**
     * Returns all enchants applied to an item.
     *
     * @param itemId the unique item id
     * @return an unmodifiable view of the item's enchants and their levels
     */
    public Map<EnchantType, Integer> getEnchants(UUID itemId) {
        Map<EnchantType, Integer> enchants = itemEnchants.get(itemId);
        if (enchants == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(enchants);
    }

    /**
     * Removes all enchants from an item.
     *
     * @param itemId the unique item id
     * @return {@code true} if the item had any enchants applied
     */
    public boolean clearEnchants(UUID itemId) {
        return itemEnchants.remove(itemId) != null;
    }
}
