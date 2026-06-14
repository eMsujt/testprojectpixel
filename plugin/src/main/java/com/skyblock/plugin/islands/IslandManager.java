package com.skyblock.plugin.islands;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * In-memory registry of per-player private islands.
 *
 * <p>Holds island records in a {@link Map} keyed by each island's owner,
 * preserving insertion order so islands are listed in the order they were
 * created. A record is added when a player's island is generated and removed
 * if the island is deleted. Not thread-safe; access from the main server
 * thread.</p>
 */
public final class IslandManager {

    /**
     * A single player-owned island.
     *
     * @param owner     the owning player's UUID
     * @param worldName the name of the island's world
     * @param spawnX    the island spawn's x coordinate
     * @param spawnY    the island spawn's y coordinate
     * @param spawnZ    the island spawn's z coordinate
     */
    public record IslandData(UUID owner, String worldName, double spawnX, double spawnY, double spawnZ) {
        public IslandData {
            Objects.requireNonNull(owner, "owner");
            Objects.requireNonNull(worldName, "worldName");
        }
    }

    private static final IslandManager INSTANCE = new IslandManager();

    private final Map<UUID, IslandData> islands = new LinkedHashMap<>();

    private IslandManager() {
    }

    public static IslandManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds an island to the registry, replacing any existing record for the
     * same owner.
     *
     * @param island the island to add
     */
    public void addIsland(IslandData island) {
        Objects.requireNonNull(island, "island");
        islands.put(island.owner(), island);
    }

    /**
     * Returns the island owned by the given player, or {@code null} if absent.
     *
     * @param owner the owning player's UUID
     * @return the island, or {@code null}
     */
    public IslandData getIsland(UUID owner) {
        return islands.get(owner);
    }

    /**
     * Removes the island owned by the given player.
     *
     * @param owner the owning player's UUID
     * @return the removed island, or {@code null} if none existed
     */
    public IslandData removeIsland(UUID owner) {
        return islands.remove(owner);
    }

    /**
     * Returns an unmodifiable view of every island in creation order.
     *
     * @return the registered islands
     */
    public Collection<IslandData> getIslands() {
        return Collections.unmodifiableCollection(islands.values());
    }

    /**
     * Loads islands from {@code islands.yml} in the given data folder, replacing
     * any currently registered islands. Missing or malformed entries are skipped.
     *
     * @param dataFolder the plugin data folder containing {@code islands.yml}
     */
    public void load(File dataFolder) {
        File file = new File(dataFolder, "islands.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = cfg.getConfigurationSection("islands");
        islands.clear();
        if (root == null) {
            return;
        }
        for (String key : root.getKeys(false)) {
            ConfigurationSection section = root.getConfigurationSection(key);
            if (section == null) {
                continue;
            }
            UUID owner;
            try {
                owner = UUID.fromString(key);
            } catch (IllegalArgumentException ignored) {
                continue;
            }
            String worldName = section.getString("world");
            if (worldName == null) {
                continue;
            }
            islands.put(owner, new IslandData(
                    owner,
                    worldName,
                    section.getDouble("x"),
                    section.getDouble("y"),
                    section.getDouble("z")));
        }
    }

    /**
     * Saves every registered island to {@code islands.yml} in the given data
     * folder, mapping each owner's UUID to its island.
     *
     * @param dataFolder the plugin data folder to write {@code islands.yml} into
     */
    public void save(File dataFolder) {
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, IslandData> entry : islands.entrySet()) {
            String path = "islands." + entry.getKey();
            IslandData island = entry.getValue();
            cfg.set(path + ".world", island.worldName());
            cfg.set(path + ".x", island.spawnX());
            cfg.set(path + ".y", island.spawnY());
            cfg.set(path + ".z", island.spawnZ());
        }
        try {
            cfg.save(new File(dataFolder, "islands.yml"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save islands.yml", e);
        }
    }
}
