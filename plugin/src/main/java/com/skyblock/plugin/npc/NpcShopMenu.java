package com.skyblock.plugin.npc;

import com.skyblock.economy.CoinManager;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Dynamic 54-slot (6-row) chest GUI for a single NPC shop.
 *
 * <p>The shop's stock is read from {@code npcshops.yml} in the plugin data
 * folder, under the {@code shops} section. The shop is matched by its shop-id or
 * by its colour-stripped display title, so a {@link NPCShopListener} can open it
 * keyed by the villager's name. Each {@code "MATERIAL:price"} entry under the
 * shop's {@code items} list is laid out left-to-right from slot 0 (capped at the
 * 54 available slots), showing its price as appended lore. Clicking an entry
 * withdraws its price from the clicking player's purse (via {@link CoinManager})
 * and grants the item; if they can't afford it, the purchase is rejected with a
 * message.</p>
 */
public class NpcShopMenu extends Menu {

    private final CoinManager coinManager;
    private final List<ShopEntry> entries = new ArrayList<>();

    /**
     * Loads the named shop using the shared {@link CoinManager} instance.
     *
     * @param plugin   the owning plugin, used for config loading and logging
     * @param shopName the shop's id or display name, used for matching and the title
     */
    public NpcShopMenu(JavaPlugin plugin, String shopName) {
        this(plugin, shopName, CoinManager.getInstance());
    }

    /**
     * Loads the named shop backed by the given {@link CoinManager}.
     *
     * @param plugin      the owning plugin, used for config loading and logging
     * @param shopName    the shop's id or display name, used for matching and the title
     * @param coinManager the coin source charged on purchase
     */
    public NpcShopMenu(JavaPlugin plugin, String shopName, CoinManager coinManager) {
        super("§6" + Objects.requireNonNull(shopName, "shopName"), 6);
        this.coinManager = Objects.requireNonNull(coinManager, "coinManager");
        load(plugin, shopName);
    }

    /**
     * Reads {@code npcshops.yml} from the plugin data folder, copying the bundled
     * default out of the jar on first run, then parses the matching shop's stock.
     */
    private void load(JavaPlugin plugin, String shopName) {
        File file = new File(plugin.getDataFolder(), "npcshops.yml");
        if (!file.exists() && plugin.getResource("npcshops.yml") != null) {
            plugin.saveResource("npcshops.yml", false);
        }
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = cfg.isConfigurationSection("shops")
                ? cfg.getConfigurationSection("shops")
                : cfg;
        ConfigurationSection shop = findShop(root, shopName);
        if (shop == null) {
            return;
        }
        for (String raw : shop.getStringList("items")) {
            int sep = raw.lastIndexOf(':');
            if (sep < 0) {
                continue;
            }
            Material material = Material.matchMaterial(raw.substring(0, sep).trim());
            if (material == null) {
                continue;
            }
            int price;
            try {
                price = Integer.parseInt(raw.substring(sep + 1).trim());
            } catch (NumberFormatException e) {
                continue;
            }
            entries.add(new ShopEntry(material, price));
        }
        plugin.getLogger().info("Loaded " + entries.size() + " entries for shop " + shopName + ".");
    }

    /** Finds the shop section matching {@code shopName} by id or colour-stripped title. */
    private ConfigurationSection findShop(ConfigurationSection root, String shopName) {
        if (root.isConfigurationSection(shopName)) {
            return root.getConfigurationSection(shopName);
        }
        for (String id : root.getKeys(false)) {
            ConfigurationSection section = root.getConfigurationSection(id);
            if (section == null) {
                continue;
            }
            String title = ChatColor.stripColor(section.getString("title", id));
            if (title != null && title.equalsIgnoreCase(shopName)) {
                return section;
            }
        }
        return null;
    }

    @Override
    protected void build() {
        for (int i = 0; i < entries.size() && i < 54; i++) {
            ShopEntry entry = entries.get(i);
            ItemStack display = new ItemBuilder(entry.material())
                    .addLore("§7Price: §6" + entry.price() + " coins")
                    .addLore("§eClick to buy!")
                    .build();
            setItem(i, display, event -> purchase((Player) event.getWhoClicked(), entry));
        }
    }

    private void purchase(Player player, ShopEntry entry) {
        if (coinManager.withdraw(player.getUniqueId(), entry.price())) {
            player.getInventory().addItem(new ItemStack(entry.material()));
            player.sendMessage("§aPurchased §6" + entry.material() + " §afor §6" + entry.price() + " coins§a!");
        } else {
            player.sendMessage("§cYou don't have enough coins!");
        }
    }

    /**
     * A single purchasable shop entry.
     *
     * @param material the item shown in the menu (also what the player receives)
     * @param price    the coin cost to purchase
     */
    public record ShopEntry(Material material, int price) {
        public ShopEntry {
            Objects.requireNonNull(material, "material");
        }
    }
}
