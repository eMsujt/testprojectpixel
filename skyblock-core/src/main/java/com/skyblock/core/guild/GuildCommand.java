package com.skyblock.core.guild;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Handles the {@code /guild} command.
 *
 * <p>Sub-commands:
 * <ul>
 *   <li>{@code /guild create <name>} — create a new guild</li>
 *   <li>{@code /guild disband}       — disband your guild (leader only)</li>
 *   <li>{@code /guild invite <player>} — invite a player to your guild (leader only)</li>
 *   <li>{@code /guild kick <player>} — kick a member (leader only)</li>
 *   <li>{@code /guild leave}         — leave your current guild</li>
 *   <li>{@code /guild info [name]}   — view guild details</li>
 *   <li>{@code /guild list}          — list all guilds</li>
 * </ul>
 * </p>
 */
public final class GuildCommand implements TabExecutor {

    private static final List<String> SUB_COMMANDS =
            Arrays.asList("create", "disband", "invite", "kick", "leave", "info", "list");

    private final GuildManager guildManager;

    public GuildCommand(GuildManager guildManager) {
        this.guildManager = guildManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create" -> handleCreate(player, args);
            case "disband" -> handleDisband(player);
            case "invite" -> handleInvite(player, args);
            case "kick" -> handleKick(player, args);
            case "leave" -> handleLeave(player);
            case "info" -> handleInfo(player, args);
            case "list" -> handleList(player);
            default -> sendUsage(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> matches = new ArrayList<>();
            String partial = args[0].toLowerCase();
            for (String sub : SUB_COMMANDS) {
                if (sub.startsWith(partial)) {
                    matches.add(sub);
                }
            }
            return matches;
        }
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if ("invite".equals(sub) || "kick".equals(sub)) {
                List<String> names = new ArrayList<>();
                String partial = args[1].toLowerCase();
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (online.getName().toLowerCase().startsWith(partial)) {
                        names.add(online.getName());
                    }
                }
                return names;
            }
            if ("info".equals(sub)) {
                List<String> names = new ArrayList<>();
                String partial = args[1].toLowerCase();
                for (String name : guildManager.getAllGuilds().keySet()) {
                    if (name.startsWith(partial)) {
                        names.add(name);
                    }
                }
                return names;
            }
        }
        return Collections.emptyList();
    }

    // -----------------------------------------------------------------------
    // Sub-command handlers
    // -----------------------------------------------------------------------

    private void handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /guild create <name>");
            return;
        }
        String name = args[1];
        try {
            guildManager.createGuild(player.getUniqueId(), name);
            player.sendMessage("Guild '" + name + "' created successfully!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            player.sendMessage("Could not create guild: " + e.getMessage());
        }
    }

    private void handleDisband(Player player) {
        try {
            GuildManager.Guild guild = guildManager.getGuildOfPlayer(player.getUniqueId());
            if (guild == null) {
                player.sendMessage("You are not in a guild.");
                return;
            }
            String name = guild.getName();
            guildManager.disbandGuild(player.getUniqueId());
            player.sendMessage("Guild '" + name + "' has been disbanded.");
        } catch (IllegalStateException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleInvite(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /guild invite <player>");
            return;
        }
        GuildManager.Guild guild = guildManager.getGuildOfPlayer(player.getUniqueId());
        if (guild == null) {
            player.sendMessage("You are not in a guild.");
            return;
        }
        if (!guild.getLeader().equals(player.getUniqueId())) {
            player.sendMessage("Only the guild leader can invite players.");
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        try {
            guildManager.addMember(guild.getName(), target.getUniqueId());
            player.sendMessage(target.getName() + " has joined guild '" + guild.getName() + "'.");
            target.sendMessage("You have been added to guild '" + guild.getName() + "' by " + player.getName() + ".");
        } catch (IllegalStateException | IllegalArgumentException e) {
            player.sendMessage("Could not invite player: " + e.getMessage());
        }
    }

    private void handleKick(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /guild kick <player>");
            return;
        }
        GuildManager.Guild guild = guildManager.getGuildOfPlayer(player.getUniqueId());
        if (guild == null) {
            player.sendMessage("You are not in a guild.");
            return;
        }
        if (!guild.getLeader().equals(player.getUniqueId())) {
            player.sendMessage("Only the guild leader can kick members.");
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        UUID targetId = target != null ? target.getUniqueId() : null;
        if (targetId == null) {
            // allow kicking by name even if offline — look through members
            for (UUID memberId : guild.getMembers()) {
                String offlineName = Bukkit.getOfflinePlayer(memberId).getName();
                if (args[1].equalsIgnoreCase(offlineName)) {
                    targetId = memberId;
                    break;
                }
            }
        }
        if (targetId == null) {
            player.sendMessage("Player '" + args[1] + "' is not a member of your guild.");
            return;
        }
        if (targetId.equals(player.getUniqueId())) {
            player.sendMessage("You cannot kick yourself. Use /guild disband to remove the guild.");
            return;
        }
        try {
            guildManager.removeMember(targetId);
            player.sendMessage(args[1] + " has been kicked from the guild.");
            if (target != null) {
                target.sendMessage("You have been kicked from guild '" + guild.getName() + "'.");
            }
        } catch (IllegalStateException e) {
            player.sendMessage("Could not kick player: " + e.getMessage());
        }
    }

    private void handleLeave(Player player) {
        try {
            GuildManager.Guild guild = guildManager.getGuildOfPlayer(player.getUniqueId());
            if (guild == null) {
                player.sendMessage("You are not in a guild.");
                return;
            }
            String name = guild.getName();
            guildManager.removeMember(player.getUniqueId());
            player.sendMessage("You left guild '" + name + "'.");
        } catch (IllegalStateException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleInfo(Player player, String[] args) {
        GuildManager.Guild guild;
        if (args.length >= 2) {
            guild = guildManager.getGuild(args[1]);
            if (guild == null) {
                player.sendMessage("Guild '" + args[1] + "' does not exist.");
                return;
            }
        } else {
            guild = guildManager.getGuildOfPlayer(player.getUniqueId());
            if (guild == null) {
                player.sendMessage("You are not in a guild. Use /guild info <name> to look up a specific guild.");
                return;
            }
        }
        String leaderName = Bukkit.getOfflinePlayer(guild.getLeader()).getName();
        player.sendMessage("=== Guild: " + guild.getName() + " ===");
        player.sendMessage("Leader: " + leaderName);
        player.sendMessage("Members: " + guild.getMemberCount() + "/" + GuildManager.MAX_MEMBERS);
    }

    private void handleList(Player player) {
        Map<String, GuildManager.Guild> all = guildManager.getAllGuilds();
        if (all.isEmpty()) {
            player.sendMessage("No guilds have been created yet.");
            return;
        }
        player.sendMessage("=== Guilds (" + all.size() + ") ===");
        for (GuildManager.Guild g : all.values()) {
            player.sendMessage("- " + g.getName() + " (" + g.getMemberCount() + " members)");
        }
    }

    private void sendUsage(Player player) {
        player.sendMessage("Usage: /guild <create|disband|invite|kick|leave|info|list>");
    }
}
