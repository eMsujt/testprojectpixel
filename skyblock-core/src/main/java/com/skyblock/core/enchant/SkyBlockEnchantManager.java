package com.skyblock.core.enchant;

import com.skyblock.core.enchanting.EnchantingManager;
import com.skyblock.core.enchant.manager.EnchantmentManager;

import java.util.Objects;
import java.util.UUID;

/**
 * Singleton that applies SkyBlock enchant effects for a given player.
 *
 * <p>Delegates per-player enchant state to {@link EnchantmentManager} and exposes
 * effect-calculation helpers consumed by {@link EnchantmentListener}.</p>
 */
public final class SkyBlockEnchantManager {

    private static final SkyBlockEnchantManager INSTANCE = new SkyBlockEnchantManager();

    private final EnchantmentManager canonical = EnchantmentManager.getInstance();

    private SkyBlockEnchantManager() {}

    public static SkyBlockEnchantManager getInstance() {
        return INSTANCE;
    }

    /**
     * Applies combat enchant multipliers to {@code baseDamage} for the attacker.
     *
     * <p>Applied in order: SHARPNESS → CRITICAL → EXECUTE (below 20% health) → GIANT_KILLER.</p>
     */
    public double applyCombatEnchants(UUID attackerId, double baseDamage, double targetHealthFraction) {
        Objects.requireNonNull(attackerId, "attackerId");
        double damage = baseDamage;

        int sharpness = canonical.getLevel(attackerId, EnchantingManager.SkyBlockEnchantment.SHARPNESS);
        if (sharpness > 0) damage += sharpness * 2.0;

        int critical = canonical.getLevel(attackerId, EnchantingManager.SkyBlockEnchantment.CRITICAL);
        if (critical > 0) damage *= (1.0 + critical * 0.10);

        int execute = canonical.getLevel(attackerId, EnchantingManager.SkyBlockEnchantment.EXECUTE);
        if (execute > 0 && targetHealthFraction < 0.20) damage *= (1.0 + execute * 0.10);

        int giantKiller = canonical.getLevel(attackerId, EnchantingManager.SkyBlockEnchantment.GIANT_KILLER);
        if (giantKiller > 0) damage *= (1.0 + giantKiller * 0.10);

        return damage;
    }

    /** Returns the fortune bonus multiplier for block-break drops. */
    public double getFortuneMultiplier(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return 1.0 + canonical.getLevel(playerId, EnchantingManager.SkyBlockEnchantment.FORTUNE) * 0.10;
    }

    /** Returns {@code true} if the player has TELEKINESIS active (level ≥ 1). */
    public boolean hasTelekinesis(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return canonical.getLevel(playerId, EnchantingManager.SkyBlockEnchantment.TELEKINESIS) > 0;
    }
}
