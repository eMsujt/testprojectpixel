package com.skyblock.auctions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Manages buy-it-now auction listings: sellers list an item at a fixed price
 * and the first buyer to purchase it claims the item immediately. Sellers may
 * cancel their own unsold listings.
 *
 * <p>Listings are identified by an id assigned on creation. Not thread-safe;
 * synchronize externally if accessed from multiple threads.</p>
 */
public final class AuctionManager {

    /** A single active buy-it-now listing. */
    private static final class Listing {
        final UUID seller;
        final String itemName;
        final double price;

        Listing(UUID seller, String itemName, double price) {
            this.seller = seller;
            this.itemName = itemName;
            this.price = price;
        }
    }

    private final Map<Long, Listing> listings = new HashMap<>();
    private long nextListingId = 1;

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
        listings.put(listingId, new Listing(seller, itemName, price));
        return listingId;
    }

    /**
     * Returns whether a listing with the given id is currently active.
     *
     * @param listingId the listing id
     * @return {@code true} if the listing exists and has not been sold or
     *         cancelled
     */
    public boolean isActive(long listingId) {
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
     * Returns the fixed purchase price of a listing.
     *
     * @param listingId the listing id
     * @return the buy-it-now price
     * @throws IllegalArgumentException if the listing does not exist
     */
    public double getPrice(long listingId) {
        return requireListing(listingId).price;
    }

    /**
     * Purchases a listing, removing it from the active listings.
     *
     * @param listingId the listing id
     * @param buyer     the buying player's UUID, must not be null
     * @return the name of the purchased item
     * @throws IllegalArgumentException if the listing does not exist, the buyer
     *                                  is null, or the buyer is the seller
     */
    public String buyListing(long listingId, UUID buyer) {
        Listing listing = requireListing(listingId);
        if (buyer == null) {
            throw new IllegalArgumentException("buyer must not be null");
        }
        if (buyer.equals(listing.seller)) {
            throw new IllegalArgumentException("seller cannot buy their own listing");
        }
        listings.remove(listingId);
        return listing.itemName;
    }

    /**
     * Cancels an unsold listing. Only the seller may cancel their listing.
     *
     * @param listingId the listing id
     * @param seller    the cancelling player's UUID, must be the seller
     * @throws IllegalArgumentException if the listing does not exist or
     *                                  {@code seller} is not the listing's
     *                                  seller
     */
    public void cancelListing(long listingId, UUID seller) {
        Listing listing = requireListing(listingId);
        if (!listing.seller.equals(seller)) {
            throw new IllegalArgumentException("only the seller can cancel a listing");
        }
        listings.remove(listingId);
    }

    /**
     * Returns the ids of all currently active listings.
     *
     * @return an unmodifiable view of the active listing ids
     */
    public Set<Long> getActiveListings() {
        return Collections.unmodifiableSet(listings.keySet());
    }

    private Listing requireListing(long listingId) {
        Listing listing = listings.get(listingId);
        if (listing == null) {
            throw new IllegalArgumentException("no active listing with id: " + listingId);
        }
        return listing;
    }
}
