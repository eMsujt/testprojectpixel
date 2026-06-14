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

    public record AuctionListing(UUID id, UUID sellerId, String itemName, int quantity, double startingBid,
                                 double currentBid, UUID highestBidder, long endTime) {}

    private static final AuctionHouseManager INSTANCE = new AuctionHouseManager();

    private final Map<UUID, AuctionListing> listings = new HashMap<>();

    private AuctionHouseManager() {}

    public static AuctionHouseManager getInstance() {
        return INSTANCE;
    }

    public void addListing(AuctionListing listing) {
        listings.put(listing.id(), listing);
    }

    public AuctionListing getListing(UUID id) {
        return listings.get(id);
    }

    public boolean removeListing(UUID id) {
        return listings.remove(id) != null;
    }

    public List<AuctionListing> getAllListings() {
        return Collections.unmodifiableList(new ArrayList<>(listings.values()));
    }

    public void placeBid(UUID listingId, UUID bidderId, double amount) {
        AuctionListing existing = listings.get(listingId);
        if (existing == null) {
            return;
        }
        listings.put(listingId, new AuctionListing(
                existing.id(),
                existing.sellerId(),
                existing.itemName(),
                existing.quantity(),
                existing.startingBid(),
                amount,
                bidderId,
                existing.endTime()
        ));
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "auction_house.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        listings.clear();
        if (cfg.isConfigurationSection("listings")) {
            for (String key : cfg.getConfigurationSection("listings").getKeys(false)) {
                try {
                    UUID id = UUID.fromString(key);
                    String prefix = "listings." + key + ".";
                    UUID sellerId = UUID.fromString(cfg.getString(prefix + "sellerId", ""));
                    String itemName = cfg.getString(prefix + "itemName", "");
                    int quantity = cfg.getInt(prefix + "quantity", 1);
                    double startingBid = cfg.getDouble(prefix + "startingBid");
                    double currentBid = cfg.getDouble(prefix + "currentBid");
                    String highestBidderStr = cfg.getString(prefix + "highestBidder");
                    UUID highestBidder = highestBidderStr != null ? UUID.fromString(highestBidderStr) : null;
                    long endTime = cfg.getLong(prefix + "endTime");
                    listings.put(id, new AuctionListing(id, sellerId, itemName, quantity, startingBid, currentBid, highestBidder, endTime));
                } catch (IllegalArgumentException ignored) {
                    // skip malformed entry
                }
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "auction_house.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (AuctionListing listing : listings.values()) {
            String prefix = "listings." + listing.id().toString() + ".";
            cfg.set(prefix + "sellerId", listing.sellerId().toString());
            cfg.set(prefix + "itemName", listing.itemName());
            cfg.set(prefix + "quantity", listing.quantity());
            cfg.set(prefix + "startingBid", listing.startingBid());
            cfg.set(prefix + "currentBid", listing.currentBid());
            if (listing.highestBidder() != null) {
                cfg.set(prefix + "highestBidder", listing.highestBidder().toString());
            }
            cfg.set(prefix + "endTime", listing.endTime());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save auction_house.yml", e);
        }
    }
}
