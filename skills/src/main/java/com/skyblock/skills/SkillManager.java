package com.skyblock.skills;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Tracks per-player skill XP and derives the current level from it.
 *
 * <p>XP is stored as a two-level map: {@code player UUID → (Skill → total XP)}.
 * Level thresholds mirror the classic Hypixel SkyBlock XP curve; the array
 * index corresponds to the XP required to <em>complete</em> that level
 * (i.e. {@code XP_PER_LEVEL[0]} is the cost to reach level 1). All public
 * methods are thread-safe.</p>
 */
public final class SkillManager {

    /**
     * XP required to complete each level, indexed from 0 (level 1) upward.
     * The array has 60 entries to cover the highest cap (level 60 skills).
     */
    private static final long[] XP_PER_LEVEL = {
            50,      125,     200,     300,     500,
            750,     1_000,   1_500,   2_000,   3_500,
            5_000,   7_500,   10_000,  15_000,  20_000,
            30_000,  50_000,  75_000,  100_000, 200_000,
            300_000, 400_000, 500_000, 600_000, 700_000,
            800_000, 900_000, 1_000_000, 1_100_000, 1_200_000,
            1_300_000, 1_400_000, 1_500_000, 1_600_000, 1_700_000,
            1_800_000, 1_900_000, 2_000_000, 2_100_000, 2_200_000,
            2_300_000, 2_400_000, 2_500_000, 2_600_000, 2_750_000,
            2_900_000, 3_100_000, 3_400_000, 3_700_000, 4_000_000,
            4_300_000, 4_600_000, 4_900_000, 5_200_000, 5_500_000,
            5_800_000, 6_100_000, 6_400_000, 6_700_000, 7_000_000
    };

    private final HashMap<UUID, Map<Skill, Long>> xpStore = new HashMap<>();

    /**
     * Adds {@code amount} XP to a player's skill, creating their entry if
     * absent. The total is clamped at {@link Long#MAX_VALUE}; negative amounts
     * are ignored.
     *
     * @param playerId the player's unique id
     * @param skill    the skill receiving XP
     * @param amount   the non-negative XP to add
     * @throws NullPointerException if {@code playerId} or {@code skill} is null
     */
    public synchronized void addXp(UUID playerId, Skill skill, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(skill, "skill");
        if (amount <= 0) return;

        Map<Skill, Long> skills = xpStore.computeIfAbsent(playerId,
                k -> new EnumMap<>(Skill.class));
        long current = skills.getOrDefault(skill, 0L);
        long updated = (current > Long.MAX_VALUE - amount) ? Long.MAX_VALUE : current + amount;
        skills.put(skill, updated);
    }

    /**
     * Returns the total accumulated XP for a player's skill.
     *
     * @param playerId the player's unique id
     * @param skill    the skill to query
     * @return total XP, or {@code 0} if no XP has been recorded
     * @throws NullPointerException if either argument is null
     */
    public synchronized long getXp(UUID playerId, Skill skill) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(skill, "skill");
        Map<Skill, Long> skills = xpStore.get(playerId);
        if (skills == null) return 0L;
        return skills.getOrDefault(skill, 0L);
    }

    /**
     * Returns the current level derived from total XP, capped at
     * {@link Skill#getMaxLevel()}.
     *
     * @param playerId the player's unique id
     * @param skill    the skill to query
     * @return level in the range {@code [0, skill.getMaxLevel()]}
     * @throws NullPointerException if either argument is null
     */
    public synchronized int getLevel(UUID playerId, Skill skill) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(skill, "skill");
        long xp = getXp(playerId, skill);
        return computeLevel(xp, skill.getMaxLevel());
    }

    /**
     * Returns an immutable snapshot of all skill XP for a player.
     *
     * @param playerId the player's unique id
     * @return unmodifiable map of skill → total XP; empty if the player has
     *         no recorded XP
     * @throws NullPointerException if {@code playerId} is null
     */
    public synchronized Map<Skill, Long> getAllXp(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<Skill, Long> skills = xpStore.get(playerId);
        if (skills == null) return Map.of();
        return Collections.unmodifiableMap(new EnumMap<>(skills));
    }

    // XP table lookup: count how many cumulative thresholds the total XP satisfies.
    private static int computeLevel(long totalXp, int maxLevel) {
        int level = 0;
        long cumulative = 0;
        int cap = Math.min(maxLevel, XP_PER_LEVEL.length);
        for (int i = 0; i < cap; i++) {
            cumulative += XP_PER_LEVEL[i];
            if (totalXp >= cumulative) {
                level = i + 1;
            } else {
                break;
            }
        }
        return level;
    }
}
