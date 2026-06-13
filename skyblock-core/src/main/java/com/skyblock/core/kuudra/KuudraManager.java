package com.skyblock.core.kuudra;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's Kuudra key counts and completion counts per tier.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class KuudraManager {

    /** Kuudra tiers, in ascending difficulty order. */
    public enum KuudraTier {
        BASIC("Basic"),
        HOT("Hot"),
        BURNING("Burning"),
        FIERY("Fiery"),
        INFERNAL("Infernal");

        /** Human-readable display name shown to players. */
        public final String displayName;

        KuudraTier(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private static final KuudraManager INSTANCE = new KuudraManager();

    /** Per-player Kuudra key counts indexed by tier ordinal. */
    private final Map<UUID, int[]> playerKeys = new HashMap<>();

    /** Per-player Kuudra completion counts indexed by tier ordinal. */
    private final Map<UUID, int[]> playerCompletions = new HashMap<>();

    /** Per-player Kuudra kill counts indexed by tier ordinal. */
    private final Map<UUID, int[]> playerKills = new HashMap<>();

    private KuudraManager() {
    }

    /**
     * Returns the single shared {@code KuudraManager} instance.
     *
     * @return the singleton instance
     */
    public static KuudraManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Key management
    // -------------------------------------------------------------------------

    /**
     * Returns the number of Kuudra keys the player has for the given tier.
     *
     * @param playerId the player to look up
     * @param tier     the Kuudra tier
     * @return the key count, {@code 0} if not set
     */
    public int getKeys(UUID playerId, KuudraTier tier) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        int[] keys = playerKeys.get(playerId);
        return keys == null ? 0 : keys[tier.ordinal()];
    }

    /**
     * Adds keys for the given tier (clamped to {@code >= 0}).
     *
     * @param playerId the player to update
     * @param tier     the Kuudra tier
     * @param amount   the amount to add (may be negative)
     * @return the new key count
     */
    public int addKeys(UUID playerId, KuudraTier tier, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        int[] keys = playerKeys.computeIfAbsent(playerId, id -> new int[KuudraTier.values().length]);
        keys[tier.ordinal()] = Math.max(0, keys[tier.ordinal()] + amount);
        return keys[tier.ordinal()];
    }

    /**
     * Sets the key count for the given tier.
     *
     * @param playerId the player to update
     * @param tier     the Kuudra tier
     * @param count    the new count (clamped to {@code >= 0})
     */
    public void setKeys(UUID playerId, KuudraTier tier, int count) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        int[] keys = playerKeys.computeIfAbsent(playerId, id -> new int[KuudraTier.values().length]);
        keys[tier.ordinal()] = Math.max(0, count);
    }

    // -------------------------------------------------------------------------
    // Completion management
    // -------------------------------------------------------------------------

    /**
     * Returns the number of Kuudra completions the player has for the given tier.
     *
     * @param playerId the player to look up
     * @param tier     the Kuudra tier
     * @return the completion count, {@code 0} if not set
     */
    public int getCompletions(UUID playerId, KuudraTier tier) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        int[] completions = playerCompletions.get(playerId);
        return completions == null ? 0 : completions[tier.ordinal()];
    }

    /**
     * Adds completions for the given tier (clamped to {@code >= 0}).
     *
     * @param playerId the player to update
     * @param tier     the Kuudra tier
     * @param amount   the amount to add (may be negative)
     * @return the new completion count
     */
    public int addCompletions(UUID playerId, KuudraTier tier, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        int[] completions = playerCompletions.computeIfAbsent(playerId, id -> new int[KuudraTier.values().length]);
        completions[tier.ordinal()] = Math.max(0, completions[tier.ordinal()] + amount);
        return completions[tier.ordinal()];
    }

    // -------------------------------------------------------------------------
    // Kill count management
    // -------------------------------------------------------------------------

    /**
     * Returns the number of Kuudra kills the player has for the given tier.
     *
     * @param playerId the player to look up
     * @param tier     the Kuudra tier
     * @return the kill count, {@code 0} if not set
     */
    public int getKillCount(UUID playerId, KuudraTier tier) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        int[] kills = playerKills.get(playerId);
        return kills == null ? 0 : kills[tier.ordinal()];
    }

    /**
     * Increments the kill count for the given tier by one and returns the new total.
     *
     * @param playerId the player to update
     * @param tier     the Kuudra tier
     * @return the new kill count
     */
    public int addKill(UUID playerId, KuudraTier tier) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        int[] kills = playerKills.computeIfAbsent(playerId, id -> new int[KuudraTier.values().length]);
        return ++kills[tier.ordinal()];
    }

    /**
     * Adds the given amount to the kill count for the given tier (clamped to {@code >= 0}).
     *
     * @param playerId the player to update
     * @param tier     the Kuudra tier
     * @param amount   the amount to add (may be negative)
     * @return the new kill count
     */
    public int addKills(UUID playerId, KuudraTier tier, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        int[] kills = playerKills.computeIfAbsent(playerId, id -> new int[KuudraTier.values().length]);
        kills[tier.ordinal()] = Math.max(0, kills[tier.ordinal()] + amount);
        return kills[tier.ordinal()];
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /**
     * Resets all Kuudra data for the given player.
     *
     * @param playerId the player to reset
     */
    public void reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        playerKeys.remove(playerId);
        playerCompletions.remove(playerId);
        playerKills.remove(playerId);
    }

    /**
     * Removes all Kuudra data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean had = playerKeys.remove(playerId) != null;
        had |= playerCompletions.remove(playerId) != null;
        had |= playerKills.remove(playerId) != null;
        return had;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "kuudra.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        playerKeys.clear();
        playerCompletions.clear();
        playerKills.clear();
        KuudraTier[] tiers = KuudraTier.values();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (cfg.isConfigurationSection(key + ".keys")) {
                    int[] keys = new int[tiers.length];
                    for (KuudraTier tier : tiers) {
                        keys[tier.ordinal()] = cfg.getInt(key + ".keys." + tier.name(), 0);
                    }
                    playerKeys.put(uuid, keys);
                }
                if (cfg.isConfigurationSection(key + ".completions")) {
                    int[] completions = new int[tiers.length];
                    for (KuudraTier tier : tiers) {
                        completions[tier.ordinal()] = cfg.getInt(key + ".completions." + tier.name(), 0);
                    }
                    playerCompletions.put(uuid, completions);
                }
                if (cfg.isConfigurationSection(key + ".kills")) {
                    int[] kills = new int[tiers.length];
                    for (KuudraTier tier : tiers) {
                        kills[tier.ordinal()] = cfg.getInt(key + ".kills." + tier.name(), 0);
                    }
                    playerKills.put(uuid, kills);
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "kuudra.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        KuudraTier[] tiers = KuudraTier.values();
        for (Map.Entry<UUID, int[]> entry : playerKeys.entrySet()) {
            String key = entry.getKey().toString();
            int[] keys = entry.getValue();
            for (KuudraTier tier : tiers) {
                cfg.set(key + ".keys." + tier.name(), keys[tier.ordinal()]);
            }
        }
        for (Map.Entry<UUID, int[]> entry : playerCompletions.entrySet()) {
            String key = entry.getKey().toString();
            int[] completions = entry.getValue();
            for (KuudraTier tier : tiers) {
                cfg.set(key + ".completions." + tier.name(), completions[tier.ordinal()]);
            }
        }
        for (Map.Entry<UUID, int[]> entry : playerKills.entrySet()) {
            String key = entry.getKey().toString();
            int[] kills = entry.getValue();
            for (KuudraTier tier : tiers) {
                cfg.set(key + ".kills." + tier.name(), kills[tier.ordinal()]);
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save kuudra.yml", e);
        }
    }
}
