package com.skyblock.events;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Singleton tracking which {@link SkyBlockEvent}s are currently active.
 *
 * <p>Events are started and stopped explicitly; the manager keeps the set of
 * active events and answers queries about them. Access the shared instance
 * via {@link #getInstance()}. Not thread-safe; synchronize externally if
 * accessed from multiple threads.</p>
 */
public final class EventManager {

    private static final EventManager INSTANCE = new EventManager();

    private final Set<SkyBlockEvent> activeEvents = EnumSet.noneOf(SkyBlockEvent.class);

    private EventManager() {
    }

    /**
     * Returns the shared manager instance.
     *
     * @return the singleton {@code EventManager}
     */
    public static EventManager getInstance() {
        return INSTANCE;
    }

    /**
     * Starts an event, marking it as active.
     *
     * @param event the event to start, must not be null
     * @return {@code true} if the event was not already active
     * @throws IllegalArgumentException if the event is null
     */
    public boolean startEvent(SkyBlockEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        return activeEvents.add(event);
    }

    /**
     * Stops an event, marking it as no longer active.
     *
     * @param event the event to stop, must not be null
     * @return {@code true} if the event was active and has been stopped
     * @throws IllegalArgumentException if the event is null
     */
    public boolean stopEvent(SkyBlockEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        return activeEvents.remove(event);
    }

    /**
     * Returns whether an event is currently active.
     *
     * @param event the event to check
     * @return {@code true} if the event is active
     */
    public boolean isActive(SkyBlockEvent event) {
        return activeEvents.contains(event);
    }

    /**
     * Returns the currently active events.
     *
     * @return an unmodifiable view of the active events
     */
    public Set<SkyBlockEvent> getActiveEvents() {
        return Collections.unmodifiableSet(activeEvents);
    }

    /**
     * Returns the currently active events that grant bonus rewards.
     *
     * @return an unmodifiable set of the active bonus-reward events, empty if none
     */
    public Set<SkyBlockEvent> getActiveBonusRewardEvents() {
        EnumSet<SkyBlockEvent> bonus = EnumSet.noneOf(SkyBlockEvent.class);
        for (SkyBlockEvent event : activeEvents) {
            if (event.grantsBonusRewards()) {
                bonus.add(event);
            }
        }
        return Collections.unmodifiableSet(bonus);
    }

    /**
     * Stops all active events.
     */
    public void stopAllEvents() {
        activeEvents.clear();
    }

    /**
     * Returns the number of currently active events.
     *
     * @return the active event count
     */
    public int getActiveEventCount() {
        return activeEvents.size();
    }
}
