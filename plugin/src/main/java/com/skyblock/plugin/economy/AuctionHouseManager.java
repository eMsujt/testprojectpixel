package com.skyblock.plugin.economy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * In-memory registry of active auction house listings.
 *
 * <p>Holds the live listings in a {@link List} in posting order so menus
 * display them in the order they were posted. A listing is added when a player
 * puts an item up for sale and removed when it is bought or cancelled. Not
 * thread-safe; access from the main server thread.</p>
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

    private final List<AuctionListing> listings = new ArrayList<>();

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
        listings.add(listing);
    }

    /**
     * Returns the listing with the given id, or {@code null} if absent.
     *
     * @param id the listing id
     * @return the listing, or {@code null}
     */
    public AuctionListing getListing(UUID id) {
        for (AuctionListing listing : listings) {
            if (listing.id().equals(id)) {
                return listing;
            }
        }
        return null;
    }

    /**
     * Removes the listing with the given id.
     *
     * @param id the listing id
     * @return the removed listing, or {@code null} if none existed
     */
    public AuctionListing removeListing(UUID id) {
        for (int i = 0; i < listings.size(); i++) {
            if (listings.get(i).id().equals(id)) {
                return listings.remove(i);
            }
        }
        return null;
    }

    /**
     * Returns an unmodifiable view of every active listing in posting order.
     *
     * @return the active listings
     */
    public List<AuctionListing> getListings() {
        return Collections.unmodifiableList(listings);
    }
}
