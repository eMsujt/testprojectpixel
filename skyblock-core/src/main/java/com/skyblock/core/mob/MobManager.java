package com.skyblock.core.mob;

import com.skyblock.core.SkyBlockPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Singleton manager for SkyBlock custom mobs.
 *
 * <p>Loads mob definitions from {@code mobs.yml} on first access and exposes
 * them by id for combat/spawning use.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class MobManager {

    /** Immutable data record for a single mob definition. */
    public static final class MobDefinition {
        private final String id;
        private final String displayName;
        private final EntityType entityType;
        private final double health;
        private final double damage;
        private final int xpReward;
        private final int coinReward;

        MobDefinition(String id, String displayName, EntityType entityType,
                      double health, double damage, int xpReward, int coinReward) {
            this.id = id;
            this.displayName = displayName;
            this.entityType = entityType;
            this.health = health;
            this.damage = damage;
            this.xpReward = xpReward;
            this.coinReward = coinReward;
        }

        public String getId()           { return id; }
        public String getDisplayName()  { return displayName; }
        public EntityType getEntityType(){ return entityType; }
        public double getHealth()        { return health; }
        public double getDamage()        { return damage; }
        public int getXpReward()         { return xpReward; }
        public int getCoinReward()       { return coinReward; }
    }

    private static final MobManager INSTANCE = new MobManager();

    private final Map<String, MobDefinition> mobs = new LinkedHashMap<>();

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
    public void init(SkyBlockPlugin plugin) {
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
        for (String id : section.getKeys(false)) {
            ConfigurationSection mob = section.getConfigurationSection(id);
            Objects.requireNonNull(mob, "null section for mob: " + id);
            String displayName = mob.getString("displayName", id);
            EntityType entityType = EntityType.valueOf(
                    mob.getString("entityType", "ZOMBIE").toUpperCase());
            double health     = mob.getDouble("health", 20.0);
            double damage     = mob.getDouble("damage", 5.0);
            int    xpReward   = mob.getInt("xpReward", 0);
            int    coinReward = mob.getInt("coinReward", 0);
            mobs.put(id, new MobDefinition(id, displayName, entityType,
                    health, damage, xpReward, coinReward));
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
}
