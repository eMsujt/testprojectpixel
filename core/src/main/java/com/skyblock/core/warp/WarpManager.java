package com.skyblock.core.warp;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WarpManager {

    private final Map<String, Location> warps = new HashMap<>();

    public WarpManager() {
        seedDefaults();
    }

    private void seedDefaults() {
        String[] defaults = {
            "hub", "barn", "mushroom_desert", "gold_mine", "deep_caverns",
            "dwarven_mines", "crystal_hollows", "the_park", "spiders_den",
            "blazing_fortress", "the_end", "crimson_isle", "the_rift", "dungeon_hub"
        };
        org.bukkit.World world = Bukkit.getWorld("world");
        for (String name : defaults) {
            warps.put(name, new Location(world, 0, 64, 0));
        }
    }

    public void setWarp(String name, Location location) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(location, "location");
        warps.put(name.toLowerCase(), location);
    }

    public Location getWarp(String name) {
        Objects.requireNonNull(name, "name");
        return warps.get(name.toLowerCase());
    }

    public boolean removeWarp(String name) {
        Objects.requireNonNull(name, "name");
        return warps.remove(name.toLowerCase()) != null;
    }

    public boolean hasWarp(String name) {
        Objects.requireNonNull(name, "name");
        return warps.containsKey(name.toLowerCase());
    }

    public Map<String, Location> getWarps() {
        return Collections.unmodifiableMap(warps);
    }
}
