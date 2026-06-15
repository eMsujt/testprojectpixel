package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public final class FastTravelMenu implements InventoryHolder, Listener {

    private static final String TITLE = "§bFast Travel";
    private static final int SIZE = 54;

    private static final Island[] ISLANDS = {
        new Island(10, Material.COMPASS,     "§bHub",                  "hub",             "§7Travel to the Hub."),
        new Island(11, Material.HAY_BLOCK,   "§aThe Farming Islands",  "farming_islands", "§7Travel to The Farming Islands."),
        new Island(12, Material.OAK_SAPLING, "§aThe Park",             "the_park",        "§7Travel to The Park."),
        new Island(13, Material.COBWEB,      "§cSpider's Den",          "spiders_den",     "§7Travel to the Spider's Den."),
        new Island(14, Material.END_STONE,   "§5The End",              "the_end",         "§7Travel to The End."),
        new Island(15, Material.NETHERRACK,  "§cCrimson Isle",         "crimson_isle",    "§7Travel to the Crimson Isle."),
        new Island(16, Material.GOLD_ORE,    "§6Gold Mine",            "gold_mine",       "§7Travel to the Gold Mine."),
    };

    private record Island(int slot, Material material, String displayName, String worldName, String lore) {}

    private final Inventory inventory;

    public FastTravelMenu() {
        this.inventory = Bukkit.createInventory(this, SIZE, TITLE);
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
        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r", null);
        for (int slot = 0; slot < SIZE; slot++) {
            int row = slot / 9;
            int col = slot % 9;
            if (row == 0 || row == 5 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        for (Island island : ISLANDS) {
            inventory.setItem(island.slot(), makeItem(island.material(), island.displayName(),
                    Collections.singletonList(island.lore())));
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof FastTravelMenu)) {
            return;
        }
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        int slot = event.getRawSlot();
        for (Island island : ISLANDS) {
            if (island.slot() == slot) {
                teleport(player, island);
                return;
            }
        }
    }

    private void teleport(Player player, Island island) {
        World world = Bukkit.getWorld(island.worldName());
        if (world != null) {
            Location spawn = world.getSpawnLocation();
            player.teleport(spawn);
            player.sendMessage("§aTeleported to " + island.displayName() + "§a!");
        } else {
            player.sendMessage("§c" + island.displayName() + " §cis not available.");
        }
        player.closeInventory();
    }

    private static ItemStack makeItem(Material material, String name, java.util.List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) {
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
