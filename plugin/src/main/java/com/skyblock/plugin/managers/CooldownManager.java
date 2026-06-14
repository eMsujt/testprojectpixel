package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CooldownManager {

    private static final CooldownManager INSTANCE = new CooldownManager();

    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    private CooldownManager() {}

    public static CooldownManager getInstance() {
        return INSTANCE;
    }

    public void setCooldown(UUID uuid, String action, long expireTime) {
        cooldowns.computeIfAbsent(uuid, k -> new HashMap<>()).put(action, expireTime);
    }

    public boolean isOnCooldown(UUID uuid, String action) {
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (playerCooldowns == null) {
            return false;
        }
        Long expireTime = playerCooldowns.get(action);
        return expireTime != null && System.currentTimeMillis() < expireTime;
    }

    public long getRemainingMillis(UUID uuid, String action) {
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (playerCooldowns == null) {
            return 0L;
        }
        Long expireTime = playerCooldowns.get(action);
        if (expireTime == null) {
            return 0L;
        }
        long remaining = expireTime - System.currentTimeMillis();
        return remaining > 0 ? remaining : 0L;
    }

    public void clearCooldown(UUID uuid, String action) {
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (playerCooldowns != null) {
            playerCooldowns.remove(action);
        }
    }

    public void removePlayer(UUID uuid) {
        cooldowns.remove(uuid);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "cooldowns.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        cooldowns.clear();
        if (cfg.isConfigurationSection("players")) {
            for (String uuidKey : cfg.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID id = UUID.fromString(uuidKey);
                    String prefix = "players." + uuidKey;
                    if (cfg.isConfigurationSection(prefix)) {
                        Map<String, Long> playerCooldowns = new HashMap<>();
                        for (String action : cfg.getConfigurationSection(prefix).getKeys(false)) {
                            playerCooldowns.put(action, cfg.getLong(prefix + "." + action));
                        }
                        cooldowns.put(id, playerCooldowns);
                    }
                } catch (IllegalArgumentException ignored) {
                    // skip malformed entry
                }
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "cooldowns.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<String, Long>> playerEntry : cooldowns.entrySet()) {
            String prefix = "players." + playerEntry.getKey().toString();
            for (Map.Entry<String, Long> actionEntry : playerEntry.getValue().entrySet()) {
                cfg.set(prefix + "." + actionEntry.getKey(), actionEntry.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save cooldowns.yml", e);
        }
    }
}
