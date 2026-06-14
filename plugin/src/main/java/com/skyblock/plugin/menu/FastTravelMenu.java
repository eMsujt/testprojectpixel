package com.skyblock.plugin.menu;

import com.skyblock.plugin.islands.IslandManager;
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

import java.util.List;

public final class FastTravelMenu implements InventoryHolder, Listener {

    private static final String TITLE = "§bFast Travel";
    private static final int SIZE = 54;

    private static final int HUB_SLOT = 19;
    private static final int ISLAND_SLOT = 25;

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
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        inventory.setItem(HUB_SLOT, makeItem(Material.NETHER_STAR, "§bHub",
                List.of("§7Teleport to the SkyBlock Hub.", "§7", "§eClick to warp!")));

        inventory.setItem(ISLAND_SLOT, makeItem(Material.GRASS_BLOCK, "§aPrivate Island",
                List.of("§7Teleport to your Private Island.", "§7", "§eClick to warp!")));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof FastTravelMenu)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        int slot = event.getRawSlot();

        if (slot == HUB_SLOT) {
            player.closeInventory();
            World hub = Bukkit.getWorlds().get(0);
            player.teleport(hub.getSpawnLocation());
            player.sendMessage("§aTeleported to the Hub!");
        } else if (slot == ISLAND_SLOT) {
            player.closeInventory();
            IslandManager.IslandData island = IslandManager.getInstance().getIsland(player.getUniqueId());
            if (island == null) {
                player.sendMessage("§cYou do not have a Private Island.");
                return;
            }
            World world = Bukkit.getWorld(island.worldName());
            if (world == null) {
                player.sendMessage("§cYour island world is not loaded.");
                return;
            }
            player.teleport(new Location(world, island.spawnX(), island.spawnY(), island.spawnZ()));
            player.sendMessage("§aTeleported to your Private Island!");
        }
    }

    private static ItemStack makeItem(Material material, String name, List<String> lore) {
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
