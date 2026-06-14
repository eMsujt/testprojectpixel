package com.skyblock.plugin.collection;

import com.skyblock.economy.CoinManager;
import org.bukkit.entity.Player;

public final class CollectionRewardManager {

    private static final CollectionRewardManager INSTANCE = new CollectionRewardManager();

    // Coin reward for each tier index (0 = tier 1, 1 = tier 2, ..., 9 = tier 10)
    private static final long[] TIER_REWARDS = {
            50L,    // Tier I
            150L,   // Tier II
            400L,   // Tier III
            1_000L, // Tier IV
            2_500L, // Tier V
            5_000L, // Tier VI
            10_000L,// Tier VII
            25_000L,// Tier VIII
            50_000L,// Tier IX
            100_000L// Tier X
    };

    private final CoinManager coinManager = CoinManager.getInstance();

    private CollectionRewardManager() {}

    public static CollectionRewardManager getInstance() {
        return INSTANCE;
    }

    /**
     * Grants coin rewards for every tier crossed from {@code tierBefore} up to
     * {@code tierAfter} (exclusive lower, inclusive upper).
     */
    public void grant(Player player, int tierBefore, int tierAfter) {
        long total = 0L;
        for (int t = tierBefore + 1; t <= tierAfter; t++) {
            int idx = t - 1;
            if (idx >= 0 && idx < TIER_REWARDS.length) {
                total += TIER_REWARDS[idx];
            }
        }
        if (total <= 0L) return;
        coinManager.addPurse(player.getUniqueId(), total);
        player.sendMessage("§6+§e" + total + " Coins §7(Collection Reward)");
    }
}
