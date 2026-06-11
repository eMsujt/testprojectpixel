package com.skyblock.shops;

/**
 * The categories of items sold in SkyBlock shops, in display order.
 */
public enum ShopCategory {

    WEAPONS("Weapons", 0),
    ARMOR("Armor", 1),
    TOOLS("Tools", 2),
    CONSUMABLES("Consumables", 3),
    RESOURCES("Resources", 4),
    BLOCKS("Blocks", 5),
    MISC("Misc", 6);

    private final String displayName;
    private final int displayOrder;

    ShopCategory(String displayName, int displayOrder) {
        this.displayName = displayName;
        this.displayOrder = displayOrder;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }
}
