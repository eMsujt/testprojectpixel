package com.skyblock.core.manager;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public final class BazaarManager {

    public enum BazaarCategory {
        FARMING, MINING, COMBAT, FORAGING, FISHING, MISC
    }

    public enum BazaarProduct {
        // FARMING
        RAW_PORKCHOP("Raw Porkchop", BazaarCategory.FARMING),
        RAW_CHICKEN("Raw Chicken", BazaarCategory.FARMING),
        RAW_BEEF("Raw Beef", BazaarCategory.FARMING),
        RAW_RABBIT("Raw Rabbit", BazaarCategory.FARMING),
        RAW_MUTTON("Raw Mutton", BazaarCategory.FARMING),
        WHEAT("Wheat", BazaarCategory.FARMING),
        CARROT("Carrot", BazaarCategory.FARMING),
        POTATO("Potato", BazaarCategory.FARMING),
        SUGAR_CANE("Sugar Cane", BazaarCategory.FARMING),
        PUMPKIN("Pumpkin", BazaarCategory.FARMING),
        MELON("Melon Slice", BazaarCategory.FARMING),
        COCOA_BEANS("Cocoa Beans", BazaarCategory.FARMING),
        NETHER_WART("Nether Wart", BazaarCategory.FARMING),
        RED_MUSHROOM("Red Mushroom", BazaarCategory.FARMING),
        BROWN_MUSHROOM("Brown Mushroom", BazaarCategory.FARMING),
        CACTUS("Cactus", BazaarCategory.FARMING),
        SUGAR("Sugar", BazaarCategory.FARMING),
        ENCHANTED_BREAD("Enchanted Bread", BazaarCategory.FARMING),
        // MINING
        COBBLESTONE("Cobblestone", BazaarCategory.MINING),
        COAL("Coal", BazaarCategory.MINING),
        IRON_INGOT("Iron Ingot", BazaarCategory.MINING),
        GOLD_INGOT("Gold Ingot", BazaarCategory.MINING),
        DIAMOND("Diamond", BazaarCategory.MINING),
        LAPIS_LAZULI("Lapis Lazuli", BazaarCategory.MINING),
        EMERALD("Emerald", BazaarCategory.MINING),
        REDSTONE("Redstone", BazaarCategory.MINING),
        QUARTZ("Nether Quartz", BazaarCategory.MINING),
        OBSIDIAN("Obsidian", BazaarCategory.MINING),
        GLOWSTONE_DUST("Glowstone Dust", BazaarCategory.MINING),
        GRAVEL("Gravel", BazaarCategory.MINING),
        FLINT("Flint", BazaarCategory.MINING),
        ICE("Ice", BazaarCategory.MINING),
        NETHERRACK("Netherrack", BazaarCategory.MINING),
        END_STONE("End Stone", BazaarCategory.MINING),
        MITHRIL_ORE("Mithril Ore", BazaarCategory.MINING),
        // COMBAT
        ROTTEN_FLESH("Rotten Flesh", BazaarCategory.COMBAT),
        BONE("Bone", BazaarCategory.COMBAT),
        STRING("String", BazaarCategory.COMBAT),
        SPIDER_EYE("Spider Eye", BazaarCategory.COMBAT),
        GUNPOWDER("Gunpowder", BazaarCategory.COMBAT),
        BLAZE_ROD("Blaze Rod", BazaarCategory.COMBAT),
        GHAST_TEAR("Ghast Tear", BazaarCategory.COMBAT),
        MAGMA_CREAM("Magma Cream", BazaarCategory.COMBAT),
        SLIME_BALL("Slimeball", BazaarCategory.COMBAT),
        ENDER_PEARL("Ender Pearl", BazaarCategory.COMBAT),
        ENDER_EYE("Eye of Ender", BazaarCategory.COMBAT),
        // FORAGING
        OAK_LOG("Oak Wood", BazaarCategory.FORAGING),
        BIRCH_LOG("Birch Wood", BazaarCategory.FORAGING),
        SPRUCE_LOG("Spruce Wood", BazaarCategory.FORAGING),
        DARK_OAK_LOG("Dark Oak Wood", BazaarCategory.FORAGING),
        ACACIA_LOG("Acacia Wood", BazaarCategory.FORAGING),
        JUNGLE_LOG("Jungle Wood", BazaarCategory.FORAGING),
        MANGROVE_LOG("Mangrove Wood", BazaarCategory.FORAGING),
        // FISHING
        COD("Raw Fish", BazaarCategory.FISHING),
        SALMON("Raw Salmon", BazaarCategory.FISHING),
        PUFFERFISH("Pufferfish", BazaarCategory.FISHING),
        TROPICAL_FISH("Tropical Fish", BazaarCategory.FISHING),
        PRISMARINE_SHARD("Prismarine Shard", BazaarCategory.FISHING),
        PRISMARINE_CRYSTALS("Prismarine Crystals", BazaarCategory.FISHING),
        INK_SAC("Ink Sac", BazaarCategory.FISHING),
        // MISC
        PAPER("Paper", BazaarCategory.MISC),
        GLASS("Glass", BazaarCategory.MISC),
        SAND("Sand", BazaarCategory.MISC),
        SOUL_SAND("Soul Sand", BazaarCategory.MISC),
        LEATHER("Leather", BazaarCategory.MISC),
        FEATHER("Feather", BazaarCategory.MISC);

        private final String          displayName;
        private final BazaarCategory  category;

        BazaarProduct(String displayName, BazaarCategory category) {
            this.displayName = displayName;
            this.category    = category;
        }

        public String         getDisplayName() { return displayName; }
        public BazaarCategory getCategory()    { return category; }
        public String         getItemId()      { return name(); }
    }

    public enum FeeTier {
        BASE(0.0125),
        TIER_1(0.0110),
        TIER_2(0.0090),
        TIER_3(0.0060),
        TIER_4(0.0030),
        TIER_5(0.0010);

        private final double rate;

        FeeTier(double rate) { this.rate = rate; }

        public double getRate() { return rate; }
    }

    public static final class FillResult {
        private final int    quantityFilled;
        private final int    quantityRemaining;
        private final int    ordersMatched;
        private final double totalCoins;

        FillResult(int quantityFilled, int quantityRemaining, int ordersMatched, double totalCoins) {
            this.quantityFilled    = quantityFilled;
            this.quantityRemaining = quantityRemaining;
            this.ordersMatched     = ordersMatched;
            this.totalCoins        = totalCoins;
        }

        public boolean isFullyFilled()     { return quantityRemaining == 0; }
        public int     quantityFilled()    { return quantityFilled; }
        public int     quantityRemaining() { return quantityRemaining; }
        public int     ordersMatched()     { return ordersMatched; }
        public double  totalCoins()        { return totalCoins; }
    }

    public static class BazaarOrder {
        private final UUID   id;
        private final UUID   owner;
        private       int    quantity;
        private final double priceEach;

        BazaarOrder(UUID owner, int quantity, double priceEach) {
            this.id        = UUID.randomUUID();
            this.owner     = owner;
            this.quantity  = quantity;
            this.priceEach = priceEach;
        }

        public UUID   id()        { return id; }
        public UUID   owner()     { return owner; }
        public int    quantity()  { return quantity; }
        public double priceEach() { return priceEach; }
    }

    /** @deprecated Use {@link BazaarOrder} */
    @Deprecated
    public static final class Order extends BazaarOrder {
        Order(UUID owner, int quantity, double priceEach) {
            super(owner, quantity, priceEach);
        }
    }

    /** Immutable product catalogue: item-id → BazaarProduct. */
    public static final Map<String, BazaarProduct> PRODUCT_DATA;
    static {
        Map<String, BazaarProduct> map = new LinkedHashMap<>();
        for (BazaarProduct p : BazaarProduct.values()) map.put(p.getItemId(), p);
        PRODUCT_DATA = Collections.unmodifiableMap(map);
    }

    private static final BazaarManager INSTANCE = new BazaarManager();

    public static BazaarManager getInstance() { return INSTANCE; }

    /**
     * Live order book: per item, sell orders keyed by price ascending (cheapest ask first).
     * Each price level holds a FIFO deque of resting orders.
     */
    private final Map<String, TreeMap<Double, Deque<BazaarOrder>>> sellOrders = new HashMap<>();

    /**
     * Live order book: per item, buy orders keyed by price descending (highest bid first).
     * Each price level holds a FIFO deque of resting orders.
     */
    private final Map<String, TreeMap<Double, Deque<BazaarOrder>>> buyOrders  = new HashMap<>();

    private final Map<UUID, Double>               claimableCoins = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> claimableItems = new HashMap<>();
    private final Map<UUID, FeeTier>              playerFeeTiers  = new HashMap<>();

    private BazaarManager() {}

    public void load(java.io.File dataFolder) {}

    public void save(java.io.File dataFolder) {}

    public void clear() {
        sellOrders.clear();
        buyOrders.clear();
        claimableCoins.clear();
        claimableItems.clear();
        playerFeeTiers.clear();
    }

    // ---- Order book accessors ----

    public int getSellOrderCount(String item) {
        TreeMap<Double, Deque<BazaarOrder>> book = sellOrders.get(item);
        if (book == null) return 0;
        int count = 0;
        for (Deque<BazaarOrder> d : book.values()) count += d.size();
        return count;
    }

    public int getBuyOrderCount(String item) {
        TreeMap<Double, Deque<BazaarOrder>> book = buyOrders.get(item);
        if (book == null) return 0;
        int count = 0;
        for (Deque<BazaarOrder> d : book.values()) count += d.size();
        return count;
    }

    /** Returns sell orders flattened in ascending price order (cheapest first). */
    public List<BazaarOrder> getSellOrders(String item) {
        TreeMap<Double, Deque<BazaarOrder>> book = sellOrders.get(item);
        if (book == null) return Collections.emptyList();
        List<BazaarOrder> result = new ArrayList<>();
        for (Deque<BazaarOrder> d : book.values()) result.addAll(d);
        return Collections.unmodifiableList(result);
    }

    /** Returns buy orders flattened in descending price order (highest bid first). */
    public List<BazaarOrder> getBuyOrders(String item) {
        TreeMap<Double, Deque<BazaarOrder>> book = buyOrders.get(item);
        if (book == null) return Collections.emptyList();
        List<BazaarOrder> result = new ArrayList<>();
        for (Deque<BazaarOrder> d : book.values()) result.addAll(d);
        return Collections.unmodifiableList(result);
    }

    public double getLowestAsk(String item) {
        TreeMap<Double, Deque<BazaarOrder>> book = sellOrders.get(item);
        return (book == null || book.isEmpty()) ? Double.MAX_VALUE : book.firstKey();
    }

    public double getHighestBid(String item) {
        TreeMap<Double, Deque<BazaarOrder>> book = buyOrders.get(item);
        return (book == null || book.isEmpty()) ? 0.0 : book.firstKey();
    }

    /**
     * Cancels a standing order owned by {@code player}.
     *
     * @param player  the owner's UUID
     * @param isBuy   true to search buy orders, false for sell orders
     * @param orderId the {@link BazaarOrder#id()} to remove
     * @return true if found and removed, false if not found or not owned by player
     */
    public boolean cancelOrder(UUID player, boolean isBuy, UUID orderId) {
        Map<String, TreeMap<Double, Deque<BazaarOrder>>> book = isBuy ? buyOrders : sellOrders;
        for (Map.Entry<String, TreeMap<Double, Deque<BazaarOrder>>> itemEntry : book.entrySet()) {
            TreeMap<Double, Deque<BazaarOrder>> priceBook = itemEntry.getValue();
            for (Map.Entry<Double, Deque<BazaarOrder>> priceEntry : priceBook.entrySet()) {
                Deque<BazaarOrder> deque = priceEntry.getValue();
                Iterator<BazaarOrder> it = deque.iterator();
                while (it.hasNext()) {
                    BazaarOrder o = it.next();
                    if (o.id().equals(orderId) && o.owner().equals(player)) {
                        it.remove();
                        if (deque.isEmpty()) priceBook.remove(priceEntry.getKey());
                        if (priceBook.isEmpty()) book.remove(itemEntry.getKey());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // ---- Base (NPC reference) prices + menu display prices ----

    /** Instant-sell is this fraction of the buy price when there's no live order book (the spread). */
    private static final double SELL_SPREAD = 0.9;

    /** Reference buy price (coins each) per product, used when the live order book has no liquidity. */
    private static final Map<BazaarProduct, Double> BASE_PRICE = new EnumMap<>(BazaarProduct.class);

    static {
        // FARMING
        BASE_PRICE.put(BazaarProduct.RAW_PORKCHOP, 6.0);   BASE_PRICE.put(BazaarProduct.RAW_CHICKEN, 6.0);
        BASE_PRICE.put(BazaarProduct.RAW_BEEF, 7.0);       BASE_PRICE.put(BazaarProduct.RAW_RABBIT, 8.0);
        BASE_PRICE.put(BazaarProduct.RAW_MUTTON, 7.0);     BASE_PRICE.put(BazaarProduct.WHEAT, 6.0);
        BASE_PRICE.put(BazaarProduct.CARROT, 3.0);         BASE_PRICE.put(BazaarProduct.POTATO, 3.0);
        BASE_PRICE.put(BazaarProduct.SUGAR_CANE, 4.0);     BASE_PRICE.put(BazaarProduct.PUMPKIN, 8.0);
        BASE_PRICE.put(BazaarProduct.MELON, 2.0);          BASE_PRICE.put(BazaarProduct.COCOA_BEANS, 4.0);
        BASE_PRICE.put(BazaarProduct.NETHER_WART, 5.0);    BASE_PRICE.put(BazaarProduct.RED_MUSHROOM, 5.0);
        BASE_PRICE.put(BazaarProduct.BROWN_MUSHROOM, 5.0); BASE_PRICE.put(BazaarProduct.CACTUS, 4.0);
        BASE_PRICE.put(BazaarProduct.SUGAR, 5.0);          BASE_PRICE.put(BazaarProduct.ENCHANTED_BREAD, 960.0);
        // MINING
        BASE_PRICE.put(BazaarProduct.COBBLESTONE, 3.0);    BASE_PRICE.put(BazaarProduct.COAL, 4.0);
        BASE_PRICE.put(BazaarProduct.IRON_INGOT, 6.0);     BASE_PRICE.put(BazaarProduct.GOLD_INGOT, 6.0);
        BASE_PRICE.put(BazaarProduct.DIAMOND, 10.0);       BASE_PRICE.put(BazaarProduct.LAPIS_LAZULI, 8.0);
        BASE_PRICE.put(BazaarProduct.EMERALD, 8.0);        BASE_PRICE.put(BazaarProduct.REDSTONE, 3.0);
        BASE_PRICE.put(BazaarProduct.QUARTZ, 5.0);         BASE_PRICE.put(BazaarProduct.OBSIDIAN, 10.0);
        BASE_PRICE.put(BazaarProduct.GLOWSTONE_DUST, 4.0); BASE_PRICE.put(BazaarProduct.GRAVEL, 3.0);
        BASE_PRICE.put(BazaarProduct.FLINT, 4.0);          BASE_PRICE.put(BazaarProduct.ICE, 2.0);
        BASE_PRICE.put(BazaarProduct.NETHERRACK, 1.0);     BASE_PRICE.put(BazaarProduct.END_STONE, 3.0);
        BASE_PRICE.put(BazaarProduct.MITHRIL_ORE, 35.0);
        // COMBAT
        BASE_PRICE.put(BazaarProduct.ROTTEN_FLESH, 3.0);   BASE_PRICE.put(BazaarProduct.BONE, 5.0);
        BASE_PRICE.put(BazaarProduct.STRING, 4.0);         BASE_PRICE.put(BazaarProduct.SPIDER_EYE, 4.0);
        BASE_PRICE.put(BazaarProduct.GUNPOWDER, 6.0);      BASE_PRICE.put(BazaarProduct.BLAZE_ROD, 16.0);
        BASE_PRICE.put(BazaarProduct.GHAST_TEAR, 25.0);    BASE_PRICE.put(BazaarProduct.MAGMA_CREAM, 12.0);
        BASE_PRICE.put(BazaarProduct.SLIME_BALL, 6.0);     BASE_PRICE.put(BazaarProduct.ENDER_PEARL, 12.0);
        BASE_PRICE.put(BazaarProduct.ENDER_EYE, 18.0);
        // FORAGING
        BASE_PRICE.put(BazaarProduct.OAK_LOG, 5.0);        BASE_PRICE.put(BazaarProduct.BIRCH_LOG, 5.0);
        BASE_PRICE.put(BazaarProduct.SPRUCE_LOG, 5.0);     BASE_PRICE.put(BazaarProduct.DARK_OAK_LOG, 6.0);
        BASE_PRICE.put(BazaarProduct.ACACIA_LOG, 6.0);     BASE_PRICE.put(BazaarProduct.JUNGLE_LOG, 6.0);
        BASE_PRICE.put(BazaarProduct.MANGROVE_LOG, 6.0);
        // FISHING
        BASE_PRICE.put(BazaarProduct.COD, 12.0);           BASE_PRICE.put(BazaarProduct.SALMON, 14.0);
        BASE_PRICE.put(BazaarProduct.PUFFERFISH, 16.0);    BASE_PRICE.put(BazaarProduct.TROPICAL_FISH, 20.0);
        BASE_PRICE.put(BazaarProduct.PRISMARINE_SHARD, 8.0); BASE_PRICE.put(BazaarProduct.PRISMARINE_CRYSTALS, 10.0);
        BASE_PRICE.put(BazaarProduct.INK_SAC, 4.0);
        // MISC
        BASE_PRICE.put(BazaarProduct.PAPER, 2.0);          BASE_PRICE.put(BazaarProduct.GLASS, 3.0);
        BASE_PRICE.put(BazaarProduct.SAND, 2.0);           BASE_PRICE.put(BazaarProduct.SOUL_SAND, 6.0);
        BASE_PRICE.put(BazaarProduct.LEATHER, 6.0);        BASE_PRICE.put(BazaarProduct.FEATHER, 4.0);
    }

    /** The reference buy price (coins) for a product when there's no live order book. */
    public double getBasePrice(BazaarProduct product) {
        return BASE_PRICE.getOrDefault(product, 1.0);
    }

    /** Instant-buy price: the lowest live ask, or the base price if the book has no sellers. */
    public double getDisplayBuyPrice(BazaarProduct product) {
        double ask = getLowestAsk(product.getItemId());
        return ask >= Double.MAX_VALUE / 2 ? getBasePrice(product) : ask;
    }

    /** Instant-sell price: the highest live bid, or base × spread if the book has no buyers. */
    public double getDisplaySellPrice(BazaarProduct product) {
        double bid = getHighestBid(product.getItemId());
        return bid > 0.0 ? bid : getBasePrice(product) * SELL_SPREAD;
    }

    // ---- Fee management ----

    public double computeFee(double gross) {
        return gross * FeeTier.BASE.rate;
    }

    public double computeFee(double gross, FeeTier tier) {
        return gross * tier.rate;
    }

    public FeeTier getFeeTier(UUID player) {
        return playerFeeTiers.getOrDefault(player, FeeTier.BASE);
    }

    public void setFeeTier(UUID player, FeeTier tier) {
        playerFeeTiers.put(player, tier);
    }

    // ---- Claimable escrow ----

    public double getClaimableCoins(UUID player) {
        return claimableCoins.getOrDefault(player, 0.0);
    }

    public double claimCoins(UUID player) {
        double amount = claimableCoins.getOrDefault(player, 0.0);
        claimableCoins.put(player, 0.0);
        return amount;
    }

    public int getClaimableItems(UUID player, String item) {
        Map<String, Integer> map = claimableItems.get(player);
        return map == null ? 0 : map.getOrDefault(item, 0);
    }

    public int claimItems(UUID player, String item) {
        Map<String, Integer> map = claimableItems.get(player);
        if (map == null) return 0;
        int amount = map.getOrDefault(item, 0);
        map.put(item, 0);
        return amount;
    }

    private void creditCoins(UUID owner, double gross, FeeTier tier) {
        claimableCoins.merge(owner, gross - computeFee(gross, tier), Double::sum);
    }

    private void creditItems(UUID buyer, String item, int qty) {
        claimableItems.computeIfAbsent(buyer, k -> new HashMap<>())
                      .merge(item, qty, Integer::sum);
    }

    // ---- Order placement with limit-order matching ----

    public void addSellOrder(UUID seller, String item, int quantity, double price) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        int remaining = quantity;
        TreeMap<Double, Deque<BazaarOrder>> bids = buyOrders.get(item);
        if (bids != null) {
            // Match against resting bids (highest first); execute at resting bid price.
            while (remaining > 0 && !bids.isEmpty() && bids.firstKey() >= price) {
                Deque<BazaarOrder> topDeque = bids.firstEntry().getValue();
                BazaarOrder top  = topDeque.peekFirst();
                int         fill = Math.min(remaining, top.quantity);
                creditCoins(seller, fill * top.priceEach, getFeeTier(seller));
                creditItems(top.owner, item, fill);
                top.quantity -= fill;
                remaining    -= fill;
                if (top.quantity == 0) {
                    topDeque.pollFirst();
                    if (topDeque.isEmpty()) bids.pollFirstEntry();
                }
            }
            if (bids.isEmpty()) buyOrders.remove(item);
        }
        if (remaining > 0) insertSell(item, new BazaarOrder(seller, remaining, price));
    }

    public void addBuyOrder(UUID buyer, String item, int quantity, double price) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        int remaining = quantity;
        TreeMap<Double, Deque<BazaarOrder>> asks = sellOrders.get(item);
        if (asks != null) {
            // Match against resting asks (cheapest first); execute at resting ask price.
            while (remaining > 0 && !asks.isEmpty() && asks.firstKey() <= price) {
                Deque<BazaarOrder> topDeque = asks.firstEntry().getValue();
                BazaarOrder top  = topDeque.peekFirst();
                int         fill = Math.min(remaining, top.quantity);
                creditCoins(top.owner, fill * top.priceEach, getFeeTier(top.owner));
                creditItems(buyer, item, fill);
                top.quantity -= fill;
                remaining    -= fill;
                if (top.quantity == 0) {
                    topDeque.pollFirst();
                    if (topDeque.isEmpty()) asks.pollFirstEntry();
                }
            }
            if (asks.isEmpty()) sellOrders.remove(item);
        }
        if (remaining > 0) insertBuy(item, new BazaarOrder(buyer, remaining, price));
    }

    // ---- Instant (taker) operations ----

    public FillResult instantBuy(UUID buyer, String item, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        TreeMap<Double, Deque<BazaarOrder>> asks = sellOrders.get(item);
        int    filled = 0, remaining = quantity, matched = 0;
        double coins  = 0.0;
        if (asks != null) {
            while (remaining > 0 && !asks.isEmpty()) {
                Deque<BazaarOrder> topDeque = asks.firstEntry().getValue();
                BazaarOrder ask  = topDeque.peekFirst();
                int         fill = Math.min(remaining, ask.quantity);
                double      gross = fill * ask.priceEach;
                creditCoins(ask.owner, gross, getFeeTier(ask.owner));
                coins        += gross;
                filled       += fill;
                remaining    -= fill;
                ask.quantity -= fill;
                matched++;
                if (ask.quantity == 0) {
                    topDeque.pollFirst();
                    if (topDeque.isEmpty()) asks.pollFirstEntry();
                }
            }
            if (asks.isEmpty()) sellOrders.remove(item);
        }
        return new FillResult(filled, remaining, matched, coins);
    }

    public FillResult instantSell(UUID seller, String item, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        TreeMap<Double, Deque<BazaarOrder>> bids = buyOrders.get(item);
        int    filled = 0, remaining = quantity, matched = 0;
        double coins  = 0.0;
        if (bids != null) {
            while (remaining > 0 && !bids.isEmpty()) {
                Deque<BazaarOrder> topDeque = bids.firstEntry().getValue();
                BazaarOrder bid  = topDeque.peekFirst();
                int         fill = Math.min(remaining, bid.quantity);
                coins        += fill * bid.priceEach;
                filled       += fill;
                remaining    -= fill;
                bid.quantity -= fill;
                matched++;
                if (bid.quantity == 0) {
                    topDeque.pollFirst();
                    if (topDeque.isEmpty()) bids.pollFirstEntry();
                }
            }
            if (bids.isEmpty()) buyOrders.remove(item);
        }
        return new FillResult(filled, remaining, matched, coins);
    }

    // ---- Price-level insertion helpers ----

    private void insertSell(String item, BazaarOrder order) {
        sellOrders.computeIfAbsent(item, k -> new TreeMap<>())
                  .computeIfAbsent(order.priceEach, k -> new ArrayDeque<>())
                  .addLast(order);
    }

    private void insertBuy(String item, BazaarOrder order) {
        buyOrders.computeIfAbsent(item, k -> new TreeMap<>(Comparator.reverseOrder()))
                 .computeIfAbsent(order.priceEach, k -> new ArrayDeque<>())
                 .addLast(order);
    }
}
