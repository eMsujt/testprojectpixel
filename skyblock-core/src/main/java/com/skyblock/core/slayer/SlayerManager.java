package com.skyblock.core.slayer;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's slayer XP and kill count for every {@link SlayerType}.
 */
public final class SlayerManager {

    public enum SlayerType {
        REVENANT, TARANTULA, SVEN, VOIDGLOOM
    }

    public static final int MAX_LEVEL = 9;

    private static final long[] LEVEL_XP = {
            5, 15, 200, 1_000, 5_000, 20_000, 100_000, 400_000, 1_000_000
    };

    private static final SlayerManager INSTANCE = new SlayerManager();

    private final Map<UUID, Map<SlayerType, Long>> xpMap = new HashMap<>();
    private final Map<UUID, Map<SlayerType, Integer>> killMap = new HashMap<>();

    private SlayerManager() {}

    public static SlayerManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds XP to the player's total for the given slayer type.
     *
     * @param playerId player receiving XP
     * @param type     slayer type being progressed
     * @param amount   XP to add, must not be negative
     * @return new total XP
     */
    public long addXp(UUID playerId, SlayerType type, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        return xpMap.computeIfAbsent(playerId, id -> new EnumMap<>(SlayerType.class))
                .merge(type, amount, Long::sum);
    }

    /**
     * Records a boss kill for the player.
     *
     * @param playerId player that killed the boss
     * @param type     slayer type of the boss
     * @return new total kill count
     */
    public int addKill(UUID playerId, SlayerType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        return killMap.computeIfAbsent(playerId, id -> new EnumMap<>(SlayerType.class))
                .merge(type, 1, Integer::sum);
    }

    /**
     * Returns the player's total XP for the given slayer type.
     *
     * @param playerId player to look up
     * @param type     slayer type to look up
     * @return total XP, {@code 0} if none recorded
     */
    public long getXp(UUID playerId, SlayerType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<SlayerType, Long> xp = xpMap.get(playerId);
        return xp == null ? 0L : xp.getOrDefault(type, 0L);
    }

    /**
     * Returns the player's total boss kills for the given slayer type.
     *
     * @param playerId player to look up
     * @param type     slayer type to look up
     * @return kill count, {@code 0} if none recorded
     */
    public int getKills(UUID playerId, SlayerType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<SlayerType, Integer> kills = killMap.get(playerId);
        return kills == null ? 0 : kills.getOrDefault(type, 0);
    }

    /**
     * Returns the player's current slayer level (0–{@value #MAX_LEVEL}) for the given type.
     *
     * @param playerId player to look up
     * @param type     slayer type to look up
     * @return slayer level
     */
    public int getLevel(UUID playerId, SlayerType type) {
        long xp = getXp(playerId, type);
        int level = 0;
        for (long threshold : LEVEL_XP) {
            if (xp >= threshold) {
                level++;
            } else {
                break;
            }
        }
        return level;
    }

    /**
     * Returns the XP required to reach the next level, or {@code 0} at max level.
     *
     * @param playerId player to look up
     * @param type     slayer type to look up
     * @return XP needed for the next level, {@code 0} if already at max
     */
    public long xpToNextLevel(UUID playerId, SlayerType type) {
        int level = getLevel(playerId, type);
        if (level >= MAX_LEVEL) return 0L;
        return LEVEL_XP[level] - getXp(playerId, type);
    }
}
