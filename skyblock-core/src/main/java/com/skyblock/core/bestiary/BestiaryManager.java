package com.skyblock.core.bestiary;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton tracking how many times each player has killed each mob type.
 *
 * <p>Kill counts are stored in memory only; they are not persisted across
 * server restarts in this implementation.</p>
 *
 * <p>Not thread-safe; access from the main server thread only.</p>
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
        SEA_WALKER("sea_walker",           "Sea Walker"),
        SEA_GUARDIAN("sea_guardian",       "Sea Guardian"),
        TARANTULA("tarantula",             "Tarantula"),
        GOBLIN("goblin",                   "Goblin"),
        AUTOMATON("automaton",             "Automaton");

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

    /**
     * Resets all kill counts for the given player.
     *
     * @param playerId the player's UUID
     */
    public void resetKills(UUID playerId) {
        kills.remove(playerId);
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
