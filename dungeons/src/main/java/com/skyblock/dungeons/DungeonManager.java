package com.skyblock.dungeons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Tracks active dungeon sessions keyed by session ID.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class DungeonManager {

    private final Map<UUID, DungeonSession> sessions = new HashMap<>();

    /**
     * Starts a new dungeon session.
     *
     * @param sessionId unique identifier for this run
     * @param floor     the dungeon floor number, must be positive
     * @param bossId    the UUID of the boss entity
     * @return the newly created {@link DungeonSession}
     * @throws IllegalArgumentException if floor is not positive
     */
    public DungeonSession startSession(UUID sessionId, int floor, UUID bossId) {
        Objects.requireNonNull(sessionId, "sessionId");
        Objects.requireNonNull(bossId, "bossId");
        if (floor < 1) {
            throw new IllegalArgumentException("floor must be positive: " + floor);
        }
        DungeonSession session = new DungeonSession(floor, bossId);
        sessions.put(sessionId, session);
        return session;
    }

    /**
     * Returns the session with the given ID, or empty if none exists.
     *
     * @param sessionId the session to look up
     * @return the session wrapped in an Optional
     */
    public Optional<DungeonSession> getSession(UUID sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    /**
     * Ends a session, removing it from tracking.
     *
     * @param sessionId the session to remove
     */
    public void endSession(UUID sessionId) {
        sessions.remove(sessionId);
    }

    /**
     * Returns an unmodifiable view of all active sessions keyed by session ID.
     *
     * @return the active sessions
     */
    public Map<UUID, DungeonSession> getActiveSessions() {
        return Collections.unmodifiableMap(sessions);
    }

    /**
     * Adds a player to an active session.
     *
     * @param sessionId the target session
     * @param playerId  the player to add
     * @throws IllegalArgumentException if the session does not exist
     */
    public void addPlayer(UUID sessionId, UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        DungeonSession session = sessions.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("no active session: " + sessionId);
        }
        session.playerIds.add(playerId);
    }

    /**
     * Removes a player from an active session.
     *
     * @param sessionId the target session
     * @param playerId  the player to remove
     */
    public void removePlayer(UUID sessionId, UUID playerId) {
        DungeonSession session = sessions.get(sessionId);
        if (session != null) {
            session.playerIds.remove(playerId);
        }
    }

    /**
     * Represents a single active dungeon run.
     */
    public static final class DungeonSession {

        private final int floor;
        private final UUID bossId;
        private final List<UUID> playerIds = new ArrayList<>();

        private DungeonSession(int floor, UUID bossId) {
            this.floor = floor;
            this.bossId = bossId;
        }

        /** Returns the dungeon floor this session is on. */
        public int getFloor() {
            return floor;
        }

        /** Returns the UUID of the boss for this session. */
        public UUID getBossId() {
            return bossId;
        }

        /** Returns an unmodifiable view of the players in this session. */
        public List<UUID> getPlayerIds() {
            return Collections.unmodifiableList(playerIds);
        }
    }
}
