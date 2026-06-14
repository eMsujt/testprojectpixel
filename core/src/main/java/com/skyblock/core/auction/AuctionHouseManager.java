package com.skyblock.core.auction;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
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

    public String getAuctionHouseStats(UUID player) {
        List<String> history = auctionHistory.getOrDefault(player, Collections.emptyList());
        int auctionsCreated = 0;
        int itemsSold = 0;
        long totalCoins = 0;
        for (String entry : history) {
            if (entry.startsWith("Listed")) {
                auctionsCreated++;
            } else if (entry.startsWith("Purchased")) {
                itemsSold++;
            }
            int idx = entry.lastIndexOf(" coins");
            if (idx > 0) {
                String before = entry.substring(0, idx);
                int space = before.lastIndexOf(' ');
                if (space >= 0) {
                    try {
                        totalCoins += (long) Double.parseDouble(before.substring(space + 1));
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return "Auction Stats: Auctions Created: " + auctionsCreated + ", Items Sold: " + itemsSold + ", Total Coins: " + totalCoins;
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

    public void load(File dataFolder) {
        File file = new File(dataFolder, "auctionhouse.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        items.clear();
        auctionHistory.clear();
        if (cfg.isConfigurationSection("items")) {
            for (String key : cfg.getConfigurationSection("items").getKeys(false)) {
                try {
                    UUID id = UUID.fromString(key);
                    String itemName = cfg.getString("items." + key + ".itemName");
                    if (itemName == null) continue;
                    UUID seller = UUID.fromString(cfg.getString("items." + key + ".seller", ""));
                    long price = cfg.getLong("items." + key + ".price", 0L);
                    items.put(id, new AuctionItem(seller, itemName, price));
                } catch (IllegalArgumentException ignored) {
                    // skip malformed entries
                }
            }
        }
        if (cfg.isConfigurationSection("auctionHistory")) {
            for (String key : cfg.getConfigurationSection("auctionHistory").getKeys(false)) {
                try {
                    auctionHistory.put(UUID.fromString(key),
                            new ArrayList<>(cfg.getStringList("auctionHistory." + key)));
                } catch (IllegalArgumentException ignored) {
                    // skip malformed entries
                }
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "auctionhouse.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, AuctionItem> entry : items.entrySet()) {
            String key = "items." + entry.getKey().toString();
            AuctionItem item = entry.getValue();
            cfg.set(key + ".seller", item.seller().toString());
            cfg.set(key + ".itemName", item.itemName());
            cfg.set(key + ".price", item.price());
        }
        for (Map.Entry<UUID, List<String>> entry : auctionHistory.entrySet()) {
            cfg.set("auctionHistory." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save auctionhouse.yml", e);
        }
    }
}
