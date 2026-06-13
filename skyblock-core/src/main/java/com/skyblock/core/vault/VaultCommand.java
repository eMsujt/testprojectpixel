package com.skyblock.core.vault;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class VaultCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "deposit", "withdraw", "upgrade");

    private final VaultManager vaultManager;

    public VaultCommand(VaultManager vaultManager) {
        this.vaultManager = vaultManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleInfo(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"     -> handleInfo(player);
            case "deposit"  -> handleDeposit(player, args);
            case "withdraw" -> handleWithdraw(player, args);
            case "upgrade"  -> handleUpgrade(player);
            default         -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleInfo(Player player) {
        VaultManager.VaultTier tier = vaultManager.getTier(player.getUniqueId());
        long balance = vaultManager.getBalance(player.getUniqueId());
        player.sendMessage("=== Vault ===");
        player.sendMessage("  Tier:     " + tier.name());
        player.sendMessage("  Balance:  " + balance + " / " + tier.getCapacity());
        VaultManager.VaultTier next = tier.next();
        if (next != null) {
            player.sendMessage("  Next tier: " + next.name() + " (capacity " + next.getCapacity() + ")");
        } else {
            player.sendMessage("  You have reached the maximum vault tier.");
        }
    }

    private void handleDeposit(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /vault deposit <amount>");
            return;
        }
        long amount;
        try {
            amount = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount: " + args[1]);
            return;
        }
        if (amount <= 0) {
            player.sendMessage("Amount must be positive.");
            return;
        }
        long deposited = vaultManager.deposit(player.getUniqueId(), amount);
        if (deposited < amount) {
            player.sendMessage("Vault is full or would exceed capacity. Deposited " + deposited + " coins.");
        } else {
            player.sendMessage("Deposited " + deposited + " coins. Balance: " + vaultManager.getBalance(player.getUniqueId()));
        }
    }

    private void handleWithdraw(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /vault withdraw <amount>");
            return;
        }
        long amount;
        try {
            amount = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount: " + args[1]);
            return;
        }
        if (amount <= 0) {
            player.sendMessage("Amount must be positive.");
            return;
        }
        long withdrawn = vaultManager.withdraw(player.getUniqueId(), amount);
        if (withdrawn == 0) {
            player.sendMessage("Your vault is empty.");
        } else {
            player.sendMessage("Withdrew " + withdrawn + " coins. Balance: " + vaultManager.getBalance(player.getUniqueId()));
        }
    }

    private void handleUpgrade(Player player) {
        VaultManager.VaultTier current = vaultManager.getTier(player.getUniqueId());
        VaultManager.VaultTier next = current.next();
        if (next == null) {
            player.sendMessage("Your vault is already at the maximum tier (" + current.name() + ").");
            return;
        }
        vaultManager.setTier(player.getUniqueId(), next);
        player.sendMessage("Vault upgraded to " + next.name() + " tier (capacity " + next.getCapacity() + ").");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Vault Commands ===");
        player.sendMessage("/vault info               — show tier and balance");
        player.sendMessage("/vault deposit <amount>   — deposit coins into vault");
        player.sendMessage("/vault withdraw <amount>  — withdraw coins from vault");
        player.sendMessage("/vault upgrade            — upgrade vault tier");
    }
}
