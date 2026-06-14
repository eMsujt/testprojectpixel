package com.skyblock.plugin.menu;

import com.skyblock.plugin.economy.BankManager;
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

import java.util.Arrays;
import java.util.List;

public final class BankMenu implements InventoryHolder, Listener {

    private static final String TITLE = "§6Bank";
    private static final int SIZE = 27;

    private static final int PURSE_SLOT   = 11;
    private static final int BANK_SLOT    = 13;
    private static final int DEPOSIT_SLOT = 15;
    private static final int CLOSE_SLOT   = 22;

    private final Inventory inventory;

    /** No-arg constructor for global listener registration only — never open this instance. */
    BankMenu() {
        this.inventory = Bukkit.createInventory(this, SIZE, TITLE);
    }

    public BankMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, SIZE, TITLE);
        build(player);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void build(Player player) {
        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r", null);
        for (int slot = 0; slot < SIZE; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 18 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        BankManager bm = BankManager.getInstance();
        long bank  = bm.getBank(player.getUniqueId());
        long purse = bm.getPurse(player.getUniqueId());

        inventory.setItem(PURSE_SLOT, makeItem(Material.GOLD_INGOT, "§6Purse",
                List.of("§7Balance: §6" + purse + " coins")));

        inventory.setItem(BANK_SLOT, makeItem(Material.GOLD_BLOCK, "§6Bank Account",
                Arrays.asList("§7Balance: §6" + bank + " coins",
                              "§7Click Deposit to move purse coins here.")));

        inventory.setItem(DEPOSIT_SLOT, makeItem(Material.EMERALD, "§aDeposit All",
                List.of("§7Move all purse coins into the bank.")));

        inventory.setItem(CLOSE_SLOT, makeItem(Material.BARRIER, "§cClose", null));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BankMenu)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        int slot = event.getRawSlot();
        BankManager bm = BankManager.getInstance();

        if (slot == DEPOSIT_SLOT) {
            long purse = bm.getPurse(player.getUniqueId());
            if (purse > 0 && bm.deposit(player.getUniqueId(), purse)) {
                player.sendMessage("§aDeposited §6" + purse + " §acoins into your bank.");
                refresh(player);
            }
        } else if (slot == CLOSE_SLOT) {
            player.closeInventory();
        }
    }

    private void refresh(Player player) {
        inventory.clear();
        build(player);
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
