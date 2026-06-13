package com.skyblock.core.enchantment;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing SkyBlock enchantments and their effects.
 *
 * <p>Tracks which {@link SkyBlockEnchant} values at which levels are active for each player
 * and exposes effect-calculation helpers consumed by listeners.</p>
 */
public final class SkyBlockEnchantManager {

    /** Every SkyBlock enchant type. */
    public enum SkyBlockEnchant {
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
    private static final Map<SkyBlockEnchant, Integer> MAX_LEVELS;

    static {
        MAX_LEVELS = new EnumMap<>(SkyBlockEnchant.class);
        MAX_LEVELS.put(SkyBlockEnchant.SHARPNESS, 7);
        MAX_LEVELS.put(SkyBlockEnchant.CRITICAL, 7);
        MAX_LEVELS.put(SkyBlockEnchant.SMITE, 7);
        MAX_LEVELS.put(SkyBlockEnchant.BANE_OF_ARTHROPODS, 7);
        MAX_LEVELS.put(SkyBlockEnchant.FIRST_STRIKE, 4);
        MAX_LEVELS.put(SkyBlockEnchant.GIANT_KILLER, 7);
        MAX_LEVELS.put(SkyBlockEnchant.ENDER_SLAYER, 7);
        MAX_LEVELS.put(SkyBlockEnchant.DRAGON_HUNTER, 5);
        MAX_LEVELS.put(SkyBlockEnchant.THUNDERLORD, 7);
        MAX_LEVELS.put(SkyBlockEnchant.VAMPIRISM, 6);
        MAX_LEVELS.put(SkyBlockEnchant.LIFE_STEAL, 5);
        MAX_LEVELS.put(SkyBlockEnchant.LETHALITY, 6);
        MAX_LEVELS.put(SkyBlockEnchant.EXECUTE, 5);
        MAX_LEVELS.put(SkyBlockEnchant.PROSECUTE, 5);
        MAX_LEVELS.put(SkyBlockEnchant.OVERLOAD, 5);
        MAX_LEVELS.put(SkyBlockEnchant.TELEKINESIS, 1);
        MAX_LEVELS.put(SkyBlockEnchant.LOOTING, 4);
        MAX_LEVELS.put(SkyBlockEnchant.SMELTING_TOUCH, 1);
        MAX_LEVELS.put(SkyBlockEnchant.MAGNET, 1);
        MAX_LEVELS.put(SkyBlockEnchant.SILK_TOUCH, 1);
        MAX_LEVELS.put(SkyBlockEnchant.LUCK_OF_THE_SEA, 7);
        MAX_LEVELS.put(SkyBlockEnchant.ANGLER, 6);
        MAX_LEVELS.put(SkyBlockEnchant.FRAIL, 5);
        MAX_LEVELS.put(SkyBlockEnchant.EXPERTISE, 10);
        MAX_LEVELS.put(SkyBlockEnchant.CULTIVATING, 10);
        MAX_LEVELS.put(SkyBlockEnchant.GREEN_THUMB, 5);
        MAX_LEVELS.put(SkyBlockEnchant.DEDICATION, 4);
        MAX_LEVELS.put(SkyBlockEnchant.REPLENISH, 1);
        MAX_LEVELS.put(SkyBlockEnchant.HARVESTING, 6);
        MAX_LEVELS.put(SkyBlockEnchant.TURBO_WHEAT, 5);
        MAX_LEVELS.put(SkyBlockEnchant.TURBO_COCO, 5);
        MAX_LEVELS.put(SkyBlockEnchant.TURBO_CACTUS, 5);
        MAX_LEVELS.put(SkyBlockEnchant.TURBO_MELON, 5);
        MAX_LEVELS.put(SkyBlockEnchant.TURBO_PUMPKIN, 5);
        MAX_LEVELS.put(SkyBlockEnchant.TURBO_WARTS, 5);
        MAX_LEVELS.put(SkyBlockEnchant.TURBO_MUSHROOMS, 5);
        MAX_LEVELS.put(SkyBlockEnchant.TURBO_POTATO, 5);
        MAX_LEVELS.put(SkyBlockEnchant.TURBO_CARROT, 5);
        MAX_LEVELS.put(SkyBlockEnchant.TURBO_SUGAR_CANE, 5);
        MAX_LEVELS.put(SkyBlockEnchant.EFFICIENCY, 5);
        MAX_LEVELS.put(SkyBlockEnchant.FORTUNE, 4);
        MAX_LEVELS.put(SkyBlockEnchant.PROTECTION, 7);
        MAX_LEVELS.put(SkyBlockEnchant.THORNS, 3);
        MAX_LEVELS.put(SkyBlockEnchant.GROWTH, 7);
        MAX_LEVELS.put(SkyBlockEnchant.FEATHER_FALLING, 7);
        MAX_LEVELS.put(SkyBlockEnchant.SUGAR_RUSH, 3);
        MAX_LEVELS.put(SkyBlockEnchant.REJUVENATE, 5);
        MAX_LEVELS.put(SkyBlockEnchant.LUCK, 7);
        MAX_LEVELS.put(SkyBlockEnchant.CHANCE, 5);
        MAX_LEVELS.put(SkyBlockEnchant.ULTIMATE_WISE, 5);
    }

