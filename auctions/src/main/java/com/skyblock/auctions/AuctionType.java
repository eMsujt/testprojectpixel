package com.skyblock.auctions;

/**
 * The kinds of listings available in the auction house.
 */
public enum AuctionType {

    BIN("Buy It Now"),
    AUCTION("Auction");

    private final String displayName;

    AuctionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
