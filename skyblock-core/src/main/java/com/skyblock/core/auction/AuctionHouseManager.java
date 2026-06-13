package com.skyblock.core.auction;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Singleton managing auction house listings that support both buy-it-now (BIN)
 * and bid-based auctions. Listings are identified by a UUID assigned on creation.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class AuctionHouseManager {

    /** The two auction modes available in the Auction House. */
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

    /** Every auction category available in the Auction House. */
    public enum AuctionCategory {
        WEAPONS("Weapons"),
        ARMOR("Armor"),
        ACCESSORIES("Accessories"),
        CONSUMABLES("Consumables"),
        BLOCKS("Blocks"),
        MINIONS("Minions"),
        MISC("Misc");

        private final String displayName;

        AuctionCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private static final AuctionHouseManager INSTANCE = new AuctionHouseManager();

    /** A single active auction house listing. */
    public record AuctionListing(UUID id, UUID seller, ItemStack item, String itemName,
                                 AuctionCategory category, double startingBid, AuctionType type) {

        public AuctionListing {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(seller, "seller");
            Objects.requireNonNull(item, "item");
            Objects.requireNonNull(itemName, "itemName");
            Objects.requireNonNull(category, "category");
            Objects.requireNonNull(type, "type");
            if (startingBid < 0) {
                throw new IllegalArgumentException("startingBid must not be negative: " + startingBid);
            }
        }
    }

    private static final class ListingState {
        final AuctionListing listing;
        double highestBid;
        UUID highestBidder;

        ListingState(AuctionListing listing) {
            this.listing = listing;
            this.highestBid = listing.startingBid();
        }
    }

    private final Map<UUID, ListingState> listings = new HashMap<>();

    private AuctionHouseManager() {}

    /**
     * Returns the single shared {@code AuctionHouseManager} instance.
     *
     * @return the singleton instance
     */
    public static AuctionHouseManager getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a new auction house listing.
     *
     * @param seller      the selling player's UUID, must not be null
     * @param item        the item being listed, must not be null
     * @param itemName    the display name of the listed item, must not be null
     * @param category    the auction category, must not be null
     * @param startingBid the minimum bid or BIN price, must not be negative
     * @param type        the auction type ({@link AuctionType#BIN} or {@link AuctionType#AUCTION})
     * @return the UUID of the newly created listing
     */
    public UUID createListing(UUID seller, ItemStack item, String itemName,
                              AuctionCategory category, double startingBid, AuctionType type) {
        Objects.requireNonNull(seller, "seller");
        Objects.requireNonNull(type, "type");
        UUID listingId = UUID.randomUUID();
        AuctionListing listing = new AuctionListing(listingId, seller, item, itemName,
                category, startingBid, type);
        listings.put(listingId, new ListingState(listing));
        return listingId;
    }

    /**
     * Returns all active listings belonging to the given category.
     *
     * @param category the category to filter by, must not be null
     * @return listings in that category; empty list if none
     */
    public List<AuctionListing> getListingsByCategory(AuctionCategory category) {
        Objects.requireNonNull(category, "category");
        return listings.values().stream()
                .map(s -> s.listing)
                .filter(l -> l.category() == category)
                .collect(Collectors.toList());
    }

    /**
     * Places a bid on a bid-based auction or purchases a BIN listing outright.
     *
     * <p>For BIN listings the {@code amount} must meet the starting price and the listing
     * is removed on purchase. For bid-based auctions the first bid must meet the starting
     * bid; subsequent bids must strictly exceed the current highest bid.</p>
     *
     * @param listingId the listing UUID
     * @param bidder    the bidding player's UUID, must not be null
     * @param amount    the bid or purchase amount
     * @return {@code true} if the listing was consumed (BIN purchase), {@code false} if the
     *         bid was recorded but the auction is still open
     * @throws IllegalArgumentException if the listing does not exist, the bidder is the
     *                                  seller, or the amount is too low
     */
    public boolean placeBid(UUID listingId, UUID bidder, double amount) {
        ListingState state = requireListing(listingId);
        Objects.requireNonNull(bidder, "bidder");
        if (bidder.equals(state.listing.seller())) {
            throw new IllegalArgumentException("seller cannot bid on their own listing");
        }
        if (state.listing.type() == AuctionType.BIN) {
            if (amount < state.listing.startingBid()) {
                throw new IllegalArgumentException(
                        "amount must meet the BIN price " + state.listing.startingBid() + ": " + amount);
            }
            listings.remove(listingId);
            return true;
        }
        boolean tooLow = state.highestBidder == null
                ? amount < state.highestBid
                : amount <= state.highestBid;
        if (tooLow) {
            throw new IllegalArgumentException(
                    "bid must exceed current highest bid " + state.highestBid + ": " + amount);
        }
        state.highestBid = amount;
        state.highestBidder = bidder;
        return false;
    }

    /**
     * Ends a bid-based auction, removing it from the active listings.
     *
     * @param listingId the listing UUID
     * @return the winning bidder's UUID, or {@code null} if no bids were placed
     * @throws IllegalArgumentException if the listing does not exist or is a BIN listing
     */
    public UUID endAuction(UUID listingId) {
        ListingState state = requireListing(listingId);
        if (state.listing.type() == AuctionType.BIN) {
            throw new IllegalArgumentException("cannot end a BIN listing as an auction: " + listingId);
        }
        listings.remove(listingId);
        return state.highestBidder;
    }

    /**
     * Cancels an active listing. Only the seller may cancel their own listing.
     *
     * @param listingId the listing UUID
     * @param seller    the cancelling player's UUID, must not be null
     * @throws IllegalArgumentException if the listing does not exist or {@code seller}
     *                                  is not the listing's seller
     */
    public void cancelListing(UUID listingId, UUID seller) {
        ListingState state = requireListing(listingId);
        Objects.requireNonNull(seller, "seller");
        if (!seller.equals(state.listing.seller())) {
            throw new IllegalArgumentException("only the seller can cancel their listing");
        }
        listings.remove(listingId);
    }

    /**
     * Returns whether a listing with the given id is currently active.
     *
     * @param listingId the listing UUID
     * @return {@code true} if the listing exists and has not ended or been cancelled
     */
    public boolean isActive(UUID listingId) {
        return listings.containsKey(listingId);
    }

    /**
     * Returns the {@link AuctionListing} record for an active listing.
     *
     * @param listingId the listing UUID
     * @return the listing record, never null
     * @throws IllegalArgumentException if the listing does not exist
     */
    public AuctionListing getListing(UUID listingId) {
        return requireListing(listingId).listing;
    }

    /**
     * Returns the current highest bid for a bid-based auction.
     *
     * @param listingId the listing UUID
     * @return the current highest bid amount
     * @throws IllegalArgumentException if the listing does not exist
     */
    public double getHighestBid(UUID listingId) {
        return requireListing(listingId).highestBid;
    }

    /**
     * Returns the current highest bidder for a bid-based auction.
     *
     * @param listingId the listing UUID
     * @return the highest bidder's UUID, or {@code null} if no bids have been placed
     * @throws IllegalArgumentException if the listing does not exist
     */
    public UUID getHighestBidder(UUID listingId) {
        return requireListing(listingId).highestBidder;
    }

    /**
     * Returns the ids of all currently active listings.
     *
     * @return an unmodifiable view of the active listing ids
     */
    public Set<UUID> getActiveListings() {
        return Collections.unmodifiableSet(listings.keySet());
    }

    /** Removes all stored listings. */
    public void clear() {
        listings.clear();
    }

    private ListingState requireListing(UUID listingId) {
        ListingState state = listings.get(listingId);
        if (state == null) {
            throw new IllegalArgumentException("no active listing with id: " + listingId);
        }
        return state;
    }
}
