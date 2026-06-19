package com.skyblock.core.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CollectionsManager {

    private static final CollectionsManager INSTANCE = new CollectionsManager();

    private final Map<UUID, Map<String, Long>> playerCollections = new HashMap<>();

    private CollectionsManager() {}

    public static CollectionsManager getInstance() {
        return INSTANCE;
    }

    public Map<String, Long> getCollections(UUID playerId) {
        return playerCollections.computeIfAbsent(playerId, k -> new HashMap<>());
    }

    public void addAmount(UUID playerId, String collection, long amount) {
        getCollections(playerId).merge(collection, amount, Long::sum);
    }

    public long getAmount(UUID playerId, String collection) {
        return getCollections(playerId).getOrDefault(collection, 0L);
    }
}
