package com.skyblock.economy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

/**
 * Manages auction house listings that support both buy-it-now (BIN) and
 * bid-based auctions. Listings are identified by a UUID assigned on creation.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class AuctionHouseManager {

    /** A single active auction house listing. */
    public record AuctionListing(UUID id, UUID seller, ItemStack item, String itemName,
                                 double startingBid, boolean binListing) {

        public AuctionListing {
            if (id == null) throw new IllegalArgumentException("id must not be null");
            if (seller == null) throw new IllegalArgumentException("seller must not be null");
            if (item == null) throw new IllegalArgumentException("item must not be null");
            if (itemName == null) throw new IllegalArgumentException("itemName must not be null");
            if (startingBid < 0) throw new IllegalArgumentException("startingBid must not be negative: " + startingBid);
        }
    }

    /** Maps each auction category name to its metadata: {maxListings, taxPercent}. */
    public static final Map<String, int[]> AUCTION_CATEGORY_DATA;

    static {
        Map<String, int[]> m = new HashMap<>();
        m.put("Weapons",      new int[]{16, 1});
        m.put("Swords",       new int[]{16, 1});
        m.put("Bows",         new int[]{16, 1});
        m.put("Wands",        new int[]{16, 1});
        m.put("Fishing Rods", new int[]{16, 1});
        m.put("Armor",        new int[]{16, 1});
        m.put("Helmets",      new int[]{16, 1});
        m.put("Chestplates",  new int[]{16, 1});
        m.put("Leggings",     new int[]{16, 1});
        m.put("Boots",        new int[]{16, 1});
        m.put("Accessories",  new int[]{16, 1});
        m.put("Talismans",    new int[]{16, 1});
        m.put("Rings",        new int[]{16, 1});
        m.put("Orbs",         new int[]{16, 1});
        m.put("Necklaces",    new int[]{16, 1});
        m.put("Consumables",  new int[]{16, 1});
        m.put("Potions",      new int[]{16, 1});
        m.put("Scrolls",      new int[]{16, 1});
        m.put("Arrows",       new int[]{16, 1});
        m.put("Blocks",       new int[]{16, 1});
        m.put("Pets",         new int[]{16, 1});
        m.put("Misc",         new int[]{16, 1});
        AUCTION_CATEGORY_DATA = Collections.unmodifiableMap(m);
    }

    private final Map<UUID, ListingState> listings = new HashMap<>();
    private final Map<UUID, List<String>> auctionHistory = new HashMap<>();

    public void recordAuctionEvent(UUID player, String summary) {
        auctionHistory.computeIfAbsent(player, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getAuctionHistory(UUID player) {
        return Collections.unmodifiableList(auctionHistory.getOrDefault(player, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllAuctionHistory() {
        return Collections.unmodifiableMap(auctionHistory);
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

    /**
     * Creates a new auction house listing.
     *
     * @param seller      the selling player's UUID, must not be null
     * @param item        the item being listed, must not be null
     * @param itemName    the display name of the listed item, must not be null
     * @param startingBid the minimum bid or BIN price, must not be negative
     * @param binListing  {@code true} for a buy-it-now listing, {@code false}
     *                    for a bid-based auction
     * @return the UUID of the newly created listing
     */
    public UUID createListing(UUID seller, ItemStack item, String itemName, double startingBid,
                              boolean binListing) {
        UUID listingId = UUID.randomUUID();
        AuctionListing listing = new AuctionListing(listingId, seller, item, itemName,
                startingBid, binListing);
        listings.put(listingId, new ListingState(listing));
        recordAuctionEvent(seller, "Listed " + itemName + " for " + startingBid + " coins");
        return listingId;
    }

    /**
     * Places a bid on a bid-based auction or purchases a BIN listing outright.
     *
     * <p>For BIN listings the {@code amount} must meet the starting price and
     * the buyer receives the item immediately (listing is removed). For
     * bid-based auctions the first bid must meet the starting bid; subsequent
     * bids must strictly exceed the current highest bid.</p>
     *
     * @param listingId the listing UUID
     * @param bidder    the bidding player's UUID, must not be null
     * @param amount    the bid or purchase amount
     * @return {@code true} if the listing was consumed (BIN purchase or exact
     *         match on a BIN), {@code false} if the bid was recorded but the
     *         auction is still open
     * @throws IllegalArgumentException if the listing does not exist, the
     *                                  bidder is null or the seller, or the
     *                                  amount is too low
     */
    public boolean placeBid(UUID listingId, UUID bidder, double amount) {
        ListingState state = requireListing(listingId);
        if (bidder == null) throw new IllegalArgumentException("bidder must not be null");
        if (bidder.equals(state.listing.seller())) {
            throw new IllegalArgumentException("seller cannot bid on their own listing");
        }
        if (state.listing.binListing()) {
            if (amount < state.listing.startingBid()) {
                throw new IllegalArgumentException(
                        "amount must meet the BIN price " + state.listing.startingBid() + ": " + amount);
            }
            listings.remove(listingId);
            recordAuctionEvent(bidder, "Purchased " + state.listing.itemName() + " for " + amount + " coins");
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
     * @return the winning bidder's UUID, or {@code null} if no one bid
     * @throws IllegalArgumentException if the listing does not exist or is
     *                                  a BIN listing
     */
    public UUID endAuction(UUID listingId) {
        ListingState state = requireListing(listingId);
        if (state.listing.binListing()) {
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
     * @throws IllegalArgumentException if the listing does not exist or
     *                                  {@code seller} is not the listing's seller
     */
    public void cancelListing(UUID listingId, UUID seller) {
        ListingState state = requireListing(listingId);
        if (seller == null) throw new IllegalArgumentException("seller must not be null");
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

    private ListingState requireListing(UUID listingId) {
        ListingState state = listings.get(listingId);
        if (state == null) {
            throw new IllegalArgumentException("no active listing with id: " + listingId);
        }
        return state;
    }
}
