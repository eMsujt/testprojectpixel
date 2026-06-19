package com.skyblock.core.manager;

import com.skyblock.core.model.PlayerProfile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton registry of per-player {@link PlayerProfile} state, keyed by UUID.
 * Profiles are created lazily on first access.
 */
public final class ProfileManager {

    private static final ProfileManager INSTANCE = new ProfileManager();

    private final Map<UUID, PlayerProfile> profiles = new HashMap<>();

    private ProfileManager() {}

    public static ProfileManager getInstance() {
        return INSTANCE;
    }

    /** Returns the player's profile, creating an empty one if absent. */
    public PlayerProfile getProfile(UUID playerId) {
        return profiles.computeIfAbsent(playerId, PlayerProfile::new);
    }

    public boolean hasProfile(UUID playerId) {
        return profiles.containsKey(playerId);
    }

    public PlayerProfile removeProfile(UUID playerId) {
        return profiles.remove(playerId);
    }
}
