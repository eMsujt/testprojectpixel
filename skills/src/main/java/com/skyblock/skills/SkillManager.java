package com.skyblock.skills;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Tracks per-player skill progression: the XP a player has earned in each
 * {@link SkillType} and the levels unlocked from those totals.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class SkillManager {

    /** A skill a player can level up by performing its activity. */
    public enum SkillType {
        /** Harvesting crops and breeding farm animals. */
        FARMING,
        /** Mining ores, stone, and gemstones. */
        MINING,
        /** Slaying mobs and bosses. */
        COMBAT,
        /** Chopping logs and gathering forest resources. */
        FORAGING,
        /** Catching fish and sea creatures. */
        FISHING,
        /** Enchanting items at an enchantment table. */
        ENCHANTING,
        /** Brewing potions. */
        ALCHEMY,
        /** Levelling up pets. */
        TAMING,
        /** Crafting items. */
        CARPENTRY
    }

    /** The highest level a skill can reach. */
    public static final int MAX_LEVEL = 50;

    /** Cumulative XP required to reach each level, indexed by level - 1. */
    private static final double[] XP_PER_LEVEL = new double[MAX_LEVEL];

    static {
        double cumulative = 0;
        for (int level = 0; level < MAX_LEVEL; level++) {
            cumulative += 50 * Math.pow(1.15, level);
            XP_PER_LEVEL[level] = cumulative;
        }
    }

    private final Map<UUID, Map<SkillType, Double>> experience = new HashMap<>();

    /**
     * Adds XP to the player's progress for the given skill, e.g. after mining
     * ore or slaying a mob.
     *
     * @param playerId the player gaining XP
     * @param skill    the skill being progressed
     * @param amount   the XP to add, must not be negative
     * @return the player's total XP for the skill after the addition
     * @throws IllegalArgumentException if {@code amount} is negative or not finite
     * @throws NullPointerException if {@code playerId} or {@code skill} is {@code null}
     */
    public double addExperience(UUID playerId, SkillType skill, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(skill, "skill");
        if (amount < 0 || !Double.isFinite(amount)) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<SkillType, Double> totals =
                experience.computeIfAbsent(playerId, id -> new EnumMap<>(SkillType.class));
        double total = totals.getOrDefault(skill, 0.0) + amount;
        totals.put(skill, total);
        return total;
    }

    /**
     * Returns how much XP the player has earned in the given skill.
     *
     * @param playerId the player to look up
     * @param skill    the skill to look up
     * @return the total XP earned, {@code 0} if the player has none
     */
    public double getExperience(UUID playerId, SkillType skill) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(skill, "skill");
        return experience.getOrDefault(playerId, Map.of()).getOrDefault(skill, 0.0);
    }

    /**
     * Returns the level the player has reached in the given skill.
     *
     * @param playerId the player to look up
     * @param skill    the skill to look up
     * @return the level between {@code 0} and {@link #MAX_LEVEL}
     */
    public int getLevel(UUID playerId, SkillType skill) {
        double xp = getExperience(playerId, skill);
        int level = 0;
        while (level < MAX_LEVEL && xp >= XP_PER_LEVEL[level]) {
            level++;
        }
        return level;
    }

    /**
     * Returns how much more XP the player needs to reach the next level of the
     * given skill.
     *
     * @param playerId the player to look up
     * @param skill    the skill to look up
     * @return the missing XP, or {@code 0} if the skill is at {@link #MAX_LEVEL}
     */
    public double getExperienceToNextLevel(UUID playerId, SkillType skill) {
        int level = getLevel(playerId, skill);
        if (level >= MAX_LEVEL) {
            return 0;
        }
        return XP_PER_LEVEL[level] - getExperience(playerId, skill);
    }

    /**
     * Resets all of the player's skill progress back to zero.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had progress to reset, {@code false} otherwise
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return experience.remove(playerId) != null;
    }
}
