package com.skyblock.plugin.island;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton in-memory registry of {@link IslandData} instances keyed by player
 * UUID.
 *
 * <p>On {@link #onEnable()} the server's primary world
 * ({@code Bukkit.getWorlds().get(0)}) is cached as the hub world so the rest of
 * the plugin can resolve it without repeatedly querying the server.</p>
 *
 * <p>The island map is mutated only on the server main thread; access it from
 * the main thread or guard it externally.</p>
 */
public final class IslandManager {

    private static final IslandManager INSTANCE = new IslandManager();

    private final Map<UUID, IslandData> islands = new HashMap<>();

    private World hubWorld;

    private IslandManager() {}

    public static IslandManager getInstance() {
        return INSTANCE;
    }

    /**
     * Caches the server's primary world as the hub world.
     *
     * <p>Call from the plugin's {@code onEnable} once the server's worlds have
     * been loaded.</p>
     */
    public void onEnable() {
        hubWorld = Bukkit.getWorlds().get(0);
    }

    /**
     * Returns the cached hub world.
     *
     * @return the hub world, or {@code null} if {@link #onEnable()} has not run
     */
    public World getHubWorld() {
        return hubWorld;
    }

    /**
     * Returns the island for the given player, creating and registering a new
     * one if none exists yet.
     *
     * @param uuid unique identifier of the player
     * @param worldName name of the player's island world
     * @return the player's island data, never {@code null}
     */
    public IslandData getOrCreate(UUID uuid, String worldName) {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(worldName, "worldName");
        return islands.computeIfAbsent(uuid, id -> new IslandData(id, worldName));
    }

    /**
     * Returns the island for the given player, or {@code null} if none has been
     * registered.
     *
     * @param uuid unique identifier of the player
     * @return the player's island data, or {@code null}
     */
    public IslandData getIsland(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return islands.get(uuid);
    }

    /**
     * Returns whether an island is registered for the given player.
     *
     * @param uuid unique identifier of the player
     * @return {@code true} if an island exists
     */
    public boolean hasIsland(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return islands.containsKey(uuid);
    }

    /**
     * Removes the island for the given player.
     *
     * @param uuid unique identifier of the player
     * @return the removed island, or {@code null} if none existed
     */
    public IslandData removeIsland(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return islands.remove(uuid);
    }

    /**
     * Returns an immutable snapshot of all registered islands keyed by UUID.
     *
     * @return the registered islands
     */
    public Map<UUID, IslandData> getIslands() {
        return Collections.unmodifiableMap(islands);
    }

    /**
     * Persists the given player's island metadata to
     * {@code plugins/SkyBlock/islands/<uuid>.yml}.
     *
     * <p>Does nothing if no island is registered for the player. The write runs
     * on the calling thread; invoke it off the main thread for bulk saves.</p>
     *
     * @param plugin the owning plugin, used for the data folder
     * @param uuid   unique identifier of the player whose island to save
     */
    public void save(Plugin plugin, UUID uuid) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(uuid, "uuid");

        IslandData data = islands.get(uuid);
        if (data == null) {
            return;
        }

        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("uuid", uuid.toString());
        cfg.set("worldName", data.getWorldName());

        File dir = new File(plugin.getDataFolder(), "islands");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            cfg.save(new File(dir, uuid + ".yml"));
        } catch (IOException e) {
            plugin.getLogger().warning(
                    "Failed to save island for " + uuid + ": " + e.getMessage());
        }
    }

    /**
     * Loads the given player's island metadata from
     * {@code plugins/SkyBlock/islands/<uuid>.yml}, registering it in memory.
     *
     * @param plugin the owning plugin, used for the data folder
     * @param uuid   unique identifier of the player whose island to load
     * @return the loaded island, or {@code null} if no file exists yet
     */
    public IslandData load(Plugin plugin, UUID uuid) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(uuid, "uuid");

        File file = new File(new File(plugin.getDataFolder(), "islands"), uuid + ".yml");
        if (!file.exists()) {
            return null;
        }

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        String worldName = cfg.getString("worldName");
        if (worldName == null) {
            return null;
        }

        IslandData data = new IslandData(uuid, worldName);
        islands.put(uuid, data);
        return data;
    }

    /**
     * A single player's SkyBlock island.
     *
     * <p>Tracks the owning player and the name of the world that backs their
     * island. Instances are not thread-safe; access them from the server main
     * thread or guard them externally.</p>
     */
    public static final class IslandData {

        private final UUID player;
        private final String worldName;

        /**
         * Creates island data for the given player.
         *
         * @param player unique identifier of the owning player
         * @param worldName name of the island world
         */
        public IslandData(UUID player, String worldName) {
            this.player = Objects.requireNonNull(player, "player");
            this.worldName = Objects.requireNonNull(worldName, "worldName");
        }

        /**
         * Returns the unique identifier of the player this island belongs to.
         *
         * @return the owning player's UUID
         */
        public UUID getPlayer() {
            return player;
        }

        /**
         * Returns the name of the world that backs this island.
         *
         * @return the island world name
         */
        public String getWorldName() {
            return worldName;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof IslandData other && player.equals(other.player);
        }

        @Override
        public int hashCode() {
            return player.hashCode();
        }

        @Override
        public String toString() {
            return "IslandData{player=" + player + ", worldName=" + worldName + '}';
        }
    }
}
