package com.skyblock.core.auction;

import com.skyblock.core.manager.AuctionHouseManager;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @deprecated Use {@link com.skyblock.core.manager.AuctionHouseManager} (the canonical singleton).
 */
@Deprecated
public final class AuctionManager {

    private static final com.skyblock.core.manager.AuctionHouseManager DELEGATE =
            com.skyblock.core.manager.AuctionHouseManager.getInstance();

    private AuctionManager() {}

    public static com.skyblock.core.manager.AuctionHouseManager getInstance() {
        return DELEGATE;
    }

    public UUID createListing(UUID seller, String itemName, double startingBid,
                              AuctionHouseManager.AuctionType type,
                              AuctionHouseManager.AuctionCategory category) {
        return DELEGATE.createListing(seller, null, itemName, category, startingBid, type);
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

    public AuctionHouseManager.AuctionListing getListing(UUID listingId) {
        return DELEGATE.getListing(listingId);
    }

    public double getHighestBid(UUID listingId) {
        return DELEGATE.getHighestBid(listingId);
    }

    public UUID getHighestBidder(UUID listingId) {
        return DELEGATE.getHighestBidder(listingId);
    }

    public List<AuctionHouseManager.AuctionListing> getActiveListings() {
        return DELEGATE.getActiveListings().stream()
                .map(DELEGATE::getListing)
                .collect(Collectors.toList());
    }

    public List<AuctionHouseManager.AuctionListing> getListingsBySeller(UUID seller) {
        return DELEGATE.getActiveListings().stream()
                .map(DELEGATE::getListing)
                .filter(l -> seller.equals(l.seller()))
                .collect(Collectors.toList());
    }

    public List<AuctionHouseManager.AuctionListing> getListingsByCategory(
            AuctionHouseManager.AuctionCategory category) {
        return DELEGATE.getListingsByCategory(category);
    }

    public void clear() {
        DELEGATE.clear();
    }
}
