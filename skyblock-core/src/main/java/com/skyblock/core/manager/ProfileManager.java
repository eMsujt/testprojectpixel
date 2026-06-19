package com.skyblock.core.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class ProfileManager {

    public enum ProfileMode {
        NORMAL,
        IRONMAN,
        STRANDED,
        BINGO
    }

    public static final class PlayerProfile {
        private final UUID owner;
        private final String name;
        private final ProfileMode mode;
        private int level;
        private long experience;
        private long coins;

        public PlayerProfile(UUID owner, String name, ProfileMode mode) {
            this.owner = Objects.requireNonNull(owner, "owner");
            this.name = Objects.requireNonNull(name, "name");
            this.mode = Objects.requireNonNull(mode, "mode");
        }

        public UUID getOwner() { return owner; }
        public String getName() { return name; }
        public ProfileMode getMode() { return mode; }
        public int getLevel() { return level; }
        public long getExperience() { return experience; }
        public long getCoins() { return coins; }

        public void setLevel(int level) { this.level = level; }
        public void setExperience(long experience) { this.experience = experience; }
        public void setCoins(long coins) { this.coins = coins; }

        public void addExperience(long amount) { this.experience += amount; }
        public void addCoins(long amount) { this.coins += amount; }
    }

    private static final ProfileManager INSTANCE = new ProfileManager();

    /** profileId -> profile */
    private final Map<UUID, PlayerProfile> profilesById = new HashMap<>();

    /** ownerId -> list of profile ids */
    private final Map<UUID, List<UUID>> profilesByOwner = new HashMap<>();

    /** ownerId -> active profile name */
    private final Map<UUID, String> activeProfile = new HashMap<>();

    /** ownerId -> list of profile names */
    private final Map<UUID, List<String>> profiles = new HashMap<>();

    private ProfileManager() {}

    public static ProfileManager getInstance() {
        return INSTANCE;
    }

    public PlayerProfile createProfile(UUID owner, String name, ProfileMode mode) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(mode, "mode");
        PlayerProfile profile = new PlayerProfile(owner, name, mode);
        UUID profileId = UUID.randomUUID();
        profilesById.put(profileId, profile);
        profilesByOwner.computeIfAbsent(owner, k -> new ArrayList<>()).add(profileId);
        return profile;
    }

    public PlayerProfile getProfile(UUID profileId) {
        Objects.requireNonNull(profileId, "profileId");
        return profilesById.get(profileId);
    }

    public List<PlayerProfile> getProfilesForOwner(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        List<UUID> ids = profilesByOwner.getOrDefault(owner, Collections.emptyList());
        List<PlayerProfile> result = new ArrayList<>(ids.size());
        for (UUID id : ids) {
            PlayerProfile p = profilesById.get(id);
            if (p != null) {
                result.add(p);
            }
        }
        return Collections.unmodifiableList(result);
    }

    public boolean deleteProfile(UUID profileId) {
        Objects.requireNonNull(profileId, "profileId");
        PlayerProfile removed = profilesById.remove(profileId);
        if (removed == null) {
            return false;
        }
        List<UUID> ownerProfiles = profilesByOwner.get(removed.getOwner());
        if (ownerProfiles != null) {
            ownerProfiles.remove(profileId);
        }
        return true;
    }

    public String getActiveProfile(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        return activeProfile.get(owner);
    }

    public void setActiveProfile(UUID owner, String profileName) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(profileName, "profileName");
        activeProfile.put(owner, profileName);
    }

    public List<String> getProfiles(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        return Collections.unmodifiableList(profiles.getOrDefault(owner, Collections.emptyList()));
    }

    public void addProfile(UUID owner, String profileName) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(profileName, "profileName");
        profiles.computeIfAbsent(owner, k -> new ArrayList<>()).add(profileName);
    }

    public void removeProfileName(UUID owner, String profileName) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(profileName, "profileName");
        List<String> list = profiles.get(owner);
        if (list != null) {
            list.remove(profileName);
        }
    }

    public void clear() {
        profilesById.clear();
        profilesByOwner.clear();
        activeProfile.clear();
        profiles.clear();
    }
}
