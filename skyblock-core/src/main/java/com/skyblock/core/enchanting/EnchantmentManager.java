package com.skyblock.core.enchanting;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.EnchantmentManager} instead.
 */
@Deprecated
public final class EnchantmentManager {

    /** Every SkyBlock enchantment with display name. */
    public enum SkyBlockEnchantment {
        SHARPNESS("Sharpness"),
        CRITICAL("Critical"),
        SMITE("Smite"),
        BANE_OF_ARTHROPODS("Bane of Arthropods"),
        FIRST_STRIKE("First Strike"),
        GIANT_KILLER("Giant Killer"),
        ENDER_SLAYER("Ender Slayer"),
        DRAGON_HUNTER("Dragon Hunter"),
        THUNDERLORD("Thunderlord"),
        TELEKINESIS("Telekinesis"),
        LOOTING("Looting"),
        LUCK("Luck"),
        LUCK_OF_THE_SEA("Luck of the Sea"),
        ANGLER("Angler"),
        FRAIL("Frail"),
        MAGNET("Magnet"),
        EXPERTISE("Expertise"),
        SMELTING_TOUCH("Smelting Touch"),
        EFFICIENCY("Efficiency"),
        FORTUNE("Fortune"),
        SILK_TOUCH("Silk Touch"),
        PROTECTION("Protection"),
        THORNS("Thorns"),
        GROWTH("Growth"),
        FEATHER_FALLING("Feather Falling"),
        SUGAR_RUSH("Sugar Rush"),
        REJUVENATE("Rejuvenate"),
        CHANCE("Chance"),
        OVERLOAD("Overload"),
        ULTIMATE_WISE("Ultimate Wise"),
        VAMPIRISM("Vampirism"),
        LIFE_STEAL("Life Steal"),
        LETHALITY("Lethality"),
        PROSECUTE("Prosecute"),
        EXECUTE("Execute");

        private final String displayName;

        SkyBlockEnchantment(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
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
}
