package com.skyblock.auctionhouse;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Manages buy-it-now (BIN) auction house listings: sellers list an item at a
 * fixed price and buyers purchase it instantly, ending the listing. Bid-based
 * auctions are handled by the separate {@code skyblock-auction} module.
 *
 * <p>Listings are identified by an id assigned on creation. Not thread-safe;
 * synchronize externally if accessed from multiple threads.</p>
 */
public final class AuctionHouseManager {

    /** A single active buy-it-now listing. */
    private static final class BinListing {
        final UUID seller;
        final String itemName;
        final double price;

        BinListing(UUID seller, String itemName, double price) {
            this.seller = seller;
            this.itemName = itemName;
            this.price = price;
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

    private final Map<Long, BinListing> listings = new HashMap<>();
    private long nextListingId = 1;
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

    /**
     * Creates a new buy-it-now listing for the given item.
     *
     * @param seller   the selling player's UUID, must not be null
     * @param itemName the name of the listed item, must not be null
     * @param price    the fixed purchase price, must be positive
     * @return the id of the newly created listing
     * @throws IllegalArgumentException if {@code seller} or {@code itemName} is
     *                                  null, or {@code price} is not positive
     */
    public long createListing(UUID seller, String itemName, double price) {
        if (seller == null || itemName == null) {
            throw new IllegalArgumentException("seller and itemName must not be null");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("price must be positive: " + price);
        }
        long listingId = nextListingId++;
        listings.put(listingId, new BinListing(seller, itemName, price));
        recordAuctionEvent(seller, "Listed " + itemName + " for " + price + " coins");
        return listingId;
    }

    /**
     * Purchases an active listing, removing it from the auction house.
     *
     * @param listingId the listing id
     * @param buyer     the buying player's UUID, must not be null
     * @return the price paid for the item
     * @throws IllegalArgumentException if the listing does not exist, or the
     *                                  buyer is null or the seller
     */
    public double buyListing(long listingId, UUID buyer) {
        BinListing listing = requireListing(listingId);
        if (buyer == null) {
            throw new IllegalArgumentException("buyer must not be null");
        }
        if (buyer.equals(listing.seller)) {
            throw new IllegalArgumentException("seller cannot buy their own listing");
        }
        listings.remove(listingId);
        recordAuctionEvent(buyer, "Purchased " + listing.itemName + " for " + listing.price + " coins");
        return listing.price;
    }

    /**
     * Cancels an active listing. Only the seller may cancel their own listing.
     *
     * @param listingId the listing id
     * @param seller    the cancelling player's UUID, must not be null
     * @throws IllegalArgumentException if the listing does not exist, or
     *                                  {@code seller} is null or not the
     *                                  listing's seller
     */
    public void cancelListing(long listingId, UUID seller) {
        BinListing listing = requireListing(listingId);
        if (seller == null) {
            throw new IllegalArgumentException("seller must not be null");
        }
        if (!seller.equals(listing.seller)) {
            throw new IllegalArgumentException("only the seller can cancel their listing");
        }
        listings.remove(listingId);
    }

    /**
     * Returns whether a listing with the given id is currently active.
     *
     * @param listingId the listing id
     * @return {@code true} if the listing exists and has not been bought or
     *         cancelled
     */
    public boolean isListed(long listingId) {
        return listings.containsKey(listingId);
    }

    /**
     * Returns the seller of an active listing.
     *
     * @param listingId the listing id
     * @return the selling player's UUID
     * @throws IllegalArgumentException if the listing does not exist
     */
    public UUID getSeller(long listingId) {
        return requireListing(listingId).seller;
    }

    /**
     * Returns the name of the listed item.
     *
     * @param listingId the listing id
     * @return the listed item's name
     * @throws IllegalArgumentException if the listing does not exist
     */
    public String getItemName(long listingId) {
        return requireListing(listingId).itemName;
    }

    /**
     * Returns the fixed purchase price of an active listing.
     *
     * @param listingId the listing id
     * @return the buy-it-now price
     * @throws IllegalArgumentException if the listing does not exist
     */
    public double getPrice(long listingId) {
        return requireListing(listingId).price;
    }

    /**
     * Returns the ids of all currently active listings.
     *
     * @return an unmodifiable view of the active listing ids
     */
    public Set<Long> getActiveListings() {
        return Collections.unmodifiableSet(listings.keySet());
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "auctionhouse.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        listings.clear();
        auctionHistory.clear();
        nextListingId = cfg.getLong("nextListingId", 1L);
        if (cfg.isConfigurationSection("listings")) {
            for (String key : cfg.getConfigurationSection("listings").getKeys(false)) {
                try {
                    long id = Long.parseLong(key);
                    UUID seller = UUID.fromString(cfg.getString("listings." + key + ".seller", ""));
                    String itemName = cfg.getString("listings." + key + ".itemName");
                    if (itemName == null) continue;
                    double price = cfg.getDouble("listings." + key + ".price", 0.0);
                    listings.put(id, new BinListing(seller, itemName, price));
                } catch (IllegalArgumentException | NumberFormatException ignored) {
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
        cfg.set("nextListingId", nextListingId);
        for (Map.Entry<Long, BinListing> entry : listings.entrySet()) {
            String key = "listings." + entry.getKey();
            BinListing listing = entry.getValue();
            cfg.set(key + ".seller", listing.seller.toString());
            cfg.set(key + ".itemName", listing.itemName);
            cfg.set(key + ".price", listing.price);
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

    private BinListing requireListing(long listingId) {
        BinListing listing = listings.get(listingId);
        if (listing == null) {
            throw new IllegalArgumentException("no active listing with id: " + listingId);
        }
        return listing;
    }
}
