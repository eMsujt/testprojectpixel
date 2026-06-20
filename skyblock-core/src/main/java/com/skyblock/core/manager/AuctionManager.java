package com.skyblock.core.manager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Singleton managing buy-it-now auction listings.
 *
 * <p>Listings are identified by a UUID assigned on creation. Coins and items
 * are escrowed here until the player calls {@link #claimCoins(UUID)} or
 * {@link #claimItems(UUID)}.</p>
 *
 * <p>Not thread-safe; synchronize externally when accessed from multiple threads.</p>
 */
public final class AuctionManager {

    private static final AuctionManager INSTANCE = new AuctionManager();

    /** Flat listing fee as a fraction of the asking price (1%). */
    public static final double LISTING_FEE_RATE = 0.01;
    /** Claim tax deducted from proceeds when a seller collects their coins (1%). */
    public static final double CLAIM_TAX = 0.01;

    /** An active listing in the auction. */
    public record Listing(UUID id, UUID seller, ItemStack item,
                          String itemName, String category, double price) {
        public Listing {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(seller, "seller");
            Objects.requireNonNull(item, "item");
            Objects.requireNonNull(itemName, "itemName");
            Objects.requireNonNull(category, "category");
            if (price < 0) throw new IllegalArgumentException("price must not be negative: " + price);
        }
    }

    private final Map<UUID, Listing> listings = new HashMap<>();
    private final Map<UUID, Double> pendingCoins = new HashMap<>();
    private final Map<UUID, List<ItemStack>> pendingItems = new HashMap<>();

    private AuctionManager() {}

    /** Returns the single shared {@code AuctionManager} instance. */
    public static AuctionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a new buy-it-now listing.
     *
     * @param seller    the selling player's UUID
     * @param item      the item being listed (held in escrow)
     * @param itemName  display name shown in the auction UI
     * @param category  the category label (e.g. "Weapons", "Armor")
     * @param price     the buy-it-now price in coins (must be &ge; 0)
     * @return the UUID of the newly created listing
     */
    public UUID createListing(UUID seller, ItemStack item, String itemName, String category, double price) {
        Objects.requireNonNull(seller, "seller");
        Objects.requireNonNull(item, "item");
        Objects.requireNonNull(itemName, "itemName");
        Objects.requireNonNull(category, "category");
        if (price < 0) throw new IllegalArgumentException("price must not be negative: " + price);
        UUID id = UUID.randomUUID();
        listings.put(id, new Listing(id, seller, item, itemName, category, price));
        return id;
    }

    /**
     * Purchases the listing, consuming it and escrowing coins to the seller and the item to the buyer.
     *
     * @param listingId the listing UUID
     * @param buyer     the purchasing player's UUID
     * @throws IllegalArgumentException if the listing is unknown, or the buyer is the seller
     */
    public void purchase(UUID listingId, UUID buyer) {
        Objects.requireNonNull(buyer, "buyer");
        Listing listing = requireListing(listingId);
        if (buyer.equals(listing.seller())) {
            throw new IllegalArgumentException("seller cannot purchase their own listing");
        }
        listings.remove(listingId);
        double net = listing.price() * (1.0 - CLAIM_TAX);
        pendingCoins.merge(listing.seller(), net, Double::sum);
        pendingItems.computeIfAbsent(buyer, k -> new ArrayList<>()).add(listing.item());
    }

    /**
     * Cancels a listing and returns the item to the seller's claim queue.
     *
     * @param listingId the listing UUID
     * @param seller    the cancelling player (must match the listing's seller)
     * @throws IllegalArgumentException if the listing is unknown or the caller is not the seller
     */
    public void cancelListing(UUID listingId, UUID seller) {
        Objects.requireNonNull(seller, "seller");
        Listing listing = requireListing(listingId);
        if (!seller.equals(listing.seller())) {
            throw new IllegalArgumentException("only the seller can cancel their listing");
        }
        listings.remove(listingId);
        pendingItems.computeIfAbsent(seller, k -> new ArrayList<>()).add(listing.item());
    }

    /** Returns whether the listing is currently active. */
    public boolean isActive(UUID listingId) {
        return listings.containsKey(listingId);
    }

    /** Returns the {@link Listing} for the given id. */
    public Listing getListing(UUID listingId) {
        return requireListing(listingId);
    }

    /** Returns an unmodifiable list of all active listings. */
    public List<Listing> getListings() {
        return Collections.unmodifiableList(new ArrayList<>(listings.values()));
    }

    /**
     * Returns all active listings whose category matches the given label (case-insensitive).
     *
     * @param category the category label to filter by
     */
    public List<Listing> getListingsByCategory(String category) {
        Objects.requireNonNull(category, "category");
        return listings.values().stream()
                .filter(l -> l.category().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    /**
     * Returns all active listings whose item name contains the given query (case-insensitive).
     *
     * @param query the substring to search for
     */
    public List<Listing> searchListings(String query) {
        Objects.requireNonNull(query, "query");
        String lower = query.toLowerCase();
        return listings.values().stream()
                .filter(l -> l.itemName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    /**
     * Returns all active listings created by the given seller.
     *
     * @param seller the seller's UUID
     */
    public List<Listing> getListingsBySeller(UUID seller) {
        Objects.requireNonNull(seller, "seller");
        return listings.values().stream()
                .filter(l -> l.seller().equals(seller))
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Escrow and claim queues
    // -------------------------------------------------------------------------

    /**
     * Returns the coins waiting for the given player (sale proceeds) without removing them.
     *
     * @param player the player's UUID
     * @return the pending coin balance (0 if none)
     */
    public double getPendingCoins(UUID player) {
        Objects.requireNonNull(player, "player");
        return pendingCoins.getOrDefault(player, 0.0);
    }

    /**
     * Collects and clears the coins waiting for the given player.
     *
     * @param player the player's UUID
     * @return the collected amount (0 if none were pending)
     */
    public double claimCoins(UUID player) {
        Objects.requireNonNull(player, "player");
        Double amount = pendingCoins.remove(player);
        return amount == null ? 0.0 : amount;
    }

    /**
     * Returns an unmodifiable view of the items waiting for the given player.
     *
     * @param player the player's UUID
     */
    public List<ItemStack> getPendingItems(UUID player) {
        Objects.requireNonNull(player, "player");
        return Collections.unmodifiableList(
                pendingItems.getOrDefault(player, Collections.emptyList()));
    }

    /**
     * Collects and clears the items waiting for the given player.
     *
     * @param player the player's UUID
     * @return the collected items; empty list if none were pending
     */
    public List<ItemStack> claimItems(UUID player) {
        Objects.requireNonNull(player, "player");
        List<ItemStack> claimed = pendingItems.remove(player);
        return claimed == null ? new ArrayList<>() : claimed;
    }

    // -------------------------------------------------------------------------
    // Persistence (one YAML file per seller under {dataFolder}/auctions/<uuid>.yml)
    // -------------------------------------------------------------------------

    /**
     * Loads every seller's active listings from {@code dataFolder/auctions/<uuid>.yml}.
     * Each file holds one section per listing id, storing the escrowed item,
     * display name, category and price.
     *
     * @param dataFolder the plugin's data folder
     */
    public void load(File dataFolder) {
        Objects.requireNonNull(dataFolder, "dataFolder");
        File dir = new File(dataFolder, "auctions");
        File[] files = dir.listFiles((d, name) -> name.endsWith(".yml"));
        if (files == null) {
            return;
        }
        for (File file : files) {
            String base = file.getName().substring(0, file.getName().length() - 4);
            UUID seller;
            try {
                seller = UUID.fromString(base);
            } catch (IllegalArgumentException e) {
                continue;
            }
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            for (String key : cfg.getKeys(false)) {
                ConfigurationSection section = cfg.getConfigurationSection(key);
                if (section == null) {
                    continue;
                }
                ItemStack item = section.getItemStack("item");
                if (item == null) {
                    continue;
                }
                UUID id;
                try {
                    id = UUID.fromString(key);
                } catch (IllegalArgumentException e) {
                    continue;
                }
                String itemName = section.getString("itemName", "");
                String category = section.getString("category", "");
                double price = section.getDouble("price", 0.0);
                listings.put(id, new Listing(id, seller, item, itemName, category, price));
            }
        }
    }

    /**
     * Saves every seller's active listings to {@code dataFolder/auctions/<uuid>.yml},
     * one file per seller.
     *
     * @param dataFolder the plugin's data folder
     */
    public void save(File dataFolder) {
        Objects.requireNonNull(dataFolder, "dataFolder");
        File dir = new File(dataFolder, "auctions");
        if (!dir.exists() && !dir.mkdirs()) {
            return;
        }
        Map<UUID, YamlConfiguration> bySeller = new HashMap<>();
        for (Listing listing : listings.values()) {
            YamlConfiguration cfg = bySeller.computeIfAbsent(listing.seller(), s -> new YamlConfiguration());
            String path = listing.id().toString();
            cfg.set(path + ".item", listing.item());
            cfg.set(path + ".itemName", listing.itemName());
            cfg.set(path + ".category", listing.category());
            cfg.set(path + ".price", listing.price());
        }
        for (Map.Entry<UUID, YamlConfiguration> entry : bySeller.entrySet()) {
            try {
                entry.getValue().save(new File(dir, entry.getKey() + ".yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** Removes all active listings and clears all escrow state. */
    public void clear() {
        listings.clear();
        pendingCoins.clear();
        pendingItems.clear();
    }

    private Listing requireListing(UUID listingId) {
        Listing listing = listings.get(listingId);
        if (listing == null) {
            throw new IllegalArgumentException("no active listing with id: " + listingId);
        }
        return listing;
    }
}
