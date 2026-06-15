package com.skyblock.core.collection;

import com.skyblock.core.model.Collection;
import com.skyblock.core.model.CollectionCategory;

import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.CollectionManager} instead.
 */
@Deprecated
public final class CollectionManager {

    private static final CollectionManager INSTANCE = new CollectionManager();
    private final com.skyblock.core.manager.CollectionManager delegate =
            com.skyblock.core.manager.CollectionManager.getInstance();

    private CollectionManager() {}

    public static CollectionManager getInstance() {
        return INSTANCE;
    }

    public long addItems(UUID playerId, Collection collection, long amount) {
        return delegate.addItems(playerId, collection, amount);
    }

    public long getItems(UUID playerId, Collection collection) {
        return delegate.getItems(playerId, collection);
    }

    public int getTier(UUID playerId, Collection collection) {
        return delegate.getTier(playerId, collection);
    }

    public long getItemsToNextTier(UUID playerId, Collection collection) {
        return delegate.getItemsToNextTier(playerId, collection);
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
}
