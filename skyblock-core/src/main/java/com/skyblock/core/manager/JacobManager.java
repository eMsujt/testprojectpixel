package com.skyblock.core.manager;

import com.skyblock.core.manager.GardenManager.ContestMedal;
import com.skyblock.core.manager.GardenManager.ContestRegistration;
import com.skyblock.core.manager.GardenManager.GardenCrop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton coordinator for Jacob's Farming Contests.
 *
 * <p>This manager owns no state of its own: the contest <em>schedule</em> is
 * owned by {@link CalendarManager} and each player's contest <em>results</em>
 * (medals, personal bests, registration) are owned by {@link GardenManager}.
 * {@code JacobManager} simply joins the two so callers have a single, Jacob-
 * focused entry point.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class JacobManager {

    private static final JacobManager INSTANCE = new JacobManager();

    private JacobManager() {
    }

    /**
     * Returns the single shared {@code JacobManager} instance.
     *
     * @return the singleton instance
     */
    public static JacobManager getInstance() {
        return INSTANCE;
    }

    /**
     * A Jacob's Farming Contest scheduled on a particular year-day, together with
     * the crops it features.
     */
    public static final class UpcomingContest {
        private final int day;
        private final List<GardenCrop> crops;

        public UpcomingContest(int day, List<GardenCrop> crops) {
            this.day = day;
            this.crops = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(crops, "crops")));
        }

        /** @return the contest's year-day (1–{@value CalendarManager#DAYS_PER_YEAR}) */
        public int getDay() {
            return day;
        }

        /** @return an immutable list of the crops featured in this contest */
        public List<GardenCrop> getCrops() {
            return crops;
        }
    }

    // -------------------------------------------------------------------------
    // Schedule
    // -------------------------------------------------------------------------

    /**
     * Returns whether a Jacob's Farming Contest is running on the current calendar day.
     *
     * @return {@code true} if a contest is active today
     */
    public boolean isContestActive() {
        return CalendarManager.getInstance().isContestToday();
    }

    /**
     * Returns the crops featured in today's Jacob's Farming Contest.
     *
     * @return an immutable list of today's contest crops, empty if no contest is scheduled today
     */
    public List<GardenCrop> getActiveContestCrops() {
        return CalendarManager.getInstance().getGardenCropsToday();
    }

    /**
     * Returns the next {@code count} Jacob's Farming Contests, starting from the
     * next contest on or after the current calendar day.
     *
     * @param count the number of upcoming contests to return (clamped to {@code >= 0})
     * @return an immutable list of upcoming contests, in chronological order
     */
    public List<UpcomingContest> getUpcomingContests(int count) {
        List<UpcomingContest> contests = new ArrayList<>();
        if (count <= 0) {
            return Collections.unmodifiableList(contests);
        }
        int day = CalendarManager.nextContestDay(CalendarManager.getInstance().getCurrentDay());
        for (int i = 0; i < count; i++) {
            contests.add(new UpcomingContest(day, CalendarManager.getGardenCrops(day)));
            int afterDay = day % CalendarManager.DAYS_PER_YEAR + 1;
            day = CalendarManager.nextContestDay(afterDay);
        }
        return Collections.unmodifiableList(contests);
    }

    // -------------------------------------------------------------------------
    // Player results
    // -------------------------------------------------------------------------

    /**
     * Returns how many of the given medal the player has earned across all contests.
     *
     * @param playerId the player to look up
     * @param medal    the medal tier
     * @return the medal count, {@code 0} if none
     */
    public int getMedalCount(UUID playerId, ContestMedal medal) {
        return GardenManager.getInstance().getContestMedalCount(playerId, medal);
    }

    /**
     * Returns the total number of medals the player has earned across all tiers.
     *
     * @param playerId the player to look up
     * @return the total medal count, {@code 0} if none
     */
    public int getTotalMedals(UUID playerId) {
        GardenManager garden = GardenManager.getInstance();
        int total = 0;
        for (ContestMedal medal : ContestMedal.values()) {
            if (medal != ContestMedal.NONE) {
                total += garden.getContestMedalCount(playerId, medal);
            }
        }
        return total;
    }

    /**
     * Returns the number of Jacob's Farming Contests the player has participated in.
     *
     * @param playerId the player to look up
     * @return the contest count, {@code 0} if none
     */
    public int getContestsParticipated(UUID playerId) {
        return GardenManager.getInstance().getContestsParticipated(playerId);
    }

    /**
     * Returns the player's best contest collection for the given crop.
     *
     * @param playerId the player to look up
     * @param crop     the contest crop
     * @return the highest collection recorded, {@code 0} if never contested
     */
    public long getBestCollection(UUID playerId, GardenCrop crop) {
        return GardenManager.getInstance().getBestContestCollection(playerId, crop);
    }

    // -------------------------------------------------------------------------
    // Registration
    // -------------------------------------------------------------------------

    /**
     * Registers the player for the contest held on the given year-day, signing
     * them up to farm the chosen crop.
     *
     * @param playerId   the player registering
     * @param contestDay the contest's year-day
     * @param crop       the crop the player wishes to farm
     * @return the resulting {@link ContestRegistration}
     * @throws IllegalArgumentException if no contest is held that day or the crop is not featured
     */
    public ContestRegistration register(UUID playerId, int contestDay, GardenCrop crop) {
        return GardenManager.getInstance().registerForContest(playerId, contestDay, crop);
    }

    /**
     * Returns the player's active contest registration.
     *
     * @param playerId the player to look up
     * @return the registration, or {@code null} if the player is not registered
     */
    public ContestRegistration getRegistration(UUID playerId) {
        return GardenManager.getInstance().getContestRegistration(playerId);
    }

    /**
     * Returns whether the player is currently registered for a contest.
     *
     * @param playerId the player to look up
     * @return {@code true} if registered
     */
    public boolean isRegistered(UUID playerId) {
        return GardenManager.getInstance().isRegisteredForContest(playerId);
    }

    /**
     * Finalizes the player's registered contest, scoring their collection and
     * clearing the registration.
     *
     * @param playerId  the player submitting their result
     * @param collected the number of crops collected during the contest
     * @return the {@link ContestMedal} earned this contest
     * @throws IllegalStateException if the player is not registered for a contest
     */
    public ContestMedal submit(UUID playerId, long collected) {
        return GardenManager.getInstance().submitContest(playerId, collected);
    }
}
