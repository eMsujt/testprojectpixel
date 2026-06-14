package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ProfileManager {

    private static final ProfileManager INSTANCE = new ProfileManager();

    private final Map<UUID, String> activeProfile = new HashMap<>();

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
        return java.util.Collections.unmodifiableMap(activeProfile);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "profiles.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        activeProfile.clear();
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
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "profiles.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, String> entry : activeProfile.entrySet()) {
            cfg.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save profiles.yml", e);
        }
    }
}
