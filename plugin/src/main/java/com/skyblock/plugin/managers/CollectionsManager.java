package com.skyblock.plugin.managers;

import java.io.File;
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

    public long getCollectionCount(UUID playerId, String collection) {
        com.skyblock.core.model.Collection c = com.skyblock.core.model.Collection.parse(collection);
        return c == null ? 0L : delegate.getItems(playerId, c);
    }

    public void addCollectionCount(UUID playerId, String collection, long amount) {
        delegate.addItems(playerId, collection, amount);
    }

    public void setCollectionCount(UUID playerId, String collection, long amount) {
        delegate.reset(playerId);
        delegate.addItems(playerId, collection, amount);
    }

    public Map<String, Long> getCollectionCounts(UUID playerId) {
        Map<String, Long> result = new HashMap<>();
        for (Map.Entry<com.skyblock.core.model.Collection, Long> e : delegate.getAll(playerId).entrySet()) {
            result.put(e.getKey().itemKey, e.getValue());
        }
        return result;
    }

    public int getCollectionMilestone(UUID playerId, String collection) {
        com.skyblock.core.model.Collection c = com.skyblock.core.model.Collection.parse(collection);
        return c == null ? 0 : delegate.getTier(playerId, c);
    }

    public void setCollectionMilestone(UUID playerId, String collection, int tier) {
        // tier is now computed dynamically from count — no-op
    }

    public Map<String, Integer> getCollectionMilestones(UUID playerId) {
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<com.skyblock.core.model.Collection, Long> e : delegate.getAll(playerId).entrySet()) {
            result.put(e.getKey().itemKey, delegate.getTier(playerId, e.getKey()));
        }
        return result;
    }

    public void recordUnlock(UUID playerId, String summary) {
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

    public void load(File dataFolder) {
        delegate.load(dataFolder);
    }

    public void save(File dataFolder) {
        delegate.save(dataFolder);
    }
}
