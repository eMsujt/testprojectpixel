package com.skyblock.core.collections;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class CollectionsManager {

    public static final int MAX_TIER = 9;

    // Item counts required to reach each tier (index 0 = tier I … index 8 = tier IX).
    public static final Map<String, int[]> COLLECTION_DATA;

    static {
        Map<String, int[]> m = new LinkedHashMap<>();
        // Farming
        m.put("wheat",        new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put("carrot",       new int[]{100, 250, 500, 1_750, 5_000, 15_000, 30_000, 60_000, 100_000});
        m.put("potato",       new int[]{100, 250, 500, 1_750, 5_000, 15_000, 30_000, 60_000, 100_000});
        m.put("pumpkin",      new int[]{40, 100, 250, 1_000, 2_500, 7_500, 15_000, 25_000, 50_000});
        m.put("melon",        new int[]{250, 500, 1_500, 5_000, 15_000, 30_000, 60_000, 100_000, 250_000});
        m.put("mushroom",     new int[]{50, 100, 250, 1_000, 2_500, 7_500, 20_000, 50_000, 100_000});
        m.put("cactus",       new int[]{50, 100, 250, 1_000, 2_500, 7_500, 20_000, 50_000, 100_000});
        m.put("sugar_cane",   new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put("nether_wart",  new int[]{50, 100, 250, 1_000, 2_500, 7_500, 20_000, 50_000, 100_000});
        m.put("cocoa_beans",  new int[]{50, 100, 250, 1_000, 2_500, 7_500, 20_000, 50_000, 100_000});
        // Mining
        m.put("cobblestone",  new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put("coal",         new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put("iron_ingot",   new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("gold_ingot",   new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("diamond",      new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("emerald",      new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("redstone",     new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put("lapis_lazuli", new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("quartz",       new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("obsidian",     new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("glowstone",    new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("gravel",       new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("ice",          new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("netherrack",   new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("sand",         new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("end_stone",    new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        // Foraging
        m.put("oak_log",      new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put("spruce_log",   new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put("birch_log",    new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put("jungle_log",   new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put("acacia_log",   new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put("dark_oak_log", new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        // Combat
        m.put("rotten_flesh",       new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put("bone",               new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put("spider_eye",         new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("string",             new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("gunpowder",          new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("ender_pearl",        new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("ghast_tear",         new int[]{20, 50, 100, 250, 1_000, 2_500, 5_000, 10_000, 20_000});
        m.put("slime_ball",         new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("blaze_rod",          new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("magma_cream",        new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        // Fishing
        m.put("raw_fish",           new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put("raw_salmon",         new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put("clownfish",          new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put("pufferfish",         new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put("prismarine_shard",   new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put("prismarine_crystals",new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put("clay",               new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put("lily_pad",           new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put("ink_sac",            new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put("sponge",             new int[]{10, 25, 50, 100, 250, 500, 1_000, 2_500, 5_000});
        COLLECTION_DATA = Collections.unmodifiableMap(m);
    }

    private static final long[] ITEMS_PER_TIER = {
            50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000
    };

    public enum CollectionType {
        // Farming
        WHEAT("Wheat"),
        CARROT("Carrot"),
        POTATO("Potato"),
        PUMPKIN("Pumpkin"),
        MELON("Melon"),
        MUSHROOM("Mushroom"),
        CACTUS("Cactus"),
        SUGAR_CANE("Sugar Cane"),
        NETHER_WART("Nether Wart"),
        COCOA_BEANS("Cocoa Beans"),
        // Mining
        COBBLESTONE("Cobblestone"),
        COAL("Coal"),
        IRON_INGOT("Iron Ingot"),
        GOLD_INGOT("Gold Ingot"),
        DIAMOND("Diamond"),
        EMERALD("Emerald"),
        REDSTONE("Redstone"),
        LAPIS_LAZULI("Lapis Lazuli"),
        QUARTZ("Quartz"),
        OBSIDIAN("Obsidian"),
        GLOWSTONE("Glowstone"),
        GRAVEL("Gravel"),
        ICE("Ice"),
        NETHERRACK("Netherrack"),
        SAND("Sand"),
        END_STONE("End Stone"),
        // Foraging
        OAK_LOG("Oak Log"),
        SPRUCE_LOG("Spruce Log"),
        BIRCH_LOG("Birch Log"),
        JUNGLE_LOG("Jungle Log"),
        ACACIA_LOG("Acacia Log"),
        DARK_OAK_LOG("Dark Oak Log"),
        // Combat
        ROTTEN_FLESH("Rotten Flesh"),
        BONE("Bone"),
        SPIDER_EYE("Spider Eye"),
        STRING("String"),
        GUNPOWDER("Gunpowder"),
        ENDER_PEARL("Ender Pearl"),
        GHAST_TEAR("Ghast Tear"),
        SLIME_BALL("Slime Ball"),
        BLAZE_ROD("Blaze Rod"),
        MAGMA_CREAM("Magma Cream"),
        // Fishing
        RAW_FISH("Raw Fish"),
        RAW_SALMON("Raw Salmon"),
        CLOWNFISH("Clownfish"),
        PUFFERFISH("Pufferfish"),
        PRISMARINE_SHARD("Prismarine Shard"),
        PRISMARINE_CRYSTALS("Prismarine Crystals"),
        CLAY("Clay"),
        LILY_PAD("Lily Pad"),
        INK_SAC("Ink Sac"),
        SPONGE("Sponge");

        private final String displayName;

        CollectionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum CollectionCategory {
        FARMING("Farming",
                CollectionType.WHEAT, CollectionType.CARROT, CollectionType.POTATO,
                CollectionType.PUMPKIN, CollectionType.MELON, CollectionType.MUSHROOM,
                CollectionType.CACTUS, CollectionType.SUGAR_CANE,
                CollectionType.NETHER_WART, CollectionType.COCOA_BEANS),
        MINING("Mining",
                CollectionType.COBBLESTONE, CollectionType.COAL, CollectionType.IRON_INGOT,
                CollectionType.GOLD_INGOT, CollectionType.DIAMOND, CollectionType.EMERALD,
                CollectionType.REDSTONE, CollectionType.LAPIS_LAZULI, CollectionType.QUARTZ,
                CollectionType.OBSIDIAN, CollectionType.GLOWSTONE, CollectionType.GRAVEL,
                CollectionType.ICE, CollectionType.NETHERRACK, CollectionType.SAND,
                CollectionType.END_STONE),
        COMBAT("Combat",
                CollectionType.ROTTEN_FLESH, CollectionType.BONE, CollectionType.SPIDER_EYE,
                CollectionType.STRING, CollectionType.GUNPOWDER, CollectionType.ENDER_PEARL,
                CollectionType.GHAST_TEAR, CollectionType.SLIME_BALL, CollectionType.BLAZE_ROD,
                CollectionType.MAGMA_CREAM),
        FORAGING("Foraging",
                CollectionType.OAK_LOG, CollectionType.SPRUCE_LOG, CollectionType.BIRCH_LOG,
                CollectionType.JUNGLE_LOG, CollectionType.ACACIA_LOG, CollectionType.DARK_OAK_LOG),
        FISHING("Fishing",
                CollectionType.RAW_FISH, CollectionType.RAW_SALMON, CollectionType.CLOWNFISH,
                CollectionType.PUFFERFISH, CollectionType.PRISMARINE_SHARD,
                CollectionType.PRISMARINE_CRYSTALS, CollectionType.CLAY, CollectionType.LILY_PAD,
                CollectionType.INK_SAC, CollectionType.SPONGE);

        private final String displayName;
        private final CollectionType[] collections;

        CollectionCategory(String displayName, CollectionType... collections) {
            this.displayName = displayName;
            this.collections = collections;
        }

        public String getDisplayName() {
            return displayName;
        }

        public CollectionType[] getCollections() {
            return collections;
        }
    }

    private static final CollectionsManager INSTANCE = new CollectionsManager();

    private final Map<UUID, Map<CollectionType, Long>> playerCollections = new HashMap<>();
    private final Map<UUID, List<String>> collectionsHistory = new HashMap<>();

    private CollectionsManager() {}

    public static CollectionsManager getInstance() {
        return INSTANCE;
    }

    public long addItems(UUID playerId, CollectionType type, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<CollectionType, Long> totals = playerCollections.computeIfAbsent(
                playerId, id -> new EnumMap<>(CollectionType.class));
        int tierBefore = getTier(playerId, type);
        long total = totals.getOrDefault(type, 0L) + amount;
        totals.put(type, total);
        recordCollectionEvent(playerId, "Added " + amount + " " + type.getDisplayName() + ": total " + total);
        int tierAfter = getTier(playerId, type);
        if (tierAfter > tierBefore) {
            recordCollectionEvent(playerId, "Reached tier " + tierAfter + " in " + type.getDisplayName());
        }
        return total;
    }

    public long getItems(UUID playerId, CollectionType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<CollectionType, Long> totals = playerCollections.get(playerId);
        return totals == null ? 0L : totals.getOrDefault(type, 0L);
    }

    public int getTier(UUID playerId, CollectionType type) {
        long items = getItems(playerId, type);
        int tier = 0;
        while (tier < MAX_TIER && items >= ITEMS_PER_TIER[tier]) {
            tier++;
        }
        return tier;
    }

    public long getItemsToNextTier(UUID playerId, CollectionType type) {
        int tier = getTier(playerId, type);
        if (tier >= MAX_TIER) {
            return 0L;
        }
        return ITEMS_PER_TIER[tier] - getItems(playerId, type);
    }

    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerCollections.remove(playerId) != null;
    }

    // Collections history

    public void recordCollectionEvent(UUID playerId, String summary) {
        collectionsHistory.computeIfAbsent(playerId, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getCollectionsHistory(UUID playerId) {
        return Collections.unmodifiableList(collectionsHistory.getOrDefault(playerId, new ArrayList<>()));
    }

    public Map<UUID, List<String>> getAllCollectionsHistory() {
        Map<UUID, List<String>> copy = new HashMap<>();
        for (Map.Entry<UUID, List<String>> entry : collectionsHistory.entrySet()) {
            copy.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        }
        return Collections.unmodifiableMap(copy);
    }

    public String getCollectionStats(UUID playerId) {
        Map<CollectionType, Long> totals = playerCollections.getOrDefault(playerId, new EnumMap<>(CollectionType.class));
        List<Map.Entry<CollectionType, Long>> sorted = new ArrayList<>(totals.entrySet());
        sorted.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));
        StringBuilder sb = new StringBuilder("Top Collections:");
        int limit = Math.min(5, sorted.size());
        if (limit == 0) {
            sb.append(" none");
        } else {
            for (int i = 0; i < limit; i++) {
                sb.append(" ").append(sorted.get(i).getKey().getDisplayName())
                        .append("=").append(sorted.get(i).getValue());
            }
        }
        return sb.toString();
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "collections.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        playerCollections.clear();
        collectionsHistory.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (cfg.isConfigurationSection(key)) {
                    Map<CollectionType, Long> totals = new EnumMap<>(CollectionType.class);
                    for (String typeName : cfg.getConfigurationSection(key).getKeys(false)) {
                        try {
                            CollectionType type = CollectionType.valueOf(typeName);
                            totals.put(type, cfg.getLong(key + "." + typeName, 0L));
                        } catch (IllegalArgumentException ignored) {
                            // skip unknown collection types
                        }
                    }
                    if (!totals.isEmpty()) {
                        playerCollections.put(uuid, totals);
                    }
                }
                List<String> colHistory = cfg.getStringList(key + ".collectionsHistory");
                if (!colHistory.isEmpty()) {
                    collectionsHistory.put(uuid, new ArrayList<>(colHistory));
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "collections.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<CollectionType, Long>> entry : playerCollections.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<CollectionType, Long> col : entry.getValue().entrySet()) {
                cfg.set(key + "." + col.getKey().name(), col.getValue());
            }
        }
        for (Map.Entry<UUID, List<String>> entry : collectionsHistory.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                cfg.set(entry.getKey().toString() + ".collectionsHistory", entry.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save collections.yml", e);
        }
    }
}
