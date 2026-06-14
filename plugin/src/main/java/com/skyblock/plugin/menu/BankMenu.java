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
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

public final class BankMenu implements InventoryHolder, Listener {

    private static final String TITLE = "§6Bank Account";
    private static final int SIZE = 27;

    /** Slot showing the account holder's player skull. */
    private static final int SKULL_SLOT = 1;
    /** Slot showing the bank/purse balance summary. */
    private static final int BALANCE_SLOT = 13;
    /** Slot for the Deposit All button. */
    private static final int DEPOSIT_SLOT = 11;
    /** Slot for the Withdraw All button. */
    private static final int WITHDRAW_SLOT = 15;
    /** Slot for the close button. */
    private static final int CLOSE_SLOT = 22;

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
            if (slot < 9 || slot >= SIZE - 9 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        BankManager bm = BankManager.getInstance();
        long bank = bm.getBank(player.getUniqueId());
        long purse = bm.getPurse(player.getUniqueId());

        inventory.setItem(SKULL_SLOT, makeSkull(player, "§a" + player.getName(),
                List.of("§7Bank balance: §6" + bank + " coins")));

        inventory.setItem(BALANCE_SLOT, makeItem(Material.GOLD_INGOT, "§aBank & Purse",
                Arrays.asList("§7Balance: §6" + bank + " coins",
                              "§7Purse: §6" + purse + " coins")));

        inventory.setItem(DEPOSIT_SLOT, makeItem(Material.EMERALD, "§aDeposit All",
                List.of("§7Move all purse coins into the bank.")));

        inventory.setItem(WITHDRAW_SLOT, makeItem(Material.GOLD_NUGGET, "§cWithdraw All",
                List.of("§7Move all bank coins to your purse.")));

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
        } else if (slot == WITHDRAW_SLOT) {
            long bank = bm.getBank(player.getUniqueId());
            if (bank > 0 && bm.withdraw(player.getUniqueId(), bank)) {
                player.sendMessage("§aWithdrew §6" + bank + " §acoins from your bank.");
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

    private static ItemStack makeSkull(Player player, String name, List<String> lore) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(player);
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
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
