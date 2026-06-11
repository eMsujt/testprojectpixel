package com.skyblock.collections;

/**
 * Top-level groupings for SkyBlock collections.
 *
 * <p>Each category covers a distinct play-style. Materials are registered to
 * a category at startup via {@link CollectionRegistry}; ordinal order matches
 * the in-game tab ordering.</p>
 */
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
     * Returns the human-readable name shown in menus and chat.
     *
     * @return the display name, e.g. {@code "Farming"}
     */
    public String getDisplayName() {
        return displayName;
    }
}
