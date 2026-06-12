package com.skyblock.core.party;

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
 * Handles the {@code /party} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /party invite <player>}  — invite a player to your party</li>
 *   <li>{@code /party accept <player>}  — accept a party invite</li>
 *   <li>{@code /party decline <player>} — decline a party invite</li>
 *   <li>{@code /party kick <player>}    — kick a member from your party (leader only)</li>
 *   <li>{@code /party leave}            — leave your current party</li>
 *   <li>{@code /party list}             — list all members of your party</li>
 * </ul>
 * </p>
 */
public final class PartyCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("invite", "accept", "decline", "kick", "leave", "list");

    private final PartyManager partyManager;

    public PartyCommand(PartyManager partyManager) {
        this.partyManager = partyManager;
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
            case "invite"  -> handleInvite(player, args);
            case "accept"  -> handleAccept(player, args);
            case "decline" -> handleDecline(player, args);
            case "kick"    -> handleKick(player, args);
            case "leave"   -> handleLeave(player);
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
        return Collections.emptyList();
    }

    // -------------------------------------------------------------------------
    // Subcommand handlers
    // -------------------------------------------------------------------------

    private void handleInvite(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /party invite <player>");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        if (target.equals(player)) {
            player.sendMessage("You cannot invite yourself.");
            return;
        }
        if (partyManager.inParty(target.getUniqueId())) {
            player.sendMessage(target.getName() + " is already in a party.");
            return;
        }

        // Create party if the leader doesn't have one yet
        if (!partyManager.inParty(player.getUniqueId())) {
            partyManager.createParty(player.getUniqueId());
        } else {
            PartyManager.Party party = partyManager.getParty(player.getUniqueId());
            if (!party.getLeader().equals(player.getUniqueId())) {
                player.sendMessage("Only the party leader can invite players.");
                return;
            }
        }

        partyManager.sendInvite(player.getUniqueId(), target.getUniqueId());
        player.sendMessage("Party invite sent to " + target.getName() + ".");
        target.sendMessage(player.getName() + " has invited you to their party. Use /party accept "
                + player.getName() + " to join.");
    }

    private void handleAccept(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /party accept <player>");
            return;
        }
        Player leader = Bukkit.getPlayerExact(args[1]);
        if (leader == null) {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        if (!partyManager.hasInvite(leader.getUniqueId(), player.getUniqueId())) {
            player.sendMessage("No pending party invite from " + leader.getName() + ".");
            return;
        }
        if (partyManager.inParty(player.getUniqueId())) {
            player.sendMessage("You are already in a party. Leave it first with /party leave.");
            return;
        }

        partyManager.clearInvite(player.getUniqueId());
        partyManager.joinParty(leader.getUniqueId(), player.getUniqueId());

        player.sendMessage("You joined " + leader.getName() + "'s party!");
        leader.sendMessage(player.getName() + " joined your party!");

        // Notify other members
        PartyManager.Party party = partyManager.getParty(player.getUniqueId());
        for (UUID memberId : party.getMembers()) {
            if (memberId.equals(player.getUniqueId())) continue;
            Player member = Bukkit.getPlayer(memberId);
            if (member != null) {
                member.sendMessage(player.getName() + " joined the party.");
            }
        }
    }

    private void handleDecline(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /party decline <player>");
            return;
        }
        Player leader = Bukkit.getPlayerExact(args[1]);
        if (leader == null) {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        if (!partyManager.hasInvite(leader.getUniqueId(), player.getUniqueId())) {
            player.sendMessage("No pending party invite from " + leader.getName() + ".");
            return;
        }
        partyManager.clearInvite(player.getUniqueId());
        player.sendMessage("You declined the party invite from " + leader.getName() + ".");
        leader.sendMessage(player.getName() + " declined your party invite.");
    }

    private void handleKick(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /party kick <player>");
            return;
        }
        PartyManager.Party party = partyManager.getParty(player.getUniqueId());
        if (party == null) {
            player.sendMessage("You are not in a party.");
            return;
        }
        if (!party.getLeader().equals(player.getUniqueId())) {
            player.sendMessage("Only the party leader can kick members.");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        UUID targetId = target != null ? target.getUniqueId() : null;

        // Allow kicking by name even if offline by searching party members
        if (targetId == null) {
            for (UUID memberId : party.getMembers()) {
                Player online = Bukkit.getPlayer(memberId);
                if (online != null && online.getName().equalsIgnoreCase(args[1])) {
                    targetId = memberId;
                    target = online;
                    break;
                }
            }
        }

        if (targetId == null || !party.getMembers().contains(targetId)) {
            player.sendMessage("'" + args[1] + "' is not in your party.");
            return;
        }

        partyManager.kickFromParty(targetId);
        player.sendMessage("You kicked " + args[1] + " from the party.");
        if (target != null) {
            target.sendMessage("You were kicked from " + player.getName() + "'s party.");
        }

        // Notify remaining members
        for (UUID memberId : party.getMembers()) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null) {
                member.sendMessage(args[1] + " was kicked from the party.");
            }
        }
    }

    private void handleLeave(Player player) {
        if (!partyManager.inParty(player.getUniqueId())) {
            player.sendMessage("You are not in a party.");
            return;
        }
        PartyManager.Party party = partyManager.getParty(player.getUniqueId());
        boolean wasLeader = party.getLeader().equals(player.getUniqueId());
        Set<UUID> allMembers = party.getAllMembers();

        partyManager.leaveParty(player.getUniqueId());
        player.sendMessage(wasLeader ? "You disbanded the party." : "You left the party.");

        for (UUID memberId : allMembers) {
            if (memberId.equals(player.getUniqueId())) continue;
            Player member = Bukkit.getPlayer(memberId);
            if (member != null) {
                member.sendMessage(wasLeader
                        ? player.getName() + " disbanded the party."
                        : player.getName() + " left the party.");
            }
        }
    }

    private void handleList(Player player) {
        PartyManager.Party party = partyManager.getParty(player.getUniqueId());
        if (party == null) {
            player.sendMessage("You are not in a party.");
            return;
        }
        Player leader = Bukkit.getPlayer(party.getLeader());
        String leaderName = leader != null ? leader.getName() : party.getLeader().toString();
        player.sendMessage("=== Party ===");
        player.sendMessage("Leader: " + leaderName);
        Set<UUID> members = party.getMembers();
        if (members.isEmpty()) {
            player.sendMessage("Members: (none)");
        } else {
            StringBuilder sb = new StringBuilder("Members: ");
            for (UUID memberId : members) {
                Player member = Bukkit.getPlayer(memberId);
                sb.append(member != null ? member.getName() : memberId.toString()).append(", ");
            }
            player.sendMessage(sb.substring(0, sb.length() - 2));
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void sendHelp(Player player) {
        player.sendMessage("=== Party Commands ===");
        player.sendMessage("/party invite <player>  — invite a player to your party");
        player.sendMessage("/party accept <player>  — accept a party invite");
        player.sendMessage("/party decline <player> — decline a party invite");
        player.sendMessage("/party kick <player>    — kick a member (leader only)");
        player.sendMessage("/party leave            — leave your current party");
        player.sendMessage("/party list             — list all party members");
    }
}
