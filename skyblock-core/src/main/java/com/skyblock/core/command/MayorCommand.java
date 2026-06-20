package com.skyblock.core.command;

import com.skyblock.core.manager.MayorManager;
import com.skyblock.core.manager.MayorManager.MayorCandidate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class MayorCommand extends PlayerCommand {

    private final MayorManager mayorManager;

    public MayorCommand(MayorManager mayorManager) {
        this.mayorManager = mayorManager;
    }

    @Override
    protected void openMenu(Player player) {
        showStatus(player);
    }

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            showStatus(player);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "vote"    -> handleVote(player, args);
            case "set"     -> handleSet(player, args);
            case "info"    -> handleInfo(player);
            case "history" -> handleHistory(player);
            default        -> sendHelp(player, label);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Arrays.asList("vote", "set", "info", "history").stream()
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("vote") || args[0].equalsIgnoreCase("set"))) {
            String lower = args[1].toLowerCase();
            return Arrays.stream(MayorCandidate.values())
                    .map(c -> c.name().toLowerCase())
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        return List.of();
    }

    private void showStatus(Player player) {
        MayorCandidate mayor = mayorManager.getCurrentMayor();
        if (mayor == null) {
            player.sendMessage("§eNo mayor is currently in office.");
        } else {
            player.sendMessage("§6Current Mayor: §f" + mayor.getDisplayName());
            player.sendMessage("§7Perks: §f" + String.join(", ", mayor.getPerks()));
        }
        player.sendMessage("§7Election in §f" + mayorManager.getDaysUntilElection() + "§7 day(s).");
        MayorCandidate vote = mayorManager.getVote(player.getUniqueId());
        if (vote != null) {
            player.sendMessage("§7Your vote: §f" + vote.getDisplayName());
        }
    }

    private void handleVote(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /mayor vote <candidate>");
            return;
        }
        MayorCandidate candidate = parseMayor(player, args[1]);
        if (candidate == null) return;
        mayorManager.vote(player.getUniqueId(), candidate);
        player.sendMessage("§aYou voted for §f" + candidate.getDisplayName() + "§a!");
    }

    private void handleSet(Player player, String[] args) {
        if (!player.hasPermission("skyblock.mayor.admin")) {
            player.sendMessage("§cYou do not have permission to set the mayor.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("§cUsage: /mayor set <candidate>");
            return;
        }
        MayorCandidate candidate = parseMayor(player, args[1]);
        if (candidate == null) return;
        mayorManager.setCurrentMayor(candidate);
        player.sendMessage("§aMayor set to §f" + candidate.getDisplayName() + "§a.");
    }

    private void handleInfo(Player player) {
        MayorCandidate mayor = mayorManager.getCurrentMayor();
        if (mayor == null) {
            player.sendMessage("§eNo mayor is currently in office.");
            return;
        }
        player.sendMessage("§6=== Mayor: " + mayor.getDisplayName() + " ===");
        player.sendMessage("§7Perks:");
        for (String perk : mayor.getPerks()) {
            player.sendMessage("  §f- " + perk);
        }
        player.sendMessage("§7Stat Bonuses:");
        Map<com.skyblock.core.model.Stat, Double> bonuses = mayorManager.getActiveStatBonuses();
        if (bonuses.isEmpty()) {
            player.sendMessage("  §7None");
        } else {
            bonuses.forEach((stat, val) ->
                    player.sendMessage("  §f" + stat.name() + ": +" + val));
        }
        player.sendMessage("§7Cycle day: §f" + mayorManager.getCycleDay()
                + "§7 / §f" + MayorManager.ELECTION_CYCLE_DAYS);
    }

    private void handleHistory(Player player) {
        List<String> history = mayorManager.getElectionHistory();
        if (history.isEmpty()) {
            player.sendMessage("§7No election history yet.");
            return;
        }
        player.sendMessage("§6=== Election History ===");
        int start = Math.max(0, history.size() - 10);
        for (int i = start; i < history.size(); i++) {
            player.sendMessage("§7" + history.get(i));
        }
    }

    private MayorCandidate parseMayor(Player player, String input) {
        try {
            return MayorCandidate.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cUnknown candidate: §f" + input
                    + "§c. Valid candidates: "
                    + Arrays.stream(MayorCandidate.values())
                            .map(c -> c.name().toLowerCase())
                            .reduce((a, b) -> a + ", " + b).orElse(""));
            return null;
        }
    }

    private void sendHelp(Player player, String label) {
        player.sendMessage("§6=== Mayor Commands ===");
        player.sendMessage("§7/" + label + " §f— view current mayor");
        player.sendMessage("§7/" + label + " vote <candidate> §f— vote for a candidate");
        player.sendMessage("§7/" + label + " info §f— detailed mayor info and stat bonuses");
        player.sendMessage("§7/" + label + " history §f— view election history");
        player.sendMessage("§7/" + label + " set <candidate> §f— force-set mayor (admin)");
    }
}
