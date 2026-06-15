package com.skyblock.core.profile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Comparator;

/** @deprecated Use {@link com.skyblock.core.manager.ProfileManager} instead. */
@Deprecated
public final class ProfileManager {

    private final Map<UUID, Map<String, ProfileData>> profiles = new HashMap<>();

    public boolean createProfile(UUID uuid, String name) {
        Map<String, ProfileData> playerProfiles = profiles.computeIfAbsent(uuid, k -> new HashMap<>());
        if (playerProfiles.containsKey(name.toLowerCase())) {
            return false;
        }
        playerProfiles.put(name.toLowerCase(), new ProfileData(name));
        return true;
    }

    public boolean deleteProfile(UUID uuid, String name) {
        Map<String, ProfileData> playerProfiles = profiles.get(uuid);
        if (playerProfiles == null) {
            return false;
        }
        return playerProfiles.remove(name.toLowerCase()) != null;
    }

    public ProfileData getProfile(UUID uuid, String name) {
        Map<String, ProfileData> playerProfiles = profiles.get(uuid);
        if (playerProfiles == null) {
            return null;
        }
        return playerProfiles.get(name.toLowerCase());
    }

    public Map<String, ProfileData> getProfiles(UUID uuid) {
        return Collections.unmodifiableMap(profiles.computeIfAbsent(uuid, k -> new HashMap<>()));
    }

    public boolean hasProfile(UUID uuid, String name) {
        Map<String, ProfileData> playerProfiles = profiles.get(uuid);
        return playerProfiles != null && playerProfiles.containsKey(name.toLowerCase());
    }

    public Set<String> getProfileNames(UUID uuid) {
        Map<String, ProfileData> playerProfiles = profiles.get(uuid);
        if (playerProfiles == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(playerProfiles.keySet());
    }

    public String getProfileStats(UUID uuid) {
        Map<String, ProfileData> playerProfiles = profiles.get(uuid);
        int count = playerProfiles == null ? 0 : playerProfiles.size();
        if (playerProfiles == null || playerProfiles.isEmpty()) {
            return "Profile Stats: Profiles: " + count;
        }
        ProfileData active = playerProfiles.values().stream()
                .min(Comparator.comparing(ProfileData::getName))
                .get();
        return "Profile Stats: Active: " + active.getName() + " | Game Mode: " + active.getGameMode()
                + " | Coins: " + active.getCoinsBalance() + " | Profiles: " + count;
    }

    public static final class ProfileData {
        private final String name;
        private String gameMode;
        private long coinsBalance;

        public ProfileData(String name) {
            this.name = name;
            this.gameMode = "normal";
            this.coinsBalance = 0L;
        }

        public String getName() { return name; }
        public String getGameMode() { return gameMode; }
        public void setGameMode(String gameMode) { this.gameMode = gameMode; }
        public long getCoinsBalance() { return coinsBalance; }
        public void setCoinsBalance(long coinsBalance) { this.coinsBalance = Math.max(0, coinsBalance); }
    }
}
