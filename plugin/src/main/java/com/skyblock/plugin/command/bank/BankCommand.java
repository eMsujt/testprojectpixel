package com.skyblock.plugin.command.bank;

import com.skyblock.core.bank.BankManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.plugin.commands.BankCommand} instead.
 */
@Deprecated
public final class BankCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        UUID id = player.getUniqueId();
        BankManager manager = BankManager.getInstance();

        if (args.length == 0) {
            double balance = manager.getBalance(id);
            BankManager.BankTier tier = manager.getTier(id);
            player.sendMessage("=== Your Bank ===");
            player.sendMessage("  Balance: " + balance + " coins");
            player.sendMessage("  Tier: " + tier.getDisplayName() + " (" + tier.getInterestRate() + "% interest)");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "deposit" -> {
                if (args.length < 2) {
                    player.sendMessage("Usage: /bank deposit <amount>");
                    return true;
                }
                try {
                    double amount = Double.parseDouble(args[1]);
                    manager.deposit(id, amount);
                    player.sendMessage("Deposited " + amount + " coins. Balance: " + manager.getBalance(id));
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid amount: " + args[1]);
                } catch (IllegalArgumentException e) {
                    player.sendMessage(e.getMessage());
                }
            }
            case "history" -> handleHistory(player);
            case "withdraw" -> {
                if (args.length < 2) {
                    player.sendMessage("Usage: /bank withdraw <amount>");
                    return true;
                }
                try {
                    double amount = Double.parseDouble(args[1]);
                    manager.withdraw(id, amount);
                    player.sendMessage("Withdrew " + amount + " coins. Balance: " + manager.getBalance(id));
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid amount: " + args[1]);
                } catch (IllegalArgumentException e) {
                    player.sendMessage(e.getMessage());
                }
            }
            default -> player.sendMessage("Usage: /bank [deposit|withdraw|history] <amount>");
        }

        return true;
    }

    private void handleHistory(Player player) {
        List<String> history = BankManager.getInstance().getBankHistory(player.getUniqueId());
        if (history.isEmpty()) {
            player.sendMessage("No bank history found.");
            return;
        }
        player.sendMessage("=== Bank History ===");
        for (int i = 0; i < history.size(); i++) {
            player.sendMessage((i + 1) + ". " + history.get(i));
        }
    }
}
