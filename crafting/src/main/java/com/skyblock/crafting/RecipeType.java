package com.skyblock.crafting;

/**
 * The kinds of recipes a player can use to create items in SkyBlock.
 *
 * <p>Each type carries the human-readable display name shown in recipe
 * menus. The type determines which station or mechanic processes a
 * {@link CraftingRecipe}, e.g. the crafting table or the forge.</p>
 */
public enum RecipeType {

    CRAFTING("Crafting Table"),
    FORGE("Forge"),
    SMELTING("Furnace"),
    BREWING("Brewing Stand"),
    ANVIL("Anvil"),
    TRADE("Trade");

    private final String displayName;

    RecipeType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable name shown in recipe menus.
     *
     * @return the display name, e.g. {@code "Crafting Table"}
     */
    public String getDisplayName() {
        return displayName;
    }
}
