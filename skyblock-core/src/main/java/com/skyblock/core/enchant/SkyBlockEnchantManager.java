package com.skyblock.core.enchant;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton that applies SkyBlock enchant effects for a given player.
 *
 * <p>Delegates per-player enchant state to {@link EnchantManager} and exposes
 * effect-calculation helpers consumed by {@link SkyBlockEnchantListener}.</p>
 */
public final class SkyBlockEnchantManager {

    private static final SkyBlockEnchantManager INSTANCE = new SkyBlockEnchantManager();

    private final EnchantManager enchantManager = EnchantManager.getInstance();

    private SkyBlockEnchantManager() {}

    /**
     * Returns the single shared {@code SkyBlockEnchantManager} instance.
     *
     * @return the singleton instance
     */
    public static SkyBlockEnchantManager getInstance() {
        return INSTANCE;
    }

    /**
     * Applies combat enchant multipliers to {@code baseDamage} for the attacker.
     *
     * <p>Applied in order: SHARPNESS → CRITICAL → EXECUTE (below 20 % health).</p>
     *
     * @param attackerId  UUID of the attacking player
     * @param baseDamage  raw damage before enchant bonuses
     * @param targetHealthFraction  current health of the target as a fraction of max (0–1)
     * @return damage after enchant bonuses
     */
    public double applyCombatEnchants(UUID attackerId, double baseDamage, double targetHealthFraction) {
        Objects.requireNonNull(attackerId, "attackerId");
        double damage = baseDamage;

        // SHARPNESS: +2% per level
        int sharpness = enchantManager.getLevel(attackerId, EnchantManager.EnchantType.SHARPNESS);
        if (sharpness > 0) {
            damage += sharpness * 2.0;
        }

        // CRITICAL: +10% crit damage per level (always on — companion to CombatManager crit roll)
        int critical = enchantManager.getLevel(attackerId, EnchantManager.EnchantType.CRITICAL);
        if (critical > 0) {
            damage *= (1.0 + critical * 0.10);
        }

        // EXECUTE: +10% bonus damage per level when target is below 20% health
        int execute = enchantManager.getLevel(attackerId, EnchantManager.EnchantType.EXECUTE);
        if (execute > 0 && targetHealthFraction < 0.20) {
            damage *= (1.0 + execute * 0.10);
        }

        // GIANT_KILLER: +10% per level (flat bonus against high-health targets)
        int giantKiller = enchantManager.getLevel(attackerId, EnchantManager.EnchantType.GIANT_KILLER);
        if (giantKiller > 0) {
            damage *= (1.0 + giantKiller * 0.10);
        }

        return damage;
    }

    /**
     * Returns the fortune bonus multiplier for block-break drops.
     *
     * @param playerId the player breaking the block
     * @return a multiplier ≥ 1.0; level-3 FORTUNE yields 1.3, etc.
     */
    public double getFortuneMultiplier(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int fortune = enchantManager.getLevel(playerId, EnchantManager.EnchantType.FORTUNE);
        return 1.0 + fortune * 0.10;
    }

    /**
     * Returns {@code true} if the player has TELEKINESIS active (level ≥ 1).
     *
     * @param playerId the player to check
     * @return {@code true} if TELEKINESIS is applied
     */
    public boolean hasTelekinesis(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return enchantManager.getLevel(playerId, EnchantManager.EnchantType.TELEKINESIS) > 0;
    }

    /**
     * Returns a read-only view of all enchants currently active for the player.
     *
     * @param playerId the player to look up
     * @return map of enchant type to level; empty if none
     */
    public Map<EnchantManager.EnchantType, Integer> getActiveEnchants(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return enchantManager.getEnchants(playerId);
    }
}
