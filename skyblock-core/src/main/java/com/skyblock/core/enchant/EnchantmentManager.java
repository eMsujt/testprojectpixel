package com.skyblock.core.enchant;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing SkyBlock enchantments applied to player items.
 *
 * <p>Tracks which enchantments at which levels are active for each player.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class EnchantmentManager {

    /** Every SkyBlock enchant type. */
    public enum SkyBlockEnchant {
        // Combat
        SHARPNESS,
        CRITICAL,
        SMITE,
        BANE_OF_ARTHROPODS,
        FIRST_STRIKE,
        GIANT_KILLER,
        ENDER_SLAYER,
        DRAGON_HUNTER,
        THUNDERLORD,
        VAMPIRISM,
        LIFE_STEAL,
        LETHALITY,
        EXECUTE,
        PROSECUTE,
        OVERLOAD,
        // Utility
        TELEKINESIS,
        LOOTING,
        SMELTING_TOUCH,
        MAGNET,
        SILK_TOUCH,
        // Fishing
        LUCK_OF_THE_SEA,
        ANGLER,
        FRAIL,
        EXPERTISE,
        // Farming
        CULTIVATING,
        GREEN_THUMB,
        DEDICATION,
        REPLENISH,
        HARVESTING,
        TURBO_WHEAT,
        TURBO_COCO,
        TURBO_CACTUS,
        TURBO_MELON,
        TURBO_PUMPKIN,
        TURBO_WARTS,
        TURBO_MUSHROOMS,
        TURBO_POTATO,
        TURBO_CARROT,
        TURBO_SUGAR_CANE,
        // Mining / Tool
        EFFICIENCY,
        FORTUNE,
        // Armor
        PROTECTION,
        THORNS,
        GROWTH,
        FEATHER_FALLING,
        SUGAR_RUSH,
        REJUVENATE,
        // Misc
        LUCK,
        CHANCE,
        ULTIMATE_WISE
    }

    /** Maximum level allowed per enchant type. */
    private static final Map<SkyBlockEnchant, Integer> MAX_LEVELS;

    static {
        MAX_LEVELS = new EnumMap<>(SkyBlockEnchant.class);
        MAX_LEVELS.put(SkyBlockEnchant.SHARPNESS, 7);
        MAX_LEVELS.put(SkyBlockEnchant.CRITICAL, 7);
        MAX_LEVELS.put(SkyBlockEnchant.SMITE, 7);
        MAX_LEVELS.put(SkyBlockEnchant.BANE_OF_ARTHROPODS, 7);
        MAX_LEVELS.put(SkyBlockEnchant.FIRST_STRIKE, 4);
        MAX_LEVELS.put(SkyBlockEnchant.GIANT_KILLER, 7);
        MAX_LEVELS.put(SkyBlockEnchant.ENDER_SLAYER, 7);
        MAX_LEVELS.put(SkyBlockEnchant.DRAGON_HUNTER, 5);
        MAX_LEVELS.put(SkyBlockEnchant.THUNDERLORD, 7);
        MAX_LEVELS.put(SkyBlockEnchant.VAMPIRISM, 6);
        MAX_LEVELS.put(SkyBlockEnchant.LIFE_STEAL, 5);
        MAX_LEVELS.put(SkyBlockEnchant.LETHALITY, 6);
        MAX_LEVELS.put(SkyBlockEnchant.EXECUTE, 5);
        MAX_LEVELS.put(SkyBlockEnchant.PROSECUTE, 5);
        MAX_LEVELS.put(SkyBlockEnchant.OVERLOAD, 5);
        MAX_LEVELS.put(SkyBlockEnchant.TELEKINESIS, 1);
        MAX_LEVELS.put(SkyBlockEnchant.LOOTING, 4);
        MAX_LEVELS.put(SkyBlockEnchant.SMELTING_TOUCH, 1);
        MAX_LEVELS.put(SkyBlockEnchant.MAGNET, 1);
        MAX_LEVELS.put(SkyBlockEnchant.SILK_TOUCH, 1);
        MAX_LEVELS.put(SkyBlockEnchant.LUCK_OF_THE_SEA, 7);
        MAX_LEVELS.put(SkyBlockEnchant.ANGLER, 6);
        MAX_LEVELS.put(SkyBlockEnchant.FRAIL, 5);
        MAX_LEVELS.put(SkyBlockEnchant.EXPERTISE, 10);
        MAX_LEVELS.put(SkyBlockEnchant.CULTIVATING, 10);
        MAX_LEVELS.put(SkyBlockEnchant.GREEN_THUMB, 5);
        MAX_LEVELS.put(SkyBlockEnchant.DEDICATION, 4);
        MAX_LEVELS.put(SkyBlockEnchant.REPLENISH, 1);
        MAX_LEVELS.put(SkyBlockEnchant.HARVESTING, 6);
        MAX_LEVELS.put(SkyBlockEnchant.TURBO_WHEAT, 5);
        MAX_LEVELS.put(SkyBlockEnchant.TURBO_COCO, 5);
        MAX_LEVELS.put(SkyBlockEnchant.TURBO_CACTUS, 5);
        MAX_LEVELS.put(SkyBlockEnchant.TURBO_MELON, 5);
        MAX_LEVELS.put(SkyBlockEnchant.TURBO_PUMPKIN, 5);
        MAX_LEVELS.put(SkyBlockEnchant.TURBO_WARTS, 5);
        MAX_LEVELS.put(SkyBlockEnchant.TURBO_MUSHROOMS, 5);
        MAX_LEVELS.put(SkyBlockEnchant.TURBO_POTATO, 5);
        MAX_LEVELS.put(SkyBlockEnchant.TURBO_CARROT, 5);
        MAX_LEVELS.put(SkyBlockEnchant.TURBO_SUGAR_CANE, 5);
        MAX_LEVELS.put(SkyBlockEnchant.EFFICIENCY, 5);
        MAX_LEVELS.put(SkyBlockEnchant.FORTUNE, 4);
        MAX_LEVELS.put(SkyBlockEnchant.PROTECTION, 7);
        MAX_LEVELS.put(SkyBlockEnchant.THORNS, 3);
        MAX_LEVELS.put(SkyBlockEnchant.GROWTH, 7);
        MAX_LEVELS.put(SkyBlockEnchant.FEATHER_FALLING, 7);
        MAX_LEVELS.put(SkyBlockEnchant.SUGAR_RUSH, 3);
        MAX_LEVELS.put(SkyBlockEnchant.REJUVENATE, 5);
        MAX_LEVELS.put(SkyBlockEnchant.LUCK, 7);
        MAX_LEVELS.put(SkyBlockEnchant.CHANCE, 5);
        MAX_LEVELS.put(SkyBlockEnchant.ULTIMATE_WISE, 5);
    }

    private static final EnchantmentManager INSTANCE = new EnchantmentManager();

    /** Per-player enchantment levels; absent entries mean the enchantment is not applied. */
    private final Map<UUID, Map<SkyBlockEnchant, Integer>> playerEnchantments = new HashMap<>();

    private EnchantmentManager() {}

    /**
     * Returns the single shared {@code EnchantmentManager} instance.
     *
     * @return the singleton instance
     */
    public static EnchantmentManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the level of the given enchant for the player, or {@code 0} if not applied.
     *
     * @param playerId the player to look up
     * @param type     the enchant type to query
     * @return the enchantment level, or {@code 0}
     */
    public int getLevel(UUID playerId, SkyBlockEnchant type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<SkyBlockEnchant, Integer> enchants = playerEnchantments.get(playerId);
        return enchants == null ? 0 : enchants.getOrDefault(type, 0);
    }

    /**
     * Applies an enchant at the given level to the player.
     *
     * @param playerId the player to update
     * @param type     the enchant type to apply
     * @param level    the level to set; must be between 1 and the enchantment's max level
     * @throws IllegalArgumentException if the level is out of range
     */
    public void setEnchantment(UUID playerId, SkyBlockEnchant type, int level) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        int max = MAX_LEVELS.getOrDefault(type, 1);
        if (level < 1 || level > max) {
            throw new IllegalArgumentException(
                    "Level " + level + " out of range [1, " + max + "] for " + type);
        }
        playerEnchantments.computeIfAbsent(playerId, id -> new EnumMap<>(SkyBlockEnchant.class))
                .put(type, level);
    }

    /**
     * Removes an enchant from the player.
     *
     * @param playerId the player to update
     * @param type     the enchant type to remove
     * @return {@code true} if the enchantment was present, {@code false} otherwise
     */
    public boolean removeEnchantment(UUID playerId, SkyBlockEnchant type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<SkyBlockEnchant, Integer> enchants = playerEnchantments.get(playerId);
        if (enchants == null) {
            return false;
        }
        boolean removed = enchants.remove(type) != null;
        if (enchants.isEmpty()) {
            playerEnchantments.remove(playerId);
        }
        return removed;
    }

    /**
     * Returns an unmodifiable view of all enchantments currently applied to the player.
     *
     * @param playerId the player to look up
     * @return a map of enchant type to level; empty if the player has none
     */
    public Map<SkyBlockEnchant, Integer> getEnchantments(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<SkyBlockEnchant, Integer> enchants = playerEnchantments.get(playerId);
        return enchants == null ? Collections.emptyMap() : Collections.unmodifiableMap(enchants);
    }

    /**
     * Returns the maximum allowed level for the given enchant type.
     *
     * @param type the enchant type to query
     * @return the maximum level
     */
    public int getMaxLevel(SkyBlockEnchant type) {
        Objects.requireNonNull(type, "type");
        return MAX_LEVELS.getOrDefault(type, 1);
    }

    /**
     * Removes all enchantment data for the given player.
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerEnchantments.remove(playerId) != null;
    }
}
