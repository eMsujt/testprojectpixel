package com.skyblock.bazaar;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Manages bazaar product listings with instant buy and instant sell prices.
 *
 * <p>Products are identified by their product id (e.g. {@code "ENCHANTED_DIAMOND"}).
 * Unlisted products cannot be traded. Not thread-safe; synchronize externally
 * if accessed from multiple threads.</p>
 */
public final class BazaarManager {

    private final Map<String, Double> buyPrices = new HashMap<>();
    private final Map<String, Double> sellPrices = new HashMap<>();

    /**
     * Lists a product on the bazaar, or updates its prices if already listed.
     *
     * @param productId the product id, must not be null
     * @param buyPrice  the per-unit instant buy price, must not be negative
     * @param sellPrice the per-unit instant sell price, must not be negative
     * @throws IllegalArgumentException if a price is negative or {@code productId} is null
     */
    public void listProduct(String productId, double buyPrice, double sellPrice) {
        if (productId == null) {
            throw new IllegalArgumentException("productId must not be null");
        }
        if (buyPrice < 0 || sellPrice < 0) {
            throw new IllegalArgumentException(
                    "prices must not be negative: buy=" + buyPrice + ", sell=" + sellPrice);
        }
        buyPrices.put(productId, buyPrice);
        sellPrices.put(productId, sellPrice);
    }

    /**
     * Removes a product from the bazaar.
     *
     * @param productId the product id
     * @return {@code true} if the product was listed and has been removed
     */
    public boolean delistProduct(String productId) {
        sellPrices.remove(productId);
        return buyPrices.remove(productId) != null;
    }

    /**
     * Returns whether the product is currently listed on the bazaar.
     *
     * @param productId the product id
     * @return {@code true} if the product is listed
     */
    public boolean isListed(String productId) {
        return buyPrices.containsKey(productId);
    }

    /**
     * Returns the per-unit instant buy price of a listed product.
     *
     * @param productId the product id
     * @return the instant buy price per unit
     * @throws IllegalArgumentException if the product is not listed
     */
    public double getBuyPrice(String productId) {
        Double price = buyPrices.get(productId);
        if (price == null) {
            throw new IllegalArgumentException("product is not listed: " + productId);
        }
        return price;
    }

    /**
     * Returns the per-unit instant sell price of a listed product.
     *
     * @param productId the product id
     * @return the instant sell price per unit
     * @throws IllegalArgumentException if the product is not listed
     */
    public double getSellPrice(String productId) {
        Double price = sellPrices.get(productId);
        if (price == null) {
            throw new IllegalArgumentException("product is not listed: " + productId);
        }
        return price;
    }

    /**
     * Calculates the total cost of instantly buying the given amount of a product.
     *
     * @param productId the product id
     * @param amount    the number of units to buy, must be positive
     * @return the total cost
     * @throws IllegalArgumentException if the product is not listed or {@code amount} is not positive
     */
    public double instantBuy(String productId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        return getBuyPrice(productId) * amount;
    }

    /**
     * Calculates the total payout of instantly selling the given amount of a product.
     *
     * @param productId the product id
     * @param amount    the number of units to sell, must be positive
     * @return the total payout
     * @throws IllegalArgumentException if the product is not listed or {@code amount} is not positive
     */
    public double instantSell(String productId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        return getSellPrice(productId) * amount;
    }

    /**
     * Returns the ids of all products currently listed on the bazaar.
     *
     * @return an unmodifiable view of the listed product ids
     */
    public Set<String> getListedProducts() {
        return Collections.unmodifiableSet(buyPrices.keySet());
    }
}
