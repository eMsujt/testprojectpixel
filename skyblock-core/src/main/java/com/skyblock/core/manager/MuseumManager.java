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
        SPECIAL("Special");

        /** Human-readable display name shown to players. */
        public final String displayName;

        MuseumCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }


    /** Donation-count milestones, ordered by ascending threshold. */
    public enum DonationMilestone {
        NONE(0),
        NOVICE(10),
        COLLECTOR(25),
        CURATOR(50),
        PATRON(100);

        /** Total donations required to reach this milestone. */
        public final int threshold;

        DonationMilestone(int threshold) {
            this.threshold = threshold;
        }

        public int getThreshold() {
            return threshold;
        }
    }


    private static final MuseumManager INSTANCE = new MuseumManager();

    /** Per-player donated items by category. */
    private final Map<UUID, Map<MuseumCategory, Set<String>>> donations = new java.util.HashMap<>();

    /** Catalog of all donatable item names per category, used to measure completion. */
    private final Map<MuseumCategory, Set<String>> catalog = new EnumMap<>(MuseumCategory.class);

    /** Museum value contributed by each registered item, used for value totals. */
    private final Map<String, Long> itemValues = new java.util.HashMap<>();

    private MuseumManager() {
    }

    /**
     * Registers an item as donatable in the given category, expanding the completion catalog.
     *
     * @param category the museum category
     * @param itemName the name of the donatable item
     * @return {@code true} if the item was newly registered, {@code false} if already known
     */
    public boolean registerItem(MuseumCategory category, String itemName) {
        return registerItem(category, itemName, 0L);
    }

    /**
     * Registers an item as donatable in the given category with a museum value.
     *
     * @param category the museum category
     * @param itemName the name of the donatable item
     * @param value    the museum value the item contributes when donated
     * @return {@code true} if the item was newly registered, {@code false} if already known
     */
    public boolean registerItem(MuseumCategory category, String itemName, long value) {
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(itemName, "itemName");
        itemValues.put(itemName, value);
        return catalog.computeIfAbsent(category, c -> new HashSet<>()).add(itemName);
    }

    /**
     * Returns the number of registered donatable items in the given category.
     *
     * @param category the museum category
     * @return the catalog size for the category
     */
    public int getCategorySize(MuseumCategory category) {
        Objects.requireNonNull(category, "category");
        Set<String> items = catalog.get(category);
        return items == null ? 0 : items.size();
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
     * Returns the total museum value of all items the player has donated.
     *
     * <p>Only donated items registered in the catalog contribute; each adds the value
     * supplied at {@link #registerItem(MuseumCategory, String, long)} (zero by default).</p>
     *
     * @param playerId the player
     * @return the summed museum value across all donations
     */
    public long getMuseumValue(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<MuseumCategory, Set<String>> playerDonations = donations.get(playerId);
        if (playerDonations == null) return 0L;
        long total = 0L;
        for (Set<String> items : playerDonations.values()) {
            for (String item : items) {
                total += itemValues.getOrDefault(item, 0L);
            }
        }
        return total;
    }

    /**
     * Returns the player's completion of the given category as a fraction in {@code [0.0, 1.0]}.
     *
     * <p>Completion counts only donated items that are registered in the catalog. A category
     * with no registered items is considered complete and returns {@code 1.0}.</p>
     *
     * @param playerId the player
     * @param category the museum category
     * @return the completion fraction, between {@code 0.0} and {@code 1.0}
     */
    public double getCategoryCompletion(UUID playerId, MuseumCategory category) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(category, "category");
        int size = getCategorySize(category);
        if (size == 0) return 1.0;
        Set<String> registered = catalog.get(category);
        int donated = 0;
        for (String item : getDonations(playerId, category)) {
            if (registered.contains(item)) donated++;
        }
        return (double) donated / size;
    }

    /**
     * Returns whether the player has donated every registered item in the given category.
     *
     * @param playerId the player
     * @param category the museum category
     * @return {@code true} if the category is fully donated
     */
    public boolean isCategoryComplete(UUID playerId, MuseumCategory category) {
        return getCategoryCompletion(playerId, category) >= 1.0;
    }

    /**
     * Returns the highest {@link DonationMilestone} the player has reached based on
     * their total number of unique donations across all categories.
     *
     * @param playerId the player
     * @return the highest milestone whose threshold the player meets, never {@code null}
     */
    public DonationMilestone getMilestone(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int total = getTotalDonations(playerId);
        DonationMilestone reached = DonationMilestone.NONE;
        for (DonationMilestone milestone : DonationMilestone.values()) {
            if (total >= milestone.threshold) reached = milestone;
        }
        return reached;
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
