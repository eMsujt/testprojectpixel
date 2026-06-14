package com.skyblock.economy;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Manages bazaar products available for instant buy/sell and limit order books.
 *
 * <p>Each product maintains two order books keyed by price (coins per unit)
 * mapped to total quantity at that price:
 * <ul>
 *   <li><b>buy orders</b> (bids) – {@code TreeMap<Long,Long>} in descending
 *       price order so {@code buyOrders.firstKey()} is the best (highest) bid.
 *   <li><b>sell orders</b> (asks) – {@code TreeMap<Long,Long>} in ascending
 *       price order so {@code sellOrders.firstKey()} is the best (lowest) ask.
 * </ul>
 * Order books use {@code Long} prices to represent whole-coin amounts.
 * Instant-buy/sell pricing uses {@code double} for backward compatibility.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class BazaarManager {

    /** Catalog of all tradeable bazaar products. */
    public enum BazaarProduct {
        WHEAT,
        CARROT,
        POTATO,
        SUGAR_CANE,
        PUMPKIN,
        MELON,
        SEEDS,
        COCOA_BEANS,
        CACTUS,
        MUSHROOM,
        NETHER_WART,
        RED_MUSHROOM,
        BROWN_MUSHROOM,
        SAND,
        GRAVEL,
        ICE,
        NETHERRACK,
        GLOWSTONE_DUST,
        COBBLESTONE,
        OAK_WOOD,
        SPRUCE_WOOD,
        BIRCH_WOOD,
        DARK_OAK_WOOD,
        JUNGLE_WOOD,
        ACACIA_WOOD,
        RAW_FISH,
        PUFFERFISH,
        CLOWNFISH,
        RAW_SALMON,
        INK_SACK,
        ROTTEN_FLESH,
        BONE,
        GUNPOWDER,
        SPIDER_EYE,
        STRING,
        FEATHER,
        LEATHER,
        RABBIT_HIDE,
        RAW_CHICKEN,
        RAW_BEEF
    }

    /**
     * A single bazaar product listing with live pricing, volume data, and order books.
     *
     * <p>Instances are created only through
     * {@link BazaarManager#addProduct(String, double, double)} and mutated
     * only through {@link BazaarManager}.</p>
     */
    public static final class ProductData {

        private final String productId;
        private double buyPrice;
        private double sellPrice;
        private long volume;

        /** Bids: highest price first. */
        private final TreeMap<Long, Long> buyOrders = new TreeMap<>(Comparator.reverseOrder());
        /** Asks: lowest price first. */
        private final TreeMap<Long, Long> sellOrders = new TreeMap<>();

        private ProductData(String productId, double buyPrice, double sellPrice) {
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

        /**
         * Returns an unmodifiable view of the buy-order book (bids).
         *
         * <p>Keys are prices in descending order; values are quantities at
         * that price.</p>
         *
         * @return buy orders, highest price first
         */
        public Map<Long, Long> getBuyOrders() {
            return Collections.unmodifiableMap(buyOrders);
        }

        /**
         * Returns an unmodifiable view of the sell-order book (asks).
         *
         * <p>Keys are prices in ascending order; values are quantities at
         * that price.</p>
         *
         * @return sell orders, lowest price first
         */
        public Map<Long, Long> getSellOrders() {
            return Collections.unmodifiableMap(sellOrders);
        }

        /**
         * Returns the best (highest) buy-order price, or {@code -1} if there
         * are no open buy orders.
         *
         * @return the highest bid price, or {@code -1}
         */
        public long bestBuyPrice() {
            return buyOrders.isEmpty() ? -1L : buyOrders.firstKey();
        }

        /**
         * Returns the best (lowest) sell-order price, or {@code -1} if there
         * are no open sell orders.
         *
         * @return the lowest ask price, or {@code -1}
         */
        public long bestSellPrice() {
            return sellOrders.isEmpty() ? -1L : sellOrders.firstKey();
        }
    }

    private final Map<String, ProductData> products = new HashMap<>();
    private final Map<UUID, List<String>> bazaarHistory = new HashMap<>();

    /**
     * Registers a new product in the bazaar.
     *
     * @param productId the unique product identifier, must not be null or blank
     * @param buyPrice  the instant-buy price per unit, must be positive
     * @param sellPrice the instant-sell price per unit, must be positive and
     *                  not exceed {@code buyPrice}
     * @return the newly created product data
     * @throws IllegalArgumentException if any argument is invalid or the
     *                                  product is already registered
     */
    public ProductData addProduct(String productId, double buyPrice, double sellPrice) {
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
        ProductData product = new ProductData(productId, buyPrice, sellPrice);
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
        ProductData product = requireProduct(productId);
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
        ProductData product = requireProduct(productId);
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
        ProductData product = requireProduct(productId);
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        product.volume += amount;
        return product.sellPrice * amount;
    }

    /**
     * Places a buy order (bid) for the given product, merging into any
     * existing order at the same price.
     *
     * @param productId the product to bid on
     * @param price     the bid price per unit, must be positive
     * @param quantity  the number of units, must be positive
     * @throws IllegalArgumentException if the product does not exist or
     *                                  {@code price}/{@code quantity} are invalid
     */
    public void placeBuyOrder(String productId, long price, long quantity) {
        ProductData product = requireProduct(productId);
        requirePositive(price, "price");
        requirePositive(quantity, "quantity");
        product.buyOrders.merge(price, quantity, Math::addExact);
    }

    /**
     * Places a sell order (ask) for the given product, merging into any
     * existing order at the same price.
     *
     * @param productId the product to ask on
     * @param price     the ask price per unit, must be positive
     * @param quantity  the number of units, must be positive
     * @throws IllegalArgumentException if the product does not exist or
     *                                  {@code price}/{@code quantity} are invalid
     */
    public void placeSellOrder(String productId, long price, long quantity) {
        ProductData product = requireProduct(productId);
        requirePositive(price, "price");
        requirePositive(quantity, "quantity");
        product.sellOrders.merge(price, quantity, Math::addExact);
    }

    /**
     * Cancels (reduces) a buy order at the given price by {@code quantity}
     * units.  If the remaining quantity reaches zero the price level is
     * removed entirely.
     *
     * @param productId the product
     * @param price     the price level to reduce
     * @param quantity  the units to cancel, must be positive and not exceed
     *                  the quantity currently at that price
     * @throws IllegalArgumentException if the product does not exist, the
     *                                  price level does not exist, or
     *                                  {@code quantity} exceeds what is queued
     */
    public void cancelBuyOrder(String productId, long price, long quantity) {
        ProductData product = requireProduct(productId);
        requirePositive(quantity, "quantity");
        cancelOrder(product.buyOrders, price, quantity, "buy");
    }

    /**
     * Cancels (reduces) a sell order at the given price by {@code quantity}
     * units.  If the remaining quantity reaches zero the price level is
     * removed entirely.
     *
     * @param productId the product
     * @param price     the price level to reduce
     * @param quantity  the units to cancel, must be positive and not exceed
     *                  the quantity currently at that price
     * @throws IllegalArgumentException if the product does not exist, the
     *                                  price level does not exist, or
     *                                  {@code quantity} exceeds what is queued
     */
    public void cancelSellOrder(String productId, long price, long quantity) {
        ProductData product = requireProduct(productId);
        requirePositive(quantity, "quantity");
        cancelOrder(product.sellOrders, price, quantity, "sell");
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
     * @return the product data, or {@code null} if none is registered under that id
     */
    public ProductData getProduct(String productId) {
        return products.get(productId);
    }

    /**
     * Returns all registered products.
     *
     * @return an unmodifiable view of the registered products
     */
    public Collection<ProductData> getProducts() {
        return Collections.unmodifiableCollection(products.values());
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

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private ProductData requireProduct(String productId) {
        ProductData product = products.get(productId);
        if (product == null) {
            throw new IllegalArgumentException("no product registered with id: " + productId);
        }
        return product;
    }

    private static void requirePositive(long value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive: " + value);
        }
    }

    private static void cancelOrder(TreeMap<Long, Long> book, long price, long quantity, String side) {
        Long current = book.get(price);
        if (current == null) {
            throw new IllegalArgumentException(
                    "no open " + side + " order at price " + price);
        }
        if (quantity > current) {
            throw new IllegalArgumentException(
                    "cancel quantity " + quantity + " exceeds queued " + side
                            + " quantity " + current + " at price " + price);
        }
        if (quantity == current) {
            book.remove(price);
        } else {
            book.put(price, current - quantity);
        }
    }
}
