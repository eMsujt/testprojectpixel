package com.skyblock.core.shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Singleton managing shop inventory organised by {@link ShopCategory}.
 *
 * <p>Items are registered at startup and looked up by category at runtime.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class ShopManager {

    /** Top-level categories available in the SkyBlock shop. */
    public enum ShopCategory {
        WEAPONS,
        ARMOR,
        TOOLS,
        FOOD,
        BLOCKS,
        SEEDS
    }

    /** Named shop locations, each selling items from a specific set of categories. */
    public enum ShopType {
        VILLAGE_MERCHANT("Village Merchant", ShopCategory.FOOD, ShopCategory.SEEDS, ShopCategory.BLOCKS),
        WEAPONSMITH("Weaponsmith", ShopCategory.WEAPONS),
        ARMORSMITH("Armorsmith", ShopCategory.ARMOR),
        TOOLSMITH("Toolsmith", ShopCategory.TOOLS),
        GENERAL_STORE("General Store", ShopCategory.WEAPONS, ShopCategory.ARMOR, ShopCategory.TOOLS,
                ShopCategory.FOOD, ShopCategory.BLOCKS, ShopCategory.SEEDS);

        private final String displayName;
        private final Set<ShopCategory> categories;

        ShopType(String displayName, ShopCategory... categories) {
            this.displayName = displayName;
            this.categories = Collections.unmodifiableSet(EnumSet.copyOf(Arrays.asList(categories)));
        }

        public String getDisplayName() { return displayName; }
        public Set<ShopCategory> getCategories() { return categories; }
    }

    /**
     * An item that can be purchased from the shop.
     *
     * @param name     the display name shown to players
     * @param material the Bukkit Material key (e.g. "IRON_SWORD")
     * @param price    the coin cost, must not be negative
     */
    public record ShopItem(String name, String material, double price) {
        public ShopItem {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(material, "material");
            if (price < 0) {
                throw new IllegalArgumentException("price must not be negative, got " + price);
            }
        }
    }

    private static final ShopManager INSTANCE = new ShopManager();

    private final Map<ShopCategory, List<ShopItem>> items = new EnumMap<>(ShopCategory.class);

    private ShopManager() {
        for (ShopCategory category : ShopCategory.values()) {
            items.put(category, new ArrayList<>());
        }
        registerDefaults();
    }

    /**
     * Returns the single shared {@code ShopManager} instance.
     *
     * @return the singleton instance
     */
    public static ShopManager getInstance() {
        return INSTANCE;
    }

    /**
     * Registers a new item in the given category.
     *
     * @param category the category to add to
     * @param item     the item to register
     */
    public void register(ShopCategory category, ShopItem item) {
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(item, "item");
        items.get(category).add(item);
    }

    /**
     * Returns an unmodifiable view of all items in the given category.
     *
     * @param category the category to query
     * @return the items in that category, never {@code null}
     */
    public List<ShopItem> getItems(ShopCategory category) {
        Objects.requireNonNull(category, "category");
        return Collections.unmodifiableList(items.get(category));
    }

    /**
     * Returns the first item in {@code category} whose name matches {@code name}
     * (case-insensitive), or {@code null} if not found.
     *
     * @param category the category to search
     * @param name     the display name to look up
     * @return the matching item, or {@code null}
     */
    public ShopItem findByName(ShopCategory category, String name) {
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(name, "name");
        for (ShopItem item : items.get(category)) {
            if (item.name().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Returns all items sold by the given shop type (union across its categories).
     *
     * @param type the shop type to query
     * @return combined unmodifiable list of items
     */
    public List<ShopItem> getItems(ShopType type) {
        Objects.requireNonNull(type, "type");
        List<ShopItem> result = new ArrayList<>();
        for (ShopCategory category : type.getCategories()) {
            result.addAll(items.get(category));
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Returns the first item sold by {@code type} whose name matches {@code name}
     * (case-insensitive), or {@code null} if not found.
     *
     * @param type the shop type to search
     * @param name the display name to look up
     * @return the matching item, or {@code null}
     */
    public ShopItem findByName(ShopType type, String name) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(name, "name");
        for (ShopCategory category : type.getCategories()) {
            ShopItem found = findByName(category, name);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /** Clears all items from every category. */
    public void clear() {
        for (ShopCategory category : ShopCategory.values()) {
            items.get(category).clear();
        }
    }

    private void registerDefaults() {
        // Weapons
        register(ShopCategory.WEAPONS, new ShopItem("Wooden Sword", "WOODEN_SWORD", 10));
        register(ShopCategory.WEAPONS, new ShopItem("Stone Sword", "STONE_SWORD", 50));
        register(ShopCategory.WEAPONS, new ShopItem("Iron Sword", "IRON_SWORD", 200));
        register(ShopCategory.WEAPONS, new ShopItem("Golden Sword", "GOLDEN_SWORD", 150));
        register(ShopCategory.WEAPONS, new ShopItem("Diamond Sword", "DIAMOND_SWORD", 1000));

        // Armor
        register(ShopCategory.ARMOR, new ShopItem("Leather Helmet", "LEATHER_HELMET", 30));
        register(ShopCategory.ARMOR, new ShopItem("Iron Chestplate", "IRON_CHESTPLATE", 300));
        register(ShopCategory.ARMOR, new ShopItem("Iron Leggings", "IRON_LEGGINGS", 250));
        register(ShopCategory.ARMOR, new ShopItem("Iron Boots", "IRON_BOOTS", 150));
        register(ShopCategory.ARMOR, new ShopItem("Diamond Chestplate", "DIAMOND_CHESTPLATE", 2000));

        // Tools
        register(ShopCategory.TOOLS, new ShopItem("Wooden Pickaxe", "WOODEN_PICKAXE", 10));
        register(ShopCategory.TOOLS, new ShopItem("Stone Pickaxe", "STONE_PICKAXE", 50));
        register(ShopCategory.TOOLS, new ShopItem("Iron Pickaxe", "IRON_PICKAXE", 200));
        register(ShopCategory.TOOLS, new ShopItem("Iron Axe", "IRON_AXE", 200));
        register(ShopCategory.TOOLS, new ShopItem("Iron Shovel", "IRON_SHOVEL", 150));
        register(ShopCategory.TOOLS, new ShopItem("Fishing Rod", "FISHING_ROD", 100));

        // Food
        register(ShopCategory.FOOD, new ShopItem("Bread", "BREAD", 5));
        register(ShopCategory.FOOD, new ShopItem("Cooked Beef", "COOKED_BEEF", 10));
        register(ShopCategory.FOOD, new ShopItem("Baked Potato", "BAKED_POTATO", 4));
        register(ShopCategory.FOOD, new ShopItem("Cookie", "COOKIE", 2));
        register(ShopCategory.FOOD, new ShopItem("Pumpkin Pie", "PUMPKIN_PIE", 6));

        // Blocks
        register(ShopCategory.BLOCKS, new ShopItem("Cobblestone", "COBBLESTONE", 1));
        register(ShopCategory.BLOCKS, new ShopItem("Oak Log", "OAK_LOG", 5));
        register(ShopCategory.BLOCKS, new ShopItem("Sand", "SAND", 2));
        register(ShopCategory.BLOCKS, new ShopItem("Gravel", "GRAVEL", 2));
        register(ShopCategory.BLOCKS, new ShopItem("Glass", "GLASS", 3));

        // Seeds
        register(ShopCategory.SEEDS, new ShopItem("Wheat Seeds", "WHEAT_SEEDS", 1));
        register(ShopCategory.SEEDS, new ShopItem("Carrot", "CARROT", 2));
        register(ShopCategory.SEEDS, new ShopItem("Potato", "POTATO", 2));
        register(ShopCategory.SEEDS, new ShopItem("Melon Seeds", "MELON_SEEDS", 3));
        register(ShopCategory.SEEDS, new ShopItem("Pumpkin Seeds", "PUMPKIN_SEEDS", 3));
        register(ShopCategory.SEEDS, new ShopItem("Nether Wart", "NETHER_WART", 10));
    }
}
