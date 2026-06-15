package com.skyblock.auction;

import com.skyblock.core.manager.AuctionHouseManager;

import java.util.Collection;
import java.util.Collections;
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

    public UUID createListing(UUID player, String itemName, double startingBid) {
        return DELEGATE.createListing(player, null, itemName,
                AuctionHouseManager.AuctionCategory.MISC,
                startingBid, AuctionHouseManager.AuctionType.AUCTION);
    }

    public boolean isActive(UUID auctionId) {
        return DELEGATE.isActive(auctionId);
    }

    public AuctionHouseManager.AuctionListing getListing(UUID auctionId) {
        return DELEGATE.getListing(auctionId);
    }

    public UUID endAuction(UUID auctionId) {
        return DELEGATE.endAuction(auctionId);
    }

    public Set<UUID> getActiveAuctions() {
        return DELEGATE.getActiveListings();
    }
}
