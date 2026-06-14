package com.skyblock.core.auctionhouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AuctionHouseManager {

    public record AuctionItem(UUID seller, String itemName, long price) {
        public AuctionItem {
            Objects.requireNonNull(seller, "seller");
            Objects.requireNonNull(itemName, "itemName");
            if (price < 0) throw new IllegalArgumentException("price must not be negative");
        }
    }

    /** Maps each auction category name to its metadata: {maxListings, taxPercent}. */
    public static final Map<String, int[]> AUCTION_CATEGORY_DATA;

    static {
        Map<String, int[]> m = new HashMap<>();
        m.put("Weapons",      new int[]{16, 1});
        m.put("Swords",       new int[]{16, 1});
        m.put("Bows",         new int[]{16, 1});
        m.put("Wands",        new int[]{16, 1});
        m.put("Fishing Rods", new int[]{16, 1});
        m.put("Armor",        new int[]{16, 1});
        m.put("Helmets",      new int[]{16, 1});
        m.put("Chestplates",  new int[]{16, 1});
        m.put("Leggings",     new int[]{16, 1});
        m.put("Boots",        new int[]{16, 1});
        m.put("Accessories",  new int[]{16, 1});
        m.put("Talismans",    new int[]{16, 1});
        m.put("Rings",        new int[]{16, 1});
        m.put("Orbs",         new int[]{16, 1});
        m.put("Necklaces",    new int[]{16, 1});
        m.put("Consumables",  new int[]{16, 1});
        m.put("Potions",      new int[]{16, 1});
        m.put("Scrolls",      new int[]{16, 1});
        m.put("Arrows",       new int[]{16, 1});
        m.put("Blocks",       new int[]{16, 1});
        m.put("Pets",         new int[]{16, 1});
        m.put("Misc",         new int[]{16, 1});
        AUCTION_CATEGORY_DATA = Collections.unmodifiableMap(m);
    }

    private final Map<UUID, AuctionItem> items = new HashMap<>();
    private final Map<UUID, List<String>> auctionHistory = new HashMap<>();

    public void recordAuctionEvent(UUID player, String summary) {
        auctionHistory.computeIfAbsent(player, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getAuctionHistory(UUID player) {
        return Collections.unmodifiableList(auctionHistory.getOrDefault(player, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllAuctionHistory() {
        return Collections.unmodifiableMap(auctionHistory);
    }

    public UUID addItem(UUID seller, String itemName, long price) {
        UUID id = UUID.randomUUID();
        items.put(id, new AuctionItem(seller, itemName, price));
        recordAuctionEvent(seller, "Listed " + itemName + " for " + price + " coins");
        return id;
    }

    public AuctionItem getItem(UUID id) {
        return items.get(id);
    }

    public boolean cancelItem(UUID id, UUID seller) {
        AuctionItem item = items.get(id);
        if (item == null || !item.seller().equals(seller)) return false;
        items.remove(id);
        return true;
    }

    public boolean purchase(UUID id, UUID buyer) {
        AuctionItem item = items.get(id);
        if (item == null || item.seller().equals(buyer)) return false;
        items.remove(id);
        recordAuctionEvent(buyer, "Purchased " + item.itemName() + " for " + item.price() + " coins");
        return true;
    }

    public List<AuctionItem> getActiveItems() {
        return Collections.unmodifiableList(new ArrayList<>(items.values()));
    }

    public Map<UUID, AuctionItem> getItems() {
        return Collections.unmodifiableMap(items);
    }
}
