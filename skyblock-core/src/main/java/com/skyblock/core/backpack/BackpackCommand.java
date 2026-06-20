package com.skyblock.core.backpack;

import com.skyblock.core.menu.BackpackMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /backpack} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /backpack tier}            — show your current tier and capacity</li>
 *   <li>{@code /backpack upgrade <tier>}  — (op) set your backpack tier</li>
 *   <li>{@code /backpack add <item>}      — add an item name to your backpack</li>
 *   <li>{@code /backpack remove <item>}   — remove an item name from your backpack</li>
 *   <li>{@code /backpack list}            — list items in your backpack</li>
 * </ul>
 * </p>
 */
public final class BackpackCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("tier", "upgrade", "add", "remove", "list");

    private final BackpackManager backpackManager;

    public BackpackCommand(BackpackManager backpackManager) {
        this.backpackManager = backpackManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            new BackpackMenu(player).open(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "tier"    -> handleTier(player);
            case "upgrade" -> handleUpgrade(player, args);
            case "add"     -> handleAdd(player, args);
            case "remove"  -> handleRemove(player, args);
            case "list"    -> handleList(player);
            default        -> sendHelp(player);
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
        if (args.length == 2 && args[0].equalsIgnoreCase("upgrade")) {
            String prefix = args[1].toUpperCase();
            return Arrays.stream(BackpackManager.BackpackTier.values())
                    .map(Enum::name)
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleTier(Player player) {
        BackpackManager.BackpackTier tier = backpackManager.getTier(player.getUniqueId());
        int used = backpackManager.getItems(player.getUniqueId()).size();
        player.sendMessage("Backpack tier: " + tier.name() + " (" + used + "/" + tier.slots + " slots used)");
    }

    private void handleUpgrade(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("Usage: /backpack upgrade <SMALL|MEDIUM|LARGE|JUMBO>");
            return;
        }
        BackpackManager.BackpackTier tier;
        try {
            tier = BackpackManager.BackpackTier.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown tier: " + args[1] + ". Valid: SMALL, MEDIUM, LARGE, JUMBO");
            return;
        }
        backpackManager.setTier(player.getUniqueId(), tier);
        player.sendMessage("Backpack upgraded to " + tier.name() + " (" + tier.slots + " slots).");
    }

    private void handleAdd(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /backpack add <item>");
            return;
        }
        String itemName = args[1];
        boolean added = backpackManager.addItem(player.getUniqueId(), itemName);
        if (added) {
            player.sendMessage("Added '" + itemName + "' to your backpack.");
        } else {
            BackpackManager.BackpackTier tier = backpackManager.getTier(player.getUniqueId());
            player.sendMessage("Your backpack is full (" + tier.slots + "/" + tier.slots + " slots).");
        }
    }

    private void handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /backpack remove <item>");
            return;
        }
        String itemName = args[1];
        boolean removed = backpackManager.removeItem(player.getUniqueId(), itemName);
        if (removed) {
            player.sendMessage("Removed '" + itemName + "' from your backpack.");
        } else {
            player.sendMessage("Item '" + itemName + "' not found in your backpack.");
        }
    }

    private void handleList(Player player) {
        List<String> items = backpackManager.getItems(player.getUniqueId());
        BackpackManager.BackpackTier tier = backpackManager.getTier(player.getUniqueId());
        if (items.isEmpty()) {
            player.sendMessage("Your backpack is empty. (Tier: " + tier.name() + ", Capacity: " + tier.slots + ")");
            return;
        }
        player.sendMessage("=== Backpack (" + items.size() + "/" + tier.slots + " slots, Tier: " + tier.name() + ") ===");
        for (int i = 0; i < items.size(); i++) {
            player.sendMessage("  " + (i + 1) + ". " + items.get(i));
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Backpack Commands ===");
        player.sendMessage("/backpack tier                    — view your tier and capacity");
        player.sendMessage("/backpack upgrade <tier>          — (op) set your backpack tier");
        player.sendMessage("/backpack add <item>              — add an item to your backpack");
        player.sendMessage("/backpack remove <item>           — remove an item from your backpack");
        player.sendMessage("/backpack list                    — list items in your backpack");
    }
}
