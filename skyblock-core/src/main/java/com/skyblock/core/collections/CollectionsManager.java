package com.skyblock.core.collections;

import com.skyblock.core.model.Collection;
import com.skyblock.core.model.CollectionCategory;

import java.io.File;
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

    public long addItems(UUID playerId, Collection type, long amount) {
        return delegate.addItems(playerId, type, amount);
    }

    public long getItems(UUID playerId, Collection type) {
        return delegate.getItems(playerId, type);
    }

    public int getTier(UUID playerId, Collection type) {
        return delegate.getTier(playerId, type);
    }

    public long getItemsToNextTier(UUID playerId, Collection type) {
        return delegate.getItemsToNextTier(playerId, type);
    }

    public Map<Collection, Long> getAll(UUID playerId) {
        return delegate.getAll(playerId);
    }

    public long getTotalForCategory(UUID playerId, CollectionCategory category) {
        return delegate.getTotalForCategory(playerId, category);
    }

    public boolean reset(UUID playerId) {
        return delegate.reset(playerId);
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

    public void load(File dataFolder) {
        delegate.load(dataFolder);
    }

    public void save(File dataFolder) {
        delegate.save(dataFolder);
    }
}
