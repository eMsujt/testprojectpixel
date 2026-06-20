package com.skyblock.core.command;

import com.skyblock.core.manager.IslandManager;
import com.skyblock.core.menu.IslandMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class IslandCommand extends PlayerCommand {

    private static final List<String> SUB_COMMANDS = Arrays.asList("create", "home", "visit", "leave", "upgrade");

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            openMenu(player);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "create" -> handleCreate(player);
            case "home"  -> handleHome(player);
            case "visit"  -> handleVisit(player, args);
            case "leave"  -> handleLeave(player);
            case "upgrade" -> openMenu(player);
            default -> player.sendMessage("§cUnknown sub-command. Usage: /" + label + " [create|visit|leave|upgrade]");
        }
        return true;
    }

    @Override
    protected void openMenu(Player player) {
        new IslandMenu(player.getUniqueId()).open(player);
    }

    private void handleCreate(Player player) {
        IslandManager manager = IslandManager.getInstance();
        if (manager.hasIsland(player.getUniqueId())) {
            player.sendMessage("§cYou already have an island!");
            return;
        }
        manager.createIsland(player);
        player.sendMessage("§aYour island has been created!");
    }

    private void handleHome(Player player) {
        IslandManager manager = IslandManager.getInstance();
        Optional<org.bukkit.World> world = manager.getIslandWorld(player.getUniqueId());
        if (world.isEmpty()) {
            player.sendMessage("§cYou do not have an island yet. Use /island create.");
            return;
        }
        player.teleport(world.get().getSpawnLocation());
        player.sendMessage("§aTeleported to your island.");
    }

    private void handleVisit(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /island visit <player>");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            player.sendMessage("§cPlayer not found: " + args[1]);
            return;
        }
        IslandManager manager = IslandManager.getInstance();
        Optional<org.bukkit.World> world = manager.getIslandWorld(target.getUniqueId());
        if (world.isEmpty()) {
            player.sendMessage("§c" + target.getName() + " does not have an island.");
            return;
        }
        player.teleport(world.get().getSpawnLocation());
        player.sendMessage("§aTeleported to " + target.getName() + "'s island.");
    }

    private void handleLeave(Player player) {
        boolean left = IslandManager.getInstance().leaveIsland(player.getUniqueId());
        if (left) {
            player.sendMessage("§aYou have left the island.");
        } else {
            player.sendMessage("§cYou are not a member of any island.");
        }
    }

    @Override
    public List<String> onTabComplete(org.bukkit.command.CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUB_COMMANDS.stream().filter(s -> s.startsWith(prefix)).toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("visit")) {
            String prefix = args[1].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(prefix))
                    .toList();
        }
        return List.of();
    }
}
