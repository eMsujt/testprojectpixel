package com.skyblock.plugin.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class IslandManager {

    private static final IslandManager INSTANCE = new IslandManager();

    private final Map<UUID, Integer> visitorCounts = new HashMap<>();

    private IslandManager() {}

    public static IslandManager getInstance() {
        return INSTANCE;
    }

    public int getVisitorCount(UUID playerId) {
        return visitorCounts.getOrDefault(playerId, 0);
    }

    public void addVisitor(UUID islandOwner) {
        visitorCounts.merge(islandOwner, 1, Integer::sum);
    }

    public void setVisitorCount(UUID islandOwner, int count) {
        visitorCounts.put(islandOwner, count);
    }

    public Map<UUID, Integer> getVisitorCounts() {
        return visitorCounts;
    }
}
