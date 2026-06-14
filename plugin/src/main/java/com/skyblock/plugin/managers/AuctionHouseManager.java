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

    public record AuctionListing(UUID id, String itemName, int quantity, double startingBid,
                                 double currentBid, UUID highestBidder, long endTime) {}

    private static final AuctionHouseManager INSTANCE = new AuctionHouseManager();

    private final Map<UUID, List<AuctionListing>> active = new HashMap<>();

    private AuctionHouseManager() {}

    public static AuctionHouseManager getInstance() {
        return INSTANCE;
    }

    public List<AuctionListing> getListings(UUID sellerId) {
        return active.computeIfAbsent(sellerId, k -> new ArrayList<>());
    }

    public void addListing(UUID sellerId, AuctionListing listing) {
        getListings(sellerId).add(listing);
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

    public void load(File dataFolder) {
        File file = new File(dataFolder, "auction_house.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        active.clear();
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
                                listings.add(new AuctionListing(id, itemName, quantity, startingBid, currentBid, highestBidder, endTime));
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
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save auction_house.yml", e);
        }
    }
}
