package com.skyblock.plugin.items;

import com.skyblock.plugin.gui.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads and serves the plugin's custom items defined in {@code items.yml}.
 *
 * <p>Each item is keyed by its id and carries a display name, material and
 * rarity, with optional lore lines. Items are read once on enable and held in
 * memory for lookup by other systems.</p>
 */
public final class ItemManager {

    private static final ItemManager INSTANCE = new ItemManager();

    private final Map<String, CustomItem> items = new LinkedHashMap<>();

    private ItemManager() {
    }

    public static ItemManager getInstance() {
        return INSTANCE;
    }

    /**
     * Reads {@code items.yml} from the plugin data folder, copying the bundled
     * default out of the jar on first run, then parses each defined item.
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
        // Support an "items" wrapper section, falling back to root-level keys.
        ConfigurationSection root = cfg.isConfigurationSection("items")
                ? cfg.getConfigurationSection("items")
                : cfg;
        items.clear();
        for (String id : root.getKeys(false)) {
            if (!root.isConfigurationSection(id)) {
                continue;
            }
            ConfigurationSection section = root.getConfigurationSection(id);
            Material material = Material.matchMaterial(section.getString("material", ""));
            if (material == null) {
                plugin.getLogger().warning("Skipping item '" + id + "': unknown material.");
                continue;
            }
            String displayName = section.getString("displayName", id);
            String rarity = section.getString("rarity", "COMMON");
            List<String> lore = section.getStringList("lore");
            items.put(id, new CustomItem(id, displayName, material, rarity, lore));
        }
        plugin.getLogger().info("Loaded " + items.size() + " custom items.");
    }

    /** Returns the custom item with the given id, or {@code null} if absent. */
    public CustomItem getItem(String id) {
        return items.get(id);
    }

    /** Returns an unmodifiable view of all loaded custom items keyed by id. */
    public Map<String, CustomItem> getItems() {
        return Collections.unmodifiableMap(items);
    }

    /**
     * An immutable custom item definition parsed from {@code items.yml}.
     */
    public static final class CustomItem {

        private final String id;
        private final String displayName;
        private final Material material;
        private final String rarity;
        private final List<String> lore;

        CustomItem(String id, String displayName, Material material, String rarity, List<String> lore) {
            this.id = id;
            this.displayName = displayName;
            this.material = material;
            this.rarity = rarity;
            this.lore = List.copyOf(lore);
        }

        public String getId() {
            return id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Material getMaterial() {
            return material;
        }

        public String getRarity() {
            return rarity;
        }

        public List<String> getLore() {
            return lore;
        }

        /** Builds a fresh {@link ItemStack} representing this item. */
        public ItemStack toItemStack() {
            return new ItemBuilder(material)
                    .displayName(displayName)
                    .lore(lore)
                    .build();
        }
    }
}
