package com.skyblock.core.accessory;

import com.skyblock.core.accessory.AccessoryManager.AccessoryRarity;
import com.skyblock.core.talisman.TalismanManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles the {@code /accessory} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /accessory list}                      — list accessories and their rarities</li>
 *   <li>{@code /accessory info <type>}               — show rarity and stat multiplier for an accessory</li>
 *   <li>{@code /accessory setrarity <type> <rarity>} — assign a rarity to an accessory</li>
 *   <li>{@code /accessory remove <type>}             — remove a rarity assignment from an accessory</li>
 * </ul>
 * </p>
 */
public final class AccessoryCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "info", "setrarity", "remove");

    private final AccessoryManager accessoryManager;

    public AccessoryCommand(AccessoryManager accessoryManager) {
        this.accessoryManager = accessoryManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /accessory <list|info|setrarity|remove>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"      -> handleList(player);
            case "info"      -> handleInfo(player, args);
            case "setrarity" -> handleSetRarity(player, args);
            case "remove"    -> handleRemove(player, args);
            default          -> player.sendMessage("Unknown subcommand. Usage: /accessory <list|info|setrarity|remove>");
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
            if (sub.equals("info") || sub.equals("setrarity") || sub.equals("remove")) {
                String prefix = args[1].toUpperCase();
                return Arrays.stream(TalismanManager.TalismanType.values())
                        .map(Enum::name)
                        .filter(n -> n.startsWith(prefix))
                        .sorted()
                        .collect(Collectors.toList());
            }
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("setrarity")) {
            String prefix = args[2].toUpperCase();
            return Arrays.stream(AccessoryRarity.values())
                    .map(Enum::name)
                    .filter(n -> n.startsWith(prefix))
                    .sorted()
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        Map<TalismanManager.TalismanType, AccessoryRarity> accessories =
                accessoryManager.getAccessories(player.getUniqueId());
        player.sendMessage("=== Accessories (" + accessories.size() + ") ===");
        if (accessories.isEmpty()) {
            player.sendMessage("No accessories assigned. Use /accessory setrarity <type> <rarity> to add one.");
            return;
        }
        accessories.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> player.sendMessage(String.format(
                        "  %s — %s (x%.1f)", e.getKey().name(), e.getValue().getDisplayName(),
                        e.getValue().getStatMultiplier())));
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /accessory info <type>");
            return;
        }
        TalismanManager.TalismanType type = parseType(args[1]);
        if (type == null) {
            player.sendMessage("Unknown accessory type: " + args[1]);
            return;
        }
        AccessoryRarity rarity = accessoryManager.getRarity(player.getUniqueId(), type);
        player.sendMessage(String.format("%s — Rarity: %s (x%.1f stat multiplier)",
                type.name(), rarity.getDisplayName(), rarity.getStatMultiplier()));
    }

    private void handleSetRarity(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /accessory setrarity <type> <rarity>");
            return;
        }
        TalismanManager.TalismanType type = parseType(args[1]);
        if (type == null) {
            player.sendMessage("Unknown accessory type: " + args[1]);
            return;
        }
        AccessoryRarity rarity = AccessoryRarity.fromName(args[2]);
        if (rarity == null) {
            player.sendMessage("Unknown rarity: " + args[2] + ". Valid rarities: " +
                    Arrays.stream(AccessoryRarity.values()).map(AccessoryRarity::name)
                            .collect(Collectors.joining(", ")));
            return;
        }
        accessoryManager.setRarity(player.getUniqueId(), type, rarity);
        player.sendMessage(String.format("Set %s rarity to %s (x%.1f).",
                type.name(), rarity.getDisplayName(), rarity.getStatMultiplier()));
    }

    private void handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /accessory remove <type>");
            return;
        }
        TalismanManager.TalismanType type = parseType(args[1]);
        if (type == null) {
            player.sendMessage("Unknown accessory type: " + args[1]);
            return;
        }
        if (accessoryManager.removeAccessory(player.getUniqueId(), type)) {
            player.sendMessage("Removed rarity assignment for " + type.name() + ".");
        } else {
            player.sendMessage(type.name() + " has no rarity assignment.");
        }
    }

    private static TalismanManager.TalismanType parseType(String name) {
        try {
            return TalismanManager.TalismanType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
