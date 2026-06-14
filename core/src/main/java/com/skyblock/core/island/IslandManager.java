package com.skyblock.core.island;

import org.bukkit.Location;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class IslandManager {

    public static final Map<String, Integer> UPGRADES;

    static {
        Map<String, Integer> m = new LinkedHashMap<>();
        m.put("size",    5);
        m.put("minions", 5);
        m.put("coops",   3);
        m.put("storage", 4);
        m.put("warps",   3);
        UPGRADES = Collections.unmodifiableMap(m);
    }

    private final Map<UUID, Location> islandHomes = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> islandUpgrades = new HashMap<>();

    public void setHome(UUID player, Location location) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(location, "location");
        islandHomes.put(player, location);
    }

    public Location getHome(UUID player) {
        Objects.requireNonNull(player, "player");
        return islandHomes.get(player);
    }

    public boolean hasHome(UUID player) {
        Objects.requireNonNull(player, "player");
        return islandHomes.containsKey(player);
    }

    public Map<UUID, Location> getIslandHomes() {
        return Collections.unmodifiableMap(islandHomes);
    }

    public int getUpgradeLevel(UUID player, String type) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(type, "type");
        return islandUpgrades.getOrDefault(player, Collections.emptyMap()).getOrDefault(type, 0);
    }

    public void setUpgradeLevel(UUID player, String type, int level) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(type, "type");
        islandUpgrades.computeIfAbsent(player, k -> new HashMap<>()).put(type, level);
    }

    public Map<String, Integer> getUpgrades(UUID player) {
        Objects.requireNonNull(player, "player");
        return Collections.unmodifiableMap(islandUpgrades.getOrDefault(player, Collections.emptyMap()));
    }
}
