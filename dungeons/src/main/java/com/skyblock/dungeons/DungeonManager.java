package com.skyblock.dungeons;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Tracks active dungeon runs keyed by session ID.
 *
 * <p>A run is started for a party of players on a {@link DungeonFloor}; each
 * player can be in at most one active run at a time. Not thread-safe;
 * synchronize externally if accessed from multiple threads.</p>
 */
public final class DungeonManager {

    private final Map<UUID, DungeonSession> activeSessions = new HashMap<>();
    private final Map<UUID, UUID> playerSessions = new HashMap<>();
    private final Map<UUID, List<String>> dungeonHistory = new HashMap<>();

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
    public DungeonSession startRun(List<Player> party, DungeonFloor floor) {
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
            recordDungeonEvent(playerId, "Completed floor " + session.getFloor().name());
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

    public void recordDungeonEvent(UUID playerUuid, String summary) {
        dungeonHistory
                .computeIfAbsent(playerUuid, k -> new ArrayList<>())
                .add(summary);
    }

    public List<String> getDungeonHistory(UUID playerUuid) {
        return Collections.unmodifiableList(dungeonHistory.getOrDefault(playerUuid, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllDungeonHistory() {
        return Collections.unmodifiableMap(dungeonHistory);
    }

    public String getDungeonStats(UUID playerId) {
        List<String> history = getDungeonHistory(playerId);
        return "Total completions: " + history.size();
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "dungeons.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        dungeonHistory.clear();
        if (cfg.isConfigurationSection("dungeonHistory")) {
            for (String key : cfg.getConfigurationSection("dungeonHistory").getKeys(false)) {
                try {
                    List<String> entries = cfg.getStringList("dungeonHistory." + key);
                    if (!entries.isEmpty()) {
                        dungeonHistory.put(UUID.fromString(key), new ArrayList<>(entries));
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "dungeons.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, List<String>> entry : dungeonHistory.entrySet()) {
            cfg.set("dungeonHistory." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save dungeons.yml", e);
        }
    }

    /**
     * Represents a single active dungeon run.
     */
    public static final class DungeonSession {

        private final UUID sessionId;
        private final DungeonFloor floor;
        private final Set<UUID> playerIds;

        private DungeonSession(UUID sessionId, DungeonFloor floor, Set<UUID> playerIds) {
            this.sessionId = sessionId;
            this.floor = floor;
            this.playerIds = playerIds;
        }

        /** Returns the unique id of this run. */
        public UUID getSessionId() {
            return sessionId;
        }

        /** Returns the dungeon floor this run is on. */
        public DungeonFloor getFloor() {
            return floor;
        }

        /** Returns an unmodifiable view of the players in this run. */
        public Set<UUID> getPlayerIds() {
            return Collections.unmodifiableSet(playerIds);
        }
    }
}
