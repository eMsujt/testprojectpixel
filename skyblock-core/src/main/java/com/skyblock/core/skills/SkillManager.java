package com.skyblock.core.skills;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's XP and level per {@link Skill}.
 *
 * <p>XP is stored per player as an {@link EnumMap} of skill to total XP
 * earned. Not thread-safe; synchronize externally if accessed from multiple
 * threads.</p>
 */
public final class SkillManager {

    /** The highest level a skill can reach. */
    public static final int MAX_LEVEL = 50;

    /** Cumulative XP required to reach each level, indexed by level - 1. */
    private static final long[] XP_PER_LEVEL = {
            50, 175, 375, 675, 1_175, 1_925, 2_925, 4_425, 6_425, 9_925,
            14_925, 22_425, 32_425, 47_425, 67_425, 97_425, 147_425, 222_425,
            322_425, 522_425, 822_425, 1_222_425, 1_722_425, 2_322_425,
            3_022_425, 3_822_425, 4_722_425, 5_822_425, 7_222_425, 8_822_425,
            10_622_425, 12_622_425, 14_922_425, 17_522_425, 20_422_425,
            23_622_425, 27_122_425, 30_922_425, 35_022_425, 39_522_425,
            44_522_425, 50_022_425, 56_022_425, 62_522_425, 69_522_425,
            77_022_425, 85_022_425, 93_522_425, 102_522_425, 111_522_425
    };

    /** Skills tracked in SkyBlock. */
    public enum Skill {
        FARMING,
        MINING,
        COMBAT,
        FORAGING,
        FISHING
    }

    private static final SkillManager INSTANCE = new SkillManager();

    private final Map<UUID, Map<Skill, Long>> playerXp = new HashMap<>();

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
     * Adds XP to the player's total for the given skill.
     *
     * @param playerId the player gaining XP
     * @param skill    the skill being progressed
     * @param amount   the XP to add, must not be negative
     * @return the player's total XP for the skill after the addition
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public long addXp(UUID playerId, Skill skill, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(skill, "skill");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<Skill, Long> totals = playerXp.computeIfAbsent(
                playerId, id -> new EnumMap<>(Skill.class));
        long total = totals.getOrDefault(skill, 0L) + amount;
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
    public long getXp(UUID playerId, Skill skill) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(skill, "skill");
        Map<Skill, Long> totals = playerXp.get(playerId);
        return totals == null ? 0L : totals.getOrDefault(skill, 0L);
    }

    /**
     * Returns the level the player has reached in the given skill.
     *
     * @param playerId the player to look up
     * @param skill    the skill to look up
     * @return the level between {@code 0} and {@link #MAX_LEVEL}
     */
    public int getLevel(UUID playerId, Skill skill) {
        long xp = getXp(playerId, skill);
        int level = 0;
        while (level < MAX_LEVEL && xp >= XP_PER_LEVEL[level]) {
            level++;
        }
        return level;
    }

    /**
     * Returns how much more XP the player needs to reach the next level.
     *
     * @param playerId the player to look up
     * @param skill    the skill to look up
     * @return the missing XP, or {@code 0} if the skill is at {@link #MAX_LEVEL}
     */
    public long getXpToNextLevel(UUID playerId, Skill skill) {
        int level = getLevel(playerId, skill);
        if (level >= MAX_LEVEL) {
            return 0L;
        }
        return XP_PER_LEVEL[level] - getXp(playerId, skill);
    }

    /**
     * Resets all of the player's skill XP.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had XP to reset, {@code false} otherwise
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerXp.remove(playerId) != null;
    }
}
