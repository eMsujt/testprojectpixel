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
 * YAML-driven registry of custom {@link ItemRegistry.ItemDefinition custom item}
 * definitions loaded from {@code items.yml} on startup.
 *
 * <p>The bundled {@code items.yml} is copied out of the jar on first run and
 * parsed: every key under the {@code items} section is an item-id mapped to its
 * {@code displayName}, {@link Material}, {@code rarity} and the combat/defensive
 * stats it grants. Loaded definitions are held in memory and looked up by id.</p>
 */
public final class CustomItemRegistry {

    private static final CustomItemRegistry INSTANCE = new CustomItemRegistry();

    private final Map<String, ItemRegistry.ItemDefinition> items = new LinkedHashMap<>();

    private CustomItemRegistry() {
    }

    public static CustomItemRegistry getInstance() {
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
            ItemRegistry.ItemDefinition item = parse(plugin, id, root.getConfigurationSection(id));
            if (item != null) {
                items.put(id, item);
            }
        }
        plugin.getLogger().info("Loaded " + items.size() + " custom items.");
    }

    /** Parses a single item section, or returns {@code null} if it is invalid. */
    private ItemRegistry.ItemDefinition parse(JavaPlugin plugin, String id, ConfigurationSection section) {
        Material material = Material.matchMaterial(section.getString("material", ""));
        if (material == null) {
            plugin.getLogger().warning("Skipping item '" + id + "': unknown material.");
            return null;
        }
        ItemRegistry.Rarity rarity;
        try {
            rarity = ItemRegistry.Rarity.valueOf(
                    section.getString("rarity", "COMMON").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Skipping item '" + id + "': unknown rarity.");
            return null;
        }
        return new ItemRegistry.ItemDefinition(
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

    /** Returns the registered item with the given id, or {@code null} if absent. */
    public ItemRegistry.ItemDefinition getItem(String id) {
        return items.get(id);
    }

    /** Returns an unmodifiable view of all registered items keyed by id. */
    public Map<String, ItemRegistry.ItemDefinition> getItems() {
        return Collections.unmodifiableMap(items);
    }
}
