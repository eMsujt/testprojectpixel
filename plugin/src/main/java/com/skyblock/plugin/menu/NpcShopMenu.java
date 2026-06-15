package com.skyblock.plugin.menu;

import com.skyblock.economy.CoinManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/** @deprecated Use {@link com.skyblock.core.menu.ShopMenu} instead. */
@Deprecated
public final class NpcShopMenu implements InventoryHolder, Listener {

    private static final int FIRST_ITEM_SLOT = 10;
    private static final int LAST_ITEM_SLOT  = 43;

    private final Inventory inventory;
    private final List<ShopEntry> entries;
    private final Map<Integer, ShopEntry> slotEntries = new HashMap<>();

    public NpcShopMenu(JavaPlugin plugin) {
        this(loadEntries(plugin));
    }

    public NpcShopMenu(List<ShopEntry> entries) {
        this.entries = entries != null ? List.copyOf(entries) : List.of();
        this.inventory = Bukkit.createInventory(this, 54, "§6Shop");
        build();
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void build() {
        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        int slot = FIRST_ITEM_SLOT;
        for (ShopEntry entry : entries) {
            while (slot <= LAST_ITEM_SLOT && (slot % 9 == 0 || slot % 9 == 8)) {
                slot++;
            }
            if (slot > LAST_ITEM_SLOT) {
                break;
            }
            inventory.setItem(slot, buildIcon(entry));
            slotEntries.put(slot, entry);
            slot++;
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof NpcShopMenu)) {
            return;
        }
        event.setCancelled(true);
        int rawSlot = event.getRawSlot();
        if (rawSlot < FIRST_ITEM_SLOT || rawSlot > LAST_ITEM_SLOT) {
            return;
        }
        ShopEntry entry = slotEntries.get(rawSlot);
        if (entry == null) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        purchase(player, entry);
    }

    private static void purchase(Player player, ShopEntry entry) {
        CoinManager coins = CoinManager.getInstance();
        if (coins.getBalance(player.getUniqueId()) < entry.price()) {
            player.sendMessage("§cInsufficient coins!");
            return;
        }
        coins.withdraw(player.getUniqueId(), entry.price());
        player.getInventory().addItem(new ItemStack(entry.material()));
        player.sendMessage("§aPurchased §6" + formatName(entry.material())
                + " §afor §6" + entry.price() + " coins§a!");
    }

    private static ItemStack buildIcon(ShopEntry entry) {
        ItemStack item = new ItemStack(entry.material());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§f" + formatName(entry.material()));
            meta.setLore(List.of(
                    "§7Price: §6" + entry.price() + " coins",
                    "§eClick to buy!"));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static List<ShopEntry> loadEntries(JavaPlugin plugin) {
        Objects.requireNonNull(plugin, "plugin");
        var resource = plugin.getResource("shop_items.yml");
        if (resource == null) {
            return List.of();
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(
                new InputStreamReader(resource, StandardCharsets.UTF_8));
        ConfigurationSection section = cfg.isConfigurationSection("shop-items")
                ? cfg.getConfigurationSection("shop-items")
                : cfg;
        List<ShopEntry> list = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            if (!section.isConfigurationSection(key)) {
                continue;
            }
            Material material = Material.matchMaterial(key.toUpperCase(Locale.ROOT));
            if (material == null) {
                continue;
            }
            int price = section.getConfigurationSection(key).getInt("buy", 0);
            list.add(new ShopEntry(material, price));
        }
        return list;
    }

    private static ItemStack makeItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static String formatName(Material material) {
        String raw = material.name().replace('_', ' ');
        StringBuilder sb = new StringBuilder(raw.length());
        boolean cap = true;
        for (char c : raw.toCharArray()) {
            sb.append(cap ? Character.toUpperCase(c) : Character.toLowerCase(c));
            cap = (c == ' ');
        }
        return sb.toString();
    }
}
