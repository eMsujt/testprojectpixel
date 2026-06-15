package com.skyblock.shop;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @deprecated Use {@link com.skyblock.core.manager.ShopManager} (the canonical singleton).
 */
@Deprecated
public final class ShopManager {

    /** The category of items a shop trades in. */
    public enum ShopType {
        /** Crops, seeds, and other farm produce. */
        FARMING,
        /** Ores, gemstones, and mining materials. */
        MINING,
        /** Weapons, armor, and combat consumables. */
        COMBAT,
        /** Logs and foraged materials. */
        FORAGING,
        /** Fish and fishing gear. */
        FISHING,
        /** Potions and brewing ingredients. */
        ALCHEMY,
        /** Building blocks and decorative items. */
        BUILDING
    }

    /** A single item listing in a shop catalog. */
    public static final class ShopEntry {

        private final String itemId;
        private final double buyPrice;
        private final double sellPrice;

        private ShopEntry(String itemId, double buyPrice, double sellPrice) {
            this.itemId = itemId;
            this.buyPrice = buyPrice;
            this.sellPrice = sellPrice;
        }

        /** Returns the id of the listed item. */
        public String getItemId() {
            return itemId;
        }

        /** Returns the price a player pays to buy one of this item. */
        public double getBuyPrice() {
            return buyPrice;
        }

        /** Returns the price a player receives for selling one of this item. */
        public double getSellPrice() {
            return sellPrice;
        }
    }

    private static final com.skyblock.core.manager.ShopManager DELEGATE =
            com.skyblock.core.manager.ShopManager.getInstance();

    private final Map<ShopType, ConcurrentHashMap<String, ShopEntry>> catalogs;

    public ShopManager() {
        Map<ShopType, ConcurrentHashMap<String, ShopEntry>> map = new EnumMap<>(ShopType.class);
        for (ShopType type : ShopType.values()) {
            map.put(type, new ConcurrentHashMap<>());
        }
        this.catalogs = Collections.unmodifiableMap(map);
    }

    /**
     * Adds or replaces an item listing in the given shop's catalog.
     *
     * @param type      the shop to list the item in
     * @param itemId    the item's id, non-blank
     * @param buyPrice  the buy price in coins, must be positive
     * @param sellPrice the sell price in coins, must be non-negative and at
     *                  most the buy price
     * @return the created listing
     */
    public ShopEntry addEntry(ShopType type, String itemId, double buyPrice, double sellPrice) {
        requireType(type);
        String validated = requireItemId(itemId);
        if (buyPrice <= 0 || !Double.isFinite(buyPrice)) {
            throw new IllegalArgumentException("buyPrice must be positive");
        }
        if (sellPrice < 0 || !Double.isFinite(sellPrice)) {
            throw new IllegalArgumentException("sellPrice must be non-negative");
        }
        if (sellPrice > buyPrice) {
            throw new IllegalArgumentException("sellPrice must be at most the buy price");
        }
        ShopEntry entry = new ShopEntry(validated, buyPrice, sellPrice);
        catalogs.get(type).put(validated, entry);
        // write-through: keep canonical in sync (shop type used as shop id)
        List<com.skyblock.core.manager.ShopManager.ShopEntry> canonical = new java.util.ArrayList<>();
        for (java.util.Map.Entry<String, ShopEntry> e : catalogs.get(type).entrySet()) {
            canonical.add(new com.skyblock.core.manager.ShopManager.ShopEntry(
                    e.getKey(), (long) e.getValue().getBuyPrice(), (long) e.getValue().getSellPrice()));
        }
        DELEGATE.registerShop(type.name(), type.name(), canonical);
        return entry;
    }

    /**
     * Returns the listing for the given item in the given shop, if any.
     *
     * @param type   the shop to look in
     * @param itemId the item's id
     * @return the listing, or empty if the item is not listed
     */
    public Optional<ShopEntry> getEntry(ShopType type, String itemId) {
        requireType(type);
        return Optional.ofNullable(catalogs.get(type).get(requireItemId(itemId)));
    }

    /**
     * Removes an item listing from the given shop's catalog.
     *
     * @param type   the shop to remove the listing from
     * @param itemId the item's id
     * @return {@code true} if the item was listed and is now removed
     */
    public boolean removeEntry(ShopType type, String itemId) {
        requireType(type);
        return catalogs.get(type).remove(requireItemId(itemId)) != null;
    }

    /**
     * Returns the total cost of buying the given amount of an item from the
     * given shop.
     *
     * @param type   the shop to buy from
     * @param itemId the item's id
     * @param amount the number of items to buy, must be positive
     * @return the total buy cost in coins
     * @throws IllegalArgumentException if the item is not listed in the shop
     */
    public double getBuyCost(ShopType type, String itemId, int amount) {
        return requireEntry(type, itemId).buyPrice * requireAmount(amount);
    }

    /**
     * Returns the total payout for selling the given amount of an item to the
     * given shop.
     *
     * @param type   the shop to sell to
     * @param itemId the item's id
     * @param amount the number of items to sell, must be positive
     * @return the total sell payout in coins
     * @throws IllegalArgumentException if the item is not listed in the shop
     */
    public double getSellValue(ShopType type, String itemId, int amount) {
        return requireEntry(type, itemId).sellPrice * requireAmount(amount);
    }

    /**
     * Returns an unmodifiable view of the given shop's catalog keyed by item id.
     *
     * @param type the shop whose catalog to return
     * @return the shop's listings, empty if it has none
     */
    public Map<String, ShopEntry> getCatalog(ShopType type) {
        requireType(type);
        return Collections.unmodifiableMap(catalogs.get(type));
    }

    private ShopEntry requireEntry(ShopType type, String itemId) {
        requireType(type);
        ShopEntry entry = catalogs.get(type).get(requireItemId(itemId));
        if (entry == null) {
            throw new IllegalArgumentException("item is not listed in the " + type + " shop");
        }
        return entry;
    }

    private static void requireType(ShopType type) {
        if (type == null) {
            throw new IllegalArgumentException("type must be non-null");
        }
    }

    private static String requireItemId(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("itemId must be non-blank");
        }
        return itemId.trim();
    }

    private static int requireAmount(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        return amount;
    }
}
