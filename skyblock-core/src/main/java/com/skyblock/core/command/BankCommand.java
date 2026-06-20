package com.skyblock.core.command;

import com.skyblock.core.manager.BankManager;
import com.skyblock.core.manager.BankManager.BankTier;
import com.skyblock.core.manager.BankManager.BankType;
import com.skyblock.core.menu.BankMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class BankCommand extends PlayerCommand {

    private final BankManager bankManager;

    public BankCommand(BankManager bankManager) {
        this.bankManager = bankManager;
    }

    @Override
    protected void openMenu(Player p) {
        new BankMenu(p.getUniqueId()).open(p);
    }

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            openMenu(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "balance"  -> handleBalance(player);
            case "deposit"  -> handleDeposit(player, args);
            case "withdraw" -> handleWithdraw(player, args);
            case "tier"     -> handleTier(player, args);
            case "type"     -> handleType(player, args);
            case "history"  -> handleHistory(player);
            case "interest" -> handleInterest(player);
            case "coop"     -> handleCoop(player, args);
            default         -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Arrays.asList("balance", "deposit", "withdraw", "tier", "type", "history", "interest", "coop").stream()
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("coop")) {
            String lower = args[1].toLowerCase();
            return Arrays.asList("balance", "deposit", "withdraw").stream()
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("type")) {
            String lower = args[1].toLowerCase();
            return Arrays.stream(BankType.values())
                    .map(t -> t.name().toLowerCase())
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("tier")) {
            String lower = args[1].toLowerCase();
            return Arrays.stream(BankTier.values())
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
            BankTier tier = bankManager.getTier(player.getUniqueId());
            player.sendMessage("Your bank tier: " + tier.getDisplayName() + " (interest rate: " + tier.getInterestRate() + "%)");
            return;
        }
        try {
            BankTier tier = BankTier.valueOf(args[1].toUpperCase());
            bankManager.setTier(player.getUniqueId(), tier);
            player.sendMessage("Bank tier set to: " + tier.getDisplayName());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown tier. Valid tiers: STARTER, GOLD, DELUXE, SUPER_DELUXE, PREMIER, PREMIER_PLUS");
        }
    }

    private void handleType(Player player, String[] args) {
        if (args.length < 2) {
            BankType type = bankManager.getBankType(player.getUniqueId());
            player.sendMessage("Your bank type: " + type.getDisplayName() + (type.isShared() ? " (shared with island)" : ""));
            return;
        }
        try {
            BankType type = BankType.valueOf(args[1].toUpperCase());
            bankManager.setBankType(player.getUniqueId(), type);
            player.sendMessage("Bank type set to: " + type.getDisplayName());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown type. Valid types: PERSONAL, ISLAND");
        }
    }

    private void handleHistory(Player player) {
        List<String> history = bankManager.getAccount(player.getUniqueId()).transactionHistory();
        if (history.isEmpty()) {
            player.sendMessage("No transactions recorded.");
            return;
        }
        player.sendMessage("=== Transaction History ===");
        int start = Math.max(0, history.size() - 10);
        for (int i = start; i < history.size(); i++) {
            player.sendMessage(history.get(i));
        }
    }

    private void handleInterest(Player player) {
        double interest = bankManager.applyInterest(player.getUniqueId());
        if (interest <= 0) {
            player.sendMessage("No interest applied (balance is zero).");
        } else {
            player.sendMessage(String.format("Interest applied: +%.2f coins. New balance: %.2f coins",
                    interest, bankManager.getBalance(player.getUniqueId())));
        }
    }

    private void handleCoop(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /bank coop <balance|deposit|withdraw> <coopName> [amount]");
            return;
        }
        String sub = args[1].toLowerCase();
        String coopName = args[2];
        switch (sub) {
            case "balance" -> {
                double balance = bankManager.getCoopBalance(coopName);
                player.sendMessage("Co-op bank balance (" + coopName + "): " + balance + " coins");
            }
            case "deposit" -> {
                if (args.length < 4) {
                    player.sendMessage("Usage: /bank coop deposit <coopName> <amount>");
                    return;
                }
                double amount = parseAmount(player, args[3]);
                if (amount <= 0) return;
                try {
                    bankManager.depositCoop(coopName, amount);
                    player.sendMessage("Deposited " + amount + " coins into co-op bank (" + coopName
                            + "). New balance: " + bankManager.getCoopBalance(coopName));
                } catch (IllegalArgumentException e) {
                    player.sendMessage(e.getMessage());
                }
            }
            case "withdraw" -> {
                if (args.length < 4) {
                    player.sendMessage("Usage: /bank coop withdraw <coopName> <amount>");
                    return;
                }
                double amount = parseAmount(player, args[3]);
                if (amount <= 0) return;
                try {
                    bankManager.withdrawCoop(coopName, amount);
                    player.sendMessage("Withdrew " + amount + " coins from co-op bank (" + coopName
                            + "). New balance: " + bankManager.getCoopBalance(coopName));
                } catch (IllegalArgumentException e) {
                    player.sendMessage(e.getMessage());
                }
            }
            default -> player.sendMessage("Usage: /bank coop <balance|deposit|withdraw> <coopName> [amount]");
        }
    }

    private double parseAmount(Player player, String input) {
        try {
            double amount = Double.parseDouble(input);
            if (amount <= 0) {
                player.sendMessage("Amount must be a positive number.");
                return 0;
            }
            return amount;
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount: " + input);
            return 0;
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
        player.sendMessage("/bank interest — apply interest to your balance");
        player.sendMessage("/bank coop <balance|deposit|withdraw> <coopName> [amount] — manage co-op bank");
    }
}
