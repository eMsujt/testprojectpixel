package com.skyblock.core.util;

/**
 * Pure-utility class for SkyBlock calendar arithmetic.
 *
 * <p>Contains the canonical {@link SkyBlockMonth} enum and static helpers for
 * converting between year-days, month+day pairs, and display strings.
 * All state (current day, events, participation) lives in
 * {@code com.skyblock.core.manager.CalendarManager}.</p>
 */
public final class SkyBlockCalendar {

    /** The twelve months (seasons) of a SkyBlock year, in order. */
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

        public final String displayName;

        SkyBlockMonth(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** Days in each SkyBlock month. */
    public static final int DAYS_PER_MONTH = 31;
    /** Total days in a SkyBlock year (12 months × 31 days). */
    public static final int DAYS_PER_YEAR = SkyBlockMonth.values().length * DAYS_PER_MONTH;

    private SkyBlockCalendar() {}

    /**
     * Returns the {@link SkyBlockMonth} for the given year-day.
     *
     * @param yearDay 1-based day within the year (1–{@value #DAYS_PER_YEAR})
     * @return the corresponding month
     * @throws IllegalArgumentException if {@code yearDay} is out of range
     */
    public static SkyBlockMonth monthForDay(int yearDay) {
        checkYearDay(yearDay);
        return SkyBlockMonth.values()[(yearDay - 1) / DAYS_PER_MONTH];
    }

    /**
     * Returns the day-of-month (1–{@value #DAYS_PER_MONTH}) for the given year-day.
     *
     * @param yearDay 1-based day within the year (1–{@value #DAYS_PER_YEAR})
     * @return day within the month
     * @throws IllegalArgumentException if {@code yearDay} is out of range
     */
    public static int dayOfMonth(int yearDay) {
        checkYearDay(yearDay);
        return ((yearDay - 1) % DAYS_PER_MONTH) + 1;
    }

    /**
     * Returns the year-day for the given month and day-of-month.
     *
     * @param month      the target month (non-null)
     * @param dayOfMonth day within the month (1–{@value #DAYS_PER_MONTH})
     * @return the 1-based year-day
     * @throws IllegalArgumentException if {@code dayOfMonth} is out of range
     */
    public static int yearDayOf(SkyBlockMonth month, int dayOfMonth) {
        if (month == null) throw new IllegalArgumentException("month must not be null");
        if (dayOfMonth < 1 || dayOfMonth > DAYS_PER_MONTH) {
            throw new IllegalArgumentException(
                    "dayOfMonth must be between 1 and " + DAYS_PER_MONTH + ", got " + dayOfMonth);
        }
        return month.ordinal() * DAYS_PER_MONTH + dayOfMonth;
    }

    /**
     * Returns a human-readable label for the given year-day, e.g. {@code "Early Spring 3"}.
     *
     * @param yearDay 1-based day within the year (1–{@value #DAYS_PER_YEAR})
     * @return formatted date string
     */
    public static String formatDate(int yearDay) {
        return monthForDay(yearDay).displayName + " " + dayOfMonth(yearDay);
    }

    private static void checkYearDay(int yearDay) {
        if (yearDay < 1 || yearDay > DAYS_PER_YEAR) {
            throw new IllegalArgumentException(
                    "yearDay must be between 1 and " + DAYS_PER_YEAR + ", got " + yearDay);
        }
    }
}
