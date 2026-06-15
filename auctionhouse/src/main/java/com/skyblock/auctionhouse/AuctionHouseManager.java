package com.skyblock.auctionhouse;

import com.skyblock.core.manager.AuctionHouseManager;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.AuctionHouseManager} (the canonical singleton).
 */
@Deprecated
public final class AuctionHouseManager {

    public static final Map<String, int[]> AUCTION_CATEGORY_DATA =
            com.skyblock.core.manager.AuctionHouseManager.AUCTION_CATEGORY_DATA;

    private static final com.skyblock.core.manager.AuctionHouseManager DELEGATE =
            com.skyblock.core.manager.AuctionHouseManager.getInstance();

    private AuctionHouseManager() {}

    public static com.skyblock.core.manager.AuctionHouseManager getInstance() {
        return DELEGATE;
    }

    public long createListing(UUID seller, String itemName, double price) {
        UUID id = DELEGATE.addItem(seller, itemName, (long) price, 0L);
        DELEGATE.recordAuction(seller, "Listed " + itemName + " for " + price + " coins");
        // encode UUID least-significant bits as a stable long id
        return id.getLeastSignificantBits() & Long.MAX_VALUE;
    }

    public void recordAuctionEvent(UUID player, String summary) {
        DELEGATE.recordAuction(player, summary);
    }

    public List<String> getAuctionHistory(UUID player) {
        return DELEGATE.getAuctionHistory(player);
    }

    public Map<UUID, List<String>> getAllAuctionHistory() {
        return DELEGATE.getAllAuctionHistory();
    }

    public String getAuctionHouseStats(UUID player) {
        return DELEGATE.getAuctionHouseStats(player);
    }

    public void load(File dataFolder) {
        DELEGATE.load(dataFolder);
    }

    public void save(File dataFolder) {
        DELEGATE.save(dataFolder);
    }
}
