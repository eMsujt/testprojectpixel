package com.skyblock.core.essence;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /essence} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /essence balance [type]}     — show balance for one or all essence types</li>
 *   <li>{@code /essence add <type> <amount>} — (op) grant essence to yourself</li>
 *   <li>{@code /essence remove <type> <amount>} — (op) deduct essence from yourself</li>
 * </ul>
 * </p>
 */
public final class EssenceCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("balance", "add", "remove");
    private static final List<String> TYPE_NAMES = Arrays.stream(EssenceManager.EssenceType.values())
            .map(t -> t.name().toLowerCase())
            .collect(Collectors.toList());

    private final EssenceManager essenceManager;

    public EssenceCommand(EssenceManager essenceManager) {
        this.essenceManager = essenceManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /essence <balance|add|remove>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "balance" -> handleBalance(player, args);
            case "add"     -> handleAdd(player, args);
            case "remove"  -> handleRemove(player, args);
            default        -> player.sendMessage("Unknown subcommand. Usage: /essence <balance|add|remove>");
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
            if (sub.equals("balance") || sub.equals("add") || sub.equals("remove")) {
                String prefix = args[1].toLowerCase();
                return TYPE_NAMES.stream()
                        .filter(t -> t.startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private void handleBalance(Player player, String[] args) {
        if (args.length >= 2) {
            EssenceManager.EssenceType type = parseType(player, args[1]);
            if (type == null) return;
            int balance = essenceManager.getBalance(player.getUniqueId(), type);
            player.sendMessage(type.getDisplayName() + " Essence: " + balance);
        } else {
            player.sendMessage("=== Essence Balances ===");
            for (EssenceManager.EssenceType type : EssenceManager.EssenceType.values()) {
                int balance = essenceManager.getBalance(player.getUniqueId(), type);
                player.sendMessage(type.getDisplayName() + " Essence: " + balance);
            }
        }
    }

    private void handleAdd(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 3) {
            player.sendMessage("Usage: /essence add <type> <amount>");
            return;
        }
        EssenceManager.EssenceType type = parseType(player, args[1]);
        if (type == null) return;
        int amount = parseAmount(player, args[2]);
        if (amount <= 0) return;
        int newBalance = essenceManager.addEssence(player.getUniqueId(), type, amount);
        player.sendMessage("Added " + amount + " " + type.getDisplayName()
                + " Essence. New balance: " + newBalance);
    }

    private void handleRemove(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 3) {
            player.sendMessage("Usage: /essence remove <type> <amount>");
            return;
        }
        EssenceManager.EssenceType type = parseType(player, args[1]);
        if (type == null) return;
        int amount = parseAmount(player, args[2]);
        if (amount <= 0) return;
        boolean success = essenceManager.removeEssence(player.getUniqueId(), type, amount);
        if (success) {
            int remaining = essenceManager.getBalance(player.getUniqueId(), type);
            player.sendMessage("Removed " + amount + " " + type.getDisplayName()
                    + " Essence. Remaining: " + remaining);
        } else {
            player.sendMessage("Insufficient " + type.getDisplayName() + " Essence.");
        }
    }

    /** Parses an essence type name, sending an error to the player on failure. */
    private EssenceManager.EssenceType parseType(Player player, String input) {
        try {
            return EssenceManager.EssenceType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown essence type: " + input
                    + ". Valid types: " + String.join(", ", TYPE_NAMES));
            return null;
        }
    }

    /** Parses a positive integer amount, sending an error to the player on failure. */
    private int parseAmount(Player player, String input) {
        try {
            int amount = Integer.parseInt(input);
            if (amount <= 0) {
                player.sendMessage("Amount must be a positive number.");
                return 0;
            }
            return amount;
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount: " + input);
            return 0;
        }
    }

}
