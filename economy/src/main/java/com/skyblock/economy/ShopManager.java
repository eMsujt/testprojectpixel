package com.skyblock.economy;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @deprecated Use {@link com.skyblock.core.manager.ShopManager} (the canonical singleton).
 */
@Deprecated
public final class ShopManager {

    private static final ShopManager INSTANCE = new ShopManager();
    private static final com.skyblock.core.manager.ShopManager DELEGATE =
            com.skyblock.core.manager.ShopManager.getInstance();

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
        SUCCESS, INSUFFICIENT_FUNDS, SHOP_NOT_FOUND, ITEM_NOT_FOUND
    }

    private ShopManager() {}

    public static ShopManager getInstance() {
        return INSTANCE;
    }

    public synchronized void registerShop(String id, String title, List<ShopEntry> entries) {
        List<com.skyblock.core.manager.ShopManager.ShopEntry> canonical = entries == null ? List.of()
                : entries.stream().map(e -> new com.skyblock.core.manager.ShopManager.ShopEntry(
                        e.itemId(), e.buyPrice(), e.sellPrice())).collect(Collectors.toList());
        DELEGATE.registerShop(id, title, canonical);
    }

    public synchronized boolean unregisterShop(String id) {
        return DELEGATE.unregisterShop(id);
    }

    public synchronized Optional<Shop> getShop(String id) {
        return DELEGATE.getShop(id).map(ShopManager::fromCanonical);
    }

    public synchronized Map<String, Shop> getShops() {
        return Collections.unmodifiableMap(DELEGATE.getShops().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> fromCanonical(e.getValue()),
                        (a, b) -> a, LinkedHashMap::new)));
    }

    public synchronized Optional<ShopEntry> getEntry(String shopId, String itemId) {
        return DELEGATE.getEntry(shopId, itemId)
                .map(e -> new ShopEntry(e.itemId(), e.buyPrice(), e.sellPrice()));
    }

    /**
     * Deducts the item's buy price from the player's purse via the given
     * {@link CoinManager}.
     */
    public synchronized TransactionResult buy(UUID playerId, String shopId, String itemId,
                                               CoinManager coins) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(coins, "coins");
        if (DELEGATE.getShop(shopId).isEmpty()) return TransactionResult.SHOP_NOT_FOUND;
        Optional<com.skyblock.core.manager.ShopManager.ShopEntry> entry = DELEGATE.getEntry(shopId, itemId);
        if (entry.isEmpty()) return TransactionResult.ITEM_NOT_FOUND;
        return coins.withdraw(playerId, entry.get().buyPrice())
                ? TransactionResult.SUCCESS : TransactionResult.INSUFFICIENT_FUNDS;
    }

    /**
     * Credits the item's sell price to the player's purse via the given
     * {@link CoinManager}.
     */
    public synchronized TransactionResult sell(UUID playerId, String shopId, String itemId,
                                                CoinManager coins) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(coins, "coins");
        if (DELEGATE.getShop(shopId).isEmpty()) return TransactionResult.SHOP_NOT_FOUND;
        Optional<com.skyblock.core.manager.ShopManager.ShopEntry> entry = DELEGATE.getEntry(shopId, itemId);
        if (entry.isEmpty()) return TransactionResult.ITEM_NOT_FOUND;
        coins.deposit(playerId, entry.get().sellPrice());
        return TransactionResult.SUCCESS;
    }

    public synchronized void load(File dataFolder) {
        DELEGATE.load(dataFolder);
    }

    public synchronized void save(File dataFolder) {
        DELEGATE.save(dataFolder);
    }

    private static Shop fromCanonical(com.skyblock.core.manager.ShopManager.Shop s) {
        return new Shop(s.id(), s.title(), s.entries().stream()
                .map(e -> new ShopEntry(e.itemId(), e.buyPrice(), e.sellPrice()))
                .collect(Collectors.toList()));
    }
}
