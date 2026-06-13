package com.skyblock.core.bank;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class BankCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("balance", "deposit", "withdraw");

    private final BankManager manager;

    public BankCommand(BankManager manager) {
        this.manager = manager;
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
            case "balance"  -> handleBalance(player);
            case "deposit"  -> handleDeposit(player, args);
            case "withdraw" -> handleWithdraw(player, args);
            default         -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleBalance(Player player) {
        double bal = manager.getBalance(player.getUniqueId());
        player.sendMessage("Bank balance: " + String.format("%.2f", bal));
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
        try {
            manager.deposit(player.getUniqueId(), amount);
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
            return;
        }
        player.sendMessage("Deposited " + String.format("%.2f", amount) + ". Balance: " + String.format("%.2f", manager.getBalance(player.getUniqueId())));
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
        boolean success;
        try {
            success = manager.withdraw(player.getUniqueId(), amount);
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
            return;
        }
        if (!success) {
            player.sendMessage("Insufficient funds.");
            return;
        }
        player.sendMessage("Withdrew " + String.format("%.2f", amount) + ". Balance: " + String.format("%.2f", manager.getBalance(player.getUniqueId())));
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Bank Commands ===");
        player.sendMessage("/bank balance — view your bank balance");
        player.sendMessage("/bank deposit <amount> — deposit coins into your bank");
        player.sendMessage("/bank withdraw <amount> — withdraw coins from your bank");
    }
}
