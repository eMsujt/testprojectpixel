package com.skyblock.plugin.economy;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.UUID;

/**
 * YAML-driven registry of Bazaar products loaded from {@code bazaar.yml}.
 *
 * <p>Each product carries an item id and its two standing prices: the
 * insta-buy price is what a player pays to instantly buy, and the insta-sell
 * price is what they receive to instantly sell (normally slightly lower,
 * modelling the spread). The bundled default is copied out of the jar on first
 * run. Loaded products are held in memory in definition order and looked up by
 * item id.</p>
 */
public final class BazaarManager {

    private static final BazaarManager INSTANCE = new BazaarManager();

    /**
     * A single loaded bazaar product.
     *
     * @param itemId         the item id this product is keyed by
     * @param instaBuyPrice  the instant-buy price in coins
     * @param instaSellPrice the instant-sell price in coins
     */
    public record BazaarProduct(String itemId, double instaBuyPrice, double instaSellPrice) {
        public BazaarProduct {
            Objects.requireNonNull(itemId, "itemId");
        }
    }

    /**
     * A single outstanding buy or sell order placed by a player.
     *
     * @param player the UUID of the player who placed the order
     * @param price  the unit price in coins
     * @param amount the number of items the order is for
     */
    public record BazaarOrder(UUID player, double price, int amount) {
        public BazaarOrder {
            Objects.requireNonNull(player, "player");
        }
    }

    private final Map<String, BazaarProduct> products = new LinkedHashMap<>();

    /** Outstanding buy orders keyed by product item-id; highest price polled first. */
    private final Map<String, PriorityQueue<BazaarOrder>> buyOrders = new HashMap<>();

    /** Outstanding sell orders keyed by product item-id; lowest price polled first. */
    private final Map<String, PriorityQueue<BazaarOrder>> sellOrders = new HashMap<>();

    private BazaarManager() {
    }

    public static BazaarManager getInstance() {
        return INSTANCE;
    }

    /**
     * Reads {@code bazaar.yml} from the plugin data folder, copying the bundled
     * default out of the jar on first run, then parses every defined product.
     *
     * @param plugin the owning plugin, used for resource extraction and logging
     */
    public void load(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "bazaar.yml");
        if (!file.exists() && plugin.getResource("bazaar.yml") != null) {
            plugin.saveResource("bazaar.yml", false);
        }
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = cfg.isConfigurationSection("products")
                ? cfg.getConfigurationSection("products")
                : cfg;
        products.clear();
        for (String itemId : root.getKeys(false)) {
            if (!root.isConfigurationSection(itemId)) {
                continue;
            }
            ConfigurationSection section = root.getConfigurationSection(itemId);
            products.put(itemId, new BazaarProduct(itemId,
                    section.getDouble("buy"), section.getDouble("sell")));
        }
        plugin.getLogger().info("Loaded " + products.size() + " bazaar products.");
    }

    /** Registers (or replaces) a product keyed by its item id. */
    public void register(BazaarProduct product) {
        Objects.requireNonNull(product, "product");
        products.put(product.itemId(), product);
    }

    /** Returns the product with the given item id, or {@code null} if absent. */
    public BazaarProduct getProduct(String itemId) {
        return products.get(itemId);
    }

    /** Returns the instant-buy price for an item, or {@code 0.0} if unpriced. */
    public double getInstaBuyPrice(String itemId) {
        BazaarProduct product = products.get(itemId);
        return product == null ? 0.0 : product.instaBuyPrice();
    }

    /** Returns the instant-sell price for an item, or {@code 0.0} if unpriced. */
    public double getInstaSellPrice(String itemId) {
        BazaarProduct product = products.get(itemId);
        return product == null ? 0.0 : product.instaSellPrice();
    }

    /** Returns an unmodifiable view of all loaded products in definition order. */
    public List<BazaarProduct> getProducts() {
        return Collections.unmodifiableList(new ArrayList<>(products.values()));
    }

    /** Records a buy order for the given item id. */
    public void addBuyOrder(String itemId, BazaarOrder order) {
        Objects.requireNonNull(itemId, "itemId");
        Objects.requireNonNull(order, "order");
        buyOrders.computeIfAbsent(itemId, k -> new PriorityQueue<>(Comparator.comparingDouble(BazaarOrder::price).reversed()))
                .add(order);
    }

    /** Records a sell order for the given item id. */
    public void addSellOrder(String itemId, BazaarOrder order) {
        Objects.requireNonNull(itemId, "itemId");
        Objects.requireNonNull(order, "order");
        sellOrders.computeIfAbsent(itemId, k -> new PriorityQueue<>(Comparator.comparingDouble(BazaarOrder::price)))
                .add(order);
    }

    /**
     * Returns a snapshot of buy orders for the given item, highest price first.
     * The returned list is a copy; the underlying queue is not modified.
     */
    public List<BazaarOrder> getBuyOrders(String itemId) {
        PriorityQueue<BazaarOrder> pq = buyOrders.get(itemId);
        if (pq == null || pq.isEmpty()) {
            return Collections.emptyList();
        }
        List<BazaarOrder> snapshot = new ArrayList<>(pq);
        snapshot.sort(Comparator.comparingDouble(BazaarOrder::price).reversed());
        return Collections.unmodifiableList(snapshot);
    }

    /**
     * Returns a snapshot of sell orders for the given item, lowest price first.
     * The returned list is a copy; the underlying queue is not modified.
     */
    public List<BazaarOrder> getSellOrders(String itemId) {
        PriorityQueue<BazaarOrder> pq = sellOrders.get(itemId);
        if (pq == null || pq.isEmpty()) {
            return Collections.emptyList();
        }
        List<BazaarOrder> snapshot = new ArrayList<>(pq);
        snapshot.sort(Comparator.comparingDouble(BazaarOrder::price));
        return Collections.unmodifiableList(snapshot);
    }

    /** Returns the highest bid price for an item, or {@code 0.0} if no buy orders exist. */
    public double getHighestBid(String itemId) {
        PriorityQueue<BazaarOrder> pq = buyOrders.get(itemId);
        return (pq == null || pq.isEmpty()) ? 0.0 : pq.peek().price();
    }

    /** Returns the lowest ask price for an item, or {@code Double.MAX_VALUE} if no sell orders exist. */
    public double getLowestAsk(String itemId) {
        PriorityQueue<BazaarOrder> pq = sellOrders.get(itemId);
        return (pq == null || pq.isEmpty()) ? Double.MAX_VALUE : pq.peek().price();
    }
}
