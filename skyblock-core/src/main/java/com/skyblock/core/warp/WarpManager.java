package com.skyblock.core.warp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Singleton registry of named warp destinations with file-based persistence.
 *
 * <p>Use {@link #load(File)} and {@link #save(File)} to persist warps across
 * restarts. Warp names are stored and matched in lower-case.</p>
 */
public final class WarpManager {

    private static final WarpManager INSTANCE = new WarpManager();

    private final Map<String, Location> warps = new HashMap<>();

    private WarpManager() {
    }

    public static WarpManager getInstance() {
        return INSTANCE;
    }

    /**
     * Registers or overwrites a named warp at the given location.
     *
     * @param name     warp name (case-insensitive)
     * @param location target location; a defensive copy is stored
     */
    public void setWarp(String name, Location location) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(location, "location");
        warps.put(name.toLowerCase(), location.clone());
    }

    /**
     * Returns the location for the given warp name, if present.
     *
     * @param name warp name (case-insensitive)
     * @return a cloned {@link Location}, or empty
     */
    public Optional<Location> getWarp(String name) {
        Objects.requireNonNull(name, "name");
        Location loc = warps.get(name.toLowerCase());
        return loc == null ? Optional.empty() : Optional.of(loc.clone());
    }

    /**
     * Removes a warp.
     *
     * @param name warp name (case-insensitive)
     * @return {@code true} if the warp existed and was removed
     */
    public boolean removeWarp(String name) {
        Objects.requireNonNull(name, "name");
        return warps.remove(name.toLowerCase()) != null;
    }

    /**
     * Returns an unmodifiable view of all registered warp names (lower-case).
     *
     * @return the set of warp names
     */
    public Set<String> getWarpNames() {
        return Collections.unmodifiableSet(warps.keySet());
    }

    /**
     * Loads warps from a YAML file, replacing any previously registered warps.
     * Missing worlds are silently skipped.
     *
     * @param file the YAML file to read from; no-op if it does not exist
     */
    public void load(File file) {
        Objects.requireNonNull(file, "file");
        warps.clear();
        if (!file.exists()) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String name : config.getKeys(false)) {
            String worldName = config.getString(name + ".world", "world");
            double x = config.getDouble(name + ".x");
            double y = config.getDouble(name + ".y");
            double z = config.getDouble(name + ".z");
            float yaw = (float) config.getDouble(name + ".yaw");
            float pitch = (float) config.getDouble(name + ".pitch");
            World world = Bukkit.getWorld(worldName);
            if (world != null) {
                warps.put(name, new Location(world, x, y, z, yaw, pitch));
            }
        }
    }

    /**
     * Saves all registered warps to a YAML file.
     *
     * @param file the YAML file to write to
     * @throws IOException if the file cannot be written
     */
    public void save(File file) throws IOException {
        Objects.requireNonNull(file, "file");
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<String, Location> entry : warps.entrySet()) {
            String name = entry.getKey();
            Location loc = entry.getValue();
            config.set(name + ".world", loc.getWorld() != null ? loc.getWorld().getName() : "world");
            config.set(name + ".x", loc.getX());
            config.set(name + ".y", loc.getY());
            config.set(name + ".z", loc.getZ());
            config.set(name + ".yaw", (double) loc.getYaw());
            config.set(name + ".pitch", (double) loc.getPitch());
        }
        config.save(file);
    }
}
