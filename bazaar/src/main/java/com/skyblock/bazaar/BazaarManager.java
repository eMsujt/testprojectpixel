package com.skyblock.bazaar;

import com.skyblock.bazaar.BazaarOrder.OrderType;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Manages the bazaar order book.
 *
 * <p>Buy and sell orders are stored per-product in two
 * {@code ConcurrentHashMap<String, Deque<BazaarOrder>>} maps keyed by product id.
 * Each deque is a {@link ConcurrentLinkedDeque} so individual enqueue/dequeue
 * operations are thread-safe without external locking.</p>
 */
public final class BazaarManager {

    private final ConcurrentHashMap<String, Deque<BazaarOrder>> buyOrders = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Deque<BazaarOrder>> sellOrders = new ConcurrentHashMap<>();

    /**
     * Places a buy order for the given product.
     *
     * @param productId    the product id, must not be null
     * @param playerId     the buying player's UUID, must not be null
     * @param amount       units to buy, must be positive
     * @param pricePerUnit per-unit limit price, must not be negative
     * @return the newly created {@link BazaarOrder}
     */
    public BazaarOrder placeBuyOrder(String productId, UUID playerId, int amount, double pricePerUnit) {
        BazaarOrder order = new BazaarOrder(UUID.randomUUID(), playerId, productId, amount, pricePerUnit, OrderType.BUY);
        buyOrders.computeIfAbsent(productId, k -> new ConcurrentLinkedDeque<>()).addLast(order);
        return order;
    }

    /**
     * Places a sell order for the given product.
     *
     * @param productId    the product id, must not be null
     * @param playerId     the selling player's UUID, must not be null
     * @param amount       units to sell, must be positive
     * @param pricePerUnit per-unit limit price, must not be negative
     * @return the newly created {@link BazaarOrder}
     */
    public BazaarOrder placeSellOrder(String productId, UUID playerId, int amount, double pricePerUnit) {
        BazaarOrder order = new BazaarOrder(UUID.randomUUID(), playerId, productId, amount, pricePerUnit, OrderType.SELL);
        sellOrders.computeIfAbsent(productId, k -> new ConcurrentLinkedDeque<>()).addLast(order);
        return order;
    }

    /**
     * Cancels an open order by its id, searching both buy and sell books.
     *
     * @param orderId the order id to remove
     * @return {@code true} if the order was found and removed
     */
    public boolean cancelOrder(UUID orderId) {
        for (Deque<BazaarOrder> deque : buyOrders.values()) {
            if (deque.removeIf(o -> o.orderId().equals(orderId))) {
                return true;
            }
        }
        for (Deque<BazaarOrder> deque : sellOrders.values()) {
            if (deque.removeIf(o -> o.orderId().equals(orderId))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all open buy orders for the given product, in insertion order.
     *
     * @param productId the product id
     * @return an unmodifiable view; empty if no orders exist
     */
    public Collection<BazaarOrder> getBuyOrders(String productId) {
        Deque<BazaarOrder> deque = buyOrders.get(productId);
        return deque == null ? Collections.emptyList() : Collections.unmodifiableCollection(deque);
    }

    /**
     * Returns all open sell orders for the given product, in insertion order.
     *
     * @param productId the product id
     * @return an unmodifiable view; empty if no orders exist
     */
    public Collection<BazaarOrder> getSellOrders(String productId) {
        Deque<BazaarOrder> deque = sellOrders.get(productId);
        return deque == null ? Collections.emptyList() : Collections.unmodifiableCollection(deque);
    }

    /**
     * Returns the best (lowest) ask price among open sell orders for a product.
     *
     * @param productId the product id
     * @return the lowest pricePerUnit, or {@link Double#MAX_VALUE} if no sell orders exist
     */
    public double getBestSellPrice(String productId) {
        Deque<BazaarOrder> deque = sellOrders.get(productId);
        if (deque == null) {
            return Double.MAX_VALUE;
        }
        return deque.stream().mapToDouble(BazaarOrder::pricePerUnit).min().orElse(Double.MAX_VALUE);
    }

    /**
     * Returns the best (highest) bid price among open buy orders for a product.
     *
     * @param productId the product id
     * @return the highest pricePerUnit, or {@code 0} if no buy orders exist
     */
    public double getBestBuyPrice(String productId) {
        Deque<BazaarOrder> deque = buyOrders.get(productId);
        if (deque == null) {
            return 0;
        }
        return deque.stream().mapToDouble(BazaarOrder::pricePerUnit).max().orElse(0);
    }
}
