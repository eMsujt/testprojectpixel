package com.skyblock.core.collections.manager;

import com.skyblock.core.manager.EconomyManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Grants the coin rewards a player earns when a collection tiers up, mirroring
 * Hypixel SkyBlock's collection reward tables. Invoked from
 * {@link com.skyblock.core.collections.listener.CollectionListener} once a
 * collection total crosses one or more of its configured tier thresholds.
 *
 * <p>Each unlocked tier pays out coins that scale with the tier number, so a
 * single gather that crosses several tiers still pays out each one.</p>
 */
public final class CollectionRewardManager {

    private static final CollectionRewardManager INSTANCE = new CollectionRewardManager();

    /** Base coins awarded per unlocked tier; the reward grows with the tier number. */
    private static final long COINS_PER_TIER = 100L;

    private final EconomyManager coinManager = EconomyManager.getInstance();

    private CollectionRewardManager() {}

    public static CollectionRewardManager getInstance() {
        return INSTANCE;
    }

    /**
     * Grants every tier-up reward earned between {@code fromTier} (exclusive) and
     * {@code toTier} (inclusive), so a single gather that crosses several tiers
     * still pays out each one.
     *
     * @param player     the player who tiered up
     * @param collection the collection material that advanced
     * @param fromTier   the tier before the gather
     * @param toTier     the tier after the gather
     */
    public void grantTierUpRewards(Player player, Material collection, int fromTier, int toTier) {
        if (player == null || collection == null || toTier <= fromTier) {
            return;
        }
        UUID uuid = player.getUniqueId();
        for (int tier = fromTier + 1; tier <= toTier; tier++) {
            long coins = COINS_PER_TIER * tier;
            coinManager.addPurse(uuid, coins);
            sendRewardMessage(player, collection, tier, coins);
        }
    }

    private void sendRewardMessage(Player player, Material collection, int tier, long coins) {
        String name = collection.name().toLowerCase().replace('_', ' ');
        player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "COLLECTION REWARD "
                + ChatColor.GRAY + name + " Tier " + tier
                + ChatColor.GRAY + "  +" + coins + " coins");
    }
}
