package com.skyblock.auctions;

/**
 * The listing modes a seller can choose when putting an item up for auction.
 *
 * <p>Each type carries the display name shown in the auction UI and a flag
 * indicating whether buyers compete through bidding or purchase instantly at
 * a fixed price.</p>
 */
public enum AuctionType {

    AUCTION("Auction", true),
    BIN("Buy It Now", false);

    private final String displayName;
    private final boolean bidding;

    AuctionType(String displayName, boolean bidding) {
        this.displayName = displayName;
        this.bidding = bidding;
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
     * Returns {@code true} if buyers compete for the item by placing bids
     * (AUCTION), or {@code false} if the item sells instantly at the
     * seller-set price (BIN).
     *
     * @return whether this type is resolved through bidding
     */
    public boolean isBidding() {
        return bidding;
    }
}
