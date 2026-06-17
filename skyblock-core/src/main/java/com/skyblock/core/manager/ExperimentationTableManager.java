package com.skyblock.core.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Canonical singleton managing the Experimentation Table minigames:
 * Superpairs, Chronomatron and Ultrasequencer.
 *
 * <p>Each player may have at most one active game session at a time. Completing a
 * game awards enchanting experience based on the game tier. Game logic is driven
 * by explicit player inputs (no timers or randomness), so callers supply the board
 * layout / sequence when starting a game.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class ExperimentationTableManager {

    /** The three Experimentation Table minigames. */
    public enum GameType {
        SUPERPAIRS("Superpairs"),
        CHRONOMATRON("Chronomatron"),
        ULTRASEQUENCER("Ultrasequencer");

        private final String displayName;

        GameType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    /**
     * Difficulty tier for an experiment. Higher tiers use larger boards / longer
     * sequences and grant proportionally more enchanting experience on completion.
     */
    public enum GameTier {
        BEGINNER("Beginner", 600),
        HIGH("High", 1200),
        GRAND("Grand", 2400),
        SUPREME("Supreme", 4800),
        TRANSCENDENT("Transcendent", 9600);

        private final String displayName;
        private final int enchantingXpReward;

        GameTier(String displayName, int enchantingXpReward) {
            this.displayName = displayName;
            this.enchantingXpReward = enchantingXpReward;
        }

        public String getDisplayName() { return displayName; }

        /** Base enchanting experience awarded for completing this tier. */
        public int getEnchantingXpReward() { return enchantingXpReward; }
    }

    /** Outcome of a single move in any of the minigames. */
    public enum MoveResult {
        /** The move was valid but did not finish the game. */
        CONTINUE,
        /** The two flipped Superpairs cards matched. */
        MATCH,
        /** The two flipped Superpairs cards did not match (board resets the pair). */
        NO_MATCH,
        /** The move was wrong; the game is over and lost. */
        FAIL,
        /** The move was valid and completed the game; rewards are now claimable. */
        WIN
    }

    /**
     * An in-progress game for a single player. Subclasses encode the rules of each
     * {@link GameType}. A session is finished once {@link #isFinished()} returns true,
     * at which point {@link #isWon()} reports the outcome.
     */
    public abstract static class GameSession {
        private final GameType type;
        private final GameTier tier;
        private boolean finished;
        private boolean won;

        private GameSession(GameType type, GameTier tier) {
            this.type = Objects.requireNonNull(type, "type");
            this.tier = Objects.requireNonNull(tier, "tier");
        }

        public GameType getType() { return type; }
        public GameTier getTier() { return tier; }
        public boolean isFinished() { return finished; }
        public boolean isWon() { return won; }

        void win() { finished = true; won = true; }
        void lose() { finished = true; won = false; }

        void checkLive() {
            if (finished) {
                throw new IllegalStateException("Game already finished");
            }
        }
    }

    /**
     * Superpairs: a grid of face-down cards, each value present twice. The player
     * flips cards two at a time; matched pairs stay revealed. The game is won once
     * every pair is found.
     */
    public static final class SuperpairsSession extends GameSession {
        private final List<Integer> cards;
        private final Set<Integer> matched = new HashSet<>();
        private int firstFlipped = -1;
        private int clicks;

        private SuperpairsSession(GameTier tier, List<Integer> cards) {
            super(GameType.SUPERPAIRS, tier);
            this.cards = cards;
        }

        /** Number of card flips performed so far. */
        public int getClicks() { return clicks; }

        /** Number of pairs matched so far. */
        public int getMatchedPairs() { return matched.size() / 2; }

        /**
         * Flips the card at {@code index}.
         *
         * @return {@link MoveResult#MATCH} or {@link MoveResult#NO_MATCH} once a second
         *         card is flipped, {@link MoveResult#CONTINUE} after the first of a pair,
         *         or {@link MoveResult#WIN} when the final pair is matched
         * @throws IndexOutOfBoundsException if {@code index} is outside the board
         * @throws IllegalArgumentException  if the card is already matched or re-flipped
         */
        public MoveResult flip(int index) {
            checkLive();
            if (index < 0 || index >= cards.size()) {
                throw new IndexOutOfBoundsException("No card at index " + index);
            }
            if (matched.contains(index)) {
                throw new IllegalArgumentException("Card " + index + " already matched");
            }
            if (index == firstFlipped) {
                throw new IllegalArgumentException("Card " + index + " already flipped");
            }
            clicks++;
            if (firstFlipped < 0) {
                firstFlipped = index;
                return MoveResult.CONTINUE;
            }
            int first = firstFlipped;
            firstFlipped = -1;
            if (cards.get(first).equals(cards.get(index))) {
                matched.add(first);
                matched.add(index);
                if (matched.size() == cards.size()) {
                    win();
                    return MoveResult.WIN;
                }
                return MoveResult.MATCH;
            }
            return MoveResult.NO_MATCH;
        }
    }

    /**
     * Chronomatron: the table shows a colour sequence that grows by one each round.
     * Each round the player must reproduce the whole sequence from the start. The
     * game is won after the final round's sequence is entered correctly; a wrong
     * input ends the game.
     */
    public static final class ChronomatronSession extends GameSession {
        private final List<Integer> sequence;
        private int round = 1;
        private int inputIndex;

        private ChronomatronSession(GameTier tier, List<Integer> sequence) {
            super(GameType.CHRONOMATRON, tier);
            this.sequence = sequence;
        }

        /** The current round number (1-based); equals how many symbols must be repeated. */
        public int getRound() { return round; }

        /**
         * Submits the next symbol in the player's reproduction of the current round.
         *
         * @return {@link MoveResult#CONTINUE} while the round is incomplete,
         *         {@link MoveResult#FAIL} on a wrong symbol, or {@link MoveResult#WIN}
         *         when the final round is completed
         */
        public MoveResult submit(int symbol) {
            checkLive();
            if (sequence.get(inputIndex) != symbol) {
                lose();
                return MoveResult.FAIL;
            }
            inputIndex++;
            if (inputIndex == round) {
                if (round == sequence.size()) {
                    win();
                    return MoveResult.WIN;
                }
                round++;
                inputIndex = 0;
            }
            return MoveResult.CONTINUE;
        }
    }

    /**
     * Ultrasequencer: numbered slots are revealed briefly, then the player must click
     * them in ascending numeric order (1, 2, 3, …). A wrong slot ends the game; the
     * game is won once the highest number is clicked.
     */
    public static final class UltrasequencerSession extends GameSession {
        private final int length;
        private int next = 1;

        private UltrasequencerSession(GameTier tier, int length) {
            super(GameType.ULTRASEQUENCER, tier);
            this.length = length;
        }

        /** The next number the player must click (1-based). */
        public int getNext() { return next; }

        /**
         * Clicks the slot the player believes holds {@code number}.
         *
         * @return {@link MoveResult#CONTINUE} on a correct non-final click,
         *         {@link MoveResult#FAIL} on a wrong number, or {@link MoveResult#WIN}
         *         when the last number is clicked
         */
        public MoveResult click(int number) {
            checkLive();
            if (number != next) {
                lose();
                return MoveResult.FAIL;
            }
            if (next == length) {
                win();
                return MoveResult.WIN;
            }
            next++;
            return MoveResult.CONTINUE;
        }
    }

    private static final ExperimentationTableManager INSTANCE = new ExperimentationTableManager();

    /** Per-player active game session; absent when the player is not playing. */
    private final Map<UUID, GameSession> sessions = new HashMap<>();

    /** Per-player count of completed (won) games, by game type. */
    private final Map<UUID, Map<GameType, Integer>> completions = new HashMap<>();

    private ExperimentationTableManager() {}

    public static ExperimentationTableManager getInstance() {
        return INSTANCE;
    }

    /**
     * Starts a Superpairs game for the player.
     *
     * @param cards the board layout; must be non-empty, of even length, and contain
     *              each value exactly twice
     * @throws IllegalStateException    if the player already has an active game
     * @throws IllegalArgumentException if the layout is not a valid set of pairs
     */
    public SuperpairsSession startSuperpairs(UUID playerId, GameTier tier, List<Integer> cards) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        Objects.requireNonNull(cards, "cards");
        if (cards.isEmpty() || cards.size() % 2 != 0) {
            throw new IllegalArgumentException("cards must be non-empty and of even length");
        }
        Map<Integer, Integer> counts = new HashMap<>();
        for (Integer card : cards) {
            Objects.requireNonNull(card, "card value");
            counts.merge(card, 1, Integer::sum);
        }
        for (Map.Entry<Integer, Integer> e : counts.entrySet()) {
            if (e.getValue() != 2) {
                throw new IllegalArgumentException("Card value " + e.getKey() + " must appear exactly twice");
            }
        }
        requireNoActiveGame(playerId);
        SuperpairsSession session = new SuperpairsSession(tier, new ArrayList<>(cards));
        sessions.put(playerId, session);
        return session;
    }

    /**
     * Starts a Chronomatron game for the player.
     *
     * @param sequence the full colour sequence to reproduce round by round; non-empty
     * @throws IllegalStateException    if the player already has an active game
     * @throws IllegalArgumentException if the sequence is empty
     */
    public ChronomatronSession startChronomatron(UUID playerId, GameTier tier, List<Integer> sequence) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        Objects.requireNonNull(sequence, "sequence");
        if (sequence.isEmpty()) {
            throw new IllegalArgumentException("sequence must be non-empty");
        }
        for (Integer symbol : sequence) {
            Objects.requireNonNull(symbol, "sequence symbol");
        }
        requireNoActiveGame(playerId);
        ChronomatronSession session = new ChronomatronSession(tier, new ArrayList<>(sequence));
        sessions.put(playerId, session);
        return session;
    }

    /**
     * Starts an Ultrasequencer game for the player.
     *
     * @param length the count of numbered slots (1..length); must be positive
     * @throws IllegalStateException    if the player already has an active game
     * @throws IllegalArgumentException if {@code length} is not positive
     */
    public UltrasequencerSession startUltrasequencer(UUID playerId, GameTier tier, int length) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        if (length < 1) {
            throw new IllegalArgumentException("length must be positive");
        }
        requireNoActiveGame(playerId);
        UltrasequencerSession session = new UltrasequencerSession(tier, length);
        sessions.put(playerId, session);
        return session;
    }

    private void requireNoActiveGame(UUID playerId) {
        GameSession active = sessions.get(playerId);
        if (active != null && !active.isFinished()) {
            throw new IllegalStateException("Player already has an active game");
        }
    }

    /**
     * Returns the player's current game session, or {@code null} if none is active.
     */
    public GameSession getSession(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return sessions.get(playerId);
    }

    /**
     * Ends the player's active game without awarding rewards.
     *
     * @return {@code true} if a session was removed, {@code false} otherwise
     */
    public boolean abandon(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return sessions.remove(playerId) != null;
    }

    /**
     * Claims the player's finished game, awarding enchanting experience if it was won
     * and clearing the session.
     *
     * @return the enchanting experience awarded ({@code 0} if the game was lost)
     * @throws IllegalStateException if there is no finished game to claim
     */
    public int claim(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        GameSession session = sessions.get(playerId);
        if (session == null || !session.isFinished()) {
            throw new IllegalStateException("No finished game to claim");
        }
        sessions.remove(playerId);
        if (!session.isWon()) {
            return 0;
        }
        completions.computeIfAbsent(playerId, id -> new EnumMap<>(GameType.class))
                .merge(session.getType(), 1, Integer::sum);
        return session.getTier().getEnchantingXpReward();
    }

    /**
     * Returns how many games of the given type the player has won and claimed.
     */
    public int getCompletions(UUID playerId, GameType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<GameType, Integer> byType = completions.get(playerId);
        return byType == null ? 0 : byType.getOrDefault(type, 0);
    }

    /**
     * Returns an unmodifiable view of the player's claimed wins by game type.
     */
    public Map<GameType, Integer> getAllCompletions(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<GameType, Integer> byType = completions.get(playerId);
        return byType == null ? Collections.emptyMap() : Collections.unmodifiableMap(byType);
    }
}
