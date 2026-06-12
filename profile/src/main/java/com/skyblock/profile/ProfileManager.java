package com.skyblock.profile;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manages per-player profiles identified by a profile name.
 *
 * <p>Each player can own multiple profiles, exposed as immutable
 * {@link Profile} snapshots. The first profile a player creates becomes
 * their active profile. Not thread-safe; synchronize externally if
 * accessed from multiple threads.</p>
 */
public final class ProfileManager {

    /**
     * An immutable snapshot of a player's profile.
     *
     * @param owner           the unique id of the owning player
     * @param name            the profile's unique name per player
     * @param createdAtMillis the creation time in epoch milliseconds
     */
    public record Profile(UUID owner, String name, long createdAtMillis) {
    }

    private final Map<UUID, Map<String, Profile>> profilesByPlayer = new HashMap<>();
    private final Map<UUID, String> activeProfiles = new HashMap<>();

    /**
     * Creates a new profile for a player.
     *
     * <p>If the player has no other profiles, the new profile becomes
     * their active profile.</p>
     *
     * @param playerId the unique id of the player, must not be null
     * @param name     the profile name, unique per player, must not be null
     * @return the created profile
     * @throws IllegalArgumentException if any argument is null or the player
     *         already has a profile with the given name
     */
    public Profile createProfile(UUID playerId, String name) {
        requirePlayerId(playerId);
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        Map<String, Profile> profiles =
                profilesByPlayer.computeIfAbsent(playerId, id -> new LinkedHashMap<>());
        if (profiles.containsKey(name)) {
            throw new IllegalArgumentException("player already has a profile named: " + name);
        }
        Profile profile = new Profile(playerId, name, System.currentTimeMillis());
        profiles.put(name, profile);
        activeProfiles.putIfAbsent(playerId, name);
        return profile;
    }

    /**
     * Returns a player's profile by name.
     *
     * @param playerId the unique id of the player
     * @param name     the profile name
     * @return the profile, or {@code null} if the player has no profile with that name
     */
    public Profile getProfile(UUID playerId, String name) {
        Map<String, Profile> profiles = profilesByPlayer.get(playerId);
        return profiles == null ? null : profiles.get(name);
    }

    /**
     * Returns all profiles owned by a player, in creation order.
     *
     * @param playerId the unique id of the player
     * @return an unmodifiable list of the player's profiles, empty if none
     */
    public List<Profile> getProfiles(UUID playerId) {
        Map<String, Profile> profiles = profilesByPlayer.get(playerId);
        return profiles == null
                ? Collections.emptyList()
                : List.copyOf(profiles.values());
    }

    /**
     * Returns a player's currently active profile.
     *
     * @param playerId the unique id of the player
     * @return the active profile, or {@code null} if the player has no profiles
     */
    public Profile getActiveProfile(UUID playerId) {
        String name = activeProfiles.get(playerId);
        return name == null ? null : getProfile(playerId, name);
    }

    /**
     * Switches a player's active profile.
     *
     * @param playerId the unique id of the player
     * @param name     the name of the profile to activate
     * @throws IllegalArgumentException if the player has no profile with the given name
     */
    public void setActiveProfile(UUID playerId, String name) {
        if (getProfile(playerId, name) == null) {
            throw new IllegalArgumentException("player has no profile named: " + name);
        }
        activeProfiles.put(playerId, name);
    }

    /**
     * Deletes a player's profile.
     *
     * <p>If the deleted profile was active, the player's oldest remaining
     * profile becomes active instead.</p>
     *
     * @param playerId the unique id of the player
     * @param name     the name of the profile to delete
     * @return {@code true} if the player had the profile and it has been deleted
     */
    public boolean deleteProfile(UUID playerId, String name) {
        Map<String, Profile> profiles = profilesByPlayer.get(playerId);
        if (profiles == null || profiles.remove(name) == null) {
            return false;
        }
        if (profiles.isEmpty()) {
            profilesByPlayer.remove(playerId);
            activeProfiles.remove(playerId);
        } else if (name.equals(activeProfiles.get(playerId))) {
            activeProfiles.put(playerId, profiles.keySet().iterator().next());
        }
        return true;
    }

    /**
     * Returns the number of profiles owned by a player.
     *
     * @param playerId the unique id of the player
     * @return the player's profile count
     */
    public int getProfileCount(UUID playerId) {
        Map<String, Profile> profiles = profilesByPlayer.get(playerId);
        return profiles == null ? 0 : profiles.size();
    }

    private static void requirePlayerId(UUID playerId) {
        if (playerId == null) {
            throw new IllegalArgumentException("playerId must not be null");
        }
    }
}
