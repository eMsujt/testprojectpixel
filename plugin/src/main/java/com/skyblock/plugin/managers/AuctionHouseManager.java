package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class AuctionHouseManager {

    public record AuctionListing(UUID id, UUID seller, String itemName, int quantity, double startingBid,
                                 double currentBid, UUID highestBidder, long endTime) {}

    private static final AuctionHouseManager INSTANCE = new AuctionHouseManager();

    private final Map<UUID, List<AuctionListing>> active = new HashMap<>();
    private final Map<UUID, List<String>> bidHistory = new HashMap<>();
    private final Map<UUID, List<String>> auctionHistory = new HashMap<>();
    private final Map<String, List<String[]>> bids = new HashMap<>();

    private AuctionHouseManager() {}

    public static AuctionHouseManager getInstance() {
        return INSTANCE;
    }

    public List<AuctionListing> getListings(UUID sellerId) {
        return active.computeIfAbsent(sellerId, k -> new ArrayList<>());
    }

    public void addListing(UUID sellerId, AuctionListing listing) {
        getListings(sellerId).add(listing);
        recordAuctionEvent(sellerId, "Listed " + listing.itemName() + " x" + listing.quantity() + " starting at " + listing.startingBid() + " coins");
    }

    public boolean removeListing(UUID sellerId, UUID listingId) {
        List<AuctionListing> listings = active.get(sellerId);
        if (listings == null) {
            return false;
        }
        return listings.removeIf(l -> l.id().equals(listingId));
    }

    public List<AuctionListing> getAllListings() {
        List<AuctionListing> all = new ArrayList<>();
        for (List<AuctionListing> listings : active.values()) {
            all.addAll(listings);
        }
        return Collections.unmodifiableList(all);
    }

    public void placeBid(UUID sellerId, UUID listingId, UUID bidderId, double amount) {
        List<AuctionListing> listings = active.get(sellerId);
        if (listings == null) {
            return;
        }
        for (int i = 0; i < listings.size(); i++) {
            AuctionListing existing = listings.get(i);
            if (existing.id().equals(listingId)) {
                listings.set(i, new AuctionListing(
                        existing.id(),
                        existing.seller(),
                        existing.itemName(),
                        existing.quantity(),
                        existing.startingBid(),
                        amount,
                        bidderId,
                        existing.endTime()
                ));
                return;
            }
        }
    }

    public boolean placeBid(UUID bidder, String itemId, double amount) {
        for (List<AuctionListing> listings : active.values()) {
            for (int i = 0; i < listings.size(); i++) {
                AuctionListing existing = listings.get(i);
                if (existing.itemName().equals(itemId)) {
                    if (amount <= existing.currentBid()) {
                        return false;
                    }
                    listings.set(i, new AuctionListing(
                            existing.id(),
                            existing.seller(),
                            existing.itemName(),
                            existing.quantity(),
                            existing.startingBid(),
                            amount,
                            bidder,
                            existing.endTime()
                    ));
                    return true;
                }
            }
        }
        return false;
    }

    public void recordBid(UUID bidder, String itemId, int bidAmount) {
        List<String> history = bidHistory.computeIfAbsent(bidder, k -> new ArrayList<>());
        history.add(0, itemId + ":" + bidAmount);
        bids.computeIfAbsent(itemId, k -> new ArrayList<>()).add(new String[]{bidder.toString(), String.valueOf(bidAmount)});
    }

    public List<String> getBidHistory(UUID bidder) {
        return bidHistory.getOrDefault(bidder, Collections.emptyList());
    }

    public Map<UUID, List<String>> getAllBidHistory() {
        return Collections.unmodifiableMap(bidHistory);
    }

    public List<String[]> getBids(String itemId) {
        return Collections.unmodifiableList(bids.getOrDefault(itemId, Collections.emptyList()));
    }

    public Map<String, List<String[]>> getAllBids() {
        return Collections.unmodifiableMap(bids);
    }

    public void recordAuctionEvent(UUID player, String summary) {
        auctionHistory.computeIfAbsent(player, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getAuctionHistory(UUID player) {
        return Collections.unmodifiableList(
                auctionHistory.getOrDefault(player, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllAuctionHistory() {
        return Collections.unmodifiableMap(auctionHistory);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "auction_house.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        active.clear();
        bidHistory.clear();
        auctionHistory.clear();
        bids.clear();
        if (cfg.isConfigurationSection("bidHistory")) {
            for (String key : cfg.getConfigurationSection("bidHistory").getKeys(false)) {
                try {
                    UUID bidder = UUID.fromString(key);
                    List<String> entries = cfg.getStringList("bidHistory." + key);
                    bidHistory.put(bidder, new ArrayList<>(entries));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        if (cfg.isConfigurationSection("auctionHistory")) {
            for (String key : cfg.getConfigurationSection("auctionHistory").getKeys(false)) {
                try {
                    auctionHistory.put(UUID.fromString(key),
                            new ArrayList<>(cfg.getStringList("auctionHistory." + key)));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        if (cfg.isConfigurationSection("bids")) {
            for (String itemId : cfg.getConfigurationSection("bids").getKeys(false)) {
                List<String[]> pairs = new ArrayList<>();
                List<?> raw = cfg.getList("bids." + itemId, Collections.emptyList());
                for (Object entry : raw) {
                    if (entry instanceof List) {
                        List<?> pair = (List<?>) entry;
                        if (pair.size() == 2) {
                            pairs.add(new String[]{String.valueOf(pair.get(0)), String.valueOf(pair.get(1))});
                        }
                    }
                }
                bids.put(itemId, pairs);
            }
        }
        if (cfg.isConfigurationSection("active")) {
            for (String sellerKey : cfg.getConfigurationSection("active").getKeys(false)) {
                try {
                    UUID sellerId = UUID.fromString(sellerKey);
                    List<AuctionListing> listings = new ArrayList<>();
                    String sellerPath = "active." + sellerKey;
                    if (cfg.isConfigurationSection(sellerPath)) {
                        for (String listingKey : cfg.getConfigurationSection(sellerPath).getKeys(false)) {
                            try {
                                UUID id = UUID.fromString(listingKey);
                                String prefix = sellerPath + "." + listingKey + ".";
                                String itemName = cfg.getString(prefix + "itemName", "");
                                int quantity = cfg.getInt(prefix + "quantity", 1);
                                double startingBid = cfg.getDouble(prefix + "startingBid");
                                double currentBid = cfg.getDouble(prefix + "currentBid");
                                String highestBidderStr = cfg.getString(prefix + "highestBidder");
                                UUID highestBidder = highestBidderStr != null ? UUID.fromString(highestBidderStr) : null;
                                long endTime = cfg.getLong(prefix + "endTime");
                                listings.add(new AuctionListing(id, sellerId, itemName, quantity, startingBid, currentBid, highestBidder, endTime));
                            } catch (IllegalArgumentException ignored) {}
                        }
                    }
                    active.put(sellerId, listings);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "auction_house.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, List<AuctionListing>> entry : active.entrySet()) {
            String sellerPath = "active." + entry.getKey().toString();
            for (AuctionListing listing : entry.getValue()) {
                String prefix = sellerPath + "." + listing.id().toString() + ".";
                cfg.set(prefix + "itemName", listing.itemName());
                cfg.set(prefix + "quantity", listing.quantity());
                cfg.set(prefix + "startingBid", listing.startingBid());
                cfg.set(prefix + "currentBid", listing.currentBid());
                if (listing.highestBidder() != null) {
                    cfg.set(prefix + "highestBidder", listing.highestBidder().toString());
                }
                cfg.set(prefix + "endTime", listing.endTime());
            }
        }
        for (Map.Entry<UUID, List<String>> entry : bidHistory.entrySet()) {
            cfg.set("bidHistory." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, List<String>> entry : auctionHistory.entrySet()) {
            cfg.set("auctionHistory." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<String, List<String[]>> entry : bids.entrySet()) {
            List<List<String>> pairs = new ArrayList<>();
            for (String[] pair : entry.getValue()) {
                List<String> p = new ArrayList<>();
                p.add(pair[0]);
                p.add(pair[1]);
                pairs.add(p);
            }
            cfg.set("bids." + entry.getKey(), pairs);
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save auction_house.yml", e);
        }
    }
}
