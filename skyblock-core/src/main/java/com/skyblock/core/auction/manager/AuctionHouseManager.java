package com.skyblock.core.auction.manager;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
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

    /** Maps each AH category name to its constituent item-type labels. */
    public static final Map<String, String[]> ITEM_CATEGORIES;

    static {
        Map<String, String[]> m = new HashMap<>();
        m.put("Weapons",      new String[]{"Sword", "Bow", "Fishing Rod", "Wand", "Shortbow", "Long Bow"});
        m.put("Armor",        new String[]{"Helmet", "Chestplate", "Leggings", "Boots"});
        m.put("Accessories",  new String[]{"Talisman", "Ring", "Orb", "Necklace", "Gloves", "Belt", "Cloak", "Bracelet", "Gauntlet"});
        m.put("Consumables",  new String[]{"Potion", "Scroll", "Arrow", "Quiver Arrow", "Food"});
        m.put("Blocks",       new String[]{"Block", "Mineral", "Wood", "Stone", "Sand", "Glass", "Planks", "Log"});
        m.put("Mobs",         new String[]{"Pet", "Pet Item", "Monster Drop", "Animal Drop", "Sea Creature Drop"});
        m.put("Misc",         new String[]{"Essence", "Rune", "Bait", "Dye", "Travel Scroll", "Reforge Stone", "Shards", "Enchanted Book"});
        ITEM_CATEGORIES = Collections.unmodifiableMap(m);
    }

    /**
     * Fraction of a listing's starting bid that each successive bid must add on top of the
     * current highest bid (the automatic minimum-bid increment for ascending auctions).
     */
    public static final double MIN_BID_INCREMENT = 0.15;

    /**
     * Tax taken from the final sale price when a seller claims their coins
     * (1% of the winning bid / BIN price).
     */
    public static final double CLAIM_TAX = 0.01;

    /**
     * Returns the fraction of the starting bid charged as an up-front listing fee,
     * tiered by listing value. Higher-value listings pay a higher rate.
     *
     * @param startingBid the starting bid or BIN price
     * @return the listing-fee rate (e.g. {@code 0.01} for 1%)
     */
    public static double listingFeeRate(double startingBid) {
        if (startingBid >= 100_000_000) return 0.025;
        if (startingBid >= 10_000_000)  return 0.02;
        if (startingBid >= 1_000_000)   return 0.015;
        return 0.01;
    }

    /**
     * Returns the up-front listing fee, in whole coins, for a listing at the given
     * starting bid using the {@linkplain #listingFeeRate(double) tiered rate}.
     *
     * @param startingBid the starting bid or BIN price
     * @return the listing fee in coins (rounded to the nearest whole coin)
     */
    public static long calculateListingFee(double startingBid) {
        return Math.round(startingBid * listingFeeRate(startingBid));
    }

    private static final AuctionHouseManager INSTANCE = new AuctionHouseManager();

    /**
     * Lightweight persistent auction item.
     *
     * @param itemName  display name of the item
     * @param seller    UUID of the selling player
     * @param price     buy-it-now price in coins
     * @param endEpoch  unix epoch milliseconds when the listing expires
     */
    public record AuctionItem(String itemName, UUID seller, long price, long endEpoch) {
        public AuctionItem {
            Objects.requireNonNull(itemName, "itemName");
            Objects.requireNonNull(seller, "seller");
            if (price < 0) throw new IllegalArgumentException("price must not be negative: " + price);
        }
    }

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
        /** Unix epoch milliseconds when the listing expires, or {@code 0} for no expiry. */
        long endEpoch;

        ListingState(AuctionListing listing) {
            this.listing = listing;
            this.highestBid = listing.startingBid();
        }
    }

    private final Map<UUID, ListingState> listings = new HashMap<>();
    private final Map<UUID, AuctionItem> items = new HashMap<>();
    private final Map<UUID, Integer> auctionCounts = new HashMap<>();
    private final Map<UUID, List<String>> auctionHistory = new HashMap<>();
    /** Coins held for players to claim: sale proceeds and refunds for outbid bidders. */
    private final Map<UUID, Double> pendingCoins = new HashMap<>();
    /** Items held for players to claim: won auctions and unsold listings returned to sellers. */
    private final Map<UUID, List<ItemStack>> pendingItems = new HashMap<>();

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
        return createListing(seller, item, itemName, category, startingBid, type, 0L);
    }

    /**
     * Creates a new auction house listing that expires at a fixed time.
     *
     * <p>The item is held in escrow by the manager for the duration of the listing.
     * Once {@code endEpoch} has passed, {@link #processExpired(long)} settles the
     * listing: an auction with bids is awarded to the highest bidder, while an
     * unsold listing is returned to the seller's {@linkplain #claimItems(UUID) claim queue}.</p>
     *
     * @param seller      the selling player's UUID, must not be null
     * @param item        the item being listed, must not be null
     * @param itemName    the display name of the listed item, must not be null
     * @param category    the auction category, must not be null
     * @param startingBid the minimum bid or BIN price, must not be negative
     * @param type        the auction type ({@link AuctionType#BIN} or {@link AuctionType#AUCTION})
     * @param endEpoch    unix epoch milliseconds when the listing expires, or {@code 0} for no expiry
     * @return the UUID of the newly created listing
     */
    public UUID createListing(UUID seller, ItemStack item, String itemName,
                              AuctionCategory category, double startingBid, AuctionType type, long endEpoch) {
        Objects.requireNonNull(seller, "seller");
        Objects.requireNonNull(type, "type");
        UUID listingId = UUID.randomUUID();
        AuctionListing listing = new AuctionListing(listingId, seller, item, itemName,
                category, startingBid, type);
        ListingState state = new ListingState(listing);
        state.endEpoch = endEpoch;
        listings.put(listingId, state);
        recordAuction(seller, "Listed " + itemName + " (" + type.getDisplayName() + ") starting at " + startingBid
                + " coins (fee " + calculateListingFee(startingBid) + ")");
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
            settleSale(state, bidder, amount);
            recordAuction(bidder, "Purchased " + state.listing.itemName() + " for " + amount + " coins");
            return true;
        }
        double minBid = minimumBidFor(state);
        if (amount < minBid) {
            throw new IllegalArgumentException(
                    "bid must be at least the minimum next bid " + minBid + ": " + amount);
        }
        // Refund the outbid leader's escrowed coins before recording the new top bid.
        if (state.highestBidder != null) {
            creditCoins(state.highestBidder, state.highestBid);
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
        if (state.highestBidder != null) {
            // Winner's escrowed bid pays the seller; the item is released to the winner.
            settleSale(state, state.highestBidder, state.highestBid);
        } else {
            // No bids: the listing is returned to the seller.
            creditItem(state.listing.seller(), state.listing.item());
        }
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
        // Refund any standing bid and return the item to the seller.
        if (state.highestBidder != null) {
            creditCoins(state.highestBidder, state.highestBid);
        }
        creditItem(state.listing.seller(), state.listing.item());
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
     * Returns the minimum amount the next bid must meet for a bid-based auction.
     *
     * <p>Until the first bid is placed this is the starting bid; afterwards each bid must add
     * at least {@link #MIN_BID_INCREMENT} of the starting bid on top of the current highest bid.</p>
     *
     * @param listingId the listing UUID
     * @return the minimum acceptable next bid amount
     * @throws IllegalArgumentException if the listing does not exist
     */
    public double getMinimumBid(UUID listingId) {
        return minimumBidFor(requireListing(listingId));
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

    // -------------------------------------------------------------------------
    // Timed auctions: expiry and settlement
    // -------------------------------------------------------------------------

    /**
     * Returns the expiry time of a listing.
     *
     * @param listingId the listing UUID
     * @return unix epoch milliseconds when the listing expires, or {@code 0} if it has no expiry
     * @throws IllegalArgumentException if the listing does not exist
     */
    public long getEndEpoch(UUID listingId) {
        return requireListing(listingId).endEpoch;
    }

    /**
     * Returns whether a listing has passed its expiry time at the given moment.
     *
     * @param listingId the listing UUID
     * @param now       the current time in unix epoch milliseconds
     * @return {@code true} if the listing has a non-zero expiry that {@code now} has reached
     * @throws IllegalArgumentException if the listing does not exist
     */
    public boolean isExpired(UUID listingId, long now) {
        long end = requireListing(listingId).endEpoch;
        return end > 0 && now >= end;
    }

    /**
     * Settles every listing whose expiry time has passed at {@code now}, removing it from
     * the active listings. An auction with bids is awarded to the highest bidder (the seller
     * receives the proceeds minus {@link #CLAIM_TAX}); an unsold auction or expired BIN
     * listing is returned to the seller's claim queue.
     *
     * @param now the current time in unix epoch milliseconds
     * @return the ids of the listings that were settled
     */
    public List<UUID> processExpired(long now) {
        List<UUID> settled = new ArrayList<>();
        for (Map.Entry<UUID, ListingState> entry : new ArrayList<>(listings.entrySet())) {
            ListingState state = entry.getValue();
            if (state.endEpoch <= 0 || now < state.endEpoch) continue;
            listings.remove(entry.getKey());
            if (state.listing.type() == AuctionType.AUCTION && state.highestBidder != null) {
                settleSale(state, state.highestBidder, state.highestBid);
            } else {
                creditItem(state.listing.seller(), state.listing.item());
            }
            settled.add(entry.getKey());
        }
        return settled;
    }

    // -------------------------------------------------------------------------
    // Escrow and claim queues
    // -------------------------------------------------------------------------

    /**
     * Returns the coins currently escrowed for an active bid-based auction (the highest bid),
     * or {@code 0} if no bid has been placed.
     *
     * @param listingId the listing UUID
     * @return the escrowed bid amount
     * @throws IllegalArgumentException if the listing does not exist
     */
    public double getEscrowedBid(UUID listingId) {
        ListingState state = requireListing(listingId);
        return state.highestBidder == null ? 0.0 : state.highestBid;
    }

    /**
     * Returns the coins waiting to be collected by the given player (sale proceeds and
     * refunds for outbid bids) without removing them.
     *
     * @param player the player's UUID, must not be null
     * @return the pending coin balance (0 if none)
     */
    public double getPendingCoins(UUID player) {
        Objects.requireNonNull(player, "player");
        return pendingCoins.getOrDefault(player, 0.0);
    }

    /**
     * Collects and clears the coins waiting for the given player.
     *
     * @param player the player's UUID, must not be null
     * @return the collected coin amount (0 if none were pending)
     */
    public double claimCoins(UUID player) {
        Objects.requireNonNull(player, "player");
        Double amount = pendingCoins.remove(player);
        return amount == null ? 0.0 : amount;
    }

    /**
     * Returns an unmodifiable view of the items waiting to be collected by the given player
     * (won auctions and unsold listings returned to the seller) without removing them.
     *
     * @param player the player's UUID, must not be null
     * @return the pending items; empty list if none
     */
    public List<ItemStack> getPendingItems(UUID player) {
        Objects.requireNonNull(player, "player");
        return Collections.unmodifiableList(
                pendingItems.getOrDefault(player, Collections.emptyList()));
    }

    /**
     * Collects and clears the items waiting for the given player.
     *
     * @param player the player's UUID, must not be null
     * @return the collected items; empty list if none were pending
     */
    public List<ItemStack> claimItems(UUID player) {
        Objects.requireNonNull(player, "player");
        List<ItemStack> claimed = pendingItems.remove(player);
        return claimed == null ? new ArrayList<>() : claimed;
    }

    /** Awards a sold item to the buyer and credits the seller their proceeds less {@link #CLAIM_TAX}. */
    private void settleSale(ListingState state, UUID buyer, double price) {
        creditItem(buyer, state.listing.item());
        creditCoins(state.listing.seller(), price * (1.0 - CLAIM_TAX));
    }

    private void creditCoins(UUID player, double amount) {
        pendingCoins.merge(player, amount, Double::sum);
    }

    private void creditItem(UUID player, ItemStack item) {
        pendingItems.computeIfAbsent(player, k -> new ArrayList<>()).add(item);
    }

    // -------------------------------------------------------------------------
    // Auction counts
    // -------------------------------------------------------------------------

    /**
     * Returns the number of auctions the given player has created.
     *
     * @param player the player's UUID, must not be null
     * @return the player's total auction count (0 if never listed)
     */
    public int getAuctionCount(UUID player) {
        Objects.requireNonNull(player, "player");
        return auctionCounts.getOrDefault(player, 0);
    }

    /**
     * Increments the auction count for the given player by one.
     *
     * @param player the player's UUID, must not be null
     */
    public void incrementAuctionCount(UUID player) {
        Objects.requireNonNull(player, "player");
        auctionCounts.merge(player, 1, Integer::sum);
    }

    /**
     * Sets the auction count for the given player.
     *
     * @param player the player's UUID, must not be null
     * @param count  the count to set, must not be negative
     */
    public void setAuctionCount(UUID player, int count) {
        Objects.requireNonNull(player, "player");
        if (count < 0) throw new IllegalArgumentException("count must not be negative: " + count);
        auctionCounts.put(player, count);
    }

    // -------------------------------------------------------------------------
    // Auction history
    // -------------------------------------------------------------------------

    /**
     * Records an auction event for the given player.
     *
     * @param player  the player's UUID, must not be null
     * @param summary a human-readable description of the event
     */
    public void recordAuction(UUID player, String summary) {
        Objects.requireNonNull(player, "player");
        auctionHistory.computeIfAbsent(player, k -> new ArrayList<>()).add(summary);
    }

    /**
     * Returns the auction history for the given player.
     *
     * @param player the player's UUID, must not be null
     * @return unmodifiable list of history entries; empty if none recorded
     */
    public List<String> getAuctionHistory(UUID player) {
        Objects.requireNonNull(player, "player");
        return Collections.unmodifiableList(
                auctionHistory.getOrDefault(player, Collections.emptyList()));
    }

    /**
     * Returns the auction history for all players.
     *
     * @return unmodifiable map of player UUID to their history entries
     */
    public Map<UUID, List<String>> getAllAuctionHistory() {
        return Collections.unmodifiableMap(auctionHistory);
    }

    public String getAuctionHouseStats(UUID player) {
        List<String> history = auctionHistory.getOrDefault(player, Collections.emptyList());
        int auctionsCreated = 0;
        int itemsSold = 0;
        long totalCoins = 0;
        for (String entry : history) {
            if (entry.startsWith("Listed")) {
                auctionsCreated++;
            } else if (entry.startsWith("Purchased")) {
                itemsSold++;
            }
            int idx = entry.lastIndexOf(" coins");
            if (idx > 0) {
                String before = entry.substring(0, idx);
                int space = before.lastIndexOf(' ');
                if (space >= 0) {
                    try {
                        totalCoins += (long) Double.parseDouble(before.substring(space + 1));
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return "Auction Stats: Auctions Created: " + auctionsCreated + ", Items Sold: " + itemsSold + ", Total Coins: " + totalCoins;
    }

    // -------------------------------------------------------------------------
    // Player-facing listing helper
    // -------------------------------------------------------------------------

    /**
     * Lists an item on behalf of a player.
     *
     * @param player the listing player, must not be null
     * @param item   the item to list, must not be null
     * @param price  the BIN price or starting bid in coins (must be ≥ 0)
     * @param isBin  {@code true} for buy-it-now, {@code false} for a bid-based auction
     * @return the UUID of the newly created listing
     */
    public UUID listItem(Player player, ItemStack item, long price, boolean isBin) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(item, "item");
        AuctionType type = isBin ? AuctionType.BIN : AuctionType.AUCTION;
        return createListing(player.getUniqueId(), item, deriveItemName(item), AuctionCategory.MISC, price, type);
    }

    private static String deriveItemName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) return meta.getDisplayName();
        String raw = item.getType().name().replace('_', ' ');
        StringBuilder sb = new StringBuilder();
        for (String word : raw.split(" ")) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) sb.append(word.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // AuctionItem storage
    // -------------------------------------------------------------------------

    /**
     * Lists a new {@link AuctionItem} in the auction house.
     *
     * @param seller    the selling player's UUID
     * @param itemName  display name of the item
     * @param price     buy-it-now price in coins (must be ≥ 0)
     * @param endEpoch  unix epoch milliseconds when the listing expires
     * @return the UUID assigned to the new listing
     */
    public UUID addItem(UUID seller, String itemName, long price, long endEpoch) {
        UUID id = UUID.randomUUID();
        items.put(id, new AuctionItem(itemName, seller, price, endEpoch));
        return id;
    }

    /**
     * Returns the {@link AuctionItem} for the given id, or {@code null} if not found.
     *
     * @param id the listing UUID
     * @return the item, or {@code null}
     */
    public AuctionItem getItem(UUID id) {
        return items.get(id);
    }

    /**
     * Completes a purchase of the given {@link AuctionItem} listing.
     *
     * <p>Removes the listing and records the transaction in the buyer's auction history.</p>
     *
     * @param id    the listing UUID
     * @param buyer the purchasing player's UUID, must not be null
     * @return the purchased {@link AuctionItem}
     * @throws IllegalArgumentException if the listing does not exist or the buyer is the seller
     */
    public AuctionItem purchaseItem(UUID id, UUID buyer) {
        Objects.requireNonNull(buyer, "buyer");
        AuctionItem item = items.get(id);
        if (item == null) throw new IllegalArgumentException("no listing with id: " + id);
        if (item.seller().equals(buyer)) throw new IllegalArgumentException("seller cannot purchase their own listing");
        items.remove(id);
        recordAuction(buyer, "Purchased " + item.itemName() + " for " + item.price() + " coins");
        return item;
    }

    /**
     * Cancels a listing. Only the original seller may cancel.
     *
     * @param id     the listing UUID
     * @param seller the cancelling player's UUID
     * @throws IllegalArgumentException if the listing does not exist or the caller is not the seller
     */
    public void cancelItem(UUID id, UUID seller) {
        AuctionItem item = items.get(id);
        if (item == null) throw new IllegalArgumentException("no listing with id: " + id);
        if (!item.seller().equals(seller)) throw new IllegalArgumentException("only the seller can cancel their listing");
        items.remove(id);
    }

    /**
     * Returns an unmodifiable snapshot of all stored {@link AuctionItem}} listings.
     *
     * @return list of all items; never null
     */
    public List<AuctionItem> getActiveItems() {
        return Collections.unmodifiableList(new ArrayList<>(items.values()));
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "auctionhouse.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        auctionCounts.clear();
        auctionHistory.clear();
        pendingCoins.clear();
        pendingItems.clear();
        if (cfg.isConfigurationSection("auctionCounts")) {
            for (String key : cfg.getConfigurationSection("auctionCounts").getKeys(false)) {
                try {
                    auctionCounts.put(UUID.fromString(key), cfg.getInt("auctionCounts." + key));
                } catch (IllegalArgumentException ignored) {
                    // skip malformed entries
                }
            }
        }
        items.clear();
        for (String key : cfg.getKeys(false)) {
            if (key.equals("listings") || key.equals("pendingCoins") || key.equals("pendingItems")
                    || key.equals("auctionCounts") || key.equals("auctionHistory")) continue;
            try {
                UUID id = UUID.fromString(key);
                String itemName = cfg.getString(key + ".itemName");
                if (itemName == null) continue;
                UUID seller = UUID.fromString(cfg.getString(key + ".seller", ""));
                long price = cfg.getLong(key + ".price", 0L);
                long endEpoch = cfg.getLong(key + ".endEpoch", 0L);
                items.put(id, new AuctionItem(itemName, seller, price, endEpoch));
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
        if (cfg.isConfigurationSection("auctionHistory")) {
            for (String key : cfg.getConfigurationSection("auctionHistory").getKeys(false)) {
                try {
                    auctionHistory.put(UUID.fromString(key),
                            new ArrayList<>(cfg.getStringList("auctionHistory." + key)));
                } catch (IllegalArgumentException ignored) {
                    // skip malformed entries
                }
            }
        }
        listings.clear();
        if (cfg.isConfigurationSection("listings")) {
            for (String key : cfg.getConfigurationSection("listings").getKeys(false)) {
                try {
                    UUID id = UUID.fromString(key);
                    UUID seller = UUID.fromString(cfg.getString("listings." + key + ".seller", ""));
                    String itemName = cfg.getString("listings." + key + ".itemName");
                    if (itemName == null) continue;
                    AuctionCategory category = AuctionCategory.valueOf(
                            cfg.getString("listings." + key + ".category", "MISC"));
                    double startingBid = cfg.getDouble("listings." + key + ".startingBid", 0.0);
                    AuctionType type = AuctionType.valueOf(
                            cfg.getString("listings." + key + ".type", "BIN"));
                    org.bukkit.inventory.ItemStack stack =
                            cfg.getItemStack("listings." + key + ".item");
                    if (stack == null) continue;
                    AuctionListing listing = new AuctionListing(
                            id, seller, stack, itemName, category, startingBid, type);
                    ListingState state = new ListingState(listing);
                    state.highestBid = cfg.getDouble("listings." + key + ".highestBid", startingBid);
                    state.endEpoch = cfg.getLong("listings." + key + ".endEpoch", 0L);
                    String bidderStr = cfg.getString("listings." + key + ".highestBidder");
                    if (bidderStr != null && !bidderStr.isEmpty()) {
                        state.highestBidder = UUID.fromString(bidderStr);
                    }
                    listings.put(id, state);
                } catch (IllegalArgumentException ignored) {
                    // skip malformed entries
                }
            }
        }
        if (cfg.isConfigurationSection("pendingCoins")) {
            for (String key : cfg.getConfigurationSection("pendingCoins").getKeys(false)) {
                try {
                    pendingCoins.put(UUID.fromString(key), cfg.getDouble("pendingCoins." + key));
                } catch (IllegalArgumentException ignored) {
                    // skip malformed entries
                }
            }
        }
        if (cfg.isConfigurationSection("pendingItems")) {
            for (String key : cfg.getConfigurationSection("pendingItems").getKeys(false)) {
                try {
                    List<?> raw = cfg.getList("pendingItems." + key, Collections.emptyList());
                    List<ItemStack> stacks = new ArrayList<>();
                    for (Object o : raw) {
                        if (o instanceof ItemStack) stacks.add((ItemStack) o);
                    }
                    pendingItems.put(UUID.fromString(key), stacks);
                } catch (IllegalArgumentException ignored) {
                    // skip malformed entries
                }
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "auctionhouse.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : auctionCounts.entrySet()) {
            cfg.set("auctionCounts." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, AuctionItem> entry : items.entrySet()) {
            String key = entry.getKey().toString();
            AuctionItem item = entry.getValue();
            cfg.set(key + ".itemName", item.itemName());
            cfg.set(key + ".seller", item.seller().toString());
            cfg.set(key + ".price", item.price());
            cfg.set(key + ".endEpoch", item.endEpoch());
        }
        for (Map.Entry<UUID, List<String>> entry : auctionHistory.entrySet()) {
            cfg.set("auctionHistory." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, ListingState> entry : listings.entrySet()) {
            String key = "listings." + entry.getKey().toString();
            ListingState state = entry.getValue();
            AuctionListing listing = state.listing;
            cfg.set(key + ".seller", listing.seller().toString());
            cfg.set(key + ".itemName", listing.itemName());
            cfg.set(key + ".category", listing.category().name());
            cfg.set(key + ".startingBid", listing.startingBid());
            cfg.set(key + ".type", listing.type().name());
            cfg.set(key + ".item", listing.item());
            cfg.set(key + ".highestBid", state.highestBid);
            cfg.set(key + ".endEpoch", state.endEpoch);
            if (state.highestBidder != null) {
                cfg.set(key + ".highestBidder", state.highestBidder.toString());
            }
        }
        for (Map.Entry<UUID, Double> entry : pendingCoins.entrySet()) {
            cfg.set("pendingCoins." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, List<ItemStack>> entry : pendingItems.entrySet()) {
            cfg.set("pendingItems." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save auctionhouse.yml", e);
        }
    }

    /** Removes all stored listings, counts, and history. */
    public void clear() {
        listings.clear();
        items.clear();
        auctionCounts.clear();
        auctionHistory.clear();
        pendingCoins.clear();
        pendingItems.clear();
    }

    private static double minimumBidFor(ListingState state) {
        if (state.highestBidder == null) {
            return state.listing.startingBid();
        }
        return state.highestBid + Math.round(state.listing.startingBid() * MIN_BID_INCREMENT);
    }

    private ListingState requireListing(UUID listingId) {
        ListingState state = listings.get(listingId);
        if (state == null) {
            throw new IllegalArgumentException("no active listing with id: " + listingId);
        }
        return state;
    }
}
