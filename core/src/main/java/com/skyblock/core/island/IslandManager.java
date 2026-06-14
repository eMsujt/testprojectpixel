package com.skyblock.core.island;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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

    public static final Map<String, int[]> UPGRADE_DATA;

    static {
        Map<String, int[]> m = new LinkedHashMap<>();
        m.put("MINION_SLOTS",   new int[]{10, 500});
        m.put("COOP_SLOTS",     new int[]{4,  1000});
        m.put("ISLAND_SIZE",    new int[]{5,  2000});
        m.put("CHEST_SIZE",     new int[]{5,  750});
        m.put("GUEST_LIMIT",    new int[]{4,  250});
        m.put("REDSTONE_LIMIT", new int[]{5,  500});
        m.put("CROP_GROWTH",    new int[]{5,  1500});
        m.put("MOB_SPAWN_RATE", new int[]{5,  1500});
        m.put("BEACON_RANGE",   new int[]{3,  2500});
        m.put("WARP_LIMIT",     new int[]{3,  1000});
        m.put("BANK_SIZE",      new int[]{5,  3000});
        m.put("STORAGE",        new int[]{4,  2000});
        UPGRADE_DATA = Collections.unmodifiableMap(m);
    }

    private final Map<UUID, Location> islandHomes = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> islandUpgrades = new HashMap<>();
    private final Map<UUID, List<String>> islandHistory = new HashMap<>();

    public void recordIslandEvent(UUID playerUuid, String summary) {
        islandHistory.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getIslandHistory(UUID playerUuid) {
        return Collections.unmodifiableList(islandHistory.getOrDefault(playerUuid, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllIslandHistory() {
        return Collections.unmodifiableMap(islandHistory);
    }

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
        recordIslandEvent(player, "Upgrade set: " + type + " -> " + level);
    }

    public Map<String, Integer> getUpgrades(UUID player) {
        Objects.requireNonNull(player, "player");
        return Collections.unmodifiableMap(islandUpgrades.getOrDefault(player, Collections.emptyMap()));
    }
}
