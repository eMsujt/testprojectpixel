package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.TradingManager;
import com.skyblock.trading.TradingManager.TradeRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public final class TradeCommand implements CommandExecutor {

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
            player.sendMessage("Usage: /trade request <player>");
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("Player not found: " + args[1]);
            return;
        }
        if (target.equals(player)) {
            player.sendMessage("You cannot request a trade with yourself.");
            return;
        }
        TradingManager.getInstance().sendRequest(player.getUniqueId(), target.getUniqueId());
        player.sendMessage("Trade request sent to " + target.getName() + ".");
        target.sendMessage(player.getName() + " has sent you a trade request. Use /trade accept "
                + player.getName() + " to accept.");
    }

    private void handleAccept(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /trade accept <player>");
            return;
        }
        Player requester = Bukkit.getPlayer(args[1]);
        if (requester == null) {
            player.sendMessage("Player not found: " + args[1]);
            return;
        }
        try {
            TradingManager.getInstance().acceptRequest(requester.getUniqueId(), player.getUniqueId());
            player.sendMessage("You accepted the trade request from " + requester.getName() + ".");
            requester.sendMessage(player.getName() + " has accepted your trade request.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            player.sendMessage("No pending trade request from " + requester.getName() + ".");
        }
    }

    private void handleDecline(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /trade decline <player>");
            return;
        }
        Player requester = Bukkit.getPlayer(args[1]);
        if (requester == null) {
            player.sendMessage("Player not found: " + args[1]);
            return;
        }
        boolean removed = TradingManager.getInstance().declineRequest(requester.getUniqueId());
        if (removed) {
            player.sendMessage("You declined the trade request from " + requester.getName() + ".");
            requester.sendMessage(player.getName() + " has declined your trade request.");
        } else {
            player.sendMessage("No pending trade request from " + requester.getName() + ".");
        }
    }

    private void handleStatus(Player player) {
        Optional<TradeRequest> req = TradingManager.getInstance().getRequest(player.getUniqueId());
        if (req.isEmpty()) {
            player.sendMessage("You have no outgoing trade request.");
        } else {
            Player target = Bukkit.getPlayer(req.get().getTarget());
            String targetName = target != null ? target.getName() : req.get().getTarget().toString();
            player.sendMessage("Pending trade request to: " + targetName);
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Trade Commands ===");
        player.sendMessage("/trade request <player>  — send a trade request");
        player.sendMessage("/trade accept <player>   — accept a trade request");
        player.sendMessage("/trade decline <player>  — decline a trade request");
        player.sendMessage("/trade status            — show your outgoing request");
    }
}
