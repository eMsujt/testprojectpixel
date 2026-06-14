package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.BankManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class BankCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleBalance(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "balance"  -> handleBalance(player);
            case "deposit"  -> handleDeposit(player, args);
            case "withdraw" -> handleWithdraw(player, args);
            default         -> sendHelp(player);
        }
        return true;
    }

    private void handleBalance(Player player) {
        double balance = BankManager.getInstance().getBalance(player.getUniqueId());
        player.sendMessage(String.format("Your bank balance: %.1f coins", balance));
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
            player.sendMessage("Invalid amount: " + args[1]);
            return;
        }
        if (amount <= 0) {
            player.sendMessage("Deposit amount must be positive.");
            return;
        }
        BankManager manager = BankManager.getInstance();
        manager.deposit(player.getUniqueId(), amount);
        player.sendMessage(String.format("Deposited %.1f coins. New balance: %.1f coins",
                amount, manager.getBalance(player.getUniqueId())));
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
            player.sendMessage("Invalid amount: " + args[1]);
            return;
        }
        if (amount <= 0) {
            player.sendMessage("Withdrawal amount must be positive.");
            return;
        }
        BankManager manager = BankManager.getInstance();
        double current = manager.getBalance(player.getUniqueId());
        if (amount > current) {
            player.sendMessage(String.format("Insufficient funds. Balance: %.1f coins", current));
            return;
        }
        manager.withdraw(player.getUniqueId(), amount);
        player.sendMessage(String.format("Withdrew %.1f coins. New balance: %.1f coins",
                amount, manager.getBalance(player.getUniqueId())));
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Bank Commands ===");
        player.sendMessage("/bank                   — show your balance");
        player.sendMessage("/bank balance           — show your balance");
        player.sendMessage("/bank deposit <amount>  — deposit coins");
        player.sendMessage("/bank withdraw <amount> — withdraw coins");
    }
}
