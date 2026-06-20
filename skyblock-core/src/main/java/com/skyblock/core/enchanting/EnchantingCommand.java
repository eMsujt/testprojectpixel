package com.skyblock.core.enchanting;

import com.skyblock.core.manager.EnchantingManager;
import com.skyblock.core.util.SkyblockUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Handles the {@code /enchanting} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /enchanting list}                                     — list all enchant types</li>
 *   <li>{@code /enchanting info <enchantment>}                       — show max level for an enchant type</li>
 *   <li>{@code /enchanting apply <enchantment> <level>}              — apply an enchant type at a level</li>
 *   <li>{@code /enchanting remove <enchantment>}                     — remove an enchant type</li>
 *   <li>{@code /enchanting view}                                     — list your active enchantments</li>
 *   <li>{@code /enchanting book add <enchantment> <level> [name]}    — add an enchantment book to your inventory</li>
 *   <li>{@code /enchanting book list}                                — list your enchantment books</li>
 *   <li>{@code /enchanting book apply <index>}                       — apply a book by index (1-based)</li>
 * </ul>
 * </p>
 */
public final class EnchantingCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("list", "info", "apply", "remove", "view", "type", "book", "history");

    private static final List<String> BOOK_SUBCOMMANDS = Arrays.asList("add", "list", "apply");

    private final EnchantingManager enchantingManager;

    public EnchantingCommand(EnchantingManager enchantingManager) {
        this.enchantingManager = enchantingManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /enchanting <list|info|apply|remove|view|type>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"   -> handleList(player);
            case "info"   -> handleInfo(player, args);
            case "apply"  -> handleApply(player, args);
            case "remove" -> handleRemove(player, args);
            case "view"   -> handleView(player);
            case "type"   -> handleType(player);
            case "book"    -> handleBook(player, args);
            case "history" -> handleHistory(player);
            default        -> player.sendMessage("Unknown subcommand. Usage: /enchanting <list|info|apply|remove|view|type|book|history>");
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
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("info") || sub.equals("apply") || sub.equals("remove")) {
                String prefix = args[1].toLowerCase();
                return Arrays.stream(EnchantingManager.SkyBlockEnchantment.values())
                        .map(e -> e.name().toLowerCase())
                        .filter(n -> n.startsWith(prefix))
                        .sorted()
                        .collect(Collectors.toList());
            }
            if (sub.equals("book")) {
                String prefix = args[1].toLowerCase();
                return BOOK_SUBCOMMANDS.stream()
                        .filter(s -> s.startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("book") && args[1].equalsIgnoreCase("add")) {
            String prefix = args[2].toLowerCase();
            return Arrays.stream(EnchantingManager.SkyBlockEnchantment.values())
                    .map(e -> e.name().toLowerCase())
                    .filter(n -> n.startsWith(prefix))
                    .sorted()
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        player.sendMessage("=== SkyBlock Enchant Types ===");
        Arrays.stream(EnchantingManager.SkyBlockEnchantment.values())
                .forEach(e -> player.sendMessage(String.format(
                        "%s (max level: %d)",
                        e.getDisplayName(),
                        e.getMaxLevel())));
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /enchanting info <enchantment>");
            return;
        }
        EnchantingManager.SkyBlockEnchantment type = parseType(args[1]);
        if (type == null) {
            player.sendMessage("Unknown enchantment: " + args[1] + ". Use /enchanting list to see available enchantments.");
            return;
        }
        int currentLevel = enchantingManager.getLevel(player.getUniqueId(), type);
        int maxLevel = type.getMaxLevel();
        player.sendMessage(String.format("%s — current level: %d / max level: %d",
                type.getDisplayName(), currentLevel, maxLevel));
    }

    private void handleApply(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /enchanting apply <enchantment> <level>");
            return;
        }
        EnchantingManager.SkyBlockEnchantment type = parseType(args[1]);
        if (type == null) {
            player.sendMessage("Unknown enchantment: " + args[1] + ". Use /enchanting list to see available enchantments.");
            return;
        }
        int level;
        try {
            level = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Level must be a number.");
            return;
        }
        try {
            enchantingManager.setEnchantment(player.getUniqueId(), type, level);
            player.sendMessage("Applied " + type.getDisplayName() + " level " + level + ".");
        } catch (IllegalArgumentException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /enchanting remove <enchantment>");
            return;
        }
        EnchantingManager.SkyBlockEnchantment type = parseType(args[1]);
        if (type == null) {
            player.sendMessage("Unknown enchantment: " + args[1] + ". Use /enchanting list to see available enchantments.");
            return;
        }
        boolean removed = enchantingManager.removeEnchantment(player.getUniqueId(), type);
        if (removed) {
            player.sendMessage("Removed " + type.getDisplayName() + ".");
        } else {
            player.sendMessage("You do not have " + type.getDisplayName() + " applied.");
        }
    }

    private void handleView(Player player) {
        Map<EnchantingManager.SkyBlockEnchantment, Integer> enchantments =
                enchantingManager.getEnchantments(player.getUniqueId());
        if (enchantments.isEmpty()) {
            player.sendMessage("You have no active enchantments.");
            return;
        }
        player.sendMessage("=== Your Enchantments ===");
        enchantments.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> player.sendMessage(
                        e.getKey().getDisplayName() + " " + e.getValue()));
    }

    private void handleType(Player player) {
        player.sendMessage("=== SkyBlock Enchant Names ===");
        Arrays.stream(EnchantingManager.SkyBlockEnchant.values())
                .forEach(e -> player.sendMessage(e.name()));
    }

    private void handleBook(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /enchanting book <add|list|apply>");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "add" -> {
                // /enchanting book add <enchantment> <level> [name]
                if (args.length < 4) {
                    player.sendMessage("Usage: /enchanting book add <enchantment> <level> [name]");
                    return;
                }
                EnchantingManager.SkyBlockEnchantment type = parseType(args[2]);
                if (type == null) {
                    player.sendMessage("Unknown enchantment: " + args[2] + ". Use /enchanting list to see available enchantments.");
                    return;
                }
                int level;
                try {
                    level = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    player.sendMessage("Level must be a number.");
                    return;
                }
                String bookName = args.length >= 5
                        ? String.join(" ", Arrays.copyOfRange(args, 4, args.length))
                        : type.getDisplayName() + " Book " + SkyblockUtils.toRoman(level);
                try {
                    EnchantingManager.EnchantmentBook book =
                            new EnchantingManager.EnchantmentBook(bookName, type, level);
                    enchantingManager.addBook(player.getUniqueId(), book);
                    player.sendMessage("Added book \"" + bookName + "\" to your inventory.");
                } catch (IllegalArgumentException e) {
                    player.sendMessage(e.getMessage());
                }
            }
            case "list" -> {
                List<EnchantingManager.EnchantmentBook> books =
                        enchantingManager.getBooks(player.getUniqueId());
                if (books.isEmpty()) {
                    player.sendMessage("You have no enchantment books.");
                    return;
                }
                player.sendMessage("=== Your Enchantment Books ===");
                IntStream.range(0, books.size()).forEach(i -> {
                    EnchantingManager.EnchantmentBook b = books.get(i);
                    player.sendMessage(String.format("%d. %s (%s %d)",
                            i + 1, b.name(), b.enchantment().getDisplayName(), b.level()));
                });
            }
            case "apply" -> {
                // /enchanting book apply <index>
                if (args.length < 3) {
                    player.sendMessage("Usage: /enchanting book apply <index>");
                    return;
                }
                int index;
                try {
                    index = Integer.parseInt(args[2]) - 1;
                } catch (NumberFormatException e) {
                    player.sendMessage("Index must be a number.");
                    return;
                }
                try {
                    EnchantingManager.EnchantmentBook book =
                            enchantingManager.applyBook(player.getUniqueId(), index);
                    player.sendMessage("Applied \"" + book.name() + "\" — "
                            + book.enchantment().getDisplayName() + " " + book.level() + " is now active.");
                } catch (IndexOutOfBoundsException e) {
                    player.sendMessage("No book at that index. Use /enchanting book list to see your books.");
                }
            }
            default -> player.sendMessage("Usage: /enchanting book <add|list|apply>");
        }
    }

    private void handleHistory(Player player) {
        List<String> history = enchantingManager.getEnchantingHistory(player.getUniqueId());
        if (history.isEmpty()) {
            player.sendMessage("You have no enchanting history.");
            return;
        }
        player.sendMessage("=== Your Enchanting History ===");
        for (int i = 0; i < history.size(); i++) {
            player.sendMessage((i + 1) + ". " + history.get(i));
        }
    }

    private static EnchantingManager.SkyBlockEnchantment parseType(String name) {
        try {
            return EnchantingManager.SkyBlockEnchantment.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
