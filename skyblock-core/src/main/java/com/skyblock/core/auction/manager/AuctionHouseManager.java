package com.skyblock.core.auction.manager;

/**
 * @deprecated Use {@link com.skyblock.core.manager.AuctionHouseManager} instead.
 */
@Deprecated
public final class AuctionHouseManager {

    private AuctionHouseManager() {}

    public static com.skyblock.core.manager.AuctionHouseManager getInstance() {
        return com.skyblock.core.manager.AuctionHouseManager.getInstance();
    }
}
