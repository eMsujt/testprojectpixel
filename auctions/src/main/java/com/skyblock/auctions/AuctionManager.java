package com.skyblock.auctions;

import com.skyblock.core.manager.AuctionHouseManager;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.bukkit.entity.Player;

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

    public AuctionHouseManager.AuctionListing createListing(Player seller, String itemName,
            AuctionType type, double startingPrice) {
        AuctionHouseManager.AuctionType canonicalType = type.isBidding()
                ? AuctionHouseManager.AuctionType.AUCTION
                : AuctionHouseManager.AuctionType.BIN;
        UUID id = DELEGATE.createListing(seller.getUniqueId(), null, itemName,
                AuctionHouseManager.AuctionCategory.MISC, startingPrice, canonicalType);
        return DELEGATE.getListing(id);
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

    public boolean cancelListing(UUID auctionId, UUID sellerId) {
        try {
            DELEGATE.cancelListing(auctionId, sellerId);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public Collection<AuctionHouseManager.AuctionListing> getActiveAuctions() {
        java.util.List<AuctionHouseManager.AuctionListing> result = new java.util.ArrayList<>();
        for (UUID id : DELEGATE.getActiveListings()) {
            result.add(DELEGATE.getListing(id));
        }
        return Collections.unmodifiableCollection(result);
    }
}
