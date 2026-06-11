package com.skyblock.auctionhouse;

import java.util.Collections;
import java.util.HashMap;
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

    private final Map<Long, BinListing> listings = new HashMap<>();
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
        listings.put(listingId, new BinListing(seller, itemName, price));
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

    private BinListing requireListing(long listingId) {
        BinListing listing = listings.get(listingId);
        if (listing == null) {
            throw new IllegalArgumentException("no active listing with id: " + listingId);
        }
        return listing;
    }
}
