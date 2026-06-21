package com.skyblock.core.manager;

import com.skyblock.core.model.PlayerProfile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class ProfileManager {

    /**
     * Immutable header record identifying a profile slot.
     *
     * @param profileId unique identifier for this profile slot
     * @param playerId  UUID of the owning player
     * @param name      display name for the profile (e.g. "Mango")
     */
    public record Profile(UUID profileId, UUID playerId, String name) {
        public Profile {
            Objects.requireNonNull(profileId, "profileId");
            Objects.requireNonNull(playerId, "playerId");
            Objects.requireNonNull(name, "name");
            if (name.isBlank()) throw new IllegalArgumentException("name must not be blank");
        }
    }

    private static final ProfileManager INSTANCE = new ProfileManager();

    /** playerId → PlayerProfile */
    private final Map<UUID, PlayerProfile> profiles = new HashMap<>();

    private ProfileManager() {}

    public static ProfileManager getInstance() {
        return INSTANCE;
    }

    /** Returns the {@link PlayerProfile} for {@code playerId}, creating one if absent. */
    public PlayerProfile getOrCreate(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return profiles.computeIfAbsent(playerId, PlayerProfile::new);
    }

    /** Returns the {@link PlayerProfile} for {@code playerId}, or {@code null} if none. */
    public PlayerProfile get(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return profiles.get(playerId);
    }

    /** Registers a pre-built {@link PlayerProfile}. Replaces any existing entry. */
    public void put(PlayerProfile profile) {
        Objects.requireNonNull(profile, "profile");
        profiles.put(profile.getUuid(), profile);
    }

    /** Removes and returns the profile for {@code playerId}, or {@code null} if absent. */
    public PlayerProfile remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return profiles.remove(playerId);
    }

    /** Returns an unmodifiable view of all loaded profiles keyed by player UUID. */
    public Map<UUID, PlayerProfile> getAllProfiles() {
        return Collections.unmodifiableMap(profiles);
    }

    /** Clears all loaded profiles. */
    public void clear() {
        profiles.clear();
    }
}
