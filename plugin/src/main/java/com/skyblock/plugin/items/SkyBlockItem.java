package com.skyblock.plugin.items;

import com.skyblock.core.model.Rarity;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

}
