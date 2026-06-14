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

        UUID id = player.getUniqueId();
        BankManager manager = BankManager.getInstance();
        double balance = manager.getBalance(id);
        BankManager.BankTier tier = manager.getTier(id);
        BankManager.BankType type = manager.getBankType(id);
        player.sendMessage("=== Bank ===");
        player.sendMessage("Balance: " + balance);
        player.sendMessage("Tier: " + tier.getDisplayName());
        player.sendMessage("Type: " + type.getDisplayName());
        return true;
    }
}
