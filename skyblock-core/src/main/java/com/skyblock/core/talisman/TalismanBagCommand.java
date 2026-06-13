package com.skyblock.core.talisman;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /talismanbag} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /talismanbag info}           — show bag capacity and current usage</li>
 *   <li>{@code /talismanbag list}            — list all accessories in the bag</li>
 *   <li>{@code /talismanbag add <type>}      — add an accessory to the bag</li>
 *   <li>{@code /talismanbag remove <type>}   — remove an accessory from the bag</li>
 *   <li>{@code /talismanbag rarity [type]}   — list rarity tiers or show a specific accessory's rarity</li>
 * </ul>
 * </p>
 */
public final class TalismanBagCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("info", "list", "add", "remove", "rarity");

    private final TalismanBagManager talismanBagManager;

    public TalismanBagCommand(TalismanBagManager talismanBagManager) {
        this.talismanBagManager = talismanBagManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /talismanbag <info|list|add|remove|rarity>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"   -> handleInfo(player);
            case "list"   -> handleList(player);
            case "add"    -> handleAdd(player, args);
            case "remove" -> handleRemove(player, args);
            case "rarity" -> handleRarity(player, args);
            default       -> player.sendMessage("Unknown subcommand. Usage: /talismanbag <info|list|add|remove|rarity>");
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
        if (args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")
                || args[0].equalsIgnoreCase("rarity"))) {
            String prefix = args[1].toUpperCase();
            return Arrays.stream(TalismanManager.TalismanType.values())
                    .map(Enum::name)
                    .filter(n -> n.startsWith(prefix))
                    .sorted()
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleInfo(Player player) {
        int count = talismanBagManager.getCount(player.getUniqueId());
        player.sendMessage(String.format("=== Talisman Bag ==="));
        player.sendMessage(String.format("Slots used: %d / %d", count, TalismanBagManager.DEFAULT_CAPACITY));
    }

    private void handleList(Player player) {
        List<TalismanManager.TalismanType> contents = talismanBagManager.getContents(player.getUniqueId());
        if (contents.isEmpty()) {
            player.sendMessage("Your talisman bag is empty.");
            return;
        }
        player.sendMessage("=== Talisman Bag Contents ===");
        contents.stream()
                .sorted((a, b) -> a.name().compareTo(b.name()))
                .forEach(t -> player.sendMessage(String.format("%s [%s] — +%.1f %s",
                        t.name(), t.rarity.getDisplayName(), t.bonus, t.stat.name())));
    }

    private void handleAdd(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /talismanbag add <type>");
            return;
        }
        TalismanManager.TalismanType type = parseTalismanType(args[1]);
        if (type == null) {
            player.sendMessage("Unknown accessory type: " + args[1] + ". Use /talisman list to see available types.");
            return;
        }
        if (talismanBagManager.addToBag(player.getUniqueId(), type)) {
            player.sendMessage("Added " + type.name() + " to your talisman bag.");
        } else {
            player.sendMessage("Your talisman bag is full (" + TalismanBagManager.DEFAULT_CAPACITY + " slots).");
        }
    }

    private void handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /talismanbag remove <type>");
            return;
        }
        TalismanManager.TalismanType type = parseTalismanType(args[1]);
        if (type == null) {
            player.sendMessage("Unknown accessory type: " + args[1] + ". Use /talisman list to see available types.");
            return;
        }
        if (talismanBagManager.removeFromBag(player.getUniqueId(), type)) {
            player.sendMessage("Removed " + type.name() + " from your talisman bag.");
        } else {
            player.sendMessage("Your talisman bag does not contain " + type.name() + ".");
        }
    }

    private void handleRarity(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("=== Accessory Rarities ===");
            for (TalismanManager.AccessoryRarity rarity : TalismanManager.AccessoryRarity.values()) {
                player.sendMessage(String.format("%s — %.1fx stat multiplier", rarity.getDisplayName(), rarity.statMultiplier));
            }
            return;
        }
        TalismanManager.TalismanType type = parseTalismanType(args[1]);
        if (type == null) {
            player.sendMessage("Unknown accessory type: " + args[1] + ". Use /talisman list to see available types.");
            return;
        }
        player.sendMessage(String.format("%s is %s (%.1fx multiplier).",
                type.name(), type.rarity.getDisplayName(), type.rarity.statMultiplier));
    }

    private static TalismanManager.TalismanType parseTalismanType(String name) {
        try {
            return TalismanManager.TalismanType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
