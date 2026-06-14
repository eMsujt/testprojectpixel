package com.skyblock.core.collections;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class CollectionsManager {

    private final Map<UUID, Map<String, Long>> collectionAmounts = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> collectionTiers = new HashMap<>();

    // Item counts required to reach each tier (index 0 = tier I, index N-1 = max tier).
    public static final Map<String, int[]> COLLECTION_TIERS;

    static {
        Map<String, int[]> m = new LinkedHashMap<>();
        // Farming
        m.put("wheat",       new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put("carrot",      new int[]{100, 250, 500, 1_750, 5_000, 15_000, 30_000, 60_000, 100_000});
        m.put("potato",      new int[]{100, 250, 500, 1_750, 5_000, 15_000, 30_000, 60_000, 100_000});
        m.put("pumpkin",     new int[]{40, 100, 250, 1_000, 2_500, 7_500, 15_000, 25_000, 50_000});
        m.put("melon",       new int[]{250, 500, 1_500, 5_000, 15_000, 30_000, 60_000, 100_000, 250_000});
        m.put("mushroom",    new int[]{50, 100, 250, 1_000, 2_500, 7_500, 20_000, 50_000, 100_000});
        m.put("cocoa",       new int[]{50, 100, 250, 1_000, 2_500, 7_500, 20_000, 50_000, 100_000});
        m.put("sugar_cane",  new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put("nether_wart", new int[]{50, 100, 250, 1_000, 2_500, 7_500, 20_000, 50_000, 100_000});
        // Mining
        m.put("cobblestone", new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put("coal",        new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put("iron",        new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("gold",        new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("diamond",     new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("lapis",       new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("emerald",     new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("redstone",    new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        m.put("quartz",      new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        m.put("obsidian",    new int[]{50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000, 50_000});
        // Foraging
        m.put("wood",        new int[]{50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000});
        // Fishing
        m.put("fish",        new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put("salmon",      new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put("clownfish",   new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put("pufferfish",  new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        m.put("ink_sac",     new int[]{20, 50, 100, 250, 1_000, 2_500, 7_500, 15_000, 30_000});
        COLLECTION_TIERS = Collections.unmodifiableMap(m);
    }

    public static final List<String> COLLECTIONS = List.of(
            "wheat", "carrot", "potato", "pumpkin", "melon",
            "mushroom", "cocoa", "cactus", "sugar_cane", "nether_wart",
            "cobblestone", "coal", "iron", "gold", "diamond",
            "lapis", "emerald", "redstone", "quartz", "obsidian",
            "wood", "leather", "feather", "string", "ink_sac",
            "fish", "clownfish", "pufferfish", "salmon"
    );

    public long getAmount(UUID uuid, String collection) {
        return collectionAmounts.computeIfAbsent(uuid, k -> new HashMap<>())
                .getOrDefault(collection.toLowerCase(), 0L);
    }

    public void addAmount(UUID uuid, String collection, long amount) {
        Map<String, Long> amounts = collectionAmounts.computeIfAbsent(uuid, k -> new HashMap<>());
        String key = collection.toLowerCase();
        amounts.put(key, amounts.getOrDefault(key, 0L) + Math.max(0, amount));
    }

    public int getTier(UUID uuid, String collection) {
        return collectionTiers.computeIfAbsent(uuid, k -> new HashMap<>())
                .getOrDefault(collection.toLowerCase(), 0);
    }

    public void setTier(UUID uuid, String collection, int tier) {
        collectionTiers.computeIfAbsent(uuid, k -> new HashMap<>())
                .put(collection.toLowerCase(), Math.max(0, tier));
    }

    public Map<String, Long> getCollectionAmounts(UUID uuid) {
        return Collections.unmodifiableMap(collectionAmounts.computeIfAbsent(uuid, k -> new HashMap<>()));
    }

    public Map<String, Integer> getCollectionTiers(UUID uuid) {
        return Collections.unmodifiableMap(collectionTiers.computeIfAbsent(uuid, k -> new HashMap<>()));
    }
}
