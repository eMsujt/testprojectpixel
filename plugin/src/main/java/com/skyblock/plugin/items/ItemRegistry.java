package com.skyblock.plugin.items;

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
 * YAML-driven framework that loads {@link SkyBlockItem} definitions from the
 * {@code items/} resource directory.
 *
 * <p>Every {@code *.yml} file under the plugin data folder's {@code items/}
 * subdirectory is parsed; each top-level key in a file defines one item. The
 * bundled defaults are copied out of the jar on first run. Loaded items are
 * held in memory and looked up by their id.</p>
 */
public final class ItemRegistry {

    private static final ItemRegistry INSTANCE = new ItemRegistry();

    private final Map<String, SkyBlockItem> items = new LinkedHashMap<>();

    private ItemRegistry() {
    }

    public static ItemRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Reads every {@code *.yml} file from the {@code items/} directory inside the
     * plugin data folder, copying the bundled default out of the jar on first
     * run, then parses each defined item.
     *
     * @param plugin the owning plugin, used for resource extraction and logging
     */
    public void load(JavaPlugin plugin) {
        File dir = new File(plugin.getDataFolder(), "items");
        if (!dir.isDirectory() && plugin.getResource("items/items.yml") != null) {
            plugin.saveResource("items/items.yml", false);
        }
        if (!dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles((d, name) -> name.toLowerCase(Locale.ROOT).endsWith(".yml"));
        if (files == null) {
            return;
        }
        items.clear();
        for (File file : files) {
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            // Support an "items" wrapper section, falling back to root-level keys.
            ConfigurationSection root = cfg.isConfigurationSection("items")
                    ? cfg.getConfigurationSection("items")
                    : cfg;
            for (String id : root.getKeys(false)) {
                if (!root.isConfigurationSection(id)) {
                    continue;
                }
                SkyBlockItem item = parse(plugin, id, root.getConfigurationSection(id));
                if (item != null) {
                    items.put(id, item);
                }
            }
        }
        plugin.getLogger().info("Loaded " + items.size() + " registry items.");
    }

    /** Parses a single item section, or returns {@code null} if it is invalid. */
    private SkyBlockItem parse(JavaPlugin plugin, String id, ConfigurationSection section) {
        Material material = Material.matchMaterial(section.getString("material", ""));
        if (material == null) {
            plugin.getLogger().warning("Skipping item '" + id + "': unknown material.");
            return null;
        }
        SkyBlockItem.Rarity rarity;
        try {
            rarity = SkyBlockItem.Rarity.valueOf(
                    section.getString("rarity", "COMMON").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Skipping item '" + id + "': unknown rarity.");
            return null;
        }
        String displayName = section.getString("displayName", id);
        SkyBlockItem.StatBlock stats = new SkyBlockItem.StatBlock(
                section.getInt("health"),
                section.getInt("defense"),
                section.getInt("strength"),
                section.getInt("intelligence"),
                section.getInt("critChance"),
                section.getInt("critDamage"),
                section.getInt("speed"));
        return new SkyBlockItem(id, material, rarity, displayName, stats);
    }

    /** Returns the registered item with the given id, or {@code null} if absent. */
    public SkyBlockItem getItem(String id) {
        return items.get(id);
    }

    /** Returns an unmodifiable view of all registered items keyed by id. */
    public Map<String, SkyBlockItem> getItems() {
        return Collections.unmodifiableMap(items);
    }
}
