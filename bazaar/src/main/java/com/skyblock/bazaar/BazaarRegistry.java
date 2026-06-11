package com.skyblock.bazaar;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Registry of known {@link BazaarProduct}s, keyed by product ID.
 *
 * <p>Modules register products at startup; gameplay code queries live prices
 * via {@link #getProduct(String)}. All methods are thread-safe.</p>
 */
public final class BazaarRegistry {

    private final Map<String, BazaarProduct> productsById = new LinkedHashMap<>();

    /**
     * Registers a product.
     *
     * @param product the product to register
     * @throws IllegalStateException if a product with the same ID is already registered
     */
    public synchronized void register(BazaarProduct product) {
        Objects.requireNonNull(product, "product");
        if (productsById.containsKey(product.getProductId())) {
            throw new IllegalStateException(product.getProductId() + " is already registered");
        }
        productsById.put(product.getProductId(), product);
    }

    /**
     * Replaces an existing product snapshot (e.g. on a price update).
     *
     * @param product the updated product
     * @throws IllegalStateException if the product has not been registered yet
     */
    public synchronized void update(BazaarProduct product) {
        Objects.requireNonNull(product, "product");
        if (!productsById.containsKey(product.getProductId())) {
            throw new IllegalStateException(product.getProductId() + " is not registered");
        }
        productsById.put(product.getProductId(), product);
    }

    /**
     * Returns the product for the given ID, or {@code null} if not registered.
     *
     * @param productId the product ID to look up
     * @return the product, or {@code null}
     */
    public synchronized BazaarProduct getProduct(String productId) {
        return productsById.get(productId);
    }

    /**
     * Returns whether a product with the given ID is registered.
     *
     * @param productId the ID to test
     * @return {@code true} if registered
     */
    public synchronized boolean isRegistered(String productId) {
        return productsById.containsKey(productId);
    }

    /**
     * Returns an immutable snapshot of all registered products.
     *
     * @return all products in registration order
     */
    public synchronized Collection<BazaarProduct> getAll() {
        return Collections.unmodifiableCollection(new LinkedHashMap<>(productsById).values());
    }
}
