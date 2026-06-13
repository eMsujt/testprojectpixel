package com.skyblock.core.kuudra;

import java.util.HashMap;
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

    private final Map<UUID, int[]> playerCompletions = new HashMap<>();

    public int getCompletions(UUID playerId, KuudraTier tier) {
        int[] c = playerCompletions.get(playerId);
        return c == null ? 0 : c[tier.ordinal()];
    }

    public void addCompletion(UUID playerId, KuudraTier tier) {
        int[] c = playerCompletions.computeIfAbsent(playerId, id -> new int[KuudraTier.values().length]);
        c[tier.ordinal()]++;
    }
}
