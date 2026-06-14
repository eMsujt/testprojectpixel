package com.skyblock.plugin.shop;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public final class ShopManager {

    private static final ShopManager INSTANCE = new ShopManager();

    private final Map<Material, ShopItem> items = new EnumMap<>(Material.class);

    private ShopManager() {
    }

    public static ShopManager getInstance() {
        return INSTANCE;
    }

    public void load(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "shop_items.yml");
        if (!file.exists() && plugin.getResource("shop_items.yml") != null) {
            plugin.saveResource("shop_items.yml", false);
        }
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = cfg.isConfigurationSection("shop-items")
                ? cfg.getConfigurationSection("shop-items")
                : cfg;
        items.clear();
        for (String id : root.getKeys(false)) {
            if (!root.isConfigurationSection(id)) {
                continue;
            }
            Material material = Material.matchMaterial(id.toUpperCase(Locale.ROOT));
            if (material == null) {
                plugin.getLogger().warning("Skipping shop item '" + id + "': unknown material.");
                continue;
            }
            ConfigurationSection section = root.getConfigurationSection(id);
            items.put(material, new ShopItem(section.getInt("buy"), section.getInt("sell")));
        }
        plugin.getLogger().info("Loaded " + items.size() + " shop items.");
    }

    public ShopItem getShopItem(Material material) {
        return items.get(material);
    }

    public Map<Material, ShopItem> getItems() {
        return Collections.unmodifiableMap(items);
    }

    /**
     * A single NPC shop entry: the coin price to buy one of the item and the
     * (lower) coin price received when selling one back.
     *
     * @param buy  the coin price a player pays to buy one from the NPC
     * @param sell the coin price a player receives when selling one back
     */
    public record ShopItem(int buy, int sell) {
    }
}
