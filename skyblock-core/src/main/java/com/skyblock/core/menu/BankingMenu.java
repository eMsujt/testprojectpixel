package com.skyblock.core.menu;

import com.skyblock.core.manager.BankManager;
import com.skyblock.core.manager.BankingManager;
import com.skyblock.core.manager.EconomyManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * 6-row GUI titled {@code §6Bank Account}. Displays the player's purse and
 * bank balance with Deposit All / Withdraw All actions via {@link BankingManager}.
 */
public final class BankingMenu extends AbstractSkyBlockMenu {

    private static final int DEPOSIT_SLOT  = 11;
    private static final int BALANCE_SLOT  = 13;
    private static final int WITHDRAW_SLOT = 15;
    private static final int PURSE_SLOT    = 22;
    private static final int CLOSE_SLOT    = 49;

    public BankingMenu(Player player) {
        super(player, "§6Bank Account", 6);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                setItem(slot, pane);
            }
        }

        UUID uuid = player.getUniqueId();
        BankManager bank = BankManager.getInstance();
        EconomyManager econ = EconomyManager.getInstance();

        long purse = econ.getPurse(uuid);
        double balance = bank.getBalance(uuid);

        setItem(PURSE_SLOT, new ItemBuilder(Material.GOLD_NUGGET)
                .displayName("§6Purse")
                .lore("§7Balance: §6" + String.format("%,.0f", (double) purse) + " Coins")
                .build());

        setItem(BALANCE_SLOT, new ItemBuilder(Material.GOLD_INGOT)
                .displayName("§6Personal Bank")
                .lore("§7Balance: §6" + String.format("%,.0f", balance) + " Coins")
                .build());

        setItem(DEPOSIT_SLOT, new ItemBuilder(Material.EMERALD)
                .displayName("§aDeposit All")
                .lore("§7Move all purse coins into the bank.", "", "§eClick to deposit!")
                .build(),
                e -> {
                    e.setCancelled(true);
                    long p = econ.getPurse(uuid);
                    if (p > 0 && BankingManager.getInstance().deposit(uuid, p)) {
                        player.sendMessage("§aDeposited §6" + String.format("%,.0f", (double) p) + " §acoins into your bank.");
                        new BankingMenu(player).open(player);
                    }
                });

        setItem(WITHDRAW_SLOT, new ItemBuilder(Material.DROPPER)
                .displayName("§eWithdraw All")
                .lore("§7Move all bank coins to your purse.", "", "§eClick to withdraw!")
                .build(),
                e -> {
                    e.setCancelled(true);
                    double b = bank.getBalance(uuid);
                    if (b > 0 && BankingManager.getInstance().withdraw(uuid, (long) b)) {
                        player.sendMessage("§aWithdrew §6" + String.format("%,.0f", b) + " §acoins from your bank.");
                        new BankingMenu(player).open(player);
                    }
                });

        setItem(CLOSE_SLOT, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .lore("§7Click to close.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    player.closeInventory();
                });
    }
}
