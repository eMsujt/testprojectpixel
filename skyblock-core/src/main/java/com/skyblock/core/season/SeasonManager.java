package com.skyblock.core.season;

import java.util.Objects;

/**
 * Singleton tracking the current SkyBlock season and providing season-cycle utilities.
 *
 * <p>Seasons cycle through the twelve SkyBlock periods in order. Not thread-safe;
 * synchronize externally if accessed from multiple threads.</p>
 */
public final class SeasonManager {

    /** All twelve SkyBlock seasons in cycle order. */
    public enum Season {
        EARLY_SPRING,
        SPRING,
        LATE_SPRING,
        EARLY_SUMMER,
        SUMMER,
        LATE_SUMMER,
        EARLY_AUTUMN,
        AUTUMN,
        LATE_AUTUMN,
        EARLY_WINTER,
        WINTER,
        LATE_WINTER;

        /** Returns the season that follows this one (wraps around). */
        public Season next() {
            Season[] values = values();
            return values[(ordinal() + 1) % values.length];
        }

        /** Returns the season that precedes this one (wraps around). */
        public Season previous() {
            Season[] values = values();
            return values[(ordinal() + values.length - 1) % values.length];
        }

        /** Returns a human-readable display name (e.g. {@code "Early Spring"}). */
        public String displayName() {
            String lower = name().replace('_', ' ').toLowerCase();
            StringBuilder sb = new StringBuilder();
            for (String word : lower.split(" ")) {
                if (!sb.isEmpty()) sb.append(' ');
                sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
            }
            return sb.toString();
        }
    }

    private static final SeasonManager INSTANCE = new SeasonManager();

    private Season currentSeason = Season.EARLY_SPRING;
    private int day = 1;

    private SeasonManager() {
    }

    /**
     * Returns the single shared {@code SeasonManager} instance.
     *
     * @return the singleton instance
     */
    public static SeasonManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the current season.
     *
     * @return the current {@link Season}
     */
    public Season getCurrentSeason() {
        return currentSeason;
    }

    /**
     * Sets the current season directly.
     *
     * @param season the season to set (must not be {@code null})
     */
    public void setCurrentSeason(Season season) {
        Objects.requireNonNull(season, "season");
        this.currentSeason = season;
    }

    /**
     * Returns the current in-game day within this season (1-based).
     *
     * @return the current day
     */
    public int getDay() {
        return day;
    }

    /**
     * Sets the current in-game day within this season.
     *
     * @param day the day to set (must be positive)
     * @throws IllegalArgumentException if {@code day} is not positive
     */
    public void setDay(int day) {
        if (day <= 0) {
            throw new IllegalArgumentException("day must be positive");
        }
        this.day = day;
    }

    /**
     * Advances the season to the next one in the cycle and resets the day to 1.
     *
     * @return the new current {@link Season}
     */
    public Season advanceSeason() {
        currentSeason = currentSeason.next();
        day = 1;
        return currentSeason;
    }

    /**
     * Returns the season that follows the current one without mutating state.
     *
     * @return the next {@link Season}
     */
    public Season getNextSeason() {
        return currentSeason.next();
    }
}
