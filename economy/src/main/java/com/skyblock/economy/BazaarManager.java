package com.skyblock.economy;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages bazaar products available for instant buy and sell.
 *
 * <p>Products are stored in a {@link HashMap} keyed by product id (e.g.
 * {@code "WHEAT"}).  A product tracks the current instant-buy price, the
 * current instant-sell price, and the cumulative traded volume.  Not
 * thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class BazaarManager {

    /**
     * A single bazaar product with live pricing and volume data.
     *
     * <p>Instances are created only through
     * {@link BazaarManager#addProduct(String, double, double)} and mutated
     * only through {@link BazaarManager}.</p>
     */
    public static final class BazaarProduct {

        private final String productId;
        private double buyPrice;
        private double sellPrice;
        private long volume;

        private BazaarProduct(String productId, double buyPrice, double sellPrice) {
            this.productId = productId;
            this.buyPrice = buyPrice;
            this.sellPrice = sellPrice;
        }

        /**
         * Returns the unique product identifier.
         *
         * @return the product id, e.g. {@code "WHEAT"}
         */
        public String getProductId() {
            return productId;
        }

        /**
         * Returns the current price a player pays to instantly buy one unit.
         *
         * @return the instant-buy price per unit
         */
        public double getBuyPrice() {
            return buyPrice;
        }

        /**
         * Returns the current price a player receives when instantly selling
         * one unit.
         *
         * @return the instant-sell price per unit
         */
        public double getSellPrice() {
            return sellPrice;
        }

        /**
         * Returns the total number of units traded through this product since
         * it was registered.
         *
         * @return cumulative traded volume
         */
        public long getVolume() {
            return volume;
        }
    }

    private final Map<String, BazaarProduct> products = new HashMap<>();

    /**
     * Registers a new product in the bazaar.
     *
     * @param productId the unique product identifier, must not be null or blank
     * @param buyPrice  the instant-buy price per unit, must be positive
     * @param sellPrice the instant-sell price per unit, must be positive and
     *                  not exceed {@code buyPrice}
     * @return the newly created product
     * @throws IllegalArgumentException if any argument is invalid or the
     *                                  product is already registered
     */
    public BazaarProduct addProduct(String productId, double buyPrice, double sellPrice) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("productId must not be null or blank");
        }
        if (buyPrice <= 0 || sellPrice <= 0) {
            throw new IllegalArgumentException("buyPrice and sellPrice must be positive");
        }
        if (sellPrice > buyPrice) {
            throw new IllegalArgumentException(
                    "sellPrice must not exceed buyPrice: sellPrice=" + sellPrice
                            + ", buyPrice=" + buyPrice);
        }
        if (products.containsKey(productId)) {
            throw new IllegalArgumentException("product already registered: " + productId);
        }
        BazaarProduct product = new BazaarProduct(productId, buyPrice, sellPrice);
        products.put(productId, product);
        return product;
    }

    /**
     * Updates the pricing for an existing product.
     *
     * @param productId the product to update
     * @param buyPrice  the new instant-buy price, must be positive
     * @param sellPrice the new instant-sell price, must be positive and not
     *                  exceed {@code buyPrice}
     * @throws IllegalArgumentException if the product does not exist or
     *                                  prices are invalid
     */
    public void updatePrices(String productId, double buyPrice, double sellPrice) {
        BazaarProduct product = requireProduct(productId);
        if (buyPrice <= 0 || sellPrice <= 0) {
            throw new IllegalArgumentException("buyPrice and sellPrice must be positive");
        }
        if (sellPrice > buyPrice) {
            throw new IllegalArgumentException(
                    "sellPrice must not exceed buyPrice: sellPrice=" + sellPrice
                            + ", buyPrice=" + buyPrice);
        }
        product.buyPrice = buyPrice;
        product.sellPrice = sellPrice;
    }

    /**
     * Records an instant-buy transaction, incrementing the product's volume.
     *
     * @param productId the product being bought
     * @param amount    the number of units bought, must be positive
     * @return the total cost of the transaction
     * @throws IllegalArgumentException if the product does not exist or
     *                                  {@code amount} is not positive
     */
    public double instantBuy(String productId, int amount) {
        BazaarProduct product = requireProduct(productId);
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        product.volume += amount;
        return product.buyPrice * amount;
    }

    /**
     * Records an instant-sell transaction, incrementing the product's volume.
     *
     * @param productId the product being sold
     * @param amount    the number of units sold, must be positive
     * @return the total payout for the transaction
     * @throws IllegalArgumentException if the product does not exist or
     *                                  {@code amount} is not positive
     */
    public double instantSell(String productId, int amount) {
        BazaarProduct product = requireProduct(productId);
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        product.volume += amount;
        return product.sellPrice * amount;
    }

    /**
     * Returns whether a product with the given id is registered.
     *
     * @param productId the product id to check
     * @return {@code true} if the product exists
     */
    public boolean hasProduct(String productId) {
        return products.containsKey(productId);
    }

    /**
     * Returns a product by id.
     *
     * @param productId the product id
     * @return the product, or {@code null} if none is registered under that id
     */
    public BazaarProduct getProduct(String productId) {
        return products.get(productId);
    }

    /**
     * Returns all registered products.
     *
     * @return an unmodifiable view of the registered products
     */
    public Collection<BazaarProduct> getProducts() {
        return Collections.unmodifiableCollection(products.values());
    }

    private BazaarProduct requireProduct(String productId) {
        BazaarProduct product = products.get(productId);
        if (product == null) {
            throw new IllegalArgumentException("no product registered with id: " + productId);
        }
        return product;
    }
}
