package com.skyblock.core.manager;

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

public final class WarpManager {

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

        SkyBlockZone(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

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

        SkyBlockLocation(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

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

        public final String displayName;

        WarpLocation(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
        public String warpKey() { return name().toLowerCase(); }
    }

    private static final WarpManager INSTANCE = new WarpManager();

    private final Map<String, Warp> warps = new HashMap<>();

    private WarpManager() {}

    public static WarpManager getInstance() { return INSTANCE; }

    public void setWarp(String name, Location location) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(location, "location");
        warps.put(name.toLowerCase(), Warp.fromLocation(name.toLowerCase(), location));
    }

    public Optional<Warp> getWarp(String name) {
        Objects.requireNonNull(name, "name");
        return Optional.ofNullable(warps.get(name.toLowerCase()));
    }

    public Optional<Warp> getWarp(WarpLocation location) {
        Objects.requireNonNull(location, "location");
        return getWarp(location.warpKey());
    }

    public boolean removeWarp(String name) {
        Objects.requireNonNull(name, "name");
        return warps.remove(name.toLowerCase()) != null;
    }

    public Set<String> getWarpNames() {
        return Collections.unmodifiableSet(warps.keySet());
    }

    public void load(File file) {
        Objects.requireNonNull(file, "file");
        warps.clear();
        if (!file.exists()) return;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String name : config.getKeys(false)) {
            String worldName = config.getString(name + ".world", "world");
            double x     = config.getDouble(name + ".x");
            double y     = config.getDouble(name + ".y");
            double z     = config.getDouble(name + ".z");
            float  yaw   = (float) config.getDouble(name + ".yaw");
            float  pitch = (float) config.getDouble(name + ".pitch");
            World  world = Bukkit.getWorld(worldName);
            if (world != null) {
                warps.put(name, new Warp(name, world, x, y, z, yaw, pitch));
            }
        }
    }

    public void save(File file) throws IOException {
        Objects.requireNonNull(file, "file");
        YamlConfiguration config = new YamlConfiguration();
        for (Warp warp : warps.values()) {
            String name = warp.getName();
            config.set(name + ".world", warp.getWorld().getName());
            config.set(name + ".x",     warp.getX());
            config.set(name + ".y",     warp.getY());
            config.set(name + ".z",     warp.getZ());
            config.set(name + ".yaw",   (double) warp.getYaw());
            config.set(name + ".pitch", (double) warp.getPitch());
        }
        config.save(file);
    }
}
