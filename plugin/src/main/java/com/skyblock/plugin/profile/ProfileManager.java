package com.skyblock.plugin.profile;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton in-memory registry of {@link PlayerProfile} instances keyed by
 * player UUID.
 *
 * <p>On {@link PlayerJoinEvent} the joining player's profile is loaded from the
 * registry, or a new empty one is created and registered if none exists yet.</p>
 *
 * <p>The profile map is mutated only on the server main thread; access it from
 * the main thread or guard it externally.</p>
 */
public final class ProfileManager implements Listener {

    private static final ProfileManager INSTANCE = new ProfileManager();

    private final Map<UUID, PlayerProfile> profiles = new HashMap<>();

    private ProfileManager() {}

    public static ProfileManager getInstance() {
        return INSTANCE;
    }

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

    /**
     * Loads the joining player's profile, creating a new one if none exists.
     *
     * @param event the join event
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        getOrCreate(event.getPlayer().getUniqueId());
    }
}
