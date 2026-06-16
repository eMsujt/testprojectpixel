package com.skyblock.core.manager;

import com.skyblock.core.model.Stat;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Canonical singleton tracking each player's bestiary: per-mob kill counts, the
 * milestone tier those kills unlock, family completion, and the permanent stat
 * bonuses earned from total tier progress.
 *
 * <p>Tiers follow a doubling threshold curve — tier {@code n} requires
 * {@code BASE_TIER_KILLS * 2^(n-1)} cumulative kills, capped at {@link #MAX_TIER}.
 * A {@link BestiaryFamily} is complete once every mob in it reaches
 * {@link #MAX_TIER}. Every {@link #TIERS_PER_MILESTONE} tiers unlocked across all
 * mobs grants the next permanent stat reward from a fixed cycle; see
 * {@link #getStatBonuses(UUID)}.</p>
 *
 * <p>Kill counts are stored in memory only; they are not persisted across server
 * restarts. Not thread-safe; access from the main server thread only.</p>
 */
public final class BestiaryManager {

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

    /** Broad categories that group mob families together. */
    public enum BestiaryCategory {
        COMBAT("Combat",  new BestiaryFamily[]{BestiaryFamily.ZOMBIE, BestiaryFamily.SKELETON,
                                               BestiaryFamily.SPIDER, BestiaryFamily.CREEPER,
                                               BestiaryFamily.SILVERFISH}),
        SLAYER("Slayer",  new BestiaryFamily[]{BestiaryFamily.ENDERMAN, BestiaryFamily.BLAZE,
                                               BestiaryFamily.WITCH, BestiaryFamily.TARANTULA,
                                               BestiaryFamily.PIGLIN}),
        BOSS  ("Boss",    new BestiaryFamily[]{BestiaryFamily.GHAST, BestiaryFamily.SLIME,
                                               BestiaryFamily.GOLEM, BestiaryFamily.WITHER,
                                               BestiaryFamily.GUARDIAN}),
        NETHER("Nether",  new BestiaryFamily[]{BestiaryFamily.HOGLIN, BestiaryFamily.STRIDER,
                                               BestiaryFamily.VINDICATOR}),
        OCEAN ("Ocean",   new BestiaryFamily[]{BestiaryFamily.SEA_WALKER, BestiaryFamily.SEA_GUARDIAN,
                                               BestiaryFamily.PHANTOM}),
        MINING("Mining",  new BestiaryFamily[]{BestiaryFamily.GOBLIN, BestiaryFamily.AUTOMATON,
                                               BestiaryFamily.SHULKER});

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

        public String getDisplayName() {
            return displayName;
        }
    }

    /** Kills required to reach tier 1 of a mob's bestiary entry. */
    public static final int BASE_TIER_KILLS = 10;

    /** The highest tier a bestiary entry can reach. */
    public static final int MAX_TIER = 10;

    /** Number of bestiary tiers a player must unlock to earn the next permanent stat reward. */
    public static final int TIERS_PER_MILESTONE = 5;

    /** The permanent stat reward granted at each {@link #TIERS_PER_MILESTONE}-tier milestone, applied in order and cycling. */
    private static final Stat[] REWARD_STATS = {
            Stat.HEALTH, Stat.STRENGTH, Stat.CRIT_DAMAGE, Stat.DEFENSE, Stat.MAGIC_FIND
    };

    /** Amount granted for the matching entry in {@link #REWARD_STATS}. */
    private static final double[] REWARD_AMOUNTS = {2.0, 1.0, 1.0, 1.0, 1.0};

    private static final BestiaryManager INSTANCE = new BestiaryManager();

    /** Per-player kill counts keyed by mob type name (lower-case). */
    private final Map<UUID, Map<String, Integer>> kills = new HashMap<>();

    private BestiaryManager() {}

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
     * Returns the bestiary tier the player has unlocked for the given {@link BestiaryMob}.
     *
     * @param playerId the player's UUID
     * @param mob      the mob type
     * @return the unlocked tier, between 0 and {@link #MAX_TIER}
     */
    public int getTier(UUID playerId, BestiaryMob mob) {
        return mob == null ? 0 : getTier(playerId, mob.mobKey);
    }

    /**
     * Returns the kills still needed for the player to reach the next tier of a
     * mob's entry, or zero if the entry is already at {@link #MAX_TIER}.
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
        long threshold = BASE_TIER_KILLS * (1L << tier);
        return (int) (threshold - getKills(playerId, mobType));
    }

    // -------------------------------------------------------------------------
    // Family completion
    // -------------------------------------------------------------------------

    /**
     * Returns whether the player has reached {@link #MAX_TIER} for every mob type
     * in the given family.
     *
     * @param playerId the player's UUID
     * @param family   the bestiary family
     * @return {@code true} if the family is fully completed
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
     * Returns the number of bestiary families the player has fully completed.
     *
     * @param playerId the player's UUID
     * @return the completed family count, between 0 and the number of families
     */
    public int getCompletedFamilyCount(UUID playerId) {
        if (playerId == null) {
            return 0;
        }
        int complete = 0;
        for (BestiaryFamily family : BestiaryFamily.values()) {
            if (isFamilyComplete(playerId, family)) {
                complete++;
            }
        }
        return complete;
    }

    // -------------------------------------------------------------------------
    // Milestone stat bonuses
    // -------------------------------------------------------------------------

    /**
     * Returns the total number of bestiary tiers the player has unlocked across
     * every tracked {@link BestiaryMob}.
     *
     * @param playerId the player's UUID
     * @return the summed tier count
     */
    public int getTotalTiers(UUID playerId) {
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
     * Returns the permanent stat bonuses the player has earned from bestiary tiers.
     *
     * <p>Each {@link #TIERS_PER_MILESTONE} tiers unlocked grants the next reward
     * from a fixed cycle; the returned map aggregates every reward earned so far.</p>
     *
     * @param playerId the player's UUID
     * @return an unmodifiable map of stat to total bonus, never {@code null}
     */
    public Map<Stat, Double> getStatBonuses(UUID playerId) {
        int rewards = getTotalTiers(playerId) / TIERS_PER_MILESTONE;
        Map<Stat, Double> bonuses = new EnumMap<>(Stat.class);
        for (int i = 0; i < rewards; i++) {
            int slot = i % REWARD_STATS.length;
            bonuses.merge(REWARD_STATS[slot], REWARD_AMOUNTS[slot], Double::sum);
        }
        return Collections.unmodifiableMap(bonuses);
    }

    // -------------------------------------------------------------------------
    // Cleanup
    // -------------------------------------------------------------------------

    /**
     * Resets all kill counts for the given player.
     *
     * @param playerId the player's UUID
     */
    public void resetKills(UUID playerId) {
        kills.remove(playerId);
    }

    /**
     * Removes all state for the given player.
     *
     * @param playerId the player's UUID
     */
    public void remove(UUID playerId) {
        kills.remove(playerId);
    }
}
