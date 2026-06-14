package com.skyblock.plugin.calendar;

/**
 * Utility for converting real time into SkyBlock calendar values.
 *
 * <p>One SkyBlock day equals 20 real minutes (24 000 Minecraft ticks). Each
 * month has 31 days and a year has 12 months, giving 372 SkyBlock days per
 * year. All methods derive their values from {@link System#currentTimeMillis()}
 * and are safe to call from any thread.</p>
 */
public final class SkyBlockCalendar {

    /** Minecraft ticks in one SkyBlock day. */
    public static final long TICKS_PER_DAY = 24_000L;

    /** Real milliseconds in one SkyBlock day (20 minutes). */
    public static final long MS_PER_DAY = 20L * 60L * 1_000L;

    /** SkyBlock days per month. */
    public static final int DAYS_PER_MONTH = 31;

    /** SkyBlock months per year. */
    public static final int MONTHS_PER_YEAR = 12;

    /** SkyBlock days per year. */
    public static final int DAYS_PER_YEAR = DAYS_PER_MONTH * MONTHS_PER_YEAR;

    /** The twelve SkyBlock month names, in calendar order. */
    public static final String[] MONTH_NAMES = {
            "Early Spring", "Spring", "Late Spring",
            "Early Summer", "Summer", "Late Summer",
            "Early Autumn", "Autumn", "Late Autumn",
            "Early Winter", "Winter", "Late Winter"
    };

    private SkyBlockCalendar() {}

    /** Returns the total number of elapsed SkyBlock days since the epoch. */
    public static long totalDays() {
        return System.currentTimeMillis() / MS_PER_DAY;
    }

    /**
     * Returns the current day of the month (1–31).
     */
    public static int getDayOfMonth() {
        return (int) (totalDays() % DAYS_PER_MONTH) + 1;
    }

    /**
     * Returns the current month index (0–11), where {@code 0} is Early Spring.
     */
    public static int getMonthIndex() {
        return (int) ((totalDays() % DAYS_PER_YEAR) / DAYS_PER_MONTH);
    }

    /**
     * Returns the display name of the current SkyBlock month.
     */
    public static String getMonthName() {
        return MONTH_NAMES[getMonthIndex()];
    }

    /**
     * Returns the current SkyBlock year (1-based).
     */
    public static long getYear() {
        return totalDays() / DAYS_PER_YEAR + 1;
    }

    /**
     * Returns the number of ticks elapsed within the current SkyBlock day
     * (0–23 999), derived from wall-clock milliseconds.
     */
    public static long getTickOfDay() {
        long msIntoDay = System.currentTimeMillis() % MS_PER_DAY;
        return msIntoDay * TICKS_PER_DAY / MS_PER_DAY;
    }

    /**
     * Formats the current date as {@code "<Month> <day>"}, e.g.
     * {@code "Early Spring 1st"}.
     */
    public static String formatDate() {
        return getMonthName() + " " + ordinal(getDayOfMonth());
    }

    /** Returns the ordinal string for a day number (e.g. {@code 1} → {@code "1st"}). */
    public static String ordinal(int day) {
        if (day >= 11 && day <= 13) {
            return day + "th";
        }
        switch (day % 10) {
            case 1: return day + "st";
            case 2: return day + "nd";
            case 3: return day + "rd";
            default: return day + "th";
        }
    }
}
