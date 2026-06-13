package com.skyblock.core.auction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing active auction house listings for SkyBlock.
 *
 * <p>Supports both buy-it-now (BIN) and bid-based auctions. Each listing is
 * identified by a {@link UUID} assigned on creation.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class AuctionManager {

    /** Item categories used to filter auction house listings. */
    public enum AuctionCategory {
        WEAPONS("Weapons"),
        ARMOR("Armor"),
        ACCESSORIES("Accessories"),
        CONSUMABLES("Consumables"),
        BLOCKS("Blocks"),
        MISC("Misc");

        private final String displayName;

        AuctionCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** The two auction modes available in the auction house. */
    public enum AuctionType {
        BIN("Buy It Now"),
        AUCTION("Bid-based");

        private final String displayName;

        AuctionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** An immutable auction listing snapshot. */
    public record AuctionListing(UUID id, UUID seller, String itemName,
                                 double startingBid, AuctionType type, AuctionCategory category) {

        public AuctionListing {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(seller, "seller");
            Objects.requireNonNull(itemName, "itemName");
            Objects.requireNonNull(type, "type");
            Objects.requireNonNull(category, "category");
            if (startingBid < 0) {
                throw new IllegalArgumentException("startingBid must not be negative: " + startingBid);
            }
        }
    }

    private static final class State {
        final AuctionListing entry;
        double currentBid;
        UUID highestBidder;

        State(AuctionListing entry) {
            this.entry = entry;
            this.currentBid = entry.startingBid();
        }
    }

    private static final AuctionManager INSTANCE = new AuctionManager();

    private final Map<UUID, State> listings = new HashMap<>();

    private AuctionManager() {}

    /**
     * Returns the single shared {@code AuctionManager} instance.
     *
     * @return the singleton instance
     */
    public static AuctionManager getInstance() {
        return INSTANCE;
    }

    // ---------------------------------------------------------------------------
    // Listing lifecycle
    // ---------------------------------------------------------------------------

    /**
     * Creates a new listing and returns its assigned id.
     *
     * @param seller      the selling player's UUID
     * @param itemName    display name of the item
     * @param startingBid minimum bid or BIN price (must be ≥ 0)
     * @param type        the auction type ({@link AuctionType#BIN} or {@link AuctionType#AUCTION})
     * @param category    the item category ({@link AuctionCategory})
     * @return the new listing's UUID
     */
    public UUID createListing(UUID seller, String itemName,
                              double startingBid, AuctionType type, AuctionCategory category) {
        Objects.requireNonNull(seller, "seller");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(category, "category");
        UUID id = UUID.randomUUID();
        listings.put(id, new State(new AuctionListing(id, seller, itemName, startingBid, type, category)));
        return id;
    }

    /**
     * Places a bid or buys a BIN listing.
     *
     * <p>For BIN listings the amount must meet the asking price; the listing is
     * removed on purchase and {@code true} is returned. For bid-based auctions the
     * first bid must meet {@code startingBid}; subsequent bids must strictly exceed
     * the current highest bid.</p>
     *
     * @param listingId the listing UUID
     * @param bidder    the bidding player's UUID
     * @param amount    the bid or purchase amount
     * @return {@code true} if the listing was purchased (BIN), {@code false} if the
     *         bid was recorded and the auction remains open
     * @throws IllegalArgumentException if the listing does not exist, the bidder is
     *                                  the seller, or the amount is too low
     */
    public boolean placeBid(UUID listingId, UUID bidder, double amount) {
        State state = requireState(listingId);
        Objects.requireNonNull(bidder, "bidder");
        if (bidder.equals(state.entry.seller())) {
            throw new IllegalArgumentException("seller cannot bid on their own listing");
        }
        if (state.entry.type() == AuctionType.BIN) {
            if (amount < state.entry.startingBid()) {
                throw new IllegalArgumentException(
                        "amount must meet the BIN price " + state.entry.startingBid() + ": " + amount);
            }
            listings.remove(listingId);
            return true;
        }
        boolean tooLow = state.highestBidder == null
                ? amount < state.currentBid
                : amount <= state.currentBid;
        if (tooLow) {
            throw new IllegalArgumentException(
                    "bid must exceed current highest bid " + state.currentBid + ": " + amount);
        }
        state.currentBid = amount;
        state.highestBidder = bidder;
        return false;
    }

    /**
     * Ends a bid-based auction, removing it and returning the winning bidder.
     *
     * @param listingId the listing UUID
     * @return the winning bidder's UUID, or {@code null} if no bids were placed
     * @throws IllegalArgumentException if the listing does not exist or is a BIN listing
     */
    public UUID endAuction(UUID listingId) {
        State state = requireState(listingId);
        if (state.entry.type() == AuctionType.BIN) {
            throw new IllegalArgumentException("cannot end a BIN listing as an auction: " + listingId);
        }
        listings.remove(listingId);
        return state.highestBidder;
    }

    /**
     * Cancels a listing. Only the original seller may cancel.
     *
     * @param listingId the listing UUID
     * @param seller    the cancelling player's UUID
     * @throws IllegalArgumentException if the listing does not exist or the caller is
     *                                  not the seller
     */
    public void cancelListing(UUID listingId, UUID seller) {
        State state = requireState(listingId);
        Objects.requireNonNull(seller, "seller");
        if (!seller.equals(state.entry.seller())) {
            throw new IllegalArgumentException("only the seller can cancel their listing");
        }
        listings.remove(listingId);
    }

    // ---------------------------------------------------------------------------
    // Queries
    // ---------------------------------------------------------------------------

    /**
     * Returns whether a listing with the given id is currently active.
     *
     * @param listingId the listing UUID
     * @return {@code true} if the listing is active
     */
    public boolean isActive(UUID listingId) {
        return listings.containsKey(listingId);
    }

    /**
     * Returns the {@link AuctionListing} for an active listing.
     *
     * @param listingId the listing UUID
     * @return the entry record, never null
     * @throws IllegalArgumentException if the listing does not exist
     */
    public AuctionListing getListing(UUID listingId) {
        return requireState(listingId).entry;
    }

    /**
     * Returns the current highest bid for a listing.
     *
     * @param listingId the listing UUID
     * @return the current highest bid (equals {@code startingBid} before any bids)
     * @throws IllegalArgumentException if the listing does not exist
     */
    public double getHighestBid(UUID listingId) {
        return requireState(listingId).currentBid;
    }

    /**
     * Returns the current highest bidder for a bid-based listing.
     *
     * @param listingId the listing UUID
     * @return the highest bidder's UUID, or {@code null} if no bids yet
     * @throws IllegalArgumentException if the listing does not exist
     */
    public UUID getHighestBidder(UUID listingId) {
        return requireState(listingId).highestBidder;
    }

    /**
     * Returns a snapshot list of all active listings.
     *
     * @return unmodifiable list of all active {@link AuctionListing} records
     */
    public List<AuctionListing> getActiveListings() {
        List<AuctionListing> result = new ArrayList<>();
        for (State s : listings.values()) {
            result.add(s.entry);
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Returns a snapshot of listings created by the given seller.
     *
     * @param seller the seller's UUID
     * @return unmodifiable list of the seller's active {@link AuctionListing} records
     */
    public List<AuctionListing> getListingsBySeller(UUID seller) {
        Objects.requireNonNull(seller, "seller");
        List<AuctionListing> result = new ArrayList<>();
        for (State s : listings.values()) {
            if (seller.equals(s.entry.seller())) {
                result.add(s.entry);
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Returns a snapshot of listings in the given category.
     *
     * @param category the category to filter by
     * @return unmodifiable list of matching {@link AuctionListing} records
     */
    public List<AuctionListing> getListingsByCategory(AuctionCategory category) {
        Objects.requireNonNull(category, "category");
        List<AuctionListing> result = new ArrayList<>();
        for (State s : listings.values()) {
            if (category == s.entry.category()) {
                result.add(s.entry);
            }
        }
        return Collections.unmodifiableList(result);
    }

    /** Removes all active listings. */
    public void clear() {
        listings.clear();
    }

    private State requireState(UUID listingId) {
        State s = listings.get(listingId);
        if (s == null) {
            throw new IllegalArgumentException("no active listing with id: " + listingId);
        }
        return s;
    }
}
