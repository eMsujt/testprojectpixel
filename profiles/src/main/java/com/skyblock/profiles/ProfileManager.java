package com.skyblock.profiles;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manages the SkyBlock profiles owned by each player.
 *
 * <p>Profiles are identified by their profile id, which is unique per
 * player. Each player has at most one active profile, which is selected
 * automatically when their first profile is created. Not thread-safe;
 * synchronize externally if accessed from multiple threads.</p>
 */
/** @deprecated Use {@link com.skyblock.core.manager.ProfileManager} instead. */
@Deprecated
public final class ProfileManager {

    private final Map<UUID, Map<String, SkyBlockProfile>> playerProfiles = new HashMap<>();
    private final Map<UUID, String> activeProfiles = new HashMap<>();

    /**
     * Creates a new profile for a player.
     *
     * <p>If the player has no other profiles, the new profile becomes
     * their active profile.</p>
     *
     * @param playerId  the player's unique id, must not be null
     * @param profileId the unique identifier of the profile, must not be null
     * @param mode      the game mode to create the profile with, must not be null
     * @return the created profile
     * @throws IllegalArgumentException if any argument is null or the player
     *         already has a profile with the given id
     */
    public SkyBlockProfile createProfile(UUID playerId, String profileId, GameMode mode) {
        if (playerId == null) {
            throw new IllegalArgumentException("playerId must not be null");
        }
        if (profileId == null) {
            throw new IllegalArgumentException("profileId must not be null");
        }
        if (mode == null) {
            throw new IllegalArgumentException("mode must not be null");
        }
        Map<String, SkyBlockProfile> profiles =
                playerProfiles.computeIfAbsent(playerId, id -> new LinkedHashMap<>());
        if (profiles.containsKey(profileId)) {
            throw new IllegalArgumentException("player already has a profile with id: " + profileId);
        }
        SkyBlockProfile profile =
                new SkyBlockProfile(profileId, playerId, mode, System.currentTimeMillis());
        profiles.put(profileId, profile);
        activeProfiles.putIfAbsent(playerId, profileId);
        return profile;
    }

    /**
     * Returns a player's profile by id.
     *
     * @param playerId  the player's unique id
     * @param profileId the profile id
     * @return the profile, or {@code null} if the player has no profile with that id
     */
    public SkyBlockProfile getProfile(UUID playerId, String profileId) {
        Map<String, SkyBlockProfile> profiles = playerProfiles.get(playerId);
        return profiles == null ? null : profiles.get(profileId);
    }

    /**
     * Returns all profiles owned by a player, in creation order.
     *
     * @param playerId the player's unique id
     * @return an unmodifiable list of the player's profiles, empty if none
     */
    public List<SkyBlockProfile> getProfiles(UUID playerId) {
        Map<String, SkyBlockProfile> profiles = playerProfiles.get(playerId);
        return profiles == null
                ? Collections.emptyList()
                : List.copyOf(profiles.values());
    }

    /**
     * Returns a player's currently active profile.
     *
     * @param playerId the player's unique id
     * @return the active profile, or {@code null} if the player has no profiles
     */
    public SkyBlockProfile getActiveProfile(UUID playerId) {
        String profileId = activeProfiles.get(playerId);
        return profileId == null ? null : getProfile(playerId, profileId);
    }

    /**
     * Switches a player's active profile.
     *
     * @param playerId  the player's unique id
     * @param profileId the id of the profile to activate
     * @throws IllegalArgumentException if the player has no profile with the given id
     */
    public void setActiveProfile(UUID playerId, String profileId) {
        if (getProfile(playerId, profileId) == null) {
            throw new IllegalArgumentException("player has no profile with id: " + profileId);
        }
        activeProfiles.put(playerId, profileId);
    }

    /**
     * Deletes a player's profile.
     *
     * <p>If the deleted profile was active, the player's oldest remaining
     * profile becomes active instead.</p>
     *
     * @param playerId  the player's unique id
     * @param profileId the id of the profile to delete
     * @return {@code true} if the player had the profile and it has been deleted
     */
    public boolean deleteProfile(UUID playerId, String profileId) {
        Map<String, SkyBlockProfile> profiles = playerProfiles.get(playerId);
        if (profiles == null || profiles.remove(profileId) == null) {
            return false;
        }
        if (profiles.isEmpty()) {
            playerProfiles.remove(playerId);
            activeProfiles.remove(playerId);
        } else if (profileId.equals(activeProfiles.get(playerId))) {
            SkyBlockProfile oldest = profiles.values().stream()
                    .min(Comparator.comparingLong(SkyBlockProfile::getCreatedAt))
                    .get();
            activeProfiles.put(playerId, oldest.getProfileId());
        }
        return true;
    }

    /**
     * Returns the number of profiles owned by a player.
     *
     * @param playerId the player's unique id
     * @return the player's profile count
     */
    public int getProfileCount(UUID playerId) {
        Map<String, SkyBlockProfile> profiles = playerProfiles.get(playerId);
        return profiles == null ? 0 : profiles.size();
    }

    public String getProfileStats(UUID playerId) {
        SkyBlockProfile active = getActiveProfile(playerId);
        String id = active == null ? "none" : active.getProfileId();
        String mode = active == null ? "N/A" : active.getMode().name();
        String created = active == null ? "N/A"
                : new SimpleDateFormat("yyyy-MM-dd").format(new Date(active.getCreatedAt()));
        int count = getProfileCount(playerId);
        return "Profile Stats: Active: " + id + " | Mode: " + mode + " | Created: " + created + " | Profiles: " + count;
    }
}
