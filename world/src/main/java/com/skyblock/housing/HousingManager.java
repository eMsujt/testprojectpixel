package com.skyblock.housing;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages player housing plots: claiming a personal plot, naming it,
 * controlling its visit permission, and maintaining a guest whitelist.
 *
 * <p>Each player owns at most one plot, stored in a {@link ConcurrentHashMap}
 * keyed by owner UUID. Per-plot guest sets are concurrent as well, so all
 * operations are thread-safe.</p>
 */
public final class HousingManager {

    /** Maximum length of a plot name. */
    public static final int MAX_NAME_LENGTH = 32;

    /** Who is allowed to visit a plot. */
    public enum VisitPermission {
        /** Anyone may visit. */
        PUBLIC,
        /** Only whitelisted guests may visit. */
        GUESTS_ONLY,
        /** Only the owner may visit. */
        PRIVATE
    }

    /** A player's housing plot. */
    public static final class Plot {

        private final UUID owner;
        private volatile String name;
        private volatile VisitPermission permission;
        private final Set<UUID> guests = ConcurrentHashMap.newKeySet();

        private Plot(UUID owner, String name) {
            this.owner = owner;
            this.name = name;
            this.permission = VisitPermission.PRIVATE;
        }

        /** Returns the UUID of the plot's owner. */
        public UUID getOwner() {
            return owner;
        }

        /** Returns the plot's display name. */
        public String getName() {
            return name;
        }

        /** Returns the plot's current visit permission. */
        public VisitPermission getPermission() {
            return permission;
        }

        /** Returns an unmodifiable view of the plot's guest whitelist. */
        public Set<UUID> getGuests() {
            return Collections.unmodifiableSet(guests);
        }
    }

    private final ConcurrentHashMap<UUID, Plot> plots = new ConcurrentHashMap<>();

    /**
     * Claims a plot for the given player.
     *
     * @param owner the claiming player's UUID
     * @param name  the plot's display name, non-blank and at most
     *              {@value #MAX_NAME_LENGTH} characters
     * @return the newly created plot
     * @throws IllegalStateException if the player already owns a plot
     */
    public Plot claimPlot(UUID owner, String name) {
        requireOwner(owner);
        String validated = requireName(name);
        Plot created = new Plot(owner, validated);
        if (plots.putIfAbsent(owner, created) != null) {
            throw new IllegalStateException("player already owns a plot");
        }
        return created;
    }

    /**
     * Returns the plot owned by the given player, if any.
     *
     * @param owner the owner's UUID
     * @return the player's plot, or empty if they have not claimed one
     */
    public Optional<Plot> getPlot(UUID owner) {
        requireOwner(owner);
        return Optional.ofNullable(plots.get(owner));
    }

    /**
     * Renames the given player's plot.
     *
     * @param owner the owner's UUID
     * @param name  the new display name, non-blank and at most
     *              {@value #MAX_NAME_LENGTH} characters
     * @return {@code true} if the player owns a plot and it was renamed
     */
    public boolean renamePlot(UUID owner, String name) {
        requireOwner(owner);
        String validated = requireName(name);
        Plot plot = plots.get(owner);
        if (plot == null) {
            return false;
        }
        plot.name = validated;
        return true;
    }

    /**
     * Sets the visit permission of the given player's plot.
     *
     * @param owner      the owner's UUID
     * @param permission the new visit permission
     * @return {@code true} if the player owns a plot and it was updated
     */
    public boolean setPermission(UUID owner, VisitPermission permission) {
        requireOwner(owner);
        if (permission == null) {
            throw new IllegalArgumentException("permission must be non-null");
        }
        Plot plot = plots.get(owner);
        if (plot == null) {
            return false;
        }
        plot.permission = permission;
        return true;
    }

    /**
     * Adds a guest to the given player's plot whitelist.
     *
     * @param owner the owner's UUID
     * @param guest the guest's UUID, must differ from the owner
     * @return {@code true} if the player owns a plot and the guest was newly added
     */
    public boolean addGuest(UUID owner, UUID guest) {
        requireGuest(owner, guest);
        Plot plot = plots.get(owner);
        return plot != null && plot.guests.add(guest);
    }

    /**
     * Removes a guest from the given player's plot whitelist.
     *
     * @param owner the owner's UUID
     * @param guest the guest's UUID
     * @return {@code true} if the player owns a plot and the guest was removed
     */
    public boolean removeGuest(UUID owner, UUID guest) {
        requireGuest(owner, guest);
        Plot plot = plots.get(owner);
        return plot != null && plot.guests.remove(guest);
    }

    /**
     * Returns whether the given visitor may enter the given player's plot.
     * The owner may always visit their own plot.
     *
     * @param owner   the plot owner's UUID
     * @param visitor the visiting player's UUID
     * @return {@code true} if the plot exists and the visitor is allowed in
     */
    public boolean canVisit(UUID owner, UUID visitor) {
        requireOwner(owner);
        if (visitor == null) {
            throw new IllegalArgumentException("visitor must be non-null");
        }
        Plot plot = plots.get(owner);
        if (plot == null) {
            return false;
        }
        if (owner.equals(visitor)) {
            return true;
        }
        return switch (plot.permission) {
            case PUBLIC -> true;
            case GUESTS_ONLY -> plot.guests.contains(visitor);
            case PRIVATE -> false;
        };
    }

    /**
     * Unclaims and removes the given player's plot.
     *
     * @param owner the owner's UUID
     * @return {@code true} if the player owned a plot and it was removed
     */
    public boolean unclaimPlot(UUID owner) {
        requireOwner(owner);
        return plots.remove(owner) != null;
    }

    /**
     * Returns an unmodifiable view of all claimed plots keyed by owner UUID.
     *
     * @return all claimed plots, empty if there are none
     */
    public Map<UUID, Plot> getAllPlots() {
        return Collections.unmodifiableMap(plots);
    }

    private static void requireOwner(UUID owner) {
        if (owner == null) {
            throw new IllegalArgumentException("owner must be non-null");
        }
    }

    private static void requireGuest(UUID owner, UUID guest) {
        requireOwner(owner);
        if (guest == null) {
            throw new IllegalArgumentException("guest must be non-null");
        }
        if (owner.equals(guest)) {
            throw new IllegalArgumentException("guest must differ from the owner");
        }
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must be non-blank");
        }
        String trimmed = name.trim();
        if (trimmed.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("name must be at most " + MAX_NAME_LENGTH + " characters");
        }
        return trimmed;
    }
}
