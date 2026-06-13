package com.skyblock.core.title;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /title} command for managing per-player cosmetic titles.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /title list}          — show your active title</li>
 *   <li>{@code /title set <title>}   — set your active title</li>
 *   <li>{@code /title remove}        — clear your active title</li>
 * </ul>
 * </p>
 */
public final class TitleCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "set", "remove");

    private final TitleManager titleManager;

    public TitleCommand(TitleManager titleManager) {
        this.titleManager = titleManager;
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
            case "list"   -> handleList(player);
            case "set"    -> handleSet(player, args);
            case "remove" -> handleRemove(player);
            default       -> sendHelp(player);
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
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        String current = titleManager.getTitle(player.getUniqueId());
        player.sendMessage("=== Your Titles ===");
        if (current != null) {
            player.sendMessage("Active: " + current);
        } else {
            player.sendMessage("You have no active title.");
        }
    }

    private void handleSet(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /title set <title>");
            return;
        }
        String title = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        titleManager.setTitle(player.getUniqueId(), title);
        player.sendMessage("Title set to: " + title);
    }

    private void handleRemove(Player player) {
        if (!titleManager.hasTitle(player.getUniqueId())) {
            player.sendMessage("You do not have an active title.");
            return;
        }
        titleManager.clearTitle(player.getUniqueId());
        player.sendMessage("Your title has been removed.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Title Commands ===");
        player.sendMessage("/title list          — view your active title");
        player.sendMessage("/title set <title>   — set your active title");
        player.sendMessage("/title remove        — remove your active title");
    }
}
