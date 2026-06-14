package com.skyblock.plugin.gui.menus;

import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import com.skyblock.plugin.managers.BankManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.UUID;

/**
 * 54-slot Bank GUI matching Hypixel SkyBlock's Bank interface.
 *
 * Slot layout:
 *   Slot 13 — gold block showing current balance (click to deposit)
 *   Slot 31 — emerald showing deposit prompt (click to deposit)
 *   Slot 29 — redstone showing withdraw prompt (click to withdraw)
 *   Border   — glass pane filler around the edges
 */
public class BankMenu extends Menu {

    private static final int SLOT_BALANCE  = 13;
    private static final int SLOT_DEPOSIT  = 31;
    private static final int SLOT_WITHDRAW = 29;

    private final UUID playerId;

    public BankMenu(UUID playerId) {
        super(ChatColor.GOLD + "Bank", 6);
        this.playerId = playerId;
    }

    @Override
    protected void build() {
        fillBorder();

        double balance = BankManager.getInstance().getBalance(playerId);

        setItem(SLOT_BALANCE, new ItemBuilder(Material.GOLD_BLOCK)
                .displayName(ChatColor.GOLD + "Your Balance")
                .lore(ChatColor.YELLOW + "Balance: " + ChatColor.WHITE + String.format("%.1f coins", balance),
                      "",
                      ChatColor.GRAY + "Click to manage your bank.")
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .build(),
                e -> e.getWhoClicked().sendMessage(
                        ChatColor.GOLD + "Your balance: " + ChatColor.WHITE + String.format("%.1f coins", BankManager.getInstance().getBalance(playerId))));

        setItem(SLOT_DEPOSIT, new ItemBuilder(Material.EMERALD)
                .displayName(ChatColor.GREEN + "Deposit Coins")
                .lore(ChatColor.GRAY + "Click to deposit coins into your bank.")
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .build(),
                e -> e.getWhoClicked().sendMessage(ChatColor.YELLOW + "Use /bank deposit <amount> to deposit coins."));

        setItem(SLOT_WITHDRAW, new ItemBuilder(Material.REDSTONE)
                .displayName(ChatColor.RED + "Withdraw Coins")
                .lore(ChatColor.GRAY + "Click to withdraw coins from your bank.")
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .build(),
                e -> e.getWhoClicked().sendMessage(ChatColor.YELLOW + "Use /bank withdraw <amount> to withdraw coins."));
    }

    private void fillBorder() {
        org.bukkit.inventory.ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName(" ")
                .build();
        // top and bottom rows
        for (int i = 0; i < 9; i++) {
            setItem(i, pane);
            setItem(45 + i, pane);
        }
        // left and right columns (rows 1–4)
        for (int row = 1; row <= 4; row++) {
            setItem(row * 9, pane);
            setItem(row * 9 + 8, pane);
        }
    }

    /** Convenience factory — opens the menu for the given player immediately. */
    public static void openFor(Player player) {
        new BankMenu(player.getUniqueId()).open(player);
    }
}
