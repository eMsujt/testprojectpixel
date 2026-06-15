package com.skyblock.collections;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.CollectionManager} instead.
 */
@Deprecated
public final class CollectionsManager {

    private static final CollectionsManager INSTANCE = new CollectionsManager();
    private final com.skyblock.core.manager.CollectionManager delegate =
            com.skyblock.core.manager.CollectionManager.getInstance();

    private CollectionsManager() {}

    public static CollectionsManager getInstance() {
        return INSTANCE;
    }

    public long addCount(UUID playerId, String collectionId, long amount) {
        return delegate.addItems(playerId, collectionId, amount);
    }

    public long getCount(UUID playerId, String collectionId) {
        com.skyblock.core.model.Collection c = com.skyblock.core.model.Collection.parse(collectionId);
        return c == null ? 0L : delegate.getItems(playerId, c);
    }

    public Map<String, Long> getCounts(UUID playerId) {
        Map<String, Long> result = new HashMap<>();
        for (Map.Entry<com.skyblock.core.model.Collection, Long> e : delegate.getAll(playerId).entrySet()) {
            result.put(e.getKey().itemKey, e.getValue());
        }
        return Collections.unmodifiableMap(result);
    }

    public void resetCounts(UUID playerId) {
        delegate.reset(playerId);
    }

    public void recordCollectionEvent(UUID playerId, String summary) {
        delegate.recordCollectionEvent(playerId, summary);
    }

    public List<String> getCollectionsHistory(UUID playerId) {
        return delegate.getCollectionsHistory(playerId);
    }

    public Map<UUID, List<String>> getAllCollectionsHistory() {
        return delegate.getAllCollectionsHistory();
    }

    public String getCollectionStats(UUID playerId) {
        return delegate.getCollectionStats(playerId);
    }
}
