package com.skyblock.plugin.commands;

import com.skyblock.core.bank.BankManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

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
        UUID id = player.getUniqueId();
        BankManager manager = BankManager.getInstance();
        double balance = manager.getBalance(id);
        BankManager.BankTier tier = manager.getTier(id);
        BankManager.BankType type = manager.getBankType(id);
        player.sendMessage("=== Bank ===");
        player.sendMessage("Balance: " + balance + " coins");
        player.sendMessage("Tier: " + tier.getDisplayName());
        player.sendMessage("Type: " + type.getDisplayName());
    }

    private void handleDeposit(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblock bank deposit <amount>");
            return;
        }
        try {
            double amount = Double.parseDouble(args[1]);
            if (amount <= 0) {
                player.sendMessage("Amount must be greater than 0.");
                return;
            }
            BankManager manager = BankManager.getInstance();
            manager.deposit(player.getUniqueId(), amount);
            player.sendMessage("Deposited " + amount + " coins. Balance: " + manager.getBalance(player.getUniqueId()));
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount: " + args[1]);
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleWithdraw(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblock bank withdraw <amount>");
            return;
        }
        try {
            double amount = Double.parseDouble(args[1]);
            if (amount <= 0) {
                player.sendMessage("Amount must be greater than 0.");
                return;
            }
            BankManager manager = BankManager.getInstance();
            manager.withdraw(player.getUniqueId(), amount);
            player.sendMessage("Withdrew " + amount + " coins. Balance: " + manager.getBalance(player.getUniqueId()));
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount: " + args[1]);
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Bank Commands ===");
        player.sendMessage("/skyblock bank balance           — show your balance, tier, and type");
        player.sendMessage("/skyblock bank deposit <amount>  — deposit coins into your bank");
        player.sendMessage("/skyblock bank withdraw <amount> — withdraw coins from your bank");
    }
}
