package com.skyblock.collections;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CollectionsManager {

    private static final CollectionsManager INSTANCE = new CollectionsManager();

    private final Map<UUID, Map<String, Long>> perPlayerCounts = new HashMap<>();

    private CollectionsManager() {
    }

    public static CollectionsManager getInstance() {
        return INSTANCE;
    }

    public long addCount(UUID playerId, String collectionId, long amount) {
        if (collectionId == null || collectionId.isBlank()) {
            throw new IllegalArgumentException("collectionId must not be null or blank");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        return perPlayerCounts.computeIfAbsent(playerId, id -> new HashMap<>())
                .merge(collectionId, amount, Long::sum);
    }

    public long getCount(UUID playerId, String collectionId) {
        Map<String, Long> counts = perPlayerCounts.get(playerId);
        if (counts == null) {
            return 0L;
        }
        return counts.getOrDefault(collectionId, 0L);
    }

    public Map<String, Long> getCounts(UUID playerId) {
        Map<String, Long> counts = perPlayerCounts.get(playerId);
        return counts == null
                ? Map.of()
                : Collections.unmodifiableMap(new HashMap<>(counts));
    }

    public void resetCounts(UUID playerId) {
        perPlayerCounts.remove(playerId);
    }
}
