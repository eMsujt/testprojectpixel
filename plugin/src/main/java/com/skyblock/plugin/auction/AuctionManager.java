package com.skyblock.plugin.auction;

import com.skyblock.core.manager.AuctionHouseManager;

import java.util.Collections;
import java.util.List;
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

    public void addListing(AuctionEntry listing) {
        DELEGATE.addItem(listing.seller(), listing.itemName(),
                (long) listing.price(), Long.MAX_VALUE);
    }

    public AuctionHouseManager.AuctionItem getListing(UUID id) {
        return DELEGATE.getItem(id);
    }

    public AuctionHouseManager.AuctionItem removeListing(UUID id) {
        AuctionHouseManager.AuctionItem item = DELEGATE.getItem(id);
        if (item != null) {
            DELEGATE.cancelItem(id, item.seller());
        }
        return item;
    }

    public List<AuctionHouseManager.AuctionItem> getListings() {
        return DELEGATE.getActiveItems();
    }

    public void addAuctionListing(AuctionListing listing) {
        DELEGATE.createListing(listing.seller(), listing.item(), listing.id(),
                AuctionHouseManager.AuctionCategory.MISC, listing.price(),
                AuctionHouseManager.AuctionType.BIN);
    }

    public boolean removeAuctionListing(String id) {
        for (UUID uid : DELEGATE.getActiveListings()) {
            AuctionHouseManager.AuctionListing l = DELEGATE.getListing(uid);
            if (l.itemName().equals(id)) {
                DELEGATE.cancelListing(uid, l.seller());
                return true;
            }
        }
        return false;
    }

    public List<AuctionHouseManager.AuctionListing> getAuctionListings() {
        return DELEGATE.getActiveListings().stream()
                .map(DELEGATE::getListing)
                .collect(Collectors.toList());
    }
}
