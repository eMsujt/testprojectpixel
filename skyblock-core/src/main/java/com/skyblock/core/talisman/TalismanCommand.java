package com.skyblock.core.talisman;

import com.skyblock.core.stat.Stat;
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
 * Handles the {@code /talisman} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /talisman list}           — list all available talisman types</li>
 *   <li>{@code /talisman equip <type>}   — equip a talisman</li>
 *   <li>{@code /talisman unequip <type>} — unequip a talisman</li>
 *   <li>{@code /talisman equipped}       — show your currently equipped talismans</li>
 *   <li>{@code /talisman bonuses}        — show your total stat bonuses</li>
 * </ul>
 * </p>
 */
public final class TalismanCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("list", "equip", "unequip", "equipped", "bonuses", "rarity");

    private final TalismanManager talismanManager;

    public TalismanCommand(TalismanManager talismanManager) {
        this.talismanManager = talismanManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /talisman <list|equip|unequip|equipped|bonuses|rarity>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"     -> handleList(player);
            case "equip"    -> handleEquip(player, args);
            case "unequip"  -> handleUnequip(player, args);
            case "equipped" -> handleEquipped(player);
            case "bonuses"  -> handleBonuses(player);
            case "rarity"   -> handleRarity(player, args);
            default         -> player.sendMessage("Unknown subcommand. Usage: /talisman <list|equip|unequip|equipped|bonuses|rarity>");
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
        if (args.length == 2 && (args[0].equalsIgnoreCase("equip") || args[0].equalsIgnoreCase("unequip")
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

    private void handleList(Player player) {
        player.sendMessage("=== Available Talismans ===");
        for (TalismanManager.TalismanType type : TalismanManager.TalismanType.values()) {
            player.sendMessage(String.format("%s [%s] — +%.1f %s",
                    type.name(), type.rarity.getDisplayName(), type.bonus, type.stat.name()));
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
            player.sendMessage("Unknown talisman type: " + args[1] + ". Use /talisman list to see available types.");
            return;
        }
        player.sendMessage(String.format("%s is %s (%.1fx multiplier).",
                type.name(), type.rarity.getDisplayName(), type.rarity.statMultiplier));
    }

    private void handleEquip(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /talisman equip <type>");
            return;
        }
        TalismanManager.TalismanType type = parseTalismanType(args[1]);
        if (type == null) {
            player.sendMessage("Unknown talisman type: " + args[1] + ". Use /talisman list to see available types.");
            return;
        }
        if (talismanManager.equip(player.getUniqueId(), type)) {
            player.sendMessage("Equipped " + type.name() + " (+%.1f %s).".formatted(type.bonus, type.stat.name()));
        } else {
            player.sendMessage("You already have " + type.name() + " equipped.");
        }
    }

    private void handleUnequip(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /talisman unequip <type>");
            return;
        }
        TalismanManager.TalismanType type = parseTalismanType(args[1]);
        if (type == null) {
            player.sendMessage("Unknown talisman type: " + args[1] + ". Use /talisman list to see available types.");
            return;
        }
        if (talismanManager.unequip(player.getUniqueId(), type)) {
            player.sendMessage("Unequipped " + type.name() + ".");
        } else {
            player.sendMessage("You do not have " + type.name() + " equipped.");
        }
    }

    private void handleEquipped(Player player) {
        var equipped = talismanManager.getEquipped(player.getUniqueId());
        if (equipped.isEmpty()) {
            player.sendMessage("You have no talismans equipped.");
            return;
        }
        player.sendMessage("=== Your Equipped Talismans ===");
        equipped.stream()
                .sorted((a, b) -> a.name().compareTo(b.name()))
                .forEach(t -> player.sendMessage(String.format("%s — +%.1f %s", t.name(), t.bonus, t.stat.name())));
    }

    private void handleBonuses(Player player) {
        Map<Stat, Double> bonuses = talismanManager.getTotalBonuses(player.getUniqueId());
        if (bonuses.isEmpty()) {
            player.sendMessage("You have no talisman bonuses. Equip talismans with /talisman equip <type>.");
            return;
        }
        player.sendMessage("=== Your Talisman Bonuses ===");
        bonuses.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> player.sendMessage(String.format("+%.1f %s", e.getValue(), e.getKey().name())));
    }

    private static TalismanManager.TalismanType parseTalismanType(String name) {
        try {
            return TalismanManager.TalismanType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
