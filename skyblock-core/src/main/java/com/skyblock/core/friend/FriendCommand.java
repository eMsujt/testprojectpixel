package com.skyblock.core.friend;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles the {@code /friend} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /friend add <player>}     — send a friend request</li>
 *   <li>{@code /friend accept <player>}  — accept a pending request</li>
 *   <li>{@code /friend decline <player>} — decline a pending request</li>
 *   <li>{@code /friend remove <player>}  — remove a friend</li>
 *   <li>{@code /friend list}             — list your friends</li>
 * </ul>
 * </p>
 */
public final class FriendCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList(
            "add", "accept", "decline", "remove", "list"
    );

    private final FriendManager friendManager;

    public FriendCommand(FriendManager friendManager) {
        this.friendManager = friendManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "add"     -> handleAdd(player, args);
            case "accept"  -> handleAccept(player, args);
            case "decline" -> handleDecline(player, args);
            case "remove"  -> handleRemove(player, args);
            case "list"    -> handleList(player);
            default        -> sendHelp(player);
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
            if (sub.equals("add") || sub.equals("accept") || sub.equals("decline") || sub.equals("remove")) {
                String prefix = args[1].toLowerCase();
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(n -> n.toLowerCase().startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private void handleAdd(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /friend add <player>");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        if (target.equals(player)) {
            player.sendMessage("You cannot add yourself as a friend.");
            return;
        }
        try {
            friendManager.sendRequest(player.getUniqueId(), target.getUniqueId());
            player.sendMessage("Friend request sent to " + target.getName() + ".");
            target.sendMessage(player.getName() + " sent you a friend request. Use /friend accept " + player.getName() + " to accept.");
        } catch (IllegalStateException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleAccept(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /friend accept <player>");
            return;
        }
        Player sender = Bukkit.getPlayerExact(args[1]);
        if (sender == null) {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        try {
            friendManager.acceptRequest(player.getUniqueId(), sender.getUniqueId());
            player.sendMessage("You are now friends with " + sender.getName() + ".");
            sender.sendMessage(player.getName() + " accepted your friend request.");
        } catch (IllegalStateException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleDecline(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /friend decline <player>");
            return;
        }
        Player sender = Bukkit.getPlayerExact(args[1]);
        if (sender == null) {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        boolean had = friendManager.declineRequest(player.getUniqueId(), sender.getUniqueId());
        if (had) {
            player.sendMessage("Friend request from " + sender.getName() + " declined.");
        } else {
            player.sendMessage("No pending friend request from " + sender.getName() + ".");
        }
    }

    private void handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /friend remove <player>");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        UUID targetId;
        String targetName;
        if (target != null) {
            targetId = target.getUniqueId();
            targetName = target.getName();
        } else {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        try {
            friendManager.removeFriend(player.getUniqueId(), targetId);
            player.sendMessage("Removed " + targetName + " from your friends.");
            target.sendMessage(player.getName() + " removed you from their friends.");
        } catch (IllegalStateException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleList(Player player) {
        Set<UUID> friendSet = friendManager.getFriends(player.getUniqueId());
        if (friendSet.isEmpty()) {
            player.sendMessage("You have no friends yet. Use /friend add <player> to add one.");
            return;
        }
        player.sendMessage("=== Friends (" + friendSet.size() + ") ===");
        for (UUID id : friendSet) {
            Player online = Bukkit.getPlayer(id);
            String name = online != null ? online.getName() + " [online]" : id.toString() + " [offline]";
            player.sendMessage("  - " + name);
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Friend Commands ===");
        player.sendMessage("/friend add <player>     — send a friend request");
        player.sendMessage("/friend accept <player>  — accept a friend request");
        player.sendMessage("/friend decline <player> — decline a friend request");
        player.sendMessage("/friend remove <player>  — remove a friend");
        player.sendMessage("/friend list             — list your friends");
    }
}
