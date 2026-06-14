package com.skyblock.plugin.economy;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * In-memory registry of active auction house listings.
 *
 * <p>Holds the live listings in a {@link Map} keyed by each listing's unique
 * id, preserving insertion order so menus display them in the order they were
 * posted. A listing is added when a player puts an item up for sale and removed
 * when it is bought or cancelled. Not thread-safe; access from the main server
 * thread.</p>
 */
public final class AuctionHouseManager {

    /**
     * A single active auction house listing.
     *
     * @param id       the listing's unique id
     * @param seller   the selling player's UUID
     * @param itemName the name of the item being sold
     * @param price    the starting/buy-it-now price in coins
     */
    public record AuctionListing(UUID id, UUID seller, String itemName, double price) {
        public AuctionListing {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(seller, "seller");
            Objects.requireNonNull(itemName, "itemName");
        }
    }

    private static final AuctionHouseManager INSTANCE = new AuctionHouseManager();

    private final Map<UUID, AuctionListing> listings = new LinkedHashMap<>();

    private AuctionHouseManager() {
    }

    public static AuctionHouseManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds a listing to the active auctions.
     *
     * @param listing the listing to add
     */
    public void addListing(AuctionListing listing) {
        Objects.requireNonNull(listing, "listing");
        listings.put(listing.id(), listing);
    }

    /**
     * Returns the listing with the given id, or {@code null} if absent.
     *
     * @param id the listing id
     * @return the listing, or {@code null}
     */
    public AuctionListing getListing(UUID id) {
        return listings.get(id);
    }

    /**
     * Removes the listing with the given id.
     *
     * @param id the listing id
     * @return the removed listing, or {@code null} if none existed
     */
    public AuctionListing removeListing(UUID id) {
        return listings.remove(id);
    }

    /**
     * Returns an unmodifiable view of every active listing in posting order.
     *
     * @return the active listings
     */
    public Collection<AuctionListing> getListings() {
        return Collections.unmodifiableCollection(listings.values());
    }
}
