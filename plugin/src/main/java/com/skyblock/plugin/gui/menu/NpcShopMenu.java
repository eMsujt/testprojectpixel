package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.manager.EconomyManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A YAML-driven NPC shop menu.
 *
 * <p>A 54-slot (6-row) menu whose title and wares come from a named shop in the
 * bundled {@code npc_shops.yml}, framed by a gray glass border. Each item the
 * shop sells is rendered across the inner slots; clicking one buys a single
 * unit, debiting the player's purse via {@link EconomyManager}. Matches the
 * Hypixel NPC shop layout.</p>
 */
public class NpcShopMenu extends Menu {

    /** A single buyable item: its material and its coin buy price. */
    private record ShopItem(Material material, int price) {
    }

    /** Centred content slots across the middle rows, one per item. */
    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    private final List<ShopItem> items;

    /**
     * Opens the named shop, reading its title and items from {@code npc_shops.yml}.
     *
     * @param plugin the owning plugin, used for resource extraction
     * @param shopId the shop-id key under the {@code shops} section
     */
    public NpcShopMenu(JavaPlugin plugin, String shopId) {
        this(load(plugin, shopId));
    }

    private NpcShopMenu(ShopDefinition shop) {
        super(shop.title, 6);
        this.items = shop.items;
    }

    @Override
    protected void build() {
        fillBorder();

        int count = Math.min(items.size(), SLOTS.length);
        for (int i = 0; i < count; i++) {
            ShopItem item = items.get(i);
            setItem(SLOTS[i], new ItemBuilder(item.material())
                            .displayName("§f" + formatName(item.material()))
                            .lore("§7Price: §6" + item.price() + " coins", "§eClick to buy!")
                            .build(),
                    event -> {
                        event.setCancelled(true);
                        HumanEntity who = event.getWhoClicked();
                        if (EconomyManager.getInstance().removeCoins(who.getUniqueId(), item.price())) {
                            who.getInventory().addItem(new ItemStack(item.material()));
                            who.sendMessage("§aPurchased §6" + formatName(item.material())
                                    + " §afor §6" + item.price() + " coins§a!");
                        } else {
                            who.sendMessage("§cYou don't have enough coins!");
                        }
                    });
        }
    }

    /** Fills the menu's outer edge with gray glass panes, matching Hypixel. */
    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                setItem(slot, pane);
            }
        }
    }

    /**
     * Copies the bundled {@code npc_shops.yml} out of the jar on first run, then
     * parses the requested shop's title and items.
     */
    private static ShopDefinition load(JavaPlugin plugin, String shopId) {
        File file = new File(plugin.getDataFolder(), "npc_shops.yml");
        if (!file.exists() && plugin.getResource("npc_shops.yml") != null) {
            plugin.saveResource("npc_shops.yml", false);
        }
        String title = "§aShop";
        List<ShopItem> items = new ArrayList<>();
        if (!file.exists()) {
            return new ShopDefinition(title, items);
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection shop = cfg.getConfigurationSection("shops." + shopId);
        if (shop == null) {
            return new ShopDefinition(title, items);
        }
        title = shop.getString("title", title);
        for (String entry : shop.getStringList("items")) {
            int colon = entry.lastIndexOf(':');
            if (colon < 0) {
                continue;
            }
            Material material = Material.matchMaterial(
                    entry.substring(0, colon).toUpperCase(Locale.ROOT));
            if (material == null) {
                continue;
            }
            int price;
            try {
                price = Integer.parseInt(entry.substring(colon + 1).trim());
            } catch (NumberFormatException ex) {
                continue;
            }
            items.add(new ShopItem(material, price));
        }
        return new ShopDefinition(title, items);
    }

    /** Converts a material name to title case, e.g. {@code IRON_INGOT} → "Iron Ingot". */
    private static String formatName(Material material) {
        String raw = material.name().replace('_', ' ').toLowerCase(Locale.ROOT);
        StringBuilder sb = new StringBuilder(raw.length());
        boolean cap = true;
        for (char c : raw.toCharArray()) {
            sb.append(cap ? Character.toUpperCase(c) : c);
            cap = (c == ' ');
        }
        return sb.toString();
    }

    /** A parsed shop: its inventory title and the items it sells. */
    private record ShopDefinition(String title, List<ShopItem> items) {
    }
}
