package com.skyblock.plugin.collections;

import org.bukkit.plugin.java.JavaPlugin;

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

    /** No-op — event-based persistence superseded by canonical load/save. */
    public void register(JavaPlugin owningPlugin) {}

    public int addCollection(UUID playerId, String collection, long amount) {
        int tierBefore = delegate.getTier(playerId, com.skyblock.core.model.Collection.parse(collection) != null
                ? com.skyblock.core.model.Collection.parse(collection)
                : com.skyblock.core.model.Collection.COBBLESTONE);
        delegate.addItems(playerId, collection, amount);
        int tierAfter = delegate.getTier(playerId, com.skyblock.core.model.Collection.parse(collection) != null
                ? com.skyblock.core.model.Collection.parse(collection)
                : com.skyblock.core.model.Collection.COBBLESTONE);
        return tierAfter - tierBefore;
    }

    public int getTier(UUID playerId, String collection) {
        com.skyblock.core.model.Collection c = com.skyblock.core.model.Collection.parse(collection);
        return c == null ? 0 : delegate.getTier(playerId, c);
    }

    public long getCollection(UUID playerId, String collection) {
        com.skyblock.core.model.Collection c = com.skyblock.core.model.Collection.parse(collection);
        return c == null ? 0L : delegate.getItems(playerId, c);
    }

    public Map<String, Long> getCollections(UUID playerId) {
        Map<String, Long> result = new java.util.HashMap<>();
        for (Map.Entry<com.skyblock.core.model.Collection, Long> e : delegate.getAll(playerId).entrySet()) {
            result.put(e.getKey().itemKey, e.getValue());
        }
        return result;
    }
}
