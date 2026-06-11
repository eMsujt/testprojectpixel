package com.skyblock.auctions;

/**
 * The lifecycle states of an auction house listing.
 */
public enum AuctionStatus {

    ACTIVE("Active"),
    SOLD("Sold"),
    EXPIRED("Expired"),
    CANCELLED("Cancelled"),
    CLAIMED("Claimed");

    private final String displayName;

    AuctionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
