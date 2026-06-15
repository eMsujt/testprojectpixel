package com.skyblock.core.accessory;

import com.skyblock.core.model.AccessoryRarity;
import com.skyblock.core.model.Stat;
import com.skyblock.core.talisman.TalismanManager;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton tracking each player's accessory bag contents and the stat bonuses
 * they provide.
 *
 * <p>The accessory bag holds up to {@link #MAX_SLOTS} accessories. Accessories
 * stored in the bag passively apply their stat bonuses.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class AccessoryBagManager {

    /**
     * Upgrade tier for accessories, determining the magic power contributed to the
     * Accessory Bag's total magic power pool.
     */
    public enum AccessoryTier {
        COMMON("Common", 3),
        UNCOMMON("Uncommon", 5),
        RARE("Rare", 8),
        EPIC("Epic", 12),
        LEGENDARY("Legendary", 16),
        MYTHIC("Mythic", 22),
        SPECIAL("Special", 3);

        private final String displayName;
        /** Magic power contributed when this tier of accessory is equipped in the bag. */
        public final int magicPower;

        AccessoryTier(String displayName, int magicPower) {
            this.displayName = displayName;
            this.magicPower = magicPower;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** Maximum number of accessories a player can hold in the bag. */
    public static final int MAX_SLOTS = 45;

    private static final AccessoryBagManager INSTANCE = new AccessoryBagManager();

    /** Per-player set of accessories stored in the bag. */
    private final Map<UUID, Set<TalismanManager.TalismanType>> bags = new HashMap<>();

    private AccessoryBagManager() {
    }

    /**
     * Returns the single shared {@code AccessoryBagManager} instance.
     *
     * @return the singleton instance
     */
    public static AccessoryBagManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds an accessory to the player's bag.
     *
     * @param playerId the player's UUID, must not be null
     * @param type     the accessory to add, must not be null
     * @return {@code true} if added, {@code false} if already present or bag is full
     */
    public boolean addAccessory(UUID playerId, TalismanManager.TalismanType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Set<TalismanManager.TalismanType> bag =
                bags.computeIfAbsent(playerId, id -> EnumSet.noneOf(TalismanManager.TalismanType.class));
        if (bag.contains(type)) {
            return false;
        }
        if (bag.size() >= MAX_SLOTS) {
            return false;
        }
        return bag.add(type);
    }

    /**
     * Removes an accessory from the player's bag.
     *
     * @param playerId the player's UUID, must not be null
     * @param type     the accessory to remove, must not be null
     * @return {@code true} if removed, {@code false} if it was not in the bag
     */
    public boolean removeAccessory(UUID playerId, TalismanManager.TalismanType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Set<TalismanManager.TalismanType> bag = bags.get(playerId);
        return bag != null && bag.remove(type);
    }

    /**
     * Returns whether the player has the given accessory in their bag.
     *
     * @param playerId the player's UUID, must not be null
     * @param type     the accessory to check, must not be null
     * @return {@code true} if present
     */
    public boolean hasAccessory(UUID playerId, TalismanManager.TalismanType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Set<TalismanManager.TalismanType> bag = bags.get(playerId);
        return bag != null && bag.contains(type);
    }

    /**
     * Returns an unmodifiable view of the accessories in the player's bag.
     *
     * @param playerId the player's UUID, must not be null
     * @return an unmodifiable set of accessories, empty if none
     */
    public Set<TalismanManager.TalismanType> getContents(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<TalismanManager.TalismanType> bag = bags.get(playerId);
        return bag == null ? Collections.emptySet() : Collections.unmodifiableSet(bag);
    }

    /**
     * Returns the number of accessories in the player's bag.
     *
     * @param playerId the player's UUID, must not be null
     * @return current count (0 if empty)
     */
    public int getSize(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<TalismanManager.TalismanType> bag = bags.get(playerId);
        return bag == null ? 0 : bag.size();
    }

    /**
     * Computes the total stat bonuses from all accessories in the player's bag.
     *
     * @param playerId the player's UUID, must not be null
     * @return map of {@link Stat} to total bonus; empty if bag is empty
     */
    public Map<Stat, Double> getTotalBonuses(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<TalismanManager.TalismanType> bag = bags.get(playerId);
        if (bag == null || bag.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Stat, Double> totals = new EnumMap<>(Stat.class);
        for (TalismanManager.TalismanType t : bag) {
            totals.merge(t.stat, t.bonus, Double::sum);
        }
        return totals;
    }

    /**
     * Returns an unmodifiable set of accessories in the player's bag matching the given rarity.
     *
     * @param playerId the player's UUID, must not be null
     * @param rarity   the rarity to filter by, must not be null
     * @return an unmodifiable set of matching accessories, empty if none
     */
    public Set<TalismanManager.TalismanType> getContentsByRarity(UUID playerId, AccessoryRarity rarity) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(rarity, "rarity");
        Set<TalismanManager.TalismanType> bag = bags.get(playerId);
        if (bag == null || bag.isEmpty()) {
            return Collections.emptySet();
        }
        Set<TalismanManager.TalismanType> result = EnumSet.noneOf(TalismanManager.TalismanType.class);
        for (TalismanManager.TalismanType t : bag) {
            if (t.rarity == rarity) {
                result.add(t);
            }
        }
        return Collections.unmodifiableSet(result);
    }

    /**
     * Returns the total magic power contributed by accessories in the player's bag
     * that match the given tier.
     *
     * @param playerId the player's UUID, must not be null
     * @param tier     the tier to query, must not be null
     * @return total magic power for that tier (number of matching accessories × tier magic power)
     */
    public int getMagicPower(UUID playerId, AccessoryTier tier) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        Set<TalismanManager.TalismanType> bag = bags.get(playerId);
        if (bag == null || bag.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (TalismanManager.TalismanType t : bag) {
            if (t.rarity.name().equals(tier.name())) {
                count++;
            }
        }
        return count * tier.magicPower;
    }

    /**
     * Clears all accessories from the player's bag.
     *
     * @param playerId the player's UUID, must not be null
     * @return {@code true} if the player had any accessories, {@code false} otherwise
     */
    public boolean clear(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return bags.remove(playerId) != null;
    }
}
