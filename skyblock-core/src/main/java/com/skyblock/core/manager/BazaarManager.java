package com.skyblock.core.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class BazaarManager {

    public enum BazaarProduct {
        // FARMING
        RAW_PORKCHOP("Raw Porkchop", "FARMING"),
        RAW_CHICKEN("Raw Chicken", "FARMING"),
        RAW_BEEF("Raw Beef", "FARMING"),
        RAW_RABBIT("Raw Rabbit", "FARMING"),
        RAW_MUTTON("Raw Mutton", "FARMING"),
        WHEAT("Wheat", "FARMING"),
        CARROT("Carrot", "FARMING"),
        POTATO("Potato", "FARMING"),
        SUGAR_CANE("Sugar Cane", "FARMING"),
        PUMPKIN("Pumpkin", "FARMING"),
        MELON("Melon Slice", "FARMING"),
        COCOA_BEANS("Cocoa Beans", "FARMING"),
        NETHER_WART("Nether Wart", "FARMING"),
        RED_MUSHROOM("Red Mushroom", "FARMING"),
        BROWN_MUSHROOM("Brown Mushroom", "FARMING"),
        CACTUS("Cactus", "FARMING"),
        SUGAR("Sugar", "FARMING"),
        ENCHANTED_BREAD("Enchanted Bread", "FARMING"),
        // MINING
        COBBLESTONE("Cobblestone", "MINING"),
        COAL("Coal", "MINING"),
        IRON_INGOT("Iron Ingot", "MINING"),
        GOLD_INGOT("Gold Ingot", "MINING"),
        DIAMOND("Diamond", "MINING"),
        LAPIS_LAZULI("Lapis Lazuli", "MINING"),
        EMERALD("Emerald", "MINING"),
        REDSTONE("Redstone", "MINING"),
        QUARTZ("Nether Quartz", "MINING"),
        OBSIDIAN("Obsidian", "MINING"),
        GLOWSTONE_DUST("Glowstone Dust", "MINING"),
        GRAVEL("Gravel", "MINING"),
        FLINT("Flint", "MINING"),
        ICE("Ice", "MINING"),
        NETHERRACK("Netherrack", "MINING"),
        END_STONE("End Stone", "MINING"),
        MITHRIL_ORE("Mithril Ore", "MINING"),
        // COMBAT
        ROTTEN_FLESH("Rotten Flesh", "COMBAT"),
        BONE("Bone", "COMBAT"),
        STRING("String", "COMBAT"),
        SPIDER_EYE("Spider Eye", "COMBAT"),
        GUNPOWDER("Gunpowder", "COMBAT"),
        BLAZE_ROD("Blaze Rod", "COMBAT"),
        GHAST_TEAR("Ghast Tear", "COMBAT"),
        MAGMA_CREAM("Magma Cream", "COMBAT"),
        SLIME_BALL("Slimeball", "COMBAT"),
        ENDER_PEARL("Ender Pearl", "COMBAT"),
        ENDER_EYE("Eye of Ender", "COMBAT"),
        // FORAGING
        OAK_LOG("Oak Wood", "FORAGING"),
        BIRCH_LOG("Birch Wood", "FORAGING"),
        SPRUCE_LOG("Spruce Wood", "FORAGING"),
        DARK_OAK_LOG("Dark Oak Wood", "FORAGING"),
        ACACIA_LOG("Acacia Wood", "FORAGING"),
        JUNGLE_LOG("Jungle Wood", "FORAGING"),
        MANGROVE_LOG("Mangrove Wood", "FORAGING"),
        // FISHING
        COD("Raw Fish", "FISHING"),
        SALMON("Raw Salmon", "FISHING"),
        PUFFERFISH("Pufferfish", "FISHING"),
        TROPICAL_FISH("Tropical Fish", "FISHING"),
        PRISMARINE_SHARD("Prismarine Shard", "FISHING"),
        PRISMARINE_CRYSTALS("Prismarine Crystals", "FISHING"),
        INK_SAC("Ink Sac", "FISHING"),
        // MISC
        PAPER("Paper", "MISC"),
        GLASS("Glass", "MISC"),
        SAND("Sand", "MISC"),
        SOUL_SAND("Soul Sand", "MISC"),
        LEATHER("Leather", "MISC"),
        FEATHER("Feather", "MISC");

        private final String displayName;
        private final String category;

        BazaarProduct(String displayName, String category) {
            this.displayName = displayName;
            this.category = category;
        }

        public String getDisplayName() { return displayName; }
        public String getCategory()    { return category; }
        public String getItemId()      { return name(); }
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

    public static final class Order {
        private final UUID   owner;
        private       int    quantity;
        private final double priceEach;

        Order(UUID owner, int quantity, double priceEach) {
            this.owner     = owner;
            this.quantity  = quantity;
            this.priceEach = priceEach;
        }

        public UUID   owner()     { return owner; }
        public int    quantity()  { return quantity; }
        public double priceEach() { return priceEach; }
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

    /** Sell orders per item, sorted ascending by price (cheapest first). */
    private final Map<String, List<Order>> sellOrders = new HashMap<>();
    /** Buy orders per item, sorted descending by price (highest first). */
    private final Map<String, List<Order>> buyOrders  = new HashMap<>();

    private final Map<UUID, Double>               claimableCoins = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> claimableItems = new HashMap<>();
    private final Map<UUID, FeeTier>              playerFeeTiers  = new HashMap<>();

    private BazaarManager() {}

    public void load(java.io.File dataFolder) {}

    public void save(java.io.File dataFolder) {}

    // ---- Order book accessors ----

    public int getSellOrderCount(String item) {
        List<Order> list = sellOrders.get(item);
        return list == null ? 0 : list.size();
    }

    public int getBuyOrderCount(String item) {
        List<Order> list = buyOrders.get(item);
        return list == null ? 0 : list.size();
    }

    public List<Order> getSellOrders(String item) {
        List<Order> list = sellOrders.get(item);
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
    }

    public List<Order> getBuyOrders(String item) {
        List<Order> list = buyOrders.get(item);
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
    }

    public double getLowestAsk(String item) {
        List<Order> list = sellOrders.get(item);
        return (list == null || list.isEmpty()) ? Double.MAX_VALUE : list.get(0).priceEach;
    }

    public double getHighestBid(String item) {
        List<Order> list = buyOrders.get(item);
        return (list == null || list.isEmpty()) ? 0.0 : list.get(0).priceEach;
    }

    // ---- Menu display prices ----

    public double getDisplayBuyPrice(BazaarProduct product)  { return getLowestAsk(product.getItemId()); }
    public double getDisplaySellPrice(BazaarProduct product) { return getHighestBid(product.getItemId()); }

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
        List<Order> bids = buyOrders.get(item);
        if (bids != null) {
            // Match against resting bids (highest first); execute at resting bid price.
            while (remaining > 0 && !bids.isEmpty() && bids.get(0).priceEach >= price) {
                Order top  = bids.get(0);
                int   fill = Math.min(remaining, top.quantity);
                creditCoins(seller, fill * top.priceEach, getFeeTier(seller));
                creditItems(top.owner, item, fill);
                top.quantity -= fill;
                remaining    -= fill;
                if (top.quantity == 0) bids.remove(0);
            }
            if (bids.isEmpty()) buyOrders.remove(item);
        }
        if (remaining > 0) insertSell(item, new Order(seller, remaining, price));
    }

    public void addBuyOrder(UUID buyer, String item, int quantity, double price) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        int remaining = quantity;
        List<Order> asks = sellOrders.get(item);
        if (asks != null) {
            // Match against resting asks (cheapest first); execute at resting ask price.
            while (remaining > 0 && !asks.isEmpty() && asks.get(0).priceEach <= price) {
                Order top  = asks.get(0);
                int   fill = Math.min(remaining, top.quantity);
                creditCoins(top.owner, fill * top.priceEach, getFeeTier(top.owner));
                creditItems(buyer, item, fill);
                top.quantity -= fill;
                remaining    -= fill;
                if (top.quantity == 0) asks.remove(0);
            }
            if (asks.isEmpty()) sellOrders.remove(item);
        }
        if (remaining > 0) insertBuy(item, new Order(buyer, remaining, price));
    }

    // ---- Instant (taker) operations ----

    public FillResult instantBuy(UUID buyer, String item, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        List<Order> asks = sellOrders.get(item);
        int    filled = 0, remaining = quantity, matched = 0;
        double coins  = 0.0;
        if (asks != null) {
            while (remaining > 0 && !asks.isEmpty()) {
                Order ask  = asks.get(0);
                int   fill = Math.min(remaining, ask.quantity);
                double gross = fill * ask.priceEach;
                creditCoins(ask.owner, gross, getFeeTier(ask.owner));
                coins        += gross;
                filled       += fill;
                remaining    -= fill;
                ask.quantity -= fill;
                matched++;
                if (ask.quantity == 0) asks.remove(0);
            }
            if (asks.isEmpty()) sellOrders.remove(item);
        }
        return new FillResult(filled, remaining, matched, coins);
    }

    public FillResult instantSell(UUID seller, String item, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        List<Order> bids = buyOrders.get(item);
        int    filled = 0, remaining = quantity, matched = 0;
        double coins  = 0.0;
        if (bids != null) {
            while (remaining > 0 && !bids.isEmpty()) {
                Order bid  = bids.get(0);
                int   fill = Math.min(remaining, bid.quantity);
                coins        += fill * bid.priceEach;
                filled       += fill;
                remaining    -= fill;
                bid.quantity -= fill;
                matched++;
                if (bid.quantity == 0) bids.remove(0);
            }
            if (bids.isEmpty()) buyOrders.remove(item);
        }
        return new FillResult(filled, remaining, matched, coins);
    }

    // ---- Sorted insertion helpers ----

    private void insertSell(String item, Order order) {
        List<Order> asks = sellOrders.computeIfAbsent(item, k -> new ArrayList<>());
        int lo = 0, hi = asks.size();
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (asks.get(mid).priceEach < order.priceEach) lo = mid + 1;
            else hi = mid;
        }
        asks.add(lo, order);
    }

    private void insertBuy(String item, Order order) {
        List<Order> bids = buyOrders.computeIfAbsent(item, k -> new ArrayList<>());
        int lo = 0, hi = bids.size();
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (bids.get(mid).priceEach > order.priceEach) lo = mid + 1;
            else hi = mid;
        }
        bids.add(lo, order);
    }
}
