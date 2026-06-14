package com.skyblock.core.auctionhouse;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
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
public final class AuctionHouseManager {

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
                                 double startingBid, AuctionType type) {

        public AuctionListing {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(seller, "seller");
            Objects.requireNonNull(itemName, "itemName");
            Objects.requireNonNull(type, "type");
            if (startingBid < 0) {
                throw new IllegalArgumentException("startingBid must not be negative: " + startingBid);
            }
        }
    }

    /**
     * Immutable snapshot of an auction entry including live bid state and expiry.
     *
     * @param seller     the selling player's UUID
     * @param item       the display name of the listed item
     * @param startPrice the opening bid price (coins)
     * @param currentBid the current highest bid (equals startPrice before any bids)
     * @param topBidder  the current highest bidder, or {@code null} if no bids yet
     * @param expiry     epoch-millis at which the auction closes (0 means no expiry)
     */
    public record AuctionEntry(UUID seller, String item, long startPrice,
                               long currentBid, UUID topBidder, long expiry) {

        public AuctionEntry {
            Objects.requireNonNull(seller, "seller");
            Objects.requireNonNull(item, "item");
            if (startPrice < 0) {
                throw new IllegalArgumentException("startPrice must not be negative: " + startPrice);
            }
            if (currentBid < startPrice) {
                throw new IllegalArgumentException("currentBid must not be less than startPrice");
            }
        }
    }

    private static final class State {
        final AuctionListing entry;
        double currentBid;
        UUID highestBidder;
        long expiry;

        State(AuctionListing entry, long expiry) {
            this.entry = entry;
            this.currentBid = entry.startingBid();
            this.expiry = expiry;
        }
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

    private static final AuctionHouseManager INSTANCE = new AuctionHouseManager();

    private final Map<UUID, State> listings = new HashMap<>();

    private AuctionHouseManager() {}

    /**
     * Returns the single shared {@code AuctionHouseManager} instance.
     *
     * @return the singleton instance
     */
    public static AuctionHouseManager getInstance() {
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
     * @return the new listing's UUID
     */
    public UUID createListing(UUID seller, String itemName,
                              double startingBid, AuctionType type) {
        Objects.requireNonNull(seller, "seller");
        Objects.requireNonNull(type, "type");
        UUID id = UUID.randomUUID();
        listings.put(id, new State(new AuctionListing(id, seller, itemName, startingBid, type), 0L));
        return id;
    }

    /**
     * Posts a new bid-based auction using the {@link AuctionEntry} shape and returns its id.
     *
     * @param seller     the selling player's UUID
     * @param item       the display name of the item
     * @param startPrice the opening bid price (must be ≥ 0)
     * @param expiry     epoch-millis at which the auction closes (0 for no expiry)
     * @return the new listing's UUID
     */
    public UUID postEntry(UUID seller, String item, long startPrice, long expiry) {
        Objects.requireNonNull(seller, "seller");
        Objects.requireNonNull(item, "item");
        UUID id = UUID.randomUUID();
        listings.put(id, new State(
                new AuctionListing(id, seller, item, startPrice, AuctionType.AUCTION), expiry));
        return id;
    }

    /**
     * Returns a live {@link AuctionEntry} snapshot for the given listing.
     *
     * @param listingId the listing UUID
     * @return an {@code AuctionEntry} reflecting the current bid state
     * @throws IllegalArgumentException if the listing does not exist
     */
    public AuctionEntry getEntry(UUID listingId) {
        State s = requireState(listingId);
        return new AuctionEntry(
                s.entry.seller(),
                s.entry.itemName(),
                (long) s.entry.startingBid(),
                (long) s.currentBid,
                s.highestBidder,
                s.expiry);
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

    // ---------------------------------------------------------------------------
    // Persistence
    // ---------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "auctionhouse.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        listings.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID id = UUID.fromString(key);
                UUID seller = UUID.fromString(cfg.getString(key + ".seller", ""));
                String itemName = cfg.getString(key + ".itemName");
                if (itemName == null) continue;
                double startingBid = cfg.getDouble(key + ".startingBid", 0.0);
                AuctionType type = AuctionType.valueOf(cfg.getString(key + ".type", "AUCTION"));
                long expiry = cfg.getLong(key + ".expiry", 0L);
                AuctionListing listing = new AuctionListing(id, seller, itemName, startingBid, type);
                State state = new State(listing, expiry);
                state.currentBid = cfg.getDouble(key + ".currentBid", startingBid);
                String bidderStr = cfg.getString(key + ".highestBidder");
                if (bidderStr != null && !bidderStr.isEmpty()) {
                    state.highestBidder = UUID.fromString(bidderStr);
                }
                listings.put(id, state);
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "auctionhouse.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, State> entry : listings.entrySet()) {
            String key = entry.getKey().toString();
            State state = entry.getValue();
            cfg.set(key + ".seller", state.entry.seller().toString());
            cfg.set(key + ".itemName", state.entry.itemName());
            cfg.set(key + ".startingBid", state.entry.startingBid());
            cfg.set(key + ".type", state.entry.type().name());
            cfg.set(key + ".expiry", state.expiry);
            cfg.set(key + ".currentBid", state.currentBid);
            if (state.highestBidder != null) {
                cfg.set(key + ".highestBidder", state.highestBidder.toString());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save auctionhouse.yml", e);
        }
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
