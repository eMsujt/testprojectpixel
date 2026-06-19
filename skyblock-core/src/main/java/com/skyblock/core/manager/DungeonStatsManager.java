package com.skyblock.core.manager;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for a player's overall Catacombs progression statistics.
 *
 * <p>Where {@link DungeonManager} tracks per-floor records, runs and class XP,
 * this manager owns the <em>aggregate</em> Catacombs numbers shown on the stats
 * screen: the overall Catacombs level (derived from accumulated Catacombs XP),
 * the lifetime number of secrets found and the lifetime number of dungeon
 * bosses killed.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class DungeonStatsManager {

    /** Maximum Catacombs level. */
    public static final int MAX_CATACOMBS_LEVEL = 50;

    /** Cumulative XP required to reach each Catacombs level (index = level, 0..50). */
    private static final long[] CATACOMBS_XP_TABLE = {
        0L, 50L, 125L, 235L, 395L, 625L, 955L, 1425L, 2095L, 3045L, 4385L,
        6275L, 8940L, 12700L, 17960L, 25340L, 35640L, 50040L, 70040L, 97640L,
        135640L, 188140L, 259640L, 356640L, 488640L, 668640L, 911640L, 1239640L,
        1684640L, 2284640L, 3084640L, 4149640L, 5559640L, 7459640L, 9959640L,
        13259640L, 17559640L, 23159640L, 30359640L, 39559640L, 51559640L,
        66559640L, 85559640L, 109559640L, 139559640L, 177559640L, 225559640L,
        285559640L, 360559640L, 453559640L, 569809640L
    };

    private static final DungeonStatsManager INSTANCE = new DungeonStatsManager();

    public static DungeonStatsManager getInstance() {
        return INSTANCE;
    }

    private DungeonStatsManager() {}

    /** Accumulated Catacombs XP per player. */
    private final Map<UUID, Double> catacombsXp = new HashMap<>();
    /** Lifetime secrets found per player. */
    private final Map<UUID, Integer> secretsFound = new HashMap<>();
    /** Lifetime dungeon bosses killed per player. */
    private final Map<UUID, Integer> bossKills = new HashMap<>();

    // -------------------------------------------------------------------------
    // Catacombs XP / level
    // -------------------------------------------------------------------------

    /** Adds Catacombs XP and returns the player's new total. */
    public double addCatacombsXp(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative: " + amount);
        return catacombsXp.merge(playerId, amount, Double::sum);
    }

    /** Returns the player's accumulated Catacombs XP. */
    public double getCatacombsXp(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return catacombsXp.getOrDefault(playerId, 0.0);
    }

    /** Returns the player's Catacombs level (0..{@link #MAX_CATACOMBS_LEVEL}). */
    public int getCatacombsLevel(UUID playerId) {
        double xp = getCatacombsXp(playerId);
        int level = 0;
        while (level < MAX_CATACOMBS_LEVEL && xp >= CATACOMBS_XP_TABLE[level + 1]) {
            level++;
        }
        return level;
    }

    /** Returns the XP still needed to reach the next Catacombs level, or 0 at max level. */
    public double getXpToNextLevel(UUID playerId) {
        int level = getCatacombsLevel(playerId);
        if (level >= MAX_CATACOMBS_LEVEL) return 0.0;
        return CATACOMBS_XP_TABLE[level + 1] - getCatacombsXp(playerId);
    }

    // -------------------------------------------------------------------------
    // Aggregate counters
    // -------------------------------------------------------------------------

    /** Adds to the player's lifetime secrets-found count and returns the new total. */
    public int addSecretsFound(UUID playerId, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative: " + amount);
        return secretsFound.merge(playerId, amount, Integer::sum);
    }

    /** Returns the player's lifetime secrets-found count. */
    public int getSecretsFound(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return secretsFound.getOrDefault(playerId, 0);
    }

    /** Adds to the player's lifetime boss-kill count and returns the new total. */
    public int addBossKill(UUID playerId, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative: " + amount);
        return bossKills.merge(playerId, amount, Integer::sum);
    }

    /** Returns the player's lifetime boss-kill count. */
    public int getBossKills(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return bossKills.getOrDefault(playerId, 0);
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "dungeon-stats.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        catacombsXp.clear();
        secretsFound.clear();
        bossKills.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                double xp = cfg.getDouble(key + ".catacombsXp", 0.0);
                if (xp > 0) catacombsXp.put(uuid, xp);
                int secrets = cfg.getInt(key + ".secretsFound", 0);
                if (secrets > 0) secretsFound.put(uuid, secrets);
                int kills = cfg.getInt(key + ".bossKills", 0);
                if (kills > 0) bossKills.put(uuid, kills);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "dungeon-stats.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Double> entry : catacombsXp.entrySet()) {
            cfg.set(entry.getKey().toString() + ".catacombsXp", entry.getValue());
        }
        for (Map.Entry<UUID, Integer> entry : secretsFound.entrySet()) {
            cfg.set(entry.getKey().toString() + ".secretsFound", entry.getValue());
        }
        for (Map.Entry<UUID, Integer> entry : bossKills.entrySet()) {
            cfg.set(entry.getKey().toString() + ".bossKills", entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save dungeon-stats.yml", e);
        }
    }
}
