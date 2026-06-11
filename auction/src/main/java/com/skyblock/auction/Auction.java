package com.skyblock.auction;

import java.util.Objects;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

/**
 * Data holder for a single auction house listing.
 *
 * <p>Tracks the listed item, the seller, the current price and the listing
 * lifecycle via {@link AuctionStatus}. Instances are not thread-safe; access
 * them from the server main thread or guard them externally.</p>
 */
public final class Auction {

    private final UUID id;
    private final UUID sellerUuid;
    private final ItemStack item;
    private final long startingBid;
    private final long endTime;
    private long currentBid;
    private UUID highestBidderUuid;
    private AuctionStatus status;

    /**
     * Creates a new listing in the {@link AuctionStatus#ACTIVE} state with
     * no bids placed.
     *
     * @param id          unique identifier of this listing
     * @param sellerUuid  unique identifier of the selling player
     * @param item        the item being sold; defensively copied
     * @param startingBid the minimum first bid in coins, must not be negative
     * @param endTime     epoch millisecond timestamp at which the listing ends
     * @throws IllegalArgumentException if {@code startingBid} is negative
     */
    public Auction(UUID id, UUID sellerUuid, ItemStack item, long startingBid, long endTime) {
        this.id = Objects.requireNonNull(id, "id");
        this.sellerUuid = Objects.requireNonNull(sellerUuid, "sellerUuid");
        this.item = Objects.requireNonNull(item, "item").clone();
        if (startingBid < 0) {
            throw new IllegalArgumentException("startingBid must not be negative, got " + startingBid);
        }
        this.startingBid = startingBid;
        this.endTime = endTime;
        this.currentBid = 0L;
        this.highestBidderUuid = null;
        this.status = AuctionStatus.ACTIVE;
    }

    /**
     * Returns the unique identifier of this listing.
     *
     * @return the listing UUID
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the unique identifier of the selling player.
     *
     * @return the seller's UUID
     */
    public UUID getSellerUuid() {
        return sellerUuid;
    }

    /**
     * Returns a copy of the item being sold.
     *
     * @return the listed item
     */
    public ItemStack getItem() {
        return item.clone();
    }

    /**
     * Returns the minimum first bid in coins.
     *
     * @return the starting bid, never negative
     */
    public long getStartingBid() {
        return startingBid;
    }

    /**
     * Returns the epoch millisecond timestamp at which the listing ends.
     *
     * @return the end time
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * Returns the highest bid placed so far in coins.
     *
     * @return the current bid, or 0 if no bid has been placed
     */
    public long getCurrentBid() {
        return currentBid;
    }

    /**
     * Returns the unique identifier of the highest bidder.
     *
     * @return the highest bidder's UUID, or {@code null} if no bid has been
     *         placed
     */
    public UUID getHighestBidderUuid() {
        return highestBidderUuid;
    }

    /**
     * Returns the current lifecycle state of this listing.
     *
     * @return the status, never {@code null}
     */
    public AuctionStatus getStatus() {
        return status;
    }

    /**
     * Sets the current lifecycle state of this listing.
     *
     * @param status the new status
     */
    public void setStatus(AuctionStatus status) {
        this.status = Objects.requireNonNull(status, "status");
    }

    /**
     * Places a bid on this listing, replacing the previous highest bid.
     *
     * @param bidderUuid unique identifier of the bidding player
     * @param amount     the bid in coins; must be at least the starting bid
     *                   and exceed the current bid
     * @throws IllegalStateException    if the listing is not open for bids
     * @throws IllegalArgumentException if {@code amount} is below the
     *                                  starting bid or not above the current
     *                                  bid
     */
    public void placeBid(UUID bidderUuid, long amount) {
        Objects.requireNonNull(bidderUuid, "bidderUuid");
        if (!status.isOpen()) {
            throw new IllegalStateException("auction is " + status + ", not open for bids");
        }
        if (amount < startingBid) {
            throw new IllegalArgumentException(
                    "bid must be at least the starting bid " + startingBid + ", got " + amount);
        }
        if (amount <= currentBid) {
            throw new IllegalArgumentException(
                    "bid must exceed the current bid " + currentBid + ", got " + amount);
        }
        this.currentBid = amount;
        this.highestBidderUuid = bidderUuid;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Auction other && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Auction{id=" + id + ", seller=" + sellerUuid + ", status=" + status
                + ", currentBid=" + currentBid + '}';
    }
}
