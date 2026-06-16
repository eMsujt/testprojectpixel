package com.skyblock.core.manager;

/**
 * @deprecated Use {@link com.skyblock.core.collections.manager.CollectionManager} instead.
 */
@Deprecated
public final class CollectionManager {

    private CollectionManager() {}

    public static com.skyblock.core.collections.manager.CollectionManager getInstance() {
        return com.skyblock.core.collections.manager.CollectionManager.getInstance();
    }
}
