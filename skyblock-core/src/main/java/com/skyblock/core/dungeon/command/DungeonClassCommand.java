package com.skyblock.core.dungeon.command;

import com.skyblock.core.manager.DungeonClassManager;
import com.skyblock.core.manager.DungeonManager;
import com.skyblock.core.manager.DungeonManager.DungeonClass;
import com.skyblock.core.menu.DungeonClassMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles the {@code /dungeonclass} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /dungeonclass}               — open the dungeon class GUI</li>
 *   <li>{@code /dungeonclass list}          — list all classes and your level in each</li>
 *   <li>{@code /dungeonclass select <cls>}  — select an active dungeon class</li>
 *   <li>{@code /dungeonclass info <cls>}    — print passive stats for a class</li>
 * </ul>
 * </p>
 */
public final class DungeonClassCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "select", "info");

    private final DungeonClassManager dungeonClassManager;

    public DungeonClassCommand(DungeonClassManager dungeonClassManager) {
        this.dungeonClassManager = dungeonClassManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            new DungeonClassMenu(player).open(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"   -> handleList(player);
            case "select" -> handleSelect(player, args);
            case "info"   -> handleInfo(player, args);
            default       -> sendHelp(player);
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
        if (args.length == 2 && (args[0].equalsIgnoreCase("select") || args[0].equalsIgnoreCase("info"))) {
            String prefix = args[1].toUpperCase();
            return Arrays.stream(DungeonClass.values())
                    .map(Enum::name)
                    .filter(n -> n.startsWith(prefix))
                    .sorted()
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        DungeonManager dungeons = DungeonManager.getInstance();
        DungeonClass selected = dungeons.getClass(player.getUniqueId());
        player.sendMessage("=== Dungeon Classes ===");
        for (DungeonClass cls : DungeonClass.values()) {
            int level = dungeons.getClassLevel(player.getUniqueId(), cls);
            String marker = cls.equals(selected) ? " §a[SELECTED]" : "";
            player.sendMessage("  " + cls.getDisplayName() + ": level " + level
                    + "/" + DungeonManager.MAX_CLASS_LEVEL + marker);
        }
    }

    private void handleSelect(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /dungeonclass select <HEALER|MAGE|BERSERK|ARCHER|TANK>");
            return;
        }
        DungeonClass cls = parseClass(args[1]);
        if (cls == null) {
            player.sendMessage("Unknown class: " + args[1] + ". Valid classes: HEALER, MAGE, BERSERK, ARCHER, TANK.");
            return;
        }
        DungeonManager.getInstance().setClass(player.getUniqueId(), cls);
        player.sendMessage("§aYou are now playing as §b" + cls.getDisplayName() + "§a.");
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /dungeonclass info <HEALER|MAGE|BERSERK|ARCHER|TANK>");
            return;
        }
        DungeonClass cls = parseClass(args[1]);
        if (cls == null) {
            player.sendMessage("Unknown class: " + args[1] + ". Valid classes: HEALER, MAGE, BERSERK, ARCHER, TANK.");
            return;
        }
        player.sendMessage("=== " + cls.getDisplayName() + " ===");
        player.sendMessage("Passive stats per level:");
        dungeonClassManager.getPassiveStatsPerLevel(cls).entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> player.sendMessage(String.format("  +%.1f %s", e.getValue(), e.getKey().getDisplayName())));
        int level = DungeonManager.getInstance().getClassLevel(player.getUniqueId(), cls);
        player.sendMessage("Your level: " + level + "/" + DungeonManager.MAX_CLASS_LEVEL);
        if (level > 0) {
            player.sendMessage("Your total bonuses:");
            dungeonClassManager.getPassiveStats(cls, level).entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> player.sendMessage(String.format("  +%.1f %s", e.getValue(), e.getKey().getDisplayName())));
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Dungeon Class Commands ===");
        player.sendMessage("/dungeonclass              — open the class selection GUI");
        player.sendMessage("/dungeonclass list         — list all classes and your levels");
        player.sendMessage("/dungeonclass select <cls> — select your active class");
        player.sendMessage("/dungeonclass info <cls>   — show passive stats for a class");
    }

    private static DungeonClass parseClass(String name) {
        try {
            return DungeonClass.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
