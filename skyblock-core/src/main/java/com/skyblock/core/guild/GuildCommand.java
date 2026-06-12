package com.skyblock.core.guild;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles the {@code /guild} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /guild create <name>}  — create a new guild</li>
 *   <li>{@code /guild invite <player>} — invite a player to your guild (leader only)</li>
 *   <li>{@code /guild accept}         — accept a pending guild invite</li>
 *   <li>{@code /guild decline}        — decline a pending guild invite</li>
 *   <li>{@code /guild kick <player>}  — kick a member from your guild (leader only)</li>
 *   <li>{@code /guild leave}          — leave your current guild</li>
 *   <li>{@code /guild disband}        — disband your guild (leader only)</li>
 *   <li>{@code /guild info}           — show info about your current guild</li>
 * </ul>
 * </p>
 */
public final class GuildCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList(
            "create", "invite", "accept", "decline", "kick", "leave", "disband", "info"
    );

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
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create"  -> handleCreate(player, args);
            case "invite"  -> handleInvite(player, args);
            case "accept"  -> handleAccept(player);
            case "decline" -> handleDecline(player);
            case "kick"    -> handleKick(player, args);
            case "leave"   -> handleLeave(player);
            case "disband" -> handleDisband(player);
            case "info"    -> handleInfo(player);
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
            if (sub.equals("invite") || sub.equals("kick")) {
                String prefix = args[1].toLowerCase();
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(n -> n.toLowerCase().startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /guild create <name>");
            return;
        }
        String name = args[1];
        try {
            GuildManager.Guild guild = guildManager.createGuild(name, player.getUniqueId());
            player.sendMessage("Guild '" + guild.name() + "' created.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleInvite(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /guild invite <player>");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        try {
            guildManager.sendInvite(player.getUniqueId(), target.getUniqueId());
            player.sendMessage("Invited " + target.getName() + " to your guild.");
            target.sendMessage(player.getName() + " has invited you to their guild. Use /guild accept or /guild decline.");
        } catch (IllegalStateException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleAccept(Player player) {
        try {
            GuildManager.Guild guild = guildManager.acceptInvite(player.getUniqueId());
            player.sendMessage("You joined the guild '" + guild.name() + "'.");
        } catch (IllegalStateException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleDecline(Player player) {
        boolean had = guildManager.declineInvite(player.getUniqueId());
        if (had) {
            player.sendMessage("Guild invite declined.");
        } else {
            player.sendMessage("You have no pending guild invite.");
        }
    }

    private void handleKick(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /guild kick <player>");
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
            guildManager.kickMember(player.getUniqueId(), targetId);
            player.sendMessage("Kicked " + targetName + " from the guild.");
            target.sendMessage("You have been kicked from the guild.");
        } catch (IllegalStateException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleLeave(Player player) {
        try {
            GuildManager.Guild guild = guildManager.getGuild(player.getUniqueId());
            if (guild != null && guild.leader().equals(player.getUniqueId())) {
                player.sendMessage("You are the leader. Use /guild disband to disband, or transfer leadership first.");
                return;
            }
            guildManager.leaveGuild(player.getUniqueId());
            player.sendMessage("You left the guild.");
        } catch (IllegalStateException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleDisband(Player player) {
        try {
            GuildManager.Guild guild = guildManager.getGuild(player.getUniqueId());
            if (guild == null) {
                player.sendMessage("You are not in a guild.");
                return;
            }
            String name = guild.name();
            guildManager.disbandGuild(player.getUniqueId());
            player.sendMessage("Guild '" + name + "' has been disbanded.");
        } catch (IllegalStateException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleInfo(Player player) {
        GuildManager.Guild guild = guildManager.getGuild(player.getUniqueId());
        if (guild == null) {
            player.sendMessage("You are not in a guild.");
            return;
        }
        Player leaderPlayer = Bukkit.getPlayer(guild.leader());
        String leaderName = leaderPlayer != null ? leaderPlayer.getName() : guild.leader().toString();
        player.sendMessage("=== Guild: " + guild.name() + " ===");
        player.sendMessage("Leader: " + leaderName);
        player.sendMessage("Members (" + guild.members().size() + "):");
        for (UUID memberId : guild.members()) {
            Player memberPlayer = Bukkit.getPlayer(memberId);
            String memberName = memberPlayer != null ? memberPlayer.getName() : memberId.toString();
            player.sendMessage("  - " + memberName);
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Guild Commands ===");
        player.sendMessage("/guild create <name>   — create a guild");
        player.sendMessage("/guild invite <player> — invite a player (leader only)");
        player.sendMessage("/guild accept          — accept a guild invite");
        player.sendMessage("/guild decline         — decline a guild invite");
        player.sendMessage("/guild kick <player>   — kick a member (leader only)");
        player.sendMessage("/guild leave           — leave your guild");
        player.sendMessage("/guild disband         — disband your guild (leader only)");
        player.sendMessage("/guild info            — show guild info");
    }
}
