package com.skyblock.core.manager;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Canonical Crimson Isle faction-reputation manager.
 *
 * <p>Tracks each player's chosen faction (Mages or Barbarians), per-faction
 * reputation (clamped to {@code [MIN_REPUTATION, MAX_REPUTATION]}), the
 * reputation tier that reputation maps to, and quest-driven reputation gain.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class ReputationManager {

    /** The two Crimson Isle factions a player can align with. */
    public enum Faction {
        MAGE("Mages"),
        BARBARIAN("Barbarians");

        private final String displayName;

        Faction(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Reputation tiers, ordered from lowest to highest. The {@code minReputation}
     * is the inclusive lower bound of reputation that maps to the tier.
     */
    public enum ReputationTier {
        HATED("Hated", -12000),
        HOSTILE("Hostile", -6000),
        DISTASTEFUL("Distasteful", -3000),
        COLD("Cold", -1000),
        NEUTRAL("Neutral", 0),
        CORDIAL("Cordial", 1000),
        FRIENDLY("Friendly", 3000),
        HONORED("Honored", 6000),
        TRUSTED("Trusted", 9000),
        RESPECTED("Respected", 12000);

        private final String displayName;
        private final int minReputation;

        ReputationTier(String displayName, int minReputation) {
            this.displayName = displayName;
            this.minReputation = minReputation;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getMinReputation() {
            return minReputation;
        }
    }

    public static final int MAX_REPUTATION = 12000;
    public static final int MIN_REPUTATION = -12000;

    private static final ReputationManager INSTANCE = new ReputationManager();

    private final Map<UUID, Faction> playerFactions = new HashMap<>();
    private final Map<UUID, Map<Faction, Integer>> playerReputation = new HashMap<>();
    private final Map<UUID, Map<Faction, Integer>> questsCompleted = new HashMap<>();

    private ReputationManager() {}

    public static ReputationManager getInstance() {
        return INSTANCE;
    }

    public void setFaction(UUID playerId, Faction faction) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        playerFactions.put(playerId, faction);
    }

    public Faction getFaction(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerFactions.get(playerId);
    }

    /**
     * Adds reputation with the given faction, clamping the result to
     * {@code [MIN_REPUTATION, MAX_REPUTATION]}.
     *
     * @param amount the amount to add; may be negative
     * @return the new reputation total
     */
    public int addReputation(UUID playerId, Faction faction, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        Map<Faction, Integer> repMap = playerReputation.computeIfAbsent(
                playerId, id -> new EnumMap<>(Faction.class));
        int total = repMap.getOrDefault(faction, 0) + amount;
        total = Math.max(MIN_REPUTATION, Math.min(total, MAX_REPUTATION));
        repMap.put(faction, total);
        return total;
    }

    public int getReputation(UUID playerId, Faction faction) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        Map<Faction, Integer> repMap = playerReputation.get(playerId);
        return repMap == null ? 0 : repMap.getOrDefault(faction, 0);
    }

    /**
     * Records completion of a faction quest: awards {@code reputationReward}
     * reputation (clamped) and increments the player's completed-quest tally
     * for that faction.
     *
     * @param reputationReward the reputation granted by the quest; must not be negative
     * @return the new reputation total
     */
    public int completeQuest(UUID playerId, Faction faction, int reputationReward) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        if (reputationReward < 0) {
            throw new IllegalArgumentException("reputationReward must not be negative, got " + reputationReward);
        }
        questsCompleted.computeIfAbsent(playerId, id -> new EnumMap<>(Faction.class))
                .merge(faction, 1, Integer::sum);
        return addReputation(playerId, faction, reputationReward);
    }

    public int getQuestsCompleted(UUID playerId, Faction faction) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        Map<Faction, Integer> map = questsCompleted.get(playerId);
        return map == null ? 0 : map.getOrDefault(faction, 0);
    }

    /**
     * Returns the reputation tier the player currently sits in for the faction.
     *
     * @return the matching {@link ReputationTier}
     */
    public ReputationTier getReputationTier(UUID playerId, Faction faction) {
        int rep = getReputation(playerId, faction);
        ReputationTier result = ReputationTier.HATED;
        for (ReputationTier tier : ReputationTier.values()) {
            if (rep >= tier.getMinReputation()) {
                result = tier;
            } else {
                break;
            }
        }
        return result;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "crimson.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        playerFactions.clear();
        playerReputation.clear();
        questsCompleted.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID id = UUID.fromString(key);
                String factionStr = cfg.getString(key + ".faction");
                if (factionStr != null) {
                    try {
                        playerFactions.put(id, Faction.valueOf(factionStr));
                    } catch (IllegalArgumentException ignored) {}
                }
                if (cfg.isConfigurationSection(key + ".reputation")) {
                    Map<Faction, Integer> repMap = new EnumMap<>(Faction.class);
                    for (Faction f : Faction.values()) {
                        int rep = cfg.getInt(key + ".reputation." + f.name(), 0);
                        if (rep != 0) repMap.put(f, rep);
                    }
                    if (!repMap.isEmpty()) playerReputation.put(id, repMap);
                }
                if (cfg.isConfigurationSection(key + ".quests")) {
                    Map<Faction, Integer> questMap = new EnumMap<>(Faction.class);
                    for (Faction f : Faction.values()) {
                        int done = cfg.getInt(key + ".quests." + f.name(), 0);
                        if (done > 0) questMap.put(f, done);
                    }
                    if (!questMap.isEmpty()) questsCompleted.put(id, questMap);
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "crimson.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (UUID id : playerFactions.keySet()) {
            cfg.set(id + ".faction", playerFactions.get(id).name());
        }
        for (Map.Entry<UUID, Map<Faction, Integer>> entry : playerReputation.entrySet()) {
            for (Map.Entry<Faction, Integer> rep : entry.getValue().entrySet()) {
                cfg.set(entry.getKey() + ".reputation." + rep.getKey().name(), rep.getValue());
            }
        }
        for (Map.Entry<UUID, Map<Faction, Integer>> entry : questsCompleted.entrySet()) {
            for (Map.Entry<Faction, Integer> quest : entry.getValue().entrySet()) {
                cfg.set(entry.getKey() + ".quests." + quest.getKey().name(), quest.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save crimson.yml", e);
        }
    }
}
