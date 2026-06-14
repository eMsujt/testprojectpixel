package com.skyblock.core.kuudra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KuudraManager {

    public enum KuudraTier {
        BASIC("Basic"),
        HOT("Hot"),
        BURNING("Burning"),
        FIERY("Fiery"),
        INFERNAL("Infernal");

        private final String displayName;

        KuudraTier(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // TIER_DATA: {essenceCost, tokenReward, suppliesCost}
    public static final Map<String, int[]> TIER_DATA;
    static {
        Map<String, int[]> m = new HashMap<>();
        m.put("BASIC",    new int[]{    0,  1,   0});
        m.put("HOT",      new int[]{   50,  2,  25});
        m.put("BURNING",  new int[]{  150,  3,  50});
        m.put("FIERY",    new int[]{  500,  5,  75});
        m.put("INFERNAL", new int[]{ 2000, 10, 100});
        TIER_DATA = Collections.unmodifiableMap(m);
    }

    private final Map<UUID, int[]> playerCompletions = new HashMap<>();
    private final Map<UUID, List<String>> kuudraHistory = new HashMap<>();

    public int getCompletions(UUID playerId, KuudraTier tier) {
        int[] c = playerCompletions.get(playerId);
        return c == null ? 0 : c[tier.ordinal()];
    }

    public void addCompletion(UUID playerId, KuudraTier tier) {
        int[] c = playerCompletions.computeIfAbsent(playerId, id -> new int[KuudraTier.values().length]);
        c[tier.ordinal()]++;
        recordKuudraEvent(playerId, "Completed " + tier.getDisplayName() + " Kuudra tier");
    }

    public void recordKuudraEvent(UUID playerId, String summary) {
        kuudraHistory.computeIfAbsent(playerId, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getKuudraHistory(UUID playerId) {
        return Collections.unmodifiableList(kuudraHistory.getOrDefault(playerId, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllKuudraHistory() {
        return Collections.unmodifiableMap(kuudraHistory);
    }
}
