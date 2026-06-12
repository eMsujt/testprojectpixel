package com.skyblock.brewing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Tracks per-player brewing stands for timed potion brewing.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class BrewingManager {

    private final Map<UUID, Map<Integer, BrewingProcess>> brewingStands = new HashMap<>();

    /**
     * Starts brewing a potion in the given stand for the player.
     *
     * @param playerId       the player starting the brew
     * @param standId        the index of the brewing stand, must not be negative
     * @param potionId       the identifier of the potion to brew
     * @param startTime      the time the brewing process starts, in epoch milliseconds
     * @param durationMillis how long the brewing process takes, must not be negative
     * @return the created brewing process entry
     * @throws IllegalStateException if the stand is already occupied
     * @throws IllegalArgumentException if {@code standId} or {@code durationMillis} is negative
     * @throws NullPointerException if {@code playerId} or {@code potionId} is {@code null}
     */
    public BrewingProcess startBrewing(UUID playerId, int standId, String potionId, long startTime, long durationMillis) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(potionId, "potionId");
        if (standId < 0) {
            throw new IllegalArgumentException("standId must not be negative, got " + standId);
        }
        if (durationMillis < 0) {
            throw new IllegalArgumentException("durationMillis must not be negative, got " + durationMillis);
        }
        Map<Integer, BrewingProcess> stands = brewingStands.computeIfAbsent(playerId, ignored -> new HashMap<>());
        if (stands.containsKey(standId)) {
            throw new IllegalStateException("Brewing stand " + standId + " is already occupied");
        }
        BrewingProcess process = new BrewingProcess(standId, potionId, startTime, startTime + durationMillis);
        stands.put(standId, process);
        return process;
    }

    /**
     * Returns the brewing process for the player's stand, or {@code null} if the stand is empty.
     *
     * @param playerId the player to look up
     * @param standId  the index of the brewing stand
     * @return the brewing process entry, or {@code null} if nothing is brewing in it
     */
    public BrewingProcess getProcess(UUID playerId, int standId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<Integer, BrewingProcess> stands = brewingStands.get(playerId);
        return stands == null ? null : stands.get(standId);
    }

    /**
     * Collects the finished potion from the given stand, freeing it for reuse.
     *
     * @param playerId the player collecting the potion
     * @param standId  the index of the brewing stand
     * @param now      the current time in epoch milliseconds
     * @return the identifier of the brewed potion
     * @throws IllegalStateException if the stand is empty or the process has not completed
     */
    public String collectPotion(UUID playerId, int standId, long now) {
        BrewingProcess process = getProcess(playerId, standId);
        if (process == null) {
            throw new IllegalStateException("Brewing stand " + standId + " is empty");
        }
        if (!process.isComplete(now)) {
            throw new IllegalStateException(
                    "Brewing stand " + standId + " has " + process.getRemainingTime(now) + "ms remaining");
        }
        brewingStands.get(playerId).remove(standId);
        return process.getPotionId();
    }

    /**
     * Cancels the brewing process in the given stand, discarding the potion.
     *
     * @param playerId the player cancelling the process
     * @param standId  the index of the brewing stand
     * @return {@code true} if a process was cancelled, {@code false} if the stand was empty
     */
    public boolean cancelBrewing(UUID playerId, int standId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<Integer, BrewingProcess> stands = brewingStands.get(playerId);
        return stands != null && stands.remove(standId) != null;
    }

    /**
     * Returns an unmodifiable view of the player's occupied brewing stands, keyed by stand index.
     *
     * @param playerId the player to look up
     * @return the player's brewing stands, empty if none are occupied
     */
    public Map<Integer, BrewingProcess> getProcesses(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<Integer, BrewingProcess> stands = brewingStands.get(playerId);
        return stands == null ? Map.of() : Collections.unmodifiableMap(stands);
    }

    /**
     * A single potion brewing process occupying a brewing stand.
     */
    public static final class BrewingProcess {

        private final int standId;
        private final String potionId;
        private final long startTime;
        private final long endTime;

        BrewingProcess(int standId, String potionId, long startTime, long endTime) {
            this.standId = standId;
            this.potionId = potionId;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        /** Returns the index of the brewing stand this process occupies. */
        public int getStandId() {
            return standId;
        }

        /** Returns the identifier of the potion being brewed. */
        public String getPotionId() {
            return potionId;
        }

        /** Returns the time the brewing process started, in epoch milliseconds. */
        public long getStartTime() {
            return startTime;
        }

        /** Returns the time the brewing process completes, in epoch milliseconds. */
        public long getEndTime() {
            return endTime;
        }

        /**
         * Returns whether the brewing process has completed at the given time.
         *
         * @param now the current time in epoch milliseconds
         * @return {@code true} if the potion is ready to collect
         */
        public boolean isComplete(long now) {
            return now >= endTime;
        }

        /**
         * Returns the remaining brewing time at the given moment.
         *
         * @param now the current time in epoch milliseconds
         * @return the remaining time in milliseconds, or {@code 0} if already complete
         */
        public long getRemainingTime(long now) {
            return Math.max(0, endTime - now);
        }
    }
}
