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
            handleTier(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "tier"  -> handleTier(player);
            case "set"   -> handleSet(player, args);
            default      -> sendHelp(player);
        }
        return true;
    }

    private void handleTier(Player player) {
        UUID id = player.getUniqueId();
        KuudraManager manager = KuudraManager.getInstance();
        int tier = manager.getKuudraTier(id);
        player.sendMessage("=== Kuudra ===");
        player.sendMessage("Current Tier: " + tier + " — " + TIER_NAMES[tier]);
        int next = tier + 1;
        if (next <= 5) {
            player.sendMessage("Next Tier: " + next + " — " + TIER_NAMES[next]);
        } else {
            player.sendMessage("You have reached the maximum Kuudra tier!");
        }
    }

    private void handleSet(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /kuudra set <1-5>");
            return;
        }
        int tier;
        try {
            tier = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid tier: " + args[1]);
            return;
        }
        if (tier < 1 || tier > 5) {
            player.sendMessage("Tier must be between 1 and 5.");
            return;
        }
        KuudraManager.getInstance().setKuudraTier(player.getUniqueId(), tier);
        player.sendMessage("Kuudra tier set to " + tier + " — " + TIER_NAMES[tier] + ".");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Kuudra Commands ===");
        player.sendMessage("/kuudra tier       — show your current Kuudra tier");
        player.sendMessage("/kuudra set <1-5>  — set your Kuudra tier");
    }
}
