package com.skyblock.core.kuudra;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class KuudraManager {

    public enum KuudraTier {
        BASIC("Basic"), HOT("Hot"), BURNING("Burning"), FIERY("Fiery"), INFERNAL("Infernal");

        private final String displayName;

        KuudraTier(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static final class KuudraRun {
        private final KuudraTier tier;
        private final List<UUID> participants;
        private final long startTime;

        public KuudraRun(KuudraTier tier, List<UUID> participants, long startTime) {
            this.tier = tier;
            this.participants = Collections.unmodifiableList(participants);
            this.startTime = startTime;
        }

        public KuudraTier getTier() { return tier; }
        public List<UUID> getParticipants() { return participants; }
        public long getStartTime() { return startTime; }
    }

    private static final KuudraManager INSTANCE = new KuudraManager();

    private final Map<UUID, KuudraRun> activeRuns = new HashMap<>();
    private final Map<UUID, Map<KuudraTier, Integer>> completions = new HashMap<>();

    private KuudraManager() {}

    public static KuudraManager getInstance() {
        return INSTANCE;
    }

    public void joinRun(KuudraTier tier, List<UUID> participants, long startTime) {
        Objects.requireNonNull(tier, "tier");
        Objects.requireNonNull(participants, "participants");
        KuudraRun run = new KuudraRun(tier, participants, startTime);
        for (UUID id : participants) {
            activeRuns.put(id, run);
        }
    }

    public void leaveRun(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        activeRuns.remove(playerId);
    }

    public void completeRun(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        KuudraRun run = activeRuns.remove(playerId);
        if (run == null) {
            throw new IllegalStateException("Player is not in a Kuudra run.");
        }
        completions.computeIfAbsent(playerId, k -> new HashMap<>())
                .merge(run.getTier(), 1, Integer::sum);
    }

    public KuudraRun getActiveRun(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeRuns.get(playerId);
    }

    public int getCompletionCount(UUID playerId, KuudraTier tier) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        Map<KuudraTier, Integer> counts = completions.get(playerId);
        return counts == null ? 0 : counts.getOrDefault(tier, 0);
    }

    public Map<KuudraTier, Integer> getAllCompletions(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<KuudraTier, Integer> counts = completions.get(playerId);
        return counts == null ? Collections.emptyMap() : Collections.unmodifiableMap(counts);
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "kuudra.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        completions.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (cfg.isConfigurationSection(key)) {
                    Map<KuudraTier, Integer> counts = new HashMap<>();
                    for (String tierName : cfg.getConfigurationSection(key).getKeys(false)) {
                        try {
                            KuudraTier tier = KuudraTier.valueOf(tierName);
                            int val = cfg.getInt(key + "." + tierName, 0);
                            if (val > 0) {
                                counts.put(tier, val);
                            }
                        } catch (IllegalArgumentException ignored) {
                            // skip unknown tier names
                        }
                    }
                    if (!counts.isEmpty()) {
                        completions.put(uuid, counts);
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed UUIDs
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "kuudra.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<KuudraTier, Integer>> entry : completions.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<KuudraTier, Integer> e : entry.getValue().entrySet()) {
                cfg.set(key + "." + e.getKey().name(), e.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save kuudra.yml", e);
        }
    }
}
