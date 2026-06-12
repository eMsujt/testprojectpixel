package com.skyblock.core.combat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class CombatCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "kills", "deaths", "mobkills", "reset");

    private final CombatManager combatManager;

    public CombatCommand(CombatManager combatManager) {
        this.combatManager = combatManager;
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
            case "info"     -> handleInfo(player);
            case "kills"    -> player.sendMessage("Player kills: " + combatManager.getKills(player.getUniqueId()));
            case "deaths"   -> player.sendMessage("Deaths: " + combatManager.getDeaths(player.getUniqueId()));
            case "mobkills" -> player.sendMessage("Mob kills: " + combatManager.getMobKills(player.getUniqueId()));
            case "reset"    -> {
                combatManager.reset(player.getUniqueId());
                player.sendMessage("Combat stats reset.");
            }
            default -> sendHelp(player);
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

    private void handleInfo(Player player) {
        player.sendMessage("=== Combat Stats ===");
        player.sendMessage("  Player kills : " + combatManager.getKills(player.getUniqueId()));
        player.sendMessage("  Deaths       : " + combatManager.getDeaths(player.getUniqueId()));
        player.sendMessage("  Mob kills    : " + combatManager.getMobKills(player.getUniqueId()));
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Combat Commands ===");
        player.sendMessage("/combat info       — show all combat stats");
        player.sendMessage("/combat kills      — show player kill count");
        player.sendMessage("/combat deaths     — show death count");
        player.sendMessage("/combat mobkills   — show mob kill count");
        player.sendMessage("/combat reset      — reset your combat stats");
    }
}
