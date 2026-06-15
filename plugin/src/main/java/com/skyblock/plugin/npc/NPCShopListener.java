package com.skyblock.plugin.npc;

import com.skyblock.core.menu.ShopMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Listener that opens a {@link ShopMenu} when a player right-clicks a shop
 * {@link Villager}.
 *
 * <p>The villager's custom name (stripped of colour codes) is used as the shop
 * key, matched against {@code shops/<name>} in {@code shops.yml}. Villagers
 * without a custom name are ignored; for named shop villagers the interaction
 * is cancelled so the vanilla trade GUI does not also open.</p>
 */
public final class NPCShopListener implements Listener {

    private final JavaPlugin plugin;

    public NPCShopListener(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Villager) || entity.getCustomName() == null) {
            return;
        }

        event.setCancelled(true);
        String shopName = ChatColor.stripColor(entity.getCustomName());
        Player player = event.getPlayer();

        String title = resolveTitle(shopName);
        List<ShopMenu.ShopItem> items = loadItems(shopName);
        new ShopMenu(title, items).open(player);
    }

    private String resolveTitle(String shopName) {
        File file = ensureFile();
        if (file == null) return "§6Shop";
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection shops = cfg.isConfigurationSection("shops")
                ? cfg.getConfigurationSection("shops") : null;
        if (shops == null || !shops.isConfigurationSection(shopName)) return "§6Shop";
        String title = shops.getConfigurationSection(shopName).getString("title");
        return title != null ? title : "§6Shop";
    }

    private List<ShopMenu.ShopItem> loadItems(String shopName) {
        File file = ensureFile();
        if (file == null) return List.of();
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection shops = cfg.isConfigurationSection("shops")
                ? cfg.getConfigurationSection("shops") : null;
        if (shops == null || !shops.isConfigurationSection(shopName)) return List.of();

        List<ShopMenu.ShopItem> result = new ArrayList<>();
        for (String entry : shops.getConfigurationSection(shopName).getStringList("items")) {
            String clean = entry.contains("#") ? entry.substring(0, entry.indexOf('#')).trim() : entry.trim();
            int colon = clean.lastIndexOf(':');
            if (colon < 1) continue;
            Material material = Material.matchMaterial(clean.substring(0, colon).toUpperCase(Locale.ROOT));
            if (material == null) {
                plugin.getLogger().warning("Skipping shop entry '" + entry + "': unknown material.");
                continue;
            }
            int price;
            try {
                price = (int) Long.parseLong(clean.substring(colon + 1).trim());
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Skipping shop entry '" + entry + "': invalid price.");
                continue;
            }
            result.add(new ShopMenu.ShopItem(material, price));
        }
        return result;
    }

    private File ensureFile() {
        File file = new File(plugin.getDataFolder(), "shops.yml");
        if (!file.exists() && plugin.getResource("shops.yml") != null) {
            plugin.saveResource("shops.yml", false);
        }
        return file.exists() ? file : null;
    }
}
