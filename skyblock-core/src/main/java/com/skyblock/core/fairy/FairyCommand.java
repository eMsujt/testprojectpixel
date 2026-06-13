package com.skyblock.core.fairy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class FairyCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("count", "add", "set", "reset");

    private final FairyManager fairyManager;

    public FairyCommand(FairyManager fairyManager) {
        this.fairyManager = fairyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /fairy <count|add|set|reset>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "count" -> handleCount(player);
            case "add"   -> handleAdd(player, args);
            case "set"   -> handleSet(player, args);
            case "reset" -> handleReset(player);
            default      -> player.sendMessage("Unknown subcommand. Usage: /fairy <count|add|set|reset>");
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

    private void handleCount(Player player) {
        int count = fairyManager.getCount(player.getUniqueId());
        player.sendMessage("Fairy Souls: " + count + " / " + FairyManager.MAX_SOULS);
    }

    private void handleAdd(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("Usage: /fairy add <amount>");
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount: must be an integer.");
            return;
        }
        if (amount < 0) {
            player.sendMessage("Amount must not be negative.");
            return;
        }
        int total = fairyManager.addSouls(player.getUniqueId(), amount);
        player.sendMessage("Fairy Souls: " + total + " / " + FairyManager.MAX_SOULS);
    }

    private void handleSet(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("Usage: /fairy set <count>");
            return;
        }
        int count;
        try {
            count = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid count: must be an integer.");
            return;
        }
        if (count < 0 || count > FairyManager.MAX_SOULS) {
            player.sendMessage("Count must be between 0 and " + FairyManager.MAX_SOULS + ".");
            return;
        }
        fairyManager.setCount(player.getUniqueId(), count);
        player.sendMessage("Fairy Souls set to " + count + " / " + FairyManager.MAX_SOULS + ".");
    }

    private void handleReset(Player player) {
        fairyManager.remove(player.getUniqueId());
        player.sendMessage("Fairy Souls reset to 0.");
    }
}
