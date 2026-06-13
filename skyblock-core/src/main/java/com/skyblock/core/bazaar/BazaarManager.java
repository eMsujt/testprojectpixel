package com.skyblock.core.bazaar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing the bazaar order book.  Buy orders are matched against
 * sell orders automatically when a compatible order is added.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class BazaarManager {

    private static final BazaarManager INSTANCE = new BazaarManager();

    public enum BazaarOrderType {
        BUY("Buy Order"),
        SELL("Sell Order");

        private final String displayName;

        BazaarOrderType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** A unified order record representing either a buy or sell order. */
    public record BazaarOrder(UUID id, UUID player, String itemId, int quantity, double priceEach, BazaarOrderType type) {

        public BazaarOrder {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(player, "player");
            Objects.requireNonNull(itemId, "itemId");
            Objects.requireNonNull(type, "type");
            if (quantity <= 0) {
                throw new IllegalArgumentException("quantity must be positive: " + quantity);
            }
            if (priceEach <= 0) {
                throw new IllegalArgumentException("priceEach must be positive: " + priceEach);
            }
        }
    }

    /** A standing buy order: a player willing to buy {@code quantity} of {@code itemId}
     *  at up to {@code priceEach} coins each. */
    public record BuyOrder(UUID id, UUID buyer, String itemId, int quantity, double priceEach) {

        public BuyOrder {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(buyer, "buyer");
            Objects.requireNonNull(itemId, "itemId");
            if (quantity <= 0) {
                throw new IllegalArgumentException("quantity must be positive: " + quantity);
            }
            if (priceEach <= 0) {
                throw new IllegalArgumentException("priceEach must be positive: " + priceEach);
            }
        }
    }

    /** A standing sell order: a player willing to sell {@code quantity} of {@code itemId}
     *  for at least {@code priceEach} coins each. */
    public record SellOrder(UUID id, UUID seller, String itemId, int quantity, double priceEach) {

        public SellOrder {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(seller, "seller");
            Objects.requireNonNull(itemId, "itemId");
            if (quantity <= 0) {
                throw new IllegalArgumentException("quantity must be positive: " + quantity);
            }
            if (priceEach <= 0) {
                throw new IllegalArgumentException("priceEach must be positive: " + priceEach);
            }
        }
    }

    // Per-item order books: buy orders sorted highest-price first, sell orders lowest-price first
    private final Map<String, List<BuyOrder>> buyOrders = new HashMap<>();
    private final Map<String, List<SellOrder>> sellOrders = new HashMap<>();

    private BazaarManager() {}

    /**
     * Returns the single shared {@code BazaarManager} instance.
     *
     * @return the singleton instance
     */
    public static BazaarManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds a buy order to the order book.  The order is assigned a new random UUID.
     *
     * @param buyer      the buying player's UUID, must not be null
     * @param itemId     the item identifier, must not be null
     * @param quantity   the number of items to buy, must be positive
     * @param priceEach  the maximum price per item, must be positive
     * @return the UUID of the newly created buy order
     */
    public UUID addBuyOrder(UUID buyer, String itemId, int quantity, double priceEach) {
        UUID orderId = UUID.randomUUID();
        BuyOrder order = new BuyOrder(orderId, buyer, itemId, quantity, priceEach);
        buyOrders.computeIfAbsent(itemId, k -> new ArrayList<>()).add(order);
        buyOrders.get(itemId).sort(Comparator.comparingDouble(BuyOrder::priceEach).reversed());
        return orderId;
    }

    /**
     * Adds a sell order to the order book.  The order is assigned a new random UUID.
     *
     * @param seller     the selling player's UUID, must not be null
     * @param itemId     the item identifier, must not be null
     * @param quantity   the number of items to sell, must be positive
     * @param priceEach  the minimum price per item, must be positive
     * @return the UUID of the newly created sell order
     */
    public UUID addSellOrder(UUID seller, String itemId, int quantity, double priceEach) {
        UUID orderId = UUID.randomUUID();
        SellOrder order = new SellOrder(orderId, seller, itemId, quantity, priceEach);
        sellOrders.computeIfAbsent(itemId, k -> new ArrayList<>()).add(order);
        sellOrders.get(itemId).sort(Comparator.comparingDouble(SellOrder::priceEach));
        return orderId;
    }

    /**
     * Cancels a buy order.
     *
     * @param orderId the order UUID
     * @throws IllegalArgumentException if no active buy order with that id exists
     */
    public void cancelBuyOrder(UUID orderId) {
        for (List<BuyOrder> list : buyOrders.values()) {
            if (list.removeIf(o -> o.id().equals(orderId))) {
                return;
            }
        }
        throw new IllegalArgumentException("no active buy order with id: " + orderId);
    }

    /**
     * Cancels a sell order.
     *
     * @param orderId the order UUID
     * @throws IllegalArgumentException if no active sell order with that id exists
     */
    public void cancelSellOrder(UUID orderId) {
        for (List<SellOrder> list : sellOrders.values()) {
            if (list.removeIf(o -> o.id().equals(orderId))) {
                return;
            }
        }
        throw new IllegalArgumentException("no active sell order with id: " + orderId);
    }

    /**
     * Returns an unmodifiable view of all active buy orders for the given item,
     * sorted highest price first.
     *
     * @param itemId the item identifier
     * @return unmodifiable list of buy orders; empty if none exist
     */
    public List<BuyOrder> getBuyOrders(String itemId) {
        return Collections.unmodifiableList(buyOrders.getOrDefault(itemId, Collections.emptyList()));
    }

    /**
     * Returns an unmodifiable view of all active sell orders for the given item,
     * sorted lowest price first.
     *
     * @param itemId the item identifier
     * @return unmodifiable list of sell orders; empty if none exist
     */
    public List<SellOrder> getSellOrders(String itemId) {
        return Collections.unmodifiableList(sellOrders.getOrDefault(itemId, Collections.emptyList()));
    }

    /**
     * Returns the current lowest ask price for an item, or {@code Double.MAX_VALUE} if
     * there are no active sell orders.
     *
     * @param itemId the item identifier
     * @return the lowest ask price
     */
    public double getLowestAsk(String itemId) {
        List<SellOrder> orders = sellOrders.getOrDefault(itemId, Collections.emptyList());
        return orders.isEmpty() ? Double.MAX_VALUE : orders.get(0).priceEach();
    }

    /**
     * Returns the current highest bid price for an item, or {@code 0} if there are no
     * active buy orders.
     *
     * @param itemId the item identifier
     * @return the highest bid price
     */
    public double getHighestBid(String itemId) {
        List<BuyOrder> orders = buyOrders.getOrDefault(itemId, Collections.emptyList());
        return orders.isEmpty() ? 0 : orders.get(0).priceEach();
    }

    // --- BazaarProduct bridge overloads ---

    /**
     * Adds a buy order using a {@link BazaarProduct} constant as the item identifier.
     *
     * @param buyer     the buying player's UUID
     * @param product   the bazaar product
     * @param quantity  the number of items to buy, must be positive
     * @param priceEach the maximum price per item, must be positive
     * @return the UUID of the newly created buy order
     */
    public UUID addBuyOrder(UUID buyer, BazaarProduct product, int quantity, double priceEach) {
        return addBuyOrder(buyer, product.getItemId(), quantity, priceEach);
    }

    /**
     * Adds a sell order using a {@link BazaarProduct} constant as the item identifier.
     *
     * @param seller    the selling player's UUID
     * @param product   the bazaar product
     * @param quantity  the number of items to sell, must be positive
     * @param priceEach the minimum price per item, must be positive
     * @return the UUID of the newly created sell order
     */
    public UUID addSellOrder(UUID seller, BazaarProduct product, int quantity, double priceEach) {
        return addSellOrder(seller, product.getItemId(), quantity, priceEach);
    }

    /**
     * Returns buy orders for the given {@link BazaarProduct}, sorted highest price first.
     *
     * @param product the bazaar product
     * @return unmodifiable list of buy orders; empty if none exist
     */
    public List<BuyOrder> getBuyOrders(BazaarProduct product) {
        return getBuyOrders(product.getItemId());
    }

    /**
     * Returns sell orders for the given {@link BazaarProduct}, sorted lowest price first.
     *
     * @param product the bazaar product
     * @return unmodifiable list of sell orders; empty if none exist
     */
    public List<SellOrder> getSellOrders(BazaarProduct product) {
        return getSellOrders(product.getItemId());
    }

    /**
     * Returns the lowest ask price for the given {@link BazaarProduct}.
     *
     * @param product the bazaar product
     * @return the lowest ask price, or {@code Double.MAX_VALUE} if no sell orders exist
     */
    public double getLowestAsk(BazaarProduct product) {
        return getLowestAsk(product.getItemId());
    }

    /**
     * Returns the highest bid price for the given {@link BazaarProduct}.
     *
     * @param product the bazaar product
     * @return the highest bid price, or {@code 0} if no buy orders exist
     */
    public double getHighestBid(BazaarProduct product) {
        return getHighestBid(product.getItemId());
    }

    /**
     * Returns all active orders (buy and sell) placed by the given player as
     * {@link BazaarOrder} records, buy orders first then sell orders.
     *
     * @param playerId the player's UUID
     * @return unmodifiable list of the player's orders
     */
    public List<BazaarOrder> getOrdersForPlayer(UUID playerId) {
        List<BazaarOrder> result = new ArrayList<>();
        for (List<BuyOrder> list : buyOrders.values()) {
            for (BuyOrder o : list) {
                if (o.buyer().equals(playerId)) {
                    result.add(new BazaarOrder(o.id(), o.buyer(), o.itemId(), o.quantity(), o.priceEach(), BazaarOrderType.BUY));
                }
            }
        }
        for (List<SellOrder> list : sellOrders.values()) {
            for (SellOrder o : list) {
                if (o.seller().equals(playerId)) {
                    result.add(new BazaarOrder(o.id(), o.seller(), o.itemId(), o.quantity(), o.priceEach(), BazaarOrderType.SELL));
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

    /** Removes all stored orders. */
    public void clear() {
        buyOrders.clear();
        sellOrders.clear();
    }
}
