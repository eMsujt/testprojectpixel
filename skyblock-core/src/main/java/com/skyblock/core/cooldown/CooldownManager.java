package com.skyblock.core.cooldown;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking per-player, per-key cooldown expiry timestamps.
 *
 * <p>Cooldowns are persisted to {@code plugins/SkyblockCore/cooldowns.yml}
 * and survive server restarts (expired entries are pruned on load).
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class CooldownManager {

    private static final CooldownManager INSTANCE = new CooldownManager();

    /** player UUID → (key → expiry epoch ms) */
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    private CooldownManager() {
    }

    public static CooldownManager getInstance() {
        return INSTANCE;
    }

    public void setCooldown(UUID playerId, String key, long durationMs) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(key, "key");
        cooldowns.computeIfAbsent(playerId, id -> new HashMap<>())
                 .put(key, System.currentTimeMillis() + durationMs);
    }

    public long getRemainingMs(UUID playerId, String key) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(key, "key");
        Map<String, Long> map = cooldowns.get(playerId);
        if (map == null) return 0L;
        Long expiry = map.get(key);
        if (expiry == null) return 0L;
        return Math.max(0L, expiry - System.currentTimeMillis());
    }

    public boolean isOnCooldown(UUID playerId, String key) {
        return getRemainingMs(playerId, key) > 0;
    }

    public void clearCooldown(UUID playerId, String key) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(key, "key");
        Map<String, Long> map = cooldowns.get(playerId);
        if (map != null) {
            map.remove(key);
            if (map.isEmpty()) cooldowns.remove(playerId);
        }
    }

    public void clearAll(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        cooldowns.remove(playerId);
    }

    public Map<String, Long> getCooldowns(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<String, Long> map = cooldowns.get(playerId);
        return map == null ? Collections.emptyMap() : Collections.unmodifiableMap(map);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "cooldowns.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        cooldowns.clear();
        if (!cfg.isConfigurationSection("players")) return;
        long now = System.currentTimeMillis();
        for (String uuidStr : cfg.getConfigurationSection("players").getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                if (!cfg.isConfigurationSection("players." + uuidStr)) continue;
                Map<String, Long> map = new HashMap<>();
                for (String key : cfg.getConfigurationSection("players." + uuidStr).getKeys(false)) {
                    long expiry = cfg.getLong("players." + uuidStr + "." + key);
                    if (expiry > now) {
                        map.put(key, expiry);
                    }
                }
                if (!map.isEmpty()) cooldowns.put(uuid, map);
            } catch (IllegalArgumentException ignored) {
                // skip malformed UUID
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "cooldowns.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        long now = System.currentTimeMillis();
        for (Map.Entry<UUID, Map<String, Long>> playerEntry : cooldowns.entrySet()) {
            for (Map.Entry<String, Long> cdEntry : playerEntry.getValue().entrySet()) {
                if (cdEntry.getValue() > now) {
                    cfg.set("players." + playerEntry.getKey() + "." + cdEntry.getKey(), cdEntry.getValue());
                }
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save cooldowns.yml", e);
        }
    }
}
