package com.skyblock.plugin.manager;

import com.skyblock.plugin.profile.SkyBlockProfile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * In-memory registry of every loaded player's SkyBlock profile.
 *
 * <p>Holds one {@link SkyBlockProfile} per player keyed by the player's UUID, so
 * a profile can be resolved, created and discarded as players join and leave.
 * Not thread-safe; access from the main server thread.</p>
 */
/** @deprecated Use {@link com.skyblock.core.manager.ProfileManager} instead. */
@Deprecated
public final class ProfileManager {

    private static final ProfileManager INSTANCE = new ProfileManager();

    private final Map<UUID, SkyBlockProfile> profiles = new HashMap<>();

    private ProfileManager() {
    }

    public static ProfileManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the loaded profile for the given player, or {@code null} if none
     * has been loaded.
     *
     * @param uuid the player's UUID
     * @return the tracked profile, or {@code null} if absent
     */
    public SkyBlockProfile getProfile(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return profiles.get(uuid);
    }

    /**
     * Returns the loaded profile for the given player, creating and registering
     * a fresh one if none exists yet.
     *
     * @param uuid the player's UUID
     * @return the existing or newly created profile, never {@code null}
     */
    public SkyBlockProfile getOrCreateProfile(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return profiles.computeIfAbsent(uuid, SkyBlockProfile::new);
    }

    /**
     * Registers a profile, replacing any previously tracked profile for the same
     * player.
     *
     * @param profile the profile to track
     */
    public void addProfile(SkyBlockProfile profile) {
        Objects.requireNonNull(profile, "profile");
        profiles.put(profile.getUuid(), profile);
    }

    /**
     * Stops tracking the given player's profile.
     *
     * @param uuid the player's UUID
     * @return the removed profile, or {@code null} if none was tracked
     */
    public SkyBlockProfile removeProfile(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return profiles.remove(uuid);
    }

    /**
     * Returns an unmodifiable view of every loaded profile, keyed by player UUID.
     *
     * @return all tracked profiles
     */
    public Map<UUID, SkyBlockProfile> getProfiles() {
        return Collections.unmodifiableMap(profiles);
    }
}
