package com.skyblock.core.banking;

import com.skyblock.core.manager.ProfileManager;
import com.skyblock.core.manager.ProfileManager.SkyBlockProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /banking} command with per-profile coin balances.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /banking balance <profile>}           — show the coin balance for a profile</li>
 *   <li>{@code /banking deposit <profile> <amount>}  — deposit coins into a profile</li>
 *   <li>{@code /banking withdraw <profile> <amount>} — withdraw coins from a profile</li>
 * </ul>
 * </p>
 */
public final class BankingCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("balance", "deposit", "withdraw");

    private final BankingManager bankingManager;
    private final ProfileManager profileManager;

    public BankingCommand(BankingManager bankingManager, ProfileManager profileManager) {
        this.bankingManager = bankingManager;
        this.profileManager = profileManager;
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
            case "balance"  -> handleBalance(player, args);
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
        if (args.length == 2 && sender instanceof Player player) {
            String prefix = args[1].toLowerCase();
            return profileManager.getProfilesForOwner(player.getUniqueId()).stream()
                    .map(SkyBlockProfile::name)
                    .filter(n -> n.toLowerCase().startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleBalance(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /banking balance <profile>");
            return;
        }
        SkyBlockProfile profile = findProfile(player, args[1]);
        if (profile == null) return;
        double balance = bankingManager.getBalance(profile.profileId());
        player.sendMessage("[" + profile.name() + "] Bank Balance: " + balance + " coins");
    }

    private void handleDeposit(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /banking deposit <profile> <amount>");
            return;
        }
        SkyBlockProfile profile = findProfile(player, args[1]);
        if (profile == null) return;
        double amount = parseAmount(player, args[2]);
        if (amount <= 0) return;
        try {
            double newBalance = bankingManager.deposit(profile.profileId(), amount);
            player.sendMessage("[" + profile.name() + "] Deposited " + amount + " coins. Balance: " + newBalance);
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleWithdraw(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /banking withdraw <profile> <amount>");
            return;
        }
        SkyBlockProfile profile = findProfile(player, args[1]);
        if (profile == null) return;
        double amount = parseAmount(player, args[2]);
        if (amount <= 0) return;
        try {
            double newBalance = bankingManager.withdraw(profile.profileId(), amount);
            player.sendMessage("[" + profile.name() + "] Withdrew " + amount + " coins. Balance: " + newBalance);
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
        }
    }

    /** Looks up a profile by name for the given player, sending an error if not found. */
    private SkyBlockProfile findProfile(Player player, String name) {
        for (SkyBlockProfile p : profileManager.getProfilesForOwner(player.getUniqueId())) {
            if (p.name().equalsIgnoreCase(name)) {
                return p;
            }
        }
        player.sendMessage("You do not have a profile named \"" + name + "\".");
        return null;
    }

    /** Parses a positive coin amount, sending an error to the player on failure. */
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
        player.sendMessage("=== Banking Commands ===");
        player.sendMessage("/banking balance <profile> — view coin balance");
        player.sendMessage("/banking deposit <profile> <amount> — deposit coins");
        player.sendMessage("/banking withdraw <profile> <amount> — withdraw coins");
    }
}
