package com.skyblock.core.collections;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class CollectionsManager {

    public static final int MAX_TIER = 9;

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
        long total = totals.getOrDefault(type, 0L) + amount;
        totals.put(type, total);
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

    public void load(File dataFolder) {
        File file = new File(dataFolder, "collections.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        playerCollections.clear();
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
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save collections.yml", e);
        }
    }
}
