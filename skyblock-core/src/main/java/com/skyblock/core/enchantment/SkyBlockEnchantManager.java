package com.skyblock.core.enchantment;

import com.skyblock.core.enchanting.EnchantingManager;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.EnchantmentManager} instead.
 */
@Deprecated
public final class SkyBlockEnchantManager {

    /** Every SkyBlock enchant type. */
    public enum SkyBlockEnchant {
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

    private static final SkyBlockEnchantManager INSTANCE = new SkyBlockEnchantManager();
    private final com.skyblock.core.manager.EnchantmentManager canonical =
            com.skyblock.core.manager.EnchantmentManager.getInstance();

    private SkyBlockEnchantManager() {}

    public static SkyBlockEnchantManager getInstance() {
        return INSTANCE;
    }

    private static EnchantingManager.SkyBlockEnchantment toCanonical(SkyBlockEnchant e) {
        return EnchantingManager.SkyBlockEnchantment.valueOf(e.name());
    }

    public int getLevel(UUID playerId, SkyBlockEnchant enchant) {
        return canonical.getLevel(playerId, toCanonical(enchant));
    }

    public void setEnchant(UUID playerId, SkyBlockEnchant enchant, int level) {
        canonical.setEnchantment(playerId, toCanonical(enchant), level);
    }

    public boolean removeEnchant(UUID playerId, SkyBlockEnchant enchant) {
        return canonical.removeEnchantment(playerId, toCanonical(enchant));
    }

    public Map<SkyBlockEnchant, Integer> getEnchants(UUID playerId) {
        Map<SkyBlockEnchant, Integer> result = new EnumMap<>(SkyBlockEnchant.class);
        canonical.getEnchantments(playerId).forEach((k, v) -> {
            try { result.put(SkyBlockEnchant.valueOf(k.name()), v); }
            catch (IllegalArgumentException ignored) {}
        });
        return Collections.unmodifiableMap(result);
    }

    public int getMaxLevel(SkyBlockEnchant enchant) {
        return toCanonical(enchant).getMaxLevel();
    }

    public boolean remove(UUID playerId) {
        return canonical.remove(playerId);
    }

    public double applyCombatEnchants(UUID attackerId, double baseDamage, double targetHealthFraction) {
        Objects.requireNonNull(attackerId, "attackerId");
        double damage = baseDamage;
        int sharpness = getLevel(attackerId, SkyBlockEnchant.SHARPNESS);
        if (sharpness > 0) damage += sharpness * 2.0;
        int critical = getLevel(attackerId, SkyBlockEnchant.CRITICAL);
        if (critical > 0) damage *= (1.0 + critical * 0.10);
        int execute = getLevel(attackerId, SkyBlockEnchant.EXECUTE);
        if (execute > 0 && targetHealthFraction < 0.20) damage *= (1.0 + execute * 0.10);
        int giantKiller = getLevel(attackerId, SkyBlockEnchant.GIANT_KILLER);
        if (giantKiller > 0) damage *= (1.0 + giantKiller * 0.10);
        return damage;
    }

    public double getFortuneMultiplier(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return 1.0 + getLevel(playerId, SkyBlockEnchant.FORTUNE) * 0.10;
    }

    public boolean hasTelekinesis(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return getLevel(playerId, SkyBlockEnchant.TELEKINESIS) > 0;
    }
}
