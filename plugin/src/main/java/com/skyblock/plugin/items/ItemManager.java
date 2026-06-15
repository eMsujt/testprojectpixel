package com.skyblock.plugin.items;

import com.skyblock.core.model.Rarity;
import com.skyblock.core.stat.Stat;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class ItemManager {

    private static final ItemManager INSTANCE = new ItemManager();

    private final Map<String, SkyBlockItem> items = new LinkedHashMap<>();

    private ItemManager() {
    }

    public static ItemManager getInstance() {
        return INSTANCE;
    }

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
            SkyBlockItem item = parse(plugin, id, root.getConfigurationSection(id));
            if (item != null) {
                items.put(id, item);
            }
        }
        plugin.getLogger().info("Loaded " + items.size() + " custom items.");
    }

    private SkyBlockItem parse(JavaPlugin plugin, String id, ConfigurationSection section) {
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
        String displayName = section.getString("displayName", id);
        ItemStats stats = new ItemStats();
        for (Stat type : Stat.values()) {
            double value = section.getDouble(type.name().toLowerCase(Locale.ROOT));
            if (value != 0) {
                stats.setStat(type, value);
            }
        }
        return new SkyBlockItem(id, material, displayName, rarity, stats, Collections.emptyList());
    }

    public SkyBlockItem getItem(String id) {
        return items.get(id);
    }

    public Map<String, SkyBlockItem> getItems() {
        return Collections.unmodifiableMap(items);
    }
}
