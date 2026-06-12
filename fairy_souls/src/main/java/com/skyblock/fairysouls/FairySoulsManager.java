package com.skyblock.fairysouls;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Manages fairy soul world locations and per-player discovery progress.
 *
 * <p>Each fairy soul is placed at a fixed block location in a world. Souls
 * must be placed before they can be found; each player can find a given soul
 * at most once. Found souls can be exchanged for rewards in batches of
 * {@link #SOULS_PER_EXCHANGE}. Not thread-safe; synchronize externally if
 * accessed from multiple threads.</p>
 */
public final class FairySoulsManager {

    /** Number of found souls consumed by a single exchange. */
    public static final int SOULS_PER_EXCHANGE = 5;

    /**
     * A fairy soul placed at a fixed block location.
     *
     * @param world the name of the world containing the soul
     * @param x     the block x coordinate
     * @param y     the block y coordinate
     * @param z     the block z coordinate
     */
    public record SoulLocation(String world, int x, int y, int z) {

        public SoulLocation {
            if (world == null || world.isBlank()) {
                throw new IllegalArgumentException("world must not be null or blank");
            }
        }
    }

    private final Map<String, SoulLocation> souls = new LinkedHashMap<>();
    private final Map<UUID, Set<String>> foundSouls = new HashMap<>();
    private final Map<UUID, Integer> exchangedSouls = new HashMap<>();

    /**
     * Places a fairy soul at the given location so players can find it.
     *
     * @param soulId   the unique soul id, must not be null or blank
     * @param location the location of the soul, must not be null
     * @throws IllegalArgumentException if the id is null or blank, the
     *                                  location is null, or a soul with that
     *                                  id is already placed
     */
    public void placeSoul(String soulId, SoulLocation location) {
        if (soulId == null || soulId.isBlank()) {
            throw new IllegalArgumentException("soulId must not be null or blank");
        }
        Objects.requireNonNull(location, "location must not be null");
        if (souls.putIfAbsent(soulId, location) != null) {
            throw new IllegalArgumentException("soul already placed: " + soulId);
        }
    }

    /**
     * Removes a fairy soul and forgets all players' discoveries of it.
     *
     * @param soulId the soul id
     * @return {@code true} if the soul was placed and has been removed
     */
    public boolean removeSoul(String soulId) {
        if (souls.remove(soulId) == null) {
            return false;
        }
        for (Set<String> found : foundSouls.values()) {
            found.remove(soulId);
        }
        return true;
    }

    /**
     * Returns the location of a placed fairy soul.
     *
     * @param soulId the soul id
     * @return the soul's location, or {@code null} if no such soul is placed
     */
    public SoulLocation getSoulLocation(String soulId) {
        return souls.get(soulId);
    }

    /**
     * Marks a fairy soul as found by a player.
     *
     * @param playerId the unique id of the player, must not be null
     * @param soulId   the soul id
     * @return {@code true} if the soul was newly found, {@code false} if the
     *         player had already found it
     * @throws IllegalArgumentException if the player is null or the soul is
     *                                  not placed
     */
    public boolean findSoul(UUID playerId, String soulId) {
        if (playerId == null) {
            throw new IllegalArgumentException("playerId must not be null");
        }
        if (!souls.containsKey(soulId)) {
            throw new IllegalArgumentException("soul is not placed: " + soulId);
        }
        return foundSouls.computeIfAbsent(playerId, key -> new LinkedHashSet<>()).add(soulId);
    }

    /**
     * Returns whether a player has found a fairy soul.
     *
     * @param playerId the unique id of the player
     * @param soulId   the soul id
     * @return {@code true} if the player has found the soul
     */
    public boolean hasFound(UUID playerId, String soulId) {
        Set<String> found = foundSouls.get(playerId);
        return found != null && found.contains(soulId);
    }

    /**
     * Returns the number of fairy souls a player has found.
     *
     * @param playerId the unique id of the player
     * @return the player's found soul count
     */
    public int getFoundCount(UUID playerId) {
        Set<String> found = foundSouls.get(playerId);
        return found == null ? 0 : found.size();
    }

    /**
     * Returns the number of exchanges a player can currently perform.
     *
     * <p>Each exchange consumes {@link #SOULS_PER_EXCHANGE} found souls that
     * have not been exchanged yet.</p>
     *
     * @param playerId the unique id of the player
     * @return the number of pending exchanges
     */
    public int getPendingExchanges(UUID playerId) {
        int unexchanged = getFoundCount(playerId) - exchangedSouls.getOrDefault(playerId, 0);
        return unexchanged / SOULS_PER_EXCHANGE;
    }

    /**
     * Performs a single exchange for a player, consuming
     * {@link #SOULS_PER_EXCHANGE} found souls.
     *
     * @param playerId the unique id of the player
     * @return {@code true} if the exchange was performed, {@code false} if
     *         the player does not have enough unexchanged souls
     */
    public boolean exchangeSouls(UUID playerId) {
        if (getPendingExchanges(playerId) <= 0) {
            return false;
        }
        exchangedSouls.merge(playerId, SOULS_PER_EXCHANGE, Integer::sum);
        return true;
    }

    /**
     * Returns the number of souls a player has already exchanged.
     *
     * @param playerId the unique id of the player
     * @return the player's exchanged soul count
     */
    public int getExchangedCount(UUID playerId) {
        return exchangedSouls.getOrDefault(playerId, 0);
    }

    /**
     * Returns the total number of placed fairy souls.
     *
     * @return the placed soul count
     */
    public int getTotalSouls() {
        return souls.size();
    }

    /**
     * Returns all placed fairy souls by id in placement order.
     *
     * @return an unmodifiable view of the placed souls
     */
    public Map<String, SoulLocation> getSouls() {
        return Collections.unmodifiableMap(souls);
    }
}
