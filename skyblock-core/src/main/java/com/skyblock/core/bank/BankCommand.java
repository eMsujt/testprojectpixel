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
 *   <li>{@code /bank tier [tier]}       — view or set your bank tier</li>
 *   <li>{@code /bank type [type]}       — view or set your bank type</li>
 *   <li>{@code /bank history}           — view recent transactions</li>
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
            case "tier"     -> handleTier(player, args);
            case "type"     -> handleType(player, args);
            case "history"  -> handleHistory(player);
            default -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Arrays.asList("balance", "deposit", "withdraw", "tier", "type", "history").stream()
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("type")) {
            String lower = args[1].toLowerCase();
            return Arrays.stream(BankManager.BankType.values())
                    .map(t -> t.name().toLowerCase())
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("tier")) {
            String lower = args[1].toLowerCase();
            return Arrays.stream(BankManager.BankTier.values())
                    .map(t -> t.name().toLowerCase())
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

    private void handleTier(Player player, String[] args) {
        if (args.length < 2) {
            BankManager.BankTier tier = bankManager.getTier(player.getUniqueId());
            player.sendMessage("Your bank tier: " + tier.getDisplayName() + " (max balance: " + tier.getMaxBalance() + " coins)");
            return;
        }
        try {
            BankManager.BankTier tier = BankManager.BankTier.valueOf(args[1].toUpperCase());
            bankManager.setTier(player.getUniqueId(), tier);
            player.sendMessage("Bank tier set to: " + tier.getDisplayName());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown tier. Valid tiers: PERSONAL_I through PERSONAL_VII");
        }
    }

    private void handleType(Player player, String[] args) {
        if (args.length < 2) {
            BankManager.BankType type = bankManager.getBankType(player.getUniqueId());
            player.sendMessage("Your bank type: " + type.getDisplayName() + (type.isShared() ? " (shared with island)" : ""));
            return;
        }
        try {
            BankManager.BankType type = BankManager.BankType.valueOf(args[1].toUpperCase());
            bankManager.setBankType(player.getUniqueId(), type);
            player.sendMessage("Bank type set to: " + type.getDisplayName());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown type. Valid types: PERSONAL, ISLAND");
        }
    }

    private void handleHistory(Player player) {
        List<BankManager.BankTransaction> history = bankManager.getTransactions(player.getUniqueId());
        if (history.isEmpty()) {
            player.sendMessage("No transactions recorded.");
            return;
        }
        player.sendMessage("=== Transaction History ===");
        int start = Math.max(0, history.size() - 10);
        for (int i = start; i < history.size(); i++) {
            BankManager.BankTransaction tx = history.get(i);
            player.sendMessage(tx.type().getDisplayName() + ": " + tx.amount() + " coins");
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Bank Commands ===");
        player.sendMessage("/bank balance — view your balance");
        player.sendMessage("/bank deposit <amount> — deposit coins");
        player.sendMessage("/bank withdraw <amount> — withdraw coins");
        player.sendMessage("/bank tier [tier] — view or set your bank tier");
        player.sendMessage("/bank type [type] — view or set your bank type");
        player.sendMessage("/bank history — view recent transactions");
    }
}
