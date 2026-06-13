package com.skyblock.core.profile;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
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

    /** Canonical game mode for a SkyBlock profile. */
    public enum SkyBlockGameMode {
        CLASSIC("Classic"),
        IRONMAN("Ironman"),
        STRANDED("Stranded"),
        BINGO("Bingo");

        public final String displayName;

        SkyBlockGameMode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    /** Profile mode selected when creating a SkyBlock profile. */
    public enum ProfileMode {
        CLASSIC("Classic"),
        IRONMAN("Ironman"),
        STRANDED("Stranded"),
        BINGO("Bingo");

        public final String displayName;

        ProfileMode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

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
     * Per-player profile data.
     *
     * @param profileName display name for this profile
     * @param createdAt   creation timestamp in milliseconds since epoch
     * @param stats       mutable stat map (stat key → value)
     */
    public record ProfileData(String profileName, long createdAt, Map<String, Double> stats) {
        public ProfileData {
            Objects.requireNonNull(profileName, "profileName");
            if (profileName.isBlank()) {
                throw new IllegalArgumentException("profileName must not be blank");
            }
            Objects.requireNonNull(stats, "stats");
        }
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

    /** uuid -> player account data */
    private final Map<UUID, ProfileData> playerData = new HashMap<>();

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

    /**
     * Stores or replaces account-level data for a player.
     *
     * @param data the data to store; must not be {@code null}
     */
    public void setPlayerData(UUID uuid, ProfileData data) {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(data, "data");
        playerData.put(uuid, data);
    }

    /**
     * Returns the account-level data for the given player, or {@code null} if not found.
     *
     * @param uuid the player's unique id
     * @return the player's {@link ProfileData}, or {@code null}
     */
    public ProfileData getPlayerData(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return playerData.get(uuid);
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "profile.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        playerData.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String name = cfg.getString(key + ".profileName");
                if (name == null) {
                    continue;
                }
                long createdAt = cfg.getLong(key + ".createdAt", 0L);
                Map<String, Double> stats = new HashMap<>();
                if (cfg.isConfigurationSection(key + ".stats")) {
                    for (String stat : cfg.getConfigurationSection(key + ".stats").getKeys(false)) {
                        stats.put(stat, cfg.getDouble(key + ".stats." + stat));
                    }
                }
                playerData.put(uuid, new ProfileData(name, createdAt, stats));
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "profile.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, ProfileData> entry : playerData.entrySet()) {
            String key = entry.getKey().toString();
            ProfileData data = entry.getValue();
            cfg.set(key + ".profileName", data.profileName());
            cfg.set(key + ".createdAt", data.createdAt());
            for (Map.Entry<String, Double> stat : data.stats().entrySet()) {
                cfg.set(key + ".stats." + stat.getKey(), stat.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save profile.yml", e);
        }
    }

    /** Removes all registered profiles. */
    public void clear() {
        profilesById.clear();
        profilesByOwner.clear();
        playerData.clear();
    }
}
