package com.skyblock.core.dungeon;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing dungeon runs and per-player dungeon class selection.
 *
 * <p>Tracks active runs and completion counts per player. Not thread-safe;
 * synchronize externally if accessed from multiple threads.</p>
 */
public final class DungeonManager {

    /** Playable dungeon classes. */
    public enum DungeonClass {
        HEALER, MAGE, BERSERK, ARCHER, TANK
    }

    /** Immutable snapshot of an active dungeon run. */
    public static final class DungeonRun {
        public final UUID runId;
        public final UUID playerId;
        public final DungeonClass dungeonClass;
        public final int floor;

        public DungeonRun(UUID runId, UUID playerId, DungeonClass dungeonClass, int floor) {
            this.runId = Objects.requireNonNull(runId, "runId");
            this.playerId = Objects.requireNonNull(playerId, "playerId");
            this.dungeonClass = Objects.requireNonNull(dungeonClass, "dungeonClass");
            if (floor < 1) {
                throw new IllegalArgumentException("floor must be >= 1, got " + floor);
            }
            this.floor = floor;
        }
    }

    private static final DungeonManager INSTANCE = new DungeonManager();

    /** Dungeon class selected by each player. */
    private final Map<UUID, DungeonClass> playerClasses = new HashMap<>();

    /** Currently active run per player; absent if not in a dungeon. */
    private final Map<UUID, DungeonRun> activeRuns = new HashMap<>();

    /** Total completed dungeon runs per player. */
    private final Map<UUID, Integer> completionCounts = new HashMap<>();

    private DungeonManager() {
    }

    /**
     * Returns the single shared {@code DungeonManager} instance.
     *
     * @return the singleton instance
     */
    public static DungeonManager getInstance() {
        return INSTANCE;
    }

    /**
     * Sets the dungeon class for the given player.
     *
     * @param playerId the player to update
     * @param cls      the class to assign
     */
    public void setPlayerClass(UUID playerId, DungeonClass cls) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(cls, "cls");
        playerClasses.put(playerId, cls);
    }

    /**
     * Returns the dungeon class selected by the given player, or {@code null} if none set.
     *
     * @param playerId the player to look up
     * @return the player's {@link DungeonClass}, or {@code null}
     */
    public DungeonClass getPlayerClass(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerClasses.get(playerId);
    }

    /**
     * Starts a new dungeon run for the given player.
     *
     * @param playerId the player entering the dungeon
     * @param floor    the dungeon floor (must be >= 1)
     * @return the created {@link DungeonRun}
     * @throws IllegalStateException    if the player already has an active run
     * @throws IllegalStateException    if the player has no class selected
     */
    public DungeonRun startRun(UUID playerId, int floor) {
        Objects.requireNonNull(playerId, "playerId");
        if (activeRuns.containsKey(playerId)) {
            throw new IllegalStateException("Player " + playerId + " already has an active dungeon run");
        }
        DungeonClass cls = playerClasses.get(playerId);
        if (cls == null) {
            throw new IllegalStateException("Player " + playerId + " has no dungeon class selected");
        }
        DungeonRun run = new DungeonRun(UUID.randomUUID(), playerId, cls, floor);
        activeRuns.put(playerId, run);
        return run;
    }

    /**
     * Completes and removes the active dungeon run for the given player.
     *
     * @param playerId the player completing the run
     * @return the completed {@link DungeonRun}
     * @throws IllegalStateException if the player has no active run
     */
    public DungeonRun completeRun(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        DungeonRun run = activeRuns.remove(playerId);
        if (run == null) {
            throw new IllegalStateException("Player " + playerId + " has no active dungeon run");
        }
        completionCounts.merge(playerId, 1, Integer::sum);
        return run;
    }

    /**
     * Returns the active dungeon run for the given player, or {@code null} if not in a dungeon.
     *
     * @param playerId the player to look up
     * @return the active {@link DungeonRun}, or {@code null}
     */
    public DungeonRun getActiveRun(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeRuns.get(playerId);
    }

    /**
     * Returns the total number of completed dungeon runs for the given player.
     *
     * @param playerId the player to look up
     * @return the completion count, {@code 0} if the player has none
     */
    public int getCompletionCount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return completionCounts.getOrDefault(playerId, 0);
    }

    /**
     * Removes all dungeon data for the given player.
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had any data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadData = playerClasses.remove(playerId) != null;
        hadData |= activeRuns.remove(playerId) != null;
        hadData |= completionCounts.remove(playerId) != null;
        return hadData;
    }
}
