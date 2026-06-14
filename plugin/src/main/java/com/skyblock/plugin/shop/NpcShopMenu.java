package com.skyblock.plugin.shop;

import com.skyblock.economy.CoinManager;
import com.skyblock.plugin.gui.ItemBuilder;
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
 * YAML-driven 54-slot (6-row) chest GUI for a single NPC shop.
 *
 * <p>The shop's stock is read from {@code shops/<shopName>.yml} in the plugin
 * data folder, with the bundled default copied out of the jar on first run. Each
 * top-level key under the {@code items} section defines one entry via its
 * {@code material}, {@code amount} (default 1) and {@code price}. Entries are laid
 * out left-to-right from slot 0 (capped at the 54 available slots), each showing
 * its price as appended lore. Clicking an entry withdraws its price from the
 * clicking player's purse (via {@link CoinManager}) and grants the item; if they
 * can't afford it, the purchase is rejected with a message.</p>
 */
public class NpcShopMenu extends Menu {

    private final CoinManager coinManager;
    private final List<ShopEntry> entries = new ArrayList<>();
    private Map<Integer, ShopEntry> slotMap = Collections.emptyMap();

    /**
     * Loads the named shop using the shared {@link CoinManager} instance.
     *
     * @param plugin   the owning plugin, used for resource extraction and logging
     * @param shopName the shop's name, used as the title and config file name
     */
    public NpcShopMenu(JavaPlugin plugin, String shopName) {
        this(plugin, shopName, CoinManager.getInstance());
    }

    /**
     * Loads the named shop backed by the given {@link CoinManager}.
     *
     * @param plugin      the owning plugin, used for resource extraction and logging
     * @param shopName    the shop's name, used as the title and config file name
     * @param coinManager the coin source charged on purchase
     */
    public NpcShopMenu(JavaPlugin plugin, String shopName, CoinManager coinManager) {
        super("§6" + Objects.requireNonNull(shopName, "shopName"), 6);
        this.coinManager = Objects.requireNonNull(coinManager, "coinManager");
        load(plugin, shopName);
        Map<Integer, ShopEntry> map = new LinkedHashMap<>();
        for (int i = 0; i < entries.size() && i < 54; i++) {
            map.put(i, entries.get(i));
        }
        this.slotMap = Collections.unmodifiableMap(map);
    }

    /**
     * Reads {@code shops/<shopName>.yml} from the plugin data folder, copying the
     * bundled default out of the jar on first run, then parses each defined entry.
     */
    private void load(JavaPlugin plugin, String shopName) {
        String path = "shops/" + shopName + ".yml";
        File file = new File(plugin.getDataFolder(), path);
        if (!file.exists() && plugin.getResource(path) != null) {
            plugin.saveResource(path, false);
        }
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection items = cfg.isConfigurationSection("items")
                ? cfg.getConfigurationSection("items")
                : cfg;
        for (String id : items.getKeys(false)) {
            if (!items.isConfigurationSection(id)) {
                continue;
            }
            ConfigurationSection section = items.getConfigurationSection(id);
            Material material = Material.matchMaterial(
                    section.getString("material", id).toUpperCase(Locale.ROOT));
            if (material == null) {
                continue;
            }
            ItemStack item = new ItemStack(material, Math.max(1, section.getInt("amount", 1)));
            entries.add(new ShopEntry(item, section.getLong("price")));
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
