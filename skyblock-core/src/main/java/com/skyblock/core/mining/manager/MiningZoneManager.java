package com.skyblock.core.mining.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock mining zone access and player zone tracking.
 *
 * <p>Tracks which {@link MiningZone} each player is currently assigned to and
 * enforces the minimum mining level required to enter each zone.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class MiningZoneManager {

    /** Mining zones available in SkyBlock, each requiring a minimum mining level. */
    public enum MiningZone {
        GOLD_MINE("Gold Mine",          1),
        DEEP_CAVERNS("Deep Caverns",    5),
        DWARVEN_MINES("Dwarven Mines", 12),
        CRYSTAL_HOLLOWS("Crystal Hollows", 20);

        /** Human-readable display name shown to players. */
        public final String displayName;
        /** Minimum mining level required to enter this zone. */
        public final int minLevel;

        MiningZone(String displayName, int minLevel) {
            this.displayName = displayName;
            this.minLevel = minLevel;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private static final MiningZoneManager INSTANCE = new MiningZoneManager();

    /** Per-player currently assigned mining zone. */
    private final Map<UUID, MiningZone> playerZones = new HashMap<>();

    private MiningZoneManager() {}

    /**
     * Returns the single shared {@code MiningZoneManager} instance.
     *
     * @return the singleton instance
     */
    public static MiningZoneManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the zone the player is currently assigned to, or {@code null} if none.
     *
     * @param playerId the player to look up
     * @return the player's current zone, or {@code null}
     */
    public MiningZone getZone(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerZones.get(playerId);
    }

    /**
     * Assigns the player to the given zone.
     *
     * @param playerId the player to update
     * @param zone     the zone to assign, must not be null
     */
    public void setZone(UUID playerId, MiningZone zone) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(zone, "zone");
        playerZones.put(playerId, zone);
    }

    /**
     * Removes any zone assignment for the player.
     *
     * @param playerId the player to clear
     */
    public void clearZone(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        playerZones.remove(playerId);
    }

    /**
     * Returns whether the player meets the minimum level requirement for {@code zone}.
     *
     * @param miningLevel the player's current mining level
     * @param zone        the zone to check
     * @return {@code true} if the player may enter the zone
     */
    public boolean canEnter(int miningLevel, MiningZone zone) {
        Objects.requireNonNull(zone, "zone");
        return miningLevel >= zone.minLevel;
    }
}
