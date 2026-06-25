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
 * The "Personal Bank Account" menu, opened from the SkyBlock Menu. Laid out and
 * worded 1:1 with Hypixel's Bank GUI (verbatim tooltip lore from the wiki's
 * Bank/UI page): Deposit Coins (Chest, slot 11), Withdraw Coins (Dropper, 13),
 * Recent transactions (Map, 15), Information (Redstone Torch, 32) and Bank
 * Upgrades (Block of Gold, 35), plus Go Back (30) and Close (31).
 */
public final class BankMenu extends AbstractSkyBlockMenu {

    private static final String TITLE = "§6Personal Bank Account";

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
        BankTier tier = bank.getTier(uuid);
        String bal = String.format("%,.0f", balance);
        String cap = String.format("%,.0f", tier.getInterestCap());
        String rate = trimRate(tier.getInterestRate());

        setItem(11, new ItemBuilder(Material.CHEST)
                .displayName("§aDeposit Coins")
                .lore(
                        "§7Current balance: §6" + bal,
                        "",
                        "§7Store coins in the bank to keep",
                        "§7them safe while you go on",
                        "§7adventures!",
                        "",
                        "§7You will earn §b" + rate + "% §7interest every",
                        "§7season for your first §610 million",
                        "§7banked coins.",
                        "",
                        "§eClick to make a deposit!")
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
                        "§eClick to withdraw coins!")
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
                .lore("§7There are no recent", "§7transactions!")
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
                        "§7You lose half the coins in your",
                        "§7purse when dying in combat.",
                        "",
                        "§7Balance limit: §6" + cap,
                        "",
                        "§7The banker rewards you with",
                        "§b" + rate + "% §7interest each season for the",
                        "§7coins in your bank balance.",
                        "",
                        "§7Account: §a" + tier.getDisplayName())
                .build(), e -> e.setCancelled(true));

        setItem(35, new ItemBuilder(Material.GOLD_BLOCK)
                .displayName("§6Bank Upgrades")
                .lore(
                        "§7Are you so rich that you can't",
                        "§7even store your coins?",
                        "",
                        "§7Current account: §a" + tier.getDisplayName(),
                        "§7Bank limit: §6" + cap,
                        "",
                        "§eClick to view upgrades!")
                .build(), e -> e.setCancelled(true));
    }

    private static String trimRate(double v) {
        return v == Math.floor(v) ? Long.toString((long) v) : Double.toString(v);
    }
}
