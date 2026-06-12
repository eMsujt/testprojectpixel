package com.skyblock.calendar;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages the in-game calendar: converting elapsed game ticks to calendar
 * dates and scheduling named events on recurring (month, day) dates.
 *
 * <p>The calendar uses fixed lengths of {@value #DAYS_PER_MONTH} days per
 * month and {@value #MONTHS_PER_YEAR} months per year, with each in-game day
 * lasting {@value #TICKS_PER_DAY} ticks. Dates are derived purely from the
 * elapsed tick count, so the manager is deterministic and safe to query from
 * any thread. Events are stored in a {@link ConcurrentHashMap} keyed by
 * (month, day), with concurrent lists as values, so registration is also
 * thread-safe.</p>
 */
public final class CalendarManager {

    /** Length of one in-game day, in ticks (20 minutes at 20 TPS). */
    public static final long TICKS_PER_DAY = 24_000L;

    /** Number of days in each calendar month. */
    public static final int DAYS_PER_MONTH = 31;

    /** Number of months in each calendar year. */
    public static final int MONTHS_PER_YEAR = 12;

    /**
     * An in-game calendar date. Months and days are 1-based; years start at 1.
     *
     * @param year  the calendar year, starting at {@code 1}
     * @param month the month of the year, {@code 1}..{@value #MONTHS_PER_YEAR}
     * @param day   the day of the month, {@code 1}..{@value #DAYS_PER_MONTH}
     */
    public record CalendarDate(int year, int month, int day) {

        public CalendarDate {
            if (year < 1) {
                throw new IllegalArgumentException("year must be >= 1");
            }
            if (month < 1 || month > MONTHS_PER_YEAR) {
                throw new IllegalArgumentException("month must be 1.." + MONTHS_PER_YEAR);
            }
            if (day < 1 || day > DAYS_PER_MONTH) {
                throw new IllegalArgumentException("day must be 1.." + DAYS_PER_MONTH);
            }
        }
    }

    private final ConcurrentHashMap<Long, List<String>> events = new ConcurrentHashMap<>();

    /**
     * Converts an elapsed tick count to the calendar date it falls on.
     *
     * @param elapsedTicks ticks elapsed since the calendar epoch, must be {@code >= 0}
     * @return the calendar date containing the given tick
     */
    public CalendarDate dateAt(long elapsedTicks) {
        if (elapsedTicks < 0) {
            throw new IllegalArgumentException("elapsedTicks must be >= 0");
        }
        long totalDays = elapsedTicks / TICKS_PER_DAY;
        int day = (int) (totalDays % DAYS_PER_MONTH) + 1;
        long totalMonths = totalDays / DAYS_PER_MONTH;
        int month = (int) (totalMonths % MONTHS_PER_YEAR) + 1;
        int year = (int) (totalMonths / MONTHS_PER_YEAR) + 1;
        return new CalendarDate(year, month, day);
    }

    /**
     * Registers a named event that recurs every year on the given month and day.
     *
     * @param month     the month of the year, {@code 1}..{@value #MONTHS_PER_YEAR}
     * @param day       the day of the month, {@code 1}..{@value #DAYS_PER_MONTH}
     * @param eventName the name of the event to register
     */
    public void registerEvent(int month, int day, String eventName) {
        requireEventName(eventName);
        long key = dateKey(month, day);
        events.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(eventName);
    }

    /**
     * Returns an unmodifiable view of the events registered on the given month and day.
     *
     * @param month the month of the year, {@code 1}..{@value #MONTHS_PER_YEAR}
     * @param day   the day of the month, {@code 1}..{@value #DAYS_PER_MONTH}
     * @return the event names registered on that date, empty if there are none
     */
    public List<String> getEvents(int month, int day) {
        List<String> registered = events.get(dateKey(month, day));
        return registered != null ? Collections.unmodifiableList(registered) : List.of();
    }

    /**
     * Returns the events occurring on the date the given tick falls on.
     *
     * @param elapsedTicks ticks elapsed since the calendar epoch, must be {@code >= 0}
     * @return the event names registered on that date, empty if there are none
     */
    public List<String> getEventsAt(long elapsedTicks) {
        CalendarDate date = dateAt(elapsedTicks);
        return getEvents(date.month(), date.day());
    }

    /**
     * Removes a single registered event from the given month and day.
     *
     * @param month     the month of the year, {@code 1}..{@value #MONTHS_PER_YEAR}
     * @param day       the day of the month, {@code 1}..{@value #DAYS_PER_MONTH}
     * @param eventName the name of the event to remove
     * @return {@code true} if the event was registered and has been removed
     */
    public boolean unregisterEvent(int month, int day, String eventName) {
        requireEventName(eventName);
        List<String> registered = events.get(dateKey(month, day));
        return registered != null && registered.remove(eventName);
    }

    /**
     * Removes all registered events (e.g. on reload).
     *
     * @return the number of events that were registered
     */
    public int clearEvents() {
        int removed = events.values().stream().mapToInt(List::size).sum();
        events.clear();
        return removed;
    }

    /**
     * Returns an unmodifiable snapshot of all registered events keyed by
     * "month/day" (e.g. {@code "3/14"}).
     *
     * @return all registered events, empty if there are none
     */
    public Map<String, List<String>> getAllEvents() {
        Map<String, List<String>> snapshot = new ConcurrentHashMap<>();
        events.forEach((key, names) -> {
            int month = (int) (key / DAYS_PER_MONTH) + 1;
            int day = (int) (key % DAYS_PER_MONTH) + 1;
            snapshot.put(month + "/" + day, List.copyOf(names));
        });
        return Collections.unmodifiableMap(snapshot);
    }

    private static long dateKey(int month, int day) {
        if (month < 1 || month > MONTHS_PER_YEAR) {
            throw new IllegalArgumentException("month must be 1.." + MONTHS_PER_YEAR);
        }
        if (day < 1 || day > DAYS_PER_MONTH) {
            throw new IllegalArgumentException("day must be 1.." + DAYS_PER_MONTH);
        }
        return (long) (month - 1) * DAYS_PER_MONTH + (day - 1);
    }

    private static void requireEventName(String eventName) {
        if (eventName == null || eventName.isBlank()) {
            throw new IllegalArgumentException("eventName must be non-blank");
        }
    }
}
