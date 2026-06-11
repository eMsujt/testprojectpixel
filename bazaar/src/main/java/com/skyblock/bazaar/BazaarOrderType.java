package com.skyblock.bazaar;

/**
 * The two sides of a Bazaar order book.
 */
public enum BazaarOrderType {

    BUY("Buy Order"),
    SELL("Sell Offer");

    private final String displayName;

    BazaarOrderType(String displayName) {
        this.displayName = displayName;
    }

    /** Returns the human-readable label shown in menus. */
    public String getDisplayName() {
        return displayName;
    }
}
