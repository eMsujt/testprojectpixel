package com.skyblock.economy;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * In-memory registry of NPC shops, each selling items at a coin buy price and
 * optionally accepting items at a sell price.
 *
 * <p>Shops and entries are keyed by string id and held in a
 * {@link LinkedHashMap} so iteration order is stable. All mutating operations
 * are {@code synchronized} for thread safety, matching the rest of this
 * package.</p>
 *
 * <p>Persistence is handled via {@link #load(File)} and {@link #save(File)},
 * which read and write {@code shops.yml} in the given data folder.</p>
 */
public final class ShopManager {

    /**
     * A single purchasable listing inside a shop.
     *
     * @param itemId    the item identifier, e.g. {@code "WHEAT"}
     * @param buyPrice  coins a player pays to receive the item; must be positive
     * @param sellPrice coins a player receives for selling the item; must be
     *                  non-negative and at most {@code buyPrice}
     */
    public record ShopEntry(String itemId, long buyPrice, long sellPrice) {
        public ShopEntry {
            Objects.requireNonNull(itemId, "itemId");
            if (buyPrice <= 0) throw new IllegalArgumentException("buyPrice must be positive");
            if (sellPrice < 0) throw new IllegalArgumentException("sellPrice must be non-negative");
            if (sellPrice > buyPrice) throw new IllegalArgumentException("sellPrice must not exceed buyPrice");
        }
    }

    /**
     * A named shop holding an ordered list of purchasable entries.
     *
     * @param id      the shop's unique identifier
     * @param title   the display title (supports colour codes)
     * @param entries the items this shop sells, in display order
     */
    public record Shop(String id, String title, List<ShopEntry> entries) {
        public Shop {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(title, "title");
            entries = entries != null ? List.copyOf(entries) : List.of();
        }
    }

    /** Outcome of a buy or sell transaction. */
    public enum TransactionResult {
        SUCCESS,
        INSUFFICIENT_FUNDS,
        SHOP_NOT_FOUND,
        ITEM_NOT_FOUND
    }

    private final Map<String, Shop> shops = new LinkedHashMap<>();

    /**
     * Registers a shop, replacing any existing shop with the same id.
     *
     * @param id      the shop's unique identifier, must not be blank
     * @param title   the display title, must not be null
     * @param entries the items to sell; copied defensively
     */
    public synchronized void registerShop(String id, String title, List<ShopEntry> entries) {
        requireNonBlank(id, "id");
        Objects.requireNonNull(title, "title");
        shops.put(id, new Shop(id, title, entries));
    }

    /**
     * Removes a shop from the registry.
     *
     * @param id the shop id to remove
     * @return {@code true} if the shop existed and was removed
     */
    public synchronized boolean unregisterShop(String id) {
        return shops.remove(requireNonBlank(id, "id")) != null;
    }

    /**
     * Returns the shop with the given id, if present.
     *
     * @param id the shop id
     * @return an {@link Optional} containing the shop, or empty if absent
     */
    public synchronized Optional<Shop> getShop(String id) {
        return Optional.ofNullable(shops.get(requireNonBlank(id, "id")));
    }

    /**
     * Returns an unmodifiable snapshot of all registered shops keyed by id.
     *
     * @return a read-only map of all shops
     */
    public synchronized Map<String, Shop> getShops() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(shops));
    }

    /**
     * Returns the entry for the given item in the given shop, if any.
     *
     * @param shopId the shop id
     * @param itemId the item id
     * @return an {@link Optional} containing the entry, or empty if absent
     */
    public synchronized Optional<ShopEntry> getEntry(String shopId, String itemId) {
        Shop shop = shops.get(requireNonBlank(shopId, "shopId"));
        if (shop == null) return Optional.empty();
        String key = requireNonBlank(itemId, "itemId");
        return shop.entries().stream().filter(e -> e.itemId().equals(key)).findFirst();
    }

    /**
     * Deducts the item's buy price from the player's purse via the given
     * {@link CoinManager}.
     *
     * @param playerId the buyer's UUID
     * @param shopId   the shop id
     * @param itemId   the item id
     * @param coins    the coin manager to debit
     * @return the transaction result
     */
    public synchronized TransactionResult buy(UUID playerId, String shopId, String itemId,
                                               CoinManager coins) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(coins, "coins");
        Optional<ShopEntry> entry = getEntry(shopId, itemId);
        if (shops.get(shopId) == null) return TransactionResult.SHOP_NOT_FOUND;
        if (entry.isEmpty()) return TransactionResult.ITEM_NOT_FOUND;
        return coins.withdraw(playerId, entry.get().buyPrice())
                ? TransactionResult.SUCCESS
                : TransactionResult.INSUFFICIENT_FUNDS;
    }

    /**
     * Credits the item's sell price to the player's purse via the given
     * {@link CoinManager}.
     *
     * @param playerId the seller's UUID
     * @param shopId   the shop id
     * @param itemId   the item id
     * @param coins    the coin manager to credit
     * @return the transaction result
     */
    public synchronized TransactionResult sell(UUID playerId, String shopId, String itemId,
                                                CoinManager coins) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(coins, "coins");
        if (shops.get(shopId) == null) return TransactionResult.SHOP_NOT_FOUND;
        Optional<ShopEntry> entry = getEntry(shopId, itemId);
        if (entry.isEmpty()) return TransactionResult.ITEM_NOT_FOUND;
        coins.deposit(playerId, entry.get().sellPrice());
        return TransactionResult.SUCCESS;
    }

    /**
     * Loads shops from {@code shops.yml} in the given data folder.
     *
     * <p>Each key under the top-level {@code shops} section defines one shop:
     * <pre>
     * shops:
     *   general:
     *     title: "§aGeneral Store"
     *     items:
     *       - "WHEAT:10:5"     # itemId:buyPrice:sellPrice
     *       - "CARROT:8"       # buyPrice only; sellPrice defaults to 0
     * </pre>
     *
     * @param dataFolder the folder containing {@code shops.yml}
     */
    public synchronized void load(File dataFolder) {
        File file = new File(dataFolder, "shops.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = cfg.isConfigurationSection("shops")
                ? cfg.getConfigurationSection("shops")
                : cfg;
        shops.clear();
        for (String id : root.getKeys(false)) {
            if (!root.isConfigurationSection(id)) continue;
            ConfigurationSection section = root.getConfigurationSection(id);
            String title = section.getString("title", id);
            List<ShopEntry> entries = new ArrayList<>();
            for (String raw : section.getStringList("items")) {
                ShopEntry entry = parseEntry(raw);
                if (entry != null) entries.add(entry);
            }
            shops.put(id, new Shop(id, title, entries));
        }
    }

    /**
     * Saves the current shop registry to {@code shops.yml} in the given data
     * folder.
     *
     * @param dataFolder the folder to write {@code shops.yml} into
     * @throws RuntimeException if the file cannot be written
     */
    public synchronized void save(File dataFolder) {
        File file = new File(dataFolder, "shops.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Shop shop : shops.values()) {
            String base = "shops." + shop.id();
            cfg.set(base + ".title", shop.title());
            List<String> items = new ArrayList<>();
            for (ShopEntry entry : shop.entries()) {
                items.add(entry.itemId() + ":" + entry.buyPrice() + ":" + entry.sellPrice());
            }
            cfg.set(base + ".items", items);
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save shops.yml", e);
        }
    }

    /** Parses {@code "itemId:buyPrice[:sellPrice]"}, returning {@code null} on error. */
    private static ShopEntry parseEntry(String raw) {
        if (raw == null) return null;
        String[] parts = raw.split(":");
        if (parts.length < 2) return null;
        try {
            String itemId = parts[0].trim();
            long buyPrice = Long.parseLong(parts[1].trim());
            long sellPrice = parts.length >= 3 ? Long.parseLong(parts[2].trim()) : 0L;
            return new ShopEntry(itemId, buyPrice, sellPrice);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
