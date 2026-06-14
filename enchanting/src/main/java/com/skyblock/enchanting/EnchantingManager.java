package com.skyblock.enchanting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
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

    /** Static catalogue: enchant name → {maxLevel, bookshelvesRequired}. */
    public static final Map<String, int[]> ENCHANT_DATA;
    static {
        Map<String, int[]> m = new HashMap<>();
        // Combat
        m.put("SHARPNESS",          new int[]{7, 15});
        m.put("CRITICAL",           new int[]{7, 12});
        m.put("SMITE",              new int[]{7, 10});
        m.put("BANE_OF_ARTHROPODS", new int[]{7, 10});
        m.put("FIRST_STRIKE",       new int[]{4,  8});
        m.put("GIANT_KILLER",       new int[]{7, 12});
        m.put("ENDER_SLAYER",       new int[]{7, 15});
        m.put("DRAGON_HUNTER",      new int[]{5, 15});
        m.put("THUNDERLORD",        new int[]{7, 12});
        m.put("EXECUTE",            new int[]{5, 10});
        // Utility / Special
        m.put("TELEKINESIS",        new int[]{1,  5});
        m.put("LOOTING",            new int[]{4,  8});
        m.put("POWER",              new int[]{5, 10});
        m.put("SMELTING_TOUCH",     new int[]{1,  8});
        m.put("MAGNET",             new int[]{1,  5});
        m.put("LIFE_STEAL",         new int[]{5, 10});
        // Fishing
        m.put("LUCK_OF_THE_SEA",    new int[]{7, 10});
        m.put("ANGLER",             new int[]{6, 12});
        m.put("FRAIL",              new int[]{5, 10});
        m.put("EXPERTISE",          new int[]{10, 15});
        // Farming
        m.put("CULTIVATING",        new int[]{10, 15});
        m.put("GREEN_THUMB",        new int[]{5, 10});
        m.put("HARVESTING",         new int[]{6, 10});
        // Mining / Tool
        m.put("EFFICIENCY",         new int[]{5, 10});
        m.put("FORTUNE",            new int[]{4, 12});
        m.put("SILK_TOUCH",         new int[]{1,  8});
        // Armor
        m.put("PROTECTION",         new int[]{7, 15});
        m.put("THORNS",             new int[]{3,  8});
        m.put("GROWTH",             new int[]{7, 12});
        m.put("FEATHER_FALLING",    new int[]{7, 10});
        m.put("REJUVENATE",         new int[]{5, 12});
        ENCHANT_DATA = Collections.unmodifiableMap(m);
    }

    private final Map<UUID, Map<EnchantType, Integer>> itemEnchants = new HashMap<>();
    private final Map<UUID, List<String>> enchantingHistory = new HashMap<>();

    public void recordEnchantingEvent(UUID playerUuid, String summary) {
        enchantingHistory.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getEnchantingHistory(UUID playerUuid) {
        return Collections.unmodifiableList(enchantingHistory.getOrDefault(playerUuid, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllEnchantingHistory() {
        return Collections.unmodifiableMap(enchantingHistory);
    }

    public String getEnchantingStats(UUID itemId) {
        int booksApplied = enchantingHistory.getOrDefault(itemId, Collections.emptyList()).size();
        Map<EnchantType, Integer> enchants = itemEnchants.getOrDefault(itemId, Collections.emptyMap());
        int totalLevels = enchants.values().stream().mapToInt(Integer::intValue).sum();
        return "Total books applied: " + booksApplied + ", Cumulative enchant levels: " + totalLevels;
    }

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
        recordEnchantingEvent(itemId, "Enchanted " + type.name() + " level " + level);
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
        recordEnchantingEvent(itemId, "Disenchanted " + type.name());
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
