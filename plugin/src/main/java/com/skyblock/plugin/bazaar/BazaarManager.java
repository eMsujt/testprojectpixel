package com.skyblock.plugin.bazaar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * In-memory registry of active bazaar orders.
 *
 * <p>Holds the live buy/sell orders in insertion order so menus display them in
 * the order they were placed. An order is added when a player submits a bazaar
 * order and removed when it is filled or cancelled. Not thread-safe; access from
 * the main server thread.</p>
 */
public final class BazaarManager {

    /**
     * A single active bazaar order.
     *
     * @param player the placing player's UUID
     * @param qty    the number of items in the order
     * @param price  the price per item in coins
     */
    public record BazaarOrder(UUID player, int qty, double price) {
        public BazaarOrder {
            Objects.requireNonNull(player, "player");
        }
    }

    private static final BazaarManager INSTANCE = new BazaarManager();

    private final List<BazaarOrder> orders = new ArrayList<>();

    private BazaarManager() {
    }

    public static BazaarManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds an order to the active bazaar orders.
     *
     * @param order the order to add
     */
    public void addOrder(BazaarOrder order) {
        Objects.requireNonNull(order, "order");
        orders.add(order);
    }

    /**
     * Removes the given order.
     *
     * @param order the order to remove
     * @return {@code true} if the order was present and removed
     */
    public boolean removeOrder(BazaarOrder order) {
        return orders.remove(order);
    }

    /**
     * Returns an unmodifiable view of every active order in placement order.
     *
     * @return the active orders
     */
    public List<BazaarOrder> getOrders() {
        return Collections.unmodifiableList(orders);
    }
}
