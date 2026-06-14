package com.skyblock.plugin.managers;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class NetworkManager {

    private static final NetworkManager INSTANCE = new NetworkManager();

    private final Map<UUID, Long> joinTimestamps = new HashMap<>();
    private final Map<UUID, String> lastKnownServer = new HashMap<>();

    private NetworkManager() {}

    public static NetworkManager getInstance() {
        return INSTANCE;
    }

    public void recordJoin(UUID playerId) {
        joinTimestamps.put(playerId, System.currentTimeMillis());
    }

    public long getJoinTimestamp(UUID playerId) {
        return joinTimestamps.getOrDefault(playerId, -1L);
    }

    public Map<UUID, Long> getJoinTimestamps() {
        return Collections.unmodifiableMap(joinTimestamps);
    }

    public void setLastKnownServer(UUID playerId, String server) {
        lastKnownServer.put(playerId, server);
    }

    public String getLastKnownServer(UUID playerId) {
        return lastKnownServer.get(playerId);
    }

    public Map<UUID, String> getLastKnownServers() {
        return Collections.unmodifiableMap(lastKnownServer);
    }

    public void removePlayer(UUID playerId) {
        joinTimestamps.remove(playerId);
        lastKnownServer.remove(playerId);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "network.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        joinTimestamps.clear();
        lastKnownServer.clear();
        if (cfg.isConfigurationSection("players")) {
            for (String key : cfg.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID id = UUID.fromString(key);
                    String prefix = "players." + key + ".";
                    long ts = cfg.getLong(prefix + "joinTimestamp", -1L);
                    if (ts >= 0) {
                        joinTimestamps.put(id, ts);
                    }
                    String server = cfg.getString(prefix + "lastKnownServer");
                    if (server != null) {
                        lastKnownServer.put(id, server);
                    }
                } catch (IllegalArgumentException ignored) {
                    // skip malformed entry
                }
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "network.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Long> entry : joinTimestamps.entrySet()) {
            cfg.set("players." + entry.getKey().toString() + ".joinTimestamp", entry.getValue());
        }
        for (Map.Entry<UUID, String> entry : lastKnownServer.entrySet()) {
            cfg.set("players." + entry.getKey().toString() + ".lastKnownServer", entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save network.yml", e);
        }
    }
}
