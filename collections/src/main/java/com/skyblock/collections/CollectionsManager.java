package com.skyblock.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class CollectionsManager {

    private static final CollectionsManager INSTANCE = new CollectionsManager();

    private final Map<UUID, Map<String, Long>> perPlayerCounts = new HashMap<>();
    private final Map<UUID, List<String>> collectionsHistory = new HashMap<>();

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
        long newTotal = perPlayerCounts.computeIfAbsent(playerId, id -> new HashMap<>())
                .merge(collectionId, amount, Long::sum);
        recordCollectionEvent(playerId, "Added " + amount + " " + collectionId + ": total " + newTotal);
        return newTotal;
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
}
