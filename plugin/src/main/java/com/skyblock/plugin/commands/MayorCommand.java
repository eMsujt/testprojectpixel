package com.skyblock.plugin.commands;

import com.skyblock.core.mayor.MayorManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class MayorCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0 || "status".equalsIgnoreCase(args[0])) {
            handleStatus(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "candidates" -> handleCandidates(player);
            case "vote"       -> handleVote(player, args);
            case "myvote"     -> handleMyVote(player);
            default           -> sendHelp(player);
        }
        return true;
    }

    private void handleStatus(Player player) {
        MayorManager manager = MayorManager.getInstance();
        MayorManager.MayorCandidate current = manager.getCurrentMayor();
        player.sendMessage("=== Mayor ===");
        player.sendMessage("Current Mayor: " + (current != null ? current.getDisplayName() : "None"));
        if (current != null) {
            player.sendMessage("Perks: " + String.join(", ", current.getPerks()));
        }
    }

    private void handleCandidates(Player player) {
        player.sendMessage("=== Mayor Candidates ===");
        for (MayorManager.MayorCandidate candidate : MayorManager.MayorCandidate.values()) {
            player.sendMessage("  " + candidate.getDisplayName() + " — " + String.join(", ", candidate.getPerks()));
        }
    }

    private void handleVote(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /mayor vote <candidate>");
            return;
        }
        MayorManager.MayorCandidate candidate = parseCandidate(args[1]);
        if (candidate == null) {
            player.sendMessage("Unknown candidate: " + args[1] + ". Use /mayor candidates to see options.");
            return;
        }
        MayorManager.getInstance().vote(player.getUniqueId(), candidate);
        player.sendMessage("You voted for " + candidate.getDisplayName() + ".");
    }

    private void handleMyVote(Player player) {
        MayorManager.MayorCandidate vote = MayorManager.getInstance().getVote(player.getUniqueId());
        if (vote == null) {
            player.sendMessage("You have not cast a vote yet.");
        } else {
            player.sendMessage("Your vote: " + vote.getDisplayName());
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Mayor Commands ===");
        player.sendMessage("/mayor status          — show the current mayor and perks");
        player.sendMessage("/mayor candidates      — list all mayor candidates");
        player.sendMessage("/mayor vote <name>     — vote for a candidate");
        player.sendMessage("/mayor myvote          — show your current vote");
    }

    private static MayorManager.MayorCandidate parseCandidate(String name) {
        for (MayorManager.MayorCandidate c : MayorManager.MayorCandidate.values()) {
            if (c.name().equalsIgnoreCase(name) || c.getDisplayName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }
}
