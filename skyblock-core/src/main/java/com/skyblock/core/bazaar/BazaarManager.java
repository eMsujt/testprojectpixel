package com.skyblock.core.bazaar;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Singleton managing the bazaar order book with YAML-backed persistence.
 *
 * <p>Call {@link #load(File)} on plugin enable and {@link #save(File)} on
 * plugin disable to persist orders across restarts.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class BazaarManager {

    private static final BazaarManager INSTANCE = new BazaarManager();
    private static final String FILE_NAME = "bazaar.yml";
    private static final Logger LOG = Logger.getLogger(BazaarManager.class.getName());

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

    /** Removes all stored orders. */
    public void clear() {
        buyOrders.clear();
        sellOrders.clear();
    }

    /**
     * Loads buy and sell orders from {@code bazaar.yml} in the given data folder.
     * Any currently stored orders are discarded first.
     *
     * @param dataFolder the plugin's data folder; created if it does not exist
     */
    public void load(File dataFolder) {
        Objects.requireNonNull(dataFolder, "dataFolder");
        clear();
        File file = new File(dataFolder, FILE_NAME);
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        List<?> buyList = cfg.getList("buy-orders", Collections.emptyList());
        for (Object entry : buyList) {
            if (!(entry instanceof Map<?, ?> map)) continue;
            try {
                UUID id       = UUID.fromString((String) map.get("id"));
                UUID buyer    = UUID.fromString((String) map.get("buyer"));
                String itemId = (String) map.get("itemId");
                int qty       = ((Number) map.get("quantity")).intValue();
                double price  = ((Number) map.get("priceEach")).doubleValue();
                BuyOrder order = new BuyOrder(id, buyer, itemId, qty, price);
                buyOrders.computeIfAbsent(itemId, k -> new ArrayList<>()).add(order);
            } catch (Exception e) {
                LOG.warning("Skipping malformed buy-order entry: " + e.getMessage());
            }
        }
        for (List<BuyOrder> list : buyOrders.values()) {
            list.sort(Comparator.comparingDouble(BuyOrder::priceEach).reversed());
        }

        List<?> sellList = cfg.getList("sell-orders", Collections.emptyList());
        for (Object entry : sellList) {
            if (!(entry instanceof Map<?, ?> map)) continue;
            try {
                UUID id        = UUID.fromString((String) map.get("id"));
                UUID seller    = UUID.fromString((String) map.get("seller"));
                String itemId  = (String) map.get("itemId");
                int qty        = ((Number) map.get("quantity")).intValue();
                double price   = ((Number) map.get("priceEach")).doubleValue();
                SellOrder order = new SellOrder(id, seller, itemId, qty, price);
                sellOrders.computeIfAbsent(itemId, k -> new ArrayList<>()).add(order);
            } catch (Exception e) {
                LOG.warning("Skipping malformed sell-order entry: " + e.getMessage());
            }
        }
        for (List<SellOrder> list : sellOrders.values()) {
            list.sort(Comparator.comparingDouble(SellOrder::priceEach));
        }
    }

    /**
     * Saves all current buy and sell orders to {@code bazaar.yml} in the given
     * data folder.
     *
     * @param dataFolder the plugin's data folder; created if it does not exist
     */
    public void save(File dataFolder) {
        Objects.requireNonNull(dataFolder, "dataFolder");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        YamlConfiguration cfg = new YamlConfiguration();

        List<Map<String, Object>> buyList = new ArrayList<>();
        for (List<BuyOrder> list : buyOrders.values()) {
            for (BuyOrder o : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("id",        o.id().toString());
                map.put("buyer",     o.buyer().toString());
                map.put("itemId",    o.itemId());
                map.put("quantity",  o.quantity());
                map.put("priceEach", o.priceEach());
                buyList.add(map);
            }
        }
        cfg.set("buy-orders", buyList);

        List<Map<String, Object>> sellList = new ArrayList<>();
        for (List<SellOrder> list : sellOrders.values()) {
            for (SellOrder o : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("id",        o.id().toString());
                map.put("seller",    o.seller().toString());
                map.put("itemId",    o.itemId());
                map.put("quantity",  o.quantity());
                map.put("priceEach", o.priceEach());
                sellList.add(map);
            }
        }
        cfg.set("sell-orders", sellList);

        try {
            cfg.save(new File(dataFolder, FILE_NAME));
        } catch (IOException e) {
            LOG.severe("Failed to save bazaar.yml: " + e.getMessage());
        }
    }
}
