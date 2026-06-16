package com.skyblock.core.museum;

import com.skyblock.core.manager.MuseumManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles the {@code /museum} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /museum info}                        — show your total donation count</li>
 *   <li>{@code /museum view <category>}             — list donated items in a category</li>
 *   <li>{@code /museum categories}                  — list all museum categories</li>
 *   <li>{@code /museum donate <category> <item>}    — (op) donate an item to a category</li>
 * </ul>
 * </p>
 */
public final class MuseumCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList(
            "info", "view", "categories", "donate");

    private final MuseumManager museumManager;

    public MuseumCommand(MuseumManager museumManager) {
        this.museumManager = museumManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /museum <info|view|categories|donate>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"       -> handleInfo(player);
            case "view"       -> handleView(player, args);
            case "categories" -> handleCategories(player);
            case "donate"     -> handleDonate(player, args);
            default           -> player.sendMessage(
                    "Unknown subcommand. Usage: /museum <info|view|categories|donate>");
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
        if (args.length == 2 && (args[0].equalsIgnoreCase("view")
                || args[0].equalsIgnoreCase("donate"))) {
            String prefix = args[1].toUpperCase();
            return Arrays.stream(MuseumManager.MuseumCategory.values())
                    .map(Enum::name)
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleInfo(Player player) {
        int total = museumManager.getTotalDonations(player.getUniqueId());
        player.sendMessage("Museum donations: " + total + " item(s) donated.");
    }

    private void handleView(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /museum view <category>");
            return;
        }
        MuseumManager.MuseumCategory category;
        try {
            category = MuseumManager.MuseumCategory.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown category: " + args[1]);
            return;
        }
        Set<String> items = museumManager.getDonations(player.getUniqueId(), category);
        player.sendMessage("=== " + category.getDisplayName() + " Donations ===");
        if (items.isEmpty()) {
            player.sendMessage("No items donated in this category yet.");
        } else {
            for (String item : items) {
                player.sendMessage("- " + item);
            }
        }
    }

    private void handleCategories(Player player) {
        player.sendMessage("=== Museum Categories ===");
        for (MuseumManager.MuseumCategory category : MuseumManager.MuseumCategory.values()) {
            player.sendMessage(category.getDisplayName());
        }
    }

    private void handleDonate(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 3) {
            player.sendMessage("Usage: /museum donate <category> <item>");
            return;
        }
        MuseumManager.MuseumCategory category;
        try {
            category = MuseumManager.MuseumCategory.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown category: " + args[1]);
            return;
        }
        String itemName = args[2];
        boolean added = museumManager.donate(player.getUniqueId(), category, itemName);
        if (added) {
            player.sendMessage("Donated " + itemName + " to " + category.getDisplayName() + "!");
        } else {
            player.sendMessage(itemName + " has already been donated to " + category.getDisplayName() + ".");
        }
    }
}
