package com.skyblock.core.manager;

import com.skyblock.core.model.Stat;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Canonical singleton tracking how many times each player has killed each mob
 * type, the milestone tier those kills unlock, family completion, and the
 * permanent stat bonuses earned from bestiary progress.
 *
 * <p>Tiers follow a doubling threshold curve — tier {@code n} requires
 * {@code BASE_TIER_KILLS * 2^(n-1)} cumulative kills, capped at {@link #MAX_TIER}.</p>
 *
 * <p>Kill counts are persisted to {@code plugins/SkyBlock/bestiary/<uuid>.yml},
 * one file per player. Call {@link #load(File)} on startup and
 * {@link #save(File)} on shutdown.</p>
 *
 * <p>Not thread-safe; access from the main server thread only.</p>
 */
public final class BestiaryManager {

    /** Kills required to reach tier 1 of a mob's bestiary entry. */
    public static final int BASE_TIER_KILLS = 10;

    /** The highest tier a bestiary entry can reach. */
    public static final int MAX_TIER = 10;

    /** Health granted per milestone level (one level per mob tier unlocked). */
    private static final double HEALTH_PER_MILESTONE = 2.0;

    /** Bonus health granted each time a whole mob family is completed. */
    private static final double HEALTH_PER_FAMILY = 5.0;

    /** Individual mob types tracked in the bestiary. */
    public enum BestiaryMob {
        ZOMBIE("zombie",                   "Zombie"),
        ZOMBIE_VILLAGER("zombie_villager", "Zombie Villager"),
        DROWNED("drowned",                 "Drowned"),
        HUSK("husk",                       "Husk"),
        SKELETON("skeleton",               "Skeleton"),
        STRAY("stray",                     "Stray"),
        WITHER_SKELETON("wither_skeleton", "Wither Skeleton"),
        BOGGED("bogged",                   "Bogged"),
        SPIDER("spider",                   "Spider"),
        CAVE_SPIDER("cave_spider",         "Cave Spider"),
        JOCKEY("jockey",                   "Jockey"),
        CREEPER("creeper",                 "Creeper"),
        CHARGED_CREEPER("charged_creeper", "Charged Creeper"),
        ENDERMAN("enderman",               "Enderman"),
        ENDERMITE("endermite",             "Endermite"),
        ENDERMAGE("endermage",             "Endermage"),
        BLAZE("blaze",                     "Blaze"),
        SLIME("slime",                     "Slime"),
        MAGMA_CUBE("magma_cube",           "Magma Cube"),
        GHAST("ghast",                     "Ghast"),
        WITCH("witch",                     "Witch"),
        IRON_GOLEM("iron_golem",           "Iron Golem"),
        SNOW_GOLEM("snow_golem",           "Snow Golem"),
        WITHER("wither",                   "Wither"),
        PIGLIN("piglin",                   "Piglin"),
        PIGLIN_BRUTE("piglin_brute",       "Piglin Brute"),
        HOGLIN("hoglin",                   "Hoglin"),
        ZOGLIN("zoglin",                   "Zoglin"),
        STRIDER("strider",                 "Strider"),
        PHANTOM("phantom",                 "Phantom"),
        GUARDIAN("guardian",               "Guardian"),
        ELDER_GUARDIAN("elder_guardian",   "Elder Guardian"),
        SHULKER("shulker",                 "Shulker"),
        VINDICATOR("vindicator",           "Vindicator"),
        PILLAGER("pillager",               "Pillager"),
        EVOKER("evoker",                   "Evoker"),
        RAVAGER("ravager",                 "Ravager"),
        SILVERFISH("silverfish",           "Silverfish"),
        SEA_WALKER("sea_walker",             "Sea Walker"),
        SEA_GUARDIAN("sea_guardian",         "Sea Guardian"),
        TARANTULA("tarantula",               "Tarantula"),
        GOBLIN("goblin",                     "Goblin"),
        AUTOMATON("automaton",               "Automaton"),
        ENDER_DRAGON("ender_dragon",         "Ender Dragon"),
        ZOMBIE_PIGMAN("zombie_pigman",       "Zombie Pigman"),
        BAT("bat",                           "Bat"),
        SQUID("squid",                       "Squid"),
        GLOW_SQUID("glow_squid",             "Glow Squid"),
        WOLF("wolf",                         "Wolf"),
        CRYPT_GHOUL("crypt_ghoul",           "Crypt Ghoul"),
        CRYPT_UNDEAD("crypt_undead",         "Crypt Undead"),
        REVENANT_HORROR("revenant_horror",   "Revenant Horror"),
        SVEN_PACKMASTER("sven_packmaster",   "Sven Packmaster");

        /** Lower-case mob type key used in kill-count maps. */
        public final String mobKey;
        public final String displayName;

        BestiaryMob(String mobKey, String displayName) {
            this.mobKey      = mobKey;
            this.displayName = displayName;
        }
    }

    /** Bestiary categories, organized by island/location like Hypixel (not abstract groupings). */
    public enum BestiaryCategory {
        PRIVATE_ISLAND("Private Island", new BestiaryFamily[]{BestiaryFamily.ZOMBIE, BestiaryFamily.SKELETON,
                                               BestiaryFamily.SPIDER, BestiaryFamily.CREEPER,
                                               BestiaryFamily.SLIME, BestiaryFamily.WITCH, BestiaryFamily.BAT}),
        HUB("Hub",        new BestiaryFamily[]{BestiaryFamily.CRYPT_GHOUL, BestiaryFamily.WOLF}),
        SPIDERS_DEN("Spider's Den", new BestiaryFamily[]{BestiaryFamily.TARANTULA, BestiaryFamily.SILVERFISH}),
        THE_END("The End", new BestiaryFamily[]{BestiaryFamily.ENDERMAN, BestiaryFamily.ENDER_DRAGON,
                                               BestiaryFamily.SHULKER}),
        CRIMSON_ISLE("Crimson Isle", new BestiaryFamily[]{BestiaryFamily.BLAZE, BestiaryFamily.GHAST,
                                               BestiaryFamily.PIGLIN, BestiaryFamily.HOGLIN,
                                               BestiaryFamily.STRIDER, BestiaryFamily.ZOMBIE_PIGMAN,
                                               BestiaryFamily.WITHER}),
        DWARVEN_MINES("Dwarven Mines", new BestiaryFamily[]{BestiaryFamily.GOBLIN}),
        CRYSTAL_HOLLOWS("Crystal Hollows", new BestiaryFamily[]{BestiaryFamily.AUTOMATON}),
        CATACOMBS("The Catacombs", new BestiaryFamily[]{BestiaryFamily.CRYPT_UNDEAD, BestiaryFamily.GOLEM,
                                               BestiaryFamily.REVENANT, BestiaryFamily.SVEN}),
        THE_OCEAN("The Ocean", new BestiaryFamily[]{BestiaryFamily.SEA_WALKER, BestiaryFamily.SEA_GUARDIAN,
                                               BestiaryFamily.GUARDIAN, BestiaryFamily.SQUID,
                                               BestiaryFamily.PHANTOM, BestiaryFamily.VINDICATOR});

        public final String displayName;
        /** Mob families that belong to this category. */
        public final BestiaryFamily[] families;

        BestiaryCategory(String displayName, BestiaryFamily[] families) {
            this.displayName = displayName;
            this.families    = families;
        }
    }

    /** Groupings of related mob types for bestiary milestone tracking. */
    public enum BestiaryFamily {
        ZOMBIE("Zombie",           new String[]{"zombie", "zombie_villager", "drowned", "husk"}),
        SKELETON("Skeleton",       new String[]{"skeleton", "stray", "wither_skeleton", "bogged"}),
        SPIDER("Spider",           new String[]{"spider", "cave_spider", "jockey"}),
        CREEPER("Creeper",         new String[]{"creeper", "charged_creeper"}),
        ENDERMAN("Enderman",       new String[]{"enderman", "endermite", "endermage"}),
        BLAZE("Blaze",             new String[]{"blaze"}),
        SLIME("Slime",             new String[]{"slime", "magma_cube"}),
        GHAST("Ghast",             new String[]{"ghast"}),
        WITCH("Witch",             new String[]{"witch"}),
        GOLEM("Golem",             new String[]{"iron_golem", "snow_golem"}),
        WITHER("Wither",           new String[]{"wither", "wither_skeleton"}),
        PIGLIN("Piglin",           new String[]{"piglin", "piglin_brute"}),
        HOGLIN("Hoglin",           new String[]{"hoglin", "zoglin"}),
        STRIDER("Strider",         new String[]{"strider"}),
        PHANTOM("Phantom",         new String[]{"phantom"}),
        GUARDIAN("Guardian",       new String[]{"guardian", "elder_guardian"}),
        SHULKER("Shulker",         new String[]{"shulker"}),
        VINDICATOR("Vindicator",   new String[]{"vindicator", "pillager", "evoker", "ravager"}),
        SILVERFISH("Silverfish",   new String[]{"silverfish"}),
        SEA_WALKER("Sea Walker",   new String[]{"sea_walker"}),
        SEA_GUARDIAN("Sea Guardian", new String[]{"sea_guardian"}),
        TARANTULA("Tarantula",     new String[]{"tarantula"}),
        GOBLIN("Goblin",           new String[]{"goblin"}),
        AUTOMATON("Automaton",     new String[]{"automaton"}),
        // Additional SkyBlock mob families
        ENDER_DRAGON("Ender Dragon",   new String[]{"ender_dragon"}),
        ZOMBIE_PIGMAN("Zombie Pigman", new String[]{"zombie_pigman"}),
        BAT("Bat",                     new String[]{"bat"}),
        SQUID("Squid",                 new String[]{"squid", "glow_squid"}),
        WOLF("Wolf",                   new String[]{"wolf"}),
        CRYPT_GHOUL("Crypt Ghoul",     new String[]{"crypt_ghoul"}),
        CRYPT_UNDEAD("Crypt Undead",   new String[]{"crypt_undead"}),
        REVENANT("Revenant",           new String[]{"revenant_horror"}),
        SVEN("Sven Packmaster",        new String[]{"sven_packmaster"});

        public final String displayName;
        /** Lower-case mob type keys that belong to this family. */
        public final String[] mobTypes;

        BestiaryFamily(String displayName, String[] mobTypes) {
            this.displayName = displayName;
            this.mobTypes    = mobTypes;
        }

        /** Returns this family's human-readable display name. */
        public String getDisplayName() {
            return displayName;
        }
    }

    private static final BestiaryManager INSTANCE = new BestiaryManager();

    /** Per-player kill counts keyed by mob type name (lower-case). */
    private final Map<UUID, Map<String, Integer>> kills = new HashMap<>();

    private BestiaryManager() {}

    /** Returns the singleton instance. */
    public static BestiaryManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Kill tracking
    // -------------------------------------------------------------------------

    /**
     * Records one kill of {@code mobType} for {@code playerId}.
     *
     * @param playerId the killer's UUID
     * @param mobType  the mob type identifier (e.g. "zombie", "skeleton")
     */
    public void recordKill(UUID playerId, String mobType) {
        if (playerId == null || mobType == null || mobType.isEmpty()) {
            return;
        }
        String key = mobType.toLowerCase();
        kills.computeIfAbsent(playerId, k -> new HashMap<>())
             .merge(key, 1, Integer::sum);
        reapplyMilestoneStats(playerId);
    }

    /** Bestiary milestone stat bonuses currently applied, for exact removal on re-apply. */
    private final Map<UUID, Map<Stat, Double>> appliedMilestoneStats = new HashMap<>();

    /** Forgets the tracked applied bonus without touching StatManager (e.g. on quit when it resets). */
    public void clearAppliedMilestoneStats(UUID playerId) {
        appliedMilestoneStats.remove(playerId);
    }

    /** Re-applies the player's bestiary milestone stats to {@link StatManager} (balanced). */
    public void reapplyMilestoneStats(UUID playerId) {
        if (playerId == null) {
            return;
        }
        StatManager stats = StatManager.getInstance();
        Map<Stat, Double> previous = appliedMilestoneStats.remove(playerId);
        if (previous != null) {
            for (Map.Entry<Stat, Double> entry : previous.entrySet()) {
                stats.addBonus(playerId, entry.getKey(), -entry.getValue());
            }
        }
        Map<Stat, Double> applied = new EnumMap<>(Stat.class);
        for (Map.Entry<Stat, Double> entry : getMilestoneStats(playerId).entrySet()) {
            if (entry.getValue() == null || entry.getValue() == 0.0) {
                continue;
            }
            stats.addBonus(playerId, entry.getKey(), entry.getValue());
            applied.put(entry.getKey(), entry.getValue());
        }
        if (!applied.isEmpty()) {
            appliedMilestoneStats.put(playerId, applied);
        }
    }

    /**
     * Increments the player's kill count for {@code mobType}, advancing the
     * milestone tier as cumulative kills cross the {@link #getTier} thresholds.
     * Alias for {@link #recordKill(UUID, String)}.
     *
     * @param player  the killer's UUID
     * @param mobType the mob type identifier (e.g. "zombie", "skeleton")
     */
    public void addKill(UUID player, String mobType) {
        recordKill(player, mobType);
    }

    /**
     * Returns the number of kills the player has for the given mob type.
     *
     * @param playerId the player's UUID
     * @param mobType  the mob type identifier
     * @return kill count, or 0 if none recorded
     */
    public int getKills(UUID playerId, String mobType) {
        if (playerId == null || mobType == null) {
            return 0;
        }
        Map<String, Integer> entry = kills.get(playerId);
        if (entry == null) {
            return 0;
        }
        return entry.getOrDefault(mobType.toLowerCase(), 0);
    }

    /**
     * Records one kill of the given {@link BestiaryMob} for {@code playerId}.
     *
     * @param playerId the killer's UUID
     * @param mob      the mob type
     */
    public void recordKill(UUID playerId, BestiaryMob mob) {
        if (mob == null) {
            return;
        }
        recordKill(playerId, mob.mobKey);
    }

    /**
     * Returns the number of kills the player has for the given {@link BestiaryMob}.
     *
     * @param playerId the player's UUID
     * @param mob      the mob type
     * @return kill count, or 0 if none recorded
     */
    public int getKills(UUID playerId, BestiaryMob mob) {
        if (mob == null) {
            return 0;
        }
        return getKills(playerId, mob.mobKey);
    }

    /**
     * Returns an unmodifiable view of all kill counts for the given player.
     *
     * @param playerId the player's UUID
     * @return map of mob type to kill count; empty if no kills recorded
     */
    public Map<String, Integer> getAllKills(UUID playerId) {
        if (playerId == null) {
            return Collections.emptyMap();
        }
        Map<String, Integer> entry = kills.get(playerId);
        return entry != null ? Collections.unmodifiableMap(entry) : Collections.emptyMap();
    }

    /**
     * Returns the total kills the player has for all mob types in the given family.
     *
     * @param playerId the player's UUID
     * @param family   the bestiary family
     * @return summed kill count across all mob types in the family
     */
    public int getKillsForFamily(UUID playerId, BestiaryFamily family) {
        if (playerId == null || family == null) {
            return 0;
        }
        int total = 0;
        for (String mobType : family.mobTypes) {
            total += getKills(playerId, mobType);
        }
        return total;
    }

    /**
     * Returns the total kills the player has for all families in the given category.
     *
     * @param playerId the player's UUID
     * @param category the bestiary category
     * @return summed kill count across all families in the category
     */
    public int getKillsForCategory(UUID playerId, BestiaryCategory category) {
        if (playerId == null || category == null) {
            return 0;
        }
        int total = 0;
        for (BestiaryFamily family : category.families) {
            total += getKillsForFamily(playerId, family);
        }
        return total;
    }

    // -------------------------------------------------------------------------
    // Tier thresholds
    // -------------------------------------------------------------------------

    /**
     * Returns the bestiary tier the player has unlocked for a mob type. Tier 0
     * means the entry is locked; tier {@code n} requires
     * {@code BASE_TIER_KILLS * 2^(n-1)} kills, up to {@link #MAX_TIER}.
     *
     * @param playerId the player's UUID
     * @param mobType  the mob type identifier
     * @return the unlocked tier, between 0 and {@link #MAX_TIER}
     */
    public int getTier(UUID playerId, String mobType) {
        int count = getKills(playerId, mobType);
        int tier = 0;
        long threshold = BASE_TIER_KILLS;
        while (tier < MAX_TIER && count >= threshold) {
            tier++;
            threshold *= 2;
        }
        return tier;
    }

    /**
     * Returns the kills still needed for the player to reach the next tier of a
     * mob's entry, or 0 if the entry is already at {@link #MAX_TIER}.
     *
     * @param playerId the player's UUID
     * @param mobType  the mob type identifier
     * @return the remaining kills, never negative
     */
    public int getKillsToNextTier(UUID playerId, String mobType) {
        int tier = getTier(playerId, mobType);
        if (tier >= MAX_TIER) {
            return 0;
        }
        long threshold = (long) BASE_TIER_KILLS * (1L << tier);
        return (int) (threshold - getKills(playerId, mobType));
    }

    // -------------------------------------------------------------------------
    // Family completion
    // -------------------------------------------------------------------------

    /**
     * Returns whether every mob type in the family has reached {@link #MAX_TIER}.
     *
     * @param playerId the player's UUID
     * @param family   the bestiary family
     * @return {@code true} if the whole family is maxed out
     */
    public boolean isFamilyComplete(UUID playerId, BestiaryFamily family) {
        if (playerId == null || family == null) {
            return false;
        }
        for (String mobType : family.mobTypes) {
            if (getTier(playerId, mobType) < MAX_TIER) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns how many bestiary families the player has fully completed.
     *
     * @param playerId the player's UUID
     * @return the number of maxed-out families
     */
    public int getCompletedFamilyCount(UUID playerId) {
        int completed = 0;
        for (BestiaryFamily family : BestiaryFamily.values()) {
            if (isFamilyComplete(playerId, family)) {
                completed++;
            }
        }
        return completed;
    }

    // -------------------------------------------------------------------------
    // Milestone stat bonuses
    // -------------------------------------------------------------------------

    /**
     * Returns the player's bestiary milestone level: the sum of unlocked tiers
     * across every known mob type. Each unlocked tier is one milestone.
     *
     * @param playerId the player's UUID
     * @return the total number of tiers unlocked, never negative
     */
    public int getMilestoneLevel(UUID playerId) {
        if (playerId == null) {
            return 0;
        }
        int total = 0;
        for (BestiaryMob mob : BestiaryMob.values()) {
            total += getTier(playerId, mob.mobKey);
        }
        return total;
    }

    /**
     * Returns the permanent stat bonuses the player has earned from bestiary
     * progress. Every unlocked mob tier grants {@code HEALTH_PER_MILESTONE}
     * health, and every fully completed family grants {@code HEALTH_PER_FAMILY}
     * additional health.
     *
     * @param playerId the player's UUID
     * @return map of stat to bonus value; empty if the player has no progress
     */
    public Map<Stat, Double> getMilestoneStats(UUID playerId) {
        double health = getMilestoneLevel(playerId) * HEALTH_PER_MILESTONE
                      + getCompletedFamilyCount(playerId) * HEALTH_PER_FAMILY;
        if (health <= 0.0) {
            return Collections.emptyMap();
        }
        Map<Stat, Double> stats = new EnumMap<>(Stat.class);
        stats.put(Stat.HEALTH, health);
        return stats;
    }

    /**
     * Resets all kill counts for the given player.
     *
     * @param playerId the player's UUID
     */
    public void resetKills(UUID playerId) {
        kills.remove(playerId);
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    /**
     * Loads all per-player bestiary files from {@code dataFolder/bestiary/}.
     * Each file is named {@code <uuid>.yml} and contains a {@code kills} section
     * mapping mob-type keys to kill counts.
     *
     * @param dataFolder the plugin's data folder
     */
    public void load(File dataFolder) {
        File dir = new File(dataFolder, "bestiary");
        if (!dir.exists()) {
            return;
        }
        File[] files = dir.listFiles((d, name) -> name.endsWith(".yml"));
        if (files == null) {
            return;
        }
        for (File file : files) {
            String name = file.getName();
            String uuidStr = name.substring(0, name.length() - 4);
            UUID uuid;
            try {
                uuid = UUID.fromString(uuidStr);
            } catch (IllegalArgumentException e) {
                continue;
            }
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            if (!cfg.isConfigurationSection("kills")) {
                continue;
            }
            Map<String, Integer> playerKills = new HashMap<>();
            for (String mobKey : cfg.getConfigurationSection("kills").getKeys(false)) {
                playerKills.put(mobKey, cfg.getInt("kills." + mobKey, 0));
            }
            kills.put(uuid, playerKills);
        }
    }

    /**
     * Saves all in-memory kill counts to {@code dataFolder/bestiary/<uuid>.yml}.
     *
     * @param dataFolder the plugin's data folder
     */
    public void save(File dataFolder) {
        File dir = new File(dataFolder, "bestiary");
        if (!dir.exists() && !dir.mkdirs()) {
            return;
        }
        for (Map.Entry<UUID, Map<String, Integer>> entry : kills.entrySet()) {
            File file = new File(dir, entry.getKey().toString() + ".yml");
            YamlConfiguration cfg = new YamlConfiguration();
            for (Map.Entry<String, Integer> kill : entry.getValue().entrySet()) {
                cfg.set("kills." + kill.getKey(), kill.getValue());
            }
            try {
                cfg.save(file);
            } catch (IOException e) {
                // log but continue saving other players
                e.printStackTrace();
            }
        }
    }

    // -------------------------------------------------------------------------
    // Cleanup
    // -------------------------------------------------------------------------

    /**
     * Removes all state for the given player.
     *
     * @param playerId the player's UUID
     */
    public void remove(UUID playerId) {
        kills.remove(playerId);
    }
}
