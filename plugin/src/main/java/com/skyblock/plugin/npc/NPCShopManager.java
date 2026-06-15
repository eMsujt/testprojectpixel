package com.skyblock.plugin.npc;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @deprecated Use {@link com.skyblock.core.manager.ShopManager} (the canonical singleton).
 */
@Deprecated
public final class NPCShopManager {

    private static final NPCShopManager INSTANCE = new NPCShopManager();

    /**
     * A single sellable NPC shop item.
     *
     * @param itemId    the item-id
     * @param buyPrice  the coins paid to buy one unit
     * @param sellPrice the coins received for selling one unit
     */
    public record ShopItem(String itemId, int buyPrice, int sellPrice) {
        public ShopItem {
            Objects.requireNonNull(itemId, "itemId");
        }
    }

    private final Map<String, ShopItem> items = new LinkedHashMap<>();

    private NPCShopManager() {
    }

    public static NPCShopManager getInstance() {
        return INSTANCE;
    }

    /**
     * Reads {@code npcshops.yml} from the plugin data folder, copying the bundled
     * default out of the jar on first run, then parses every defined item.
     *
     * @param plugin the owning plugin, used for resource extraction and logging
     */
    public void load(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "npcshops.yml");
        if (!file.exists() && plugin.getResource("npcshops.yml") != null) {
            plugin.saveResource("npcshops.yml", false);
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
            ShopItem item = parse(id, root.getConfigurationSection(id));
            items.put(id, item);
        }
        plugin.getLogger().info("Loaded " + items.size() + " NPC shop items.");
    }

    /** Parses a single item section. */
    private ShopItem parse(String id, ConfigurationSection section) {
        int buyPrice = section.getInt("buy");
        int sellPrice = section.getInt("sell");
        return new ShopItem(id, buyPrice, sellPrice);
    }

    /** Returns the loaded item with the given id, or {@code null} if absent. */
    public ShopItem getItem(String itemId) {
        return items.get(itemId);
    }

    /** Returns an unmodifiable view of all loaded items in definition order. */
    public List<ShopItem> getItems() {
        return Collections.unmodifiableList(new ArrayList<>(items.values()));
    }
}
