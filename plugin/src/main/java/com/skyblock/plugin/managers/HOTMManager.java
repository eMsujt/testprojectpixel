package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class HOTMManager {

    /** XP required to reach each level; index 0 = XP needed to go from level 1 → 2. */
    private static final double[] HOTM_XP_TABLE = {
            500, 1500, 5000, 15000, 50000, 150000
    };

    private static final HOTMManager INSTANCE = new HOTMManager();

    private final Map<UUID, Integer> hotmLevel = new HashMap<>();
    private final Map<UUID, Double>  hotmXP    = new HashMap<>();

    private HOTMManager() {}

    public static HOTMManager getInstance() {
        return INSTANCE;
    }

    public int getHOTMLevel(UUID playerId) {
        return hotmLevel.getOrDefault(playerId, 1);
    }

    /** @deprecated use {@link #getHOTMLevel(UUID)} */
    public int getHotmLevel(UUID playerId) {
        return getHOTMLevel(playerId);
    }

    public void setHotmLevel(UUID playerId, int level) {
        hotmLevel.put(playerId, Math.max(1, Math.min(7, level)));
    }

    public void addHotmLevel(UUID playerId, int amount) {
        setHotmLevel(playerId, getHOTMLevel(playerId) + amount);
    }

    public double getHOTMXP(UUID playerId) {
        return hotmXP.getOrDefault(playerId, 0.0);
    }

    public void addHOTMXP(UUID playerId, double amount) {
        double xp = getHOTMXP(playerId) + amount;
        int level = getHOTMLevel(playerId);
        while (level < 7 && xp >= HOTM_XP_TABLE[level - 1]) {
            xp -= HOTM_XP_TABLE[level - 1];
            level++;
        }
        hotmLevel.put(playerId, level);
        hotmXP.put(playerId, xp);
    }

    public Map<UUID, Integer> getHotmLevels() {
        return Collections.unmodifiableMap(hotmLevel);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "hotm.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        hotmLevel.clear();
        hotmXP.clear();
        if (cfg.isConfigurationSection("hotmLevel")) {
            for (String uuidKey : cfg.getConfigurationSection("hotmLevel").getKeys(false)) {
                try {
                    hotmLevel.put(UUID.fromString(uuidKey), cfg.getInt("hotmLevel." + uuidKey));
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUID
                }
            }
        }
        if (cfg.isConfigurationSection("hotmXP")) {
            for (String uuidKey : cfg.getConfigurationSection("hotmXP").getKeys(false)) {
                try {
                    hotmXP.put(UUID.fromString(uuidKey), cfg.getDouble("hotmXP." + uuidKey));
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUID
                }
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "hotm.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : hotmLevel.entrySet()) {
            cfg.set("hotmLevel." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Double> entry : hotmXP.entrySet()) {
            cfg.set("hotmXP." + entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save hotm.yml", e);
        }
    }
}
