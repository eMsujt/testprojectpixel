package com.skyblock.slayers;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Tracks the active slayer quest session for each player.
 *
 * <p>A player may have at most one session at a time. Not thread-safe;
 * synchronize externally if accessed from multiple threads.</p>
 */
public final class SlayerManager {

    private final Map<UUID, SlayerQuestSession> activeSessions = new HashMap<>();

    /**
     * Starts a new slayer quest session for the player.
     *
     * @param playerId the player's UUID
     * @param type     the slayer quest line
     * @param tier     the boss tier, from 1 to {@link SlayerType#getMaxTier()}
     * @return the newly started session
     * @throws IllegalArgumentException if the tier is out of range
     * @throws IllegalStateException    if the player already has an active session
     */
    public SlayerQuestSession startQuest(UUID playerId, SlayerType type, int tier) {
        Objects.requireNonNull(type, "type");
        if (tier < 1 || tier > type.getMaxTier()) {
            throw new IllegalArgumentException(
                    "tier must be between 1 and " + type.getMaxTier() + ": " + tier);
        }
        if (activeSessions.containsKey(playerId)) {
            throw new IllegalStateException("player already has an active slayer quest: " + playerId);
        }
        SlayerQuestSession session = new SlayerQuestSession(type, tier, 0);
        activeSessions.put(playerId, session);
        return session;
    }

    /**
     * Returns the player's active session, or {@code null} if none is in progress.
     *
     * @param playerId the player's UUID
     * @return the active session, or {@code null}
     */
    public SlayerQuestSession getActiveQuest(UUID playerId) {
        return activeSessions.get(playerId);
    }

    /**
     * Returns whether the player currently has an active slayer quest session.
     *
     * @param playerId the player's UUID
     * @return {@code true} if a session is in progress
     */
    public boolean hasActiveQuest(UUID playerId) {
        return activeSessions.containsKey(playerId);
    }

    /**
     * Adds kill progress to the player's active session.
     *
     * @param playerId the player's UUID
     * @param amount   the number of kills to credit, must be non-negative
     * @return the updated session
     * @throws IllegalStateException if the player has no active session
     */
    public SlayerQuestSession addProgress(UUID playerId, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative: " + amount);
        }
        SlayerQuestSession session = activeSessions.get(playerId);
        if (session == null) {
            throw new IllegalStateException("player has no active slayer quest: " + playerId);
        }
        SlayerQuestSession updated = session.withKills(session.kills() + amount);
        activeSessions.put(playerId, updated);
        return updated;
    }

    /**
     * Ends the player's active session, whether completed or abandoned.
     *
     * @param playerId the player's UUID
     * @return the session that was removed, or {@code null} if none was active
     */
    public SlayerQuestSession endQuest(UUID playerId) {
        return activeSessions.remove(playerId);
    }

    /**
     * A single slayer quest session: the boss line, the chosen tier and the
     * kill progress accumulated toward spawning the boss.
     *
     * @param type  the slayer boss line
     * @param tier  the chosen boss tier
     * @param kills the kills credited so far
     */
    public record SlayerQuestSession(SlayerType type, int tier, int kills) {

        /**
         * Returns a copy of this session with the given kill count.
         *
         * @param kills the new kill count
         * @return a new session with the updated kill count
         */
        public SlayerQuestSession withKills(int kills) {
            return new SlayerQuestSession(type, tier, kills);
        }
    }
}
