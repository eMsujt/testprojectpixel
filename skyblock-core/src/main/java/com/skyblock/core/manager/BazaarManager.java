package com.skyblock.core.manager;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Canonical singleton managing the bazaar order book.
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

        public String getItemId() { return itemId; }
        public String getDisplayName() { return displayName; }
        public String getCategory() { return category; }

        public static BazaarProduct fromItemId(String itemId) {
            if (itemId == null) return null;
            for (BazaarProduct p : values()) {
                if (p.itemId.equalsIgnoreCase(itemId)) return p;
            }
            return null;
        }
    }

    /**
     * Bazaar sell-tax tiers. The base rate is charged on coin proceeds; community/perk upgrades
     * progressively reduce it. Items are never taxed — only coins claimed from filled sell orders.
     */
    public enum FeeTier {
        BASE("Base", 0.0125),
        TIER_1("Bazaar Flipper I", 0.0100),
        TIER_2("Bazaar Flipper II", 0.0075),
        TIER_3("Bazaar Flipper III", 0.0050),
        TIER_4("Bazaar Flipper IV", 0.0025),
        TIER_5("Bazaar Flipper V", 0.0010);

        private final String displayName;
        private final double rate;

        FeeTier(String displayName, double rate) {
            this.displayName = displayName;
            this.rate = rate;
        }

        public String getDisplayName() { return displayName; }
        public double getRate() { return rate; }
    }

    public enum BazaarOrderType {
        BUY("Buy Order"),
        SELL("Sell Order");

        private final String displayName;

        BazaarOrderType(String displayName) { this.displayName = displayName; }

        public String getDisplayName() { return displayName; }
    }

    public record BazaarOrder(UUID id, UUID player, String itemId, int quantity, double priceEach, BazaarOrderType type) {
        public BazaarOrder {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(player, "player");
            Objects.requireNonNull(itemId, "itemId");
            Objects.requireNonNull(type, "type");
            if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive: " + quantity);
            if (priceEach <= 0) throw new IllegalArgumentException("priceEach must be positive: " + priceEach);
        }
    }

    public record BuyOrder(UUID id, UUID buyer, String itemId, int quantity, double priceEach) {
        public BuyOrder {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(buyer, "buyer");
            Objects.requireNonNull(itemId, "itemId");
            if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive: " + quantity);
            if (priceEach <= 0) throw new IllegalArgumentException("priceEach must be positive: " + priceEach);
        }
    }

    public record SellOrder(UUID id, UUID seller, String itemId, int quantity, double priceEach) {
        public SellOrder {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(seller, "seller");
            Objects.requireNonNull(itemId, "itemId");
            if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive: " + quantity);
            if (priceEach <= 0) throw new IllegalArgumentException("priceEach must be positive: " + priceEach);
        }
    }

    /** Outcome of an instant transaction or a crossed order: how much filled and at what total. */
    public record FillResult(int quantityFilled, int quantityRemaining, double totalCoins, int ordersMatched) {
        public boolean isFullyFilled() { return quantityRemaining == 0; }
    }

    /** Reference market data: {@code {instantBuyPrice, sellOfferPrice}} in coins. */
    public static final Map<String, double[]> PRODUCT_DATA;

    static {
        Map<String, double[]> m = new LinkedHashMap<>();
        m.put("ENCHANTED_CARROT",         new double[]{  3_200,   3_000});
        m.put("ENCHANTED_POTATO",         new double[]{  2_800,   2_600});
        m.put("ENCHANTED_PUMPKIN",        new double[]{  5_500,   5_200});
        m.put("ENCHANTED_MELON",          new double[]{  1_800,   1_650});
        m.put("ENCHANTED_WHEAT",          new double[]{  4_000,   3_750});
        m.put("ENCHANTED_SUGAR_CANE",     new double[]{  2_500,   2_300});
        m.put("ENCHANTED_CACTUS",         new double[]{  3_600,   3_400});
        m.put("ENCHANTED_NETHER_WART",    new double[]{  6_000,   5_700});
        m.put("ENCHANTED_COCOA_BEANS",    new double[]{  1_400,   1_300});
        m.put("ENCHANTED_MUSHROOM",       new double[]{  9_500,   9_000});
        m.put("ENCHANTED_COBBLESTONE",    new double[]{    480,     450});
        m.put("ENCHANTED_COAL",           new double[]{  1_200,   1_100});
        m.put("ENCHANTED_IRON",           new double[]{  3_000,   2_800});
        m.put("ENCHANTED_GOLD",           new double[]{  8_000,   7_500});
        m.put("ENCHANTED_DIAMOND",        new double[]{ 45_000,  42_000});
        m.put("ENCHANTED_EMERALD",        new double[]{ 12_000,  11_200});
        m.put("ENCHANTED_REDSTONE",       new double[]{  2_200,   2_050});
        m.put("ENCHANTED_LAPIS",          new double[]{  1_700,   1_580});
        m.put("ENCHANTED_QUARTZ",         new double[]{  4_800,   4_500});
        m.put("ENCHANTED_OBSIDIAN",       new double[]{ 20_000,  18_500});
        m.put("ENCHANTED_OAK_LOG",        new double[]{  5_000,   4_700});
        m.put("ENCHANTED_SPRUCE_LOG",     new double[]{  4_800,   4_500});
        m.put("ENCHANTED_BIRCH_LOG",      new double[]{  4_600,   4_300});
        m.put("ENCHANTED_JUNGLE_LOG",     new double[]{  5_200,   4_900});
        m.put("ENCHANTED_ACACIA_LOG",     new double[]{  4_700,   4_400});
        m.put("ENCHANTED_DARK_OAK_LOG",   new double[]{  4_900,   4_600});
        m.put("ENCHANTED_ROTTEN_FLESH",   new double[]{    600,     560});
        m.put("ENCHANTED_BONE",           new double[]{  1_000,     940});
        m.put("ENCHANTED_SPIDER_EYE",     new double[]{  2_400,   2_250});
        m.put("ENCHANTED_STRING",         new double[]{  3_800,   3_600});
        m.put("ENCHANTED_GUNPOWDER",      new double[]{  2_000,   1_880});
        m.put("ENCHANTED_BLAZE_ROD",      new double[]{ 14_000,  13_000});
        m.put("ENCHANTED_GHAST_TEAR",     new double[]{ 18_000,  17_000});
        m.put("MITHRIL_ORE",              new double[]{    120,     110});
        m.put("TITANIUM_ORE",             new double[]{  2_500,   2_350});
        PRODUCT_DATA = Collections.unmodifiableMap(m);
    }

    /** Per-product order limits: {@code {maxOrderQuantity, maxActiveOrders}}. */
    public static final Map<String, int[]> BAZAAR_ITEM_DATA;

    static {
        Map<String, int[]> m = new LinkedHashMap<>();
        m.put("WHEAT",                    new int[]{71680, 16});
        m.put("CARROT_ITEM",              new int[]{71680, 16});
        m.put("POTATO_ITEM",              new int[]{71680, 16});
        m.put("PUMPKIN",                  new int[]{71680, 16});
        m.put("MELON",                    new int[]{71680, 16});
        m.put("SUGAR_CANE",               new int[]{71680, 16});
        m.put("CACTUS",                   new int[]{71680, 16});
        m.put("NETHER_STALK",             new int[]{71680, 16});
        m.put("COCOA_BEANS",              new int[]{71680, 16});
        m.put("MUSHROOM_COLLECTION",      new int[]{71680, 16});
        m.put("ENCHANTED_CARROT",         new int[]{ 2560, 16});
        m.put("ENCHANTED_POTATO",         new int[]{ 2560, 16});
        m.put("ENCHANTED_PUMPKIN",        new int[]{ 2560, 16});
        m.put("ENCHANTED_MELON",          new int[]{ 2560, 16});
        m.put("ENCHANTED_WHEAT",          new int[]{ 2560, 16});
        m.put("ENCHANTED_SUGAR_CANE",     new int[]{ 2560, 16});
        m.put("ENCHANTED_CACTUS",         new int[]{ 2560, 16});
        m.put("ENCHANTED_NETHER_WART",    new int[]{ 2560, 16});
        m.put("ENCHANTED_COCOA_BEANS",    new int[]{ 2560, 16});
        m.put("ENCHANTED_MUSHROOM",       new int[]{ 2560, 16});
        m.put("COBBLESTONE",              new int[]{71680, 16});
        m.put("COAL",                     new int[]{71680, 16});
        m.put("IRON_INGOT",               new int[]{71680, 16});
        m.put("GOLD_INGOT",               new int[]{71680, 16});
        m.put("DIAMOND",                  new int[]{71680, 16});
        m.put("EMERALD",                  new int[]{71680, 16});
        m.put("REDSTONE",                 new int[]{71680, 16});
        m.put("LAPIS_LAZULI",             new int[]{71680, 16});
        m.put("QUARTZ",                   new int[]{71680, 16});
        m.put("OBSIDIAN",                 new int[]{71680, 16});
        m.put("MITHRIL_ORE",              new int[]{71680, 16});
        m.put("TITANIUM_ORE",             new int[]{71680, 16});
        m.put("ENCHANTED_COBBLESTONE",    new int[]{ 2560, 16});
        m.put("ENCHANTED_COAL",           new int[]{ 2560, 16});
        m.put("ENCHANTED_IRON",           new int[]{ 2560, 16});
        m.put("ENCHANTED_GOLD",           new int[]{ 2560, 16});
        m.put("ENCHANTED_DIAMOND",        new int[]{ 2560, 16});
        m.put("ENCHANTED_EMERALD",        new int[]{ 2560, 16});
        m.put("ENCHANTED_REDSTONE",       new int[]{ 2560, 16});
        m.put("ENCHANTED_LAPIS",          new int[]{ 2560, 16});
        m.put("ENCHANTED_QUARTZ",         new int[]{ 2560, 16});
        m.put("ENCHANTED_OBSIDIAN",       new int[]{ 2560, 16});
        m.put("OAK_LOG",                  new int[]{71680, 16});
        m.put("SPRUCE_LOG",               new int[]{71680, 16});
        m.put("BIRCH_LOG",                new int[]{71680, 16});
        m.put("JUNGLE_LOG",               new int[]{71680, 16});
        m.put("ACACIA_LOG",               new int[]{71680, 16});
        m.put("DARK_OAK_LOG",             new int[]{71680, 16});
        m.put("ENCHANTED_OAK_LOG",        new int[]{ 2560, 16});
        m.put("ENCHANTED_SPRUCE_LOG",     new int[]{ 2560, 16});
        m.put("ENCHANTED_BIRCH_LOG",      new int[]{ 2560, 16});
        m.put("ENCHANTED_JUNGLE_LOG",     new int[]{ 2560, 16});
        m.put("ENCHANTED_ACACIA_LOG",     new int[]{ 2560, 16});
        m.put("ENCHANTED_DARK_OAK_LOG",   new int[]{ 2560, 16});
        m.put("ROTTEN_FLESH",             new int[]{71680, 16});
        m.put("BONE",                     new int[]{71680, 16});
        m.put("SPIDER_EYE",               new int[]{71680, 16});
        m.put("STRING",                   new int[]{71680, 16});
        m.put("GUNPOWDER",                new int[]{71680, 16});
        m.put("BLAZE_ROD",                new int[]{71680, 16});
        m.put("GHAST_TEAR",               new int[]{71680, 16});
        m.put("ENCHANTED_ROTTEN_FLESH",   new int[]{ 2560, 16});
        m.put("ENCHANTED_BONE",           new int[]{ 2560, 16});
        m.put("ENCHANTED_SPIDER_EYE",     new int[]{ 2560, 16});
        m.put("ENCHANTED_STRING",         new int[]{ 2560, 16});
        m.put("ENCHANTED_GUNPOWDER",      new int[]{ 2560, 16});
        m.put("ENCHANTED_BLAZE_ROD",      new int[]{ 2560, 16});
        m.put("ENCHANTED_GHAST_TEAR",     new int[]{ 2560, 16});
        m.put("RAW_FISH",                 new int[]{71680, 16});
        m.put("PRISMARINE_SHARD",         new int[]{71680, 16});
        m.put("PRISMARINE_CRYSTALS",      new int[]{71680, 16});
        m.put("LILY_PAD",                 new int[]{71680, 16});
        m.put("INK_SACK",                 new int[]{71680, 16});
        m.put("RUBY_GEM",                 new int[]{ 2560, 16});
        m.put("AMETHYST_GEM",             new int[]{ 2560, 16});
        m.put("JADE_GEM",                 new int[]{ 2560, 16});
        m.put("SAPPHIRE_GEM",             new int[]{ 2560, 16});
        m.put("AMBER_GEM",                new int[]{ 2560, 16});
        m.put("TOPAZ_GEM",                new int[]{ 2560, 16});
        m.put("JASPER_GEM",               new int[]{ 2560, 16});
        m.put("EXPERIENCE_BOTTLE",        new int[]{71680, 16});
        m.put("SULPHUR",                  new int[]{71680, 16});
        m.put("ENCHANTED_BOOK",           new int[]{ 2560, 16});
        m.put("RECOMBOBULATOR_3000",      new int[]{ 2560, 16});
        m.put("FUMING_POTATO_BOOK",       new int[]{ 2560, 16});
        m.put("HOT_POTATO_BOOK",          new int[]{ 2560, 16});
        m.put("DUNGEON_CHEST_KEY",        new int[]{ 2560, 16});
        m.put("REVENANT_FLESH",           new int[]{71680, 16});
        m.put("TARANTULA_WEB",            new int[]{71680, 16});
        m.put("WOLF_TOOTH",               new int[]{71680, 16});
        BAZAAR_ITEM_DATA = Collections.unmodifiableMap(m);
    }

    private final Map<String, List<BuyOrder>> buyOrders = new HashMap<>();
    private final Map<String, List<SellOrder>> sellOrders = new HashMap<>();
    private final Map<UUID, Integer> orderCounts = new HashMap<>();
    private final Map<UUID, List<String>> playerTransactions = new HashMap<>();
    private final Map<UUID, List<String>> bazaarHistory = new ConcurrentHashMap<>();
    private final Map<String, Double> instantBuyPrices = new HashMap<>();
    private final Map<String, Double> sellOfferPrices = new HashMap<>();
    private final Map<UUID, Double> claimableCoins = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> claimableItems = new HashMap<>();
    private final Map<UUID, FeeTier> feeTiers = new HashMap<>();

    private BazaarManager() {}

    public static BazaarManager getInstance() {
        return INSTANCE;
    }

    public UUID addBuyOrder(UUID buyer, String itemId, int quantity, double priceEach) {
        UUID orderId = UUID.randomUUID();
        BuyOrder order = new BuyOrder(orderId, buyer, itemId, quantity, priceEach);
        buyOrders.computeIfAbsent(itemId, k -> new ArrayList<>()).add(order);
        buyOrders.get(itemId).sort(Comparator.comparingDouble(BuyOrder::priceEach).reversed());
        recordBazaarEvent(buyer, "Placed buy order: " + quantity + "x " + itemId + " @ " + priceEach);
        matchOrders(itemId);
        return orderId;
    }

    public UUID addSellOrder(UUID seller, String itemId, int quantity, double priceEach) {
        UUID orderId = UUID.randomUUID();
        SellOrder order = new SellOrder(orderId, seller, itemId, quantity, priceEach);
        sellOrders.computeIfAbsent(itemId, k -> new ArrayList<>()).add(order);
        sellOrders.get(itemId).sort(Comparator.comparingDouble(SellOrder::priceEach));
        recordBazaarEvent(seller, "Placed sell order: " + quantity + "x " + itemId + " @ " + priceEach);
        matchOrders(itemId);
        return orderId;
    }

    public UUID addBuyOrder(UUID buyer, BazaarProduct product, int quantity, double priceEach) {
        return addBuyOrder(buyer, product.getItemId(), quantity, priceEach);
    }

    public UUID addSellOrder(UUID seller, BazaarProduct product, int quantity, double priceEach) {
        return addSellOrder(seller, product.getItemId(), quantity, priceEach);
    }

    /**
     * Crosses the book for {@code itemId}: while the highest bid meets or exceeds the lowest ask,
     * fills the resting orders against each other at the resting sell price. Depleted orders are
     * removed; partially-filled orders are replaced with their reduced remainder.
     */
    private void matchOrders(String itemId) {
        List<BuyOrder> buys = buyOrders.get(itemId);
        List<SellOrder> sells = sellOrders.get(itemId);
        if (buys == null || sells == null) return;
        while (!buys.isEmpty() && !sells.isEmpty()) {
            BuyOrder buy = buys.get(0);    // highest bid (sorted descending)
            SellOrder sell = sells.get(0); // lowest ask (sorted ascending)
            if (buy.priceEach() < sell.priceEach()) break; // no crossing trade
            int qty = Math.min(buy.quantity(), sell.quantity());
            double price = sell.priceEach();
            double total = qty * price;
            recordBazaarEvent(buy.buyer(), "Order filled: bought " + qty + "x " + itemId + " @ " + price);
            recordBazaarEvent(sell.seller(), "Order filled: sold " + qty + "x " + itemId + " @ " + price);
            addTransaction(buy.buyer(), "BUY " + qty + "x " + itemId + " for " + total);
            addTransaction(sell.seller(), "SELL " + qty + "x " + itemId + " for " + total);
            creditItems(buy.buyer(), itemId, qty);
            creditCoins(sell.seller(), total);

            buys.remove(0);
            if (buy.quantity() > qty) {
                buys.add(0, new BuyOrder(buy.id(), buy.buyer(), itemId, buy.quantity() - qty, buy.priceEach()));
            }
            sells.remove(0);
            if (sell.quantity() > qty) {
                sells.add(0, new SellOrder(sell.id(), sell.seller(), itemId, sell.quantity() - qty, sell.priceEach()));
            }
        }
    }

    /**
     * Instantly buys up to {@code quantity} units of {@code itemId}, consuming the cheapest resting
     * sell orders first. Returns how much was filled and the total coins spent.
     */
    public FillResult instantBuy(UUID buyer, String itemId, int quantity) {
        Objects.requireNonNull(buyer, "buyer");
        Objects.requireNonNull(itemId, "itemId");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive: " + quantity);
        List<SellOrder> book = sellOrders.get(itemId);
        int remaining = quantity;
        double totalCost = 0;
        int matched = 0;
        while (remaining > 0 && book != null && !book.isEmpty()) {
            SellOrder sell = book.get(0);
            int fill = Math.min(remaining, sell.quantity());
            totalCost += fill * sell.priceEach();
            remaining -= fill;
            matched++;
            book.remove(0);
            if (sell.quantity() > fill) {
                book.add(0, new SellOrder(sell.id(), sell.seller(), itemId, sell.quantity() - fill, sell.priceEach()));
            }
            recordBazaarEvent(sell.seller(), "Sell order filled: " + fill + "x " + itemId + " @ " + sell.priceEach());
            addTransaction(sell.seller(), "SELL " + fill + "x " + itemId + " for " + (fill * sell.priceEach()));
            creditCoins(sell.seller(), fill * sell.priceEach());
        }
        int filled = quantity - remaining;
        recordBazaarEvent(buyer, "Instant buy: " + filled + "x " + itemId + " for " + totalCost);
        addTransaction(buyer, "INSTANT_BUY " + filled + "x " + itemId + " for " + totalCost);
        return new FillResult(filled, remaining, totalCost, matched);
    }

    /**
     * Instantly sells up to {@code quantity} units of {@code itemId}, consuming the highest resting
     * buy orders first. Returns how much was filled and the total coins earned.
     */
    public FillResult instantSell(UUID seller, String itemId, int quantity) {
        Objects.requireNonNull(seller, "seller");
        Objects.requireNonNull(itemId, "itemId");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive: " + quantity);
        List<BuyOrder> book = buyOrders.get(itemId);
        int remaining = quantity;
        double totalEarned = 0;
        int matched = 0;
        while (remaining > 0 && book != null && !book.isEmpty()) {
            BuyOrder buy = book.get(0);
            int fill = Math.min(remaining, buy.quantity());
            totalEarned += fill * buy.priceEach();
            remaining -= fill;
            matched++;
            book.remove(0);
            if (buy.quantity() > fill) {
                book.add(0, new BuyOrder(buy.id(), buy.buyer(), itemId, buy.quantity() - fill, buy.priceEach()));
            }
            recordBazaarEvent(buy.buyer(), "Buy order filled: " + fill + "x " + itemId + " @ " + buy.priceEach());
            addTransaction(buy.buyer(), "BUY " + fill + "x " + itemId + " for " + (fill * buy.priceEach()));
            creditItems(buy.buyer(), itemId, fill);
        }
        int filled = quantity - remaining;
        recordBazaarEvent(seller, "Instant sell: " + filled + "x " + itemId + " for " + totalEarned);
        addTransaction(seller, "INSTANT_SELL " + filled + "x " + itemId + " for " + totalEarned);
        return new FillResult(filled, remaining, totalEarned, matched);
    }

    public FillResult instantBuy(UUID buyer, BazaarProduct product, int quantity) {
        return instantBuy(buyer, product.getItemId(), quantity);
    }

    public FillResult instantSell(UUID seller, BazaarProduct product, int quantity) {
        return instantSell(seller, product.getItemId(), quantity);
    }

    public void cancelBuyOrder(UUID orderId) {
        for (List<BuyOrder> list : buyOrders.values()) {
            if (list.removeIf(o -> o.id().equals(orderId))) return;
        }
        throw new IllegalArgumentException("no active buy order with id: " + orderId);
    }

    public void cancelSellOrder(UUID orderId) {
        for (List<SellOrder> list : sellOrders.values()) {
            if (list.removeIf(o -> o.id().equals(orderId))) return;
        }
        throw new IllegalArgumentException("no active sell order with id: " + orderId);
    }

    /** Cancels a buy order only if it belongs to {@code requester}. */
    public boolean cancelBuyOrder(UUID orderId, UUID requester) {
        for (List<BuyOrder> list : buyOrders.values()) {
            for (BuyOrder o : list) {
                if (o.id().equals(orderId)) {
                    if (!o.buyer().equals(requester)) return false;
                    list.remove(o);
                    return true;
                }
            }
        }
        return false;
    }

    /** Cancels a sell order only if it belongs to {@code requester}. */
    public boolean cancelSellOrder(UUID orderId, UUID requester) {
        for (List<SellOrder> list : sellOrders.values()) {
            for (SellOrder o : list) {
                if (o.id().equals(orderId)) {
                    if (!o.seller().equals(requester)) return false;
                    list.remove(o);
                    return true;
                }
            }
        }
        return false;
    }

    public List<BuyOrder> getBuyOrders(String itemId) {
        return Collections.unmodifiableList(buyOrders.getOrDefault(itemId, Collections.emptyList()));
    }

    public List<SellOrder> getSellOrders(String itemId) {
        return Collections.unmodifiableList(sellOrders.getOrDefault(itemId, Collections.emptyList()));
    }

    public List<BuyOrder> getBuyOrders(BazaarProduct product) {
        return getBuyOrders(product.getItemId());
    }

    public List<SellOrder> getSellOrders(BazaarProduct product) {
        return getSellOrders(product.getItemId());
    }

    public long getBuyOrderCount(String itemId) {
        return buyOrders.getOrDefault(itemId, Collections.emptyList()).size();
    }

    public long getSellOrderCount(String itemId) {
        return sellOrders.getOrDefault(itemId, Collections.emptyList()).size();
    }

    /** Total units resting across all sell orders — the supply available to instant buyers. */
    public long getSupply(String itemId) {
        long total = 0;
        for (SellOrder o : sellOrders.getOrDefault(itemId, Collections.emptyList())) {
            total += o.quantity();
        }
        return total;
    }

    /** Total units resting across all buy orders — the demand available to instant sellers. */
    public long getDemand(String itemId) {
        long total = 0;
        for (BuyOrder o : buyOrders.getOrDefault(itemId, Collections.emptyList())) {
            total += o.quantity();
        }
        return total;
    }

    public long getSupply(BazaarProduct product) {
        return getSupply(product.getItemId());
    }

    public long getDemand(BazaarProduct product) {
        return getDemand(product.getItemId());
    }

    public double getLowestAsk(String itemId) {
        List<SellOrder> orders = sellOrders.getOrDefault(itemId, Collections.emptyList());
        return orders.isEmpty() ? Double.MAX_VALUE : orders.get(0).priceEach();
    }

    public double getHighestBid(String itemId) {
        List<BuyOrder> orders = buyOrders.getOrDefault(itemId, Collections.emptyList());
        return orders.isEmpty() ? 0 : orders.get(0).priceEach();
    }

    public double getLowestAsk(BazaarProduct product) {
        return getLowestAsk(product.getItemId());
    }

    public double getHighestBid(BazaarProduct product) {
        return getHighestBid(product.getItemId());
    }

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

    public int getOrderCount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return orderCounts.getOrDefault(playerId, 0);
    }

    public void setOrderCount(UUID playerId, int count) {
        Objects.requireNonNull(playerId, "playerId");
        orderCounts.put(playerId, count);
    }

    public List<String> getTransactionHistory(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return Collections.unmodifiableList(playerTransactions.getOrDefault(playerId, Collections.emptyList()));
    }

    public void addTransaction(UUID playerId, String entry) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(entry, "entry");
        playerTransactions.computeIfAbsent(playerId, k -> new ArrayList<>()).add(entry);
    }

    public void recordBazaarEvent(UUID player, String summary) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(summary, "summary");
        bazaarHistory.computeIfAbsent(player, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getBazaarHistory(UUID player) {
        Objects.requireNonNull(player, "player");
        return Collections.unmodifiableList(bazaarHistory.getOrDefault(player, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllBazaarHistory() {
        return Collections.unmodifiableMap(bazaarHistory);
    }

    public String getBazaarStats(UUID player) {
        List<BazaarOrder> orders = getOrdersForPlayer(player);
        long buys = orders.stream().filter(o -> o.type() == BazaarOrderType.BUY).count();
        long sells = orders.stream().filter(o -> o.type() == BazaarOrderType.SELL).count();
        int events = bazaarHistory.getOrDefault(player, Collections.emptyList()).size();
        return "Bazaar Stats: Buy Orders: " + buys + " | Sell Orders: " + sells + " | Total Events: " + events;
    }

    /** Reference buy price: seed data if available, else lowest live ask. */
    public double getDisplayBuyPrice(BazaarProduct product) {
        double[] ref = PRODUCT_DATA.get(product.getItemId());
        return ref != null ? ref[0] : getLowestAsk(product);
    }

    /** Reference sell price: seed data if available, else highest live bid. */
    public double getDisplaySellPrice(BazaarProduct product) {
        double[] ref = PRODUCT_DATA.get(product.getItemId());
        return ref != null ? ref[1] : getHighestBid(product);
    }

    // --- Instant price accessors ---

    public double getInstantBuyPrice(String itemId) {
        return instantBuyPrices.getOrDefault(itemId, 0.0);
    }

    public double getInstantBuyPrice(BazaarProduct product) {
        return getInstantBuyPrice(product.getItemId());
    }

    public void setInstantBuyPrice(String itemId, double price) {
        instantBuyPrices.put(itemId, price);
    }

    public void setInstantBuyPrice(BazaarProduct product, double price) {
        setInstantBuyPrice(product.getItemId(), price);
    }

    public double getSellOfferPrice(String itemId) {
        return sellOfferPrices.getOrDefault(itemId, 0.0);
    }

    public double getSellOfferPrice(BazaarProduct product) {
        return getSellOfferPrice(product.getItemId());
    }

    public void setSellOfferPrice(String itemId, double price) {
        sellOfferPrices.put(itemId, price);
    }

    public void setSellOfferPrice(BazaarProduct product, double price) {
        setSellOfferPrice(product.getItemId(), price);
    }

    // --- Fee tiers ---

    /** The sell-tax tier applied to {@code player}'s claimed coin proceeds; {@link FeeTier#BASE} by default. */
    public FeeTier getFeeTier(UUID player) {
        Objects.requireNonNull(player, "player");
        return feeTiers.getOrDefault(player, FeeTier.BASE);
    }

    public void setFeeTier(UUID player, FeeTier tier) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(tier, "tier");
        feeTiers.put(player, tier);
    }

    /** Tax charged on {@code grossCoins} at the given tier. */
    public double computeFee(double grossCoins, FeeTier tier) {
        Objects.requireNonNull(tier, "tier");
        return grossCoins * tier.getRate();
    }

    /** Tax charged on {@code grossCoins} at the {@link FeeTier#BASE} rate. */
    public double computeFee(double grossCoins) {
        return computeFee(grossCoins, FeeTier.BASE);
    }

    // --- Order claim escrow ---

    /** Credits {@code player} the net (post-fee) coin proceeds of a filled sell order, held until claimed. */
    private void creditCoins(UUID player, double grossCoins) {
        double net = grossCoins - computeFee(grossCoins, getFeeTier(player));
        claimableCoins.merge(player, net, Double::sum);
    }

    /** Credits {@code player} the items of a filled buy order, held until claimed. */
    private void creditItems(UUID player, String itemId, int quantity) {
        claimableItems.computeIfAbsent(player, k -> new HashMap<>()).merge(itemId, quantity, Integer::sum);
    }

    /** Coins waiting to be claimed from {@code player}'s filled sell orders, net of fees. */
    public double getClaimableCoins(UUID player) {
        Objects.requireNonNull(player, "player");
        return claimableCoins.getOrDefault(player, 0.0);
    }

    /** Units of {@code itemId} waiting to be claimed from {@code player}'s filled buy orders. */
    public int getClaimableItems(UUID player, String itemId) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(itemId, "itemId");
        return claimableItems.getOrDefault(player, Collections.emptyMap()).getOrDefault(itemId, 0);
    }

    /** Removes and returns all coins {@code player} has waiting to claim. */
    public double claimCoins(UUID player) {
        Objects.requireNonNull(player, "player");
        Double amount = claimableCoins.remove(player);
        return amount == null ? 0.0 : amount;
    }

    /** Removes and returns the units of {@code itemId} {@code player} has waiting to claim. */
    public int claimItems(UUID player, String itemId) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(itemId, "itemId");
        Map<String, Integer> items = claimableItems.get(player);
        if (items == null) return 0;
        Integer quantity = items.remove(itemId);
        if (items.isEmpty()) claimableItems.remove(player);
        return quantity == null ? 0 : quantity;
    }

    // --- Persistence ---

    public void load(File dataFolder) {
        File file = new File(dataFolder, "bazaar.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        instantBuyPrices.clear();
        if (cfg.isConfigurationSection("prices.instantBuy")) {
            for (String itemId : cfg.getConfigurationSection("prices.instantBuy").getKeys(false)) {
                instantBuyPrices.put(itemId, cfg.getDouble("prices.instantBuy." + itemId));
            }
        }

        sellOfferPrices.clear();
        if (cfg.isConfigurationSection("prices.sellOffer")) {
            for (String itemId : cfg.getConfigurationSection("prices.sellOffer").getKeys(false)) {
                sellOfferPrices.put(itemId, cfg.getDouble("prices.sellOffer." + itemId));
            }
        }

        playerTransactions.clear();
        if (cfg.isConfigurationSection("transactions")) {
            for (String key : cfg.getConfigurationSection("transactions").getKeys(false)) {
                try {
                    playerTransactions.put(UUID.fromString(key),
                            new ArrayList<>(cfg.getStringList("transactions." + key)));
                } catch (IllegalArgumentException ignored) {}
            }
        }

        bazaarHistory.clear();
        if (cfg.isConfigurationSection("bazaarHistory")) {
            for (String key : cfg.getConfigurationSection("bazaarHistory").getKeys(false)) {
                try {
                    bazaarHistory.put(UUID.fromString(key),
                            new ArrayList<>(cfg.getStringList("bazaarHistory." + key)));
                } catch (IllegalArgumentException ignored) {}
            }
        }

        claimableCoins.clear();
        if (cfg.isConfigurationSection("claimableCoins")) {
            for (String key : cfg.getConfigurationSection("claimableCoins").getKeys(false)) {
                try {
                    claimableCoins.put(UUID.fromString(key), cfg.getDouble("claimableCoins." + key));
                } catch (IllegalArgumentException ignored) {}
            }
        }

        claimableItems.clear();
        if (cfg.isConfigurationSection("claimableItems")) {
            for (String key : cfg.getConfigurationSection("claimableItems").getKeys(false)) {
                try {
                    UUID player = UUID.fromString(key);
                    Map<String, Integer> items = new HashMap<>();
                    for (String itemId : cfg.getConfigurationSection("claimableItems." + key).getKeys(false)) {
                        items.put(itemId, cfg.getInt("claimableItems." + key + "." + itemId));
                    }
                    if (!items.isEmpty()) claimableItems.put(player, items);
                } catch (IllegalArgumentException ignored) {}
            }
        }

        feeTiers.clear();
        if (cfg.isConfigurationSection("feeTiers")) {
            for (String key : cfg.getConfigurationSection("feeTiers").getKeys(false)) {
                try {
                    feeTiers.put(UUID.fromString(key), FeeTier.valueOf(cfg.getString("feeTiers." + key)));
                } catch (IllegalArgumentException ignored) {}
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
                    } catch (Exception ignored) {}
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
                    } catch (Exception ignored) {}
                }
                if (!list.isEmpty()) {
                    list.sort(Comparator.comparingDouble(SellOrder::priceEach));
                    sellOrders.put(itemId, list);
                }
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "bazaar.yml");
        YamlConfiguration cfg = new YamlConfiguration();

        for (Map.Entry<String, Double> entry : instantBuyPrices.entrySet()) {
            cfg.set("prices.instantBuy." + entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Double> entry : sellOfferPrices.entrySet()) {
            cfg.set("prices.sellOffer." + entry.getKey(), entry.getValue());
        }
        for (Map.Entry<UUID, List<String>> entry : playerTransactions.entrySet()) {
            cfg.set("transactions." + entry.getKey(), entry.getValue());
        }
        for (Map.Entry<UUID, List<String>> entry : bazaarHistory.entrySet()) {
            cfg.set("bazaarHistory." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Double> entry : claimableCoins.entrySet()) {
            cfg.set("claimableCoins." + entry.getKey(), entry.getValue());
        }
        for (Map.Entry<UUID, Map<String, Integer>> entry : claimableItems.entrySet()) {
            for (Map.Entry<String, Integer> item : entry.getValue().entrySet()) {
                cfg.set("claimableItems." + entry.getKey() + "." + item.getKey(), item.getValue());
            }
        }
        for (Map.Entry<UUID, FeeTier> entry : feeTiers.entrySet()) {
            cfg.set("feeTiers." + entry.getKey(), entry.getValue().name());
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

    public void clear() {
        buyOrders.clear();
        sellOrders.clear();
        bazaarHistory.clear();
        claimableCoins.clear();
        claimableItems.clear();
        feeTiers.clear();
    }
}
