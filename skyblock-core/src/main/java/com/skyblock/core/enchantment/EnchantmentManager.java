package com.skyblock.core.enchantment;

import com.skyblock.core.enchanting.EnchantingManager;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.EnchantmentManager} instead.
 */
@Deprecated
public final class EnchantmentManager {

    /** Every SkyBlock enchant type. */
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

    private static final EnchantmentManager INSTANCE = new EnchantmentManager();
    private final com.skyblock.core.manager.EnchantmentManager canonical =
            com.skyblock.core.manager.EnchantmentManager.getInstance();

    private EnchantmentManager() {}

    public static EnchantmentManager getInstance() {
        return INSTANCE;
    }

    private static EnchantingManager.SkyBlockEnchantment toCanonical(EnchantType type) {
        return EnchantingManager.SkyBlockEnchantment.valueOf(type.name());
    }

    public int getLevel(UUID playerId, EnchantType type) {
        return canonical.getLevel(playerId, toCanonical(type));
    }

    public void setEnchantment(UUID playerId, EnchantType type, int level) {
        canonical.setEnchantment(playerId, toCanonical(type), level);
    }

    public boolean removeEnchantment(UUID playerId, EnchantType type) {
        return canonical.removeEnchantment(playerId, toCanonical(type));
    }

    public Map<EnchantType, Integer> getEnchantments(UUID playerId) {
        Map<EnchantType, Integer> result = new EnumMap<>(EnchantType.class);
        canonical.getEnchantments(playerId).forEach((k, v) -> {
            try { result.put(EnchantType.valueOf(k.name()), v); }
            catch (IllegalArgumentException ignored) {}
        });
        return Collections.unmodifiableMap(result);
    }

    public int getMaxLevel(EnchantType type) {
        return canonical.getMaxLevel(toCanonical(type));
    }

    public boolean remove(UUID playerId) {
        return canonical.remove(playerId);
    }
}
