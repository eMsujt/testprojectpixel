package com.skyblock.core.enchant;

import com.skyblock.core.enchanting.EnchantingManager;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.EnchantmentManager} instead.
 */
@Deprecated
public final class EnchantmentManager {

    /** Every SkyBlock enchant type with display name. */
    public enum SkyBlockEnchantment {
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

        SkyBlockEnchantment(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    private static final EnchantmentManager INSTANCE = new EnchantmentManager();
    private final com.skyblock.core.manager.EnchantmentManager canonical =
            com.skyblock.core.manager.EnchantmentManager.getInstance();

    private EnchantmentManager() {}

    public static EnchantmentManager getInstance() {
        return INSTANCE;
    }

    private static EnchantingManager.SkyBlockEnchantment toCanonical(SkyBlockEnchantment type) {
        return EnchantingManager.SkyBlockEnchantment.valueOf(type.name());
    }

    public int getLevel(UUID playerId, SkyBlockEnchantment type) {
        return canonical.getLevel(playerId, toCanonical(type));
    }

    public void setEnchantment(UUID playerId, SkyBlockEnchantment type, int level) {
        canonical.setEnchantment(playerId, toCanonical(type), level);
    }

    public boolean removeEnchantment(UUID playerId, SkyBlockEnchantment type) {
        return canonical.removeEnchantment(playerId, toCanonical(type));
    }

    public Map<SkyBlockEnchantment, Integer> getEnchantments(UUID playerId) {
        Map<SkyBlockEnchantment, Integer> result = new EnumMap<>(SkyBlockEnchantment.class);
        canonical.getEnchantments(playerId).forEach((k, v) -> {
            try { result.put(SkyBlockEnchantment.valueOf(k.name()), v); }
            catch (IllegalArgumentException ignored) {}
        });
        return Collections.unmodifiableMap(result);
    }

    public int getMaxLevel(SkyBlockEnchantment type) {
        return canonical.getMaxLevel(toCanonical(type));
    }

    public boolean remove(UUID playerId) {
        return canonical.remove(playerId);
    }

    public List<String> getEnchantingHistory(UUID playerId) {
        return canonical.getEnchantingHistory(playerId);
    }
}
