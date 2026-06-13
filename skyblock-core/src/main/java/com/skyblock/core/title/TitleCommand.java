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
 * Handles the {@code /title} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /title}              — show your active title</li>
 *   <li>{@code /title list}         — list all titles and which you've unlocked</li>
 *   <li>{@code /title set <title>}  — equip an unlocked title</li>
 *   <li>{@code /title unlock <title>} — unlock a title</li>
 *   <li>{@code /title clear}        — remove your active title</li>
 * </ul>
 * </p>
 */
public final class TitleCommand implements TabExecutor {

    private static final List<String> TITLE_NAMES = Arrays.stream(TitleManager.TitleType.values())
            .map(t -> t.name().toLowerCase())
            .collect(Collectors.toList());

    private final TitleManager titleManager;

    public TitleCommand(TitleManager titleManager) {
        if (titleManager == null) {
            throw new IllegalArgumentException("titleManager must not be null");
        }
        this.titleManager = titleManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            TitleManager.TitleType active = titleManager.getActiveTitle(player.getUniqueId());
            if (active == null) {
                player.sendMessage("You have no active title. Use /title list to see available titles.");
            } else {
                player.sendMessage("Active title: " + active.getDisplayName());
            }
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"   -> handleList(player);
            case "set"    -> handleSet(player, args);
            case "unlock" -> handleUnlock(player, args);
            case "clear"  -> handleClear(player);
            default       -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Arrays.asList("list", "set", "unlock", "clear").stream()
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("unlock"))) {
            String lower = args[1].toLowerCase();
            return TITLE_NAMES.stream()
                    .filter(t -> t.startsWith(lower))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        player.sendMessage("=== Titles ===");
        for (TitleManager.TitleType type : TitleManager.TitleType.values()) {
            boolean unlocked = titleManager.isUnlocked(player.getUniqueId(), type);
            String status = unlocked ? "[Unlocked]" : "[Locked]";
            player.sendMessage(status + " " + type.getDisplayName() + " — " + type.getDescription());
        }
    }

    private void handleSet(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /title set <title>");
            return;
        }
        TitleManager.TitleType type = parseTitle(player, args[1]);
        if (type == null) {
            return;
        }
        if (!titleManager.setActiveTitle(player.getUniqueId(), type)) {
            player.sendMessage("You have not unlocked the title '" + type.getDisplayName() + "'. Use /title unlock <title> first.");
            return;
        }
        player.sendMessage("Active title set to: " + type.getDisplayName());
    }

    private void handleUnlock(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /title unlock <title>");
            return;
        }
        TitleManager.TitleType type = parseTitle(player, args[1]);
        if (type == null) {
            return;
        }
        if (!titleManager.unlockTitle(player.getUniqueId(), type)) {
            player.sendMessage("You have already unlocked the title '" + type.getDisplayName() + "'.");
            return;
        }
        player.sendMessage("Title unlocked: " + type.getDisplayName() + " — " + type.getDescription());
    }

    private void handleClear(Player player) {
        if (titleManager.clearActiveTitle(player.getUniqueId())) {
            player.sendMessage("Your active title has been cleared.");
        } else {
            player.sendMessage("You have no active title to clear.");
        }
    }

    private TitleManager.TitleType parseTitle(Player player, String input) {
        try {
            return TitleManager.TitleType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown title '" + input + "'. Options: " + TITLE_NAMES);
            return null;
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Title Commands ===");
        player.sendMessage("/title — show your active title");
        player.sendMessage("/title list — list all titles");
        player.sendMessage("/title set <title> — equip an unlocked title");
        player.sendMessage("/title unlock <title> — unlock a title");
        player.sendMessage("/title clear — remove your active title");
    }
}
