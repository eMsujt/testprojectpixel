package com.skyblock.core.accessory.command;

import com.skyblock.core.accessory.manager.AccessoryBagManager;
import com.skyblock.core.model.AccessoryRarity;
import com.skyblock.core.model.Stat;
import com.skyblock.core.talisman.manager.TalismanManager;
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
 * Handles the {@code /accessorybag} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /accessorybag list}           — list all accessories in your bag</li>
 *   <li>{@code /accessorybag add <type>}     — add an accessory to your bag</li>
 *   <li>{@code /accessorybag remove <type>}  — remove an accessory from your bag</li>
 *   <li>{@code /accessorybag bonuses}        — show total stat bonuses from bag contents</li>
 * </ul>
 * </p>
 */
public final class AccessoryBagCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "add", "remove", "bonuses", "rarity", "tier");

    private final AccessoryBagManager accessoryBagManager;

    public AccessoryBagCommand(AccessoryBagManager accessoryBagManager) {
        this.accessoryBagManager = accessoryBagManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /accessorybag <list|add|remove|bonuses|rarity|tier>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"    -> handleList(player);
            case "add"     -> handleAdd(player, args);
            case "remove"  -> handleRemove(player, args);
            case "bonuses" -> handleBonuses(player);
            case "rarity"  -> handleRarity(player, args);
            case "tier"    -> handleTier(player, args);
            default        -> player.sendMessage("Unknown subcommand. Usage: /accessorybag <list|add|remove|bonuses|rarity|tier>");
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
            if (sub.equals("add") || sub.equals("remove")) {
                String prefix = args[1].toUpperCase();
                return Arrays.stream(TalismanManager.TalismanType.values())
                        .map(Enum::name)
                        .filter(n -> n.startsWith(prefix))
                        .sorted()
                        .collect(Collectors.toList());
            }
            if (sub.equals("rarity")) {
                String prefix = args[1].toUpperCase();
                return Arrays.stream(AccessoryRarity.values())
                        .map(Enum::name)
                        .filter(n -> n.startsWith(prefix))
                        .sorted()
                        .collect(Collectors.toList());
            }
            if (sub.equals("tier")) {
                String prefix = args[1].toUpperCase();
                return Arrays.stream(AccessoryBagManager.AccessoryTier.values())
                        .map(Enum::name)
                        .filter(n -> n.startsWith(prefix))
                        .sorted()
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        var contents = accessoryBagManager.getContents(player.getUniqueId());
        int size = accessoryBagManager.getSize(player.getUniqueId());
        player.sendMessage(String.format("=== Accessory Bag (%d/%d) ===", size, AccessoryBagManager.MAX_SLOTS));
        if (contents.isEmpty()) {
            player.sendMessage("Your accessory bag is empty. Use /accessorybag add <type> to add accessories.");
            return;
        }
        contents.stream()
                .sorted((a, b) -> a.name().compareTo(b.name()))
                .forEach(t -> player.sendMessage(String.format("  %s — +%.1f %s", t.name(), t.bonus, t.stat.name())));
    }

    private void handleAdd(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /accessorybag add <type>");
            return;
        }
        TalismanManager.TalismanType type = parseType(args[1]);
        if (type == null) {
            player.sendMessage("Unknown accessory type: " + args[1] + ". Use /accessorybag add for available types.");
            return;
        }
        if (accessoryBagManager.hasAccessory(player.getUniqueId(), type)) {
            player.sendMessage(type.name() + " is already in your accessory bag.");
            return;
        }
        if (accessoryBagManager.getSize(player.getUniqueId()) >= AccessoryBagManager.MAX_SLOTS) {
            player.sendMessage("Your accessory bag is full (" + AccessoryBagManager.MAX_SLOTS + "/" + AccessoryBagManager.MAX_SLOTS + ").");
            return;
        }
        accessoryBagManager.addAccessory(player.getUniqueId(), type);
        player.sendMessage(String.format("Added %s to your accessory bag (+%.1f %s).", type.name(), type.bonus, type.stat.name()));
    }

    private void handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /accessorybag remove <type>");
            return;
        }
        TalismanManager.TalismanType type = parseType(args[1]);
        if (type == null) {
            player.sendMessage("Unknown accessory type: " + args[1] + ". Use /accessorybag list to see your accessories.");
            return;
        }
        if (accessoryBagManager.removeAccessory(player.getUniqueId(), type)) {
            player.sendMessage("Removed " + type.name() + " from your accessory bag.");
        } else {
            player.sendMessage(type.name() + " is not in your accessory bag.");
        }
    }

    private void handleBonuses(Player player) {
        Map<Stat, Double> bonuses = accessoryBagManager.getTotalBonuses(player.getUniqueId());
        if (bonuses.isEmpty()) {
            player.sendMessage("Your accessory bag is empty. Add accessories with /accessorybag add <type>.");
            return;
        }
        player.sendMessage("=== Accessory Bag Bonuses ===");
        bonuses.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> player.sendMessage(String.format("+%.1f %s", e.getValue(), e.getKey().name())));
    }

    private void handleRarity(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /accessorybag rarity <COMMON|UNCOMMON|RARE|EPIC|LEGENDARY|MYTHIC|SPECIAL|VERY_SPECIAL>");
            return;
        }
        AccessoryRarity rarity;
        try {
            rarity = AccessoryRarity.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown rarity: " + args[1] + ". Valid values: COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC, SPECIAL, VERY_SPECIAL.");
            return;
        }
        var contents = accessoryBagManager.getContentsByRarity(player.getUniqueId(), rarity);
        player.sendMessage(String.format("=== %s Accessories (x%.1f multiplier) ===",
                rarity.getDisplayName(), rarity.statMultiplier));
        if (contents.isEmpty()) {
            player.sendMessage("You have no " + rarity.getDisplayName() + " accessories in your bag.");
            return;
        }
        contents.stream()
                .sorted((a, b) -> a.name().compareTo(b.name()))
                .forEach(t -> player.sendMessage(String.format("  %s — +%.1f %s", t.name(), t.bonus, t.stat.name())));
    }

    private void handleTier(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /accessorybag tier <COMMON|UNCOMMON|RARE|EPIC|LEGENDARY|MYTHIC|SPECIAL>");
            return;
        }
        AccessoryBagManager.AccessoryTier tier;
        try {
            tier = AccessoryBagManager.AccessoryTier.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown tier: " + args[1] + ". Valid values: COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC, SPECIAL.");
            return;
        }
        int magicPower = accessoryBagManager.getMagicPower(player.getUniqueId(), tier);
        player.sendMessage(String.format("=== %s Tier (%d magic power each) ===",
                tier.getDisplayName(), tier.magicPower));
        player.sendMessage(String.format("Total magic power from %s accessories: %d",
                tier.getDisplayName(), magicPower));
    }

    private static TalismanManager.TalismanType parseType(String name) {
        try {
            return TalismanManager.TalismanType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
