package com.skyblock.plugin.manager;

import java.util.Collections;
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

    public void addCount(UUID playerId, String key, long amount) {
        delegate.addItems(playerId, key, amount);
    }

    public long getCount(UUID playerId, String key) {
        com.skyblock.core.model.Collection c = com.skyblock.core.model.Collection.parse(key);
        return c == null ? 0L : delegate.getItems(playerId, c);
    }

    public int getTier(UUID playerId, String collectionId) {
        com.skyblock.core.model.Collection c = com.skyblock.core.model.Collection.parse(collectionId);
        return c == null ? 0 : delegate.getTier(playerId, c);
    }

    public Map<String, Object> getCollections() {
        return Collections.emptyMap();
    }
}
