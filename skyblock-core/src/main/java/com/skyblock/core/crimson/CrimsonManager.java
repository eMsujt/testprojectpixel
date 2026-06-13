package com.skyblock.core.crimson;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class CrimsonManager {

    public enum FactionType {
        MAGE("Mages"),
        BARBARIAN("Barbarians"),
        VANQUISHER("Vanquishers");

        private final String displayName;

        FactionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private static final CrimsonManager INSTANCE = new CrimsonManager();

    private final Map<UUID, FactionType> playerFactions = new HashMap<>();
    private final Map<UUID, Map<FactionType, Integer>> playerReputation = new HashMap<>();

    private CrimsonManager() {}

    public static CrimsonManager getInstance() {
        return INSTANCE;
    }

    public void setFaction(UUID playerId, FactionType faction) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        playerFactions.put(playerId, faction);
    }

    public FactionType getFaction(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerFactions.get(playerId);
    }

    public int addReputation(UUID playerId, FactionType faction, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        Map<FactionType, Integer> repMap = playerReputation.computeIfAbsent(
                playerId, id -> new EnumMap<>(FactionType.class));
        int total = repMap.getOrDefault(faction, 0) + amount;
        repMap.put(faction, total);
        return total;
    }

    public int getReputation(UUID playerId, FactionType faction) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(faction, "faction");
        Map<FactionType, Integer> repMap = playerReputation.get(playerId);
        return repMap == null ? 0 : repMap.getOrDefault(faction, 0);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "crimson.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        playerFactions.clear();
        playerReputation.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID id = UUID.fromString(key);
                String factionStr = cfg.getString(key + ".faction");
                if (factionStr != null) {
                    try {
                        playerFactions.put(id, FactionType.valueOf(factionStr));
                    } catch (IllegalArgumentException ignored) {}
                }
                if (cfg.isConfigurationSection(key + ".reputation")) {
                    Map<FactionType, Integer> repMap = new EnumMap<>(FactionType.class);
                    for (FactionType f : FactionType.values()) {
                        int rep = cfg.getInt(key + ".reputation." + f.name(), 0);
                        if (rep > 0) repMap.put(f, rep);
                    }
                    if (!repMap.isEmpty()) playerReputation.put(id, repMap);
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
        for (Map.Entry<UUID, Map<FactionType, Integer>> entry : playerReputation.entrySet()) {
            for (Map.Entry<FactionType, Integer> rep : entry.getValue().entrySet()) {
                cfg.set(entry.getKey() + ".reputation." + rep.getKey().name(), rep.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save crimson.yml", e);
        }
    }
}
