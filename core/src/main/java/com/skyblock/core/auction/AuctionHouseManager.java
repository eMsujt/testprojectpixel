package com.skyblock.core.auction;

import com.skyblock.core.manager.AuctionHouseManager;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.AuctionHouseManager} (the canonical singleton).
 */
@Deprecated
public final class AuctionHouseManager {

    public static final Map<String, int[]> AUCTION_CATEGORY_DATA =
            com.skyblock.core.manager.AuctionHouseManager.AUCTION_CATEGORY_DATA;
    public static final Map<String, String[]> ITEM_CATEGORIES =
            com.skyblock.core.manager.AuctionHouseManager.ITEM_CATEGORIES;

    public static final class AuctionType {
        public static final com.skyblock.core.manager.AuctionHouseManager.AuctionType BIN =
                com.skyblock.core.manager.AuctionHouseManager.AuctionType.BIN;
        public static final com.skyblock.core.manager.AuctionHouseManager.AuctionType AUCTION =
                com.skyblock.core.manager.AuctionHouseManager.AuctionType.AUCTION;

        private AuctionType() {}
    }

    public static final class AuctionCategory {
        public static final com.skyblock.core.manager.AuctionHouseManager.AuctionCategory WEAPONS =
                com.skyblock.core.manager.AuctionHouseManager.AuctionCategory.WEAPONS;
        public static final com.skyblock.core.manager.AuctionHouseManager.AuctionCategory ARMOR =
                com.skyblock.core.manager.AuctionHouseManager.AuctionCategory.ARMOR;
        public static final com.skyblock.core.manager.AuctionHouseManager.AuctionCategory ACCESSORIES =
                com.skyblock.core.manager.AuctionHouseManager.AuctionCategory.ACCESSORIES;
        public static final com.skyblock.core.manager.AuctionHouseManager.AuctionCategory CONSUMABLES =
                com.skyblock.core.manager.AuctionHouseManager.AuctionCategory.CONSUMABLES;
        public static final com.skyblock.core.manager.AuctionHouseManager.AuctionCategory BLOCKS =
                com.skyblock.core.manager.AuctionHouseManager.AuctionCategory.BLOCKS;
        public static final com.skyblock.core.manager.AuctionHouseManager.AuctionCategory MINIONS =
                com.skyblock.core.manager.AuctionHouseManager.AuctionCategory.MINIONS;
        public static final com.skyblock.core.manager.AuctionHouseManager.AuctionCategory MISC =
                com.skyblock.core.manager.AuctionHouseManager.AuctionCategory.MISC;

        private AuctionCategory() {}
    }

    public record AuctionItem(String itemName, UUID seller, long price, long endEpoch) {
        public AuctionItem {
            java.util.Objects.requireNonNull(itemName, "itemName");
            java.util.Objects.requireNonNull(seller, "seller");
            if (price < 0) throw new IllegalArgumentException("price must not be negative: " + price);
        }
    }

    public record AuctionListing(UUID id, UUID seller, org.bukkit.inventory.ItemStack item, String itemName,
                                 com.skyblock.core.manager.AuctionHouseManager.AuctionCategory category, double startingBid,
                                 com.skyblock.core.manager.AuctionHouseManager.AuctionType type) {
        public AuctionListing {
            java.util.Objects.requireNonNull(id, "id");
            java.util.Objects.requireNonNull(seller, "seller");
            java.util.Objects.requireNonNull(item, "item");
            java.util.Objects.requireNonNull(itemName, "itemName");
            java.util.Objects.requireNonNull(category, "category");
            java.util.Objects.requireNonNull(type, "type");
            if (startingBid < 0) {
                throw new IllegalArgumentException("startingBid must not be negative: " + startingBid);
            }
        }
    }

    private static final com.skyblock.core.manager.AuctionHouseManager DELEGATE =
            com.skyblock.core.manager.AuctionHouseManager.getInstance();

    private AuctionHouseManager() {}

    public static com.skyblock.core.manager.AuctionHouseManager getInstance() {
        return DELEGATE;
    }

    public UUID createListing(UUID seller, org.bukkit.inventory.ItemStack item, String itemName,
                              com.skyblock.core.manager.AuctionHouseManager.AuctionCategory category, double startingBid,
                              com.skyblock.core.manager.AuctionHouseManager.AuctionType type) {
        return DELEGATE.createListing(seller, item, itemName, category, startingBid, type);
    }

    public List<com.skyblock.core.manager.AuctionHouseManager.AuctionListing> getListingsByCategory(
            com.skyblock.core.manager.AuctionHouseManager.AuctionCategory category) {
        return DELEGATE.getListingsByCategory(category);
    }

    public boolean placeBid(UUID listingId, UUID bidder, double amount) {
        return DELEGATE.placeBid(listingId, bidder, amount);
    }

    public UUID endAuction(UUID listingId) {
        return DELEGATE.endAuction(listingId);
    }

    public void cancelListing(UUID listingId, UUID seller) {
        DELEGATE.cancelListing(listingId, seller);
    }

    public boolean isActive(UUID listingId) {
        return DELEGATE.isActive(listingId);
    }

    public com.skyblock.core.manager.AuctionHouseManager.AuctionListing getListing(UUID listingId) {
        return DELEGATE.getListing(listingId);
    }

    public double getHighestBid(UUID listingId) {
        return DELEGATE.getHighestBid(listingId);
    }

    public UUID getHighestBidder(UUID listingId) {
        return DELEGATE.getHighestBidder(listingId);
    }

    public Set<UUID> getActiveListings() {
        return DELEGATE.getActiveListings();
    }

    public int getAuctionCount(UUID player) {
        return DELEGATE.getAuctionCount(player);
    }

    public void incrementAuctionCount(UUID player) {
        DELEGATE.incrementAuctionCount(player);
    }

    public void setAuctionCount(UUID player, int count) {
        DELEGATE.setAuctionCount(player, count);
    }

    public void recordAuction(UUID player, String summary) {
        DELEGATE.recordAuction(player, summary);
    }

    public List<String> getAuctionHistory(UUID player) {
        return DELEGATE.getAuctionHistory(player);
    }

    public Map<UUID, List<String>> getAllAuctionHistory() {
        return DELEGATE.getAllAuctionHistory();
    }

    public String getAuctionHouseStats(UUID player) {
        return DELEGATE.getAuctionHouseStats(player);
    }

    public UUID addItem(UUID seller, String itemName, long price, long endEpoch) {
        return DELEGATE.addItem(seller, itemName, price, endEpoch);
    }

    public com.skyblock.core.manager.AuctionHouseManager.AuctionItem getItem(UUID id) {
        return DELEGATE.getItem(id);
    }

    public void cancelItem(UUID id, UUID seller) {
        DELEGATE.cancelItem(id, seller);
    }

    public List<com.skyblock.core.manager.AuctionHouseManager.AuctionItem> getActiveItems() {
        return DELEGATE.getActiveItems();
    }

    public void load(File dataFolder) {
        DELEGATE.load(dataFolder);
    }

    public void save(File dataFolder) {
        DELEGATE.save(dataFolder);
    }

    public void clear() {
        DELEGATE.clear();
    }
}
