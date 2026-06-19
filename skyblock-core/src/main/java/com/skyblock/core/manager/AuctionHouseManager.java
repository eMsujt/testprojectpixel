package com.skyblock.core.manager;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class AuctionHouseManager {

    private static final AuctionHouseManager INSTANCE = new AuctionHouseManager();

    public enum AuctionCategory {
        WEAPONS,
        ARMOR,
        ACCESSORIES,
        CONSUMABLES,
        BLOCKS,
        MISC
    }

    public static final class AuctionListing {
        private final UUID seller;
        private final ItemStack item;
        private final long price;
        private final long expiry;
        private final AuctionCategory category;
        private final UUID id;

        public AuctionListing(UUID seller, ItemStack item, long price, long expiry, AuctionCategory category) {
            this.id = UUID.randomUUID();
            this.seller = seller;
            this.item = item;
            this.price = price;
            this.expiry = expiry;
            this.category = category;
        }

        public UUID getId() { return id; }
        public UUID getSeller() { return seller; }
        public ItemStack getItem() { return item; }
        public long getPrice() { return price; }
        public long getExpiry() { return expiry; }
        public AuctionCategory getCategory() { return category; }

        public boolean isExpired(long now) {
            return now >= expiry;
        }
    }

    private final List<AuctionListing> listings = new ArrayList<>();

    private AuctionHouseManager() {}

    public static AuctionHouseManager getInstance() {
        return INSTANCE;
    }

    public void addListing(AuctionListing listing) {
        listings.add(listing);
    }

    public boolean removeListing(UUID listingId) {
        return listings.removeIf(l -> l.getId().equals(listingId));
    }

    public List<AuctionListing> getListings() {
        return Collections.unmodifiableList(listings);
    }

    public List<AuctionListing> getListingsByCategory(AuctionCategory category) {
        List<AuctionListing> result = new ArrayList<>();
        for (AuctionListing l : listings) {
            if (l.getCategory() == category) {
                result.add(l);
            }
        }
        return result;
    }

    public List<AuctionListing> getListingsBySeller(UUID seller) {
        List<AuctionListing> result = new ArrayList<>();
        for (AuctionListing l : listings) {
            if (l.getSeller().equals(seller)) {
                result.add(l);
            }
        }
        return result;
    }

    public void purgeExpired(long now) {
        listings.removeIf(l -> l.isExpired(now));
    }
}
