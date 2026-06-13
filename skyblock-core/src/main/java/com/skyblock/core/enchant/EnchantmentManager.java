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
    public enum SkyBlockEnchantmentment {
        // Combat
        SHARPNESS("Sharpness"),
        CRITICAL("Critical"),
        SMITE("Smite"),
        BANE_OF_ARTHROPODS("Bane of Arthropods"),
        FIRST_STRIKE("First Strike"),
        GIANT_KILLER("Giant Killer"),
        ENDER_SLAYER("Ender Slayer"),
        DRAGON_HUNTER("Dragon Hunter"),
        THUNDERLORD("Thunderlord"),
        VAMPIRISM("Vampirism"),
        LIFE_STEAL("Life Steal"),
        LETHALITY("Lethality"),
        EXECUTE("Execute"),
        PROSECUTE("Prosecute"),
        OVERLOAD("Overload"),
        // Utility
        TELEKINESIS("Telekinesis"),
        LOOTING("Looting"),
        SMELTING_TOUCH("Smelting Touch"),
        MAGNET("Magnet"),
        SILK_TOUCH("Silk Touch"),
        // Fishing
        LUCK_OF_THE_SEA("Luck of the Sea"),
        ANGLER("Angler"),
        FRAIL("Frail"),
        EXPERTISE("Expertise"),
        // Farming
        CULTIVATING("Cultivating"),
        GREEN_THUMB("Green Thumb"),
        DEDICATION("Dedication"),
        REPLENISH("Replenish"),
        HARVESTING("Harvesting"),
        TURBO_WHEAT("Turbo-Wheat"),
        TURBO_COCO("Turbo-Cocoa"),
        TURBO_CACTUS("Turbo-Cactus"),
        TURBO_MELON("Turbo-Melon"),
        TURBO_PUMPKIN("Turbo-Pumpkin"),
        TURBO_WARTS("Turbo-Warts"),
        TURBO_MUSHROOMS("Turbo-Mushrooms"),
        TURBO_POTATO("Turbo-Potato"),
        TURBO_CARROT("Turbo-Carrot"),
        TURBO_SUGAR_CANE("Turbo-Sugar Cane"),
        // Mining / Tool
        EFFICIENCY("Efficiency"),
        FORTUNE("Fortune"),
        // Armor
        PROTECTION("Protection"),
        THORNS("Thorns"),
        GROWTH("Growth"),
        FEATHER_FALLING("Feather Falling"),
        SUGAR_RUSH("Sugar Rush"),
        REJUVENATE("Rejuvenate"),
        // Misc
        LUCK("Luck"),
        CHANCE("Chance"),
        ULTIMATE_WISE("Ultimate Wise");

        private final String displayName;

        SkyBlockEnchantmentment(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    /** Maximum level allowed per enchant type. */
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
        MAX_LEVELS.put(SkyBlockEnchantment.OVERLOAD, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.TELEKINESIS, 1);
        MAX_LEVELS.put(SkyBlockEnchantment.LOOTING, 4);
        MAX_LEVELS.put(SkyBlockEnchantment.SMELTING_TOUCH, 1);
        MAX_LEVELS.put(SkyBlockEnchantment.MAGNET, 1);
        MAX_LEVELS.put(SkyBlockEnchantment.SILK_TOUCH, 1);
        MAX_LEVELS.put(SkyBlockEnchantment.LUCK_OF_THE_SEA, 7);
        MAX_LEVELS.put(SkyBlockEnchantment.ANGLER, 6);
        MAX_LEVELS.put(SkyBlockEnchantment.FRAIL, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.EXPERTISE, 10);
        MAX_LEVELS.put(SkyBlockEnchantment.CULTIVATING, 10);
        MAX_LEVELS.put(SkyBlockEnchantment.GREEN_THUMB, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.DEDICATION, 4);
        MAX_LEVELS.put(SkyBlockEnchantment.REPLENISH, 1);
        MAX_LEVELS.put(SkyBlockEnchantment.HARVESTING, 6);
        MAX_LEVELS.put(SkyBlockEnchantment.TURBO_WHEAT, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.TURBO_COCO, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.TURBO_CACTUS, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.TURBO_MELON, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.TURBO_PUMPKIN, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.TURBO_WARTS, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.TURBO_MUSHROOMS, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.TURBO_POTATO, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.TURBO_CARROT, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.TURBO_SUGAR_CANE, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.EFFICIENCY, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.FORTUNE, 4);
        MAX_LEVELS.put(SkyBlockEnchantment.PROTECTION, 7);
        MAX_LEVELS.put(SkyBlockEnchantment.THORNS, 3);
        MAX_LEVELS.put(SkyBlockEnchantment.GROWTH, 7);
        MAX_LEVELS.put(SkyBlockEnchantment.FEATHER_FALLING, 7);
        MAX_LEVELS.put(SkyBlockEnchantment.SUGAR_RUSH, 3);
        MAX_LEVELS.put(SkyBlockEnchantment.REJUVENATE, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.LUCK, 7);
        MAX_LEVELS.put(SkyBlockEnchantment.CHANCE, 5);
        MAX_LEVELS.put(SkyBlockEnchantment.ULTIMATE_WISE, 5);
    }

    private static final EnchantmentManager INSTANCE = new EnchantmentManager();

    /** Per-player enchantment levels; absent entries mean the enchantment is not applied. */
    private final Map<UUID, Map<SkyBlockEnchantment, Integer>> playerEnchantments = new HashMap<>();

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
    public int getLevel(UUID playerId, SkyBlockEnchantment type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<SkyBlockEnchantment, Integer> enchants = playerEnchantments.get(playerId);
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
    public void setEnchantment(UUID playerId, SkyBlockEnchantment type, int level) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        int max = MAX_LEVELS.getOrDefault(type, 1);
        if (level < 1 || level > max) {
            throw new IllegalArgumentException(
                    "Level " + level + " out of range [1, " + max + "] for " + type);
        }
        playerEnchantments.computeIfAbsent(playerId, id -> new EnumMap<>(SkyBlockEnchantment.class))
                .put(type, level);
    }

    /**
     * Removes an enchant from the player.
     *
     * @param playerId the player to update
     * @param type     the enchant type to remove
     * @return {@code true} if the enchantment was present, {@code false} otherwise
     */
    public boolean removeEnchantment(UUID playerId, SkyBlockEnchantment type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<SkyBlockEnchantment, Integer> enchants = playerEnchantments.get(playerId);
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
    public Map<SkyBlockEnchantment, Integer> getEnchantments(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<SkyBlockEnchantment, Integer> enchants = playerEnchantments.get(playerId);
        return enchants == null ? Collections.emptyMap() : Collections.unmodifiableMap(enchants);
    }

    /**
     * Returns the maximum allowed level for the given enchant type.
     *
     * @param type the enchant type to query
     * @return the maximum level
     */
    public int getMaxLevel(SkyBlockEnchantment type) {
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
