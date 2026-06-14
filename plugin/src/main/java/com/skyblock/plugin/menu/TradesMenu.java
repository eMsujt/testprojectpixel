package com.skyblock.plugin.menu;

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

import java.util.List;

public final class TradesMenu implements InventoryHolder, Listener {

    private static final List<Trade> TRADES = List.of(
            new Trade(Material.WHEAT, "§aFarmer John", 32, 40),
            new Trade(Material.COBBLESTONE, "§7Miner Pete", 64, 96),
            new Trade(Material.OAK_LOG, "§2Lumberjack Sam", 48, 60),
            new Trade(Material.COD, "§bFisher Will", 16, 24),
            new Trade(Material.ROTTEN_FLESH, "§cHunter Greg", 80, 50));

    private final Inventory inventory;

    public TradesMenu() {
        this.inventory = Bukkit.createInventory(this, 54, "§eTrades");
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

        int slot = 20;
        for (Trade trade : TRADES) {
            inventory.setItem(slot, buildIcon(trade));
            slot++;
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof TradesMenu) {
            event.setCancelled(true);
        }
    }

    private static ItemStack buildIcon(Trade trade) {
        ItemStack item = new ItemStack(trade.material());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(trade.npc());
            meta.setLore(List.of(
                    "§7Gives: §f" + trade.give() + "x " + trade.material().name(),
                    "§7Receives: §6" + trade.receive() + " coins"));
            item.setItemMeta(meta);
        }
        return item;
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

    private record Trade(Material material, String npc, int give, int receive) {
    }
}
