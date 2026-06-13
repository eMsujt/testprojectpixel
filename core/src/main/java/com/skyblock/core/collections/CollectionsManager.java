package com.skyblock.core.collections;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class CollectionsManager {

    private final Map<UUID, Map<String, Long>> collectionAmounts = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> collectionTiers = new HashMap<>();

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
