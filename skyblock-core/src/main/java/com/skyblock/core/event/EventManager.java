package com.skyblock.core.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking per-player SkyBlock event participation and scores.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class EventManager {

    /** All SkyBlock seasonal/special events. */
    public enum SkyBlockEvent {
        SPOOKY_FESTIVAL("Spooky Festival"),
        TRAVELING_ZOO("Traveling Zoo"),
        NEW_YEAR_CELEBRATION("New Year Celebration"),
        JERRY_WORKSHOP("Jerry's Workshop"),
        DARK_AUCTION("Dark Auction");

        private final String displayName;

        SkyBlockEvent(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    /** Participation status for a single event. */
    public enum EventStatus {
        NOT_JOINED, ACTIVE, COMPLETED
    }

    private static final EventManager INSTANCE = new EventManager();

    /** Per-player event scores, keyed by event. */
    private final Map<UUID, Map<SkyBlockEvent, Long>> scores = new HashMap<>();

    /** Per-player event status, keyed by event. */
    private final Map<UUID, Map<SkyBlockEvent, EventStatus>> statuses = new HashMap<>();

    private EventManager() {
    }

    /**
     * Returns the single shared {@code EventManager} instance.
     *
     * @return the singleton instance
     */
    public static EventManager getInstance() {
        return INSTANCE;
    }

    /**
     * Joins the given event for a player, setting their status to {@link EventStatus#ACTIVE}.
     *
     * @param playerId the player joining the event
     * @param event    the event to join
     */
    public void joinEvent(UUID playerId, SkyBlockEvent event) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(event, "event");
        statuses.computeIfAbsent(playerId, id -> new HashMap<>()).put(event, EventStatus.ACTIVE);
        scores.computeIfAbsent(playerId, id -> new HashMap<>()).putIfAbsent(event, 0L);
    }

    /**
     * Adds score to the given event for a player.
     *
     * @param playerId the player earning score
     * @param event    the event being scored
     * @param amount   the amount to add, must not be negative
     * @return the player's total score for the event after the addition
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public long addScore(UUID playerId, SkyBlockEvent event, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(event, "event");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<SkyBlockEvent, Long> scoreMap = scores.computeIfAbsent(playerId, id -> new HashMap<>());
        long total = scoreMap.getOrDefault(event, 0L) + amount;
        scoreMap.put(event, total);
        return total;
    }

    /**
     * Returns the player's current score for the given event.
     *
     * @param playerId the player to look up
     * @param event    the event to look up
     * @return the current score, {@code 0} if the player has none
     */
    public long getScore(UUID playerId, SkyBlockEvent event) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(event, "event");
        Map<SkyBlockEvent, Long> scoreMap = scores.get(playerId);
        return scoreMap == null ? 0L : scoreMap.getOrDefault(event, 0L);
    }

    /**
     * Returns the player's status for the given event.
     *
     * @param playerId the player to look up
     * @param event    the event to look up
     * @return the {@link EventStatus}, or {@link EventStatus#NOT_JOINED} if never joined
     */
    public EventStatus getStatus(UUID playerId, SkyBlockEvent event) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(event, "event");
        Map<SkyBlockEvent, EventStatus> statusMap = statuses.get(playerId);
        if (statusMap == null) {
            return EventStatus.NOT_JOINED;
        }
        return statusMap.getOrDefault(event, EventStatus.NOT_JOINED);
    }

    /**
     * Marks the given event as completed for the player.
     *
     * @param playerId the player to complete the event for
     * @param event    the event to complete
     */
    public void completeEvent(UUID playerId, SkyBlockEvent event) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(event, "event");
        statuses.computeIfAbsent(playerId, id -> new HashMap<>()).put(event, EventStatus.COMPLETED);
    }

    /**
     * Resets all event data for the given player.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had any event data, {@code false} otherwise
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadData = scores.remove(playerId) != null;
        hadData |= statuses.remove(playerId) != null;
        return hadData;
    }
}
