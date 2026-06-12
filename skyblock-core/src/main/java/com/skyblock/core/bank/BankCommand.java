package com.skyblock.core.bank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Handles the {@code /bank} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /bank balance}           — show your current balance</li>
 *   <li>{@code /bank deposit <amount>}  — deposit coins</li>
 *   <li>{@code /bank withdraw <amount>} — withdraw coins</li>
 * </ul>
 * </p>
 */
public final class BankCommand implements TabExecutor {

    private final BankManager bankManager;

    public BankCommand(BankManager bankManager) {
        this.bankManager = bankManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "balance" -> handleBalance(player);
            case "deposit"  -> handleDeposit(player, args);
            case "withdraw" -> handleWithdraw(player, args);
            default -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Arrays.asList("balance", "deposit", "withdraw").stream()
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        return Collections.emptyList();
    }

    private void handleBalance(Player player) {
        double balance = bankManager.getBalance(player.getUniqueId());
        player.sendMessage("Your bank balance: " + balance + " coins");
    }

    private void handleDeposit(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /bank deposit <amount>");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount.");
            return;
        }
        try {
            bankManager.deposit(player.getUniqueId(), amount);
            player.sendMessage("Deposited " + amount + " coins. New balance: " + bankManager.getBalance(player.getUniqueId()));
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleWithdraw(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /bank withdraw <amount>");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount.");
            return;
        }
        try {
            bankManager.withdraw(player.getUniqueId(), amount);
            player.sendMessage("Withdrew " + amount + " coins. New balance: " + bankManager.getBalance(player.getUniqueId()));
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Bank Commands ===");
        player.sendMessage("/bank balance — view your balance");
        player.sendMessage("/bank deposit <amount> — deposit coins");
        player.sendMessage("/bank withdraw <amount> — withdraw coins");
    }
}
