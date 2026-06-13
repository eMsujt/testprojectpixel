package com.skyblock.core.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing SkyBlock profiles.
 *
 * <p>Each player may own multiple profiles (like Hypixel SkyBlock co-op islands).
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class ProfileManager {

    public enum GameMode {
        NORMAL("Normal"),
        IRONMAN("Ironman"),
        BINGO("Bingo");

        private final String displayName;

        GameMode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    /**
     * A single SkyBlock profile owned by a player.
     *
     * @param profileId unique identifier for this profile
     * @param ownerId   UUID of the player who created the profile
     * @param name      display name for the profile (e.g. "Mango", "Strawberry")
     * @param gameMode  game mode for this profile
     */
    public record SkyBlockProfile(UUID profileId, UUID ownerId, String name, GameMode gameMode) {
        public SkyBlockProfile {
            Objects.requireNonNull(profileId, "profileId");
            Objects.requireNonNull(ownerId, "ownerId");
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(gameMode, "gameMode");
            if (name.isBlank()) {
                throw new IllegalArgumentException("name must not be blank");
            }
        }
    }

    public static final int MAX_PROFILES = 4;

    private static final ProfileManager INSTANCE = new ProfileManager();

    /** profileId -> profile */
    private final Map<UUID, SkyBlockProfile> profilesById = new HashMap<>();

    /** ownerId -> list of profile ids */
    private final Map<UUID, List<UUID>> profilesByOwner = new HashMap<>();

    private ProfileManager() {}

    /**
     * Returns the single shared {@code ProfileManager} instance.
     *
     * @return the singleton instance
     */
    public static ProfileManager getInstance() {
        return INSTANCE;
    }

    /**
     * Creates and registers a new profile for the given owner, up to a maximum of
     * {@value #MAX_PROFILES} profiles per player.
     *
     * @param ownerId  UUID of the owning player
     * @param name     display name for the new profile
     * @param gameMode game mode for the new profile
     * @return the newly created {@link SkyBlockProfile}, or {@code null} if the player
     *         already holds {@value #MAX_PROFILES} profiles
     */
    public SkyBlockProfile createProfile(UUID ownerId, String name, GameMode gameMode) {
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(gameMode, "gameMode");
        List<UUID> ownerIds = profilesByOwner.computeIfAbsent(ownerId, k -> new ArrayList<>());
        if (ownerIds.size() >= MAX_PROFILES) {
            return null;
        }
        SkyBlockProfile profile = new SkyBlockProfile(UUID.randomUUID(), ownerId, name, gameMode);
        profilesById.put(profile.profileId(), profile);
        ownerIds.add(profile.profileId());
        return profile;
    }

    /**
     * Returns the profile with the given id, or {@code null} if not found.
     *
     * @param profileId the profile's unique id
     * @return the profile, or {@code null}
     */
    public SkyBlockProfile getProfile(UUID profileId) {
        Objects.requireNonNull(profileId, "profileId");
        return profilesById.get(profileId);
    }

    /**
     * Returns an unmodifiable list of all profiles owned by the given player.
     *
     * @param ownerId UUID of the player
     * @return the player's profiles, never {@code null}
     */
    public List<SkyBlockProfile> getProfilesForOwner(UUID ownerId) {
        Objects.requireNonNull(ownerId, "ownerId");
        List<UUID> ids = profilesByOwner.getOrDefault(ownerId, Collections.emptyList());
        List<SkyBlockProfile> result = new ArrayList<>(ids.size());
        for (UUID id : ids) {
            SkyBlockProfile p = profilesById.get(id);
            if (p != null) {
                result.add(p);
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Removes a profile by id.
     *
     * @param profileId the id of the profile to remove
     * @return {@code true} if the profile existed and was removed
     */
    public boolean deleteProfile(UUID profileId) {
        Objects.requireNonNull(profileId, "profileId");
        SkyBlockProfile removed = profilesById.remove(profileId);
        if (removed == null) {
            return false;
        }
        List<UUID> ownerProfiles = profilesByOwner.get(removed.ownerId());
        if (ownerProfiles != null) {
            ownerProfiles.remove(profileId);
        }
        return true;
    }

    /** Removes all registered profiles. */
    public void clear() {
        profilesById.clear();
        profilesByOwner.clear();
    }
}
