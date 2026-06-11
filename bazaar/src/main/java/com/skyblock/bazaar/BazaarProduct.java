package com.skyblock.bazaar;

import java.util.Objects;

/**
 * Immutable snapshot of a single Bazaar product's identity and current
 * instant-transaction prices.
 *
 * <p>{@code instaBuyPrice} is the price a buyer pays to fill immediately from
 * existing sell orders; {@code instaSellPrice} is the price a seller receives
 * when hitting existing buy orders.</p>
 */
public final class BazaarProduct {

    private final String productId;
    private final String displayName;
    private final long instaBuyPrice;
    private final long instaSellPrice;

    /**
     * Constructs a BazaarProduct.
     *
     * @param productId      unique identifier, e.g. {@code "ENCHANTED_CARROT"}
     * @param displayName    human-readable name shown in menus
     * @param instaBuyPrice  price in coins to buy one unit instantly; must be &gt;= 0
     * @param instaSellPrice price in coins received when selling one unit instantly; must be &gt;= 0
     */
    public BazaarProduct(String productId, String displayName,
                         long instaBuyPrice, long instaSellPrice) {
        this.productId = Objects.requireNonNull(productId, "productId");
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        if (instaBuyPrice < 0) throw new IllegalArgumentException("instaBuyPrice must be >= 0");
        if (instaSellPrice < 0) throw new IllegalArgumentException("instaSellPrice must be >= 0");
        this.instaBuyPrice = instaBuyPrice;
        this.instaSellPrice = instaSellPrice;
    }

    /** Returns the unique product identifier. */
    public String getProductId() {
        return productId;
    }

    /** Returns the human-readable name shown in menus and chat. */
    public String getDisplayName() {
        return displayName;
    }

    /** Returns the coins-per-unit cost for an instant buy. */
    public long getInstaBuyPrice() {
        return instaBuyPrice;
    }

    /** Returns the coins-per-unit received for an instant sell. */
    public long getInstaSellPrice() {
        return instaSellPrice;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BazaarProduct other)) return false;
        return productId.equals(other.productId);
    }

    @Override
    public int hashCode() {
        return productId.hashCode();
    }

    @Override
    public String toString() {
        return "BazaarProduct{productId='" + productId + "', displayName='" + displayName
                + "', instaBuyPrice=" + instaBuyPrice + ", instaSellPrice=" + instaSellPrice + '}';
    }
}
