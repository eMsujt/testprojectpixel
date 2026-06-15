package com.skyblock.core.auctionhouse;

import com.skyblock.core.auction.AuctionHouseManager;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.auction.AuctionHouseManager} (the canonical singleton).
 */
@Deprecated
public final class AuctionHouseManager {

    private static final com.skyblock.core.auction.AuctionHouseManager DELEGATE =
            com.skyblock.core.auction.AuctionHouseManager.getInstance();

    private AuctionHouseManager() {}

    /** Returns the canonical singleton. */
    public static com.skyblock.core.auction.AuctionHouseManager getInstance() {
        return DELEGATE;
    }

    public UUID addItem(UUID seller, String itemName, long price) {
        return DELEGATE.addItem(seller, itemName, price, 0L);
    }

    public com.skyblock.core.auction.AuctionHouseManager.AuctionItem getItem(UUID id) {
        return DELEGATE.getItem(id);
    }

    public void cancelItem(UUID id, UUID seller) {
        DELEGATE.cancelItem(id, seller);
    }

    public boolean purchase(UUID id, UUID buyer) {
        return DELEGATE.placeBid(id, buyer, DELEGATE.getItem(id) != null ? DELEGATE.getItem(id).price() : 0);
    }

    public List<com.skyblock.core.auction.AuctionHouseManager.AuctionItem> getActiveItems() {
        return DELEGATE.getActiveItems();
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
