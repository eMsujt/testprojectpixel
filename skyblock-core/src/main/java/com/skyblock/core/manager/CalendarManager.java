package com.skyblock.core.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.skyblock.core.manager.GardenManager.GardenCrop;

/**
 * Canonical singleton manager for the SkyBlock in-game calendar.
 *
 * <p>Tracks the current {@link SkyBlockMonth} (the in-game season) and day
 * within the year, drives the season cycle via {@link #advanceDay()}, exposes
 * the recurring scheduled events (Jerry's Workshop, the Spooky Festival, etc.)
 * registered against calendar dates, and accumulates per-player event
 * participation counts for calendar-gated rewards.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class CalendarManager {

    /** The twelve months of the SkyBlock year (also the seasons), in order. */
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

    /** A new Jacob's Farming Contest begins every this many in-game days. */
    public static final int CONTEST_INTERVAL_DAYS = 3;
    /** Number of crops featured in each Jacob's Farming Contest. */
    public static final int CROPS_PER_CONTEST = 3;

    private static final CalendarManager INSTANCE = new CalendarManager();

    /** Current day within the SkyBlock year (1-based). */
    private int currentDay = 1;
    /** Per-player count of calendar events participated in this year. */
    private final Map<UUID, Integer> eventParticipation = new HashMap<>();
    /** Recurring events keyed by year-day (1-based); each day may host several. */
    private final Map<Integer, List<String>> scheduledEvents = new HashMap<>();

    private CalendarManager() {
        registerDefaultEvents();
    }

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
     * Returns the {@link SkyBlockMonth} (current season) corresponding to the current day.
     *
     * @return current month
     */
    public SkyBlockMonth getCurrentMonth() {
        return monthForDay(currentDay);
    }

    /**
     * Returns the current SkyBlock season, which is identical to the
     * {@linkplain #getCurrentMonth() current month}.
     *
     * @return current season
     */
    public SkyBlockMonth getCurrentSeason() {
        return getCurrentMonth();
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
     * Returns the year-day (1–{@value #DAYS_PER_YEAR}) for the given month and day-of-month.
     *
     * @param month      the month the date falls in
     * @param dayOfMonth the day within that month (1–{@value #DAYS_PER_MONTH})
     * @return the corresponding year-day
     * @throws IllegalArgumentException if {@code dayOfMonth} is out of range
     */
    public static int yearDayOf(SkyBlockMonth month, int dayOfMonth) {
        Objects.requireNonNull(month, "month");
        if (dayOfMonth < 1 || dayOfMonth > DAYS_PER_MONTH) {
            throw new IllegalArgumentException(
                    "dayOfMonth must be between 1 and " + DAYS_PER_MONTH + ", got " + dayOfMonth);
        }
        return month.ordinal() * DAYS_PER_MONTH + dayOfMonth;
    }

    /**
     * Registers a recurring event that occurs every year on the given date.
     *
     * @param month      the month the event occurs in
     * @param dayOfMonth the day within that month (1–{@value #DAYS_PER_MONTH})
     * @param eventName  the event's display name (must be non-blank)
     * @throws IllegalArgumentException if {@code dayOfMonth} is out of range or {@code eventName} is blank
     */
    public void registerEvent(SkyBlockMonth month, int dayOfMonth, String eventName) {
        if (eventName == null || eventName.isBlank()) {
            throw new IllegalArgumentException("eventName must be non-blank");
        }
        int yearDay = yearDayOf(month, dayOfMonth);
        scheduledEvents.computeIfAbsent(yearDay, k -> new ArrayList<>()).add(eventName);
    }

    /**
     * Returns the recurring events scheduled on the given date.
     *
     * @param month      the month to look up
     * @param dayOfMonth the day within that month (1–{@value #DAYS_PER_MONTH})
     * @return an unmodifiable list of event names, empty if none are scheduled
     */
    public List<String> getEventsOn(SkyBlockMonth month, int dayOfMonth) {
        List<String> events = scheduledEvents.get(yearDayOf(month, dayOfMonth));
        return events != null ? Collections.unmodifiableList(events) : List.of();
    }

    /**
     * Returns the recurring events scheduled on the current calendar day.
     *
     * @return an unmodifiable list of event names, empty if none are scheduled
     */
    public List<String> getEventsToday() {
        List<String> events = scheduledEvents.get(currentDay);
        return events != null ? Collections.unmodifiableList(events) : List.of();
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

    /**
     * Returns whether a Jacob's Farming Contest is scheduled on the given year-day.
     *
     * <p>Contests recur every {@value #CONTEST_INTERVAL_DAYS} in-game days, starting
     * on day 1 of the year.</p>
     *
     * @param day year-day (1–{@value #DAYS_PER_YEAR})
     * @return {@code true} if a contest is scheduled that day
     * @throws IllegalArgumentException if {@code day} is out of range
     */
    public static boolean isContestDay(int day) {
        if (day < 1 || day > DAYS_PER_YEAR) {
            throw new IllegalArgumentException(
                    "day must be between 1 and " + DAYS_PER_YEAR + ", got " + day);
        }
        return (day - 1) % CONTEST_INTERVAL_DAYS == 0;
    }

    /**
     * Returns the {@value #CROPS_PER_CONTEST} crops featured in the Jacob's Farming
     * Contest scheduled on the given year-day.
     *
     * <p>The crop line-up is derived deterministically from the year-day, so the
     * same date always yields the same crops without storing a schedule.</p>
     *
     * @param day year-day (1–{@value #DAYS_PER_YEAR})
     * @return an unmodifiable list of the contest's crops, or an empty list if no
     *         contest is scheduled that day
     * @throws IllegalArgumentException if {@code day} is out of range
     */
    public static List<GardenCrop> getGardenCrops(int day) {
        if (!isContestDay(day)) {
            return List.of();
        }
        GardenCrop[] all = GardenCrop.values();
        // Stride of 5 is co-prime to the crop count, so the three picks are distinct.
        int base = (day * 7) % all.length;
        List<GardenCrop> crops = new ArrayList<>(CROPS_PER_CONTEST);
        for (int i = 0; i < CROPS_PER_CONTEST; i++) {
            crops.add(all[(base + i * 5) % all.length]);
        }
        return Collections.unmodifiableList(crops);
    }

    /**
     * Returns whether a Jacob's Farming Contest is scheduled on the current calendar day.
     *
     * @return {@code true} if a contest is active today
     */
    public boolean isContestToday() {
        return isContestDay(currentDay);
    }

    /**
     * Returns the crops featured in today's Jacob's Farming Contest.
     *
     * @return an unmodifiable list of the contest's crops, empty if none is scheduled today
     */
    public List<GardenCrop> getGardenCropsToday() {
        return getGardenCrops(currentDay);
    }

    /**
     * Returns the next year-day on or after {@code fromDay} that hosts a Jacob's
     * Farming Contest, wrapping around the year if necessary.
     *
     * @param fromDay the year-day to start searching from (1–{@value #DAYS_PER_YEAR})
     * @return the next contest year-day
     * @throws IllegalArgumentException if {@code fromDay} is out of range
     */
    public static int nextContestDay(int fromDay) {
        if (fromDay < 1 || fromDay > DAYS_PER_YEAR) {
            throw new IllegalArgumentException(
                    "fromDay must be between 1 and " + DAYS_PER_YEAR + ", got " + fromDay);
        }
        for (int offset = 0; offset < CONTEST_INTERVAL_DAYS; offset++) {
            int day = ((fromDay - 1 + offset) % DAYS_PER_YEAR) + 1;
            if (isContestDay(day)) {
                return day;
            }
        }
        return fromDay; // unreachable: a contest day always exists within the interval window
    }

    /** Registers the standard recurring SkyBlock calendar events. */
    private void registerDefaultEvents() {
        registerEvent(SkyBlockMonth.EARLY_SUMMER, 1, "Traveling Zoo");
        registerEvent(SkyBlockMonth.AUTUMN, 29, "Spooky Festival");
        registerEvent(SkyBlockMonth.EARLY_WINTER, 1, "Traveling Zoo");
        registerEvent(SkyBlockMonth.LATE_WINTER, 1, "Jerry's Workshop");
        registerEvent(SkyBlockMonth.LATE_WINTER, 29, "New Year Celebration");
    }
}
