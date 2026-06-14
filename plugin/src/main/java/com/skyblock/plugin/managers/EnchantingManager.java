package com.skyblock.plugin.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class EnchantingManager {

    private static final EnchantingManager INSTANCE = new EnchantingManager();

    private final Map<UUID, Integer> enchantingXP = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> enchantLevels = new HashMap<>();
    private final Map<UUID, Integer> bookshelfPower = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> enchantHistory = new HashMap<>();

    private EnchantingManager() {}

    public static EnchantingManager getInstance() {
        return INSTANCE;
    }

    public int getEnchantingXP(UUID playerId) {
        return enchantingXP.getOrDefault(playerId, 0);
    }

    public void setEnchantingXP(UUID playerId, int amount) {
        enchantingXP.put(playerId, Math.max(0, amount));
    }

    public void addEnchantingXP(UUID playerId, int amount) {
        setEnchantingXP(playerId, getEnchantingXP(playerId) + amount);
    }

    public Map<UUID, Integer> getAllEnchantingXP() {
        return enchantingXP;
    }

    public int getEnchantLevel(UUID playerId, String enchant) {
        Map<String, Integer> levels = enchantLevels.get(playerId);
        if (levels == null) return 0;
        return levels.getOrDefault(enchant, 0);
    }

    public void addEnchantLevel(UUID playerId, String enchant, int amount) {
        enchantLevels
                .computeIfAbsent(playerId, k -> new HashMap<>())
                .merge(enchant, amount, Integer::sum);
    }

    public void setEnchantLevel(UUID playerId, String enchant, int level) {
        enchantLevels
                .computeIfAbsent(playerId, k -> new HashMap<>())
                .put(enchant, level);
    }

    public Map<String, Integer> getEnchantLevels(UUID playerId) {
        return enchantLevels.getOrDefault(playerId, new HashMap<>());
    }

    public int getBookshelfPower(UUID playerId) {
        return bookshelfPower.getOrDefault(playerId, 0);
    }

    public void addBookshelfPower(UUID playerId, int slots) {
        bookshelfPower.merge(playerId, slots, Integer::sum);
    }

    public Map<UUID, Integer> getAllBookshelfPower() {
        return bookshelfPower;
    }

    public void recordEnchant(UUID playerId, String enchantName, int level) {
        enchantHistory
                .computeIfAbsent(playerId, k -> new HashMap<>())
                .put(enchantName, level);
    }

    public Map<String, Integer> getEnchantHistory(UUID playerId) {
        return enchantHistory.getOrDefault(playerId, new HashMap<>());
    }

    public Map<UUID, Map<String, Integer>> getAllEnchantHistory() {
        return enchantHistory;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "enchanting.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        enchantingXP.clear();
        enchantLevels.clear();
        bookshelfPower.clear();
        if (cfg.isConfigurationSection("enchantingXP")) {
            for (String key : cfg.getConfigurationSection("enchantingXP").getKeys(false)) {
                try {
                    enchantingXP.put(UUID.fromString(key), cfg.getInt("enchantingXP." + key));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        if (cfg.isConfigurationSection("bookshelfPower")) {
            for (String key : cfg.getConfigurationSection("bookshelfPower").getKeys(false)) {
                try {
                    bookshelfPower.put(UUID.fromString(key), cfg.getInt("bookshelfPower." + key));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        if (cfg.isConfigurationSection("enchantLevels")) {
            ConfigurationSection levelsSection = cfg.getConfigurationSection("enchantLevels");
            for (String key : levelsSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    ConfigurationSection section = levelsSection.getConfigurationSection(key);
                    if (section == null) continue;
                    Map<String, Integer> levels = new HashMap<>();
                    for (String enchant : section.getKeys(false)) {
                        levels.put(enchant, section.getInt(enchant));
                    }
                    enchantLevels.put(uuid, levels);
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "enchanting.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : enchantingXP.entrySet()) {
            cfg.set("enchantingXP." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Integer> entry : bookshelfPower.entrySet()) {
            cfg.set("bookshelfPower." + entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<UUID, Map<String, Integer>> entry : enchantLevels.entrySet()) {
            String path = "enchantLevels." + entry.getKey().toString();
            for (Map.Entry<String, Integer> levelEntry : entry.getValue().entrySet()) {
                cfg.set(path + "." + levelEntry.getKey(), levelEntry.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save enchanting.yml", e);
        }
    }
}