    private static final SkyBlockEnchantManager INSTANCE = new SkyBlockEnchantManager();

    /** Per-player enchant levels; absent entries mean the enchant is not applied. */
    private final Map<UUID, Map<SkyBlockEnchant, Integer>> playerEnchants = new HashMap<>();

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
     * Returns the level of the given enchant for the player, or {@code 0} if not applied.
     *
     * @param playerId the player to look up
     * @param enchant  the enchant to query
     * @return the enchant level, or {@code 0}
     */
    public int getLevel(UUID playerId, SkyBlockEnchant enchant) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(enchant, "enchant");
        Map<SkyBlockEnchant, Integer> enchants = playerEnchants.get(playerId);
        return enchants == null ? 0 : enchants.getOrDefault(enchant, 0);
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
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(enchant, "enchant");
        int max = MAX_LEVELS.getOrDefault(enchant, 1);
        if (level < 1 || level > max) {
            throw new IllegalArgumentException(
                    "Level " + level + " out of range [1, " + max + "] for " + enchant);
        }
        playerEnchants.computeIfAbsent(playerId, id -> new EnumMap<>(SkyBlockEnchant.class))
                .put(enchant, level);
    }

    /**
     * Removes an enchant from the player.
     *
     * @param playerId the player to update
     * @param enchant  the enchant to remove
     * @return {@code true} if the enchant was present, {@code false} otherwise
     */
    public boolean removeEnchant(UUID playerId, SkyBlockEnchant enchant) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(enchant, "enchant");
        Map<SkyBlockEnchant, Integer> enchants = playerEnchants.get(playerId);
        if (enchants == null) {
            return false;
        }
        boolean removed = enchants.remove(enchant) != null;
        if (enchants.isEmpty()) {
            playerEnchants.remove(playerId);
        }
        return removed;
    }

    /**
     * Returns an unmodifiable view of all enchants currently applied to the player.
     *
     * @param playerId the player to look up
     * @return a map of enchant to level; empty if the player has none
     */
    public Map<SkyBlockEnchant, Integer> getEnchants(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<SkyBlockEnchant, Integer> enchants = playerEnchants.get(playerId);
        return enchants == null ? Collections.emptyMap() : Collections.unmodifiableMap(enchants);
    }

    /**
     * Returns the maximum allowed level for the given enchant.
     *
     * @param enchant the enchant to query
     * @return the maximum level
     */
    public int getMaxLevel(SkyBlockEnchant enchant) {
        Objects.requireNonNull(enchant, "enchant");
        return MAX_LEVELS.getOrDefault(enchant, 1);
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

    /**
     * Applies combat enchant multipliers to {@code baseDamage} for the attacker.
     *
     * <p>Applied in order: SHARPNESS → CRITICAL → EXECUTE (below 20 % health) → GIANT_KILLER.</p>
     *
     * @param attackerId            UUID of the attacking player
     * @param baseDamage            raw damage before enchant bonuses
     * @param targetHealthFraction  current health of the target as a fraction of max (0–1)
     * @return damage after enchant bonuses
     */
    public double applyCombatEnchants(UUID attackerId, double baseDamage, double targetHealthFraction) {
        Objects.requireNonNull(attackerId, "attackerId");
        double damage = baseDamage;

        // SHARPNESS: +2 per level
        int sharpness = getLevel(attackerId, SkyBlockEnchant.SHARPNESS);
        if (sharpness > 0) {
            damage += sharpness * 2.0;
        }

        // CRITICAL: +10% crit damage per level
        int critical = getLevel(attackerId, SkyBlockEnchant.CRITICAL);
        if (critical > 0) {
            damage *= (1.0 + critical * 0.10);
        }

        // EXECUTE: +10% bonus per level when target is below 20% health
        int execute = getLevel(attackerId, SkyBlockEnchant.EXECUTE);
        if (execute > 0 && targetHealthFraction < 0.20) {
            damage *= (1.0 + execute * 0.10);
        }

        // GIANT_KILLER: +10% per level
        int giantKiller = getLevel(attackerId, SkyBlockEnchant.GIANT_KILLER);
        if (giantKiller > 0) {
            damage *= (1.0 + giantKiller * 0.10);
        }

        return damage;
    }

    /**
     * Returns the fortune bonus multiplier for block-break drops.
     *
     * @param playerId the player breaking the block
     * @return a multiplier ≥ 1.0
     */
    public double getFortuneMultiplier(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int fortune = getLevel(playerId, SkyBlockEnchant.FORTUNE);
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
        return getLevel(playerId, SkyBlockEnchant.TELEKINESIS) > 0;
    }
}
