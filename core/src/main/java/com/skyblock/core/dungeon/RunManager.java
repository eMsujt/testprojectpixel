package com.skyblock.core.dungeon;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RunManager {

    private final Map<UUID, Map<String, Integer>> runStats = new HashMap<>();

    public int getStat(UUID playerId, String stat) {
        Map<String, Integer> stats = runStats.get(playerId);
        return stats == null ? 0 : stats.getOrDefault(stat, 0);
    }

    public void setStat(UUID playerId, String stat, int value) {
        runStats.computeIfAbsent(playerId, id -> new HashMap<>()).put(stat, value);
    }

    public void incrementStat(UUID playerId, String stat) {
        runStats.computeIfAbsent(playerId, id -> new HashMap<>())
                .merge(stat, 1, Integer::sum);
    }

    public Map<String, Integer> getStats(UUID playerId) {
        Map<String, Integer> stats = runStats.get(playerId);
        return stats == null ? Collections.emptyMap() : Collections.unmodifiableMap(stats);
    }

    public void clearStats(UUID playerId) {
        runStats.remove(playerId);
    }

    public Map<UUID, Map<String, Integer>> getAllRunStats() {
        return Collections.unmodifiableMap(runStats);
    }
}
