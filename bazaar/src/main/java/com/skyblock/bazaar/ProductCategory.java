package com.skyblock.bazaar;

/**
 * The top-level categories under which bazaar products are grouped.
 *
 * <p>Each category carries its human-readable display name as shown in the
 * bazaar menu.</p>
 */
public enum ProductCategory {

    FARMING("Farming"),
    MINING("Mining"),
    COMBAT("Combat"),
    WOODS_AND_FISHES("Woods & Fishes"),
    ODDITIES("Oddities");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable name of this category.
     *
     * @return the display name, e.g. {@code "Woods & Fishes"}
     */
    public String getDisplayName() {
        return displayName;
    }
}
