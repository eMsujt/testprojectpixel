package com.skyblock.core.manager;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's Sack item storage.
 *
 * <p>Sacks automatically pick up matching resources off the ground and store
 * their per-item counts by sack type. Each stored item has a {@link CapacityTier}
 * that caps how many of that item a sack can hold; deposits beyond the cap
 * overflow and are reported back to the caller. The "Sack of Sacks" aggregates
 * an item's count across every sack type for a player.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class SacksManager {

    /** Sack categories available to players, each with a display name. */
    public enum SackType {
        MINING("Mining Sack"),
        FARMING("Farming Sack"),
        COMBAT("Combat Sack"),
        ENCHANTING("Enchanting Sack"),
        FISHING("Fishing Sack"),
        FORAGING("Foraging Sack");

        private final String displayName;

        SackType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** Per-item storage capacity tiers; a sack holds up to {@link #getCapacity()} of an item. */
    public enum CapacityTier {
        SMALL(4_000),
        MEDIUM(25_000),
        LARGE(100_000),
        JUMBO(500_000);

        private final int capacity;

        CapacityTier(int capacity) {
            this.capacity = capacity;
        }

        public int getCapacity() {
            return capacity;
        }
    }

    /** Default capacity tier applied to items with no registered tier. */
    public static final CapacityTier DEFAULT_TIER = CapacityTier.SMALL;

    private static final SacksManager INSTANCE = new SacksManager();

    private final Map<UUID, Map<SackType, Map<String, Integer>>> sackContents = new HashMap<>();
    private final Map<String, CapacityTier> itemTiers = new HashMap<>();

    private SacksManager() {}

    /**
     * Returns the single shared {@code SacksManager} instance.
     *
     * @return the singleton instance
     */
    public static SacksManager getInstance() {
        return INSTANCE;
    }

    /**
     * Registers the capacity tier for an item, controlling how many of it a sack
     * can hold.
     *
     * @param itemId the item identifier
     * @param tier   the capacity tier to apply
     */
    public void setItemTier(String itemId, CapacityTier tier) {
        Objects.requireNonNull(itemId, "itemId");
        Objects.requireNonNull(tier, "tier");
        itemTiers.put(itemId, tier);
    }

    /**
     * Returns the capacity tier registered for an item, or {@link #DEFAULT_TIER}
     * if none has been registered.
     *
     * @param itemId the item identifier
     * @return the item's capacity tier
     */
    public CapacityTier getItemTier(String itemId) {
        Objects.requireNonNull(itemId, "itemId");
        return itemTiers.getOrDefault(itemId, DEFAULT_TIER);
    }

    /**
     * Auto-pickup deposit of items into a sack, respecting the item's capacity
     * tier. Stores as many as fit and returns the overflow that did not fit.
     *
     * @param playerId the player
     * @param sackType the sack type
     * @param itemId   the item identifier
     * @param amount   the number of items picked up (must not be negative)
     * @return the overflow amount that exceeded the sack's capacity for the item
     */
    public int addItem(UUID playerId, SackType sackType, String itemId, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(sackType, "sackType");
        Objects.requireNonNull(itemId, "itemId");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<String, Integer> contents = getOrCreateContents(playerId, sackType);
        int capacity = getItemTier(itemId).getCapacity();
        int current = contents.getOrDefault(itemId, 0);
        int stored = Math.min(capacity, current + amount);
        if (stored == 0) {
            contents.remove(itemId);
        } else {
            contents.put(itemId, stored);
        }
        return (current + amount) - stored;
    }

    /**
     * Removes items from a sack, never going below zero.
     *
     * @param playerId the player
     * @param sackType the sack type
     * @param itemId   the item identifier
     * @param amount   the number of items to remove (must not be negative)
     * @return the remaining count for that item in the sack
     */
    public int removeItem(UUID playerId, SackType sackType, String itemId, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(sackType, "sackType");
        Objects.requireNonNull(itemId, "itemId");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<String, Integer> contents = getOrCreateContents(playerId, sackType);
        int current = contents.getOrDefault(itemId, 0);
        int remaining = Math.max(0, current - amount);
        if (remaining == 0) {
            contents.remove(itemId);
        } else {
            contents.put(itemId, remaining);
        }
        return remaining;
    }

    /**
     * Returns the count of an item in a specific sack type.
     *
     * @param playerId the player
     * @param sackType the sack type
     * @param itemId   the item identifier
     * @return the item count, or 0 if none
     */
    public int getItemCount(UUID playerId, SackType sackType, String itemId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(sackType, "sackType");
        Objects.requireNonNull(itemId, "itemId");
        Map<SackType, Map<String, Integer>> playerSacks = sackContents.get(playerId);
        if (playerSacks == null) return 0;
        Map<String, Integer> contents = playerSacks.get(sackType);
        return contents == null ? 0 : contents.getOrDefault(itemId, 0);
    }

    /**
     * Sack of Sacks aggregation: the total count of an item across all of the
     * player's sack types.
     *
     * @param playerId the player
     * @param itemId   the item identifier
     * @return the aggregated item count across every sack
     */
    public int getTotalItemCount(UUID playerId, String itemId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(itemId, "itemId");
        Map<SackType, Map<String, Integer>> playerSacks = sackContents.get(playerId);
        if (playerSacks == null) return 0;
        int total = 0;
        for (Map<String, Integer> contents : playerSacks.values()) {
            total += contents.getOrDefault(itemId, 0);
        }
        return total;
    }

    /**
     * Returns an unmodifiable view of the contents of a sack type for a player.
     *
     * @param playerId the player
     * @param sackType the sack type
     * @return the item-to-count map, or an empty map if none
     */
    public Map<String, Integer> getSackContents(UUID playerId, SackType sackType) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(sackType, "sackType");
        Map<SackType, Map<String, Integer>> playerSacks = sackContents.get(playerId);
        if (playerSacks == null) return Collections.emptyMap();
        Map<String, Integer> contents = playerSacks.get(sackType);
        return contents == null ? Collections.emptyMap() : Collections.unmodifiableMap(contents);
    }

    /**
     * Removes all sack data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return sackContents.remove(playerId) != null;
    }

    private Map<String, Integer> getOrCreateContents(UUID playerId, SackType sackType) {
        return sackContents
                .computeIfAbsent(playerId, id -> new EnumMap<>(SackType.class))
                .computeIfAbsent(sackType, t -> new HashMap<>());
    }
}
