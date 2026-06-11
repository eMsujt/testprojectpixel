package com.skyblock.auction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Manages auction house listings: sellers list an item with a starting bid,
 * other players outbid each other, and ending the auction settles it with the
 * highest bidder.
 *
 * <p>Auctions are identified by an id assigned on creation. Not thread-safe;
 * synchronize externally if accessed from multiple threads.</p>
 */
public final class AuctionManager {

    /** A single active auction house listing. */
    private static final class Listing {
        final UUID seller;
        final String itemName;
        double highestBid;
        UUID highestBidder;

        Listing(UUID seller, String itemName, double startingBid) {
            this.seller = seller;
            this.itemName = itemName;
            this.highestBid = startingBid;
        }
    }

    private final Map<Long, Listing> listings = new HashMap<>();
    private long nextAuctionId = 1;

    /**
     * Creates a new auction for the given item.
     *
     * @param seller      the selling player's UUID, must not be null
     * @param itemName    the name of the auctioned item, must not be null
     * @param startingBid the minimum first bid, must not be negative
     * @return the id of the newly created auction
     * @throws IllegalArgumentException if {@code seller} or {@code itemName} is
     *                                  null, or {@code startingBid} is negative
     */
    public long createAuction(UUID seller, String itemName, double startingBid) {
        if (seller == null || itemName == null) {
            throw new IllegalArgumentException("seller and itemName must not be null");
        }
        if (startingBid < 0) {
            throw new IllegalArgumentException("startingBid must not be negative: " + startingBid);
        }
        long auctionId = nextAuctionId++;
        listings.put(auctionId, new Listing(seller, itemName, startingBid));
        return auctionId;
    }

    /**
     * Places a bid on an active auction. The first bid must meet the starting
     * bid; later bids must strictly exceed the current highest bid.
     *
     * @param auctionId the auction id
     * @param bidder    the bidding player's UUID, must not be null
     * @param amount    the bid amount
     * @throws IllegalArgumentException if the auction does not exist, the
     *                                  bidder is null or the seller, or the
     *                                  amount is too low
     */
    public void placeBid(long auctionId, UUID bidder, double amount) {
        Listing listing = requireListing(auctionId);
        if (bidder == null) {
            throw new IllegalArgumentException("bidder must not be null");
        }
        if (bidder.equals(listing.seller)) {
            throw new IllegalArgumentException("seller cannot bid on their own auction");
        }
        boolean tooLow = listing.highestBidder == null
                ? amount < listing.highestBid
                : amount <= listing.highestBid;
        if (tooLow) {
            throw new IllegalArgumentException(
                    "bid must exceed current highest bid " + listing.highestBid + ": " + amount);
        }
        listing.highestBid = amount;
        listing.highestBidder = bidder;
    }

    /**
     * Returns whether an auction with the given id is currently active.
     *
     * @param auctionId the auction id
     * @return {@code true} if the auction exists and has not ended
     */
    public boolean isActive(long auctionId) {
        return listings.containsKey(auctionId);
    }

    /**
     * Returns the seller of an active auction.
     *
     * @param auctionId the auction id
     * @return the selling player's UUID
     * @throws IllegalArgumentException if the auction does not exist
     */
    public UUID getSeller(long auctionId) {
        return requireListing(auctionId).seller;
    }

    /**
     * Returns the name of the item being auctioned.
     *
     * @param auctionId the auction id
     * @return the auctioned item's name
     * @throws IllegalArgumentException if the auction does not exist
     */
    public String getItemName(long auctionId) {
        return requireListing(auctionId).itemName;
    }

    /**
     * Returns the current highest bid, or the starting bid if no one has bid.
     *
     * @param auctionId the auction id
     * @return the current highest bid
     * @throws IllegalArgumentException if the auction does not exist
     */
    public double getHighestBid(long auctionId) {
        return requireListing(auctionId).highestBid;
    }

    /**
     * Returns the current highest bidder.
     *
     * @param auctionId the auction id
     * @return the highest bidder's UUID, or null if no one has bid
     * @throws IllegalArgumentException if the auction does not exist
     */
    public UUID getHighestBidder(long auctionId) {
        return requireListing(auctionId).highestBidder;
    }

    /**
     * Ends an auction, removing it from the active listings.
     *
     * @param auctionId the auction id
     * @return the winning bidder's UUID, or null if no one bid
     * @throws IllegalArgumentException if the auction does not exist
     */
    public UUID endAuction(long auctionId) {
        Listing listing = listings.remove(auctionId);
        if (listing == null) {
            throw new IllegalArgumentException("no active auction with id: " + auctionId);
        }
        return listing.highestBidder;
    }

    /**
     * Returns the ids of all currently active auctions.
     *
     * @return an unmodifiable view of the active auction ids
     */
    public Set<Long> getActiveAuctions() {
        return Collections.unmodifiableSet(listings.keySet());
    }

    private Listing requireListing(long auctionId) {
        Listing listing = listings.get(auctionId);
        if (listing == null) {
            throw new IllegalArgumentException("no active auction with id: " + auctionId);
        }
        return listing;
    }
}
