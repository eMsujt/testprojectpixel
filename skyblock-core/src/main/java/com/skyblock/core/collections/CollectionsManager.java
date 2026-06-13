package com.skyblock.core.collections;

import java.io.File;
import java.util.UUID;

/**
 * Singleton facade over {@link CollectionManager}.
 *
 * <p>Exposes the same item-tracking / tier API under the {@code CollectionsManager}
 * name used by other modules, delegating every call to the underlying
 * {@link CollectionManager} singleton so there is a single source of truth for
 * collection data.</p>
 */
public final class CollectionsManager {

    /** Canonical collection categories used by this facade. */
    public enum CollectionCategory {
        FARMING("Farming"),
        MINING("Mining"),
        COMBAT("Combat"),
        FORAGING("Foraging"),
        FISHING("Fishing");

        private final String displayName;

        CollectionCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        /** Maps this value to the underlying {@link CollectionManager.CollectionCategory}. */
        public CollectionManager.CollectionCategory toCategory() {
            return CollectionManager.CollectionCategory.valueOf(this.name());
        }
    }

    private static final CollectionsManager INSTANCE = new CollectionsManager();

    private final CollectionManager delegate = CollectionManager.getInstance();

    private CollectionsManager() {
    }

    public static CollectionsManager getInstance() {
        return INSTANCE;
    }

    public long addItems(UUID playerId, CollectionManager.Collection collection, long amount) {
        return delegate.addItems(playerId, collection, amount);
    }

    public long getItems(UUID playerId, CollectionManager.Collection collection) {
        return delegate.getItems(playerId, collection);
    }

    public int getTier(UUID playerId, CollectionManager.Collection collection) {
        return delegate.getTier(playerId, collection);
    }

    public long getItemsToNextTier(UUID playerId, CollectionManager.Collection collection) {
        return delegate.getItemsToNextTier(playerId, collection);
    }

    public boolean reset(UUID playerId) {
        return delegate.reset(playerId);
    }

    public void load(File dataFolder) {
        delegate.load(dataFolder);
    }

    public void save(File dataFolder) {
        delegate.save(dataFolder);
    }
}
