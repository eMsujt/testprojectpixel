package com.skyblock.plugin.menu;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public final class CalendarMenu implements InventoryHolder, Listener {

    private final Plugin plugin;

    // SkyBlock time: 1 day = 20 real minutes, 1 month = 31 days, 1 year = 12 months.
    private static final long REAL_MS_PER_SB_DAY = 20L * 60L * 1000L;
    private static final int DAYS_PER_MONTH = 31;
    private static final int MONTHS_PER_YEAR = 12;

    private static final String[] MONTH_NAMES = {
            "Early Spring", "Spring", "Late Spring",
            "Early Summer", "Summer", "Late Summer",
            "Early Autumn", "Autumn", "Late Autumn",
            "Early Winter", "Winter", "Late Winter"
    };

    private final Inventory inventory;

    public CalendarMenu(Plugin plugin) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(this, 54, "§aSkyBlock Calendar");
        Bukkit.getPluginManager().registerEvents(this, plugin);
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

        long totalDays = System.currentTimeMillis() / REAL_MS_PER_SB_DAY;
        int day = (int) (totalDays % DAYS_PER_MONTH) + 1;
        int month = (int) ((totalDays / DAYS_PER_MONTH) % MONTHS_PER_YEAR);
        int year = (int) (totalDays / (DAYS_PER_MONTH * (long) MONTHS_PER_YEAR)) + 1;

        ItemStack date = makeItem(Material.CLOCK, "§a" + MONTH_NAMES[month] + " " + ordinal(day));
        ItemMeta meta = date.getItemMeta();
        if (meta != null) {
            meta.setLore(Arrays.asList(
                    "§7Day: §a" + day,
                    "§7Month: §a" + MONTH_NAMES[month],
                    "§7Year: §a" + year));
            date.setItemMeta(meta);
        }
        inventory.setItem(22, date);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof CalendarMenu) {
            event.setCancelled(true);
        }
    }

    private String ordinal(int day) {
        if (day >= 11 && day <= 13) {
            return day + "th";
        }
        switch (day % 10) {
            case 1: return day + "st";
            case 2: return day + "nd";
            case 3: return day + "rd";
            default: return day + "th";
        }
    }

    private ItemStack makeItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }
}
