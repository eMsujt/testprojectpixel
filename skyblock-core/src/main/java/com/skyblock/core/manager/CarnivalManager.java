package com.skyblock.core.manager;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking per-player Carnival hub state.
 *
 * <p>The Carnival is a limited-time hub event whose mini-games (Fruit Digging,
 * Bombs, and Zombie Shootout) are paid for with Carnival tickets and reward
 * Carnival tokens. This manager tracks each player's ticket balance, token
 * balance, per-game best score, and how many times they have played each game.
 * Not thread-safe.</p>
 */
public final class CarnivalManager {

    /** The Carnival mini-games. */
    public enum CarnivalGame {
        FRUIT_DIGGING, BOMBS, ZOMBIE_SHOOTOUT
    }

    /** Immutable snapshot of a player's current Carnival state. */
    public static final class CarnivalData {
        public final long tickets;
        public final long tokens;
        public final Map<CarnivalGame, Integer> bestScores;
        public final Map<CarnivalGame, Integer> timesPlayed;

        public CarnivalData(long tickets, long tokens,
                            Map<CarnivalGame, Integer> bestScores,
                            Map<CarnivalGame, Integer> timesPlayed) {
            this.tickets = tickets;
            this.tokens = tokens;
            this.bestScores = Map.copyOf(bestScores);
            this.timesPlayed = Map.copyOf(timesPlayed);
        }
    }

    /** Tickets required to play a single mini-game. */
    public static final int TICKET_COST_PER_GAME = 1;

    private static final CarnivalManager INSTANCE = new CarnivalManager();

    private final Map<UUID, Long> tickets = new HashMap<>();
    private final Map<UUID, Long> tokens = new HashMap<>();
    private final Map<UUID, Map<CarnivalGame, Integer>> bestScores = new HashMap<>();
    private final Map<UUID, Map<CarnivalGame, Integer>> timesPlayed = new HashMap<>();

    private CarnivalManager() {}

    /**
     * Returns the single shared {@code CarnivalManager} instance.
     *
     * @return the singleton instance
     */
    public static CarnivalManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the player's Carnival ticket balance.
     *
     * @param playerId the player to look up
     * @return the ticket balance, or {@code 0} if the player has none
     */
    public long getTickets(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return tickets.getOrDefault(playerId, 0L);
    }

    /**
     * Credits Carnival tickets to a player.
     *
     * @param playerId the player to credit
     * @param amount   the number of tickets to add (must be non-negative)
     * @return the player's ticket balance after the credit
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public long addTickets(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative");
        }
        return tickets.merge(playerId, amount, Long::sum);
    }

    /**
     * Returns the player's Carnival token balance.
     *
     * @param playerId the player to look up
     * @return the token balance, or {@code 0} if the player has none
     */
    public long getTokens(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return tokens.getOrDefault(playerId, 0L);
    }

    /**
     * Returns the number of times the player has played the given game.
     *
     * @param playerId the player to look up
     * @param game     the mini-game
     * @return the play count, {@code 0} if never played
     */
    public int getTimesPlayed(UUID playerId, CarnivalGame game) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(game, "game");
        Map<CarnivalGame, Integer> plays = timesPlayed.get(playerId);
        return plays == null ? 0 : plays.getOrDefault(game, 0);
    }

    /**
     * Returns the player's best recorded score for the given game.
     *
     * @param playerId the player to look up
     * @param game     the mini-game
     * @return the best score, {@code 0} if never played
     */
    public int getBestScore(UUID playerId, CarnivalGame game) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(game, "game");
        Map<CarnivalGame, Integer> scores = bestScores.get(playerId);
        return scores == null ? 0 : scores.getOrDefault(game, 0);
    }

    /**
     * Plays one round of a Carnival mini-game: charges the player
     * {@link #TICKET_COST_PER_GAME} tickets, records the play, updates the best
     * score if the new score is higher, and rewards {@code tokenReward} tokens.
     *
     * @param playerId    the player playing
     * @param game        the mini-game played
     * @param score       the score achieved this round (must be non-negative)
     * @param tokenReward the tokens earned this round (must be non-negative)
     * @return {@code true} if the player could afford the game and it was played
     * @throws IllegalArgumentException if {@code score} or {@code tokenReward} is negative
     */
    public boolean playGame(UUID playerId, CarnivalGame game, int score, long tokenReward) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(game, "game");
        if (score < 0) {
            throw new IllegalArgumentException("score must be non-negative");
        }
        if (tokenReward < 0) {
            throw new IllegalArgumentException("tokenReward must be non-negative");
        }
        long balance = getTickets(playerId);
        if (balance < TICKET_COST_PER_GAME) {
            return false;
        }
        tickets.put(playerId, balance - TICKET_COST_PER_GAME);
        timesPlayed.computeIfAbsent(playerId, id -> new EnumMap<>(CarnivalGame.class))
                .merge(game, 1, Integer::sum);
        bestScores.computeIfAbsent(playerId, id -> new EnumMap<>(CarnivalGame.class))
                .merge(game, score, Math::max);
        if (tokenReward > 0) {
            tokens.merge(playerId, tokenReward, Long::sum);
        }
        return true;
    }

    /**
     * Deducts Carnival tokens from a player if they can afford it.
     *
     * @param playerId the player to charge
     * @param amount   the number of tokens to spend (must be non-negative)
     * @return {@code true} if the player had enough tokens and was charged
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public boolean spendTokens(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative");
        }
        long balance = getTokens(playerId);
        if (balance < amount) {
            return false;
        }
        tokens.put(playerId, balance - amount);
        return true;
    }

    /**
     * Returns a snapshot of the player's current Carnival state.
     *
     * @param playerId the player to look up
     * @return a {@link CarnivalData} snapshot (never {@code null})
     */
    public CarnivalData getCarnivalData(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<CarnivalGame, Integer> scores = bestScores.getOrDefault(
                playerId, new EnumMap<>(CarnivalGame.class));
        Map<CarnivalGame, Integer> plays = timesPlayed.getOrDefault(
                playerId, new EnumMap<>(CarnivalGame.class));
        return new CarnivalData(getTickets(playerId), getTokens(playerId), scores, plays);
    }

    /**
     * Resets all Carnival data for the given player.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had any Carnival data
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadData = tickets.remove(playerId) != null;
        hadData |= tokens.remove(playerId) != null;
        hadData |= bestScores.remove(playerId) != null;
        hadData |= timesPlayed.remove(playerId) != null;
        return hadData;
    }
}
