package com.skyblock.core.skills;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's XP and level for every {@link SkillType}.
 *
 * <p>Level {@code n} requires {@code 50 * n^2} cumulative XP (same curve as
 * {@code FishingManager}). Max level is 50 for all skills.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class SkillManager {

    /** Every skill tracked in SkyBlock. */
    public enum SkillType {
        FARMING, MINING, COMBAT, FORAGING, FISHING,
        ENCHANTING, ALCHEMY, TAMING, CARPENTRY, RUNECRAFTING
    }

    public static final int MAX_LEVEL = 50;

    private static final SkillManager INSTANCE = new SkillManager();

    /** Per-player XP map: player → (skill → total XP). */
    private final Map<UUID, Map<SkillType, Double>> xpMap = new HashMap<>();
    /** Per-player level cache: player → (skill → level). */
    private final Map<UUID, Map<SkillType, Integer>> levelMap = new HashMap<>();

    private SkillManager() {
    }

    /**
     * Returns the single shared {@code SkillManager} instance.
     *
     * @return the singleton instance
     */
    public static SkillManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds XP to the player's total for the given skill and updates the level.
     *
     * @param playerId the player receiving XP
     * @param skill    the skill being progressed
     * @param amount   XP to add, must not be negative
     * @return the player's new total XP for the skill
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public double addXp(UUID playerId, SkillType skill, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(skill, "skill");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<SkillType, Double> xp = xpMap.computeIfAbsent(
                playerId, id -> new EnumMap<>(SkillType.class));
        double total = xp.merge(skill, amount, Double::sum);
        levelMap.computeIfAbsent(playerId, id -> new EnumMap<>(SkillType.class))
                .put(skill, computeLevel(total));
        return total;
    }

    /**
     * Returns the player's current XP for the given skill.
     *
     * @param playerId the player to look up
     * @param skill    the skill to look up
     * @return total XP, {@code 0.0} if none recorded
     */
    public double getXp(UUID playerId, SkillType skill) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(skill, "skill");
        Map<SkillType, Double> xp = xpMap.get(playerId);
        return xp == null ? 0.0 : xp.getOrDefault(skill, 0.0);
    }

    /**
     * Returns the player's current level for the given skill (1–{@value #MAX_LEVEL}).
     *
     * @param playerId the player to look up
     * @param skill    the skill to look up
     * @return skill level
     */
    public int getLevel(UUID playerId, SkillType skill) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(skill, "skill");
        Map<SkillType, Integer> levels = levelMap.get(playerId);
        return levels == null ? 1 : levels.getOrDefault(skill, 1);
    }

    /**
     * Computes the level for the given total XP.
     * Formula: level {@code n} requires {@code 50 * n^2} cumulative XP.
     *
     * @param totalXp total accumulated XP
     * @return level between 1 and {@value #MAX_LEVEL}
     */
    private static int computeLevel(double totalXp) {
        int level = 1;
        while (level < MAX_LEVEL) {
            double threshold = 50.0 * (level + 1) * (level + 1);
            if (totalXp < threshold) {
                break;
            }
            level++;
        }
        return level;
    }
}
