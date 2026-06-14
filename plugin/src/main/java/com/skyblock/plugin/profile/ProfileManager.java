package com.skyblock.plugin.profile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * In-memory registry of {@link PlayerProfile} instances keyed by player UUID.
 *
 * <p>Instances are not thread-safe; access them from the server main thread or
 * guard them externally.</p>
 */
public final class ProfileManager {

    private final Map<UUID, PlayerProfile> profiles = new HashMap<>();

    /**
     * Returns the profile for the given player, creating and registering a new
     * empty one if none exists yet.
     *
     * @param uuid unique identifier of the player
     * @return the player's profile, never {@code null}
     */
    public PlayerProfile getOrCreate(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return profiles.computeIfAbsent(uuid, PlayerProfile::new);
    }

    /**
     * Returns the profile for the given player, or {@code null} if none has
     * been registered.
     *
     * @param uuid unique identifier of the player
     * @return the player's profile, or {@code null}
     */
    public PlayerProfile getProfile(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return profiles.get(uuid);
    }

    /**
     * Returns whether a profile is registered for the given player.
     *
     * @param uuid unique identifier of the player
     * @return {@code true} if a profile exists
     */
    public boolean hasProfile(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return profiles.containsKey(uuid);
    }

    /**
     * Removes the profile for the given player.
     *
     * @param uuid unique identifier of the player
     * @return the removed profile, or {@code null} if none existed
     */
    public PlayerProfile removeProfile(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return profiles.remove(uuid);
    }

    /**
     * Returns an immutable snapshot of all registered profiles keyed by UUID.
     *
     * @return the registered profiles
     */
    public Map<UUID, PlayerProfile> getProfiles() {
        return Collections.unmodifiableMap(profiles);
    }
}
