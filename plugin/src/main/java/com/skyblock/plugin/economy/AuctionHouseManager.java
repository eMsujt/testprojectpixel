package com.skyblock.plugin.economy;

import com.skyblock.core.manager.AuctionHouseManager;

import java.util.Collection;
import java.util.List;
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

    public void addListing(com.skyblock.core.manager.AuctionHouseManager.AuctionListing listing) {
        // listings are added via DELEGATE.createListing(); no-op bridge for legacy callers
    }

    public com.skyblock.core.manager.AuctionHouseManager.AuctionListing getListing(UUID id) {
        return DELEGATE.isActive(id) ? DELEGATE.getListing(id) : null;
    }

    public com.skyblock.core.manager.AuctionHouseManager.AuctionListing removeListing(UUID id) {
        if (!DELEGATE.isActive(id)) return null;
        com.skyblock.core.manager.AuctionHouseManager.AuctionListing listing = DELEGATE.getListing(id);
        DELEGATE.cancelListing(id, listing.seller());
        return listing;
    }

    public List<com.skyblock.core.manager.AuctionHouseManager.AuctionListing> getListings() {
        return DELEGATE.getActiveListings().stream()
                .map(DELEGATE::getListing)
                .collect(Collectors.toList());
    }
}
