package com.skyblock.plugin.auction;

import com.skyblock.core.auction.AuctionHouseManager;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public void addListing(com.skyblock.core.auction.AuctionHouseManager.AuctionListing listing) {
        // listings are added via DELEGATE.createListing(); no-op bridge for legacy callers
    }

    public com.skyblock.core.auction.AuctionHouseManager.AuctionListing getListing(UUID id) {
        return DELEGATE.isActive(id) ? DELEGATE.getListing(id) : null;
    }

    public com.skyblock.core.auction.AuctionHouseManager.AuctionListing removeListing(UUID id) {
        if (!DELEGATE.isActive(id)) return null;
        com.skyblock.core.auction.AuctionHouseManager.AuctionListing listing = DELEGATE.getListing(id);
        DELEGATE.cancelListing(id, listing.seller());
        return listing;
    }

    public Collection<com.skyblock.core.auction.AuctionHouseManager.AuctionListing> getListings() {
        return DELEGATE.getActiveListings().stream()
                .map(DELEGATE::getListing)
                .collect(Collectors.toList());
    }
}
