package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class FishingManager {

    private static final FishingManager INSTANCE = new FishingManager();

    private final Map<UUID, Map<String, Integer>> catchCounts = new HashMap<>();

    private FishingManager() {}

    public static FishingManager getInstance() {
        return INSTANCE;
    }

    public Map<String, Integer> getCatchCounts(UUID uuid) {
        return catchCounts.computeIfAbsent(uuid, k -> new HashMap<>());
    }

    public int getCatchCount(UUID uuid, String fish) {
        return catchCounts.getOrDefault(uuid, new HashMap<>()).getOrDefault(fish, 0);
    }

    public void incrementCatch(UUID uuid, String fish) {
        getCatchCounts(uuid).merge(fish, 1, Integer::sum);
    }

    public void setCatchCount(UUID uuid, String fish, int count) {
        getCatchCounts(uuid).put(fish, count);
    }

    public void removePlayer(UUID uuid) {
        catchCounts.remove(uuid);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "fishing.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        catchCounts.clear();
        if (cfg.isConfigurationSection("catchCounts")) {
            for (String uuidStr : cfg.getConfigurationSection("catchCounts").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                Map<String, Integer> counts = new HashMap<>();
                String path = "catchCounts." + uuidStr;
                if (cfg.isConfigurationSection(path)) {
                    for (String fish : cfg.getConfigurationSection(path).getKeys(false)) {
                        counts.put(fish, cfg.getInt(path + "." + fish));
                    }
                }
                catchCounts.put(uuid, counts);
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "fishing.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<String, Integer>> playerEntry : catchCounts.entrySet()) {
            String uuidStr = playerEntry.getKey().toString();
            for (Map.Entry<String, Integer> fishEntry : playerEntry.getValue().entrySet()) {
                cfg.set("catchCounts." + uuidStr + "." + fishEntry.getKey(), fishEntry.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save fishing.yml", e);
        }
    }
}
