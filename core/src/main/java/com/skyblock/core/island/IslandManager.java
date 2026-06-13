package com.skyblock.core.island;

import org.bukkit.Location;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class IslandManager {

    private final Map<UUID, Location> islandHomes = new HashMap<>();

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
}
