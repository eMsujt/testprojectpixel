package com.skyblock.bestiary;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks each player's bestiary: kill counts per mob id and the milestone
 * tier those kills unlock. Tiers follow a doubling threshold curve — tier
 * {@code n} requires {@code BASE_TIER_KILLS * 2^(n-1)} cumulative kills,
 * capped at {@link #MAX_TIER}.
 *
 * <p>State is kept in nested {@link ConcurrentHashMap}s keyed by player id
 * and mob id, so all operations are thread-safe.</p>
 */
public final class BestiaryManager {

    /** Kills required to reach tier 1 of a mob's bestiary entry. */
    public static final int BASE_TIER_KILLS = 10;

    /** The highest tier a bestiary entry can reach. */
    public static final int MAX_TIER = 10;

    private final ConcurrentHashMap<UUID, ConcurrentHashMap<String, Long>> kills = new ConcurrentHashMap<>();

    /**
     * Records kills of a mob for a player.
     *
     * @param playerId the player's id
     * @param mobId    the mob's id, non-blank
     * @param amount   the number of kills to add, must be positive
     * @return the player's new cumulative kill count for the mob
     */
    public long recordKills(UUID playerId, String mobId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        String key = requireMobId(mobId);
        return killsFor(requirePlayer(playerId)).merge(key, (long) amount, Long::sum);
    }

    /**
     * Returns the player's cumulative kill count for a mob, or zero if the
     * mob has never been killed by the player.
     *
     * @param playerId the player's id
     * @param mobId    the mob's id, non-blank
     * @return the kill count, never negative
     */
    public long getKills(UUID playerId, String mobId) {
        String key = requireMobId(mobId);
        ConcurrentHashMap<String, Long> playerKills = kills.get(requirePlayer(playerId));
        if (playerKills == null) {
            return 0L;
        }
        return playerKills.getOrDefault(key, 0L);
    }

    /**
     * Returns the bestiary tier the player has unlocked for a mob. Tier 0
     * means the entry is locked; tier {@code n} requires
     * {@code BASE_TIER_KILLS * 2^(n-1)} kills, up to {@link #MAX_TIER}.
     *
     * @param playerId the player's id
     * @param mobId    the mob's id, non-blank
     * @return the unlocked tier, between 0 and {@link #MAX_TIER}
     */
    public int getTier(UUID playerId, String mobId) {
        long count = getKills(playerId, mobId);
        int tier = 0;
        long threshold = BASE_TIER_KILLS;
        while (tier < MAX_TIER && count >= threshold) {
            tier++;
            threshold *= 2;
        }
        return tier;
    }

    /**
     * Returns the kills still needed for the player to reach the next tier
     * of a mob's entry, or zero if the entry is already at {@link #MAX_TIER}.
     *
     * @param playerId the player's id
     * @param mobId    the mob's id, non-blank
     * @return the remaining kills, never negative
     */
    public long getKillsToNextTier(UUID playerId, String mobId) {
        int tier = getTier(playerId, mobId);
        if (tier >= MAX_TIER) {
            return 0L;
        }
        long threshold = BASE_TIER_KILLS * (1L << tier);
        return threshold - getKills(playerId, mobId);
    }

    /**
     * Returns an unmodifiable copy of the player's bestiary, keyed by mob id.
     *
     * @param playerId the player's id
     * @return the player's kill counts per mob, empty if nothing recorded
     */
    public Map<String, Long> getBestiary(UUID playerId) {
        ConcurrentHashMap<String, Long> playerKills = kills.get(requirePlayer(playerId));
        if (playerKills == null) {
            return Map.of();
        }
        return Collections.unmodifiableMap(new LinkedHashMap<>(playerKills));
    }

    /**
     * Removes all bestiary progress for a player.
     *
     * @param playerId the player's id
     */
    public void resetBestiary(UUID playerId) {
        kills.remove(requirePlayer(playerId));
    }

    private ConcurrentHashMap<String, Long> killsFor(UUID playerId) {
        return kills.computeIfAbsent(playerId, id -> new ConcurrentHashMap<>());
    }

    private static String requireMobId(String mobId) {
        if (mobId == null || mobId.isBlank()) {
            throw new IllegalArgumentException("mobId must be non-blank");
        }
        return mobId.trim();
    }

    private static UUID requirePlayer(UUID playerId) {
        if (playerId == null) {
            throw new IllegalArgumentException("playerId must be non-null");
        }
        return playerId;
    }
}
