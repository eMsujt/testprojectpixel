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

    private final Map<UUID, AuctionItem> items = new HashMap<>();

    public UUID addItem(UUID seller, String itemName, long price) {
        UUID id = UUID.randomUUID();
        items.put(id, new AuctionItem(seller, itemName, price));
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
        return true;
    }

    public List<AuctionItem> getActiveItems() {
        return Collections.unmodifiableList(new ArrayList<>(items.values()));
    }

    public Map<UUID, AuctionItem> getItems() {
        return Collections.unmodifiableMap(items);
    }
}
