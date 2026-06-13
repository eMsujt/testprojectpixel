package com.skyblock.core.title;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton managing per-player active cosmetic titles with YAML persistence.
 */
public final class TitleManager {

    private static final TitleManager INSTANCE = new TitleManager();

    /** Active title per player (UUID → title string). */
    private final Map<UUID, String> titles = new HashMap<>();

    private TitleManager() {}

    public static TitleManager getInstance() {
        return INSTANCE;
    }

    public void setTitle(UUID player, String title) {
        titles.put(player, title);
    }

    public void clearTitle(UUID player) {
        titles.remove(player);
    }

    public String getTitle(UUID player) {
        return titles.get(player);
    }

    public boolean hasTitle(UUID player) {
        return titles.containsKey(player);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "titles.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        titles.clear();
        if (cfg.isConfigurationSection("titles")) {
            for (String key : cfg.getConfigurationSection("titles").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    String title = cfg.getString("titles." + key);
                    if (title != null && !title.isEmpty()) {
                        titles.put(uuid, title);
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "titles.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, String> entry : titles.entrySet()) {
            cfg.set("titles." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save titles.yml", e);
        }
    }
}
