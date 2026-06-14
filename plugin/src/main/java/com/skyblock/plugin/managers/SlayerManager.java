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
    private final Map<UUID, Map<String, Long>> slayerXp = new HashMap<>();

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

    public long getSlayerXp(UUID playerId, String bossType) {
        return slayerXp.getOrDefault(playerId, Collections.emptyMap()).getOrDefault(bossType, 0L);
    }

    public void setSlayerXp(UUID playerId, String bossType, long amount) {
        slayerXp.computeIfAbsent(playerId, k -> new HashMap<>()).put(bossType, Math.max(0L, amount));
    }

    public void addSlayerXp(UUID playerId, String bossType, long amount) {
        setSlayerXp(playerId, bossType, getSlayerXp(playerId, bossType) + amount);
    }

    public Map<String, Long> getSlayerXp(UUID playerId) {
        return Collections.unmodifiableMap(slayerXp.getOrDefault(playerId, Collections.emptyMap()));
    }

    public Map<UUID, Map<String, Long>> getAllSlayerXp() {
        return Collections.unmodifiableMap(slayerXp);
    }

    public Map<UUID, Long> getAllPlayerXpForType(String bossType) {
        Map<UUID, Long> result = new HashMap<>();
        for (Map.Entry<UUID, Map<String, Long>> entry : slayerXp.entrySet()) {
            long xp = entry.getValue().getOrDefault(bossType, 0L);
            if (xp > 0) {
                result.put(entry.getKey(), xp);
            }
        }
        return result;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "slayer.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        killCounts.clear();
        slayerXp.clear();
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
        if (cfg.isConfigurationSection("slayerXp")) {
            for (String playerKey : cfg.getConfigurationSection("slayerXp").getKeys(false)) {
                try {
                    UUID playerId = UUID.fromString(playerKey);
                    Map<String, Long> xpMap = new HashMap<>();
                    String path = "slayerXp." + playerKey;
                    if (cfg.isConfigurationSection(path)) {
                        for (String bossType : cfg.getConfigurationSection(path).getKeys(false)) {
                            xpMap.put(bossType, cfg.getLong(path + "." + bossType));
                        }
                    }
                    slayerXp.put(playerId, xpMap);
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
        for (Map.Entry<UUID, Map<String, Long>> entry : slayerXp.entrySet()) {
            String path = "slayerXp." + entry.getKey().toString();
            for (Map.Entry<String, Long> xp : entry.getValue().entrySet()) {
                cfg.set(path + "." + xp.getKey(), xp.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save slayer.yml", e);
        }
    }
}
