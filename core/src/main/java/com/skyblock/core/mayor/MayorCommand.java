package com.skyblock.core.mayor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class MayorCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("current", "perks", "candidates");

    private final MayorManager manager;

    public MayorCommand(MayorManager manager) {
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
            case "current"    -> handleCurrent(player);
            case "perks"      -> handlePerks(player);
            case "candidates" -> handleCandidates(player);
            default           -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleCurrent(Player player) {
        String mayor = manager.getCurrentMayor();
        if (mayor == null) {
            player.sendMessage("No mayor is currently elected.");
            return;
        }
        player.sendMessage("=== Current Mayor ===");
        player.sendMessage("Mayor: " + mayor);
    }

    private void handlePerks(Player player) {
        String mayor = manager.getCurrentMayor();
        if (mayor == null) {
            player.sendMessage("No mayor is currently elected.");
            return;
        }
        List<String> perks = manager.getPerks(mayor);
        player.sendMessage("=== " + mayor + "'s Perks ===");
        if (perks.isEmpty()) {
            player.sendMessage("No perks available.");
        } else {
            perks.forEach(perk -> player.sendMessage("- " + perk));
        }
    }

    private void handleCandidates(Player player) {
        List<String> candidates = manager.getCandidates();
        player.sendMessage("=== Mayor Candidates ===");
        if (candidates.isEmpty()) {
            player.sendMessage("No candidates at this time.");
        } else {
            candidates.forEach(c -> player.sendMessage("- " + c));
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Mayor Commands ===");
        player.sendMessage("/mayor current — show the current mayor");
        player.sendMessage("/mayor perks — show the current mayor's perks");
        player.sendMessage("/mayor candidates — list candidates for the next election");
    }
}
