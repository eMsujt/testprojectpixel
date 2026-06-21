package com.skyblock.core.menu;

import com.skyblock.core.coop.CoopManager;
import com.skyblock.core.manager.BankManager;
import com.skyblock.core.manager.BankManager.BankTier;
import com.skyblock.core.manager.EconomyManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * 4-row chest GUI titled '§6Bank Account'. Shows Personal and Co-op bank tabs,
 * purse balance, current bank balance, and Deposit All / Withdraw All actions.
 */
public final class BankMenu extends AbstractSkyBlockMenu {

    private static final String TITLE         = "§6Bank Account";
    private static final int    HEADER_SLOT   = 4;
    private static final int    PERSONAL_SLOT = 10;
    private static final int    COOP_SLOT     = 11;
    private static final int    PURSE_SLOT    = 12;
    private static final int    BALANCE_SLOT  = 13;
    private static final int    DEPOSIT_SLOT  = 15;
    private static final int    WITHDRAW_SLOT = 16;
    private static final int    CLOSE_SLOT    = 31;

    private boolean showingCoop;

    public BankMenu(Player player) {
        super(player, TITLE, 4);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 9; slot++)  setItem(slot, pane);
        for (int slot = 27; slot < 36; slot++) setItem(slot, pane);

        UUID uuid = player.getUniqueId();
        BankManager bank = BankManager.getInstance();
        EconomyManager econ = EconomyManager.getInstance();
        CoopManager coop = CoopManager.getInstance();

        UUID coopOwner = coop.getOwner(uuid);
        String coopKey = coopOwner != null ? coopOwner.toString() : null;

        double balance = showingCoop
                ? (coopKey != null ? bank.getCoopBalance(coopKey) : 0.0)
                : bank.getBalance(uuid);
        long purse = econ.getPurse(uuid);
        BankTier tier = bank.getTier(uuid);

        setItem(HEADER_SLOT, new ItemBuilder(Material.GOLD_INGOT)
                .displayName("§6Bank Account")
                .lore("§7Tier: §e" + tier.getDisplayName())
                .build());

        setItem(PERSONAL_SLOT, new ItemBuilder(Material.GOLD_BLOCK)
                .displayName((showingCoop ? "§7" : "§6") + "Personal Bank")
                .lore(showingCoop ? "§7Click to view." : "§eViewing.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    if (showingCoop) {
                        showingCoop = false;
                        open(player);
                    }
                });

        setItem(COOP_SLOT, new ItemBuilder(Material.EMERALD_BLOCK)
                .displayName((showingCoop ? "§6" : "§7") + "Co-op Bank")
                .lore(showingCoop ? "§eViewing." : "§7Click to view.")
                .build(),
                e -> {
                    e.setCancelled(true);
                    if (!showingCoop) {
                        showingCoop = true;
                        open(player);
                    }
                });

        setItem(PURSE_SLOT, new ItemBuilder(Material.GOLD_NUGGET)
                .displayName("§6Purse")
                .lore("§7Balance: §6" + String.format("%,.0f", (double) purse) + " Coins")
                .build());

        setItem(BALANCE_SLOT, new ItemBuilder(Material.GOLD_INGOT)
                .displayName(showingCoop ? "§6Co-op Bank" : "§6Personal Bank")
                .lore("§7Balance: §6" + String.format("%,.0f", balance) + " Coins")
                .build());

        setItem(DEPOSIT_SLOT, new ItemBuilder(Material.EMERALD)
                .displayName("§aDeposit All")
                .lore("§7Move all purse coins into the bank.", "", "§eClick to deposit!")
                .build(),
                e -> {
                    e.setCancelled(true);
                    if (showingCoop && coopKey == null) {
                        player.sendMessage("§cYou are not in a co-op.");
                        return;
                    }
                    long p = econ.getPurse(uuid);
                    if (p > 0) {
                        econ.withdraw(uuid, p);
                        if (showingCoop) {
                            bank.depositCoop(coopKey, p);
                        } else {
                            bank.deposit(uuid, p);
                        }
                        player.sendMessage("§aDeposited §6" + String.format("%,.0f", (double) p) + " §acoins into your bank.");
                        open(player);
                    }
                });

        setItem(WITHDRAW_SLOT, new ItemBuilder(Material.DROPPER)
                .displayName("§eWithdraw All")
                .lore("§7Move all bank coins to your purse.", "", "§eClick to withdraw!")
                .build(),
                e -> {
                    e.setCancelled(true);
                    if (showingCoop && coopKey == null) {
                        player.sendMessage("§cYou are not in a co-op.");
                        return;
                    }
                    double b = showingCoop
                            ? bank.getCoopBalance(coopKey)
                            : bank.getBalance(uuid);
                    if (b > 0) {
                        if (showingCoop) {
                            bank.withdrawCoop(coopKey, b);
                        } else {
                            bank.withdraw(uuid, b);
                        }
                        econ.addPurse(uuid, (long) b);
                        player.sendMessage("§aWithdrew §6" + String.format("%,.0f", b) + " §acoins from your bank.");
                        open(player);
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
