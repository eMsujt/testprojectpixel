package com.skyblock.plugin.economy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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
public final class ShopManager {

    private static final ShopManager INSTANCE = new ShopManager();

    /**
     * A single loaded shop.
     *
     * @param id       the shop-id
     * @param title    the menu title (supports colour codes)
     * @param location the NPC location, or {@code null} if its world is unloaded
     * @param entries  the items this shop sells
     */
    public record Shop(String id, String title, Location location, List<ShopMenu.ShopEntry> entries) {
        public Shop {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(title, "title");
            entries = entries != null ? List.copyOf(entries) : List.of();
        }
    }

    private final Map<String, Shop> shops = new LinkedHashMap<>();

    private ShopManager() {
    }

    public static ShopManager getInstance() {
        return INSTANCE;
    }

    /**
     * Reads {@code shops.yml} from the plugin data folder, copying the bundled
     * default out of the jar on first run, then parses every defined shop.
     *
     * @param plugin the owning plugin, used for resource extraction and logging
     */
    public void load(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "shops.yml");
        if (!file.exists() && plugin.getResource("shops.yml") != null) {
            plugin.saveResource("shops.yml", false);
        }
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = cfg.isConfigurationSection("shops")
                ? cfg.getConfigurationSection("shops")
                : cfg;
        shops.clear();
        for (String id : root.getKeys(false)) {
            if (!root.isConfigurationSection(id)) {
                continue;
            }
            Shop shop = parse(plugin, id, root.getConfigurationSection(id));
            if (shop != null) {
                shops.put(id, shop);
            }
        }
        plugin.getLogger().info("Loaded " + shops.size() + " shops.");
    }

    /** Parses a single shop section, or returns {@code null} if it is invalid. */
    private Shop parse(JavaPlugin plugin, String id, ConfigurationSection section) {
        String title = section.getString("title", id);
        Location location = parseLocation(section.getString("npc-location"));
        List<ShopMenu.ShopEntry> entries = new ArrayList<>();
        for (String raw : section.getStringList("items")) {
            int sep = raw.lastIndexOf(':');
            if (sep < 0) {
                plugin.getLogger().warning("Skipping shop item '" + raw + "' in '" + id + "': missing price.");
                continue;
            }
            Material material = Material.matchMaterial(raw.substring(0, sep).trim());
            if (material == null) {
                plugin.getLogger().warning("Skipping shop item '" + raw + "' in '" + id + "': unknown material.");
                continue;
            }
            int price;
            try {
                price = Integer.parseInt(raw.substring(sep + 1).trim());
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Skipping shop item '" + raw + "' in '" + id + "': invalid price.");
                continue;
            }
            entries.add(new ShopMenu.ShopEntry(material, price));
        }
        return new Shop(id, title, location, entries);
    }

    /** Parses a {@code "world,x,y,z"} string into a {@link Location}, or {@code null} if invalid. */
    private Location parseLocation(String raw) {
        if (raw == null) {
            return null;
        }
        String[] parts = raw.split(",");
        if (parts.length != 4) {
            return null;
        }
        World world = Bukkit.getWorld(parts[0].trim());
        if (world == null) {
            return null;
        }
        try {
            return new Location(
                    world,
                    Double.parseDouble(parts[1].trim()),
                    Double.parseDouble(parts[2].trim()),
                    Double.parseDouble(parts[3].trim()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Returns the loaded shop with the given id, or {@code null} if absent. */
    public Shop getShop(String id) {
        return shops.get(id);
    }

    /** Returns an unmodifiable view of all loaded shops keyed by id. */
    public Map<String, Shop> getShops() {
        return Collections.unmodifiableMap(shops);
    }

    /**
     * Opens the shop with the given id for a player.
     *
     * @return {@code true} if the shop existed and was opened, {@code false} otherwise
     */
    public boolean openShop(String id, Player player) {
        Objects.requireNonNull(player, "player");
        Shop shop = shops.get(id);
        if (shop == null) {
            return false;
        }
        new ShopMenu(shop.title(), shop.entries()).open(player);
        return true;
    }
}
