package com.skyblock.core.crimsonisle;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class CrimsonIsleManager {

    public enum CrimsonFaction {
        MAGE("Mages"),
        BARBARIAN("Barbarians");

        private final String displayName;

        CrimsonFaction(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static final int MAX_REPUTATION = 10000;

    private static final CrimsonIsleManager INSTANCE = new CrimsonIsleManager();

    private final Map<UUID, CrimsonFaction> playerFactions = new HashMap<>();
    private final Map<UUID, Map<CrimsonFaction, Integer>> playerReputation = new HashMap<>();

    private CrimsonIsleManager() {}

    public static CrimsonIsleManager getInstance() {
        return INSTANCE;
    }

    public void setFaction(UUID playerId, CrimsonFaction faction) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        playerFactions.put(playerId, faction);
    }

    public CrimsonFaction getFaction(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerFactions.get(playerId);
    }

    public int addReputation(UUID playerId, CrimsonFaction faction, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<CrimsonFaction, Integer> repMap = playerReputation.computeIfAbsent(
                playerId, id -> new EnumMap<>(CrimsonFaction.class));
        int total = Math.min(repMap.getOrDefault(faction, 0) + amount, MAX_REPUTATION);
        repMap.put(faction, total);
        return total;
    }

    public int getReputation(UUID playerId, CrimsonFaction faction) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        Map<CrimsonFaction, Integer> repMap = playerReputation.get(playerId);
        return repMap == null ? 0 : repMap.getOrDefault(faction, 0);
    }

    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadData = playerFactions.remove(playerId) != null;
        hadData |= playerReputation.remove(playerId) != null;
        return hadData;
    }
}
