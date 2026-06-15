package com.skyblock.plugin.items;

import com.skyblock.core.model.Rarity;
import com.skyblock.core.stat.Stat;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A custom SkyBlock item: a unique id, the {@link Material} it renders as,
 * a display name, a {@link Rarity} tier, an {@link ItemStats} block of bonuses,
 * and a list of ability descriptions.
 */
public final class SkyBlockItem {

    private final String id;
    private final Material material;
    private final String displayName;
    private final Rarity rarity;
    private final ItemStats stats;
    private final List<String> abilities;

    public SkyBlockItem(String id, Material material, String displayName, Rarity rarity,
                        ItemStats stats, List<String> abilities) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must be non-blank");
        }
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must be non-blank");
        }
        Objects.requireNonNull(material, "material");
        Objects.requireNonNull(rarity, "rarity");
        Objects.requireNonNull(stats, "stats");
        this.id = id;
        this.material = material;
        this.displayName = displayName;
        this.rarity = rarity;
        this.stats = stats;
        this.abilities = Collections.unmodifiableList(
                new ArrayList<>(abilities == null ? Collections.emptyList() : abilities));
    }

    public String getId() { return id; }
    public Material getMaterial() { return material; }
    public String getDisplayName() { return displayName; }
    public Rarity getRarity() { return rarity; }
    public ItemStats getStats() { return stats; }
    public List<String> getAbilities() { return abilities; }

    /**
     * Returns the value for the stat named by {@code name}, mapping common
     * camelCase names to the canonical {@link Stat} enum. Returns {@code 0.0}
     * for unrecognised names (e.g. "damage", which has no Stat constant).
     */
    public double stat(String name) {
        if (name == null) return 0.0;
        switch (name.toLowerCase(Locale.ROOT)) {
            case "health":        return stats.getStat(Stat.HEALTH);
            case "defense":       return stats.getStat(Stat.DEFENSE);
            case "strength":      return stats.getStat(Stat.STRENGTH);
            case "intelligence":  return stats.getStat(Stat.INTELLIGENCE);
            case "critchance":    return stats.getStat(Stat.CRIT_CHANCE);
            case "critdamage":    return stats.getStat(Stat.CRIT_DAMAGE);
            case "speed":         return stats.getStat(Stat.SPEED);
            default:              return 0.0;
        }
    }

    /**
     * An immutable block of stat bonuses granted by a reforge, expressed as
     * {@code int} values matching the seven SkyBlock combat stats.
     *
     * @param health       bonus Health
     * @param defense      bonus Defense
     * @param strength     bonus Strength
     * @param intelligence bonus Intelligence
     * @param critChance   bonus Crit Chance
     * @param critDamage   bonus Crit Damage
     * @param speed        bonus Speed
     */
    public record StatBlock(int health, int defense, int strength, int intelligence,
                            int critChance, int critDamage, int speed) {
    }

}
