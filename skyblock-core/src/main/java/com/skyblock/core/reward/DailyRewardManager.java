package com.skyblock.core.reward;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's last daily-reward claim timestamp.
 *
 * <p>Rewards reset every 24 hours. Not thread-safe; synchronize externally if needed.</p>
 */
public final class DailyRewardManager {

    /** 24 hours in milliseconds. */
    static final long COOLDOWN_MS = 24L * 60 * 60 * 1000;

    /** Coin reward granted per claim. */
    static final long REWARD_COINS = 1_000L;

    private static final DailyRewardManager INSTANCE = new DailyRewardManager();

    /** per-player last-claim epoch millis */
    private final Map<UUID, Long> lastClaim = new HashMap<>();

    private DailyRewardManager() {
    }

    public static DailyRewardManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns whether the player can claim their daily reward right now.
     *
     * @param playerId the player to check, must not be null
     * @return {@code true} if 24 h have elapsed since the last claim (or never claimed)
     */
    public boolean canClaim(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Long last = lastClaim.get(playerId);
        return last == null || (System.currentTimeMillis() - last) >= COOLDOWN_MS;
    }

    /**
     * Records a claim for the player at the current time.
     *
     * @param playerId the player claiming the reward, must not be null
     * @throws IllegalStateException if the player is not yet eligible to claim
     */
    public void recordClaim(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        if (!canClaim(playerId)) {
            throw new IllegalStateException("Player " + playerId + " cannot claim yet");
        }
        lastClaim.put(playerId, System.currentTimeMillis());
    }

    /**
     * Returns the epoch millis of the player's last successful claim.
     *
     * @param playerId the player to look up, must not be null
     * @return last-claim millis, or {@code -1} if the player has never claimed
     */
    public long getLastClaimTime(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Long last = lastClaim.get(playerId);
        return last == null ? -1L : last;
    }

    /**
     * Returns how many milliseconds remain until the player's next eligible claim.
     *
     * @param playerId the player to check, must not be null
     * @return remaining cooldown millis, or {@code 0} if already eligible
     */
    public long getRemainingCooldownMs(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Long last = lastClaim.get(playerId);
        if (last == null) {
            return 0L;
        }
        long elapsed = System.currentTimeMillis() - last;
        return Math.max(0L, COOLDOWN_MS - elapsed);
    }
}
