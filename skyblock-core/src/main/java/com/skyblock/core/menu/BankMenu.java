package com.skyblock.core.menu;

import com.skyblock.core.manager.BankManager;
import com.skyblock.core.manager.BankManager.BankTier;
import com.skyblock.core.manager.EconomyManager;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The "Personal Bank" menu, opened from the SkyBlock Menu. Laid out 1:1 with
 * Hypixel: a 4-row chest with Deposit Coins (Chest, slot 11), Withdraw Coins
 * (Dropper, 13), Recent transactions (Map, 15), a Go Back arrow (30), Close
 * (31), Information (Redstone Torch, 32) and Bank Upgrades (Block of Gold, 35).
 * Deposit/withdraw move the player's whole purse/balance.
 */
public final class BankMenu extends AbstractSkyBlockMenu {

    private static final String TITLE = "§6Personal Bank";

    public BankMenu(Player player) {
        super(player, TITLE, 4);
    }

    @Override
    protected void populate() {
        ItemStack pane = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 0; slot < 36; slot++) setItem(slot, pane);

        UUID uuid = player.getUniqueId();
        BankManager bank = BankManager.getInstance();
        EconomyManager econ = EconomyManager.getInstance();

        double balance = bank.getBalance(uuid);
        long purse = econ.getPurse(uuid);
        BankTier tier = bank.getTier(uuid);
        String bal = String.format("%,.0f", balance);

        setItem(11, new ItemBuilder(Material.CHEST)
                .displayName("§aDeposit Coins")
                .lore(
                        "§7Current balance: §6" + bal,
                        "§7Purse: §6" + String.format("%,d", purse),
                        "",
                        "§7Store coins in the bank to keep",
                        "§7them safe and earn interest.",
                        "",
                        "§eClick to deposit your purse!")
                .build(),
                e -> {
                    e.setCancelled(true);
                    long p = econ.getPurse(uuid);
                    if (p > 0) {
                        econ.withdraw(uuid, p);
                        bank.deposit(uuid, p);
                        player.sendMessage("§aDeposited §6" + String.format("%,d", p) + " §acoins into your bank.");
                    } else {
                        player.sendMessage("§cYour purse is empty.");
                    }
                    open(player);
                });

        setItem(13, new ItemBuilder(Material.DROPPER)
                .displayName("§aWithdraw Coins")
                .lore(
                        "§7Current balance: §6" + bal,
                        "",
                        "§7Take your coins out of the bank",
                        "§7in order to spend them.",
                        "",
                        "§eClick to withdraw all!")
                .build(),
                e -> {
                    e.setCancelled(true);
                    double b = bank.getBalance(uuid);
                    if (b > 0) {
                        bank.withdraw(uuid, b);
                        econ.addPurse(uuid, (long) b);
                        player.sendMessage("§aWithdrew §6" + String.format("%,.0f", b) + " §acoins from your bank.");
                    } else {
                        player.sendMessage("§cYour bank is empty.");
                    }
                    open(player);
                });

        setItem(15, new ItemBuilder(Material.MAP)
                .displayName("§aRecent transactions")
                .lore("§7Your most recent bank", "§7deposits and withdrawals.")
                .build(), e -> e.setCancelled(true));

        setItem(30, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To SkyBlock Menu")
                .build(),
                e -> { e.setCancelled(true); new SkyBlockMenu(player).open(player); });

        setItem(31, new ItemBuilder(Material.BARRIER)
                .displayName("§cClose")
                .build(),
                e -> { e.setCancelled(true); player.closeInventory(); });

        setItem(32, new ItemBuilder(Material.REDSTONE_TORCH)
                .displayName("§aInformation")
                .lore(
                        "§7Keep your coins safe in the bank!",
                        "§7You earn §b" + trimRate(tier.getInterestRate()) + "%§7 interest",
                        "§7each season on your balance.",
                        "",
                        "§7Account: §a" + tier.getDisplayName())
                .build(), e -> e.setCancelled(true));

        setItem(35, new ItemBuilder(Material.GOLD_BLOCK)
                .displayName("§6Bank Upgrades")
                .lore(
                        "§7Current account: §a" + tier.getDisplayName(),
                        "§7Interest cap: §6" + String.format("%,.0f", tier.getInterestCap()),
                        "",
                        "§7Upgrade your account to store",
                        "§7more coins and earn more interest.")
                .build(), e -> e.setCancelled(true));
    }

    private static String trimRate(double v) {
        return v == Math.floor(v) ? Long.toString((long) v) : Double.toString(v);
    }
}
