package com.skyblock.plugin.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * In-memory registry of active auction house listings.
 *
 * <p>Holds a flat {@link List} of {@link Auction} records shared across all
 * players, one entry per active listing. Listings are added when a player puts
 * an item up for auction and removed when they are bought, cancelled or end.
 * Not thread-safe; access from the main server thread.</p>
 */
public final class AuctionManager {

    /**
     * A single active auction house listing.
     *
     * @param id        the listing's unique id
     * @param seller    the selling player's UUID
     * @param itemName  the name of the item being sold
     * @param price     the current price (highest bid, or starting bid if none)
     * @param endTime   the epoch millisecond at which the auction ends
     */
    public record Auction(UUID id, UUID seller, String itemName, double price, long endTime) {}

    private static final AuctionManager INSTANCE = new AuctionManager();

    private final List<Auction> auctions = new ArrayList<>();

    private AuctionManager() {}

    public static AuctionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns an unmodifiable view of every active listing.
     *
     * @return the active auctions
     */
    public List<Auction> getAuctions() {
        return Collections.unmodifiableList(auctions);
    }

    /**
     * Adds a listing to the active auctions.
     *
     * @param auction the listing to add
     */
    public void addAuction(Auction auction) {
        auctions.add(auction);
    }

    /**
     * Removes the listing with the given id.
     *
     * @param id the listing id
     * @return {@code true} if a listing was removed
     */
    public boolean removeAuction(UUID id) {
        return auctions.removeIf(a -> a.id().equals(id));
    }
}
