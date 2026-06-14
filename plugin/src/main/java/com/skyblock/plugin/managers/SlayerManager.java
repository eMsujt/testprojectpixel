package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SlayerManager {

    private static final SlayerManager INSTANCE = new SlayerManager();

    private final Map<UUID, Map<String, Long>> killCounts = new HashMap<>();

    private SlayerManager() {}

    public static SlayerManager getInstance() {
        return INSTANCE;
    }

    public Map<String, Long> getKillCounts(UUID playerId) {
        return killCounts.computeIfAbsent(playerId, k -> new HashMap<>());
    }

    public long getKillCount(UUID playerId, String bossType) {
        return getKillCounts(playerId).getOrDefault(bossType, 0L);
    }

    public void addKill(UUID playerId, String bossType) {
        getKillCounts(playerId).merge(bossType, 1L, Long::sum);
    }

    public void setKillCount(UUID playerId, String bossType, long count) {
        getKillCounts(playerId).put(bossType, count);
    }

    public Map<UUID, Map<String, Long>> getKillCounts() {
        return Collections.unmodifiableMap(killCounts);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "slayer.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        killCounts.clear();
        if (cfg.isConfigurationSection("kills")) {
            for (String playerKey : cfg.getConfigurationSection("kills").getKeys(false)) {
                try {
                    UUID playerId = UUID.fromString(playerKey);
                    Map<String, Long> counts = new HashMap<>();
                    String path = "kills." + playerKey;
                    if (cfg.isConfigurationSection(path)) {
                        for (String bossType : cfg.getConfigurationSection(path).getKeys(false)) {
                            counts.put(bossType, cfg.getLong(path + "." + bossType));
                        }
                    }
                    killCounts.put(playerId, counts);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "slayer.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<String, Long>> entry : killCounts.entrySet()) {
            String path = "kills." + entry.getKey().toString();
            for (Map.Entry<String, Long> kill : entry.getValue().entrySet()) {
                cfg.set(path + "." + kill.getKey(), kill.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save slayer.yml", e);
        }
    }
}
