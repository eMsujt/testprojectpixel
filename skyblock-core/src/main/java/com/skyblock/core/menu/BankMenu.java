package com.skyblock.core.menu;

import com.skyblock.core.manager.BankManager;
import com.skyblock.core.economy.EconomyManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Canonical 54-slot Bank menu. Gray-pane border on all four edges; purse icon
 * (GOLD_NUGGET) at slot 20; bank balance (GOLD_BLOCK) at slot 24; Deposit All
 * (EMERALD) at slot 29; Withdraw All (DROPPER) at slot 33; close barrier at
 * slot 49.
 *
 * <p>All other BankMenu / BankingMenu / BankGui classes in this project are
 * deprecated stubs that delegate here.</p>
 */
public final class BankMenu extends Menu {

    private static final int PURSE_SLOT    = 20;
    private static final int BANK_SLOT     = 24;
    private static final int DEPOSIT_SLOT  = 29;
    private static final int WITHDRAW_SLOT = 33;
    private static final int CLOSE_SLOT    = 49;

    private final UUID playerId;
    private Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> handlers = new HashMap<>();

    public BankMenu(Player player) {
        this(player.getUniqueId());
    }

    public BankMenu(UUID playerId) {
        this.playerId = playerId;
    }

    @Override
    public void open(Player player) {
        handlers.clear();

        EconomyManager econ = EconomyManager.getInstance();
        BankManager bank = BankManager.getInstance();

        inventory = org.bukkit.Bukkit.createInventory(this, 54, "§6Bank Account");

        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        long purse = econ.getPurse(playerId);
        double balance = bank.getBalance(playerId);

        inventory.setItem(PURSE_SLOT, new ItemBuilder(Material.GOLD_NUGGET)
                .displayName("§6Purse")
                .lore("§7Balance: §6" + String.format("%,.0f", (double) purse) + " Coins")
                .build());

        inventory.setItem(BANK_SLOT, new ItemBuilder(Material.GOLD_BLOCK)
                .displayName("§6Bank Account")
                .lore("§7Balance: §6" + String.format("%,.0f", balance) + " Coins")
                .build());

        inventory.setItem(DEPOSIT_SLOT, new ItemBuilder(Material.EMERALD)
                .displayName("§aDeposit All")
                .lore("§7Move all purse coins into the bank.")
                .build());
        handlers.put(DEPOSIT_SLOT, e -> {
            long p = econ.getPurse(playerId);
            if (p > 0) {
                econ.withdraw(playerId, p);
                bank.deposit(playerId, p);
                player.sendMessage("§aDeposited §6" + String.format("%,.0f", (double) p) + " §acoins into your bank.");
                open(player);
            }
        });

        inventory.setItem(WITHDRAW_SLOT, new ItemBuilder(Material.DROPPER)
                .displayName("§eWithdraw All")
                .lore("§7Move all bank coins to your purse.")
                .build());
        handlers.put(WITHDRAW_SLOT, e -> {
            double b = bank.getBalance(playerId);
            if (b > 0) {
                bank.withdraw(playerId, b);
                econ.addPurse(playerId, (long) b);
                player.sendMessage("§aWithdrew §6" + String.format("%,.0f", b) + " §acoins from your bank.");
                open(player);
            }
        });

        inventory.setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .lore("§7Close the bank.")
                .build());
        handlers.put(CLOSE_SLOT, e -> player.closeInventory());

        player.openInventory(inventory);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Consumer<InventoryClickEvent> handler = handlers.get(event.getSlot());
        if (handler != null) {
            handler.accept(event);
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
