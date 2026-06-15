package com.skyblock.core.enchant;

import com.skyblock.core.enchanting.EnchantingManager;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.EnchantmentManager} instead.
 */
@Deprecated
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
        SHARPNESS, CRITICAL, SMITE, BANE_OF_ARTHROPODS, FIRST_STRIKE, GIANT_KILLER,
        ENDER_SLAYER, DRAGON_HUNTER, THUNDERLORD, VAMPIRISM, LIFE_STEAL, LETHALITY,
        EXECUTE, PROSECUTE, OVERLOAD,
        // Utility
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
        LUCK, CHANCE, ULTIMATE_WISE
    }

    private static final EnchantManager INSTANCE = new EnchantManager();
    private final com.skyblock.core.manager.EnchantmentManager canonical =
            com.skyblock.core.manager.EnchantmentManager.getInstance();

    private EnchantManager() {}

    public static EnchantManager getInstance() {
        return INSTANCE;
    }

    private static EnchantingManager.SkyBlockEnchantment canonical(EnchantType t) {
        return EnchantingManager.SkyBlockEnchantment.valueOf(t.name());
    }

    private static EnchantingManager.SkyBlockEnchantment canonical(SkyBlockEnchant e) {
        return EnchantingManager.SkyBlockEnchantment.valueOf(e.name());
    }

    public int getLevel(UUID playerId, EnchantType type) {
        return canonical.getLevel(playerId, canonical(type));
    }

    public void setEnchant(UUID playerId, EnchantType type, int level) {
        canonical.setEnchantment(playerId, canonical(type), level);
    }

    public boolean removeEnchant(UUID playerId, EnchantType type) {
        return canonical.removeEnchantment(playerId, canonical(type));
    }

    public Map<EnchantType, Integer> getEnchants(UUID playerId) {
        Map<EnchantType, Integer> result = new EnumMap<>(EnchantType.class);
        canonical.getEnchantments(playerId).forEach((k, v) -> {
            try { result.put(EnchantType.valueOf(k.name()), v); }
            catch (IllegalArgumentException ignored) {}
        });
        return Collections.unmodifiableMap(result);
    }

    public int getMaxLevel(EnchantType type) {
        return canonical.getMaxLevel(canonical(type));
    }

    public boolean remove(UUID playerId) {
        return canonical.remove(playerId);
    }

    public int getLevel(UUID playerId, SkyBlockEnchant enchant) {
        return canonical.getLevel(playerId, canonical(enchant));
    }

    public void setEnchant(UUID playerId, SkyBlockEnchant enchant, int level) {
        canonical.setEnchantment(playerId, canonical(enchant), level);
    }

    public boolean removeEnchant(UUID playerId, SkyBlockEnchant enchant) {
        return canonical.removeEnchantment(playerId, canonical(enchant));
    }
}
