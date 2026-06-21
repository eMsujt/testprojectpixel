package com.skyblock.core.manager;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton tracking per-player sack contents, keyed by SackType.
 */
public final class SacksManager {

    public enum SackType {
        FARMING("Farming Sack"),
        MINING("Mining Sack"),
        COMBAT("Combat Sack"),
        FISHING("Fishing Sack"),
        FORAGING("Foraging Sack");

        private final String displayName;

        SackType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private static final SacksManager INSTANCE = new SacksManager();

    // player UUID → (SackType → (itemId → count))
    private final Map<UUID, EnumMap<SackType, Map<String, Integer>>> data = new HashMap<>();

    private SacksManager() {}

    public static SacksManager getInstance() {
        return INSTANCE;
    }

    public Map<String, Integer> getSackContents(UUID player, SackType type) {
        EnumMap<SackType, Map<String, Integer>> playerSacks = data.get(player);
        if (playerSacks == null) return Collections.emptyMap();
        Map<String, Integer> contents = playerSacks.get(type);
        return contents != null ? Collections.unmodifiableMap(contents) : Collections.emptyMap();
    }

    public int addItem(UUID player, SackType type, String itemId, int amount) {
        data.computeIfAbsent(player, k -> new EnumMap<>(SackType.class))
            .computeIfAbsent(type, k -> new HashMap<>())
            .merge(itemId, amount, Integer::sum);
        return 0;
    }

    public int getItemCount(UUID player, SackType type, String itemId) {
        EnumMap<SackType, Map<String, Integer>> playerSacks = data.get(player);
        if (playerSacks == null) return 0;
        Map<String, Integer> contents = playerSacks.get(type);
        return contents != null ? contents.getOrDefault(itemId, 0) : 0;
    }

    public boolean reset(UUID player) {
        return data.remove(player) != null;
    }
}
