package com.skyblock.core.network;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking per-player network sessions and total playtime.
 *
 * <p>Join timestamps (epoch milliseconds) are stored in {@link #joinTimestamps} while a
 * player is online. Accumulated playtime is stored in {@link #totalPlaytimeSeconds} and
 * persisted to {@code plugins/SkyblockCore/network.yml}.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class NetworkManager {

    private static final NetworkManager INSTANCE = new NetworkManager();

    /** Per-player join timestamp (epoch ms); present only while the player is online. */
    private final Map<UUID, Long> joinTimestamps = new HashMap<>();

    /** Per-player accumulated playtime in seconds; persisted across restarts. */
    private final Map<UUID, Long> totalPlaytimeSeconds = new HashMap<>();

    private NetworkManager() {
    }

    public static NetworkManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Session lifecycle
    // -------------------------------------------------------------------------

    /**
     * Records that the player with the given UUID joined the server.
     *
     * @param playerId the joining player's UUID
     */
    public void playerJoin(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        joinTimestamps.put(playerId, System.currentTimeMillis());
    }

    /**
     * Records that the player with the given UUID left the server.
     * The session duration is added to their accumulated playtime.
     *
     * @param playerId the leaving player's UUID
     */
    public void playerQuit(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Long joinTime = joinTimestamps.remove(playerId);
        if (joinTime != null) {
            long sessionSeconds = (System.currentTimeMillis() - joinTime) / 1000L;
            totalPlaytimeSeconds.merge(playerId, sessionSeconds, Long::sum);
        }
    }

    // -------------------------------------------------------------------------
    // Queries
    // -------------------------------------------------------------------------

    /**
     * Returns the number of seconds the player has been online in the current session,
     * or {@code 0} if the player is not currently tracked.
     *
     * @param playerId the player's UUID
     * @return current session seconds
     */
    public long getSessionSeconds(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Long joinTime = joinTimestamps.get(playerId);
        return joinTime == null ? 0L : (System.currentTimeMillis() - joinTime) / 1000L;
    }

    /**
     * Returns the player's total accumulated playtime in seconds (excluding the current
     * session, which is flushed on quit).
     *
     * @param playerId the player's UUID
     * @return total playtime seconds
     */
    public long getTotalPlaytimeSeconds(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return totalPlaytimeSeconds.getOrDefault(playerId, 0L);
    }

    /**
     * Returns whether the given player currently has an active session.
     *
     * @param playerId the player's UUID
     * @return {@code true} if the player is tracked as online
     */
    public boolean isOnline(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return joinTimestamps.containsKey(playerId);
    }

    // -------------------------------------------------------------------------
    // Mutations
    // -------------------------------------------------------------------------

    /**
     * Adds the given number of seconds to the player's accumulated playtime.
     *
     * @param playerId the player's UUID
     * @param seconds  seconds to add (must be &ge; 0)
     */
    public void addPlaytime(UUID playerId, long seconds) {
        Objects.requireNonNull(playerId, "playerId");
        if (seconds < 0) throw new IllegalArgumentException("seconds must be >= 0");
        totalPlaytimeSeconds.merge(playerId, seconds, Long::sum);
    }

    /**
     * Resets the player's accumulated playtime to zero.
     *
     * @param playerId the player's UUID
     */
    public void resetPlaytime(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        totalPlaytimeSeconds.remove(playerId);
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "network.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        totalPlaytimeSeconds.clear();
        if (cfg.isConfigurationSection("players")) {
            for (String key : cfg.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    long seconds = cfg.getLong("players." + key + ".totalPlaytimeSeconds", 0L);
                    if (seconds > 0) {
                        totalPlaytimeSeconds.put(uuid, seconds);
                    }
                } catch (IllegalArgumentException ignored) {
                    // skip malformed UUID
                }
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "network.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Long> entry : totalPlaytimeSeconds.entrySet()) {
            cfg.set("players." + entry.getKey().toString() + ".totalPlaytimeSeconds", entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save network.yml", e);
        }
    }
}
