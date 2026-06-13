package com.skyblock.core.enchanting;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing SkyBlock enchantments for the enchanting skill system.
 *
 * <p>Tracks which enchant types at which levels are active for each player.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class EnchantingManager {

    /** Canonical 30-entry enchant catalogue used for enchanting-table interactions. */
    public enum EnchantType {
        // Combat
        SHARPNESS(        "Sharpness",          7),
        CRITICAL(         "Critical",           7),
        SMITE(            "Smite",              7),
        BANE_OF_ARTHROPODS("Bane of Arthropods", 7),
        FIRST_STRIKE(     "First Strike",       4),
        GIANT_KILLER(     "Giant Killer",       7),
        ENDER_SLAYER(     "Ender Slayer",       7),
        DRAGON_HUNTER(    "Dragon Hunter",      5),
        THUNDERLORD(      "Thunderlord",        7),
        EXECUTE(          "Execute",            5),
        // Utility / Special
        TELEKINESIS(      "Telekinesis",        1),
        LOOTING(          "Looting",            4),
        SMELTING_TOUCH(   "Smelting Touch",     1),
        MAGNET(           "Magnet",             1),
        LIFE_STEAL(       "Life Steal",         5),
        // Fishing
        LUCK_OF_THE_SEA(  "Luck of the Sea",    7),
        ANGLER(           "Angler",             6),
        FRAIL(            "Frail",              5),
        EXPERTISE(        "Expertise",         10),
        // Farming
        CULTIVATING(      "Cultivating",       10),
        GREEN_THUMB(      "Green Thumb",        5),
        HARVESTING(       "Harvesting",         6),
        // Mining / Tool
        EFFICIENCY(       "Efficiency",         5),
        FORTUNE(          "Fortune",            4),
        SILK_TOUCH(       "Silk Touch",         1),
        // Armor
        PROTECTION(       "Protection",         7),
        THORNS(           "Thorns",             3),
        GROWTH(           "Growth",             7),
        FEATHER_FALLING(  "Feather Falling",    7),
        REJUVENATE(       "Rejuvenate",         5);

        private final String displayName;
        private final int maxLevel;

        EnchantType(String displayName, int maxLevel) {
            this.displayName = displayName;
            this.maxLevel = maxLevel;
        }

        public String getDisplayName() { return displayName; }
        public int getMaxLevel() { return maxLevel; }
    }

    /** Simple enchant-name enum for category lookups and tab completion. */
    public enum SkyBlockEnchant {
        // Combat
        SHARPNESS, CRITICAL, SMITE, BANE_OF_ARTHROPODS, FIRST_STRIKE,
        GIANT_KILLER, ENDER_SLAYER, DRAGON_HUNTER, THUNDERLORD, VAMPIRISM,
        LIFE_STEAL, LETHALITY, EXECUTE, PROSECUTE, OVERLOAD,
        // Utility / Special
        TELEKINESIS, LOOTING, SMELTING_TOUCH, MAGNET, SILK_TOUCH,
        // Fishing
        LUCK_OF_THE_SEA, ANGLER, FRAIL, EXPERTISE,
        // Farming
        CULTIVATING, GREEN_THUMB, DEDICATION, REPLENISH, HARVESTING,
        TURBO_WHEAT, TURBO_COCO, TURBO_CACTUS, TURBO_MELON, TURBO_PUMPKIN,
        TURBO_WARTS, TURBO_MUSHROOMS, TURBO_POTATO, TURBO_CARROT, TURBO_SUGAR_CANE,
        // Mining / Tool
        EFFICIENCY, FORTUNE,
        // Armor
        PROTECTION, THORNS, GROWTH, FEATHER_FALLING, SUGAR_RUSH, REJUVENATE,
        // Misc
        LUCK, CHANCE, ULTIMATE_WISE,
        // Dungeon / Extra
        SHREDDER, SCAVENGER, SOUL_EATER, VENOMOUS, VICIOUS
    }

    /** Every SkyBlock enchant type with display name and maximum level. */
    public enum SkyBlockEnchantment {
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
        // Utility / Special
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
        ULTIMATE_WISE("Ultimate Wise", 5),
        // Dungeon / Extra
        SHREDDER("Shredder", 5),
        SCAVENGER("Scavenger", 4),
        SOUL_EATER("Soul Eater", 5),
        VENOMOUS("Venomous", 5),
        VICIOUS("Vicious", 5);

        private final String displayName;
        private final int maxLevel;

        SkyBlockEnchantment(String displayName, int maxLevel) {
            this.displayName = displayName;
            this.maxLevel = maxLevel;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getMaxLevel() {
            return maxLevel;
        }
    }

    private static final EnchantingManager INSTANCE = new EnchantingManager();

    /** Per-player enchantment levels; absent entries mean the enchantment is not applied. */
    private final Map<UUID, Map<SkyBlockEnchantment, Integer>> playerEnchantments = new HashMap<>();

    private EnchantingManager() {
    }

    public static EnchantingManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the level of the given enchant type for the given player, or
     * {@code 0} if the enchantment is not applied.
     */
    public int getLevel(UUID playerId, SkyBlockEnchantment type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<SkyBlockEnchantment, Integer> enchants = playerEnchantments.get(playerId);
        return enchants == null ? 0 : enchants.getOrDefault(type, 0);
    }

    /**
     * Applies an enchant type at the given level to the player.
     *
     * @throws IllegalArgumentException if the level is out of range
     */
    public void setEnchantment(UUID playerId, SkyBlockEnchantment type, int level) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        int max = type.getMaxLevel();
        if (level < 1 || level > max) {
            throw new IllegalArgumentException(
                    "Level " + level + " out of range [1, " + max + "] for " + type);
        }
        playerEnchantments.computeIfAbsent(playerId, id -> new EnumMap<>(SkyBlockEnchantment.class))
                .put(type, level);
    }

    /**
     * Removes an enchant type from the player.
     *
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
     */
    public Map<SkyBlockEnchantment, Integer> getEnchantments(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<SkyBlockEnchantment, Integer> enchants = playerEnchantments.get(playerId);
        return enchants == null ? Collections.emptyMap() : Collections.unmodifiableMap(enchants);
    }

    /**
     * Returns the maximum allowed level for the given enchant type.
     */
    public int getMaxLevel(SkyBlockEnchantment type) {
        Objects.requireNonNull(type, "type");
        return type.getMaxLevel();
    }

    /**
     * Removes all enchantment data for the given player.
     *
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerEnchantments.remove(playerId) != null;
    }
}
