package com.skyblock.core.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton tracking per-player Bingo event progress.
 *
 * <p>Bingo runs as a monthly event whose card is a set of named goals. Goals come
 * in two flavours: {@link GoalType#REGULAR} goals each player completes on their
 * own, and {@link GoalType#COMMUNITY} goals every participant contributes toward
 * collectively. Completing a goal awards a single Bingo point, and a player's
 * accumulated lifetime points determine their Bingo rank. Goal definitions are
 * registered per month so that completions can only be recorded against goals
 * that actually belong to that month's card. Not thread-safe.</p>
 */
public final class BingoManager {

    /** The two kinds of goal that appear on a Bingo card. */
    public enum GoalType {
        REGULAR, COMMUNITY
    }

    /** Immutable definition of a single Bingo goal. */
    public static final class BingoGoal {
        public final String id;
        public final GoalType type;
        public final String name;

        public BingoGoal(String id, GoalType type, String name) {
            this.id = Objects.requireNonNull(id, "id");
            this.type = Objects.requireNonNull(type, "type");
            this.name = Objects.requireNonNull(name, "name");
        }
    }

    /** Immutable snapshot of a player's progress on one month's Bingo card. */
    public static final class BingoCardProgress {
        public final String monthId;
        public final Set<String> completedRegular;
        public final Set<String> completedCommunity;
        public final int bingoPoints;

        public BingoCardProgress(String monthId, Set<String> completedRegular,
                                 Set<String> completedCommunity, int bingoPoints) {
            this.monthId = monthId;
            this.completedRegular = Set.copyOf(completedRegular);
            this.completedCommunity = Set.copyOf(completedCommunity);
            this.bingoPoints = bingoPoints;
        }
    }

    /**
     * Lifetime Bingo-point thresholds at which each rank is reached. A player at
     * {@code points} holds the rank of the highest threshold they meet or exceed,
     * so rank is {@code 1}-based with rank {@code 1} starting at {@code 0} points.
     */
    static final int[] RANK_THRESHOLDS = {0, 10, 25, 50, 100, 200, 350, 500};

    private static final BingoManager INSTANCE = new BingoManager();

    /** Goal definitions, keyed by month then goal id. */
    private final Map<String, Map<String, BingoGoal>> cards = new HashMap<>();
    /** Per-player completed goal ids, keyed by player then month. */
    private final Map<UUID, Map<String, Set<String>>> completed = new HashMap<>();

    private BingoManager() {}

    /**
     * Returns the single shared {@code BingoManager} instance.
     *
     * @return the singleton instance
     */
    public static BingoManager getInstance() {
        return INSTANCE;
    }

    /**
     * Registers a goal on the given month's Bingo card. Registering a goal whose
     * id already exists for that month replaces the previous definition.
     *
     * @param monthId the month the card belongs to
     * @param goal    the goal definition to add
     */
    public void addGoal(String monthId, BingoGoal goal) {
        Objects.requireNonNull(monthId, "monthId");
        Objects.requireNonNull(goal, "goal");
        cards.computeIfAbsent(monthId, k -> new HashMap<>()).put(goal.id, goal);
    }

    /**
     * Returns the goal with the given id on the given month's card.
     *
     * @param monthId the month to look up
     * @param goalId  the goal id to look up
     * @return the goal definition, or {@code null} if no such goal exists
     */
    public BingoGoal getGoal(String monthId, String goalId) {
        Objects.requireNonNull(monthId, "monthId");
        Objects.requireNonNull(goalId, "goalId");
        Map<String, BingoGoal> card = cards.get(monthId);
        return card == null ? null : card.get(goalId);
    }

    /**
     * Records that a player completed a goal on the given month's card. A goal can
     * only be completed if it has been registered for that month.
     *
     * @param playerId the player completing the goal
     * @param monthId  the month the goal belongs to
     * @param goalId   the goal id being completed
     * @return {@code true} if the goal was newly completed; {@code false} if it was
     *         already complete
     * @throws IllegalArgumentException if no such goal exists for that month
     */
    public boolean completeGoal(UUID playerId, String monthId, String goalId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(monthId, "monthId");
        Objects.requireNonNull(goalId, "goalId");
        if (getGoal(monthId, goalId) == null) {
            throw new IllegalArgumentException(
                    "no goal '" + goalId + "' on card for month " + monthId);
        }
        return completed
                .computeIfAbsent(playerId, k -> new HashMap<>())
                .computeIfAbsent(monthId, k -> new HashSet<>())
                .add(goalId);
    }

    /**
     * Returns whether the player has completed the given goal.
     *
     * @param playerId the player to look up
     * @param monthId  the month the goal belongs to
     * @param goalId   the goal id to check
     * @return {@code true} if the player has completed that goal
     */
    public boolean isGoalCompleted(UUID playerId, String monthId, String goalId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(monthId, "monthId");
        Objects.requireNonNull(goalId, "goalId");
        return completedIds(playerId, monthId).contains(goalId);
    }

    /**
     * Returns how many goals of the given type the player has completed this month.
     *
     * @param playerId the player to look up
     * @param monthId  the month to look up
     * @param type     the goal type to count
     * @return the number of completed goals of that type
     */
    public int getCompletedGoalCount(UUID playerId, String monthId, GoalType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(monthId, "monthId");
        Objects.requireNonNull(type, "type");
        Map<String, BingoGoal> card = cards.get(monthId);
        if (card == null) {
            return 0;
        }
        int count = 0;
        for (String goalId : completedIds(playerId, monthId)) {
            BingoGoal goal = card.get(goalId);
            if (goal != null && goal.type == type) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the Bingo points the player has earned on the given month's card.
     * Each completed goal, regular or community, is worth one point.
     *
     * @param playerId the player to look up
     * @param monthId  the month to look up
     * @return the player's Bingo points for that month
     */
    public int getMonthlyBingoPoints(UUID playerId, String monthId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(monthId, "monthId");
        Map<String, BingoGoal> card = cards.get(monthId);
        if (card == null) {
            return 0;
        }
        int points = 0;
        for (String goalId : completedIds(playerId, monthId)) {
            if (card.containsKey(goalId)) {
                points++;
            }
        }
        return points;
    }

    /**
     * Returns the player's lifetime Bingo points, summed across every month's card.
     *
     * @param playerId the player to look up
     * @return the player's total Bingo points
     */
    public int getTotalBingoPoints(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<String, Set<String>> byMonth = completed.get(playerId);
        if (byMonth == null) {
            return 0;
        }
        int total = 0;
        for (String monthId : byMonth.keySet()) {
            total += getMonthlyBingoPoints(playerId, monthId);
        }
        return total;
    }

    /**
     * Returns the player's Bingo rank, derived from their lifetime Bingo points.
     * Rank is {@code 1}-based: a player with no points holds rank {@code 1}, and
     * the rank climbs as points cross the {@link #RANK_THRESHOLDS}.
     *
     * @param playerId the player to look up
     * @return the player's Bingo rank, at least {@code 1}
     */
    public int getBingoRank(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int points = getTotalBingoPoints(playerId);
        int rank = 1;
        for (int i = 0; i < RANK_THRESHOLDS.length; i++) {
            if (points >= RANK_THRESHOLDS[i]) {
                rank = i + 1;
            } else {
                break;
            }
        }
        return rank;
    }

    /**
     * Returns a snapshot of the player's progress on the given month's card.
     *
     * @param playerId the player to look up
     * @param monthId  the month to snapshot
     * @return a {@link BingoCardProgress} snapshot (never {@code null})
     */
    public BingoCardProgress getCardProgress(UUID playerId, String monthId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(monthId, "monthId");
        Map<String, BingoGoal> card = cards.get(monthId);
        Set<String> regular = new HashSet<>();
        Set<String> community = new HashSet<>();
        if (card != null) {
            for (String goalId : completedIds(playerId, monthId)) {
                BingoGoal goal = card.get(goalId);
                if (goal == null) {
                    continue;
                }
                if (goal.type == GoalType.COMMUNITY) {
                    community.add(goalId);
                } else {
                    regular.add(goalId);
                }
            }
        }
        return new BingoCardProgress(monthId, regular, community,
                regular.size() + community.size());
    }

    /**
     * Clears all Bingo completions for the given player across every month. Goal
     * definitions are unaffected.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had any completions
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return completed.remove(playerId) != null;
    }

    /** Returns the player's completed-goal ids for a month, never {@code null}. */
    private Set<String> completedIds(UUID playerId, String monthId) {
        Map<String, Set<String>> byMonth = completed.get(playerId);
        if (byMonth == null) {
            return Set.of();
        }
        Set<String> ids = byMonth.get(monthId);
        return ids == null ? Set.of() : ids;
    }
}
