package com.skyblock.plugin.command.enchanting;

import com.skyblock.core.enchanting.EnchantingManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class EnchantingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("history")) {
            handleHistory(player);
            return true;
        }

        UUID id = player.getUniqueId();
        EnchantingManager manager = EnchantingManager.getInstance();

        Map<EnchantingManager.SkyBlockEnchantment, Integer> enchants = manager.getEnchantments(id);
        player.sendMessage("=== Your Enchantments ===");
        if (enchants.isEmpty()) {
            player.sendMessage("  No enchantments applied.");
        } else {
            for (Map.Entry<EnchantingManager.SkyBlockEnchantment, Integer> entry : enchants.entrySet()) {
                player.sendMessage("  " + entry.getKey().getDisplayName() + " " + entry.getValue()
                        + "/" + entry.getKey().getMaxLevel());
            }
        }

        return true;
    }

    private void handleHistory(Player player) {
        List<String> history = EnchantingManager.getInstance().getEnchantingHistory(player.getUniqueId());
        if (history.isEmpty()) {
            player.sendMessage("You have no enchanting history.");
            return;
        }
        player.sendMessage("=== Your Enchanting History ===");
        for (int i = 0; i < history.size(); i++) {
            player.sendMessage((i + 1) + ". " + history.get(i));
        }
    }
}
