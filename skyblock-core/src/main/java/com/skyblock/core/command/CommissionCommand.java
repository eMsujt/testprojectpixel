package com.skyblock.core.command;

import com.skyblock.core.manager.CommissionManager;
import com.skyblock.core.manager.CommissionManager.Commission;
import com.skyblock.core.manager.CommissionManager.CommissionLocation;
import com.skyblock.core.manager.CommissionManager.CommissionType;
import com.skyblock.core.menu.MiningCommissionMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class CommissionCommand extends PlayerCommand {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "generate", "claim", "progress", "stats");
    private static final List<String> LOCATION_NAMES = Arrays.stream(CommissionLocation.values())
            .map(l -> l.name().toLowerCase())
            .collect(Collectors.toList());
    private static final List<String> TYPE_NAMES = Arrays.stream(CommissionType.values())
            .map(t -> t.name().toLowerCase())
            .collect(Collectors.toList());

    private final CommissionManager commissionManager;

    public CommissionCommand(CommissionManager commissionManager) {
        this.commissionManager = commissionManager;
    }

    @Override
    protected void openMenu(Player player) {
        new MiningCommissionMenu(player).open(player);
    }

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            openMenu(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"     -> handleList(player);
            case "generate" -> handleGenerate(player, args);
            case "claim"    -> handleClaim(player, args);
            case "progress" -> handleProgress(player, args);
            case "stats"    -> handleStats(player);
            default         -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toList());
        }
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("generate")) {
                String prefix = args[1].toLowerCase();
                return LOCATION_NAMES.stream().filter(l -> l.startsWith(prefix)).collect(Collectors.toList());
            }
            if (sub.equals("claim") || sub.equals("progress")) {
                String prefix = args[1].toLowerCase();
                return TYPE_NAMES.stream().filter(t -> t.startsWith(prefix)).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        List<Commission> active = commissionManager.getActiveCommissions(player.getUniqueId());
        player.sendMessage("=== King's Commissions ===");
        if (active.isEmpty()) {
            player.sendMessage("No active commissions. Use /commission generate <location> to get some.");
            return;
        }
        for (Commission c : active) {
            String status = c.isComplete() ? " §a[COMPLETE — /commission claim " + c.getType().name().toLowerCase() + "]" : "";
            player.sendMessage("  §e" + c.getType().getDisplayName()
                    + " §7(" + c.getType().getLocation().getDisplayName() + "): "
                    + c.getProgress() + "/" + c.getType().getTarget() + status);
        }
    }

    private void handleGenerate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /commission generate <" + String.join("|", LOCATION_NAMES) + ">");
            return;
        }
        CommissionLocation location;
        try {
            location = CommissionLocation.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown location: " + args[1] + ". Valid: " + String.join(", ", LOCATION_NAMES));
            return;
        }
        List<Commission> assigned = commissionManager.generateCommissions(player.getUniqueId(), location);
        player.sendMessage("Assigned " + assigned.size() + " commission(s) from " + location.getDisplayName() + ":");
        for (Commission c : assigned) {
            player.sendMessage("  §e" + c.getType().getDisplayName() + " §7(target: " + c.getType().getTarget() + ")");
        }
    }

    private void handleClaim(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /commission claim <type>");
            return;
        }
        CommissionType type;
        try {
            type = CommissionType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown commission type: " + args[1]);
            return;
        }
        boolean claimed = commissionManager.claimCommission(player.getUniqueId(), type);
        if (claimed) {
            player.sendMessage("§aClaimed commission: " + type.getDisplayName() + "!");
        } else {
            player.sendMessage("§cCould not claim commission. Make sure it is active and complete.");
        }
    }

    private void handleProgress(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 3) {
            player.sendMessage("Usage: /commission progress <type> <amount>");
            return;
        }
        CommissionType type;
        try {
            type = CommissionType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown commission type: " + args[1]);
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount < 0) {
                player.sendMessage("Amount must not be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount: " + args[2]);
            return;
        }
        int newProgress = commissionManager.addProgress(player.getUniqueId(), type, amount);
        if (newProgress == -1) {
            player.sendMessage("You do not have an active " + type.getDisplayName() + " commission.");
        } else {
            player.sendMessage("Progress updated: " + type.getDisplayName() + " " + newProgress + "/" + type.getTarget());
        }
    }

    private void handleStats(Player player) {
        long count = commissionManager.getCompletedCount(player.getUniqueId());
        player.sendMessage("=== Commission Stats ===");
        player.sendMessage("Total completed: §e" + count);
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Commission Commands ===");
        player.sendMessage("/commission                     — open the commission menu");
        player.sendMessage("/commission list                — list active commissions");
        player.sendMessage("/commission generate <location> — generate new commissions");
        player.sendMessage("/commission claim <type>        — claim a completed commission");
        player.sendMessage("/commission stats               — view your total completed");
    }
}
