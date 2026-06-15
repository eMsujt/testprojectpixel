package com.skyblock.bazaar.model;

import java.util.UUID;

/**
 * An open buy or sell order on the bazaar.
 *
 * @param orderId      unique identifier for this order
 * @param playerId     UUID of the player who placed the order
 * @param productId    product being traded, e.g. {@code "ENCHANTED_DIAMOND"}
 * @param amount       number of units remaining to be filled, must be positive
 * @param pricePerUnit the per-unit limit price
 * @param type         whether this is a buy or sell order
 */
public record BazaarOrder(
        UUID orderId,
        UUID playerId,
        String productId,
        int amount,
        double pricePerUnit,
        OrderType type
) {

    public BazaarOrder {
        if (orderId == null || playerId == null || productId == null || type == null) {
            throw new IllegalArgumentException("null field in BazaarOrder");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        if (pricePerUnit < 0) {
            throw new IllegalArgumentException("pricePerUnit must not be negative: " + pricePerUnit);
        }
    }

    /** Whether a bazaar order is a buy or a sell. */
    public enum OrderType {
        BUY, SELL
    }
}
