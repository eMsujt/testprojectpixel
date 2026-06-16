package com.skyblock.core.hotm.command;

import com.skyblock.core.hotm.manager.HOTMManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /hotmtree} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /hotmtree view [perk]}        — show level for one or all perks</li>
 *   <li>{@code /hotmtree upgrade <perk>}     — (op) upgrade a perk by one level</li>
 *   <li>{@code /hotmtree set <perk> <level>} — (op) set a perk to an exact level</li>
 *   <li>{@code /hotmtree reset}              — (op) reset all perks to zero</li>
 * </ul>
 * </p>
 */
public final class HOTMCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("view", "upgrade", "set", "reset", "powder", "history");
    private static final List<String> PERK_NAMES = Arrays.stream(HOTMManager.HOTMPerk.values())
            .map(p -> p.name().toLowerCase())
            .collect(Collectors.toList());

    private final HOTMManager hotmManager;

    public HOTMCommand(HOTMManager hotmManager) {
        this.hotmManager = hotmManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /hotmtree <view|upgrade|set|reset|powder>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "view"    -> handleView(player, args);
            case "upgrade" -> handleUpgrade(player, args);
            case "set"     -> handleSet(player, args);
            case "reset"   -> handleReset(player);
            case "powder"  -> handlePowder(player, args);
            case "history" -> handleHistory(player);
            default        -> player.sendMessage("Unknown subcommand. Usage: /hotmtree <view|upgrade|set|reset|powder|history>");
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
            if (sub.equals("view") || sub.equals("upgrade") || sub.equals("set")) {
                String prefix = args[1].toLowerCase();
                return PERK_NAMES.stream()
                        .filter(p -> p.startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private void handleView(Player player, String[] args) {
        if (args.length >= 2) {
            HOTMManager.HOTMPerk perk = parsePerk(player, args[1]);
            if (perk == null) return;
            int level = hotmManager.getLevel(player.getUniqueId(), perk);
            player.sendMessage(perk.getDisplayName() + ": " + level + "/" + perk.maxLevel);
        } else {
            player.sendMessage("=== Heart of the Mountain ===");
            for (HOTMManager.HOTMPerk perk : HOTMManager.HOTMPerk.values()) {
                int level = hotmManager.getLevel(player.getUniqueId(), perk);
                player.sendMessage(perk.getDisplayName() + ": " + level + "/" + perk.maxLevel);
            }
        }
    }

    private void handleUpgrade(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("Usage: /hotmtree upgrade <perk>");
            return;
        }
        HOTMManager.HOTMPerk perk = parsePerk(player, args[1]);
        if (perk == null) return;
        int newLevel = hotmManager.upgrade(player.getUniqueId(), perk);
        if (newLevel == -1) {
            player.sendMessage(perk.getDisplayName() + " is already at max level (" + perk.maxLevel + ").");
        } else {
            player.sendMessage("Upgraded " + perk.getDisplayName() + " to level " + newLevel + ".");
        }
    }

    private void handleSet(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 3) {
            player.sendMessage("Usage: /hotmtree set <perk> <level>");
            return;
        }
        HOTMManager.HOTMPerk perk = parsePerk(player, args[1]);
        if (perk == null) return;
        int level = parseLevel(player, args[2]);
        if (level < 0) return;
        hotmManager.setLevel(player.getUniqueId(), perk, level);
        int actual = hotmManager.getLevel(player.getUniqueId(), perk);
        player.sendMessage(perk.getDisplayName() + " set to " + actual + ".");
    }

    private void handleReset(Player player) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        hotmManager.reset(player.getUniqueId());
        player.sendMessage("All Heart of the Mountain perks have been reset.");
    }

    private void handlePowder(Player player, String[] args) {
        if (args.length == 1) {
            long balance = hotmManager.getMithrilPowder(player.getUniqueId());
            player.sendMessage("Mithril Powder: " + balance);
            return;
        }
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 3 || (!args[1].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("spend"))) {
            player.sendMessage("Usage: /hotmtree powder [add|spend <amount>]");
            return;
        }
        long amount;
        try {
            amount = Long.parseLong(args[2]);
            if (amount < 0) {
                player.sendMessage("Amount must not be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount: " + args[2]);
            return;
        }
        if (args[1].equalsIgnoreCase("add")) {
            hotmManager.addMithrilPowder(player.getUniqueId(), amount);
            player.sendMessage("Added " + amount + " Mithril Powder. Balance: " + hotmManager.getMithrilPowder(player.getUniqueId()));
        } else {
            boolean success = hotmManager.spendMithrilPowder(player.getUniqueId(), amount);
            if (success) {
                player.sendMessage("Spent " + amount + " Mithril Powder. Balance: " + hotmManager.getMithrilPowder(player.getUniqueId()));
            } else {
                player.sendMessage("Insufficient Mithril Powder (have " + hotmManager.getMithrilPowder(player.getUniqueId()) + ", need " + amount + ").");
            }
        }
    }

    private void handleHistory(Player player) {
        List<String> history = hotmManager.getHotmHistory(player.getUniqueId());
        player.sendMessage("=== HOTM History ===");
        if (history.isEmpty()) {
            player.sendMessage("No HOTM events recorded.");
            return;
        }
        for (int i = 0; i < history.size(); i++) {
            player.sendMessage((i + 1) + ". " + history.get(i));
        }
    }

    private HOTMManager.HOTMPerk parsePerk(Player player, String input) {
        try {
            return HOTMManager.HOTMPerk.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown perk: " + input + ". Valid perks: " + String.join(", ", PERK_NAMES));
            return null;
        }
    }

    private int parseLevel(Player player, String input) {
        try {
            int level = Integer.parseInt(input);
            if (level < 0) {
                player.sendMessage("Level must not be negative.");
                return -1;
            }
            return level;
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid level: " + input);
            return -1;
        }
    }
}
