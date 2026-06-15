package com.skyblock.plugin.items;

import com.skyblock.core.model.Rarity;
import com.skyblock.core.stat.Stat;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

/**
 * Mutable config POJO representing a custom item definition loaded from YAML.
 *
 * <p>Acts as the intermediate form between a raw {@link ConfigurationSection} and
 * the immutable {@link SkyBlockItem} domain record. Use
 * {@link #fromSection(JavaPlugin, String, ConfigurationSection)} to parse a YAML
 * section, then {@link #toSkyBlockItem()} to obtain the domain object.</p>
 */
public final class ItemStatConfig {

    private final String id;
    private final Material material;
    private final String displayName;
    private final Rarity rarity;
    private final StatBlock stats;

    public ItemStatConfig(String id, Material material, String displayName,
                          Rarity rarity, StatBlock stats) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must be non-blank");
        }
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must be non-blank");
        }
        this.id = id;
        this.material = Objects.requireNonNull(material, "material");
        this.displayName = displayName;
        this.rarity = Objects.requireNonNull(rarity, "rarity");
        this.stats = Objects.requireNonNull(stats, "stats");
    }

    /**
     * Parses a single YAML section into an {@link ItemStatConfig}, returning
     * {@code null} and logging a warning if the section is invalid.
     *
     * @param plugin  used for warning logging only
     * @param id      the item id (the YAML key)
     * @param section the YAML section for this item
     * @return the parsed config, or {@code null} if the section is invalid
     */
    public static ItemStatConfig fromSection(JavaPlugin plugin, String id, ConfigurationSection section) {
        Material material = Material.matchMaterial(section.getString("material", ""));
        if (material == null) {
            plugin.getLogger().warning("Skipping item '" + id + "': unknown material.");
            return null;
        }
        Rarity rarity;
        try {
            rarity = Rarity.valueOf(
                    section.getString("rarity", "COMMON").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Skipping item '" + id + "': unknown rarity.");
            return null;
        }
        String displayName = section.getString("displayName", id);
        StatBlock stats = new StatBlock(
                (int) section.getDouble("health"),
                section.getInt("defense"),
                section.getInt("strength"),
                section.getInt("intelligence"),
                section.getInt("critChance"),
                section.getInt("critDamage"),
                section.getInt("speed"));
        return new ItemStatConfig(id, material, displayName, rarity, stats);
    }

    /** Converts this config into the immutable {@link SkyBlockItem} domain object. */
    public SkyBlockItem toSkyBlockItem() {
        ItemStats itemStats = new ItemStats();
        itemStats.setStat(Stat.HEALTH, stats.getHealth());
        itemStats.setStat(Stat.DEFENSE, stats.getDefense());
        itemStats.setStat(Stat.STRENGTH, stats.getStrength());
        itemStats.setStat(Stat.INTELLIGENCE, stats.getIntelligence());
        itemStats.setStat(Stat.CRIT_CHANCE, stats.getCritChance());
        itemStats.setStat(Stat.CRIT_DAMAGE, stats.getCritDamage());
        itemStats.setStat(Stat.SPEED, stats.getSpeed());
        return new SkyBlockItem(id, material, displayName, rarity, itemStats, Collections.emptyList());
    }

    public String getId() { return id; }
    public Material getMaterial() { return material; }
    public String getDisplayName() { return displayName; }
    public Rarity getRarity() { return rarity; }
    public StatBlock getStats() { return stats; }

    /**
     * Immutable block of stat bonuses for a custom item.
     *
     * @param health       bonus Health
     * @param defense      bonus Defense
     * @param strength     bonus Strength
     * @param intelligence bonus Intelligence
     * @param critChance   bonus Crit Chance
     * @param critDamage   bonus Crit Damage
     * @param speed        bonus Speed
     */
    public static final class StatBlock {

        private final int health;
        private final int defense;
        private final int strength;
        private final int intelligence;
        private final int critChance;
        private final int critDamage;
        private final int speed;

        public StatBlock(int health, int defense, int strength, int intelligence,
                         int critChance, int critDamage, int speed) {
            this.health = health;
            this.defense = defense;
            this.strength = strength;
            this.intelligence = intelligence;
            this.critChance = critChance;
            this.critDamage = critDamage;
            this.speed = speed;
        }

        public int getHealth() { return health; }
        public int getDefense() { return defense; }
        public int getStrength() { return strength; }
        public int getIntelligence() { return intelligence; }
        public int getCritChance() { return critChance; }
        public int getCritDamage() { return critDamage; }
        public int getSpeed() { return speed; }
    }
}
