package com.skyblock.plugin.profile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Data holder for a single SkyBlock profile owned by a player.
 *
 * <p>A profile is identified by its profile id and tracks the owning
 * player together with the player's accumulated skill experience and
 * collection counts on this profile. The backing maps are mutable so
 * that progression can be updated in place.</p>
 */
public final class SkyBlockProfile {

    private final String profileId;
    private final UUID playerUUID;
    private final Map<String, Long> skillXP = new HashMap<>();
    private final Map<String, Long> collections = new HashMap<>();

    /**
     * Creates a new profile with empty skill and collection progress.
     *
     * @param profileId  the unique identifier of the profile
     * @param playerUUID the UUID of the owning player
     */
    public SkyBlockProfile(String profileId, UUID playerUUID) {
        this.profileId = Objects.requireNonNull(profileId, "profileId");
        this.playerUUID = Objects.requireNonNull(playerUUID, "playerUUID");
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
     * Returns the mutable map of skill name to accumulated experience.
     *
     * @return the skill experience map
     */
    public Map<String, Long> getSkillXP() {
        return skillXP;
    }

    /**
     * Returns the mutable map of collection name to collected amount.
     *
     * @return the collections map
     */
    public Map<String, Long> getCollections() {
        return collections;
    }

    @Override
    public String toString() {
        return "SkyBlockProfile{profileId=" + profileId + ", playerUUID=" + playerUUID
                + ", skillXP=" + skillXP + ", collections=" + collections + '}';
    }
}
