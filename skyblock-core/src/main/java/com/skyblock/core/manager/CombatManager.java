package com.skyblock.core.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Singleton manager for SkyBlock combat skill progression and kill tracking.
 *
 * <p>Tracks per-player combat XP and level, and records kills by monster type.
 * Combat level thresholds follow a simple exponential curve
 * (50 XP × level² per level-up, capped at level 60).</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class CombatManager {

    /** Zones across SkyBlock where combat takes place. */
    public enum CombatZone {
        HUB("Hub"),
        COAL_MINE("Coal Mine"),
        GOLD_MINE("Gold Mine"),
        DEEP_CAVERNS("Deep Caverns"),
        THE_PARK("The Park"),
        SPIDERS_DEN("Spider's Den"),
        BLAZING_FORTRESS("Blazing Fortress"),
        THE_END("The End"),
        CRIMSON_ISLE("Crimson Isle"),
        CRYSTAL_HOLLOWS("Crystal Hollows"),
        THE_RIFT("The Rift"),
        DUNGEONS("Dungeons");

        /** Human-readable display name. */
        public final String displayName;

        CombatZone(String displayName) {
            this.displayName = displayName;
        }
    }

    /**
     * All monsters killable through SkyBlock combat, across every zone.
     * Each entry carries the zone it spawns in, its base health, the
     * minimum combat level to earn XP, and the XP awarded per kill.
     */
    public enum Monster {
        // --- Hub ---
        ZOMBIE(CombatZone.HUB,               100,  1,  2.0),
        SKELETON(CombatZone.HUB,             100,  1,  2.0),
        SPIDER(CombatZone.HUB,               100,  1,  2.0),
        CREEPER(CombatZone.HUB,              100,  1,  2.0),
        SLIME(CombatZone.HUB,               100,  1,  2.0),
        WITCH(CombatZone.HUB,               300,  5,  5.0),

        // --- Coal Mine ---
        COAL_MINE_ZOMBIE(CombatZone.COAL_MINE,  150,  1,  3.0),
        COAL_MINE_SKELETON(CombatZone.COAL_MINE, 150,  1,  3.0),

        // --- Gold Mine ---
        GOLD_MINE_ZOMBIE(CombatZone.GOLD_MINE,   200,  3,  4.0),
        GOLD_MINE_SKELETON(CombatZone.GOLD_MINE, 200,  3,  4.0),

        // --- Deep Caverns ---
        CAVERN_ZOMBIE(CombatZone.DEEP_CAVERNS,          400,   5,  5.0),
        CAVERN_SKELETON(CombatZone.DEEP_CAVERNS,        400,   5,  5.0),
        CAVERN_CAVE_SPIDER(CombatZone.DEEP_CAVERNS,     500,   7,  6.0),
        LAPIS_ZOMBIE(CombatZone.DEEP_CAVERNS,           600,  10,  7.0),
        DIAMOND_ZOMBIE(CombatZone.DEEP_CAVERNS,        1200,  15, 10.0),
        DIAMOND_SKELETON(CombatZone.DEEP_CAVERNS,      1200,  15, 10.0),
        REDSTONE_PIGMAN(CombatZone.DEEP_CAVERNS,       1500,  18, 12.0),
        EMERALD_SLIME(CombatZone.DEEP_CAVERNS,         2000,  20, 14.0),

        // --- The Park ---
        PACK_SPIRIT(CombatZone.THE_PARK,        600,   8,  6.0),
        HOWLING_SPIRIT(CombatZone.THE_PARK,     800,  10,  7.0),
        SPIRIT_BAT(CombatZone.THE_PARK,         400,   6,  5.0),
        SOUL_OF_THE_ALPHA(CombatZone.THE_PARK, 4000,  18, 15.0),

        // --- Spider's Den ---
        DASHER_SPIDER(CombatZone.SPIDERS_DEN,   800,  10,  7.0),
        WEAVER_SPIDER(CombatZone.SPIDERS_DEN,  1000,  12,  8.0),
        VORACIOUS_SPIDER(CombatZone.SPIDERS_DEN, 1500, 15, 10.0),
        SPLITTER_SPIDER(CombatZone.SPIDERS_DEN, 1200,  13,  9.0),
        BROODMOTHER(CombatZone.SPIDERS_DEN,    15000,  20, 25.0),

        // --- Blazing Fortress ---
        BLAZE(CombatZone.BLAZING_FORTRESS,       1000,  12,  8.0),
        WITHER_SKELETON(CombatZone.BLAZING_FORTRESS, 2000, 18, 12.0),
        MAGMA_CUBE(CombatZone.BLAZING_FORTRESS,  1500,  15, 10.0),
        GHAST(CombatZone.BLAZING_FORTRESS,       3000,  20, 14.0),
        FIRE_IMP(CombatZone.BLAZING_FORTRESS,     800,  10,  7.0),

        // --- The End ---
        ENDERMAN(CombatZone.THE_END,             5000,  25, 18.0),
        ENDERMITE(CombatZone.THE_END,            2000,  20, 12.0),
        OBSIDIAN_DEFENDER(CombatZone.THE_END,    6000,  28, 20.0),
        ZEALOT(CombatZone.THE_END,               8000,  30, 22.0),
        WATCHER(CombatZone.THE_END,             10000,  35, 25.0),
        VOIDGLOOM_SERAPH(CombatZone.THE_END,    50000,  45, 40.0),

        // --- Crimson Isle ---
        BARBARIAN(CombatZone.CRIMSON_ISLE,       3000,  22, 15.0),
        BRUISER(CombatZone.CRIMSON_ISLE,         5000,  26, 18.0),
        GRUNT(CombatZone.CRIMSON_ISLE,           2000,  20, 13.0),
        MAGE(CombatZone.CRIMSON_ISLE,            4000,  24, 16.0),
        VANQUISHER(CombatZone.CRIMSON_ISLE,     25000,  35, 30.0),
        PIGMAN(CombatZone.CRIMSON_ISLE,          2500,  21, 14.0),

        // --- Crystal Hollows ---
        GOBLIN(CombatZone.CRYSTAL_HOLLOWS,       4000,  24, 16.0),
        GOBLIN_MAGE(CombatZone.CRYSTAL_HOLLOWS,  6000,  28, 20.0),
        CRYSTAL_GOLEM(CombatZone.CRYSTAL_HOLLOWS, 8000, 32, 22.0),

        // --- The Rift ---
        RIFT_WOLF(CombatZone.THE_RIFT,           5000,  30, 20.0),
        MOSQUITO(CombatZone.THE_RIFT,            3500,  26, 17.0),
        HORNED_MUSSEL(CombatZone.THE_RIFT,       4500,  28, 19.0),
        STILLGORE_CHATEAU(CombatZone.THE_RIFT,  20000,  40, 30.0),

        // --- Dungeons ---
        SKELETON_GRUNT(CombatZone.DUNGEONS,      2000,  20, 14.0),
        ZOMBIE_GRUNT(CombatZone.DUNGEONS,        2000,  20, 14.0),
        SKELETON_MASTER(CombatZone.DUNGEONS,    10000,  35, 25.0),
        UNDEAD_SWORD(CombatZone.DUNGEONS,        4000,  24, 16.0),
        BAT(CombatZone.DUNGEONS,                 1500,  18, 10.0);

        /** Zone this monster spawns in. */
        public final CombatZone zone;
        /** Base health (HP). */
        public final int baseHealth;
        /** Minimum combat level required to earn XP from this monster. */
        public final int minLevel;
        /** XP awarded per kill when the player meets the level requirement. */
        public final double xpPerKill;

        Monster(CombatZone zone, int baseHealth, int minLevel, double xpPerKill) {
            this.zone = zone;
            this.baseHealth = baseHealth;
            this.minLevel = minLevel;
            this.xpPerKill = xpPerKill;
        }
    }

    private static final int MAX_LEVEL = 60;
    /** Base XP awarded per generic kill (overridden per-monster by {@link Monster#xpPerKill}). */
    public static final double XP_PER_KILL = 4.0;

    private static final CombatManager INSTANCE = new CombatManager();

    /** Per-player accumulated combat XP. */
    private final Map<UUID, Double> combatXp = new HashMap<>();
    /** Per-player combat level cache. */
    private final Map<UUID, Integer> combatLevel = new HashMap<>();
    /** Per-player total kills. */
    private final Map<UUID, Integer> totalKills = new HashMap<>();
    /** Per-player per-monster kill counts. */
    private final Map<UUID, Map<Monster, Integer>> monsterKills = new HashMap<>();
    /** Per-player kill event history. */
    private final Map<UUID, List<String>> killHistory = new ConcurrentHashMap<>();

    private CombatManager() {
    }

    /**
     * Returns the single shared {@code CombatManager} instance.
     *
     * @return the singleton instance
     */
    public static CombatManager getInstance() {
        return INSTANCE;
    }

    // ---------------------------------------------------------------------------
    // XP and levelling
    // ---------------------------------------------------------------------------

    /**
     * Adds combat XP to the player and updates their level if thresholds are crossed.
     *
     * @param playerId the player receiving XP
     * @param amount   XP to add, must not be negative
     * @return the player's new total XP
     */
    public double addXp(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        double total = combatXp.merge(playerId, amount, Double::sum);
        combatLevel.put(playerId, computeLevel(total));
        return total;
    }

    /**
     * Returns the player's current combat XP.
     *
     * @param playerId the player to look up
     * @return total XP, {@code 0} if none recorded
     */
    public double getXp(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return combatXp.getOrDefault(playerId, 0.0);
    }

    /**
     * Returns the player's current combat level (1–{@value #MAX_LEVEL}).
     *
     * @param playerId the player to look up
     * @return combat level
     */
    public int getLevel(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return combatLevel.getOrDefault(playerId, 1);
    }

    // ---------------------------------------------------------------------------
    // Kill tracking
    // ---------------------------------------------------------------------------

    /**
     * Records a monster kill for the player, awards XP if the player meets the
     * minimum level requirement, and returns the XP awarded (0 if below min level).
     *
     * @param playerId the player who scored the kill
     * @param monster  the monster that was killed
     * @return XP awarded by this kill
     */
    public double recordKill(UUID playerId, Monster monster) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(monster, "monster");

        totalKills.merge(playerId, 1, Integer::sum);
        monsterKills
            .computeIfAbsent(playerId, k -> new HashMap<>())
            .merge(monster, 1, Integer::sum);
        killHistory
            .computeIfAbsent(playerId, k -> new ArrayList<>())
            .add(monster.name());

        int playerLevel = getLevel(playerId);
        if (playerLevel < monster.minLevel) {
            return 0.0;
        }
        return addXp(playerId, monster.xpPerKill);
    }

    /** Returns the player's total kills across all monsters, {@code 0} if none recorded. */
    public int getTotalKills(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return totalKills.getOrDefault(playerId, 0);
    }

    /** Returns the player's kill count for a specific monster, {@code 0} if none recorded. */
    public int getKillCount(UUID playerId, Monster monster) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(monster, "monster");
        Map<Monster, Integer> counts = monsterKills.get(playerId);
        return counts == null ? 0 : counts.getOrDefault(monster, 0);
    }

    /** Returns an unmodifiable view of the player's kill event history. */
    public List<String> getKillHistory(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return Collections.unmodifiableList(killHistory.getOrDefault(playerId, new ArrayList<>()));
    }

    /** Returns the monster the player has killed most, or {@code null} if none recorded. */
    public Monster getTopMonster(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<Monster, Integer> counts = monsterKills.get(playerId);
        if (counts == null || counts.isEmpty()) {
            return null;
        }
        Monster top = null;
        int max = -1;
        for (Map.Entry<Monster, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                top = entry.getKey();
            }
        }
        return top;
    }

    /** Clears all combat data for the given player. */
    public void resetPlayer(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        combatXp.remove(playerId);
        combatLevel.remove(playerId);
        totalKills.remove(playerId);
        monsterKills.remove(playerId);
        killHistory.remove(playerId);
    }

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    /**
     * Computes the combat level for the given total XP.
     * Formula: level {@code n} requires {@code 50 * n^2} cumulative XP.
     *
     * @param totalXp total accumulated combat XP
     * @return level between 1 and {@value #MAX_LEVEL}
     */
    private static int computeLevel(double totalXp) {
        int level = 1;
        while (level < MAX_LEVEL) {
            double threshold = 50.0 * (level + 1) * (level + 1);
            if (totalXp < threshold) {
                break;
            }
            level++;
        }
        return level;
    }

    // ---------------------------------------------------------------------------
    // Persistence
    // ---------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "combat.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        combatXp.clear();
        combatLevel.clear();
        totalKills.clear();
        monsterKills.clear();
        killHistory.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                double xp = cfg.getDouble(key + ".xp", 0.0);
                if (xp > 0.0) {
                    combatXp.put(uuid, xp);
                    combatLevel.put(uuid, computeLevel(xp));
                }
                int kills = cfg.getInt(key + ".totalKills", 0);
                if (kills > 0) {
                    totalKills.put(uuid, kills);
                }
                if (cfg.isConfigurationSection(key + ".monsters")) {
                    Map<Monster, Integer> counts = new HashMap<>();
                    for (Monster m : Monster.values()) {
                        int count = cfg.getInt(key + ".monsters." + m.name(), 0);
                        if (count > 0) {
                            counts.put(m, count);
                        }
                    }
                    if (!counts.isEmpty()) {
                        monsterKills.put(uuid, counts);
                    }
                }
                List<String> events = cfg.getStringList(key + ".killHistory");
                if (!events.isEmpty()) {
                    killHistory.put(uuid, new ArrayList<>(events));
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "combat.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Double> entry : combatXp.entrySet()) {
            cfg.set(entry.getKey().toString() + ".xp", entry.getValue());
        }
        for (Map.Entry<UUID, Integer> entry : totalKills.entrySet()) {
            cfg.set(entry.getKey().toString() + ".totalKills", entry.getValue());
        }
        for (Map.Entry<UUID, Map<Monster, Integer>> playerEntry : monsterKills.entrySet()) {
            String prefix = playerEntry.getKey().toString() + ".monsters.";
            for (Map.Entry<Monster, Integer> m : playerEntry.getValue().entrySet()) {
                cfg.set(prefix + m.getKey().name(), m.getValue());
            }
        }
        for (Map.Entry<UUID, List<String>> entry : killHistory.entrySet()) {
            cfg.set(entry.getKey().toString() + ".killHistory", entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save combat.yml", e);
        }
    }
}
