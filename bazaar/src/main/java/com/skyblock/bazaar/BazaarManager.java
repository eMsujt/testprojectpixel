package com.skyblock.bazaar;

import com.skyblock.bazaar.BazaarOrder.OrderType;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final Map<UUID, List<String>> bazaarHistory = new HashMap<>();

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
        recordBazaarEvent(playerId, "Placed buy order: " + amount + "x " + productId + " @ " + pricePerUnit);
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
        recordBazaarEvent(playerId, "Placed sell order: " + amount + "x " + productId + " @ " + pricePerUnit);
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

    public void load(File dataFolder) {
        File file = new File(dataFolder, "bazaar.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        bazaarHistory.clear();
        if (cfg.isConfigurationSection("bazaarHistory")) {
            for (String key : cfg.getConfigurationSection("bazaarHistory").getKeys(false)) {
                try {
                    List<String> entries = cfg.getStringList("bazaarHistory." + key);
                    if (!entries.isEmpty()) {
                        bazaarHistory.put(UUID.fromString(key), new ArrayList<>(entries));
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "bazaar.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, List<String>> entry : bazaarHistory.entrySet()) {
            cfg.set("bazaarHistory." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save bazaar.yml", e);
        }
    }
}
