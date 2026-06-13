package com.skyblock.core.enchant;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton registry of SkyBlock custom enchants.
 *
 * <p>Tracks which enchants at which levels are active for each player.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class EnchantManager {

    /** Every SkyBlock custom enchant with its display name and maximum level. */
    public enum SkyBlockEnchant {
        // Combat
        SHARPNESS("Sharpness", 7),
        CRITICAL("Critical", 7),
        SMITE("Smite", 7),
        BANE_OF_ARTHROPODS("Bane of Arthropods", 7),
        FIRST_STRIKE("First Strike", 4),
        GIANT_KILLER("Giant Killer", 7),
        ENDER_SLAYER("Ender Slayer", 7),
        DRAGON_HUNTER("Dragon Hunter", 5),
        THUNDERLORD("Thunderlord", 7),
        VAMPIRISM("Vampirism", 6),
        LIFE_STEAL("Life Steal", 5),
        LETHALITY("Lethality", 6),
        EXECUTE("Execute", 5),
        PROSECUTE("Prosecute", 5),
        OVERLOAD("Overload", 5),
        // Utility
        TELEKINESIS("Telekinesis", 1),
        LOOTING("Looting", 4),
        SMELTING_TOUCH("Smelting Touch", 1),
        MAGNET("Magnet", 1),
        SILK_TOUCH("Silk Touch", 1),
        // Fishing
        LUCK_OF_THE_SEA("Luck of the Sea", 7),
        ANGLER("Angler", 6),
        FRAIL("Frail", 5),
        EXPERTISE("Expertise", 10),
        // Farming
        CULTIVATING("Cultivating", 10),
        GREEN_THUMB("Green Thumb", 5),
        DEDICATION("Dedication", 4),
        REPLENISH("Replenish", 1),
        HARVESTING("Harvesting", 6),
        TURBO_WHEAT("Turbo-Wheat", 5),
        TURBO_COCO("Turbo-Coco", 5),
        TURBO_CACTUS("Turbo-Cactus", 5),
        TURBO_MELON("Turbo-Melon", 5),
        TURBO_PUMPKIN("Turbo-Pumpkin", 5),
        TURBO_WARTS("Turbo-Warts", 5),
        TURBO_MUSHROOMS("Turbo-Mushrooms", 5),
        TURBO_POTATO("Turbo-Potato", 5),
        TURBO_CARROT("Turbo-Carrot", 5),
        TURBO_SUGAR_CANE("Turbo-Sugar Cane", 5),
        // Mining / Tool
        EFFICIENCY("Efficiency", 5),
        FORTUNE("Fortune", 4),
        // Armor
        PROTECTION("Protection", 7),
        THORNS("Thorns", 3),
        GROWTH("Growth", 7),
        FEATHER_FALLING("Feather Falling", 7),
        SUGAR_RUSH("Sugar Rush", 3),
        REJUVENATE("Rejuvenate", 5),
        // Misc
        LUCK("Luck", 7),
        CHANCE("Chance", 5),
        ULTIMATE_WISE("Ultimate Wise", 5);

        private final String displayName;
        private final int maxLevel;

        SkyBlockEnchant(String displayName, int maxLevel) {
            this.displayName = displayName;
            this.maxLevel = maxLevel;
        }

        public String getDisplayName() { return displayName; }
        public int getMaxLevel() { return maxLevel; }
    }

    /** Every SkyBlock custom enchant. */
    public enum EnchantType {
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

    /** Maximum level allowed per enchant. */
    private static final Map<EnchantType, Integer> MAX_LEVELS;

    static {
        MAX_LEVELS = new EnumMap<>(EnchantType.class);
        MAX_LEVELS.put(EnchantType.SHARPNESS, 7);
        MAX_LEVELS.put(EnchantType.CRITICAL, 7);
        MAX_LEVELS.put(EnchantType.SMITE, 7);
        MAX_LEVELS.put(EnchantType.BANE_OF_ARTHROPODS, 7);
        MAX_LEVELS.put(EnchantType.FIRST_STRIKE, 4);
        MAX_LEVELS.put(EnchantType.GIANT_KILLER, 7);
        MAX_LEVELS.put(EnchantType.ENDER_SLAYER, 7);
        MAX_LEVELS.put(EnchantType.DRAGON_HUNTER, 5);
        MAX_LEVELS.put(EnchantType.THUNDERLORD, 7);
        MAX_LEVELS.put(EnchantType.VAMPIRISM, 6);
        MAX_LEVELS.put(EnchantType.LIFE_STEAL, 5);
        MAX_LEVELS.put(EnchantType.LETHALITY, 6);
        MAX_LEVELS.put(EnchantType.EXECUTE, 5);
        MAX_LEVELS.put(EnchantType.PROSECUTE, 5);
        MAX_LEVELS.put(EnchantType.OVERLOAD, 5);
        MAX_LEVELS.put(EnchantType.TELEKINESIS, 1);
        MAX_LEVELS.put(EnchantType.LOOTING, 4);
        MAX_LEVELS.put(EnchantType.SMELTING_TOUCH, 1);
        MAX_LEVELS.put(EnchantType.MAGNET, 1);
        MAX_LEVELS.put(EnchantType.SILK_TOUCH, 1);
        MAX_LEVELS.put(EnchantType.LUCK_OF_THE_SEA, 7);
        MAX_LEVELS.put(EnchantType.ANGLER, 6);
        MAX_LEVELS.put(EnchantType.FRAIL, 5);
        MAX_LEVELS.put(EnchantType.EXPERTISE, 10);
        MAX_LEVELS.put(EnchantType.CULTIVATING, 10);
        MAX_LEVELS.put(EnchantType.GREEN_THUMB, 5);
        MAX_LEVELS.put(EnchantType.DEDICATION, 4);
        MAX_LEVELS.put(EnchantType.REPLENISH, 1);
        MAX_LEVELS.put(EnchantType.HARVESTING, 6);
        MAX_LEVELS.put(EnchantType.TURBO_WHEAT, 5);
        MAX_LEVELS.put(EnchantType.TURBO_COCO, 5);
        MAX_LEVELS.put(EnchantType.TURBO_CACTUS, 5);
        MAX_LEVELS.put(EnchantType.TURBO_MELON, 5);
        MAX_LEVELS.put(EnchantType.TURBO_PUMPKIN, 5);
        MAX_LEVELS.put(EnchantType.TURBO_WARTS, 5);
        MAX_LEVELS.put(EnchantType.TURBO_MUSHROOMS, 5);
        MAX_LEVELS.put(EnchantType.TURBO_POTATO, 5);
        MAX_LEVELS.put(EnchantType.TURBO_CARROT, 5);
        MAX_LEVELS.put(EnchantType.TURBO_SUGAR_CANE, 5);
        MAX_LEVELS.put(EnchantType.EFFICIENCY, 5);
        MAX_LEVELS.put(EnchantType.FORTUNE, 4);
        MAX_LEVELS.put(EnchantType.PROTECTION, 7);
        MAX_LEVELS.put(EnchantType.THORNS, 3);
        MAX_LEVELS.put(EnchantType.GROWTH, 7);
        MAX_LEVELS.put(EnchantType.FEATHER_FALLING, 7);
        MAX_LEVELS.put(EnchantType.SUGAR_RUSH, 3);
        MAX_LEVELS.put(EnchantType.REJUVENATE, 5);
        MAX_LEVELS.put(EnchantType.LUCK, 7);
        MAX_LEVELS.put(EnchantType.CHANCE, 5);
        MAX_LEVELS.put(EnchantType.ULTIMATE_WISE, 5);
    }

    private static final EnchantManager INSTANCE = new EnchantManager();

    /** Per-player enchant levels; absent entries mean the enchant is not applied. */
    private final Map<UUID, Map<EnchantType, Integer>> playerEnchants = new HashMap<>();

    private EnchantManager() {}

    /**
     * Returns the single shared {@code EnchantManager} instance.
     *
     * @return the singleton instance
     */
    public static EnchantManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the level of the given enchant for the player, or {@code 0} if not applied.
     *
     * @param playerId the player to look up
     * @param type     the enchant type to query
     * @return the enchant level, or {@code 0}
     */
    public int getLevel(UUID playerId, EnchantType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<EnchantType, Integer> enchants = playerEnchants.get(playerId);
        return enchants == null ? 0 : enchants.getOrDefault(type, 0);
    }

    /**
     * Applies an enchant at the given level to the player.
     *
     * @param playerId the player to update
     * @param type     the enchant type to apply
     * @param level    the level to set; must be between 1 and the enchant's max level
     * @throws IllegalArgumentException if the level is out of range
     */
    public void setEnchant(UUID playerId, EnchantType type, int level) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        int max = MAX_LEVELS.getOrDefault(type, 1);
        if (level < 1 || level > max) {
            throw new IllegalArgumentException(
                    "Level " + level + " out of range [1, " + max + "] for " + type);
        }
        playerEnchants.computeIfAbsent(playerId, id -> new EnumMap<>(EnchantType.class))
                .put(type, level);
    }

    /**
     * Removes an enchant from the player.
     *
     * @param playerId the player to update
     * @param type     the enchant type to remove
     * @return {@code true} if the enchant was present, {@code false} otherwise
     */
    public boolean removeEnchant(UUID playerId, EnchantType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<EnchantType, Integer> enchants = playerEnchants.get(playerId);
        if (enchants == null) {
            return false;
        }
        boolean removed = enchants.remove(type) != null;
        if (enchants.isEmpty()) {
            playerEnchants.remove(playerId);
        }
        return removed;
    }

    /**
     * Returns an unmodifiable view of all enchants currently applied to the player.
     *
     * @param playerId the player to look up
     * @return a map of enchant type to level; empty if the player has none
     */
    public Map<EnchantType, Integer> getEnchants(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<EnchantType, Integer> enchants = playerEnchants.get(playerId);
        return enchants == null ? Collections.emptyMap() : Collections.unmodifiableMap(enchants);
    }

    /**
     * Returns the maximum allowed level for the given enchant type.
     *
     * @param type the enchant type to query
     * @return the maximum level
     */
    public int getMaxLevel(EnchantType type) {
        Objects.requireNonNull(type, "type");
        return MAX_LEVELS.getOrDefault(type, 1);
    }

    /**
     * Removes all enchant data for the given player.
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerEnchants.remove(playerId) != null;
    }

    // --- SkyBlockEnchant bridge methods ---

    /**
     * Returns the level of the given enchant for the player, or {@code 0} if not applied.
     *
     * @param playerId the player to look up
     * @param enchant  the enchant to query
     * @return the enchant level, or {@code 0}
     */
    public int getLevel(UUID playerId, SkyBlockEnchant enchant) {
        Objects.requireNonNull(enchant, "enchant");
        return getLevel(playerId, EnchantType.valueOf(enchant.name()));
    }

    /**
     * Applies an enchant at the given level to the player.
     *
     * @param playerId the player to update
     * @param enchant  the enchant to apply
     * @param level    the level to set; must be between 1 and the enchant's max level
     * @throws IllegalArgumentException if the level is out of range
     */
    public void setEnchant(UUID playerId, SkyBlockEnchant enchant, int level) {
        Objects.requireNonNull(enchant, "enchant");
        if (level < 1 || level > enchant.getMaxLevel()) {
            throw new IllegalArgumentException(
                    "Level " + level + " out of range [1, " + enchant.getMaxLevel() + "] for " + enchant);
        }
        playerEnchants.computeIfAbsent(playerId, id -> new EnumMap<>(EnchantType.class))
                .put(EnchantType.valueOf(enchant.name()), level);
    }

    /**
     * Removes an enchant from the player.
     *
     * @param playerId the player to update
     * @param enchant  the enchant to remove
     * @return {@code true} if the enchant was present, {@code false} otherwise
     */
    public boolean removeEnchant(UUID playerId, SkyBlockEnchant enchant) {
        Objects.requireNonNull(enchant, "enchant");
        return removeEnchant(playerId, EnchantType.valueOf(enchant.name()));
    }
}
