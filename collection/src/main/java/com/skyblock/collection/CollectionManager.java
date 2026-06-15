package com.skyblock.collection;

import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.CollectionManager} instead.
 */
@Deprecated
public final class CollectionManager {

    private final com.skyblock.core.manager.CollectionManager delegate =
            com.skyblock.core.manager.CollectionManager.getInstance();

    public static final int MAX_TIER = com.skyblock.core.manager.CollectionManager.MAX_TIER;

    public long addItems(UUID playerId, String collectionId, long amount) {
        return delegate.addItems(playerId, collectionId, amount);
    }

    public long getItems(UUID playerId, String collectionId) {
        com.skyblock.core.model.Collection c = com.skyblock.core.model.Collection.parse(collectionId);
        return c == null ? 0L : delegate.getItems(playerId, c);
    }

    public int getTier(UUID playerId, String collectionId) {
        com.skyblock.core.model.Collection c = com.skyblock.core.model.Collection.parse(collectionId);
        return c == null ? 0 : delegate.getTier(playerId, c);
    }

    public long getItemsToNextTier(UUID playerId, String collectionId) {
        com.skyblock.core.model.Collection c = com.skyblock.core.model.Collection.parse(collectionId);
        return c == null ? 0L : delegate.getItemsToNextTier(playerId, c);
    }

    public boolean reset(UUID playerId) {
        return delegate.reset(playerId);
    }
}
