package com.skyblock.core.manager;

import com.skyblock.core.model.Collection;
import com.skyblock.core.model.CollectionCategory;
import com.skyblock.core.model.CollectionTier;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Canonical singleton tracking per-player collection progress.
 *
 * <p>Uses {@link Collection} and {@link CollectionCategory} from
 * {@code com.skyblock.core.model} as the canonical enum types.</p>
 */
public final class CollectionManager {

    public static final int MAX_TIER = 9;

    /** Per-collection cumulative tier thresholds (index 0 = tier I, index 8 = tier IX). */
    public static final Map<Collection, int[]> TIER_DATA;

    static {
        Map<Collection, int[]> m = new EnumMap<>(Collection.class);
        // Farming
        m.put(Collection.WHEAT,               new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.CARROT,              new int[]{100, 250, 500, 1_750, 5_000, 15_000, 30_000, 60_000, 100_000});
        m.put(Collection.POTATO,              new int[]{100, 250, 500, 1_750, 5_000, 15_000, 30_000, 60_000, 100_000});
        m.put(Collection.PUMPKIN,             new int[]{40, 100, 250, 1_000, 2_500, 7_500, 15_000, 25_000, 50_000});
        m.put(Collection.MELON,               new int[]{250, 500, 1_500, 5_000, 15_000, 30_000, 60_000, 100_000, 250_000});
        m.put(Collection.MUSHROOM,            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 20_000, 50_000, 100_000});
        m.put(Collection.CACTUS,              new int[]{50, 100, 250, 1_000, 2_500, 7_500, 20_000, 50_000, 100_000});
        m.put(Collection.SUGAR_CANE,          new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.NETHER_WART,         new int[]{50, 100, 250, 1_000, 2_500, 7_500, 20_000, 50_000, 100_000});
        m.put(Collection.COCOA_BEANS,         new int[]{50, 100, 250, 1_000, 2_500, 7_500, 20_000, 50_000, 100_000});
        // Mining
        m.put(Collection.COBBLESTONE,         new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.COAL,                new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.IRON_INGOT,          new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.GOLD_INGOT,          new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.DIAMOND,             new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.EMERALD,             new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.REDSTONE,            new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.LAPIS_LAZULI,        new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.QUARTZ,              new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.OBSIDIAN,            new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.GLOWSTONE,           new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.GRAVEL,              new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.ICE,                 new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.NETHERRACK,          new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.SAND,                new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.END_STONE,           new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        // Combat
        m.put(Collection.ROTTEN_FLESH,        new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.BONE,                new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.SPIDER_EYE,          new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.STRING,              new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.GUNPOWDER,           new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.ENDER_PEARL,         new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.GHAST_TEAR,          new int[]{20, 50, 100, 250, 1_000, 2_500, 5_000, 10_000, 20_000});
        m.put(Collection.SLIME_BALL,          new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.BLAZE_ROD,           new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put(Collection.MAGMA_CREAM,         new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        // Foraging
        m.put(Collection.OAK_LOG,             new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.SPRUCE_LOG,          new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.BIRCH_LOG,           new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.JUNGLE_LOG,          new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.ACACIA_LOG,          new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put(Collection.DARK_OAK_LOG,        new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        // Fishing
        m.put(Collection.RAW_FISH,            new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put(Collection.RAW_SALMON,          new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put(Collection.CLOWNFISH,           new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put(Collection.PUFFERFISH,          new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put(Collection.PRISMARINE_SHARD,    new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put(Collection.PRISMARINE_CRYSTALS, new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put(Collection.CLAY,                new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put(Collection.LILY_PAD,            new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put(Collection.INK_SAC,             new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put(Collection.SPONGE,              new int[]{10, 25, 50, 100, 250, 500, 1_000, 2_500, 5_000});
        TIER_DATA = Collections.unmodifiableMap(m);
    }

    private static final CollectionManager INSTANCE = new CollectionManager();

    private final Map<UUID, Map<Collection, Long>> playerCollections = new HashMap<>();
    private final Map<UUID, List<String>> collectionsHistory = new HashMap<>();

    private CollectionManager() {}

    public static CollectionManager getInstance() {
        return INSTANCE;
    }

    public long addItems(UUID playerId, Collection collection, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(collection, "collection");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<Collection, Long> totals = playerCollections.computeIfAbsent(
                playerId, id -> new EnumMap<>(Collection.class));
        int tierBefore = getTier(playerId, collection);
        long total = totals.getOrDefault(collection, 0L) + amount;
        totals.put(collection, total);
        recordCollectionEvent(playerId, "Added " + amount + " " + collection.getDisplayName() + ": total " + total);
        int tierAfter = getTier(playerId, collection);
        if (tierAfter > tierBefore) {
            recordCollectionEvent(playerId, "Reached tier " + tierAfter + " in " + collection.getDisplayName());
        }
        return total;
    }

    /** Adds items by name string (enum constant or item key); returns {@code -1} if the name is unknown. */
    public long addItems(UUID playerId, String collectionName, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Collection collection = Collection.parse(collectionName);
        if (collection == null) return -1L;
        return addItems(playerId, collection, amount);
    }

    public long getItems(UUID playerId, Collection collection) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(collection, "collection");
        Map<Collection, Long> totals = playerCollections.get(playerId);
        return totals == null ? 0L : totals.getOrDefault(collection, 0L);
    }

    public int getTier(UUID playerId, Collection collection) {
        int[] thresholds = TIER_DATA.get(collection);
        if (thresholds == null) return 0;
        long items = getItems(playerId, collection);
        int tier = 0;
        while (tier < thresholds.length && items >= thresholds[tier]) tier++;
        return tier;
    }

    /** Returns how many more items are needed to unlock the next tier, or {@code 0} if maxed. */
    public long getItemsToNextTier(UUID playerId, Collection collection) {
        int[] thresholds = TIER_DATA.get(collection);
        if (thresholds == null) return 0L;
        int tier = getTier(playerId, collection);
        if (tier >= thresholds.length) return 0L;
        return thresholds[tier] - getItems(playerId, collection);
    }

    /** Returns the {@link CollectionTier} the player is currently at, or {@code null} if none unlocked. */
    public CollectionTier getCollectionTier(UUID playerId, Collection collection) {
        return CollectionTier.fromLevel(getTier(playerId, collection));
    }

    /** Returns {@code true} once the player has unlocked the given tier (1-based) for the collection. */
    public boolean hasUnlockedTier(UUID playerId, Collection collection, int tier) {
        return getTier(playerId, collection) >= tier;
    }

    /** Returns {@code true} when the player has reached the maximum tier for the collection. */
    public boolean isMaxed(UUID playerId, Collection collection) {
        int[] thresholds = TIER_DATA.get(collection);
        return thresholds != null && getTier(playerId, collection) >= thresholds.length;
    }

    /** Returns progress toward the next tier as a fraction in {@code [0, 1]} (1 if maxed). */
    public double getProgressToNextTier(UUID playerId, Collection collection) {
        int[] thresholds = TIER_DATA.get(collection);
        if (thresholds == null) return 0.0;
        int tier = getTier(playerId, collection);
        if (tier >= thresholds.length) return 1.0;
        long items = getItems(playerId, collection);
        long floor = tier == 0 ? 0L : thresholds[tier - 1];
        long ceil = thresholds[tier];
        return ceil == floor ? 1.0 : (double) (items - floor) / (ceil - floor);
    }

    /** Returns the total number of collection tiers the player has unlocked across all collections. */
    public int getTotalTiersUnlocked(UUID playerId) {
        int total = 0;
        for (Collection c : TIER_DATA.keySet()) total += getTier(playerId, c);
        return total;
    }

    /** Returns an unmodifiable view of all collection totals for the player. */
    public Map<Collection, Long> getAll(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<Collection, Long> totals = playerCollections.get(playerId);
        return totals == null ? Collections.emptyMap() : Collections.unmodifiableMap(totals);
    }

    /** Returns the sum of all item totals for every collection in the given category. */
    public long getTotalForCategory(UUID playerId, CollectionCategory category) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(category, "category");
        long total = 0L;
        for (Collection c : category.getCollections()) total += getItems(playerId, c);
        return total;
    }

    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerCollections.remove(playerId) != null;
    }

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
        Map<Collection, Long> totals = playerCollections.getOrDefault(playerId, new EnumMap<>(Collection.class));
        List<Map.Entry<Collection, Long>> sorted = new ArrayList<>(totals.entrySet());
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

    /** Returns how many items the player has collected for the given material (0 if unmapped). */
    public long getCollection(UUID playerId, Material material) {
        Collection c = Collection.parse(material.name());
        return c == null ? 0L : getItems(playerId, c);
    }

    /** Returns the unlocked tier for the given material (0 if unmapped or not started). */
    public int getTier(UUID playerId, Material material) {
        Collection c = Collection.parse(material.name());
        return c == null ? 0 : getTier(playerId, c);
    }

    /**
     * Adds {@code amount} items to the player's collection for the given material and
     * returns the number of new tiers unlocked (0 if no tier was crossed, -1 if unmapped).
     */
    public int addCollection(UUID playerId, Material material, long amount) {
        Collection c = Collection.parse(material.name());
        if (c == null) return -1;
        int before = getTier(playerId, c);
        addItems(playerId, c, amount);
        return getTier(playerId, c) - before;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "collections.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        playerCollections.clear();
        collectionsHistory.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (cfg.isConfigurationSection(key + ".items")) {
                    Map<Collection, Long> totals = new EnumMap<>(Collection.class);
                    for (String name : cfg.getConfigurationSection(key + ".items").getKeys(false)) {
                        Collection c = Collection.parse(name);
                        if (c != null) totals.put(c, cfg.getLong(key + ".items." + name, 0L));
                    }
                    if (!totals.isEmpty()) playerCollections.put(uuid, totals);
                }
                List<String> history = cfg.getStringList(key + ".history");
                if (!history.isEmpty()) collectionsHistory.put(uuid, new ArrayList<>(history));
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "collections.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<Collection, Long>> entry : playerCollections.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<Collection, Long> col : entry.getValue().entrySet()) {
                cfg.set(key + ".items." + col.getKey().itemKey, col.getValue());
            }
        }
        for (Map.Entry<UUID, List<String>> entry : collectionsHistory.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                cfg.set(entry.getKey().toString() + ".history", entry.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save collections.yml", e);
        }
    }
}
