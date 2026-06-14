package com.skyblock.core.bazaar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class BazaarManager {

    public enum OrderType {
        BUY("Buy Order"),
        SELL("Sell Order");

        private final String displayName;

        OrderType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public record BazaarOrder(UUID id, UUID player, String itemId, int quantity, double priceEach, OrderType type) {
        public BazaarOrder {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(player, "player");
            Objects.requireNonNull(itemId, "itemId");
            Objects.requireNonNull(type, "type");
            if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
            if (priceEach <= 0) throw new IllegalArgumentException("priceEach must be positive");
        }
    }

    private final Map<UUID, BazaarOrder> buyOrders = new HashMap<>();
    private final Map<UUID, BazaarOrder> sellOrders = new HashMap<>();
    private final Map<UUID, List<String>> bazaarHistory = new ConcurrentHashMap<>();

    public UUID addBuyOrder(UUID buyer, String itemId, int quantity, double priceEach) {
        UUID id = UUID.randomUUID();
        buyOrders.put(id, new BazaarOrder(id, buyer, itemId, quantity, priceEach, OrderType.BUY));
        recordBazaarEvent(buyer, "Placed buy order: " + quantity + "x " + itemId + " @ " + priceEach);
        return id;
    }

    public UUID addSellOrder(UUID seller, String itemId, int quantity, double priceEach) {
        UUID id = UUID.randomUUID();
        sellOrders.put(id, new BazaarOrder(id, seller, itemId, quantity, priceEach, OrderType.SELL));
        recordBazaarEvent(seller, "Placed sell order: " + quantity + "x " + itemId + " @ " + priceEach);
        return id;
    }

    public boolean cancelBuyOrder(UUID orderId, UUID player) {
        BazaarOrder order = buyOrders.get(orderId);
        if (order == null || !order.player().equals(player)) return false;
        buyOrders.remove(orderId);
        recordBazaarEvent(player, "Cancelled buy order: " + order.quantity() + "x " + order.itemId());
        return true;
    }

    public boolean cancelSellOrder(UUID orderId, UUID player) {
        BazaarOrder order = sellOrders.get(orderId);
        if (order == null || !order.player().equals(player)) return false;
        sellOrders.remove(orderId);
        recordBazaarEvent(player, "Cancelled sell order: " + order.quantity() + "x " + order.itemId());
        return true;
    }

    public double getLowestAsk(String itemId) {
        return sellOrders.values().stream()
                .filter(o -> o.itemId().equalsIgnoreCase(itemId))
                .mapToDouble(BazaarOrder::priceEach)
                .min()
                .orElse(Double.MAX_VALUE);
    }

    public double getHighestBid(String itemId) {
        return buyOrders.values().stream()
                .filter(o -> o.itemId().equalsIgnoreCase(itemId))
                .mapToDouble(BazaarOrder::priceEach)
                .max()
                .orElse(0);
    }

    public long getBuyOrderCount(String itemId) {
        return buyOrders.values().stream()
                .filter(o -> o.itemId().equalsIgnoreCase(itemId))
                .count();
    }

    public long getSellOrderCount(String itemId) {
        return sellOrders.values().stream()
                .filter(o -> o.itemId().equalsIgnoreCase(itemId))
                .count();
    }

    public void recordBazaarEvent(UUID player, String summary) {
        bazaarHistory.computeIfAbsent(player, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getBazaarHistory(UUID player) {
        return Collections.unmodifiableList(bazaarHistory.getOrDefault(player, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllBazaarHistory() {
        Map<UUID, List<String>> copy = new HashMap<>();
        for (Map.Entry<UUID, List<String>> entry : bazaarHistory.entrySet()) {
            copy.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        }
        return Collections.unmodifiableMap(copy);
    }

    public List<BazaarOrder> getOrdersForPlayer(UUID player) {
        List<BazaarOrder> result = new ArrayList<>();
        buyOrders.values().stream()
                .filter(o -> o.player().equals(player))
                .sorted(Comparator.comparing(BazaarOrder::itemId))
                .forEach(result::add);
        sellOrders.values().stream()
                .filter(o -> o.player().equals(player))
                .sorted(Comparator.comparing(BazaarOrder::itemId))
                .forEach(result::add);
        return Collections.unmodifiableList(result);
    }
}
