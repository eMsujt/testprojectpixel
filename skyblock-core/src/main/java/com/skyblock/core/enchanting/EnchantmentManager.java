package com.skyblock.core.enchanting;

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

    /** Every SkyBlock enchantment. */
    public enum SkyBlockEnchantment {
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
        TELEKINESIS,
        LOOTING,
        LUCK,
        LUCK_OF_THE_SEA,
        ANGLER,
        FRAIL,
        MAGNET,
        EXPERTISE,
        SMELTING_TOUCH,
        EFFICIENCY,
        FORTUNE,
        SILK_TOUCH,
        PROTECTION,
        THORNS,
        GROWTH,
        FEATHER_FALLING,
        SUGAR_RUSH,
        REJUVENATE,
        CHANCE,
        OVERLOAD,
        ULTIMATE_WISE
    }

    /** Maximum level allowed per enchantment. */
    private static final Map<SkyBlockEnchantment, Integer> MAX_LEVELS;

    static {
        MAX_LEVELS = new EnumMap<>(SkyBlockEnchantment.class);
        MAX_LEVELS.put(SkyBlockEnchantment.SHARPNESS, 7);
        MAX_LEVELS.put(SkyBlockEnchantment.CRITICAL, 7);
        MAX_LEVELS.put(SkyBlockEnchantment.SMITE, 7);
        MAX_LEVELS.put(SkyBlockEnchantment.BANE_OF_ARTHROPODS, 7);
        MAX_LEVELS.put(SkyBlockEnchantment.FIRST_STRIKE, 4);
        MAX_LEVELS.put(SkyBlockEnchantment.GIANT_KILLER, 7);
        MAX_LEVELS.put(SkyBlockEnchantment.ENDER_SLAYER, 7);
        MAX_LEVELS.put(SkyBlockEnchantment.DRAGON_HUNTER, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.THUNDERLORD, 7);
        MAX_LEVELS.put(SkyBlockEnchantment.VAMPIRISM, 6);
        MAX_LEVELS.put(SkyBlockEnchantment.LIFE_STEAL, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.LETHALITY, 6);
        MAX_LEVELS.put(SkyBlockEnchantment.EXECUTE, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.PROSECUTE, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.TELEKINESIS, 1);
        MAX_LEVELS.put(SkyBlockEnchantment.LOOTING, 4);
        MAX_LEVELS.put(SkyBlockEnchantment.LUCK, 7);
        MAX_LEVELS.put(SkyBlockEnchantment.LUCK_OF_THE_SEA, 7);
        MAX_LEVELS.put(SkyBlockEnchantment.ANGLER, 6);
        MAX_LEVELS.put(SkyBlockEnchantment.FRAIL, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.MAGNET, 1);
        MAX_LEVELS.put(SkyBlockEnchantment.EXPERTISE, 10);
        MAX_LEVELS.put(SkyBlockEnchantment.SMELTING_TOUCH, 1);
        MAX_LEVELS.put(SkyBlockEnchantment.EFFICIENCY, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.FORTUNE, 4);
        MAX_LEVELS.put(SkyBlockEnchantment.SILK_TOUCH, 1);
        MAX_LEVELS.put(SkyBlockEnchantment.PROTECTION, 7);
        MAX_LEVELS.put(SkyBlockEnchantment.THORNS, 3);
        MAX_LEVELS.put(SkyBlockEnchantment.GROWTH, 7);
        MAX_LEVELS.put(SkyBlockEnchantment.FEATHER_FALLING, 7);
        MAX_LEVELS.put(SkyBlockEnchantment.SUGAR_RUSH, 3);
        MAX_LEVELS.put(SkyBlockEnchantment.REJUVENATE, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.CHANCE, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.OVERLOAD, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.ULTIMATE_WISE, 5);
    }

    private static final EnchantmentManager INSTANCE = new EnchantmentManager();

    /** Per-player enchantment levels; absent entries mean the enchantment is not applied. */
    private final Map<UUID, Map<SkyBlockEnchantment, Integer>> playerEnchantments = new HashMap<>();

    private EnchantmentManager() {
    }

    /**
     * Returns the single shared {@code EnchantmentManager} instance.
     *
     * @return the singleton instance
     */
    public static EnchantmentManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the level of the given enchantment for the given player, or
     * {@code 0} if the enchantment is not applied.
     *
     * @param playerId    the player to look up
     * @param enchantment the enchantment to query
     * @return the enchantment level, or {@code 0}
     */
    public int getLevel(UUID playerId, SkyBlockEnchantment enchantment) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(enchantment, "enchantment");
        Map<SkyBlockEnchantment, Integer> enchants = playerEnchantments.get(playerId);
        return enchants == null ? 0 : enchants.getOrDefault(enchantment, 0);
    }

    /**
     * Applies an enchantment at the given level to the player.
     *
     * @param playerId    the player to update
     * @param enchantment the enchantment to apply
     * @param level       the level to set; must be between 1 and the enchantment's max level
     * @throws IllegalArgumentException if the level is out of range
     */
    public void setEnchantment(UUID playerId, SkyBlockEnchantment enchantment, int level) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(enchantment, "enchantment");
        int max = MAX_LEVELS.getOrDefault(enchantment, 1);
        if (level < 1 || level > max) {
            throw new IllegalArgumentException(
                    "Level " + level + " out of range [1, " + max + "] for " + enchantment);
        }
        playerEnchantments.computeIfAbsent(playerId, id -> new EnumMap<>(SkyBlockEnchantment.class))
                .put(enchantment, level);
    }

    /**
     * Removes an enchantment from the player.
     *
     * @param playerId    the player to update
     * @param enchantment the enchantment to remove
     * @return {@code true} if the enchantment was present, {@code false} otherwise
     */
    public boolean removeEnchantment(UUID playerId, SkyBlockEnchantment enchantment) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(enchantment, "enchantment");
        Map<SkyBlockEnchantment, Integer> enchants = playerEnchantments.get(playerId);
        if (enchants == null) {
            return false;
        }
        boolean removed = enchants.remove(enchantment) != null;
        if (enchants.isEmpty()) {
            playerEnchantments.remove(playerId);
        }
        return removed;
    }

    /**
     * Returns an unmodifiable view of all enchantments currently applied to the player.
     *
     * @param playerId the player to look up
     * @return a map of enchantment to level; empty if the player has none
     */
    public Map<SkyBlockEnchantment, Integer> getEnchantments(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<SkyBlockEnchantment, Integer> enchants = playerEnchantments.get(playerId);
        return enchants == null ? Collections.emptyMap() : Collections.unmodifiableMap(enchants);
    }

    /**
     * Returns the maximum allowed level for the given enchantment.
     *
     * @param enchantment the enchantment to query
     * @return the maximum level
     */
    public int getMaxLevel(SkyBlockEnchantment enchantment) {
        Objects.requireNonNull(enchantment, "enchantment");
        return MAX_LEVELS.getOrDefault(enchantment, 1);
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
