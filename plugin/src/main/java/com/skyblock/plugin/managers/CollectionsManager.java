package com.skyblock.plugin.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CollectionsManager {

    private static final CollectionsManager INSTANCE = new CollectionsManager();

    private final Map<UUID, Map<String, Long>> collectionCounts = new HashMap<>();

    private CollectionsManager() {}

    public static CollectionsManager getInstance() {
        return INSTANCE;
    }

    public long getCollectionCount(UUID playerId, String collection) {
        Map<String, Long> counts = collectionCounts.get(playerId);
        if (counts == null) return 0L;
        return counts.getOrDefault(collection, 0L);
    }

    public void addCollectionCount(UUID playerId, String collection, long amount) {
        collectionCounts
                .computeIfAbsent(playerId, k -> new HashMap<>())
                .merge(collection, amount, Long::sum);
    }

    public void setCollectionCount(UUID playerId, String collection, long amount) {
        collectionCounts
                .computeIfAbsent(playerId, k -> new HashMap<>())
                .put(collection, amount);
    }

    public Map<String, Long> getCollectionCounts(UUID playerId) {
        return collectionCounts.getOrDefault(playerId, new HashMap<>());
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "collections.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        collectionCounts.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                ConfigurationSection section = cfg.getConfigurationSection(key);
                if (section == null) continue;
                Map<String, Long> counts = new HashMap<>();
                for (String collection : section.getKeys(false)) {
                    counts.put(collection, section.getLong(collection));
                }
                collectionCounts.put(uuid, counts);
            } catch (IllegalArgumentException ignored) {
                // skip malformed UUID
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "collections.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<String, Long>> entry : collectionCounts.entrySet()) {
            String uuidKey = entry.getKey().toString();
            for (Map.Entry<String, Long> countEntry : entry.getValue().entrySet()) {
                cfg.set(uuidKey + "." + countEntry.getKey(), countEntry.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save collections.yml", e);
        }
    }
}
