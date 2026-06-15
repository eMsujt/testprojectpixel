package com.skyblock.plugin.profile;

import java.util.Objects;
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

    public long incrementCollection(UUID playerId, String material, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(material, "material");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative, got " + amount);
        return delegate.addItems(playerId, material, amount);
    }

    public long getCollection(UUID playerId, String material) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(material, "material");
        com.skyblock.core.model.Collection c = com.skyblock.core.model.Collection.parse(material);
        return c == null ? 0L : delegate.getItems(playerId, c);
    }
}
