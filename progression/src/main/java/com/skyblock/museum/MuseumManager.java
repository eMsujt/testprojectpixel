package com.skyblock.museum;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages per-player museum donations.
 *
 * <p>Each player owns a set of donated item ids. Donations are stored in a
 * {@link ConcurrentHashMap} keyed by player UUID, with concurrent sets as
 * values, so donations are safe to record from any thread. An item can only
 * be donated once per player.</p>
 */
public final class MuseumManager {

    private final ConcurrentHashMap<UUID, Set<String>> donations = new ConcurrentHashMap<>();

    /**
     * Records a donation of the given item to the player's museum.
     *
     * @param playerId the player's UUID
     * @param itemId   the id of the item being donated
     * @return {@code true} if the item was newly donated, {@code false} if
     *         the player had already donated it
     */
    public boolean donate(UUID playerId, String itemId) {
        requireItemId(itemId);
        return donations
                .computeIfAbsent(playerId, id -> ConcurrentHashMap.newKeySet())
                .add(itemId);
    }

    /**
     * Returns whether the player has donated the given item.
     *
     * @param playerId the player's UUID
     * @param itemId   the id of the item to check
     * @return {@code true} if the item is in the player's museum
     */
    public boolean hasDonated(UUID playerId, String itemId) {
        requireItemId(itemId);
        Set<String> donated = donations.get(playerId);
        return donated != null && donated.contains(itemId);
    }

    /**
     * Returns the number of items the player has donated.
     *
     * @param playerId the player's UUID
     * @return the donation count, or {@code 0} if the player has no museum yet
     */
    public int getDonationCount(UUID playerId) {
        Set<String> donated = donations.get(playerId);
        return donated != null ? donated.size() : 0;
    }

    /**
     * Returns an unmodifiable view of the item ids the player has donated.
     *
     * @param playerId the player's UUID
     * @return the player's donated item ids, empty if the player has no museum yet
     */
    public Set<String> getDonations(UUID playerId) {
        Set<String> donated = donations.get(playerId);
        return donated != null ? Collections.unmodifiableSet(donated) : Set.of();
    }

    /**
     * Removes a single item from the player's museum (e.g. when reclaimed).
     *
     * @param playerId the player's UUID
     * @param itemId   the id of the item to remove
     * @return {@code true} if the item was donated and has been removed
     */
    public boolean withdraw(UUID playerId, String itemId) {
        requireItemId(itemId);
        Set<String> donated = donations.get(playerId);
        return donated != null && donated.remove(itemId);
    }

    /**
     * Removes the player's museum entirely (e.g. on data wipe).
     *
     * @param playerId the player's UUID
     * @return the number of donations the museum held, or {@code 0} if there was none
     */
    public int clear(UUID playerId) {
        Set<String> removed = donations.remove(playerId);
        return removed != null ? removed.size() : 0;
    }

    private static void requireItemId(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("itemId must be non-blank");
        }
    }
}
