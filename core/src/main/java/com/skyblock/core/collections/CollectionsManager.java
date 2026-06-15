package com.skyblock.core.collections;

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

    public long getAmount(UUID uuid, String collection) {
        com.skyblock.core.model.Collection c = com.skyblock.core.model.Collection.parse(collection);
        return c == null ? 0L : delegate.getItems(uuid, c);
    }

    public void addAmount(UUID uuid, String collection, long amount) {
        delegate.addItems(uuid, collection, Math.max(0, amount));
    }

    public int getTier(UUID uuid, String collection) {
        com.skyblock.core.model.Collection c = com.skyblock.core.model.Collection.parse(collection);
        return c == null ? 0 : delegate.getTier(uuid, c);
    }

    public void setTier(UUID uuid, String collection, int tier) {
        // tier is computed dynamically from count — no-op
    }

    public Map<String, Long> getCollectionAmounts(UUID uuid) {
        java.util.Map<String, Long> result = new java.util.HashMap<>();
        for (Map.Entry<com.skyblock.core.model.Collection, Long> e : delegate.getAll(uuid).entrySet()) {
            result.put(e.getKey().itemKey, e.getValue());
        }
        return java.util.Collections.unmodifiableMap(result);
    }

    public Map<String, Integer> getCollectionTiers(UUID uuid) {
        java.util.Map<String, Integer> result = new java.util.HashMap<>();
        for (Map.Entry<com.skyblock.core.model.Collection, Long> e : delegate.getAll(uuid).entrySet()) {
            result.put(e.getKey().itemKey, delegate.getTier(uuid, e.getKey()));
        }
        return java.util.Collections.unmodifiableMap(result);
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

    public String getCollectionStats(UUID uuid) {
        return delegate.getCollectionStats(uuid);
    }
}
