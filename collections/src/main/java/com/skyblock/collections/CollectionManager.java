package com.skyblock.collections;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.CollectionManager} instead.
 */
@Deprecated
public final class CollectionManager {

    private static final CollectionManager INSTANCE = new CollectionManager();

    private CollectionManager() {}

    public static CollectionManager getInstance() {
        return INSTANCE;
    }

    public long addProgress(UUID playerId, String collectionId, long amount) {
        return com.skyblock.core.manager.CollectionManager.getInstance().addItems(playerId, collectionId, amount);
    }

    public long getProgress(UUID playerId, String collectionId) {
        com.skyblock.core.model.Collection c = com.skyblock.core.model.Collection.parse(collectionId);
        return c == null ? 0L : com.skyblock.core.manager.CollectionManager.getInstance().getItems(playerId, c);
    }

    public Map<String, Long> getProgress(UUID playerId) {
        Map<String, Long> result = new HashMap<>();
        for (Map.Entry<com.skyblock.core.model.Collection, Long> e :
                com.skyblock.core.manager.CollectionManager.getInstance().getAll(playerId).entrySet()) {
            result.put(e.getKey().itemKey, e.getValue());
        }
        return Collections.unmodifiableMap(result);
    }

    public void resetProgress(UUID playerId) {
        com.skyblock.core.manager.CollectionManager.getInstance().reset(playerId);
    }
}
