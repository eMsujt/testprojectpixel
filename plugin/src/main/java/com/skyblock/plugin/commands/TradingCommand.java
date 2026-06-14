package com.skyblock.plugin.commands;

import com.skyblock.trading.TradingManager;
import com.skyblock.trading.TradingManager.TradeRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public final class TradingCommand implements CommandExecutor {

    private static final TradingManager MANAGER = new TradingManager();

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
            case "request", "req" -> handleRequest(player, args);
            case "accept"         -> handleAccept(player, args);
            case "decline"        -> handleDecline(player, args);
            case "status"         -> handleStatus(player);
            default               -> sendHelp(player);
        }
        return true;
    }

    private void handleRequest(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblock trade request <player>");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            player.sendMessage("Player not found: " + args[1]);
            return;
        }
        if (target.equals(player)) {
            player.sendMessage("You cannot send a trade request to yourself.");
            return;
        }
        MANAGER.sendRequest(player.getUniqueId(), target.getUniqueId());
        player.sendMessage("Trade request sent to " + target.getName() + ".");
        target.sendMessage(player.getName() + " has sent you a trade request. Use /skyblock trade accept " + player.getName() + " to accept.");
    }

    private void handleAccept(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblock trade accept <player>");
            return;
        }
        Player sender = Bukkit.getPlayerExact(args[1]);
        if (sender == null) {
            player.sendMessage("Player not found: " + args[1]);
            return;
        }
        try {
            TradeRequest request = MANAGER.acceptRequest(sender.getUniqueId(), player.getUniqueId());
            player.sendMessage("You accepted " + sender.getName() + "'s trade request.");
            sender.sendMessage(player.getName() + " accepted your trade request.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            player.sendMessage("No pending trade request from " + sender.getName() + ".");
        }
    }

    private void handleDecline(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblock trade decline <player>");
            return;
        }
        Player sender = Bukkit.getPlayerExact(args[1]);
        if (sender == null) {
            player.sendMessage("Player not found: " + args[1]);
            return;
        }
        boolean removed = MANAGER.declineRequest(sender.getUniqueId());
        if (removed) {
            player.sendMessage("You declined " + sender.getName() + "'s trade request.");
            sender.sendMessage(player.getName() + " declined your trade request.");
        } else {
            player.sendMessage("No pending trade request from " + sender.getName() + ".");
        }
    }

    private void handleStatus(Player player) {
        Optional<TradeRequest> outgoing = MANAGER.getRequest(player.getUniqueId());
        if (outgoing.isPresent()) {
            UUID targetId = outgoing.get().getTarget();
            Player target = Bukkit.getPlayer(targetId);
            String name = target != null ? target.getName() : targetId.toString();
            player.sendMessage("You have an outgoing trade request to " + name + ".");
        } else {
            player.sendMessage("You have no outgoing trade requests.");
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Trade Commands ===");
        player.sendMessage("/skyblock trade request <player>  — send a trade request");
        player.sendMessage("/skyblock trade accept <player>   — accept a trade request");
        player.sendMessage("/skyblock trade decline <player>  — decline a trade request");
        player.sendMessage("/skyblock trade status            — show your outgoing request");
    }
}
