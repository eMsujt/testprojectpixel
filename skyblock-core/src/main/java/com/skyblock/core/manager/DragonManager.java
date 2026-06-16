package com.skyblock.core.manager;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * The End dragon fight: players fill summoning eyes to spawn one of the
 * Ender Dragon types, deal damage during the fight, and earn completion
 * credit when the dragon is defeated.
 */
public final class DragonManager {

    /** The summonable Ender Dragon types and their base health. */
    public enum DragonType {
        PROTECTOR("Protector", 4_000_000L),
        OLD("Old", 4_000_000L),
        WISE("Wise", 4_000_000L),
        UNSTABLE("Unstable", 4_000_000L),
        YOUNG("Young", 4_000_000L),
        STRONG("Strong", 4_000_000L),
        SUPERIOR("Superior", 8_000_000L);

        private final String displayName;
        private final long baseHealth;

        DragonType(String displayName, long baseHealth) {
            this.displayName = displayName;
            this.baseHealth = baseHealth;
        }

        public String getDisplayName() {
            return displayName;
        }

        public long getBaseHealth() {
            return baseHealth;
        }
    }

    /** A single active dragon fight, tracking remaining health and per-player damage. */
    public static final class DragonFight {
        private final DragonType type;
        private final long maxHealth;
        private final long startTime;
        private long remainingHealth;
        private final Map<UUID, Long> damage = new HashMap<>();

        public DragonFight(DragonType type, long startTime) {
            this.type = type;
            this.maxHealth = type.getBaseHealth();
            this.remainingHealth = this.maxHealth;
            this.startTime = startTime;
        }

        public DragonType getType() { return type; }
        public long getMaxHealth() { return maxHealth; }
        public long getRemainingHealth() { return remainingHealth; }
        public long getStartTime() { return startTime; }

        public boolean isDefeated() {
            return remainingHealth <= 0;
        }

        /**
         * Apply damage from a player, crediting their contribution and reducing
         * the dragon's remaining health (never below zero).
         *
         * @return the dragon's remaining health after the hit
         * @throws IllegalArgumentException if amount is negative
         * @throws IllegalStateException    if the dragon is already defeated
         */
        public long dealDamage(UUID playerId, long amount) {
            Objects.requireNonNull(playerId, "playerId");
            if (amount < 0) {
                throw new IllegalArgumentException("damage amount cannot be negative");
            }
            if (isDefeated()) {
                throw new IllegalStateException("Dragon is already defeated.");
            }
            long applied = Math.min(amount, remainingHealth);
            remainingHealth -= applied;
            damage.merge(playerId, applied, Long::sum);
            return remainingHealth;
        }

        public long getDamageBy(UUID playerId) {
            Objects.requireNonNull(playerId, "playerId");
            return damage.getOrDefault(playerId, 0L);
        }

        public Map<UUID, Long> getDamageContributions() {
            return Collections.unmodifiableMap(damage);
        }
    }

    /** Summoning eyes required to spawn a dragon. */
    public static final int EYES_REQUIRED = 8;

    private static final DragonManager INSTANCE = new DragonManager();

    private final Map<UUID, DragonType> completions = new LinkedHashMap<>();
    private DragonFight activeFight;
    private int placedEyes;

    private DragonManager() {}

    public static DragonManager getInstance() {
        return INSTANCE;
    }

    /**
     * Place a summoning eye. Once {@link #EYES_REQUIRED} eyes are placed the
     * given dragon type is summoned and a new fight begins.
     *
     * @return true if this eye triggered the summon
     * @throws IllegalStateException if a fight is already in progress
     */
    public boolean placeEye(DragonType type, long startTime) {
        Objects.requireNonNull(type, "type");
        if (activeFight != null) {
            throw new IllegalStateException("A dragon fight is already in progress.");
        }
        if (placedEyes < EYES_REQUIRED) {
            placedEyes++;
        }
        if (placedEyes >= EYES_REQUIRED) {
            activeFight = new DragonFight(type, startTime);
            placedEyes = 0;
            return true;
        }
        return false;
    }

    public int getPlacedEyes() {
        return placedEyes;
    }

    public DragonFight getActiveFight() {
        return activeFight;
    }

    /**
     * Apply damage to the active dragon. When the dragon's health reaches zero
     * it is defeated, the fight ends, and every contributor is credited with a
     * completion of that dragon type.
     *
     * @return the dragon's remaining health after the hit
     * @throws IllegalStateException if there is no active fight
     */
    public long dealDamage(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (activeFight == null) {
            throw new IllegalStateException("There is no active dragon fight.");
        }
        long remaining = activeFight.dealDamage(playerId, amount);
        if (activeFight.isDefeated()) {
            for (UUID contributor : activeFight.getDamageContributions().keySet()) {
                completions.put(contributor, activeFight.getType());
            }
            activeFight = null;
        }
        return remaining;
    }

    /** The last dragon type a player defeated, or null if they have none. */
    public DragonType getLastCompletion(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return completions.get(playerId);
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "dragon.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        completions.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                DragonType type = DragonType.valueOf(cfg.getString(key, ""));
                completions.put(uuid, type);
            } catch (IllegalArgumentException ignored) {
                // skip malformed UUIDs or unknown dragon types
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "dragon.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, DragonType> entry : completions.entrySet()) {
            cfg.set(entry.getKey().toString(), entry.getValue().name());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save dragon.yml", e);
        }
    }
}
