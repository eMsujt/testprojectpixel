package com.skyblock.profiles;

import java.util.Objects;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.profile.manager.ProfileManager.SkyBlockProfile} instead.
 */
@Deprecated
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
