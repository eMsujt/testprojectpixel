package com.skyblock.plugin.managers;

import com.skyblock.core.manager.AuctionHouseManager;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @deprecated Use {@link com.skyblock.core.manager.AuctionHouseManager} (the canonical singleton).
 */
@Deprecated
public final class AuctionHouseManager {

    private static final com.skyblock.core.manager.AuctionHouseManager DELEGATE =
            com.skyblock.core.manager.AuctionHouseManager.getInstance();

    private AuctionHouseManager() {}

    public static com.skyblock.core.manager.AuctionHouseManager getInstance() {
        return DELEGATE;
    }

    public List<com.skyblock.core.manager.AuctionHouseManager.AuctionListing> getListings(UUID sellerId) {
        return DELEGATE.getActiveListings().stream()
                .map(DELEGATE::getListing)
                .filter(l -> sellerId.equals(l.seller()))
                .collect(Collectors.toList());
    }

    public List<com.skyblock.core.manager.AuctionHouseManager.AuctionListing> getAllListings() {
        return DELEGATE.getActiveListings().stream()
                .map(DELEGATE::getListing)
                .collect(Collectors.toList());
    }

    public boolean removeListing(UUID sellerId, UUID listingId) {
        if (!DELEGATE.isActive(listingId)) return false;
        DELEGATE.cancelListing(listingId, sellerId);
        return true;
    }

    public boolean placeBid(UUID bidder, UUID listingId, double amount) {
        return DELEGATE.placeBid(listingId, bidder, amount);
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
