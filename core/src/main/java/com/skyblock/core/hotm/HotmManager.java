package com.skyblock.core.hotm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HotmManager {

    public enum HotmUpgrade {
        MINING_SPEED_BOOST("Mining Speed Boost", 1),
        VEIN_SEEKER("Vein Seeker", 1),
        MANIAC_MINER("Maniac Miner", 1),
        EFFICIENT_MINER("Efficient Miner", 10),
        MINING_FORTUNE("Mining Fortune", 10),
        QUICK_FORGE("Quick Forge", 20),
        TITANIUM_INSANIUM("Titanium Insanium", 5),
        PROFESSIONAL("Professional", 20),
        LONESOME_MINER("Lonesome Miner", 45),
        GREAT_EXPLORER("Great Explorer", 20),
        GOBLIN_KILLER("Goblin Killer", 1);

        private final String displayName;
        private final int maxLevel;

        HotmUpgrade(String displayName, int maxLevel) {
            this.displayName = displayName;
            this.maxLevel = maxLevel;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getMaxLevel() {
            return maxLevel;
        }
    }

    private final Map<UUID, int[]> playerLevels = new HashMap<>();
    private final Map<UUID, List<String>> hotmHistory = new HashMap<>();

    public int getLevel(UUID playerId, HotmUpgrade upgrade) {
        int[] levels = playerLevels.get(playerId);
        return levels == null ? 0 : levels[upgrade.ordinal()];
    }

    public boolean upgrade(UUID playerId, HotmUpgrade upgrade) {
        int[] levels = playerLevels.computeIfAbsent(playerId, id -> new int[HotmUpgrade.values().length]);
        if (levels[upgrade.ordinal()] >= upgrade.getMaxLevel()) {
            return false;
        }
        levels[upgrade.ordinal()]++;
        return true;
    }

    public void reset(UUID playerId) {
        playerLevels.remove(playerId);
    }

    public void recordHotmEvent(UUID playerId, String summary) {
        hotmHistory.computeIfAbsent(playerId, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getHotmHistory(UUID playerId) {
        return Collections.unmodifiableList(hotmHistory.getOrDefault(playerId, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllHotmHistory() {
        return Collections.unmodifiableMap(hotmHistory);
    }
}
