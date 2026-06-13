package com.skyblock.core.mayor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking the active mayor and each player's mayor vote.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class MayorManager {

    /** Skyblock mayors that players can vote for. */
    public enum Mayor {
        JERRY("Jerry"),
        DIANA("Diana"),
        PAUL("Paul"),
        FINNEGAN("Finnegan"),
        BARRY("Barry"),
        MARINA("Marina"),
        SCORPIUS("Scorpius"),
        FOXY("Foxy"),
        COLE("Cole"),
        AATROX("Aatrox"),
        DIAZ("Diaz");

        /** Human-readable display name shown to players. */
        public final String displayName;

        Mayor(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private static final MayorManager INSTANCE = new MayorManager();

    /** The currently active mayor (null if none set). */
    private Mayor currentMayor;

    /** Per-player vote, keyed by player UUID. */
    private final Map<UUID, Mayor> playerVotes = new HashMap<>();

    private MayorManager() {
    }

    /**
     * Returns the single shared {@code MayorManager} instance.
     *
     * @return the singleton instance
     */
    public static MayorManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Current mayor
    // -------------------------------------------------------------------------

    /**
     * Returns the currently active mayor, or {@code null} if none is set.
     *
     * @return the active mayor
     */
    public Mayor getCurrentMayor() {
        return currentMayor;
    }

    /**
     * Sets the currently active mayor.
     *
     * @param mayor the mayor to set (may be {@code null} to clear)
     */
    public void setCurrentMayor(Mayor mayor) {
        this.currentMayor = mayor;
    }

    // -------------------------------------------------------------------------
    // Player votes
    // -------------------------------------------------------------------------

    /**
     * Records a player's vote for the given mayor.
     *
     * @param playerId the player casting the vote
     * @param mayor    the mayor being voted for
     */
    public void vote(UUID playerId, Mayor mayor) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(mayor, "mayor");
        playerVotes.put(playerId, mayor);
    }

    /**
     * Returns the mayor the player voted for, or {@code null} if they have not voted.
     *
     * @param playerId the player to look up
     * @return the voted-for mayor, or {@code null}
     */
    public Mayor getVote(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerVotes.get(playerId);
    }

    /**
     * Clears the player's vote.
     *
     * @param playerId the player whose vote to clear
     * @return {@code true} if the player had voted, {@code false} otherwise
     */
    public boolean clearVote(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerVotes.remove(playerId) != null;
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /**
     * Removes all data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerVotes.remove(playerId) != null;
    }
}
