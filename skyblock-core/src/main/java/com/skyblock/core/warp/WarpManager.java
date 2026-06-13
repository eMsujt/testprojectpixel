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

    /** Canonical SkyBlock warp destinations used by the /warp command. */
    public enum Warp {
        HUB("Hub"),
        GARDEN("Garden"),
        DEEP_CAVERNS("Deep Caverns"),
        DWARVEN_MINES("Dwarven Mines"),
        CRYSTAL_HOLLOWS("Crystal Hollows"),
        SPIDER_DEN("Spider's Den"),
        BLAZING_FORTRESS("Blazing Fortress"),
        THE_END("The End"),
        CRIMSON_ISLE("Crimson Isle"),
        FARMING_ISLANDS("Farming Islands"),
        THE_PARK("The Park"),
        GOLD_MINE("Gold Mine"),
        DARK_AUCTION("Dark Auction"),
        DUNGEON_HUB("Dungeon Hub"),
        THE_RIFT("The Rift");

        public final String displayName;

        Warp(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }

        public static Warp fromName(String name) {
            for (Warp w : values()) {
                if (w.displayName.equalsIgnoreCase(name) || w.name().equalsIgnoreCase(name)) {
                    return w;
                }
            }
            return null;
        }
    }

    /** Named SkyBlock zones used for zone-based logic and display. */
    public enum SkyBlockZone {
        HUB("Hub"),
        FARMING_ISLAND("Farming Island"),
        COAL_MINE("Coal Mine"),
        GOLD_MINE("Gold Mine"),
        DEEP_CAVERNS("Deep Caverns"),
        DWARVEN_MINES("Dwarven Mines"),
        CRYSTAL_HOLLOWS("Crystal Hollows"),
        THE_PARK("The Park"),
        SPIDERS_DEN("Spider's Den"),
        BLAZING_FORTRESS("Blazing Fortress"),
        THE_END("The End"),
        CRIMSON_ISLE("Crimson Isle"),
        THE_RIFT("The Rift"),
        DUNGEON_HUB("Dungeon Hub"),
        MUSHROOM_DESERT("Mushroom Desert");

        public final String displayName;

        SkyBlockZone(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** Named SkyBlock locations a player can be in, used for location-based logic and display. */
    public enum SkyBlockLocation {
        HUB("Hub"),
        FARMING_ISLANDS("Farming Islands"),
        COAL_MINE("Coal Mine"),
        GOLD_MINE("Gold Mine"),
        DEEP_CAVERNS("Deep Caverns"),
        DWARVEN_MINES("Dwarven Mines"),
        CRYSTAL_HOLLOWS("Crystal Hollows"),
        THE_PARK("The Park"),
        SPIDERS_DEN("Spider's Den"),
        BLAZING_FORTRESS("Blazing Fortress"),
        THE_END("The End"),
        CRIMSON_ISLE("Crimson Isle"),
        THE_RIFT("The Rift"),
        DUNGEON_HUB("Dungeon Hub"),
        MUSHROOM_DESERT("Mushroom Desert");

        public final String displayName;

        SkyBlockLocation(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /** Pre-defined SkyBlock warp destinations. */
    public enum WarpLocation {
        HUB("Hub"),
        FARMING_1("Barn"),
        FARMING_2("Mushroom Desert"),
        MINING_1("Gold Mine"),
        MINING_2("Deep Caverns"),
        MINING_3("Dwarven Mines"),
        FORAGING_1("The Park"),
        COMBAT_1("Spider's Den"),
        COMBAT_2("Blazing Fortress"),
        COMBAT_3("The End"),
        CRYSTAL_HOLLOWS("Crystal Hollows"),
        CRIMSON_ISLE("Crimson Isle"),
        THE_RIFT("The Rift"),
        DUNGEON_HUB("Dungeon Hub");

        /** Human-readable display name shown to players. */
        public final String displayName;

        WarpLocation(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        /** Returns the warp key used to look up this location in the registry. */
        public String warpKey() {
            return name().toLowerCase();
        }
    }

    private static final WarpManager INSTANCE = new WarpManager();

    private final Map<String, Warp> warps = new HashMap<>();

    private WarpManager() {
    }

    public static WarpManager getInstance() {
        return INSTANCE;
    }

    /**
     * Registers or overwrites a named warp at the given location.
     *
     * @param name     warp name (case-insensitive)
     * @param location target location
     */
    public void setWarp(String name, Location location) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(location, "location");
        warps.put(name.toLowerCase(), Warp.fromLocation(name.toLowerCase(), location));
    }

    /**
     * Returns the {@link Warp} for the given name, if present.
     *
     * @param name warp name (case-insensitive)
     * @return the {@link Warp}, or empty
     */
    public Optional<Warp> getWarp(String name) {
        Objects.requireNonNull(name, "name");
        return Optional.ofNullable(warps.get(name.toLowerCase()));
    }

    /**
     * Returns the {@link Warp} registered for the given {@link WarpLocation}, if present.
     *
     * @param location the pre-defined warp location
     * @return the {@link Warp}, or empty
     */
    public Optional<Warp> getWarp(WarpLocation location) {
        Objects.requireNonNull(location, "location");
        return getWarp(location.warpKey());
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
                warps.put(name, new Warp(name, world, x, y, z, yaw, pitch));
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
        for (Warp warp : warps.values()) {
            String name = warp.getName();
            config.set(name + ".world", warp.getWorld().getName());
            config.set(name + ".x", warp.getX());
            config.set(name + ".y", warp.getY());
            config.set(name + ".z", warp.getZ());
            config.set(name + ".yaw", (double) warp.getYaw());
            config.set(name + ".pitch", (double) warp.getPitch());
        }
        config.save(file);
    }
}
