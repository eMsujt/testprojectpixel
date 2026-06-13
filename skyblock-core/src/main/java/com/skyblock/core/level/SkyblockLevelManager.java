package com.skyblock.core.level;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton tracking each player's SkyBlock XP and deriving their SkyBlock level.
 *
 * <p>Level formula mirrors the standard Hypixel skill curve defined in
 * {@link com.skyblock.core.skill.SkillLevelManager}: the cumulative XP thresholds
 * are the same, and max level is {@value #MAX_LEVEL}.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class SkyblockLevelManager {

    public static final int MAX_LEVEL = 50;

    /**
     * Cumulative XP thresholds for levels 1–50 (matches the Hypixel SkyBlock skill curve).
     * Index {@code i} = total XP required to reach level {@code i+1}.
     */
    private static final long[] XP_TABLE = {
               0L,        50L,       175L,       375L,       675L,
            1175L,      1925L,      2925L,      4425L,      6425L,
            9925L,     14925L,     22425L,     32425L,     47425L,
           67425L,     97425L,    147425L,    222425L,    322425L,
          522425L,    822425L,   1222425L,   1722425L,   2322425L,
         3022425L,   3822425L,   4722425L,   5722425L,   6822425L,
         8022425L,   9322425L,  10722425L,  12222425L,  13822425L,
        15522425L,  17322425L,  19222425L,  21222425L,  23322425L,
        25522425L,  27822425L,  30222425L,  32722425L,  35322425L,
        38072425L,  40972425L,  44072425L,  47472425L,  51172425L,
    };

    private static final SkyblockLevelManager INSTANCE = new SkyblockLevelManager();

    /** Per-player cumulative SkyBlock XP; absent entries default to zero. */
    private final Map<UUID, Long> skyblockXP = new HashMap<>();

    private SkyblockLevelManager() {}

    /**
     * Returns the single shared {@code SkyblockLevelManager} instance.
     *
     * @return the singleton instance
     */
    public static SkyblockLevelManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the total SkyBlock XP for the given player.
     *
     * @param playerId the player to look up
     * @return cumulative XP, {@code 0} if the player has none
     */
    public long getXP(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return skyblockXP.getOrDefault(playerId, 0L);
    }

    /**
     * Adds SkyBlock XP to the player's total.
     *
     * @param playerId the player to update
     * @param amount   the amount of XP to add (must be positive)
     * @return the new total XP after the addition
     * @throws IllegalArgumentException if {@code amount} is not positive
     */
    public long addXP(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        long newXP = skyblockXP.getOrDefault(playerId, 0L) + amount;
        skyblockXP.put(playerId, newXP);
        return newXP;
    }

    /**
     * Sets the player's SkyBlock XP to an explicit value (op use only).
     *
     * @param playerId the player to update
     * @param xp       the new XP value (must not be negative)
     * @throws IllegalArgumentException if {@code xp} is negative
     */
    public void setXP(UUID playerId, long xp) {
        Objects.requireNonNull(playerId, "playerId");
        if (xp < 0) {
            throw new IllegalArgumentException("xp must not be negative");
        }
        skyblockXP.put(playerId, xp);
    }

    /**
     * Returns the SkyBlock level (1–{@value #MAX_LEVEL}) for the given player.
     *
     * @param playerId the player to look up
     * @return current SkyBlock level
     */
    public int getLevel(UUID playerId) {
        return levelForXP(getXP(playerId));
    }

    /**
     * Returns the SkyBlock level corresponding to the given cumulative XP.
     *
     * @param totalXP total accumulated XP
     * @return level in range [1, {@value #MAX_LEVEL}]
     */
    public int levelForXP(long totalXP) {
        for (int i = MAX_LEVEL - 1; i >= 0; i--) {
            if (totalXP >= XP_TABLE[i]) {
                return i + 1;
            }
        }
        return 1;
    }

    /**
     * Returns the cumulative XP required to reach the given level.
     *
     * @param level target level, clamped to [1, {@value #MAX_LEVEL}]
     * @return cumulative XP threshold
     */
    public long xpForLevel(int level) {
        int clamped = Math.max(1, Math.min(level, MAX_LEVEL));
        return XP_TABLE[clamped - 1];
    }

    /**
     * Returns the XP still needed to advance to the next level, or {@code 0}
     * if the player is already at max level.
     *
     * @param playerId the player to look up
     * @return XP remaining until next level, or 0 at max level
     */
    public long xpToNextLevel(UUID playerId) {
        long totalXP = getXP(playerId);
        int level = levelForXP(totalXP);
        if (level >= MAX_LEVEL) {
            return 0L;
        }
        return XP_TABLE[level] - totalXP;
    }

    /**
     * Returns an unmodifiable view of all player UUIDs with tracked XP.
     *
     * @return set of tracked player UUIDs
     */
    public Set<UUID> getTrackedPlayers() {
        return Collections.unmodifiableSet(skyblockXP.keySet());
    }

    /**
     * Removes all XP data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return skyblockXP.remove(playerId) != null;
    }
}
