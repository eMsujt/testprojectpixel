package com.skyblock.dungeons;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * Tracks active dungeon runs keyed by session ID.
 *
 * <p>A run is started for a party of players on a {@link Floor}; each player
 * can be in at most one active run at a time. Not thread-safe; synchronize
 * externally if accessed from multiple threads.</p>
 */
public final class DungeonManager {

    private final Map<UUID, DungeonSession> activeSessions = new HashMap<>();
    private final Map<UUID, UUID> playerSessions = new HashMap<>();

    /**
     * Starts a new dungeon run for the given party.
     *
     * @param party the players entering the dungeon, must not be null or empty
     * @param floor the floor to run, must not be null
     * @return the newly created {@link DungeonSession}
     * @throws IllegalArgumentException if the party is null or empty, the
     *                                  floor is null, or any party member is
     *                                  already in an active run
     */
    public DungeonSession startRun(List<Player> party, Floor floor) {
        if (party == null || party.isEmpty()) {
            throw new IllegalArgumentException("party must not be null or empty");
        }
        if (floor == null) {
            throw new IllegalArgumentException("floor must not be null");
        }
        Set<UUID> playerIds = new LinkedHashSet<>();
        for (Player player : party) {
            if (player == null) {
                throw new IllegalArgumentException("party must not contain null players");
            }
            UUID playerId = player.getUniqueId();
            if (playerSessions.containsKey(playerId)) {
                throw new IllegalArgumentException(
                        "player is already in an active run: " + player.getName());
            }
            playerIds.add(playerId);
        }
        DungeonSession session = new DungeonSession(UUID.randomUUID(), floor, playerIds);
        activeSessions.put(session.getSessionId(), session);
        for (UUID playerId : playerIds) {
            playerSessions.put(playerId, session.getSessionId());
        }
        return session;
    }

    /**
     * Ends a run, removing it and all of its players from tracking.
     *
     * @param sessionId the run to end
     * @return {@code true} if the run existed and has been ended
     */
    public boolean endRun(UUID sessionId) {
        DungeonSession session = activeSessions.remove(sessionId);
        if (session == null) {
            return false;
        }
        for (UUID playerId : session.getPlayerIds()) {
            playerSessions.remove(playerId);
        }
        return true;
    }

    /**
     * Returns the run with the given session ID, or empty if none exists.
     *
     * @param sessionId the run to look up
     * @return the run wrapped in an Optional
     */
    public Optional<DungeonSession> getSession(UUID sessionId) {
        return Optional.ofNullable(activeSessions.get(sessionId));
    }

    /**
     * Returns the active run the given player is in, or empty if none.
     *
     * @param playerId the player to look up
     * @return the player's run wrapped in an Optional
     */
    public Optional<DungeonSession> getSessionForPlayer(UUID playerId) {
        UUID sessionId = playerSessions.get(playerId);
        return sessionId == null ? Optional.empty() : getSession(sessionId);
    }

    /**
     * Returns an unmodifiable view of all active runs keyed by session ID.
     *
     * @return the active runs
     */
    public Map<UUID, DungeonSession> getActiveSessions() {
        return Collections.unmodifiableMap(activeSessions);
    }

    // -------------------------------------------------------------------------
    // Floor enum
    // -------------------------------------------------------------------------

    /**
     * The Catacombs dungeon floors available on SkyBlock.
     */
    public enum Floor {

        FLOOR_1("Bonzo", 1),
        FLOOR_2("Scarf", 5),
        FLOOR_3("The Professor", 9),
        FLOOR_4("Thorn", 14),
        FLOOR_5("Livid", 19),
        FLOOR_6("Sadan", 24),
        FLOOR_7("Necron", 29);

        private final String bossName;
        private final int recommendedLevel;

        Floor(String bossName, int recommendedLevel) {
            this.bossName = bossName;
            this.recommendedLevel = recommendedLevel;
        }

        /** Returns the name of the boss fought on this floor. */
        public String getBossName() {
            return bossName;
        }

        /** Returns the recommended Catacombs level for this floor. */
        public int getRecommendedLevel() {
            return recommendedLevel;
        }

        /** Returns the numeric floor number, 1 through 7. */
        public int getFloorNumber() {
            return ordinal() + 1;
        }

        /**
         * Returns the floor for the given floor number.
         *
         * @param floorNumber the floor number, 1 through 7
         * @return the matching floor
         * @throws IllegalArgumentException if no floor has that number
         */
        public static Floor fromFloorNumber(int floorNumber) {
            if (floorNumber < 1 || floorNumber > values().length) {
                throw new IllegalArgumentException("no such floor: " + floorNumber);
            }
            return values()[floorNumber - 1];
        }
    }

    // -------------------------------------------------------------------------
    // DungeonRoom inner enum
    // -------------------------------------------------------------------------

    /**
     * The types of room a Catacombs dungeon floor is made up of.
     */
    public enum DungeonRoom {

        ENTRANCE("Entrance", false),
        PUZZLE("Puzzle", true),
        MOB_ROOM("Mob Room", true),
        MINIBOSS("Miniboss", true),
        BOSS("Boss", true),
        FAIRY("Fairy", false),
        TRAP("Trap", true),
        BLOOD("Blood", true);

        private final String displayName;
        private final boolean requiredForCompletion;

        DungeonRoom(String displayName, boolean requiredForCompletion) {
            this.displayName = displayName;
            this.requiredForCompletion = requiredForCompletion;
        }

        /** Returns the human-readable name of this room type. */
        public String getDisplayName() {
            return displayName;
        }

        /** Returns whether this room must be cleared for full floor completion. */
        public boolean isRequiredForCompletion() {
            return requiredForCompletion;
        }
    }

    // -------------------------------------------------------------------------
    // DungeonSession inner class
    // -------------------------------------------------------------------------

    /**
     * Represents a single active dungeon run.
     */
    public static final class DungeonSession {

        private final UUID sessionId;
        private final Floor floor;
        private final Set<UUID> playerIds;

        private DungeonSession(UUID sessionId, Floor floor, Set<UUID> playerIds) {
            this.sessionId = sessionId;
            this.floor = floor;
            this.playerIds = playerIds;
        }

        /** Returns the unique id of this run. */
        public UUID getSessionId() {
            return sessionId;
        }

        /** Returns the dungeon floor this run is on. */
        public Floor getFloor() {
            return floor;
        }

        /** Returns an unmodifiable view of the players in this run. */
        public Set<UUID> getPlayerIds() {
            return Collections.unmodifiableSet(playerIds);
        }
    }
}
