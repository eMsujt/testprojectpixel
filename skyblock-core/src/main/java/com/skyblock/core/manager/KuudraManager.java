package com.skyblock.core.manager;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class KuudraManager {

    public enum KuudraTier {
        BASIC("Basic", 1, 700),
        HOT("Hot", 2, 800),
        BURNING("Burning", 3, 900),
        FIERY("Fiery", 4, 1000),
        INFERNAL("Infernal", 5, 1200);

        private final String displayName;
        private final int tier;
        private final int contributionThreshold;

        KuudraTier(String displayName, int tier, int contributionThreshold) {
            this.displayName = displayName;
            this.tier = tier;
            this.contributionThreshold = contributionThreshold;
        }

        public String getDisplayName() {
            return displayName;
        }

        /** The escalation tier number, 1 (Basic) through 5 (Infernal). */
        public int getTier() {
            return tier;
        }

        /** Minimum contribution score required to receive full loot rewards for this tier. */
        public int getContributionThreshold() {
            return contributionThreshold;
        }
    }

    /** The sequential combat phases of a Kuudra fight, in fight order. */
    public enum KuudraPhase {
        BUILD("Build"), SUPPLY("Supply"), DPS("DPS"), BURN("Burn");

        private final String displayName;

        KuudraPhase(String displayName) {
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
        private KuudraPhase phase = KuudraPhase.BUILD;

        public KuudraRun(KuudraTier tier, List<UUID> participants, long startTime) {
            this.tier = tier;
            this.participants = Collections.unmodifiableList(participants);
            this.startTime = startTime;
        }

        public KuudraTier getTier() { return tier; }
        public List<UUID> getParticipants() { return participants; }
        public long getStartTime() { return startTime; }
        public KuudraPhase getPhase() { return phase; }

        /**
         * Advance to the next combat phase (BUILD → SUPPLY → DPS → BURN).
         *
         * @return the phase now in progress
         * @throws IllegalStateException if the run is already in the final BURN phase
         */
        public KuudraPhase advancePhase() {
            KuudraPhase[] phases = KuudraPhase.values();
            if (phase.ordinal() >= phases.length - 1) {
                throw new IllegalStateException("Kuudra run is already in the final phase.");
            }
            phase = phases[phase.ordinal() + 1];
            return phase;
        }

        /** True once the run has reached and is in the final BURN phase. */
        public boolean isFinalPhase() {
            return phase == KuudraPhase.BURN;
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

    private static final KuudraManager INSTANCE = new KuudraManager();

    private final Map<UUID, KuudraRun> activeRuns = new HashMap<>();
    private final Map<UUID, Map<KuudraTier, Integer>> completions = new HashMap<>();
    private final Map<UUID, List<String>> kuudraHistory = new HashMap<>();

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

    /**
     * Advance the combat phase of the player's active run.
     *
     * @return the phase now in progress
     * @throws IllegalStateException if the player is not in a run
     */
    public KuudraPhase advancePhase(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        KuudraRun run = activeRuns.get(playerId);
        if (run == null) {
            throw new IllegalStateException("Player is not in a Kuudra run.");
        }
        return run.advancePhase();
    }

    public void completeRun(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        KuudraRun run = activeRuns.get(playerId);
        if (run == null) {
            throw new IllegalStateException("Player is not in a Kuudra run.");
        }
        if (!run.isFinalPhase()) {
            throw new IllegalStateException("Kuudra run must reach the BURN phase before it can be completed.");
        }
        activeRuns.remove(playerId);
        completions.computeIfAbsent(playerId, k -> new HashMap<>())
                .merge(run.getTier(), 1, Integer::sum);
        recordKuudraEvent(playerId, "Completed " + run.getTier().getDisplayName() + " Kuudra run");
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

    public void recordKuudraEvent(UUID playerUuid, String summary) {
        kuudraHistory.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getKuudraHistory(UUID playerUuid) {
        Objects.requireNonNull(playerUuid, "playerUuid");
        return Collections.unmodifiableList(kuudraHistory.getOrDefault(playerUuid, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllKuudraHistory() {
        return Collections.unmodifiableMap(kuudraHistory);
    }

    public String getKuudraStats(UUID playerId) {
        Map<KuudraTier, Integer> counts = getAllCompletions(playerId);
        return "Basic: " + counts.getOrDefault(KuudraTier.BASIC, 0)
                + ", Hot: " + counts.getOrDefault(KuudraTier.HOT, 0)
                + ", Burning: " + counts.getOrDefault(KuudraTier.BURNING, 0)
                + ", Fiery: " + counts.getOrDefault(KuudraTier.FIERY, 0)
                + ", Infernal: " + counts.getOrDefault(KuudraTier.INFERNAL, 0);
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
