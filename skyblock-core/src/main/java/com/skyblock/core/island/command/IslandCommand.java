package com.skyblock.core.island.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.skyblock.core.manager.IslandManager;

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
            Arrays.asList("create", "home", "visit", "invite", "kick", "leave", "upgrade", "upgrades", "warp", "setwarp", "info", "trustee", "blocks", "history");

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
            player.sendMessage("Usage: /island <create|home|visit|invite|kick|leave|upgrade|upgrades|warp|setwarp|info|trustee|blocks|history>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create"   -> handleCreate(player);
            case "home"     -> handleHome(player);
            case "visit"    -> handleVisit(player, args);
            case "invite"   -> handleInvite(player, args);
            case "kick"     -> handleKick(player, args);
            case "leave"    -> handleLeave(player);
            case "upgrade"  -> handleUpgrade(player, args);
            case "upgrades" -> handleUpgrades(player);
            case "setwarp"  -> handleSetWarp(player, args);
            case "warp"     -> handleWarp(player, args);
            case "info"     -> handleInfo(player);
            case "trustee"  -> handleTrustee(player, args);
            case "blocks"   -> handleBlocks(player, args);
            case "history"  -> handleHistory(player);
            default         -> player.sendMessage("Unknown subcommand. Usage: /island <create|home|visit|invite|kick|leave|upgrade|upgrades|warp|setwarp|info|trustee|blocks|history>");
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
            if (sub.equals("upgrade")) {
                String prefix = args[1].toUpperCase();
                return Arrays.stream(IslandManager.IslandUpgrade.values())
                        .map(Enum::name)
                        .filter(name -> name.startsWith(prefix))
                        .collect(Collectors.toList());
            }
            if (sub.equals("trustee")) {
                String prefix = args[1].toLowerCase();
                return Arrays.asList("add", "remove").stream()
                        .filter(s -> s.startsWith(prefix))
                        .collect(Collectors.toList());
            }
            if (sub.equals("blocks")) {
                String prefix = args[1].toLowerCase();
                return Collections.singletonList("add").stream()
                        .filter(s -> s.startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        if (args.length == 3) {
            String sub = args[0].toLowerCase();
            if (sub.equals("trustee")) {
                String prefix = args[2].toLowerCase();
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
        UUID ownerUuid = island.get().getOwner();
        islandManager.getIslandWorld(ownerUuid).ifPresentOrElse(
                world -> {
                    player.sendMessage("Teleporting to your island...");
                    player.teleport(world.getSpawnLocation());
                },
                () -> player.sendMessage("Your island world is not loaded.")
        );
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
        islandManager.getIslandWorld(target.getUniqueId()).ifPresentOrElse(
                world -> {
                    player.sendMessage("Visiting " + target.getName() + "'s island...");
                    player.teleport(world.getSpawnLocation());
                },
                () -> player.sendMessage(target.getName() + "'s island world is not loaded.")
        );
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

    private void handleUpgrade(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /island upgrade <" +
                    Arrays.stream(IslandManager.IslandUpgrade.values())
                            .map(Enum::name)
                            .collect(Collectors.joining("|")) + ">");
            return;
        }
        if (!islandManager.hasIsland(player.getUniqueId())) {
            player.sendMessage("You do not have an island. Use /island create first.");
            return;
        }
        IslandManager.IslandUpgrade upgrade;
        try {
            upgrade = IslandManager.IslandUpgrade.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown upgrade: " + args[1]);
            return;
        }
        boolean applied = islandManager.applyUpgrade(player.getUniqueId(), upgrade);
        if (!applied) {
            player.sendMessage(upgrade.getDisplayName() + " is already at max level (" + upgrade.getMaxLevel() + ").");
            return;
        }
        int newLevel = islandManager.getIsland(player.getUniqueId())
                .map(i -> i.getUpgradeLevel(upgrade))
                .orElse(0);
        player.sendMessage(upgrade.getDisplayName() + " upgraded to level " + newLevel + " / " + upgrade.getMaxLevel() + ".");
    }

    private void handleUpgrades(Player player) {
        Optional<IslandManager.SkyBlockIsland> islandOpt =
                islandManager.getIsland(player.getUniqueId());
        if (islandOpt.isEmpty()) {
            player.sendMessage("You do not have an island.");
            return;
        }
        IslandManager.SkyBlockIsland island = islandOpt.get();
        player.sendMessage("=== Island Upgrades ===");
        for (IslandManager.IslandUpgrade upgrade : IslandManager.IslandUpgrade.values()) {
            int level = island.getUpgradeLevel(upgrade);
            player.sendMessage(upgrade.getDisplayName() + ": " + level + " / " + upgrade.getMaxLevel());
        }
    }

    private void handleSetWarp(Player player, String[] args) {
        if (!islandManager.hasIsland(player.getUniqueId())) {
            player.sendMessage("You do not have an island. Use /island create first.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("Usage: /island setwarp <name>");
            return;
        }
        String name = args[1];
        islandManager.setWarpName(player.getUniqueId(), name);
        player.sendMessage("Island warp name set to: " + name);
    }

    private void handleWarp(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /island warp <player>");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            player.sendMessage("Player '" + args[1] + "' is not online.");
            return;
        }
        if (!islandManager.hasIsland(target.getUniqueId())) {
            player.sendMessage(target.getName() + " does not have an island.");
            return;
        }
        String warpName = islandManager.getWarpName(target.getUniqueId());
        if (warpName == null) {
            player.sendMessage(target.getName() + " has not set a warp name.");
            return;
        }
        islandManager.getIslandWorld(target.getUniqueId()).ifPresentOrElse(
                world -> {
                    player.sendMessage("Warping to " + warpName + "...");
                    player.teleport(world.getSpawnLocation());
                },
                () -> player.sendMessage(target.getName() + "'s island world is not loaded.")
        );
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

    private void handleInfo(Player player) {
        UUID id = player.getUniqueId();
        IslandManager.IslandData data = islandManager.getOrCreateIslandData(id);
        player.sendMessage("=== Island Info ===");
        player.sendMessage("  Owner: " + player.getName());
        player.sendMessage("  Level: " + data.level());
        player.sendMessage("  Blocks Placed: " + data.blocksPlaced());
        List<UUID> trustees = data.trustees();
        if (trustees.isEmpty()) {
            player.sendMessage("  Trustees: none");
        } else {
            player.sendMessage("  Trustees (" + trustees.size() + "):");
            for (UUID t : trustees) {
                org.bukkit.entity.Player online = Bukkit.getPlayer(t);
                String name = online != null ? online.getName() : t.toString();
                player.sendMessage("    - " + name);
            }
        }
    }

    private void handleTrustee(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /island trustee <add|remove> <player>");
            return;
        }
        String op = args[1].toLowerCase();
        Player target = Bukkit.getPlayerExact(args[2]);
        if (target == null) {
            player.sendMessage("Player '" + args[2] + "' is not online.");
            return;
        }
        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage("You cannot trustee yourself.");
            return;
        }
        UUID id = player.getUniqueId();
        if (op.equals("add")) {
            boolean added = islandManager.addTrustee(id, target.getUniqueId());
            if (!added) {
                player.sendMessage(target.getName() + " is already a trustee.");
                return;
            }
            player.sendMessage(target.getName() + " has been added as a trustee.");
            target.sendMessage(player.getName() + " has added you as a trustee on their island.");
        } else if (op.equals("remove")) {
            boolean removed = islandManager.removeTrustee(id, target.getUniqueId());
            if (!removed) {
                player.sendMessage(target.getName() + " is not a trustee.");
                return;
            }
            player.sendMessage(target.getName() + " has been removed as a trustee.");
            target.sendMessage(player.getName() + " has removed you as a trustee on their island.");
        } else {
            player.sendMessage("Usage: /island trustee <add|remove> <player>");
        }
    }

    private void handleHistory(Player player) {
        List<String> history = islandManager.getIslandHistory(player.getUniqueId());
        player.sendMessage("=== Island History ===");
        if (history.isEmpty()) {
            player.sendMessage("No history recorded.");
            return;
        }
        for (int i = 0; i < history.size(); i++) {
            player.sendMessage((i + 1) + ". " + history.get(i));
        }
    }

    private void handleBlocks(Player player, String[] args) {
        UUID id = player.getUniqueId();
        if (args.length >= 2 && args[1].equalsIgnoreCase("add")) {
            if (!player.isOp()) {
                player.sendMessage("You do not have permission to use this subcommand.");
                return;
            }
            if (args.length < 3) {
                player.sendMessage("Usage: /island blocks add <amount>");
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
            islandManager.addBlocksPlaced(id, amount);
            long total = islandManager.getOrCreateIslandData(id).blocksPlaced();
            player.sendMessage("Blocks placed: " + total + ".");
        } else {
            long blocks = islandManager.getOrCreateIslandData(id).blocksPlaced();
            player.sendMessage("Blocks placed on your island: " + blocks);
        }
    }
}
