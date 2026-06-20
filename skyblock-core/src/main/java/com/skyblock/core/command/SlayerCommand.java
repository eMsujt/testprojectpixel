package com.skyblock.core.command;

import com.skyblock.core.manager.SlayerManager;
import com.skyblock.core.menu.SlayerMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class SlayerCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("view", "info", "start", "type", "menu");

    private final JavaPlugin plugin;
    private final SlayerManager slayerManager;

    public SlayerCommand(JavaPlugin plugin, SlayerManager slayerManager) {
        this.plugin = plugin;
        this.slayerManager = slayerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("menu")) {
            new SlayerMenu(player).open(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "view"  -> handleView(player, args);
            case "info"  -> handleInfo(player);
            case "start" -> handleStart(player, args);
            case "type"  -> handleType(player, args);
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
        if (args.length == 2 && (args[0].equalsIgnoreCase("view")
                || args[0].equalsIgnoreCase("start")
                || args[0].equalsIgnoreCase("type"))) {
            String prefix = args[1].toLowerCase();
            return Arrays.stream(SlayerManager.SlayerType.values())
                    .map(t -> t.name().toLowerCase())
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleView(Player player, String[] args) {
        if (args.length < 2) {
            handleInfo(player);
            return;
        }
        SlayerManager.SlayerType type;
        try {
            type = SlayerManager.SlayerType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown slayer type: " + args[1]);
            return;
        }
        long xp = slayerManager.getExperience(player.getUniqueId(), type);
        int level = slayerManager.getLevel(player.getUniqueId(), type);
        int kills = slayerManager.getKillCount(player.getUniqueId(), type);
        player.sendMessage("=== " + type.getDisplayName() + " Slayer ===");
        player.sendMessage("  Level: " + level);
        player.sendMessage("  XP: " + xp);
        player.sendMessage("  Kills: " + kills);
    }

    private void handleInfo(Player player) {
        player.sendMessage("=== Slayer XP ===");
        for (SlayerManager.SlayerType type : SlayerManager.SlayerType.values()) {
            long xp = slayerManager.getExperience(player.getUniqueId(), type);
            int level = slayerManager.getLevel(player.getUniqueId(), type);
            player.sendMessage("  " + type.getDisplayName() + ": level " + level + " (" + xp + " XP)");
        }
    }

    private void handleStart(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /slayer start <type>");
            return;
        }
        SlayerManager.SlayerType type;
        try {
            type = SlayerManager.SlayerType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown slayer type: " + args[1]);
            return;
        }
        try {
            slayerManager.startQuest(player.getUniqueId(), type, SlayerManager.QuestTier.TIER_1);
            player.sendMessage("§aStarted " + type.getDisplayName() + " Tier-1 slayer quest.");
        } catch (IllegalStateException e) {
            player.sendMessage("§cYou already have an active slayer quest.");
        }
    }

    private void handleType(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /slayer type <type>");
            return;
        }
        SlayerManager.SlayerType type;
        try {
            type = SlayerManager.SlayerType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown slayer type: " + args[1]);
            return;
        }
        long xp = slayerManager.getExperience(player.getUniqueId(), type);
        int level = slayerManager.getLevel(player.getUniqueId(), type);
        player.sendMessage(type.getDisplayName() + ": level " + level + " (" + xp + " XP)");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Slayer Commands ===");
        player.sendMessage("/slayer             — open slayer menu");
        player.sendMessage("/slayer info        — show all slayer levels");
        player.sendMessage("/slayer start <type>— start a slayer quest");
        player.sendMessage("/slayer view <type> — show XP for one type");
        player.sendMessage("/slayer type <type> — show XP for one type");
    }
}
