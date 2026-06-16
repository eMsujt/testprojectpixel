package com.skyblock.core.mayor;

import com.skyblock.core.manager.MayorManager;
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

    private static final List<String> SUBCOMMANDS = Arrays.asList("current", "perks", "vote", "set", "history");
    private static final List<String> MAYOR_NAMES = Arrays.stream(MayorManager.MayorCandidate.values())
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
            case "current" -> handleCurrent(player);
            case "perks"   -> handlePerks(player);
            case "vote"    -> handleVote(player, args);
            case "set"     -> handleSet(player, args);
            case "history" -> handleHistory(player);
            default        -> player.sendMessage("Unknown subcommand. Usage: /mayor <current|perks|vote|set|history>");
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

    private void handleCurrent(Player player) {
        MayorManager.MayorCandidate current = mayorManager.getCurrentMayor();
        player.sendMessage("=== Current Mayor ===");
        player.sendMessage("Active mayor: " + (current != null ? current.getDisplayName() : "None"));
    }

    private void handlePerks(Player player) {
        MayorManager.MayorCandidate current = mayorManager.getCurrentMayor();
        if (current == null) {
            player.sendMessage("There is no active mayor.");
            return;
        }
        player.sendMessage("=== " + current.getDisplayName() + "'s Perks ===");
        for (String perk : current.getPerks()) {
            player.sendMessage("- " + perk);
        }
    }

    private void handleInfo(Player player) {
        MayorManager.MayorCandidate current = mayorManager.getCurrentMayor();
        MayorManager.MayorCandidate vote = mayorManager.getVote(player.getUniqueId());
        player.sendMessage("=== Mayor ===");
        player.sendMessage("Active mayor: " + (current != null ? current.getDisplayName() : "None"));
        player.sendMessage("Your vote: " + (vote != null ? vote.getDisplayName() : "None"));
    }

    private void handleVote(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /mayor vote <mayor>");
            return;
        }
        MayorManager.MayorCandidate mayor = parseMayor(player, args[1]);
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
        MayorManager.MayorCandidate mayor = parseMayor(player, args[1]);
        if (mayor == null) return;
        mayorManager.setCurrentMayor(mayor);
        player.sendMessage("Active mayor set to " + mayor.getDisplayName() + ".");
    }

    private void handleHistory(Player player) {
        List<String> history = mayorManager.getMayorHistory(player.getUniqueId());
        player.sendMessage("=== Mayor History ===");
        if (history.isEmpty()) {
            player.sendMessage("No mayor history found.");
            return;
        }
        for (int i = 0; i < history.size(); i++) {
            player.sendMessage((i + 1) + ". " + history.get(i));
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private MayorManager.MayorCandidate parseMayor(Player player, String input) {
        try {
            return MayorManager.MayorCandidate.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown mayor: " + input + ". Valid mayors: " + String.join(", ", MAYOR_NAMES));
            return null;
        }
    }
}
