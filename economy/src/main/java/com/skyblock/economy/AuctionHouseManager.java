package com.skyblock.economy;

import com.skyblock.core.auction.AuctionHouseManager;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    public UUID createListing(UUID seller, org.bukkit.inventory.ItemStack item, String itemName,
                              double startingBid, boolean binListing) {
        com.skyblock.core.auction.AuctionHouseManager.AuctionType type = binListing
                ? com.skyblock.core.auction.AuctionHouseManager.AuctionType.BIN
                : com.skyblock.core.auction.AuctionHouseManager.AuctionType.AUCTION;
        return DELEGATE.createListing(seller, item, itemName,
                com.skyblock.core.auction.AuctionHouseManager.AuctionCategory.MISC,
                startingBid, type);
    }

    public boolean placeBid(UUID listingId, UUID bidder, double amount) {
        return DELEGATE.placeBid(listingId, bidder, amount);
    }

    public UUID endAuction(UUID listingId) {
        return DELEGATE.endAuction(listingId);
    }

    public void cancelListing(UUID listingId, UUID seller) {
        DELEGATE.cancelListing(listingId, seller);
    }

    public boolean isActive(UUID listingId) {
        return DELEGATE.isActive(listingId);
    }

    public com.skyblock.core.auction.AuctionHouseManager.AuctionListing getListing(UUID listingId) {
        return DELEGATE.getListing(listingId);
    }

    public double getHighestBid(UUID listingId) {
        return DELEGATE.getHighestBid(listingId);
    }

    public UUID getHighestBidder(UUID listingId) {
        return DELEGATE.getHighestBidder(listingId);
    }

    public Set<UUID> getActiveListings() {
        return DELEGATE.getActiveListings();
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
