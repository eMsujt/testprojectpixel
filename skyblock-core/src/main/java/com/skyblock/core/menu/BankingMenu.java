package com.skyblock.core.menu;

import com.skyblock.core.manager.BankManager;
import com.skyblock.core.manager.EconomyManager;
import com.skyblock.core.util.SkyblockUtils;
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
 * 54-slot (6-row) personal Banking menu.
 *
 * <p>Gray-pane border on all four edges; Deposit All (EMERALD) at
 * {@link #DEPOSIT_SLOT}, the current balance (GOLD_INGOT) at
 * {@link #BALANCE_SLOT}, and Withdraw All (DROPPER) at {@link #WITHDRAW_SLOT}
 * move coins between the purse and the player's bank via {@link BankManager}.
 * Close barrier at slot 49.</p>
 */
public final class BankingMenu extends Menu {

    public static final int DEPOSIT_SLOT  = 11;
    public static final int BALANCE_SLOT  = 13;
    public static final int WITHDRAW_SLOT = 15;
    private static final int PURSE_SLOT   = 22;
    private static final int CLOSE_SLOT   = 49;

    private final UUID playerId;
    private Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> handlers = new HashMap<>();

    public BankingMenu(Player player) {
        this(player.getUniqueId());
    }

    public BankingMenu(UUID playerId) {
        super("§6Banking", 6);
        this.playerId = playerId;
    }

    /** Unused: this menu manages its own inventory via {@link #open(Player)}. */
    @Override
    protected void build() {
    }

    @Override
    public void open(Player player) {
        handlers.clear();

        EconomyManager econ = EconomyManager.getInstance();
        BankManager bank = BankManager.getInstance();

        inventory = org.bukkit.Bukkit.createInventory(this, 54, "§6Banking");

        ItemStack pane = SkyblockUtils.buildItem(Material.YELLOW_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        long purse = econ.getPurse(playerId);
        double balance = bank.getBalance(playerId);

        inventory.setItem(PURSE_SLOT, SkyblockUtils.buildItem(Material.GOLD_NUGGET,
                "§6Purse",
                "§7Balance: §6" + String.format("%,.0f", (double) purse) + " Coins"));

        inventory.setItem(BALANCE_SLOT, SkyblockUtils.buildItem(Material.GOLD_INGOT,
                "§6Personal Bank",
                "§7Balance: §6" + String.format("%,.0f", balance) + " Coins"));

        inventory.setItem(DEPOSIT_SLOT, SkyblockUtils.buildItem(Material.EMERALD,
                "§aDeposit All",
                "§7Move all purse coins into the bank."));
        handlers.put(DEPOSIT_SLOT, e -> {
            long p = econ.getPurse(playerId);
            if (p > 0) {
                econ.withdraw(playerId, p);
                bank.deposit(playerId, p);
                player.sendMessage("§aDeposited §6" + String.format("%,.0f", (double) p) + " §acoins into your bank.");
                open(player);
            }
        });

        inventory.setItem(WITHDRAW_SLOT, SkyblockUtils.buildItem(Material.DROPPER,
                "§eWithdraw All",
                "§7Move all bank coins to your purse."));
        handlers.put(WITHDRAW_SLOT, e -> {
            double b = bank.getBalance(playerId);
            if (b > 0) {
                bank.withdraw(playerId, b);
                econ.addPurse(playerId, (long) b);
                player.sendMessage("§aWithdrew §6" + String.format("%,.0f", b) + " §acoins from your bank.");
                open(player);
            }
        });

        inventory.setItem(CLOSE_SLOT, SkyblockUtils.buildItem(Material.BARRIER,
                "§cClose",
                "§7Close the bank."));
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
