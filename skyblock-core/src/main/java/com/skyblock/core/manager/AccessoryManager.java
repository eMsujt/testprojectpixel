package com.skyblock.core.manager;

import com.skyblock.core.model.AccessoryRarity;
import com.skyblock.core.model.Stat;
import com.skyblock.core.talisman.manager.TalismanManager;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager tracking the rarity of each accessory a player owns.
 *
 * <p>Rarity determines the stat multiplier applied to an accessory's bonuses
 * when it is active in the accessory bag.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class AccessoryManager {

    private static final AccessoryManager INSTANCE = new AccessoryManager();

    /** Magical power granted by a single accessory, keyed by its rarity. */
    private static final Map<AccessoryRarity, Integer> MAGICAL_POWER = new EnumMap<>(AccessoryRarity.class);

    static {
        MAGICAL_POWER.put(AccessoryRarity.COMMON, 3);
        MAGICAL_POWER.put(AccessoryRarity.UNCOMMON, 5);
        MAGICAL_POWER.put(AccessoryRarity.RARE, 8);
        MAGICAL_POWER.put(AccessoryRarity.EPIC, 12);
        MAGICAL_POWER.put(AccessoryRarity.LEGENDARY, 16);
        MAGICAL_POWER.put(AccessoryRarity.MYTHIC, 22);
        MAGICAL_POWER.put(AccessoryRarity.SPECIAL, 3);
        MAGICAL_POWER.put(AccessoryRarity.VERY_SPECIAL, 5);
    }

    /** Magical power consumed by each tuning point a player can allocate. */
    private static final int MAGICAL_POWER_PER_TUNING_POINT = 10;

    /** Per-player map of accessory type to its assigned rarity. */
    private final Map<UUID, Map<TalismanManager.TalismanType, AccessoryRarity>> playerAccessories = new HashMap<>();

    /** Per-player tuning points allocated to each stat. */
    private final Map<UUID, Map<Stat, Integer>> playerTuning = new HashMap<>();

    private AccessoryManager() {}

    /**
     * Returns the single shared {@code AccessoryManager} instance.
     *
     * @return the singleton instance
     */
    public static AccessoryManager getInstance() {
        return INSTANCE;
    }

    /**
     * Assigns a rarity to an accessory for the given player.
     *
     * @param playerId the player's UUID, must not be null
     * @param type     the accessory type, must not be null
     * @param rarity   the rarity to assign, must not be null
     */
    public void setRarity(UUID playerId, TalismanManager.TalismanType type, AccessoryRarity rarity) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(rarity, "rarity");
        playerAccessories.computeIfAbsent(playerId, id -> new HashMap<>()).put(type, rarity);
    }

    /**
     * Returns the rarity of an accessory for the given player, or {@link AccessoryRarity#COMMON}
     * if not explicitly set.
     *
     * @param playerId the player's UUID, must not be null
     * @param type     the accessory type, must not be null
     * @return the assigned rarity
     */
    public AccessoryRarity getRarity(UUID playerId, TalismanManager.TalismanType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<TalismanManager.TalismanType, AccessoryRarity> accessories = playerAccessories.get(playerId);
        if (accessories == null) {
            return AccessoryRarity.COMMON;
        }
        return accessories.getOrDefault(type, AccessoryRarity.COMMON);
    }

    /**
     * Removes the rarity assignment for an accessory from the given player.
     *
     * @param playerId the player's UUID, must not be null
     * @param type     the accessory type, must not be null
     * @return {@code true} if a rarity was removed, {@code false} if none was set
     */
    public boolean removeAccessory(UUID playerId, TalismanManager.TalismanType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<TalismanManager.TalismanType, AccessoryRarity> accessories = playerAccessories.get(playerId);
        return accessories != null && accessories.remove(type) != null;
    }

    /**
     * Returns an unmodifiable view of all accessory rarities assigned to the given player.
     *
     * @param playerId the player's UUID, must not be null
     * @return map of accessory type to rarity; empty if none assigned
     */
    public Map<TalismanManager.TalismanType, AccessoryRarity> getAccessories(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<TalismanManager.TalismanType, AccessoryRarity> accessories = playerAccessories.get(playerId);
        return accessories == null ? Collections.emptyMap() : Collections.unmodifiableMap(accessories);
    }

    /**
     * Clears all accessory rarity assignments for the given player.
     *
     * @param playerId the player's UUID, must not be null
     */
    public void clearAccessories(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        playerAccessories.remove(playerId);
        playerTuning.remove(playerId);
    }

    /**
     * Returns the magical power granted by accessories of the given rarity.
     *
     * @param rarity the rarity, must not be null
     * @return the per-accessory magical power
     */
    public static int magicalPowerFor(AccessoryRarity rarity) {
        Objects.requireNonNull(rarity, "rarity");
        return MAGICAL_POWER.getOrDefault(rarity, 0);
    }

    /**
     * Returns the total magical power from every accessory the player owns, the
     * sum of each accessory's per-rarity contribution.
     *
     * @param playerId the player's UUID, must not be null
     * @return the total magical power, or {@code 0} if the player owns no accessories
     */
    public int getTotalMagicalPower(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<TalismanManager.TalismanType, AccessoryRarity> accessories = playerAccessories.get(playerId);
        if (accessories == null) {
            return 0;
        }
        int total = 0;
        for (AccessoryRarity rarity : accessories.values()) {
            total += magicalPowerFor(rarity);
        }
        return total;
    }

    /**
     * Returns the number of tuning points the player has unlocked, one per
     * {@value #MAGICAL_POWER_PER_TUNING_POINT} magical power.
     *
     * @param playerId the player's UUID, must not be null
     * @return the total available tuning points
     */
    public int getAvailableTuningPoints(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return getTotalMagicalPower(playerId) / MAGICAL_POWER_PER_TUNING_POINT;
    }

    /**
     * Returns the number of tuning points the player has already allocated across all stats.
     *
     * @param playerId the player's UUID, must not be null
     * @return the total allocated tuning points
     */
    public int getAllocatedTuningPoints(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<Stat, Integer> tuning = playerTuning.get(playerId);
        if (tuning == null) {
            return 0;
        }
        int total = 0;
        for (int points : tuning.values()) {
            total += points;
        }
        return total;
    }

    /**
     * Returns the tuning points the player has allocated to a single stat.
     *
     * @param playerId the player's UUID, must not be null
     * @param stat     the stat, must not be null
     * @return the allocated points, or {@code 0} if none
     */
    public int getTuningPoints(UUID playerId, Stat stat) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        Map<Stat, Integer> tuning = playerTuning.get(playerId);
        return tuning == null ? 0 : tuning.getOrDefault(stat, 0);
    }

    /**
     * Allocates tuning points to a stat, replacing any previous allocation for that stat.
     * The allocation is rejected if it would push the player's total allocated points
     * beyond what their magical power has unlocked.
     *
     * @param playerId the player's UUID, must not be null
     * @param stat     the stat to tune, must not be null
     * @param points   the number of points to assign, must not be negative
     * @return {@code true} if the allocation was applied, {@code false} if it exceeded the available points
     */
    public boolean setTuningPoints(UUID playerId, Stat stat, int points) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        if (points < 0) {
            throw new IllegalArgumentException("points must not be negative");
        }
        int otherAllocated = getAllocatedTuningPoints(playerId) - getTuningPoints(playerId, stat);
        if (otherAllocated + points > getAvailableTuningPoints(playerId)) {
            return false;
        }
        if (points == 0) {
            Map<Stat, Integer> tuning = playerTuning.get(playerId);
            if (tuning != null) {
                tuning.remove(stat);
            }
        } else {
            playerTuning.computeIfAbsent(playerId, id -> new EnumMap<>(Stat.class)).put(stat, points);
        }
        refreshTuningBonus(playerId);
        return true;
    }

    /**
     * Returns an unmodifiable view of the player's tuning point allocations.
     *
     * @param playerId the player's UUID, must not be null
     * @return map of stat to allocated points; empty if none assigned
     */
    public Map<Stat, Integer> getTuning(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<Stat, Integer> tuning = playerTuning.get(playerId);
        return tuning == null ? Collections.emptyMap() : Collections.unmodifiableMap(tuning);
    }

    /**
     * Clears all tuning point allocations for the given player.
     *
     * @param playerId the player's UUID, must not be null
     */
    public void resetTuning(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        playerTuning.remove(playerId);
        refreshTuningBonus(playerId);
    }

    /** Bonuses currently applied from tuning, for exact removal on re-allocation. */
    private final Map<UUID, Map<Stat, Double>> appliedTuning = new HashMap<>();

    /** Forgets the tracked applied tuning without touching StatManager (e.g. on quit when it resets). */
    public void clearAppliedTuning(UUID playerId) {
        appliedTuning.remove(playerId);
    }

    /**
     * Re-applies the player's tuning-point bonuses to {@link StatManager} (1 stat per point),
     * removing any previously applied amounts so re-allocating stays balanced.
     */
    private void refreshTuningBonus(UUID playerId) {
        StatManager stats = StatManager.getInstance();
        Map<Stat, Double> previous = appliedTuning.remove(playerId);
        if (previous != null) {
            for (Map.Entry<Stat, Double> entry : previous.entrySet()) {
                stats.addBonus(playerId, entry.getKey(), -entry.getValue());
            }
        }
        Map<Stat, Integer> tuning = getTuning(playerId);
        if (tuning.isEmpty()) {
            return;
        }
        Map<Stat, Double> applied = new EnumMap<>(Stat.class);
        for (Map.Entry<Stat, Integer> entry : tuning.entrySet()) {
            double amount = entry.getValue();
            if (amount == 0.0) {
                continue;
            }
            stats.addBonus(playerId, entry.getKey(), amount);
            applied.put(entry.getKey(), amount);
        }
        if (!applied.isEmpty()) {
            appliedTuning.put(playerId, applied);
        }
    }
}
