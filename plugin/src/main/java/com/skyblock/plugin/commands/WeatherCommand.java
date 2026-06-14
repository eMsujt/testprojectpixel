package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.WeatherManager;
import com.skyblock.plugin.managers.WeatherManager.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class WeatherCommand implements CommandExecutor {

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
            case "set"    -> handleSet(player, args);
            case "status" -> handleStatus(player);
            case "list"   -> handleList(player);
            default       -> sendHelp(player);
        }
        return true;
    }

    private void handleSet(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblock weather set <type>");
            return;
        }
        WeatherType type;
        try {
            type = WeatherType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown weather type: " + args[1] + ". Use /skyblock weather list to see available types.");
            return;
        }
        WeatherManager.getInstance().setActiveWeather(type);
        player.sendMessage("Weather set to: " + type.name());
    }

    private void handleStatus(Player player) {
        WeatherType active = WeatherManager.getInstance().getActiveWeather();
        if (active == null) {
            player.sendMessage("No custom weather is active.");
        } else {
            player.sendMessage("Current weather: " + active.name());
        }
    }

    private void handleList(Player player) {
        player.sendMessage("=== SkyBlock Weather Types ===");
        for (WeatherType type : WeatherType.values()) {
            player.sendMessage("  " + type.name());
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Weather Commands ===");
        player.sendMessage("/skyblock weather set <type>   — set the active weather");
        player.sendMessage("/skyblock weather status       — show the current weather");
        player.sendMessage("/skyblock weather list         — list all weather types");
    }
}
