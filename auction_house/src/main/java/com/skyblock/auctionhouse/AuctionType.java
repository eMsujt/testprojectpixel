package com.skyblock.auctionhouse;

/**
 * The two listing modes available in the auction house.
 *
 * <p>Each type carries the display name shown in the auction house UI and a
 * flag indicating whether the price is fixed at listing time.</p>
 */
public enum AuctionType {

    BIN("Buy It Now", true),
    AUCTION("Auction", false);

    private final String displayName;
    private final boolean fixedPrice;

    AuctionType(String displayName, boolean fixedPrice) {
        this.displayName = displayName;
        this.fixedPrice = fixedPrice;
    }

    /**
     * Returns the human-readable name of this listing type.
     *
     * @return the display name, e.g. {@code "Buy It Now"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns {@code true} if the price is set by the seller at listing time
     * and cannot change (BIN), or {@code false} if the price is determined by
     * bidding (AUCTION).
     *
     * @return whether this type uses a fixed seller-set price
     */
    public boolean isFixedPrice() {
        return fixedPrice;
    }
}
