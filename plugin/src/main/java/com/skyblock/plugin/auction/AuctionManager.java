package com.skyblock.plugin.auction;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-memory registry of active Buy-It-Now (BIN) auction house listings.
 *
 * <p>Holds the live listings in a thread-safe {@link CopyOnWriteArrayList},
 * preserving insertion order so menus display them in the order they were
 * posted. A listing is added when a player puts an item up for sale and removed
 * when it is bought or cancelled. Safe to read and mutate from any thread.</p>
 */
public final class AuctionManager {

    /**
     * A single active BIN listing.
     *
     * @param id       the listing's unique id
     * @param seller   the selling player's UUID
     * @param itemName the name of the item being sold
     * @param price    the buy-it-now price in coins
     */
    public record AuctionEntry(UUID id, UUID seller, String itemName, double price) {
        public AuctionEntry {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(seller, "seller");
            Objects.requireNonNull(itemName, "itemName");
        }
    }

    private static final AuctionManager INSTANCE = new AuctionManager();

    private final List<AuctionEntry> listings = new CopyOnWriteArrayList<>();
    private final List<AuctionListing> auctionListings = new CopyOnWriteArrayList<>();

    private AuctionManager() {
    }

    public static AuctionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds a BIN listing to the active auctions.
     *
     * @param listing the listing to add
     */
    public void addListing(AuctionEntry listing) {
        Objects.requireNonNull(listing, "listing");
        listings.add(listing);
    }

    /**
     * Returns the listing with the given id, or {@code null} if absent.
     *
     * @param id the listing id
     * @return the listing, or {@code null}
     */
    public AuctionEntry getListing(UUID id) {
        for (AuctionEntry listing : listings) {
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
    public AuctionEntry removeListing(UUID id) {
        AuctionEntry existing = getListing(id);
        if (existing != null) {
            listings.remove(existing);
        }
        return existing;
    }

    /**
     * Returns an unmodifiable view of every active listing in posting order.
     *
     * @return the active listings
     */
    public List<AuctionEntry> getListings() {
        return Collections.unmodifiableList(listings);
    }

    /**
     * Adds an {@link AuctionListing} (item-backed BIN listing) to the active auctions.
     *
     * @param listing the listing to add
     */
    public void addAuctionListing(AuctionListing listing) {
        Objects.requireNonNull(listing, "listing");
        auctionListings.add(listing);
    }

    /**
     * Returns the {@link AuctionListing} with the given id, or {@code null} if absent.
     *
     * @param id the listing id
     * @return the listing, or {@code null}
     */
    public AuctionListing getAuctionListing(String id) {
        for (AuctionListing listing : auctionListings) {
            if (listing.id().equals(id)) {
                return listing;
            }
        }
        return null;
    }

    /**
     * Removes the {@link AuctionListing} with the given id.
     *
     * @param id the listing id
     * @return {@code true} if a listing was removed
     */
    public boolean removeAuctionListing(String id) {
        return auctionListings.removeIf(l -> l.id().equals(id));
    }

    /**
     * Returns an unmodifiable view of every active {@link AuctionListing} in posting order.
     *
     * @return the item-backed listings
     */
    public List<AuctionListing> getAuctionListings() {
        return Collections.unmodifiableList(auctionListings);
    }
}
