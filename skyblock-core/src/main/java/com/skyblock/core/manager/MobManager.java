package com.skyblock.core.manager;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * Singleton registry for SkyBlock custom mobs.
 *
 * <p>Loads mob definitions from {@code mobs.yml} on first access and exposes
 * them by id for combat/spawning use. Each definition carries per-mob stats
 * (health/damage/defense), a base level, and a spawn weight; the manager can
 * scale stats to an arbitrary level and roll a weighted spawn table.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class MobManager {

    /** Fraction by which health/damage/defense grow per level above the base level. */
    private static final double SCALE_PER_LEVEL = 0.10;

    /** Immutable data record for a single mob definition. */
    public static final class MobDefinition {
        private final String id;
        private final String displayName;
        private final EntityType entityType;
        private final double health;
        private final double damage;
        private final double defense;
        private final int baseLevel;
        private final int spawnWeight;
        private final int xpReward;
        private final int coinReward;
        private final int maxPerSpot;
        private final int respawnSeconds;
        private final boolean nightOnly;

        MobDefinition(String id, String displayName, EntityType entityType,
                      double health, double damage, double defense,
                      int baseLevel, int spawnWeight, int xpReward, int coinReward,
                      int maxPerSpot, int respawnSeconds, boolean nightOnly) {
            this.id = id;
            this.displayName = displayName;
            this.entityType = entityType;
            this.health = health;
            this.damage = damage;
            this.defense = defense;
            this.baseLevel = baseLevel;
            this.spawnWeight = spawnWeight;
            this.xpReward = xpReward;
            this.coinReward = coinReward;
            this.maxPerSpot = maxPerSpot;
            this.respawnSeconds = respawnSeconds;
            this.nightOnly = nightOnly;
        }

        public String getId()            { return id; }
        public String getDisplayName()   { return displayName; }
        public EntityType getEntityType(){ return entityType; }
        public double getHealth()         { return health; }
        public double getDamage()         { return damage; }
        public double getDefense()        { return defense; }
        public int getBaseLevel()         { return baseLevel; }
        public int getSpawnWeight()       { return spawnWeight; }
        public int getXpReward()          { return xpReward; }
        public int getCoinReward()        { return coinReward; }
        /** Max of this mob alive at one spawn point. */
        public int getMaxPerSpot()        { return maxPerSpot; }
        /** Seconds between spawns at a point once below the cap. */
        public int getRespawnSeconds()    { return respawnSeconds; }
        /** Whether this mob only spawns at night (8pm–6am). */
        public boolean isNightOnly()      { return nightOnly; }

        /** Returns the multiplier applied to a base stat at the given level. */
        private double scaleFactor(int level) {
            return 1.0 + SCALE_PER_LEVEL * Math.max(0, level - baseLevel);
        }

        /**
         * Returns the health this mob should have at the given level.
         *
         * @param level the target level (levels below the base level are clamped)
         * @return the level-scaled max health
         */
        public double getScaledHealth(int level) {
            return health * scaleFactor(level);
        }

        /**
         * Returns the damage this mob deals at the given level.
         *
         * @param level the target level (levels below the base level are clamped)
         * @return the level-scaled damage
         */
        public double getScaledDamage(int level) {
            return damage * scaleFactor(level);
        }

        /**
         * Returns the defense this mob has at the given level.
         *
         * @param level the target level (levels below the base level are clamped)
         * @return the level-scaled defense
         */
        public double getScaledDefense(int level) {
            return defense * scaleFactor(level);
        }
    }

    private static final MobManager INSTANCE = new MobManager();

    private final Map<String, MobDefinition> mobs = new LinkedHashMap<>();
    private final Random random = new Random();

    private MobManager() {
    }

    /**
     * Returns the single shared {@code MobManager} instance.
     *
     * @return the singleton instance
     */
    public static MobManager getInstance() {
        return INSTANCE;
    }

    /**
     * Loads mob definitions from {@code mobs.yml} bundled inside the plugin jar.
     *
     * @param plugin the active plugin instance used to read the resource
     * @throws IllegalStateException if {@code mobs.yml} is missing from the jar
     */
    public void init(JavaPlugin plugin) {
        InputStream stream = plugin.getResource("mobs.yml");
        if (stream == null) {
            throw new IllegalStateException("mobs.yml not found in plugin resources");
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(
                new InputStreamReader(stream, StandardCharsets.UTF_8));
        ConfigurationSection section = config.getConfigurationSection("mobs");
        if (section == null) {
            plugin.getLogger().warning("mobs.yml contains no 'mobs' section");
            return;
        }
        mobs.clear();
        for (String id : section.getKeys(false)) {
            ConfigurationSection mob = section.getConfigurationSection(id);
            Objects.requireNonNull(mob, "null section for mob: " + id);
            String displayName = mob.getString("displayName", id);
            EntityType entityType = EntityType.valueOf(
                    mob.getString("entityType", "ZOMBIE").toUpperCase());
            double health      = mob.getDouble("health", 20.0);
            double damage      = mob.getDouble("damage", 5.0);
            double defense     = mob.getDouble("defense", 0.0);
            int    baseLevel   = Math.max(1, mob.getInt("level", 1));
            int    spawnWeight = Math.max(0, mob.getInt("spawnWeight", 1));
            int    xpReward    = mob.getInt("xpReward", 0);
            int    coinReward  = mob.getInt("coinReward", 0);
            int    maxPerSpot  = Math.max(1, mob.getInt("maxPerSpot", 1));
            int    respawnSecs = Math.max(1, mob.getInt("respawnSeconds", 15));
            boolean nightOnly  = mob.getBoolean("nightOnly", false);
            mobs.put(id, new MobDefinition(id, displayName, entityType,
                    health, damage, defense, baseLevel, spawnWeight, xpReward, coinReward,
                    maxPerSpot, respawnSecs, nightOnly));
        }
        plugin.getLogger().info("Loaded " + mobs.size() + " mob definition(s) from mobs.yml");
    }

    /**
     * Returns the {@link MobDefinition} for the given id, or {@code null} if not found.
     *
     * @param id the mob identifier as defined in {@code mobs.yml}
     * @return the definition, or {@code null}
     */
    public MobDefinition getMob(String id) {
        return mobs.get(id);
    }

    /**
     * Returns an unmodifiable view of all loaded mob definitions keyed by id.
     *
     * @return immutable map of id → definition
     */
    public Map<String, MobDefinition> getMobs() {
        return Collections.unmodifiableMap(mobs);
    }

    /**
     * Rolls the weighted spawn table and returns a randomly selected mob
     * definition, where each mob's chance is proportional to its spawn weight.
     *
     * @return a weighted-random {@link MobDefinition}, or {@code null} if no mob
     *         has a positive spawn weight
     */
    public MobDefinition rollSpawn() {
        return rollSpawn(random);
    }

    /**
     * Rolls the weighted spawn table using the supplied source of randomness.
     *
     * @param rng the random source; must not be null
     * @return a weighted-random {@link MobDefinition}, or {@code null} if no mob
     *         has a positive spawn weight
     */
    public MobDefinition rollSpawn(Random rng) {
        Objects.requireNonNull(rng, "rng");
        List<MobDefinition> candidates = new ArrayList<>();
        int totalWeight = 0;
        for (MobDefinition def : mobs.values()) {
            if (def.getSpawnWeight() > 0) {
                candidates.add(def);
                totalWeight += def.getSpawnWeight();
            }
        }
        if (totalWeight <= 0) {
            return null;
        }
        int roll = rng.nextInt(totalWeight);
        for (MobDefinition def : candidates) {
            roll -= def.getSpawnWeight();
            if (roll < 0) {
                return def;
            }
        }
        return candidates.get(candidates.size() - 1);
    }
}
