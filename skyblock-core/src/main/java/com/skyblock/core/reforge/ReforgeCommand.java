package com.skyblock.core.reforge;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles the {@code /reforge} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /reforge}              — show current reforge and its bonuses</li>
 *   <li>{@code /reforge list}         — list all available reforges</li>
 *   <li>{@code /reforge info <name>}  — show stat bonuses for a reforge</li>
 *   <li>{@code /reforge apply <name>} — apply a reforge to yourself</li>
 *   <li>{@code /reforge clear}        — remove the current reforge</li>
 * </ul>
 * </p>
 */
public final class ReforgeCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "info", "apply", "clear", "stone");
    private static final List<String> STONE_SUBCOMMANDS = Arrays.asList("list", "info");

    private final ReforgeManager reforgeManager;

    public ReforgeCommand(ReforgeManager reforgeManager) {
        if (reforgeManager == null) {
            throw new IllegalArgumentException("reforgeManager must not be null");
        }
        this.reforgeManager = reforgeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleStatus(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"  -> handleList(player);
            case "info"  -> handleInfo(player, args);
            case "apply" -> handleApply(player, args);
            case "clear" -> handleClear(player);
            case "stone" -> handleStone(player, args);
            default      -> sendHelp(player);
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
        if (args.length == 2 && (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("apply"))) {
            String prefix = args[1].toLowerCase();
            return Arrays.stream(ReforgeManager.ReforgeType.values())
                    .filter(r -> r != ReforgeManager.ReforgeType.NONE)
                    .map(ReforgeManager.ReforgeType::getDisplayName)
                    .filter(n -> n.toLowerCase().startsWith(prefix))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("stone")) {
            String prefix = args[1].toLowerCase();
            return STONE_SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("stone") && args[1].equalsIgnoreCase("info")) {
            String prefix = args[2].toLowerCase();
            return Arrays.stream(ReforgeManager.ReforgeStone.values())
                    .map(ReforgeManager.ReforgeStone::getDisplayName)
                    .filter(n -> n.toLowerCase().startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleStatus(Player player) {
        UUID id = player.getUniqueId();
        ReforgeManager.ReforgeType reforge = reforgeManager.getReforge(id);
        player.sendMessage("=== Reforge ===");
        player.sendMessage("Current: " + reforge.getDisplayName());
        if (reforge != ReforgeManager.ReforgeType.NONE) {
            printBonuses(player, reforge);
        }
    }

    private void handleList(Player player) {
        player.sendMessage("=== Available Reforges ===");
        for (ReforgeManager.ReforgeType r : ReforgeManager.ReforgeType.values()) {
            if (r == ReforgeManager.ReforgeType.NONE) continue;
            player.sendMessage("  " + r.getDisplayName()
                    + " [STR+" + r.getStrengthBonus()
                    + " DEF+" + r.getDefenseBonus()
                    + " SPD+" + r.getSpeedBonus() + "]");
        }
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /reforge info <name>");
            return;
        }
        ReforgeManager.ReforgeType reforge = ReforgeManager.ReforgeType.fromName(args[1]);
        if (reforge == null || reforge == ReforgeManager.ReforgeType.NONE) {
            player.sendMessage("Unknown reforge: " + args[1]);
            return;
        }
        player.sendMessage("=== " + reforge.getDisplayName() + " ===");
        printBonuses(player, reforge);
    }

    private void handleApply(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /reforge apply <name>");
            return;
        }
        ReforgeManager.ReforgeType reforge = ReforgeManager.ReforgeType.fromName(args[1]);
        if (reforge == null || reforge == ReforgeManager.ReforgeType.NONE) {
            player.sendMessage("Unknown reforge: " + args[1]);
            return;
        }
        reforgeManager.setReforge(player.getUniqueId(), reforge);
        player.sendMessage("Applied reforge: " + reforge.getDisplayName());
        printBonuses(player, reforge);
    }

    private void handleClear(Player player) {
        reforgeManager.clearReforge(player.getUniqueId());
        player.sendMessage("Reforge cleared.");
    }

    private void handleStone(Player player, String[] args) {
        if (args.length < 2 || args[1].equalsIgnoreCase("list")) {
            player.sendMessage("=== Reforge Stones ===");
            for (ReforgeManager.ReforgeStone stone : ReforgeManager.ReforgeStone.values()) {
                player.sendMessage("  " + stone.getDisplayName() + " → " + stone.getReforge());
            }
            return;
        }
        if (args[1].equalsIgnoreCase("info")) {
            if (args.length < 3) {
                player.sendMessage("Usage: /reforge stone info <stone>");
                return;
            }
            ReforgeManager.ReforgeStone stone = ReforgeManager.ReforgeStone.fromName(args[2]);
            if (stone == null) {
                player.sendMessage("Unknown reforge stone: " + args[2]);
                return;
            }
            player.sendMessage("=== " + stone.getDisplayName() + " ===");
            player.sendMessage("  Applies reforge: " + stone.getReforge());
            return;
        }
        player.sendMessage("Usage: /reforge stone [list|info <stone>]");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Reforge Commands ===");
        player.sendMessage("/reforge                       — show current reforge and bonuses");
        player.sendMessage("/reforge list                  — list all available reforges");
        player.sendMessage("/reforge info <name>           — show stat bonuses for a reforge");
        player.sendMessage("/reforge apply <name>          — apply a reforge");
        player.sendMessage("/reforge clear                 — remove the current reforge");
        player.sendMessage("/reforge stone [list]          — list all reforge stones");
        player.sendMessage("/reforge stone info <stone>    — show which reforge a stone applies");
    }

    private static void printBonuses(Player player, ReforgeManager.ReforgeType reforge) {
        player.sendMessage("  Strength: +" + reforge.getStrengthBonus());
        player.sendMessage("  Defense:  +" + reforge.getDefenseBonus());
        player.sendMessage("  Speed:    +" + reforge.getSpeedBonus());
    }
}
