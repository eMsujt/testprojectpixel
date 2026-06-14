package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class KuudraManager {

    private static final KuudraManager INSTANCE = new KuudraManager();

    private final Map<UUID, Integer> kuudraTier = new HashMap<>();
    private final Map<UUID, Integer> runsCompleted = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> tierCompletions = new HashMap<>();

    private KuudraManager() {}

    public static KuudraManager getInstance() {
        return INSTANCE;
    }

    public int getKuudraTier(UUID playerId) {
        return kuudraTier.getOrDefault(playerId, 1);
    }

    public void setKuudraTier(UUID playerId, int tier) {
        kuudraTier.put(playerId, Math.max(1, Math.min(5, tier)));
    }

    public void addKuudraTier(UUID playerId, int amount) {
        setKuudraTier(playerId, getKuudraTier(playerId) + amount);
    }

    public Map<UUID, Integer> getKuudraTiers() {
        return Collections.unmodifiableMap(kuudraTier);
    }

    public void trackRun(UUID playerId, int tier) {
        runsCompleted.merge(playerId, 1, Integer::sum);
        if (tier > getKuudraTier(playerId)) {
            setKuudraTier(playerId, tier);
        }
    }

    public int getRunsCompleted(UUID playerId) {
        return runsCompleted.getOrDefault(playerId, 0);
    }

    public Map<UUID, Integer> getAllRunsCompleted() {
        return Collections.unmodifiableMap(runsCompleted);
    }

    public void recordTierCompletion(UUID playerId, String tierName) {
        tierCompletions.computeIfAbsent(playerId, k -> new HashMap<>())
                .merge(tierName, 1, Integer::sum);
    }

    public Map<String, Integer> getTierCompletions(UUID playerId) {
        return Collections.unmodifiableMap(tierCompletions.getOrDefault(playerId, Collections.emptyMap()));
    }

    public Map<UUID, Map<String, Integer>> getAllTierCompletions() {
        return Collections.unmodifiableMap(tierCompletions);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "kuudra.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        kuudraTier.clear();
        if (cfg.isConfigurationSection("kuudraTier")) {
            for (String uuidKey : cfg.getConfigurationSection("kuudraTier").getKeys(false)) {
                try {
                    kuudraTier.put(UUID.fromString(uuidKey), cfg.getInt("kuudraTier." + uuidKey));
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUID
                }
            }
        }
        runsCompleted.clear();
        if (cfg.isConfigurationSection("runsCompleted")) {
            for (String uuidKey : cfg.getConfigurationSection("runsCompleted").getKeys(false)) {
                try {
                    runsCompleted.put(UUID.fromString(uuidKey), cfg.getInt("runsCompleted." + uuidKey));
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUID
                }
            }
        }
        tierCompletions.clear();
        if (cfg.isConfigurationSection("tierCompletions")) {
            for (String uuidKey : cfg.getConfigurationSection("tierCompletions").getKeys(false)) {
                try {
                    UUID id = UUID.fromString(uuidKey);
                    String path = "tierCompletions." + uuidKey;
                    if (cfg.isConfigurationSection(path)) {
                        Map<String, Integer> counts = new HashMap<>();
                        for (String tier : cfg.getConfigurationSection(path).getKeys(false)) {
                            counts.put(tier, cfg.getInt(path + "." + tier));
                        }
                        tierCompletions.put(id, counts);
                    }
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUID
                }
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "kuudra.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : kuudraTier.entrySet()) {
            cfg.set("kuudraTier." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Integer> entry : runsCompleted.entrySet()) {
            cfg.set("runsCompleted." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Map<String, Integer>> outer : tierCompletions.entrySet()) {
            String base = "tierCompletions." + outer.getKey().toString();
            for (Map.Entry<String, Integer> inner : outer.getValue().entrySet()) {
                cfg.set(base + "." + inner.getKey(), inner.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save kuudra.yml", e);
        }
    }
}
