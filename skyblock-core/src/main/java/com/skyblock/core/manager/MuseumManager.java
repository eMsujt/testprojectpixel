package com.skyblock.core.manager;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton tracking each player's Museum donation progress.
 *
 * <p>Manages the items a player has donated to each museum category.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class MuseumManager {

    /** Category tiers for items donated to the Museum, each with a display name. */
    public enum MuseumCategory {
        WEAPONS("Weapons"),
        ARMOR("Armor"),
        RARITIES("Rarities"),
        FISHING("Fishing");

        /** Human-readable display name shown to players. */
        public final String displayName;

        MuseumCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }


    private static final MuseumManager INSTANCE = new MuseumManager();

    /** Per-player donated items by category. */
    private final Map<UUID, Map<MuseumCategory, Set<String>>> donations = new java.util.HashMap<>();

    private MuseumManager() {
    }

    /**
     * Returns the single shared {@code MuseumManager} instance.
     *
     * @return the singleton instance
     */
    public static MuseumManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns an unmodifiable view of the items the player has donated in the given category.
     *
     * @param playerId the player to look up
     * @param category the museum category
     * @return the set of donated item names, empty if none
     */
    public Set<String> getDonations(UUID playerId, MuseumCategory category) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(category, "category");
        Map<MuseumCategory, Set<String>> playerDonations = donations.get(playerId);
        if (playerDonations == null) return Collections.emptySet();
        Set<String> items = playerDonations.get(category);
        return items == null ? Collections.emptySet() : Collections.unmodifiableSet(items);
    }

    /**
     * Donates an item to the given category for the player.
     *
     * @param playerId the player
     * @param category the museum category
     * @param itemName the name of the item to donate
     * @return {@code true} if the item was newly donated, {@code false} if already donated
     */
    public boolean donate(UUID playerId, MuseumCategory category, String itemName) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(itemName, "itemName");
        return donations.computeIfAbsent(playerId, id -> new EnumMap<>(MuseumCategory.class))
                .computeIfAbsent(category, c -> new HashSet<>())
                .add(itemName);
    }

    /**
     * Returns the total number of unique items donated across all categories for the player.
     *
     * @param playerId the player
     * @return the total donation count
     */
    public int getTotalDonations(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<MuseumCategory, Set<String>> playerDonations = donations.get(playerId);
        if (playerDonations == null) return 0;
        int total = 0;
        for (Set<String> items : playerDonations.values()) {
            total += items.size();
        }
        return total;
    }

    /**
     * Removes all Museum data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return donations.remove(playerId) != null;
    }
}
