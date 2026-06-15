package com.skyblock.core.combat.command;

import com.skyblock.core.combat.manager.CombatManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class CombatCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "kills", "deaths", "mobkills", "reset");
    private static final List<String> MOB_TYPES = Arrays.stream(CombatManager.MobType.values())
            .map(t -> t.name().toLowerCase())
            .collect(Collectors.toList());

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
            case "mobkills" -> handleMobKills(player, args);
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
        if (args.length == 2 && args[0].equalsIgnoreCase("mobkills")) {
            String prefix = args[1].toLowerCase();
            return MOB_TYPES.stream()
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

    private void handleMobKills(Player player, String[] args) {
        if (args.length >= 2) {
            try {
                CombatManager.MobType type = CombatManager.MobType.valueOf(args[1].toUpperCase());
                int count = combatManager.getMobKillCount(player.getUniqueId(), type);
                player.sendMessage(type.name() + " kills: " + count);
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown mob type: " + args[1]);
            }
        } else {
            Map<CombatManager.MobType, Integer> counts = combatManager.getMobKillCounts(player.getUniqueId());
            player.sendMessage("=== Mob Kills ===");
            player.sendMessage("  Total: " + combatManager.getMobKills(player.getUniqueId()));
            for (CombatManager.MobType type : CombatManager.MobType.values()) {
                int count = counts.getOrDefault(type, 0);
                if (count > 0) {
                    player.sendMessage("  " + type.name() + ": " + count);
                }
            }
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Combat Commands ===");
        player.sendMessage("/combat info              — show all combat stats");
        player.sendMessage("/combat kills             — show player kill count");
        player.sendMessage("/combat deaths            — show death count");
        player.sendMessage("/combat mobkills [type]   — show mob kill count (optionally by type)");
        player.sendMessage("/combat reset             — reset your combat stats");
    }
}
