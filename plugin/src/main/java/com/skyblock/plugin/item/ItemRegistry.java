package com.skyblock.plugin.item;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * YAML-driven registry of custom item definitions.
 *
 * <p>The bundled {@code items.yml} is copied out of the jar on first run and
 * parsed: every key under the {@code items} section is an item-id mapped to its
 * {@code displayName}, {@link Material}, {@code rarity} and the combat/defensive
 * stats it grants. Loaded definitions are held in memory and looked up by id.</p>
 */
public final class ItemRegistry {

    private static final ItemRegistry INSTANCE = new ItemRegistry();

    private final Map<String, ItemDefinition> items = new LinkedHashMap<>();

    private ItemRegistry() {
    }

    public static ItemRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Copies the bundled {@code items.yml} out of the jar on first run, then
     * parses every item definition into memory.
     *
     * @param plugin the owning plugin, used for resource extraction and logging
     */
    public void load(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "items.yml");
        if (!file.exists() && plugin.getResource("items.yml") != null) {
            plugin.saveResource("items.yml", false);
        }
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = cfg.isConfigurationSection("items")
                ? cfg.getConfigurationSection("items")
                : cfg;
        items.clear();
        for (String id : root.getKeys(false)) {
            if (!root.isConfigurationSection(id)) {
                continue;
            }
            ItemDefinition item = parse(plugin, id, root.getConfigurationSection(id));
            if (item != null) {
                items.put(id, item);
            }
        }
        plugin.getLogger().info("Loaded " + items.size() + " item definitions.");
    }

    private ItemDefinition parse(JavaPlugin plugin, String id, ConfigurationSection section) {
        Material material = Material.matchMaterial(section.getString("material", ""));
        if (material == null) {
            plugin.getLogger().warning("Skipping item '" + id + "': unknown material.");
            return null;
        }
        Rarity rarity;
        try {
            rarity = Rarity.valueOf(section.getString("rarity", "COMMON").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Skipping item '" + id + "': unknown rarity.");
            return null;
        }
        return new ItemDefinition(
                id,
                section.getString("displayName", id),
                material,
                rarity,
                (int) section.getDouble("damage"),
                section.getInt("health"),
                section.getInt("defense"),
                section.getInt("strength"),
                section.getInt("intelligence"),
                section.getInt("speed"),
                section.getInt("critDamage"),
                section.getString("description", ""));
    }

    /** Returns the item definition with the given id, or {@code null} if none is registered. */
    public ItemDefinition getItem(String id) {
        return items.get(id);
    }

    /** Returns an unmodifiable view of all registered item definitions. */
    public Map<String, ItemDefinition> getItems() {
        return Collections.unmodifiableMap(items);
    }

    /** Item rarity tiers, ordered from least to most rare. */
    public enum Rarity {
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC, DIVINE, SPECIAL
    }

    /**
     * An immutable custom item definition parsed from {@code items.yml}.
     *
     * @param id           the item's unique id
     * @param displayName  the item's human-readable name
     * @param material     the Bukkit material the item renders as
     * @param rarity       the item's rarity tier
     * @param damage       bonus Damage
     * @param health       bonus Health
     * @param defense      bonus Defense
     * @param strength     bonus Strength
     * @param intelligence bonus Intelligence
     * @param speed        bonus Speed
     * @param critDamage   bonus Crit Damage
     * @param description  flavour/ability text shown in lore
     */
    public record ItemDefinition(String id, String displayName, Material material, Rarity rarity,
                                 int damage, int health, int defense, int strength,
                                 int intelligence, int speed, int critDamage, String description) {
    }
}
