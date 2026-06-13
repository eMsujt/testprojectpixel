package com.skyblock.core.calendar;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for the SkyBlock in-game calendar.
 *
 * <p>Tracks the current {@link SkyBlockMonth} and day within the year, and
 * accumulates per-player event participation counts for calendar-gated
 * rewards.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class CalendarManager {

    /** The twelve months of the SkyBlock year, in order. */
    public enum SkyBlockMonth {
        EARLY_SPRING("Early Spring"),
        SPRING("Spring"),
        LATE_SPRING("Late Spring"),
        EARLY_SUMMER("Early Summer"),
        SUMMER("Summer"),
        LATE_SUMMER("Late Summer"),
        EARLY_AUTUMN("Early Autumn"),
        AUTUMN("Autumn"),
        LATE_AUTUMN("Late Autumn"),
        EARLY_WINTER("Early Winter"),
        WINTER("Winter"),
        LATE_WINTER("Late Winter");

        /** Human-readable display name shown to players. */
        public final String displayName;

        SkyBlockMonth(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** The number of days in each SkyBlock month. */
    public static final int DAYS_PER_MONTH = 31;
    /** Total days in a SkyBlock year. */
    public static final int DAYS_PER_YEAR = SkyBlockMonth.values().length * DAYS_PER_MONTH;

    private static final CalendarManager INSTANCE = new CalendarManager();

    /** Current day within the SkyBlock year (1-based). */
    private int currentDay = 1;
    /** Per-player count of calendar events participated in this year. */
    private final Map<UUID, Integer> eventParticipation = new HashMap<>();

    private CalendarManager() {}

    /**
     * Returns the single shared {@code CalendarManager} instance.
     *
     * @return the singleton instance
     */
    public static CalendarManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the current day within the SkyBlock year (1–{@value #DAYS_PER_YEAR}).
     *
     * @return current day
     */
    public int getCurrentDay() {
        return currentDay;
    }

    /**
     * Advances the calendar by one day, wrapping at year end.
     *
     * @return the new current day
     */
    public int advanceDay() {
        currentDay = (currentDay % DAYS_PER_YEAR) + 1;
        return currentDay;
    }

    /**
     * Sets the current day within the SkyBlock year.
     *
     * @param day day number, must be between 1 and {@value #DAYS_PER_YEAR}
     * @throws IllegalArgumentException if {@code day} is out of range
     */
    public void setCurrentDay(int day) {
        if (day < 1 || day > DAYS_PER_YEAR) {
            throw new IllegalArgumentException(
                    "day must be between 1 and " + DAYS_PER_YEAR + ", got " + day);
        }
        currentDay = day;
    }

    /**
     * Returns the {@link SkyBlockMonth} corresponding to the current day.
     *
     * @return current month
     */
    public SkyBlockMonth getCurrentMonth() {
        return monthForDay(currentDay);
    }

    /**
     * Returns the day-of-month for the current day (1–{@value #DAYS_PER_MONTH}).
     *
     * @return day within the current month
     */
    public int getCurrentDayOfMonth() {
        return ((currentDay - 1) % DAYS_PER_MONTH) + 1;
    }

    /**
     * Returns the {@link SkyBlockMonth} for the given year-day.
     *
     * @param day year-day (1–{@value #DAYS_PER_YEAR})
     * @return corresponding month
     * @throws IllegalArgumentException if {@code day} is out of range
     */
    public static SkyBlockMonth monthForDay(int day) {
        if (day < 1 || day > DAYS_PER_YEAR) {
            throw new IllegalArgumentException(
                    "day must be between 1 and " + DAYS_PER_YEAR + ", got " + day);
        }
        int index = (day - 1) / DAYS_PER_MONTH;
        return SkyBlockMonth.values()[index];
    }

    /**
     * Records that the player participated in a calendar event.
     *
     * @param playerId the player who participated
     * @return the player's updated total participation count for this year
     */
    public int recordEventParticipation(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return eventParticipation.merge(playerId, 1, Integer::sum);
    }

    /**
     * Returns how many calendar events the player has participated in this year.
     *
     * @param playerId the player to look up
     * @return event participation count, {@code 0} if none recorded
     */
    public int getEventParticipation(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return eventParticipation.getOrDefault(playerId, 0);
    }

    /**
     * Resets all per-player event participation counts for the new year.
     */
    public void resetYearlyParticipation() {
        eventParticipation.clear();
    }
}
