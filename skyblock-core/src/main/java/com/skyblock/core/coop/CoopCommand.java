package com.skyblock.core.coop;

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
 * Handles the {@code /coop} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /coop invite <player>}  — invite a player to your co-op island</li>
 *   <li>{@code /coop accept <player>}  — accept a co-op invite</li>
 *   <li>{@code /coop decline <player>} — decline a co-op invite</li>
 *   <li>{@code /coop kick <player>}    — kick a member from your co-op (owner only)</li>
 *   <li>{@code /coop leave}            — leave your current co-op group</li>
 *   <li>{@code /coop list}             — list all members of your co-op group</li>
 * </ul>
 * </p>
 */
public final class CoopCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("invite", "accept", "decline", "kick", "leave", "list");

    private final CoopManager coopManager;

    public CoopCommand(CoopManager coopManager) {
        this.coopManager = coopManager;
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
            player.sendMessage("Usage: /coop invite <player>");
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
        if (coopManager.inCoop(target.getUniqueId())) {
            player.sendMessage(target.getName() + " is already in a co-op group.");
            return;
        }

        // Create co-op group if the owner doesn't have one yet
        if (!coopManager.inCoop(player.getUniqueId())) {
            coopManager.createCoop(player.getUniqueId());
        } else {
            UUID owner = coopManager.getOwner(player.getUniqueId());
            if (!owner.equals(player.getUniqueId())) {
                player.sendMessage("Only the island owner can invite players.");
                return;
            }
        }

        coopManager.sendInvite(player.getUniqueId(), target.getUniqueId());
        player.sendMessage("Co-op invite sent to " + target.getName() + ".");
        target.sendMessage(player.getName() + " has invited you to their co-op island. Use /coop accept "
                + player.getName() + " to join.");
    }

    private void handleAccept(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /coop accept <player>");
            return;
        }
        Player owner = Bukkit.getPlayerExact(args[1]);
        if (owner == null) {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        if (!coopManager.hasInvite(owner.getUniqueId(), player.getUniqueId())) {
            player.sendMessage("No pending co-op invite from " + owner.getName() + ".");
            return;
        }
        if (coopManager.inCoop(player.getUniqueId())) {
            player.sendMessage("You are already in a co-op group. Leave it first with /coop leave.");
            return;
        }

        coopManager.clearInvite(player.getUniqueId());
        coopManager.joinCoop(owner.getUniqueId(), player.getUniqueId());

        player.sendMessage("You joined " + owner.getName() + "'s co-op island!");
        owner.sendMessage(player.getName() + " joined your co-op island!");

        // Notify other members
        for (UUID memberId : coopManager.getMembers(owner.getUniqueId())) {
            if (memberId.equals(player.getUniqueId()) || memberId.equals(owner.getUniqueId())) continue;
            Player member = Bukkit.getPlayer(memberId);
            if (member != null) {
                member.sendMessage(player.getName() + " joined the co-op island.");
            }
        }
    }

    private void handleDecline(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /coop decline <player>");
            return;
        }
        Player owner = Bukkit.getPlayerExact(args[1]);
        if (owner == null) {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        if (!coopManager.hasInvite(owner.getUniqueId(), player.getUniqueId())) {
            player.sendMessage("No pending co-op invite from " + owner.getName() + ".");
            return;
        }
        coopManager.clearInvite(player.getUniqueId());
        player.sendMessage("You declined the co-op invite from " + owner.getName() + ".");
        owner.sendMessage(player.getName() + " declined your co-op invite.");
    }

    private void handleKick(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /coop kick <player>");
            return;
        }
        UUID ownerId = coopManager.getOwner(player.getUniqueId());
        if (ownerId == null) {
            player.sendMessage("You are not in a co-op group.");
            return;
        }
        if (!ownerId.equals(player.getUniqueId())) {
            player.sendMessage("Only the island owner can kick members.");
            return;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        UUID targetId = target != null ? target.getUniqueId() : null;

        // Allow kicking by name even if offline by searching co-op members
        if (targetId == null) {
            for (UUID memberId : coopManager.getMembers(player.getUniqueId())) {
                Player online = Bukkit.getPlayer(memberId);
                if (online != null && online.getName().equalsIgnoreCase(args[1])) {
                    targetId = memberId;
                    target = online;
                    break;
                }
            }
        }

        if (targetId == null || !coopManager.getMembers(player.getUniqueId()).contains(targetId)
                || targetId.equals(player.getUniqueId())) {
            player.sendMessage("'" + args[1] + "' is not a member of your co-op.");
            return;
        }

        coopManager.kickFromCoop(targetId);
        player.sendMessage("You kicked " + args[1] + " from your co-op island.");
        if (target != null) {
            target.sendMessage("You were kicked from " + player.getName() + "'s co-op island.");
        }

        for (UUID memberId : coopManager.getMembers(player.getUniqueId())) {
            if (memberId.equals(player.getUniqueId())) continue;
            Player member = Bukkit.getPlayer(memberId);
            if (member != null) {
                member.sendMessage(args[1] + " was kicked from the co-op island.");
            }
        }
    }

    private void handleLeave(Player player) {
        if (!coopManager.inCoop(player.getUniqueId())) {
            player.sendMessage("You are not in a co-op group.");
            return;
        }
        UUID ownerId = coopManager.getOwner(player.getUniqueId());
        boolean wasOwner = ownerId.equals(player.getUniqueId());
        Set<UUID> allMembers = coopManager.getMembers(ownerId);

        coopManager.leaveCoop(player.getUniqueId());
        player.sendMessage(wasOwner ? "You disbanded your co-op island." : "You left the co-op island.");

        for (UUID memberId : allMembers) {
            if (memberId.equals(player.getUniqueId())) continue;
            Player member = Bukkit.getPlayer(memberId);
            if (member != null) {
                member.sendMessage(wasOwner
                        ? player.getName() + " disbanded the co-op island."
                        : player.getName() + " left the co-op island.");
            }
        }
    }

    private void handleList(Player player) {
        UUID ownerId = coopManager.getOwner(player.getUniqueId());
        if (ownerId == null) {
            player.sendMessage("You are not in a co-op group.");
            return;
        }
        Player ownerPlayer = Bukkit.getPlayer(ownerId);
        String ownerName = ownerPlayer != null ? ownerPlayer.getName() : ownerId.toString();
        player.sendMessage("=== Co-op Island ===");
        player.sendMessage("Owner: " + ownerName);
        Set<UUID> members = coopManager.getMembers(ownerId);
        StringBuilder sb = new StringBuilder("Members: ");
        int count = 0;
        for (UUID memberId : members) {
            if (memberId.equals(ownerId)) continue;
            Player member = Bukkit.getPlayer(memberId);
            sb.append(member != null ? member.getName() : memberId.toString()).append(", ");
            count++;
        }
        if (count == 0) {
            player.sendMessage("Members: (none)");
        } else {
            player.sendMessage(sb.substring(0, sb.length() - 2));
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void sendHelp(Player player) {
        player.sendMessage("=== Co-op Commands ===");
        player.sendMessage("/coop invite <player>  — invite a player to your co-op island");
        player.sendMessage("/coop accept <player>  — accept a co-op invite");
        player.sendMessage("/coop decline <player> — decline a co-op invite");
        player.sendMessage("/coop kick <player>    — kick a member (owner only)");
        player.sendMessage("/coop leave            — leave your current co-op");
        player.sendMessage("/coop list             — list all co-op members");
    }
}
