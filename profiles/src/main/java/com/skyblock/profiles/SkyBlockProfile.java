package com.skyblock.profiles;

import java.util.Objects;
import java.util.UUID;

/**
 * Data holder for a single SkyBlock profile owned by a player.
 *
 * <p>A profile is identified by its profile id and tracks the owning
 * player, the {@link GameMode} it was created with and its creation
 * timestamp. Instances are immutable and therefore thread-safe.</p>
 */
public final class SkyBlockProfile {

    private final String profileId;
    private final UUID playerUUID;
    private final GameMode mode;
    private final long createdAt;

    /**
     * Creates a new profile.
     *
     * @param profileId  the unique identifier of the profile
     * @param playerUUID the UUID of the owning player
     * @param mode       the game mode the profile was created with
     * @param createdAt  the creation time in epoch milliseconds
     */
    public SkyBlockProfile(String profileId, UUID playerUUID, GameMode mode, long createdAt) {
        this.profileId = Objects.requireNonNull(profileId, "profileId");
        this.playerUUID = Objects.requireNonNull(playerUUID, "playerUUID");
        this.mode = Objects.requireNonNull(mode, "mode");
        this.createdAt = createdAt;
    }

    /**
     * Returns the unique identifier of the profile.
     *
     * @return the profile id
     */
    public String getProfileId() {
        return profileId;
    }

    /**
     * Returns the UUID of the player owning the profile.
     *
     * @return the owning player's UUID
     */
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    /**
     * Returns the game mode the profile was created with.
     *
     * @return the profile's game mode
     */
    public GameMode getMode() {
        return mode;
    }

    /**
     * Returns the timestamp the profile was created at.
     *
     * @return the creation time in epoch milliseconds
     */
    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "SkyBlockProfile{profileId=" + profileId + ", playerUUID=" + playerUUID
                + ", mode=" + mode + ", createdAt=" + createdAt + '}';
    }
}
