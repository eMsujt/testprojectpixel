package com.skyblock.fishing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Manages fishing loot tables per {@link FishingZone} and tracks each
 * player's fishing progression: total catches and earned fishing experience.
 *
 * <p>Loot tables are stored in an {@link EnumMap} keyed by zone; each zone
 * holds an ordered list of {@link FishingDrop} entries. Players start at zero
 * catches and zero experience. Not thread-safe; synchronize externally if
 * accessed from multiple threads.</p>
 */
public final class FishingManager {

    /** Maps fish ID to the fishing XP awarded when that fish is caught. */
    public static final Map<String, Integer> CATCH_TABLE;

    static {
        Map<String, Integer> map = new HashMap<>();
        map.put("PUFFERFISH",     15);
        map.put("INK_SAC",         5);
        map.put("LILY_PAD",        5);
        map.put("BONE",            5);
        map.put("STRING",          5);
        map.put("NAUTILUS_SHELL", 25);
        map.put("SEA_LANTERN",    20);
        map.put("PRISMARINE_SHARD", 20);
        map.put("PRISMARINE_CRYSTALS", 20);
        CATCH_TABLE = Map.copyOf(map);
    }

    private final EnumMap<FishingZone, List<FishingDrop>> lootTables = new EnumMap<>(FishingZone.class);
    private final Map<UUID, Integer> catches = new HashMap<>();
    private final Map<UUID, Double> experience = new HashMap<>();

    /**
     * Records a catch for the player, incrementing their catch count and
     * awarding the given fishing experience.
     *
     * @param playerId the player's UUID
     * @param xp       the fishing experience the catch is worth, must not be negative
     * @return the player's total catch count after this catch
     * @throws IllegalArgumentException if {@code xp} is negative
     */
    public int recordCatch(UUID playerId, double xp) {
        if (xp < 0) {
            throw new IllegalArgumentException("xp must not be negative: " + xp);
        }
        experience.merge(playerId, xp, Double::sum);
        return catches.merge(playerId, 1, Integer::sum);
    }

    /**
     * Returns how many catches the player has recorded.
     *
     * @param playerId the player's UUID
     * @return the total catch count, zero if the player has never fished
     */
    public int getCatches(UUID playerId) {
        return catches.getOrDefault(playerId, 0);
    }

    /**
     * Returns the player's accumulated fishing experience.
     *
     * @param playerId the player's UUID
     * @return the total fishing experience, zero if the player has never fished
     */
    public double getExperience(UUID playerId) {
        return experience.getOrDefault(playerId, 0.0);
    }

    /**
     * Resets the player's fishing progression back to zero.
     *
     * @param playerId the player's UUID
     */
    public void reset(UUID playerId) {
        catches.remove(playerId);
        experience.remove(playerId);
    }

    /**
     * Adds a drop entry to the loot table for the given zone.
     *
     * @param zone the fishing zone, must not be null
     * @param drop the drop to register, must not be null
     * @throws IllegalArgumentException if either argument is null
     */
    public void registerDrop(FishingZone zone, FishingDrop drop) {
        if (zone == null || drop == null) {
            throw new IllegalArgumentException("zone and drop must not be null");
        }
        lootTables.computeIfAbsent(zone, z -> new ArrayList<>()).add(drop);
    }

    /**
     * Returns an unmodifiable view of the drops registered for the given zone.
     *
     * @param zone the fishing zone, must not be null
     * @return the zone's drop list, or an empty list if none have been
     *         registered
     * @throws IllegalArgumentException if zone is null
     */
    public List<FishingDrop> getDrops(FishingZone zone) {
        if (zone == null) {
            throw new IllegalArgumentException("zone must not be null");
        }
        List<FishingDrop> drops = lootTables.get(zone);
        return drops != null ? Collections.unmodifiableList(drops) : Collections.emptyList();
    }

    /**
     * Rolls the loot table for the given zone. Each drop is independently
     * tested against its {@link FishingDrop#getDropChance()}; the first match
     * in registration order is returned.
     *
     * @param zone the fishing zone to roll, must not be null
     * @param rng  the random source, must not be null
     * @return the first drop whose chance test passed, or {@code null} if none
     *         triggered
     * @throws IllegalArgumentException if zone or rng is null
     */
    public FishingDrop rollDrop(FishingZone zone, Random rng) {
        if (zone == null) {
            throw new IllegalArgumentException("zone must not be null");
        }
        if (rng == null) {
            throw new IllegalArgumentException("rng must not be null");
        }
        for (FishingDrop drop : lootTables.getOrDefault(zone, Collections.emptyList())) {
            if (rng.nextDouble() < drop.getDropChance()) {
                return drop;
            }
        }
        return null;
    }

    /**
     * Removes all drops registered for the given zone.
     *
     * @param zone the fishing zone to clear, must not be null
     * @throws IllegalArgumentException if zone is null
     */
    public void clearDrops(FishingZone zone) {
        if (zone == null) {
            throw new IllegalArgumentException("zone must not be null");
        }
        lootTables.remove(zone);
    }
}
