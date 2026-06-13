package com.skyblock.core.sack;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class SacksManager {

    public enum SackType {
        MINING("Mining"),
        FARMING("Farming"),
        COMBAT("Combat"),
        ENCHANTING("Enchanting"),
        FISHING("Fishing"),
        FORAGING("Foraging");

        private final String displayName;

        SackType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private static final SacksManager INSTANCE = new SacksManager();

    private final Map<UUID, Map<SackType, Map<String, Integer>>> sackContents = new HashMap<>();

    private SacksManager() {}

    public static SacksManager getInstance() {
        return INSTANCE;
    }

    public int addItem(UUID playerId, SackType sackType, String itemId, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(sackType, "sackType");
        Objects.requireNonNull(itemId, "itemId");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<String, Integer> contents = getOrCreateContents(playerId, sackType);
        int total = contents.getOrDefault(itemId, 0) + amount;
        contents.put(itemId, total);
        return total;
    }

    public int removeItem(UUID playerId, SackType sackType, String itemId, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(sackType, "sackType");
        Objects.requireNonNull(itemId, "itemId");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<String, Integer> contents = getOrCreateContents(playerId, sackType);
        int current = contents.getOrDefault(itemId, 0);
        int remaining = Math.max(0, current - amount);
        if (remaining == 0) {
            contents.remove(itemId);
        } else {
            contents.put(itemId, remaining);
        }
        return remaining;
    }

    public int getItemCount(UUID playerId, SackType sackType, String itemId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(sackType, "sackType");
        Objects.requireNonNull(itemId, "itemId");
        Map<UUID, Map<SackType, Map<String, Integer>>> allSacks = sackContents;
        Map<SackType, Map<String, Integer>> playerSacks = allSacks.get(playerId);
        if (playerSacks == null) return 0;
        Map<String, Integer> contents = playerSacks.get(sackType);
        return contents == null ? 0 : contents.getOrDefault(itemId, 0);
    }

    public Map<String, Integer> getSackContents(UUID playerId, SackType sackType) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(sackType, "sackType");
        Map<SackType, Map<String, Integer>> playerSacks = sackContents.get(playerId);
        if (playerSacks == null) return java.util.Collections.emptyMap();
        Map<String, Integer> contents = playerSacks.get(sackType);
        return contents == null ? java.util.Collections.emptyMap() : java.util.Collections.unmodifiableMap(contents);
    }

    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return sackContents.remove(playerId) != null;
    }

    private Map<String, Integer> getOrCreateContents(UUID playerId, SackType sackType) {
        return sackContents
                .computeIfAbsent(playerId, id -> new EnumMap<>(SackType.class))
                .computeIfAbsent(sackType, t -> new HashMap<>());
    }
}
