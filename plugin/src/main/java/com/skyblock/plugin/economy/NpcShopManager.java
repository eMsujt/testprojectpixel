package com.skyblock.plugin.economy;

import org.bukkit.Material;
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
public final class NpcShopManager {

    private static final NpcShopManager INSTANCE = new NpcShopManager();
    private static final com.skyblock.core.manager.ShopManager DELEGATE =
            com.skyblock.core.manager.ShopManager.getInstance();

    /**
     * A single purchasable item in an NPC shop.
     *
     * @param materialName the Bukkit material name (e.g. {@code "COBBLESTONE"})
     * @param buyPrice     the coin cost to purchase one unit
     */
    public record NpcShopItem(String materialName, int buyPrice) {
        public NpcShopItem {
            Objects.requireNonNull(materialName, "materialName");
        }
    }

    /**
     * A loaded NPC shop.
     *
     * @param id          the shop-id
     * @param displayName the menu title (supports colour codes)
     * @param items       the items this shop sells
     */
    public record NpcShop(String id, String displayName, List<NpcShopItem> items) {
        public NpcShop {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(displayName, "displayName");
            items = items != null ? List.copyOf(items) : List.of();
        }
    }

    private final Map<String, NpcShop> shops = new LinkedHashMap<>();

    private NpcShopManager() {
    }

    public static NpcShopManager getInstance() {
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
            NpcShop shop = parse(plugin, id, root.getConfigurationSection(id));
            if (shop != null) {
                shops.put(id, shop);
            }
        }
        plugin.getLogger().info("NpcShopManager loaded " + shops.size() + " shops.");
        // write-through: keep canonical in sync
        for (NpcShop shop : shops.values()) {
            List<com.skyblock.core.manager.ShopManager.ShopEntry> canonical = new ArrayList<>();
            for (NpcShopItem item : shop.items()) {
                canonical.add(new com.skyblock.core.manager.ShopManager.ShopEntry(
                        item.materialName().toUpperCase(), (long) item.buyPrice(), 0L));
            }
            DELEGATE.registerShop(shop.id(), shop.displayName(), canonical);
        }
    }

    /** Parses a single shop section, or returns {@code null} if it is invalid. */
    private NpcShop parse(JavaPlugin plugin, String id, ConfigurationSection section) {
        String displayName = section.getString("title", id);
        List<NpcShopItem> items = new ArrayList<>();
        for (String raw : section.getStringList("items")) {
            int sep = raw.lastIndexOf(':');
            if (sep < 0) {
                plugin.getLogger().warning("NpcShopManager: skipping item '" + raw + "' in '" + id + "': missing price.");
                continue;
            }
            String materialName = raw.substring(0, sep).trim().toUpperCase();
            int buyPrice;
            try {
                buyPrice = Integer.parseInt(raw.substring(sep + 1).trim());
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("NpcShopManager: skipping item '" + raw + "' in '" + id + "': invalid price.");
                continue;
            }
            items.add(new NpcShopItem(materialName, buyPrice));
        }
        return new NpcShop(id, displayName, items);
    }

    /** Returns the loaded shop with the given id, or {@code null} if absent. */
    public NpcShop getShop(String id) {
        return shops.get(id);
    }

    /** Returns an unmodifiable view of all loaded shops keyed by id. */
    public Map<String, NpcShop> getShops() {
        return Collections.unmodifiableMap(shops);
    }

    /**
     * Opens the shop with the given id for a player.
     *
     * @return {@code true} if the shop existed and was opened, {@code false} otherwise
     */
    public boolean openShop(String id, Player player) {
        Objects.requireNonNull(player, "player");
        NpcShop shop = shops.get(id);
        if (shop == null) {
            return false;
        }
        List<ShopMenu.ShopEntry> entries = new ArrayList<>();
        for (NpcShopItem item : shop.items()) {
            Material material = Material.matchMaterial(item.materialName());
            if (material != null) {
                entries.add(new ShopMenu.ShopEntry(material, item.buyPrice()));
            }
        }
        new ShopMenu(shop.displayName(), entries).open(player);
        return true;
    }
}
