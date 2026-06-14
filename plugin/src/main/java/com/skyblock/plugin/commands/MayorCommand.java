package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.MayorManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public final class MayorCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleInfo(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"   -> handleInfo(player);
            case "vote"   -> handleVote(player, args);
            case "unvote" -> handleUnvote(player);
            case "votes"  -> handleVotes(player);
            case "set"    -> handleSet(player, args);
            case "setday" -> handleSetDay(player, args);
            default       -> sendHelp(player);
        }
        return true;
    }

    private void handleInfo(Player player) {
        MayorManager mgr = MayorManager.getInstance();
        String activeMayor = mgr.getCurrentMayor();
        java.util.List<String> activePerks = mgr.getPerks(activeMayor);
        player.sendMessage("=== Mayor ===");
        player.sendMessage("Active mayor: " + activeMayor);
        player.sendMessage("Active perks: " + (activePerks.isEmpty() ? "none" : String.join(", ", activePerks)));
        player.sendMessage("Election day: " + mgr.getElectionDay());
        String vote = mgr.getMayorVote(player.getUniqueId());
        player.sendMessage("Your vote: " + (vote != null ? vote : "none"));
    }

    private void handleVote(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /mayor vote <name>");
            return;
        }
        String mayorName = args[1];
        MayorManager.getInstance().setMayorVote(player.getUniqueId(), mayorName);
        player.sendMessage("You voted for " + mayorName + ".");
    }

    private void handleUnvote(Player player) {
        boolean removed = MayorManager.getInstance().clearMayorVote(player.getUniqueId());
        if (removed) {
            player.sendMessage("Your vote has been cleared.");
        } else {
            player.sendMessage("You have not cast a vote.");
        }
    }

    private void handleVotes(Player player) {
        Map<UUID, String> votes = MayorManager.getInstance().getMayorVotes();
        player.sendMessage("=== Mayor Votes ===");
        if (votes.isEmpty()) {
            player.sendMessage("No votes cast yet.");
            return;
        }
        for (Map.Entry<UUID, String> entry : votes.entrySet()) {
            player.sendMessage(entry.getKey() + ": " + entry.getValue());
        }
    }

    private void handleSet(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /mayor set <name>");
            return;
        }
        String mayorName = args[1];
        MayorManager.getInstance().setCurrentMayor(mayorName);
        player.sendMessage("Current mayor set to " + mayorName + ".");
    }

    private void handleSetDay(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /mayor setday <day>");
            return;
        }
        int day;
        try {
            day = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid day: " + args[1]);
            return;
        }
        MayorManager.getInstance().setElectionDay(day);
        player.sendMessage("Election day set to " + day + ".");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Mayor Commands ===");
        player.sendMessage("/mayor              — show current mayor info");
        player.sendMessage("/mayor info         — show current mayor info");
        player.sendMessage("/mayor vote <name>  — cast your vote");
        player.sendMessage("/mayor unvote       — clear your vote");
        player.sendMessage("/mayor votes        — list all votes");
        player.sendMessage("/mayor set <name>   — set the current mayor");
        player.sendMessage("/mayor setday <day> — set the election day");
    }
}
