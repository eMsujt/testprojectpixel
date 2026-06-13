package com.skyblock.core.mayor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /mayor} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /mayor}                  — view the active mayor and your vote</li>
 *   <li>{@code /mayor vote <mayor>}     — vote for a mayor</li>
 *   <li>{@code /mayor set <mayor>}      — (op) set the active mayor</li>
 * </ul>
 * </p>
 */
public final class MayorCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("vote", "set");
    private static final List<String> MAYOR_NAMES = Arrays.stream(MayorManager.Mayor.values())
            .map(m -> m.name().toLowerCase())
            .collect(Collectors.toList());

    private final MayorManager mayorManager;

    public MayorCommand(MayorManager mayorManager) {
        this.mayorManager = mayorManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleInfo(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "vote" -> handleVote(player, args);
            case "set"  -> handleSet(player, args);
            default     -> player.sendMessage("Unknown subcommand. Usage: /mayor <vote|set>");
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
            String prefix = args[1].toLowerCase();
            if (sub.equals("vote") || sub.equals("set")) {
                return MAYOR_NAMES.stream().filter(m -> m.startsWith(prefix)).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    // -------------------------------------------------------------------------
    // Subcommand handlers
    // -------------------------------------------------------------------------

    private void handleInfo(Player player) {
        MayorManager.Mayor current = mayorManager.getCurrentMayor();
        MayorManager.Mayor vote = mayorManager.getVote(player.getUniqueId());
        player.sendMessage("=== Mayor ===");
        player.sendMessage("Active mayor: " + (current != null ? current.getDisplayName() : "None"));
        player.sendMessage("Your vote: " + (vote != null ? vote.getDisplayName() : "None"));
    }

    private void handleVote(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /mayor vote <mayor>");
            return;
        }
        MayorManager.Mayor mayor = parseMayor(player, args[1]);
        if (mayor == null) return;
        mayorManager.vote(player.getUniqueId(), mayor);
        player.sendMessage("You voted for " + mayor.getDisplayName() + ".");
    }

    private void handleSet(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("Usage: /mayor set <mayor>");
            return;
        }
        MayorManager.Mayor mayor = parseMayor(player, args[1]);
        if (mayor == null) return;
        mayorManager.setCurrentMayor(mayor);
        player.sendMessage("Active mayor set to " + mayor.getDisplayName() + ".");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private MayorManager.Mayor parseMayor(Player player, String input) {
        try {
            return MayorManager.Mayor.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown mayor: " + input + ". Valid mayors: " + String.join(", ", MAYOR_NAMES));
            return null;
        }
    }
}
