package com.skyblock.core.booster;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class BoosterManager {

    public enum BoosterType {
        XP_BOOST(2.0, "XP Boost"),
        COIN_BOOST(1.5, "Coin Boost"),
        DROP_BOOST(1.75, "Drop Boost");

        private final double multiplier;
        private final String displayName;

        BoosterType(double multiplier, String displayName) {
            this.multiplier = multiplier;
            this.displayName = displayName;
        }

        public double getMultiplier() { return multiplier; }
        public String getDisplayName() { return displayName; }
    }

    private static final BoosterManager INSTANCE = new BoosterManager();

    private final Map<UUID, BoosterType> activeBoosters = new HashMap<>();

    private BoosterManager() {}

    public static BoosterManager getInstance() {
        return INSTANCE;
    }

    public void activate(UUID playerId, BoosterType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        activeBoosters.put(playerId, type);
    }

    public void deactivate(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        activeBoosters.remove(playerId);
    }

    public BoosterType getActive(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeBoosters.get(playerId);
    }

    public boolean hasActive(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeBoosters.containsKey(playerId);
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "boosters.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        activeBoosters.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String typeName = cfg.getString(key);
                if (typeName != null) {
                    try {
                        activeBoosters.put(uuid, BoosterType.valueOf(typeName));
                    } catch (IllegalArgumentException ignored) {
                        // skip unknown types
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "boosters.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, BoosterType> entry : activeBoosters.entrySet()) {
            cfg.set(entry.getKey().toString(), entry.getValue().name());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save boosters.yml", e);
        }
    }
}
