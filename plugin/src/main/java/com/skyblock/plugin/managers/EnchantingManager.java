package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class EnchantingManager {

    private static final EnchantingManager INSTANCE = new EnchantingManager();

    private final Map<UUID, Integer> enchantingLevels = new HashMap<>();

    private EnchantingManager() {}

    public static EnchantingManager getInstance() {
        return INSTANCE;
    }

    public int getEnchantingLevel(UUID playerId) {
        return enchantingLevels.getOrDefault(playerId, 0);
    }

    public void setEnchantingLevel(UUID playerId, int level) {
        enchantingLevels.put(playerId, level);
    }

    public void addEnchantingLevel(UUID playerId, int amount) {
        enchantingLevels.put(playerId, getEnchantingLevel(playerId) + amount);
    }

    public Map<UUID, Integer> getEnchantingLevels() {
        return enchantingLevels;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "enchanting.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        enchantingLevels.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                enchantingLevels.put(uuid, cfg.getInt(key));
            } catch (IllegalArgumentException ignored) {
                // skip malformed UUID
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "enchanting.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : enchantingLevels.entrySet()) {
            cfg.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save enchanting.yml", e);
        }
    }
}
