package com.skyblock.core.network;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /network} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /network info}          — show session time and total playtime</li>
 *   <li>{@code /network total}         — show total accumulated playtime</li>
 *   <li>{@code /network add <seconds>} — (op) add seconds to your playtime</li>
 *   <li>{@code /network reset}         — (op) reset your playtime to zero</li>
 * </ul>
 * </p>
 */
public final class NetworkCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "total", "add", "reset");

    private final NetworkManager networkManager;

    public NetworkCommand(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /network <info|total|add|reset>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"  -> handleInfo(player);
            case "total" -> handleTotal(player);
            case "add"   -> handleAdd(player, args);
            case "reset" -> handleReset(player);
            default      -> player.sendMessage("Unknown subcommand. Usage: /network <info|total|add|reset>");
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

    // -------------------------------------------------------------------------
    // Subcommand handlers
    // -------------------------------------------------------------------------

    private void handleInfo(Player player) {
        long session = networkManager.getSessionSeconds(player.getUniqueId());
        long total   = networkManager.getTotalPlaytimeSeconds(player.getUniqueId());
        player.sendMessage("=== Network Info ===");
        player.sendMessage("Session time:    " + formatSeconds(session));
        player.sendMessage("Total playtime:  " + formatSeconds(total));
    }

    private void handleTotal(Player player) {
        long total = networkManager.getTotalPlaytimeSeconds(player.getUniqueId());
        player.sendMessage("Total playtime: " + formatSeconds(total));
    }

    private void handleAdd(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("Usage: /network add <seconds>");
            return;
        }
        long seconds;
        try {
            seconds = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid seconds value.");
            return;
        }
        if (seconds < 0) {
            player.sendMessage("Seconds must be >= 0.");
            return;
        }
        networkManager.addPlaytime(player.getUniqueId(), seconds);
        player.sendMessage("Added " + seconds + "s. Total: "
                + formatSeconds(networkManager.getTotalPlaytimeSeconds(player.getUniqueId())));
    }

    private void handleReset(Player player) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        networkManager.resetPlaytime(player.getUniqueId());
        player.sendMessage("Your playtime has been reset.");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static String formatSeconds(long totalSeconds) {
        long hours   = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%dh %dm %ds", hours, minutes, seconds);
    }
}
