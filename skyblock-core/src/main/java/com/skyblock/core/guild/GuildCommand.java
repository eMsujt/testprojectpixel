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
            "create", "invite", "accept", "decline", "kick", "leave", "disband", "info", "rank", "xp", "bank"
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
            case "rank"    -> handleRank(player, args);
            case "xp"      -> handleXp(player, args);
            case "bank"    -> handleBank(player, args);
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
            if (sub.equals("xp")) {
                return Collections.singletonList("add").stream()
                        .filter(s -> s.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (sub.equals("bank")) {
                return Arrays.asList("deposit", "withdraw").stream()
                        .filter(s -> s.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (sub.equals("invite") || sub.equals("kick") || sub.equals("rank")) {
                String prefix = args[1].toLowerCase();
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(n -> n.toLowerCase().startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("rank")) {
            String prefix = args[2].toLowerCase();
            return Arrays.stream(GuildManager.GuildRank.values())
                    .filter(r -> r != GuildManager.GuildRank.GUILD_MASTER)
                    .map(r -> r.name().toLowerCase())
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
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
        player.sendMessage("Leader: " + leaderName + " [" + GuildManager.GuildRank.GUILD_MASTER.displayName() + "]");
        player.sendMessage("Members (" + guild.members().size() + "):");
        for (UUID memberId : guild.members()) {
            Player memberPlayer = Bukkit.getPlayer(memberId);
            String memberName = memberPlayer != null ? memberPlayer.getName() : memberId.toString();
            GuildManager.GuildRank rank = guild.memberRanks().getOrDefault(memberId, GuildManager.GuildRank.RECRUIT);
            player.sendMessage("  - " + memberName + " [" + rank.displayName() + "]");
        }
    }

    private void handleRank(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /guild rank <player> <rank>");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        GuildManager.GuildRank rank;
        try {
            rank = GuildManager.GuildRank.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown rank '" + args[2] + "'. Valid ranks: officer, member, recruit");
            return;
        }
        try {
            guildManager.setRank(player.getUniqueId(), target.getUniqueId(), rank);
            player.sendMessage("Set " + target.getName() + "'s rank to " + rank.displayName() + ".");
            target.sendMessage("Your guild rank has been set to " + rank.displayName() + ".");
        } catch (IllegalArgumentException | IllegalStateException e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void handleXp(Player player, String[] args) {
        GuildManager.Guild guild = guildManager.getGuild(player.getUniqueId());
        if (guild == null) {
            player.sendMessage("You are not in a guild.");
            return;
        }
        if (args.length >= 2 && args[1].equalsIgnoreCase("add")) {
            if (!player.isOp()) {
                player.sendMessage("You do not have permission to use this subcommand.");
                return;
            }
            if (args.length < 3) {
                player.sendMessage("Usage: /guild xp add <amount>");
                return;
            }
            long amount;
            try {
                amount = Long.parseLong(args[2]);
                if (amount < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid amount: " + args[2]);
                return;
            }
            guildManager.addXp(guild.name(), amount);
            player.sendMessage("Added " + amount + " XP to guild '" + guild.name() + "'. Total: " + guildManager.getXp(guild.name()));
        } else {
            long xp = guildManager.getXp(guild.name());
            player.sendMessage("Guild '" + guild.name() + "' XP: " + xp);
        }
    }

    private void handleBank(Player player, String[] args) {
        GuildManager.Guild guild = guildManager.getGuild(player.getUniqueId());
        if (guild == null) {
            player.sendMessage("You are not in a guild.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("Guild '" + guild.name() + "' bank balance: " + guildManager.getBankBalance(guild.name()));
            return;
        }
        String action = args[1].toLowerCase();
        if (!action.equals("deposit") && !action.equals("withdraw")) {
            player.sendMessage("Usage: /guild bank [deposit|withdraw <amount>]");
            return;
        }
        if (args.length < 3) {
            player.sendMessage("Usage: /guild bank " + action + " <amount>");
            return;
        }
        long amount;
        try {
            amount = Long.parseLong(args[2]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount: " + args[2]);
            return;
        }
        try {
            if (action.equals("deposit")) {
                guildManager.depositBank(guild.name(), amount);
                player.sendMessage("Deposited " + amount + " into the guild bank. Balance: " + guildManager.getBankBalance(guild.name()));
            } else {
                guildManager.withdrawBank(guild.name(), amount);
                player.sendMessage("Withdrew " + amount + " from the guild bank. Balance: " + guildManager.getBankBalance(guild.name()));
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            player.sendMessage(e.getMessage());
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
        player.sendMessage("/guild rank <player> <rank> — set a member's rank (leader only)");
        player.sendMessage("/guild xp [add <amount>]  — view or add guild XP");
        player.sendMessage("/guild bank [deposit|withdraw <amount>] — view or manage the guild bank");
    }
}
