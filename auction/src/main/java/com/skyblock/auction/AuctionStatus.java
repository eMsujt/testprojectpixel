package com.skyblock.auction;

/**
 * Lifecycle states of an auction house listing.
 *
 * <p>A listing starts {@link #ACTIVE} and ends in exactly one of the
 * terminal states: {@link #SOLD} when a bid wins, {@link #EXPIRED} when the
 * end time passes without a buyer, or {@link #CANCELLED} when the seller
 * withdraws it.</p>
 */
public enum AuctionStatus {

    ACTIVE("Active"),
    SOLD("Sold"),
    EXPIRED("Expired"),
    CANCELLED("Cancelled");

    private final String displayName;

    AuctionStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable name shown in menus and chat.
     *
     * @return the display name, e.g. {@code "Active"}
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns whether this status still accepts bids.
     *
     * @return {@code true} only for {@link #ACTIVE}
     */
    public boolean isOpen() {
        return this == ACTIVE;
    }
}
