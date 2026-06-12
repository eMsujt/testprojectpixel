package com.skyblock.core.reward;

import com.skyblock.core.bank.BankManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton managing daily reward claims.
 *
 * <p>Players may claim 1,000 coins once every 24 hours.</p>
 */
public final class DailyRewardManager {

    private static final DailyRewardManager INSTANCE = new DailyRewardManager();

    private static final long COOLDOWN_MS = 24L * 60 * 60 * 1000;
    private static final double REWARD_COINS = 1_000.0;

    private final Map<UUID, Long> lastClaim = new HashMap<>();

    private DailyRewardManager() {}

    public static DailyRewardManager getInstance() {
        return INSTANCE;
    }

    /** Returns true if the player has not claimed within the last 24 hours. */
    public boolean canClaim(UUID playerId) {
        Long last = lastClaim.get(playerId);
        return last == null || System.currentTimeMillis() - last >= COOLDOWN_MS;
    }

    /**
     * Claims the daily reward for the player, depositing coins via {@link BankManager}.
     *
     * @throws IllegalStateException if the player cannot claim yet
     */
    public void claim(UUID playerId) {
        if (!canClaim(playerId)) {
            throw new IllegalStateException("Daily reward not available yet.");
        }
        lastClaim.put(playerId, System.currentTimeMillis());
        BankManager.getInstance().deposit(playerId, REWARD_COINS);
    }

    /**
     * Returns milliseconds remaining until the player may claim again, or 0 if ready.
     */
    public long millisUntilClaim(UUID playerId) {
        Long last = lastClaim.get(playerId);
        if (last == null) {
            return 0L;
        }
        long remaining = COOLDOWN_MS - (System.currentTimeMillis() - last);
        return Math.max(0L, remaining);
    }
}
