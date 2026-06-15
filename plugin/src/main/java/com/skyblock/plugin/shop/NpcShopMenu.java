package com.skyblock.plugin.shop;

import com.skyblock.economy.CoinManager;
import com.skyblock.core.util.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @deprecated Use {@link com.skyblock.core.menu.ShopMenu} instead.
 *
 * YAML-driven 54-slot (6-row) chest GUI for a single NPC shop.
 *
 * <p>The shop's stock is read from {@code shops.yml} in the plugin data folder,
 * with the bundled default copied out of the jar on first run. Each entry under
 * {@code shops.<shopName>.items} is a {@code "MATERIAL:price"} string. Entries
 * are laid out left-to-right from slot 0 (capped at the 54 available slots),
 * each showing its price as appended lore. Clicking an entry withdraws its price
 * from the clicking player's purse (via {@link CoinManager}) and grants the item;
 * if they can't afford it, the purchase is rejected with a message.</p>
 */
@Deprecated
public class NpcShopMenu extends Menu {

    private final CoinManager coinManager;
    private final List<ShopEntry> entries = new ArrayList<>();
    private Map<Integer, ShopEntry> slotMap = Collections.emptyMap();

    /**
     * Loads the named shop using the shared {@link CoinManager} instance.
     *
     * @param plugin   the owning plugin, used for resource extraction and logging
     * @param shopName the key under {@code shops} in shops.yml
     */
    public NpcShopMenu(JavaPlugin plugin, String shopName) {
        this(plugin, shopName, CoinManager.getInstance());
    }

    /**
     * Loads the named shop backed by the given {@link CoinManager}.
     *
     * @param plugin      the owning plugin, used for resource extraction and logging
     * @param shopName    the key under {@code shops} in shops.yml
     * @param coinManager the coin source charged on purchase
     */
    public NpcShopMenu(JavaPlugin plugin, String shopName, CoinManager coinManager) {
        super(resolveTitle(plugin, Objects.requireNonNull(shopName, "shopName")), 6);
        this.coinManager = Objects.requireNonNull(coinManager, "coinManager");
        load(plugin, shopName);
        Map<Integer, ShopEntry> map = new LinkedHashMap<>();
        for (int i = 0; i < entries.size() && i < 54; i++) {
            map.put(i, entries.get(i));
        }
        this.slotMap = Collections.unmodifiableMap(map);
    }

    private static String resolveTitle(JavaPlugin plugin, String shopName) {
        File file = ensureFile(plugin);
        if (file == null) {
            return "§6Shop";
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection shops = cfg.isConfigurationSection("shops")
                ? cfg.getConfigurationSection("shops") : null;
        if (shops == null || !shops.isConfigurationSection(shopName)) {
            return "§6Shop";
        }
        String title = shops.getConfigurationSection(shopName).getString("title");
        return title != null ? title : "§6Shop";
    }

    private static File ensureFile(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "shops.yml");
        if (!file.exists() && plugin.getResource("shops.yml") != null) {
            plugin.saveResource("shops.yml", false);
        }
        return file.exists() ? file : null;
    }

    private void load(JavaPlugin plugin, String shopName) {
        File file = ensureFile(plugin);
        if (file == null) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection shops = cfg.isConfigurationSection("shops")
                ? cfg.getConfigurationSection("shops") : null;
        if (shops == null || !shops.isConfigurationSection(shopName)) {
            return;
        }
        List<String> itemList = shops.getConfigurationSection(shopName).getStringList("items");
        for (String entry : itemList) {
            // strip trailing comments (e.g. "WHEAT:6  # sell 3")
            String clean = entry.contains("#") ? entry.substring(0, entry.indexOf('#')).trim() : entry.trim();
            int colon = clean.lastIndexOf(':');
            if (colon < 1) {
                continue;
            }
            String materialName = clean.substring(0, colon).toUpperCase(Locale.ROOT);
            Material material = Material.matchMaterial(materialName);
            if (material == null) {
                plugin.getLogger().warning("Skipping shop entry '" + entry + "': unknown material.");
                continue;
            }
            long price;
            try {
                price = Long.parseLong(clean.substring(colon + 1).trim());
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Skipping shop entry '" + entry + "': invalid price.");
                continue;
            }
            entries.add(new ShopEntry(new ItemStack(material, 1), price));
        }
        plugin.getLogger().info("Loaded " + entries.size() + " entries for shop " + shopName + ".");
    }

    @Override
    protected void build() {
        slotMap.forEach((slot, entry) -> {
            ItemStack display = new ItemBuilder(entry.item())
                    .addLore("§7Price: §6" + entry.price() + " coins")
                    .addLore("§eClick to buy!")
                    .build();
            setItem(slot, display, event -> purchase((Player) event.getWhoClicked(), entry));
        });
    }

    private void purchase(Player player, ShopEntry entry) {
        if (coinManager.withdraw(player.getUniqueId(), entry.price())) {
            player.getInventory().addItem(entry.item().clone());
            player.sendMessage("§aPurchased §6" + entry.item().getType() + " §afor §6" + entry.price() + " coins§a!");
        } else {
            player.sendMessage("§cYou don't have enough coins!");
        }
    }

    /**
     * A single purchasable shop entry.
     *
     * @param item  the item shown in the menu (also what the player receives)
     * @param price the coin cost to purchase
     */
    public record ShopEntry(ItemStack item, long price) {
        public ShopEntry {
            Objects.requireNonNull(item, "item");
        }
    }
}
