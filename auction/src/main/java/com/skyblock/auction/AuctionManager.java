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
 * <p>Auctions are identified by a UUID assigned on creation. Not thread-safe;
 * synchronize externally if accessed from multiple threads.</p>
 */
public final class AuctionManager {

    /** A single active auction house listing. */
    public static final class AuctionListing {
        private final UUID seller;
        private final String itemName;
        private double highestBid;
        private UUID highestBidder;

        AuctionListing(UUID seller, String itemName, double startingBid) {
            this.seller = seller;
            this.itemName = itemName;
            this.highestBid = startingBid;
        }

        public UUID getSeller() { return seller; }
        public String getItemName() { return itemName; }
        public double getHighestBid() { return highestBid; }
        public UUID getHighestBidder() { return highestBidder; }
    }

    private final Map<UUID, AuctionListing> listings = new HashMap<>();

    /**
     * Creates a new auction for the given item.
     *
     * @param player      the selling player's UUID, must not be null
     * @param itemName    the name of the auctioned item, must not be null
     * @param startingBid the minimum first bid, must not be negative
     * @return the UUID of the newly created auction
     * @throws IllegalArgumentException if {@code player} or {@code itemName} is
     *                                  null, or {@code startingBid} is negative
     */
    public UUID createListing(UUID player, String itemName, double startingBid) {
        if (player == null || itemName == null) {
            throw new IllegalArgumentException("player and itemName must not be null");
        }
        if (startingBid < 0) {
            throw new IllegalArgumentException("startingBid must not be negative: " + startingBid);
        }
        UUID auctionId = UUID.randomUUID();
        listings.put(auctionId, new AuctionListing(player, itemName, startingBid));
        return auctionId;
    }

    /**
     * Places a bid on an active auction. The first bid must meet the starting
     * bid; later bids must strictly exceed the current highest bid.
     *
     * @param auctionId the auction UUID
     * @param bidder    the bidding player's UUID, must not be null
     * @param amount    the bid amount
     * @throws IllegalArgumentException if the auction does not exist, the
     *                                  bidder is null or the seller, or the
     *                                  amount is too low
     */
    public void placeBid(UUID auctionId, UUID bidder, double amount) {
        AuctionListing listing = requireListing(auctionId);
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
     * @param auctionId the auction UUID
     * @return {@code true} if the auction exists and has not ended
     */
    public boolean isActive(UUID auctionId) {
        return listings.containsKey(auctionId);
    }

    /**
     * Returns the listing for an active auction.
     *
     * @param auctionId the auction UUID
     * @return the {@link AuctionListing}, never null
     * @throws IllegalArgumentException if the auction does not exist
     */
    public AuctionListing getListing(UUID auctionId) {
        return requireListing(auctionId);
    }

    /**
     * Ends an auction, removing it from the active listings.
     *
     * @param auctionId the auction UUID
     * @return the winning bidder's UUID, or null if no one bid
     * @throws IllegalArgumentException if the auction does not exist
     */
    public UUID endAuction(UUID auctionId) {
        AuctionListing listing = listings.remove(auctionId);
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
    public Set<UUID> getActiveAuctions() {
        return Collections.unmodifiableSet(listings.keySet());
    }

    private AuctionListing requireListing(UUID auctionId) {
        AuctionListing listing = listings.get(auctionId);
        if (listing == null) {
            throw new IllegalArgumentException("no active auction with id: " + auctionId);
        }
        return listing;
    }
}
