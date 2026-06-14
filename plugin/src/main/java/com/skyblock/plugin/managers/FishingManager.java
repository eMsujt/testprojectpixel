package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class FishingManager {

    private static final FishingManager INSTANCE = new FishingManager();

    private final Map<UUID, Integer> fishingXp = new HashMap<>();
    private final Map<UUID, Map<String, Long>> fishCounts = new HashMap<>();
    private final Map<UUID, List<String>> catchHistory = new ConcurrentHashMap<>();

    private FishingManager() {}

    public static FishingManager getInstance() {
        return INSTANCE;
    }

    public int getFishingXp(UUID playerId) {
        return fishingXp.getOrDefault(playerId, 0);
    }

    public void addFishingXp(UUID playerId, int amount) {
        fishingXp.put(playerId, getFishingXp(playerId) + amount);
    }

    public void setFishingXp(UUID playerId, int amount) {
        fishingXp.put(playerId, amount);
    }

    public long getFishCount(UUID playerId, String fishType) {
        return fishCounts.getOrDefault(playerId, Collections.emptyMap()).getOrDefault(fishType, 0L);
    }

    public void setFishCount(UUID playerId, String fishType, long count) {
        fishCounts.computeIfAbsent(playerId, k -> new HashMap<>()).put(fishType, Math.max(0L, count));
    }

    public void addFishCount(UUID playerId, String fishType, long amount) {
        setFishCount(playerId, fishType, getFishCount(playerId, fishType) + amount);
    }

    public Map<String, Long> getFishCounts(UUID playerId) {
        return Collections.unmodifiableMap(fishCounts.getOrDefault(playerId, Collections.emptyMap()));
    }

    public Map<UUID, Map<String, Long>> getAllFishCounts() {
        return Collections.unmodifiableMap(fishCounts);
    }

    public void recordCatchEvent(UUID playerId, String summary) {
        catchHistory.computeIfAbsent(playerId, k -> new CopyOnWriteArrayList<>()).add(summary);
    }

    public List<String> getCatchHistory(UUID playerId) {
        return Collections.unmodifiableList(catchHistory.getOrDefault(playerId, new CopyOnWriteArrayList<>()));
    }

    public String getFishingStats(UUID playerId) {
        int xp = getFishingXp(playerId);
        List<String> history = getCatchHistory(playerId);
        int common = 0, uncommon = 0, rare = 0, epic = 0, legendary = 0;
        for (String entry : history) {
            String upper = entry.toUpperCase();
            if (upper.contains("LEGENDARY")) legendary++;
            else if (upper.contains("EPIC")) epic++;
            else if (upper.contains("RARE")) rare++;
            else if (upper.contains("UNCOMMON")) uncommon++;
            else if (upper.contains("COMMON")) common++;
        }
        long total = 0;
        for (long v : getFishCounts(playerId).values()) total += v;
        return "Fishing Stats: Common: " + common + " | Uncommon: " + uncommon
                + " | Rare: " + rare + " | Epic: " + epic + " | Legendary: " + legendary
                + " | Total: " + total + " | XP: " + xp;
    }

    public Map<UUID, List<String>> getAllCatchHistory() {
        Map<UUID, List<String>> copy = new HashMap<>();
        for (Map.Entry<UUID, List<String>> entry : catchHistory.entrySet()) {
            copy.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
        }
        return Collections.unmodifiableMap(copy);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "fishing.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        fishingXp.clear();
        fishCounts.clear();
        catchHistory.clear();
        if (cfg.isConfigurationSection("xp")) {
            for (String key : cfg.getConfigurationSection("xp").getKeys(false)) {
                try {
                    fishingXp.put(UUID.fromString(key), cfg.getInt("xp." + key));
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUID
                }
            }
        }
        if (cfg.isConfigurationSection("fishCounts")) {
            for (String uuidKey : cfg.getConfigurationSection("fishCounts").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidKey);
                    Map<String, Long> counts = new HashMap<>();
                    if (cfg.isConfigurationSection("fishCounts." + uuidKey)) {
                        for (String fishType : cfg.getConfigurationSection("fishCounts." + uuidKey).getKeys(false)) {
                            counts.put(fishType, cfg.getLong("fishCounts." + uuidKey + "." + fishType));
                        }
                    }
                    fishCounts.put(uuid, counts);
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUID
                }
            }
        }
        if (cfg.isConfigurationSection("catchHistory")) {
            for (String key : cfg.getConfigurationSection("catchHistory").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    List<String> events = cfg.getStringList("catchHistory." + key);
                    if (!events.isEmpty()) {
                        catchHistory.put(uuid, new ArrayList<>(events));
                    }
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUID
                }
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "fishing.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : fishingXp.entrySet()) {
            cfg.set("xp." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Map<String, Long>> entry : fishCounts.entrySet()) {
            String uuidKey = entry.getKey().toString();
            for (Map.Entry<String, Long> fish : entry.getValue().entrySet()) {
                cfg.set("fishCounts." + uuidKey + "." + fish.getKey(), fish.getValue());
            }
        }
        for (Map.Entry<UUID, List<String>> entry : catchHistory.entrySet()) {
            cfg.set("catchHistory." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save fishing.yml", e);
        }
    }
}
