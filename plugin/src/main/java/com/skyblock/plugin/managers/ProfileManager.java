package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class ProfileManager {

    private static final ProfileManager INSTANCE = new ProfileManager();

    private final Map<UUID, String> activeProfile = new HashMap<>();
    private final Map<UUID, List<String>> profileHistory = new HashMap<>();

    private ProfileManager() {}

    public static ProfileManager getInstance() {
        return INSTANCE;
    }

    public String getActiveProfile(UUID playerId) {
        return activeProfile.getOrDefault(playerId, "default");
    }

    public void setActiveProfile(UUID playerId, String profileName) {
        activeProfile.put(playerId, profileName);
    }

    public void clearActiveProfile(UUID playerId) {
        activeProfile.remove(playerId);
    }

    public Map<UUID, String> getActiveProfiles() {
        return Collections.unmodifiableMap(activeProfile);
    }

    public void recordProfileEvent(UUID playerId, String summary) {
        profileHistory.computeIfAbsent(playerId, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getProfileHistory(UUID playerId) {
        return Collections.unmodifiableList(
                profileHistory.getOrDefault(playerId, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllProfileHistory() {
        return Collections.unmodifiableMap(profileHistory);
    }

    public String getProfileStats(UUID playerId) {
        String active = activeProfile.getOrDefault(playerId, "none");
        int events = profileHistory.getOrDefault(playerId, Collections.emptyList()).size();
        return "Profile Stats: Active: " + active + " | History Events: " + events;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "profiles.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        activeProfile.clear();
        profileHistory.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String name = cfg.getString(key);
                if (name != null && !name.isEmpty()) {
                    activeProfile.put(uuid, name);
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed UUID
            }
        }
        if (cfg.isConfigurationSection("profileHistory")) {
            for (String key : cfg.getConfigurationSection("profileHistory").getKeys(false)) {
                try {
                    profileHistory.put(UUID.fromString(key),
                            new ArrayList<>(cfg.getStringList("profileHistory." + key)));
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "profiles.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, String> entry : activeProfile.entrySet()) {
            cfg.set(entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, List<String>> entry : profileHistory.entrySet()) {
            cfg.set("profileHistory." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save profiles.yml", e);
        }
    }
}
