package com.skyblock.fairysouls;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Manages fairy souls and tracks which players have collected them.
 *
 * <p>Fairy souls are identified by a unique string id (for example a region
 * name plus an index). Souls must be registered before they can be collected;
 * each player can collect a given soul at most once. Not thread-safe;
 * synchronize externally if accessed from multiple threads.</p>
 */
public final class FairySoulManager {

    private final Set<String> registeredSouls = new LinkedHashSet<>();
    private final Map<UUID, Set<String>> collectedSouls = new HashMap<>();

    /**
     * Registers a fairy soul so it can be collected by players.
     *
     * @param soulId the unique soul id, must not be null or blank
     * @throws IllegalArgumentException if the id is null or blank, or a soul
     *                                  with that id is already registered
     */
    public void registerSoul(String soulId) {
        if (soulId == null || soulId.isBlank()) {
            throw new IllegalArgumentException("soulId must not be null or blank");
        }
        if (!registeredSouls.add(soulId)) {
            throw new IllegalArgumentException("soul already registered: " + soulId);
        }
    }

    /**
     * Unregisters a fairy soul and removes it from all players' collections.
     *
     * @param soulId the soul id
     * @return {@code true} if the soul was registered and has been removed
     */
    public boolean unregisterSoul(String soulId) {
        if (!registeredSouls.remove(soulId)) {
            return false;
        }
        for (Set<String> souls : collectedSouls.values()) {
            souls.remove(soulId);
        }
        return true;
    }

    /**
     * Returns whether a fairy soul with the given id is registered.
     *
     * @param soulId the soul id
     * @return {@code true} if the soul is registered
     */
    public boolean isRegistered(String soulId) {
        return registeredSouls.contains(soulId);
    }

    /**
     * Marks a fairy soul as collected by a player.
     *
     * @param playerId the unique id of the player, must not be null
     * @param soulId   the soul id
     * @return {@code true} if the soul was newly collected, {@code false} if
     *         the player had already collected it
     * @throws IllegalArgumentException if the player is null or the soul is
     *                                  not registered
     */
    public boolean collectSoul(UUID playerId, String soulId) {
        if (playerId == null) {
            throw new IllegalArgumentException("playerId must not be null");
        }
        if (!registeredSouls.contains(soulId)) {
            throw new IllegalArgumentException("soul is not registered: " + soulId);
        }
        return collectedSouls.computeIfAbsent(playerId, key -> new LinkedHashSet<>()).add(soulId);
    }

    /**
     * Returns whether a player has collected a fairy soul.
     *
     * @param playerId the unique id of the player
     * @param soulId   the soul id
     * @return {@code true} if the player has collected the soul
     */
    public boolean hasCollected(UUID playerId, String soulId) {
        Set<String> souls = collectedSouls.get(playerId);
        return souls != null && souls.contains(soulId);
    }

    /**
     * Returns the fairy souls a player has collected, in collection order.
     *
     * @param playerId the unique id of the player
     * @return an unmodifiable view of the collected soul ids, empty if none
     */
    public Set<String> getCollectedSouls(UUID playerId) {
        Set<String> souls = collectedSouls.get(playerId);
        return souls == null ? Collections.emptySet() : Collections.unmodifiableSet(souls);
    }

    /**
     * Returns the number of fairy souls a player has collected.
     *
     * @param playerId the unique id of the player
     * @return the player's collected soul count
     */
    public int getCollectedCount(UUID playerId) {
        Set<String> souls = collectedSouls.get(playerId);
        return souls == null ? 0 : souls.size();
    }

    /**
     * Returns the total number of registered fairy souls.
     *
     * @return the registered soul count
     */
    public int getTotalSouls() {
        return registeredSouls.size();
    }

    /**
     * Returns the ids of all registered fairy souls in registration order.
     *
     * @return an unmodifiable view of the registered soul ids
     */
    public Set<String> getRegisteredSouls() {
        return Collections.unmodifiableSet(registeredSouls);
    }
}
