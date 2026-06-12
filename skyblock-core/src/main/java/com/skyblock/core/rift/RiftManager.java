package com.skyblock.core.rift;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking per-player Rift dimension state.
 *
 * <p>Tracks which zone a player is in, how many seconds of Rift time they have
 * remaining, and how many Rift mobs they have killed. Not thread-safe.</p>
 */
public final class RiftManager {

    /** Named zones inside the Rift dimension. */
    public enum RiftZone {
        STILLGORE_CHATEAU, WYLD_WOODS, LIVING_CAVE, MIRRORVERSE, LAGOON, COLOSSEUM
    }

    /** Mob types that inhabit the Rift. */
    public enum RiftMobType {
        BACTE, BLOBBERCYST, CRUX, RIFT_WEIRDO, SOULFLOW_ENGINE, VOLT
    }

    /** Immutable snapshot of a player's current Rift state. */
    public static final class RiftData {
        public final boolean inRift;
        public final RiftZone zone;
        public final long timeRemainingSeconds;
        public final Map<RiftMobType, Integer> kills;

        public RiftData(boolean inRift, RiftZone zone, long timeRemainingSeconds,
                        Map<RiftMobType, Integer> kills) {
            this.inRift = inRift;
            this.zone = zone;
            this.timeRemainingSeconds = timeRemainingSeconds;
            this.kills = Map.copyOf(kills);
        }
    }

    private static final long DEFAULT_TIME_SECONDS = 480L;

    private static final RiftManager INSTANCE = new RiftManager();

    private final Map<UUID, Boolean> inRift = new HashMap<>();
    private final Map<UUID, RiftZone> currentZone = new HashMap<>();
    private final Map<UUID, Long> timeRemaining = new HashMap<>();
    private final Map<UUID, Map<RiftMobType, Integer>> mobKills = new HashMap<>();

    private RiftManager() {}

    /**
     * Returns the single shared {@code RiftManager} instance.
     *
     * @return the singleton instance
     */
    public static RiftManager getInstance() {
        return INSTANCE;
    }

    /**
     * Marks a player as having entered the Rift, placing them in the given zone
     * with the default time allocation.
     *
     * @param playerId the player entering the Rift
     * @param zone     the starting zone
     */
    public void enterRift(UUID playerId, RiftZone zone) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(zone, "zone");
        inRift.put(playerId, true);
        currentZone.put(playerId, zone);
        timeRemaining.put(playerId, DEFAULT_TIME_SECONDS);
    }

    /**
     * Removes the player from the Rift, clearing their zone but preserving kill
     * counts so progress survives re-entry.
     *
     * @param playerId the player leaving the Rift
     * @return {@code true} if the player was actually in the Rift
     */
    public boolean exitRift(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Boolean was = inRift.remove(playerId);
        currentZone.remove(playerId);
        timeRemaining.remove(playerId);
        return Boolean.TRUE.equals(was);
    }

    /**
     * Records a kill for the given mob type and decrements the player's
     * remaining time by {@code timeCostSeconds}.
     *
     * @param playerId       the player who made the kill
     * @param type           the mob type killed
     * @param timeCostSeconds seconds to deduct from the player's Rift timer
     * @return the player's total kill count for that mob type after the addition
     */
    public int addKill(UUID playerId, RiftMobType type, long timeCostSeconds) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<RiftMobType, Integer> kills = mobKills.computeIfAbsent(
                playerId, id -> new EnumMap<>(RiftMobType.class));
        int newCount = kills.merge(type, 1, Integer::sum);
        if (timeCostSeconds > 0) {
            long current = timeRemaining.getOrDefault(playerId, 0L);
            timeRemaining.put(playerId, Math.max(0L, current - timeCostSeconds));
        }
        return newCount;
    }

    /**
     * Returns the seconds of Rift time remaining for a player.
     *
     * @param playerId the player to look up
     * @return remaining seconds, or {@code 0} if the player is not in the Rift
     */
    public long getTimeRemaining(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return timeRemaining.getOrDefault(playerId, 0L);
    }

    /**
     * Returns a snapshot of the player's current Rift state.
     *
     * @param playerId the player to look up
     * @return a {@link RiftData} snapshot (never {@code null})
     */
    public RiftData getRiftData(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean active = Boolean.TRUE.equals(inRift.get(playerId));
        RiftZone zone = currentZone.get(playerId);
        long time = timeRemaining.getOrDefault(playerId, 0L);
        Map<RiftMobType, Integer> kills = mobKills.getOrDefault(
                playerId, new EnumMap<>(RiftMobType.class));
        return new RiftData(active, zone, time, kills);
    }

    /**
     * Resets all Rift data for the given player.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had any Rift data
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadData = inRift.remove(playerId) != null;
        hadData |= currentZone.remove(playerId) != null;
        hadData |= timeRemaining.remove(playerId) != null;
        hadData |= mobKills.remove(playerId) != null;
        return hadData;
    }
}
