package com.skyblock.plugin.managers;

import com.skyblock.core.manager.AuctionHouseManager;

import java.io.File;
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

    public List<AuctionHouseManager.AuctionItem> getAuctions() {
        return DELEGATE.getActiveItems();
    }

    public void addAuction(AuctionHouseManager.AuctionItem auction) {
        DELEGATE.addItem(auction.seller(), auction.itemName(),
                auction.price(), auction.endEpoch());
    }

    public boolean removeAuction(UUID id) {
        AuctionHouseManager.AuctionItem item = DELEGATE.getItem(id);
        if (item == null) return false;
        DELEGATE.cancelItem(id, item.seller());
        return true;
    }

    public void load(File dataFolder) {
        DELEGATE.load(dataFolder);
    }

    public void save(File dataFolder) {
        DELEGATE.save(dataFolder);
    }
}
