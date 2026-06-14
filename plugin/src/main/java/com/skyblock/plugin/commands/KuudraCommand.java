package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.KuudraManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class KuudraCommand implements CommandExecutor {

    private static final String[] TIER_NAMES = {"", "Basic", "Hot", "Burning", "Fiery", "Infernal"};

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleStats(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "stats" -> handleStats(player);
            default      -> sendHelp(player);
        }
        return true;
    }

    private void handleStats(Player player) {
        UUID id = player.getUniqueId();
        KuudraManager manager = KuudraManager.getInstance();
        int tier = manager.getKuudraTier(id);

        player.sendMessage("=== Kuudra Stats ===");
        player.sendMessage("Best tier: T" + tier + " — " + TIER_NAMES[tier]);
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Kuudra Commands ===");
        player.sendMessage("/kuudra        — show your best Kuudra tier");
        player.sendMessage("/kuudra stats  — show your best Kuudra tier");
    }
}
