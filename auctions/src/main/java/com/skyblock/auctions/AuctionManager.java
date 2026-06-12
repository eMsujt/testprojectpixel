package com.skyblock.auctions;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * Manages active auction house listings.
 *
 * <p>Listings are stored in a {@link HashMap} keyed by a listing UUID
 * assigned on creation. A listing is either a bidding {@link AuctionType#AUCTION}
 * that players outbid each other on, or a fixed-price {@link AuctionType#BIN}
 * bought instantly. Ending, buying, or cancelling a listing removes it from
 * the active map. Not thread-safe; synchronize externally if accessed from
 * multiple threads.</p>
 */
public final class AuctionManager {

    /**
     * A single active auction house listing.
     *
     * <p>Instances are created only through
     * {@link AuctionManager#createListing(Player, String, AuctionType, double)}
     * and mutated only through {@link AuctionManager}.</p>
     */
    public static final class AuctionListing {

        private final UUID id;
        private final UUID seller;
        private final String itemName;
        private final AuctionType type;
        private final double startingPrice;
        private double highestBid;
        private UUID highestBidder;

        private AuctionListing(UUID id, UUID seller, String itemName, AuctionType type,
                double startingPrice) {
            this.id = id;
            this.seller = seller;
            this.itemName = itemName;
            this.type = type;
            this.startingPrice = startingPrice;
            this.highestBid = startingPrice;
        }

        /**
         * Returns this listing's unique id.
         *
         * @return the listing UUID
         */
        public UUID getId() {
            return id;
        }

        /**
         * Returns the unique id of the player selling the item.
         *
         * @return the selling player's UUID
         */
        public UUID getSeller() {
            return seller;
        }

        /**
         * Returns the name of the item being sold.
         *
         * @return the listed item's name
         */
        public String getItemName() {
            return itemName;
        }

        /**
         * Returns how this listing is resolved: through bidding or an
         * instant buy.
         *
         * @return the listing type
         */
        public AuctionType getType() {
            return type;
        }

        /**
         * Returns the seller-set price: the minimum first bid for an
         * {@link AuctionType#AUCTION}, or the instant-buy price for a
         * {@link AuctionType#BIN}.
         *
         * @return the starting price, never negative
         */
        public double getStartingPrice() {
            return startingPrice;
        }

        /**
         * Returns the current highest bid, or the starting price if no one
         * has bid yet.
         *
         * @return the current highest bid
         */
        public double getHighestBid() {
            return highestBid;
        }

        /**
         * Returns the current highest bidder.
         *
         * @return the highest bidder's UUID, or {@code null} if no one has bid
         */
        public UUID getHighestBidder() {
            return highestBidder;
        }
    }

    private final Map<UUID, AuctionListing> activeAuctions = new HashMap<>();

    /**
     * Creates a new listing for the given item and adds it to the active
     * auctions.
     *
     * @param seller        the selling player, must not be null
     * @param itemName      the name of the listed item, must not be null
     * @param type          the listing type, must not be null
     * @param startingPrice the minimum first bid (AUCTION) or instant-buy
     *                      price (BIN), must not be negative
     * @return the newly created listing
     * @throws IllegalArgumentException if {@code seller}, {@code itemName} or
     *                                  {@code type} is null, or
     *                                  {@code startingPrice} is negative
     */
    public AuctionListing createListing(Player seller, String itemName, AuctionType type,
            double startingPrice) {
        if (seller == null || itemName == null || type == null) {
            throw new IllegalArgumentException("seller, itemName and type must not be null");
        }
        if (startingPrice < 0) {
            throw new IllegalArgumentException(
                    "startingPrice must not be negative: " + startingPrice);
        }
        UUID id = UUID.randomUUID();
        AuctionListing listing =
                new AuctionListing(id, seller.getUniqueId(), itemName, type, startingPrice);
        activeAuctions.put(id, listing);
        return listing;
    }

    /**
     * Places a bid on an active bidding auction. The first bid must meet the
     * starting price; later bids must strictly exceed the current highest bid.
     *
     * @param auctionId the listing id
     * @param bidder    the bidding player, must not be null
     * @param amount    the bid amount
     * @throws IllegalArgumentException if the listing does not exist or is not
     *                                  a bidding auction, the bidder is null
     *                                  or the seller, or the amount is too low
     */
    public void placeBid(UUID auctionId, Player bidder, double amount) {
        AuctionListing listing = requireListing(auctionId);
        if (bidder == null) {
            throw new IllegalArgumentException("bidder must not be null");
        }
        if (!listing.type.isBidding()) {
            throw new IllegalArgumentException(
                    "cannot bid on a " + listing.type.getDisplayName() + " listing: " + auctionId);
        }
        if (bidder.getUniqueId().equals(listing.seller)) {
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
        listing.highestBidder = bidder.getUniqueId();
    }

    /**
     * Instantly buys a {@link AuctionType#BIN} listing, removing it from the
     * active auctions.
     *
     * @param auctionId the listing id
     * @param buyer     the buying player, must not be null
     * @return the bought listing
     * @throws IllegalArgumentException if the listing does not exist or is a
     *                                  bidding auction, or the buyer is null
     *                                  or the seller
     */
    public AuctionListing buyNow(UUID auctionId, Player buyer) {
        AuctionListing listing = requireListing(auctionId);
        if (buyer == null) {
            throw new IllegalArgumentException("buyer must not be null");
        }
        if (listing.type.isBidding()) {
            throw new IllegalArgumentException(
                    "cannot instantly buy a bidding auction: " + auctionId);
        }
        if (buyer.getUniqueId().equals(listing.seller)) {
            throw new IllegalArgumentException("seller cannot buy their own listing");
        }
        listing.highestBidder = buyer.getUniqueId();
        activeAuctions.remove(auctionId);
        return listing;
    }

    /**
     * Ends a bidding auction, removing it from the active auctions.
     *
     * @param auctionId the listing id
     * @return the winning bidder's UUID, or {@code null} if no one bid
     * @throws IllegalArgumentException if the listing does not exist or is not
     *                                  a bidding auction
     */
    public UUID endAuction(UUID auctionId) {
        AuctionListing listing = requireListing(auctionId);
        if (!listing.type.isBidding()) {
            throw new IllegalArgumentException(
                    "cannot end a " + listing.type.getDisplayName() + " listing: " + auctionId);
        }
        activeAuctions.remove(auctionId);
        return listing.highestBidder;
    }

    /**
     * Cancels a listing, removing it from the active auctions. Only the
     * seller may cancel, and only while no bids have been placed.
     *
     * @param auctionId the listing id
     * @param sellerId  the cancelling player's UUID, must be the seller
     * @return {@code true} if the listing was cancelled, {@code false} if a
     *         bid has already been placed
     * @throws IllegalArgumentException if the listing does not exist or
     *                                  {@code sellerId} is not its seller
     */
    public boolean cancelListing(UUID auctionId, UUID sellerId) {
        AuctionListing listing = requireListing(auctionId);
        if (!listing.seller.equals(sellerId)) {
            throw new IllegalArgumentException(
                    "only the seller may cancel listing: " + auctionId);
        }
        if (listing.highestBidder != null) {
            return false;
        }
        activeAuctions.remove(auctionId);
        return true;
    }

    /**
     * Returns whether a listing with the given id is currently active.
     *
     * @param auctionId the listing id
     * @return {@code true} if the listing exists and has not been settled
     */
    public boolean isActive(UUID auctionId) {
        return activeAuctions.containsKey(auctionId);
    }

    /**
     * Returns an active listing by id.
     *
     * @param auctionId the listing id
     * @return the listing, or {@code null} if none is active under that id
     */
    public AuctionListing getListing(UUID auctionId) {
        return activeAuctions.get(auctionId);
    }

    /**
     * Returns all currently active listings.
     *
     * @return an unmodifiable view of the active listings
     */
    public Collection<AuctionListing> getActiveAuctions() {
        return Collections.unmodifiableCollection(activeAuctions.values());
    }

    private AuctionListing requireListing(UUID auctionId) {
        AuctionListing listing = activeAuctions.get(auctionId);
        if (listing == null) {
            throw new IllegalArgumentException("no active auction with id: " + auctionId);
        }
        return listing;
    }
}
