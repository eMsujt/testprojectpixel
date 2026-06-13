package com.skyblock.core.bazaar;

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

/**
 * Singleton managing the bazaar order book.  Buy orders are matched against
 * sell orders automatically when a compatible order is added.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class BazaarManager {

    private static final BazaarManager INSTANCE = new BazaarManager();

    public enum BazaarProduct {

        // Farming
        WHEAT("WHEAT", "Wheat", "FARMING"),
        SEEDS("SEEDS", "Seeds", "FARMING"),
        CARROT("CARROT", "Carrot", "FARMING"),
        POTATO("POTATO", "Potato", "FARMING"),
        PUMPKIN("PUMPKIN", "Pumpkin", "FARMING"),
        MELON("MELON", "Melon", "FARMING"),
        MUSHROOM("MUSHROOM", "Mushroom", "FARMING"),
        CACTUS("CACTUS", "Cactus", "FARMING"),
        SUGAR_CANE("SUGAR_CANE", "Sugar Cane", "FARMING"),
        NETHER_WART("NETHER_WART", "Nether Wart", "FARMING"),
        COCOA_BEANS("COCOA_BEANS", "Cocoa Beans", "FARMING"),

        // Mining
        COBBLESTONE("COBBLESTONE", "Cobblestone", "MINING"),
        COAL("COAL", "Coal", "MINING"),
        IRON_INGOT("IRON_INGOT", "Iron Ingot", "MINING"),
        GOLD_INGOT("GOLD_INGOT", "Gold Ingot", "MINING"),
        DIAMOND("DIAMOND", "Diamond", "MINING"),
        EMERALD("EMERALD", "Emerald", "MINING"),
        REDSTONE("REDSTONE", "Redstone", "MINING"),
        LAPIS_LAZULI("LAPIS_LAZULI", "Lapis Lazuli", "MINING"),
        QUARTZ("QUARTZ", "Nether Quartz", "MINING"),
        OBSIDIAN("OBSIDIAN", "Obsidian", "MINING"),
        GLOWSTONE("GLOWSTONE", "Glowstone Dust", "MINING"),
        GRAVEL("GRAVEL", "Gravel", "MINING"),

        // Foraging
        OAK_LOG("OAK_LOG", "Oak Wood", "FORAGING"),
        SPRUCE_LOG("SPRUCE_LOG", "Spruce Wood", "FORAGING"),
        BIRCH_LOG("BIRCH_LOG", "Birch Wood", "FORAGING"),
        JUNGLE_LOG("JUNGLE_LOG", "Jungle Wood", "FORAGING"),
        ACACIA_LOG("ACACIA_LOG", "Acacia Wood", "FORAGING"),
        DARK_OAK_LOG("DARK_OAK_LOG", "Dark Oak Wood", "FORAGING"),

        // Combat
        ROTTEN_FLESH("ROTTEN_FLESH", "Rotten Flesh", "COMBAT"),
        BONE("BONE", "Bone", "COMBAT"),
        SPIDER_EYE("SPIDER_EYE", "Spider Eye", "COMBAT"),
        STRING("STRING", "String", "COMBAT"),
        GUNPOWDER("GUNPOWDER", "Gunpowder", "COMBAT"),
        ENDER_PEARL("ENDER_PEARL", "Ender Pearl", "COMBAT"),
        GHAST_TEAR("GHAST_TEAR", "Ghast Tear", "COMBAT"),
        SLIME_BALL("SLIME_BALL", "Slime Ball", "COMBAT"),
        BLAZE_ROD("BLAZE_ROD", "Blaze Rod", "COMBAT"),
        MAGMA_CREAM("MAGMA_CREAM", "Magma Cream", "COMBAT"),

        // Fishing
        RAW_FISH("RAW_FISH", "Raw Fish", "FISHING"),
        RAW_SALMON("RAW_SALMON", "Raw Salmon", "FISHING"),
        PUFFERFISH("PUFFERFISH", "Pufferfish", "FISHING"),
        INK_SAC("INK_SAC", "Ink Sac", "FISHING"),
        SPONGE("SPONGE", "Sponge", "FISHING"),

        // Enchanted Farming
        ENCHANTED_CARROT("ENCHANTED_CARROT", "Enchanted Carrot", "FARMING"),
        ENCHANTED_POTATO("ENCHANTED_POTATO", "Enchanted Potato", "FARMING"),
        ENCHANTED_PUMPKIN("ENCHANTED_PUMPKIN", "Enchanted Pumpkin", "FARMING"),
        ENCHANTED_MELON("ENCHANTED_MELON", "Enchanted Melon", "FARMING"),
        ENCHANTED_WHEAT("ENCHANTED_WHEAT", "Enchanted Bread", "FARMING"),
        ENCHANTED_MUSHROOM("ENCHANTED_MUSHROOM", "Enchanted Red Mushroom", "FARMING"),
        ENCHANTED_CACTUS("ENCHANTED_CACTUS", "Enchanted Cactus Green", "FARMING"),
        ENCHANTED_SUGAR_CANE("ENCHANTED_SUGAR_CANE", "Enchanted Sugar Cane", "FARMING"),
        ENCHANTED_NETHER_WART("ENCHANTED_NETHER_WART", "Enchanted Nether Wart", "FARMING"),
        ENCHANTED_COCOA_BEANS("ENCHANTED_COCOA_BEANS", "Enchanted Cookie", "FARMING"),

        // Enchanted Mining
        ENCHANTED_COBBLESTONE("ENCHANTED_COBBLESTONE", "Enchanted Cobblestone", "MINING"),
        ENCHANTED_COAL("ENCHANTED_COAL", "Enchanted Coal", "MINING"),
        ENCHANTED_IRON("ENCHANTED_IRON", "Enchanted Iron", "MINING"),
        ENCHANTED_GOLD("ENCHANTED_GOLD", "Enchanted Gold", "MINING"),
        ENCHANTED_DIAMOND("ENCHANTED_DIAMOND", "Enchanted Diamond", "MINING"),
        ENCHANTED_EMERALD("ENCHANTED_EMERALD", "Enchanted Emerald", "MINING"),
        ENCHANTED_REDSTONE("ENCHANTED_REDSTONE", "Enchanted Redstone", "MINING"),
        ENCHANTED_LAPIS("ENCHANTED_LAPIS", "Enchanted Lapis Lazuli", "MINING"),
        ENCHANTED_QUARTZ("ENCHANTED_QUARTZ", "Enchanted Quartz", "MINING"),
        ENCHANTED_OBSIDIAN("ENCHANTED_OBSIDIAN", "Enchanted Obsidian", "MINING"),

        // Enchanted Foraging
        ENCHANTED_OAK_LOG("ENCHANTED_OAK_LOG", "Enchanted Oak Wood", "FORAGING"),
        ENCHANTED_SPRUCE_LOG("ENCHANTED_SPRUCE_LOG", "Enchanted Spruce Wood", "FORAGING"),
        ENCHANTED_BIRCH_LOG("ENCHANTED_BIRCH_LOG", "Enchanted Birch Wood", "FORAGING"),
        ENCHANTED_JUNGLE_LOG("ENCHANTED_JUNGLE_LOG", "Enchanted Jungle Wood", "FORAGING"),
        ENCHANTED_ACACIA_LOG("ENCHANTED_ACACIA_LOG", "Enchanted Acacia Wood", "FORAGING"),
        ENCHANTED_DARK_OAK_LOG("ENCHANTED_DARK_OAK_LOG", "Enchanted Dark Oak Wood", "FORAGING"),

        // Enchanted Combat
        ENCHANTED_ROTTEN_FLESH("ENCHANTED_ROTTEN_FLESH", "Enchanted Rotten Flesh", "COMBAT"),
        ENCHANTED_BONE("ENCHANTED_BONE", "Enchanted Bone", "COMBAT"),
        ENCHANTED_SPIDER_EYE("ENCHANTED_SPIDER_EYE", "Enchanted Spider Eye", "COMBAT"),
        ENCHANTED_STRING("ENCHANTED_STRING", "Enchanted String", "COMBAT"),
        ENCHANTED_GUNPOWDER("ENCHANTED_GUNPOWDER", "Enchanted Gunpowder", "COMBAT"),
        ENCHANTED_ENDER_PEARL("ENCHANTED_ENDER_PEARL", "Enchanted Ender Pearl", "COMBAT"),
        ENCHANTED_GHAST_TEAR("ENCHANTED_GHAST_TEAR", "Enchanted Ghast Tear", "COMBAT"),
        ENCHANTED_SLIME_BALL("ENCHANTED_SLIME_BALL", "Enchanted Slime Ball", "COMBAT"),
        ENCHANTED_BLAZE_ROD("ENCHANTED_BLAZE_ROD", "Enchanted Blaze Rod", "COMBAT"),
        ENCHANTED_MAGMA_CREAM("ENCHANTED_MAGMA_CREAM", "Enchanted Magma Cream", "COMBAT"),

        // Enchanted Fishing
        ENCHANTED_RAW_FISH("ENCHANTED_RAW_FISH", "Enchanted Raw Fish", "FISHING"),
        ENCHANTED_RAW_SALMON("ENCHANTED_RAW_SALMON", "Enchanted Raw Salmon", "FISHING"),
        ENCHANTED_INK_SAC("ENCHANTED_INK_SAC", "Enchanted Ink Sac", "FISHING"),
        ENCHANTED_CLAY("ENCHANTED_CLAY", "Enchanted Clay Ball", "FISHING"),
        ENCHANTED_LILY_PAD("ENCHANTED_LILY_PAD", "Enchanted Lily Pad", "FISHING"),
        ENCHANTED_PRISMARINE_SHARD("ENCHANTED_PRISMARINE_SHARD", "Enchanted Prismarine Shard", "FISHING"),
        ENCHANTED_PRISMARINE_CRYSTALS("ENCHANTED_PRISMARINE_CRYSTALS", "Enchanted Prismarine Crystals", "FISHING"),

        // More Fishing
        CLAY("CLAY", "Clay", "FISHING"),
        LILY_PAD("LILY_PAD", "Lily Pad", "FISHING"),
        PRISMARINE_SHARD("PRISMARINE_SHARD", "Prismarine Shard", "FISHING"),
        PRISMARINE_CRYSTALS("PRISMARINE_CRYSTALS", "Prismarine Crystals", "FISHING"),

        // More Mining
        MITHRIL_ORE("MITHRIL_ORE", "Mithril", "MINING"),
        TITANIUM_ORE("TITANIUM_ORE", "Titanium", "MINING"),
        FLINT("FLINT", "Flint", "MINING"),
        SAND("SAND", "Sand", "MINING"),
        END_STONE("END_STONE", "End Stone", "MINING"),
        NETHERRACK("NETHERRACK", "Netherrack", "MINING"),

        // More Enchanted Mining
        ENCHANTED_GLOWSTONE_DUST("ENCHANTED_GLOWSTONE_DUST", "Enchanted Glowstone Dust", "MINING"),
        ENCHANTED_FLINT("ENCHANTED_FLINT", "Enchanted Flint", "MINING"),
        ENCHANTED_SAND("ENCHANTED_SAND", "Enchanted Sand", "MINING"),
        ENCHANTED_END_STONE("ENCHANTED_END_STONE", "Enchanted End Stone", "MINING"),
        ENCHANTED_NETHERRACK("ENCHANTED_NETHERRACK", "Enchanted Netherrack", "MINING"),

        // More Combat
        FEATHER("FEATHER", "Feather", "COMBAT"),
        LEATHER("LEATHER", "Leather", "COMBAT"),
        POISONOUS_POTATO("POISONOUS_POTATO", "Poisonous Potato", "COMBAT"),

        // More Enchanted Combat
        ENCHANTED_FEATHER("ENCHANTED_FEATHER", "Enchanted Feather", "COMBAT"),
        ENCHANTED_LEATHER("ENCHANTED_LEATHER", "Enchanted Leather", "COMBAT"),
        ENCHANTED_POISONOUS_POTATO("ENCHANTED_POISONOUS_POTATO", "Enchanted Poisonous Potato", "COMBAT"),

        // Misc
        ICE("ICE", "Ice", "MISC"),
        SNOW_BLOCK("SNOW_BLOCK", "Snow Block", "MISC"),
        ENCHANTED_BOOK("ENCHANTED_BOOK", "Enchanted Book", "MISC"),
        ENCHANTED_ICE("ENCHANTED_ICE", "Enchanted Ice", "MISC"),
        ENCHANTED_SNOW_BLOCK("ENCHANTED_SNOW_BLOCK", "Enchanted Snow Block", "MISC"),
        ENCHANTED_SEEDS("ENCHANTED_SEEDS", "Enchanted Seeds", "FARMING");

        private final String itemId;
        private final String displayName;
        private final String category;

        BazaarProduct(String itemId, String displayName, String category) {
            this.itemId = itemId;
            this.displayName = displayName;
            this.category = category;
        }

        public String getItemId() {
            return itemId;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getCategory() {
            return category;
        }

        public static BazaarProduct fromItemId(String itemId) {
            if (itemId == null) return null;
            for (BazaarProduct p : values()) {
                if (p.itemId.equalsIgnoreCase(itemId)) {
                    return p;
                }
            }
            return null;
        }
    }

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

    /** Per-player transaction history. */
    private final Map<UUID, List<String>> playerTransactions = new HashMap<>();

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

    /**
     * Returns an unmodifiable view of the transaction history for the given player.
     *
     * @param playerId the player's UUID, must not be null
     * @return unmodifiable list of transaction entries; empty if none recorded
     */
    public List<String> getTransactionHistory(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return Collections.unmodifiableList(
                playerTransactions.getOrDefault(playerId, Collections.emptyList()));
    }

    /**
     * Appends a transaction entry to the given player's history.
     *
     * @param playerId the player's UUID, must not be null
     * @param entry    the transaction string to record, must not be null
     */
    public void addTransaction(UUID playerId, String entry) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(entry, "entry");
        playerTransactions.computeIfAbsent(playerId, k -> new ArrayList<>()).add(entry);
    }

    /**
     * Loads per-player transaction histories and active buy/sell orders from
     * {@code bazaar.yml} inside the given data folder.
     *
     * @param dataFolder the plugin data folder, must not be null
     */
    public void load(File dataFolder) {
        File file = new File(dataFolder, "bazaar.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        playerTransactions.clear();
        if (cfg.isConfigurationSection("transactions")) {
            for (String key : cfg.getConfigurationSection("transactions").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    List<String> history = cfg.getStringList("transactions." + key);
                    playerTransactions.put(uuid, new ArrayList<>(history));
                } catch (IllegalArgumentException ignored) {
                    // skip malformed entries
                }
            }
        }

        buyOrders.clear();
        if (cfg.isConfigurationSection("buyOrders")) {
            for (String itemId : cfg.getConfigurationSection("buyOrders").getKeys(false)) {
                List<Map<?, ?>> orders = cfg.getMapList("buyOrders." + itemId);
                List<BuyOrder> list = new ArrayList<>();
                for (Map<?, ?> map : orders) {
                    try {
                        UUID id = UUID.fromString((String) map.get("id"));
                        UUID buyer = UUID.fromString((String) map.get("buyer"));
                        int quantity = ((Number) map.get("quantity")).intValue();
                        double priceEach = ((Number) map.get("priceEach")).doubleValue();
                        list.add(new BuyOrder(id, buyer, itemId, quantity, priceEach));
                    } catch (Exception ignored) {
                        // skip malformed order entries
                    }
                }
                if (!list.isEmpty()) {
                    list.sort(Comparator.comparingDouble(BuyOrder::priceEach).reversed());
                    buyOrders.put(itemId, list);
                }
            }
        }

        sellOrders.clear();
        if (cfg.isConfigurationSection("sellOrders")) {
            for (String itemId : cfg.getConfigurationSection("sellOrders").getKeys(false)) {
                List<Map<?, ?>> orders = cfg.getMapList("sellOrders." + itemId);
                List<SellOrder> list = new ArrayList<>();
                for (Map<?, ?> map : orders) {
                    try {
                        UUID id = UUID.fromString((String) map.get("id"));
                        UUID seller = UUID.fromString((String) map.get("seller"));
                        int quantity = ((Number) map.get("quantity")).intValue();
                        double priceEach = ((Number) map.get("priceEach")).doubleValue();
                        list.add(new SellOrder(id, seller, itemId, quantity, priceEach));
                    } catch (Exception ignored) {
                        // skip malformed order entries
                    }
                }
                if (!list.isEmpty()) {
                    list.sort(Comparator.comparingDouble(SellOrder::priceEach));
                    sellOrders.put(itemId, list);
                }
            }
        }
    }

    /**
     * Saves per-player transaction histories and active buy/sell orders to
     * {@code bazaar.yml} inside the given data folder.
     *
     * @param dataFolder the plugin data folder, must not be null
     * @throws RuntimeException if the file cannot be written
     */
    public void save(File dataFolder) {
        File file = new File(dataFolder, "bazaar.yml");
        YamlConfiguration cfg = new YamlConfiguration();

        for (Map.Entry<UUID, List<String>> entry : playerTransactions.entrySet()) {
            cfg.set("transactions." + entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, List<BuyOrder>> entry : buyOrders.entrySet()) {
            List<Map<String, Object>> serialized = new ArrayList<>();
            for (BuyOrder o : entry.getValue()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", o.id().toString());
                map.put("buyer", o.buyer().toString());
                map.put("quantity", o.quantity());
                map.put("priceEach", o.priceEach());
                serialized.add(map);
            }
            cfg.set("buyOrders." + entry.getKey(), serialized);
        }

        for (Map.Entry<String, List<SellOrder>> entry : sellOrders.entrySet()) {
            List<Map<String, Object>> serialized = new ArrayList<>();
            for (SellOrder o : entry.getValue()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", o.id().toString());
                map.put("seller", o.seller().toString());
                map.put("quantity", o.quantity());
                map.put("priceEach", o.priceEach());
                serialized.add(map);
            }
            cfg.set("sellOrders." + entry.getKey(), serialized);
        }

        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save bazaar.yml", e);
        }
    }

    /** Removes all stored orders. */
    public void clear() {
        buyOrders.clear();
        sellOrders.clear();
    }
}
