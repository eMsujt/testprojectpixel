package com.skyblock.collections;

/**
 * @deprecated Use {@link com.skyblock.core.model.CollectionCategory} instead.
 */
@Deprecated
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

    /**
     * @deprecated Use {@link com.skyblock.core.model.CollectionCategory#getDisplayName()}.
     */
    @Deprecated
    public String getDisplayName() {
        return displayName;
    }
}
