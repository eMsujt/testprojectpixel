package com.skyblock.core.reforge;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock item reforges.
 *
 * <p>Tracks the active reforge applied to each player's held item slot
 * and exposes the full {@link Reforge} catalogue with stat bonuses.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class ReforgeManager {

    /** A reforge type with display name and primary stat bonus. */
    public enum ReforgeType {
        NONE("None", 0, 0, 0),
        SHARP("Sharp", 10, 0, 0),
        FIERCE("Fierce", 20, 0, 5),
        GENTLE("Gentle", 0, 10, 0),
        STRONG("Strong", 15, 5, 0),
        SUPERIOR("Superior", 35, 20, 20),
        LEGENDARY("Legendary", 25, 10, 10),
        ANCIENT("Ancient", 30, 15, 15),
        FORCEFUL("Forceful", 5, 0, 20),
        UNPLEASANT("Unpleasant", 0, 0, 5),
        CLEAN("Clean", 0, 5, 0),
        WEIRD("Weird", 5, 5, 5);

        private final String displayName;
        private final int strengthBonus;
        private final int defenseBonus;
        private final int speedBonus;

        ReforgeType(String displayName, int strengthBonus, int defenseBonus, int speedBonus) {
            this.displayName = displayName;
            this.strengthBonus = strengthBonus;
            this.defenseBonus = defenseBonus;
            this.speedBonus = speedBonus;
        }

        public String getDisplayName() { return displayName; }
        public int getStrengthBonus() { return strengthBonus; }
        public int getDefenseBonus() { return defenseBonus; }
        public int getSpeedBonus() { return speedBonus; }

        public static ReforgeType fromName(String name) {
            for (ReforgeType r : values()) {
                if (r.displayName.equalsIgnoreCase(name) || r.name().equalsIgnoreCase(name)) {
                    return r;
                }
            }
            return null;
        }
    }

    private static final ReforgeManager INSTANCE = new ReforgeManager();

    /** Per-player active reforge. */
    private final Map<UUID, ReforgeType> playerReforges = new HashMap<>();

    private ReforgeManager() {}

    public static ReforgeManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the active reforge for the given player, or {@link Reforge#NONE} if unset.
     *
     * @param playerId the player to look up
     * @return the player's current reforge
     */
    public ReforgeType getReforge(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerReforges.getOrDefault(playerId, ReforgeType.NONE);
    }

    /**
     * Sets the active reforge for the given player.
     *
     * @param playerId the player to update
     * @param reforge  the reforge to apply; use {@link ReforgeType#NONE} to clear
     */
    public void setReforge(UUID playerId, ReforgeType reforge) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(reforge, "reforge");
        playerReforges.put(playerId, reforge);
    }

    /**
     * Clears the active reforge for the given player.
     *
     * @param playerId the player to reset
     */
    public void clearReforge(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        playerReforges.remove(playerId);
    }

    /**
     * Returns an unmodifiable view of all player reforges.
     *
     * @return map of player UUID to active {@link ReforgeType}
     */
    public Map<UUID, ReforgeType> getAllReforges() {
        return Collections.unmodifiableMap(playerReforges);
    }
}
