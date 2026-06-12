package com.skyblock.core.island;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles the {@code /island} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /island create}         — create your island</li>
 *   <li>{@code /island home}           — teleport to your island spawn</li>
 *   <li>{@code /island visit <player>} — visit another player's island</li>
 *   <li>{@code /island invite <player>}— invite a player to your island</li>
 *   <li>{@code /island kick <player>}  — remove a member from your island</li>
 *   <li>{@code /island leave}          — leave an island you are a member of</li>
 * </ul>
 * </p>
 */
public final class IslandCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("create", "home", "visit", "invite", "kick", "leave");

    private final IslandManager islandManager;

    public IslandCommand(IslandManager islandManager) {
        this.islandManager = islandManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /island <create|home|visit|invite|kick|leave>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create" -> handleCreate(player);
            case "home"   -> handleHome(player);
            case "visit"  -> handleVisit(player, args);
            case "invite" -> handleInvite(player, args);
            case "kick"   -> handleKick(player, args);
            case "leave"  -> handleLeave(player);
            default       -> player.sendMessage("Unknown subcommand. Usage: /island <create|home|visit|invite|kick|leave>");
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
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("visit") || sub.equals("invite") || sub.equals("kick")) {
                String prefix = args[1].toLowerCase();
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private void handleCreate(Player player) {
        if (islandManager.hasIsland(player.getUniqueId())) {
            player.sendMessage("You already have an island.");
            return;
        }
        islandManager.createIsland(player.getUniqueId());
        player.sendMessage("Your island has been created! Use /island home to go there.");
    }

    private void handleHome(Player player) {
        Optional<IslandManager.SkyBlockIsland> island =
                islandManager.getIslandByMember(player.getUniqueId());
        if (island.isEmpty()) {
            player.sendMessage("You do not have an island. Use /island create to make one.");
            return;
        }
        player.sendMessage("Teleporting to your island...");
        // Teleport logic would go here once world generation is in place.
    }

    private void handleVisit(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /island visit <player>");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage("Use /island home to go to your own island.");
            return;
        }
        if (!islandManager.hasIsland(target.getUniqueId())) {
            player.sendMessage(target.getName() + " does not have an island.");
            return;
        }
        player.sendMessage("Visiting " + target.getName() + "'s island...");
        // Teleport logic would go here once world generation is in place.
    }

    private void handleInvite(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /island invite <player>");
            return;
        }
        if (!islandManager.hasIsland(player.getUniqueId())) {
            player.sendMessage("You do not have an island. Use /island create first.");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage("You cannot invite yourself.");
            return;
        }
        boolean added = islandManager.addMember(player.getUniqueId(), target.getUniqueId());
        if (!added) {
            player.sendMessage(target.getName() + " is already a member of an island.");
            return;
        }
        player.sendMessage(target.getName() + " has been added to your island.");
        target.sendMessage(player.getName() + " has invited you to their island.");
    }

    private void handleKick(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /island kick <player>");
            return;
        }
        UUID ownerUuid = player.getUniqueId();
        if (!islandManager.hasIsland(ownerUuid)) {
            player.sendMessage("You do not have an island.");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        UUID targetUuid;
        String targetName;
        if (target != null) {
            targetUuid = target.getUniqueId();
            targetName = target.getName();
        } else {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        boolean removed = islandManager.removeMember(ownerUuid, targetUuid);
        if (!removed) {
            player.sendMessage(targetName + " is not a member of your island.");
            return;
        }
        player.sendMessage(targetName + " has been removed from your island.");
        target.sendMessage("You have been kicked from " + player.getName() + "'s island.");
    }

    private void handleLeave(Player player) {
        if (islandManager.hasIsland(player.getUniqueId())) {
            player.sendMessage("You own this island and cannot leave. Delete it instead.");
            return;
        }
        boolean left = islandManager.leaveIsland(player.getUniqueId());
        if (!left) {
            player.sendMessage("You are not a member of any island.");
            return;
        }
        player.sendMessage("You have left the island.");
    }
}
