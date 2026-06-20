package com.skyblock.core.command;

import com.skyblock.core.manager.BankManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Player-facing command reporting a player's liquid net worth, computed by
 * summing their {@link BankManager} bank balance and purse balance. The bank
 * tier reached by that combined wealth is shown alongside the breakdown.
 */
public final class NetWorthCommand implements TabExecutor {

    private final BankManager bankManager;

    public NetWorthCommand(BankManager bankManager) {
        this.bankManager = bankManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        UUID id = player.getUniqueId();
        double bank = bankManager.getBalance(id);
        long purse = bankManager.getPurseBalance(id);
        double total = bank + purse;

        player.sendMessage("=== Net Worth ===");
        player.sendMessage("Purse: " + purse + " coins");
        player.sendMessage("Bank: " + bank + " coins");
        player.sendMessage("Total: " + total + " coins");
        player.sendMessage("Bank Tier: " + BankManager.BankTier.forBalance(total).getDisplayName());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
