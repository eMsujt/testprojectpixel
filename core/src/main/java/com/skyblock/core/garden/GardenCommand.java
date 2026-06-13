package com.skyblock.core.garden;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class GardenCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("plots", "crops", "visit");

    private final GardenManager manager;

    public GardenCommand(GardenManager manager) {
        this.manager = manager;
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
            case "plots" -> handlePlots(player);
            case "crops" -> handleCrops(player);
            case "visit" -> handleVisit(player, args);
            default      -> sendHelp(player);
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
        if (args.length == 2 && args[0].equalsIgnoreCase("visit")) {
            String prefix = args[1].toLowerCase();
            return org.bukkit.Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handlePlots(Player player) {
        int plots = manager.getPlots(player.getUniqueId());
        player.sendMessage("=== Garden Plots ===");
        player.sendMessage("  Unlocked plots: " + plots);
    }

    private void handleCrops(Player player) {
        Map<String, Integer> levels = manager.getCropLevels(player.getUniqueId());
        player.sendMessage("=== Garden Crops ===");
        if (levels.isEmpty()) {
            player.sendMessage("  No crop levels tracked yet.");
            return;
        }
        levels.forEach((crop, level) ->
                player.sendMessage("  " + crop + ": level " + level));
    }

    private void handleVisit(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /garden visit <player>");
            return;
        }
        Player target = org.bukkit.Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage("You are already on your own garden.");
            return;
        }
        player.sendMessage("Visiting " + target.getName() + "'s garden.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Garden Commands ===");
        player.sendMessage("/garden plots — view your unlocked garden plots");
        player.sendMessage("/garden crops — view your crop levels");
        player.sendMessage("/garden visit <player> — visit another player's garden");
    }
}
