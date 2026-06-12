package com.skyblock.core.minion;

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
 * Handles the {@code /minion} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /minion list}                   — list the player's placed minions</li>
 *   <li>{@code /minion place <type> [tier]}     — place a new minion (default tier 1)</li>
 *   <li>{@code /minion upgrade <id>}            — upgrade a minion to the next tier</li>
 *   <li>{@code /minion remove <id>}             — remove a placed minion</li>
 *   <li>{@code /minion info <id>}               — show details for a specific minion</li>
 * </ul>
 * </p>
 */
public final class MinionCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("list", "place", "upgrade", "remove", "info");

    private final MinionManager minionManager;

    public MinionCommand(MinionManager minionManager) {
        this.minionManager = minionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /minion <list|place|upgrade|remove|info>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"    -> handleList(player);
            case "place"   -> handlePlace(player, args);
            case "upgrade" -> handleUpgrade(player, args);
            case "remove"  -> handleRemove(player, args);
            case "info"    -> handleInfo(player, args);
            default        -> player.sendMessage("Unknown subcommand. Usage: /minion <list|place|upgrade|remove|info>");
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
        if (args.length == 2 && args[0].equalsIgnoreCase("place")) {
            String prefix = args[1].toUpperCase();
            return Arrays.stream(MinionManager.MinionType.values())
                    .map(Enum::name)
                    .filter(n -> n.startsWith(prefix))
                    .sorted()
                    .collect(Collectors.toList());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("place")) {
            return Arrays.stream(MinionManager.MinionTier.values())
                    .map(t -> String.valueOf(t.ordinal() + 1))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        List<UUID> ids = minionManager.getMinions(player.getUniqueId());
        if (ids.isEmpty()) {
            player.sendMessage("You have no placed minions. Use /minion place <type> to add one.");
            return;
        }
        player.sendMessage("=== Your Minions ===");
        for (int i = 0; i < ids.size(); i++) {
            MinionManager.MinionData data = minionManager.getMinion(ids.get(i));
            if (data != null) {
                player.sendMessage(String.format("[%d] %s — Tier %d | ID: %s",
                        i + 1,
                        data.type.name(),
                        data.getTier().ordinal() + 1,
                        data.id.toString().substring(0, 8)));
            }
        }
    }

    private void handlePlace(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /minion place <type> [tier]");
            return;
        }
        MinionManager.MinionType type;
        try {
            type = MinionManager.MinionType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown minion type: " + args[1]
                    + ". Use /minion place with a valid type.");
            return;
        }

        MinionManager.MinionTier tier = MinionManager.MinionTier.TIER_1;
        if (args.length >= 3) {
            try {
                int tierNum = Integer.parseInt(args[2]);
                MinionManager.MinionTier[] tiers = MinionManager.MinionTier.values();
                if (tierNum < 1 || tierNum > tiers.length) {
                    player.sendMessage("Tier must be between 1 and " + tiers.length + ".");
                    return;
                }
                tier = tiers[tierNum - 1];
            } catch (NumberFormatException e) {
                player.sendMessage("Tier must be a number.");
                return;
            }
        }

        MinionManager.MinionData data = minionManager.placeMinion(player.getUniqueId(), type, tier);
        player.sendMessage("Placed " + type.name() + " Minion at Tier " + (tier.ordinal() + 1)
                + " (ID: " + data.id.toString().substring(0, 8) + ").");
    }

    private void handleUpgrade(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /minion upgrade <id>");
            return;
        }
        UUID minionId = resolveId(player, args[1]);
        if (minionId == null) return;

        boolean upgraded = minionManager.upgradeMinion(minionId);
        if (!upgraded) {
            MinionManager.MinionData data = minionManager.getMinion(minionId);
            if (data == null) {
                player.sendMessage("Minion not found: " + args[1]);
            } else {
                player.sendMessage(data.type.name() + " Minion is already at max tier ("
                        + MinionManager.MinionTier.values().length + ").");
            }
        } else {
            MinionManager.MinionData data = minionManager.getMinion(minionId);
            player.sendMessage("Upgraded to " + data.type.name() + " Minion Tier "
                    + (data.getTier().ordinal() + 1) + "!");
        }
    }

    private void handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /minion remove <id>");
            return;
        }
        UUID minionId = resolveId(player, args[1]);
        if (minionId == null) return;

        MinionManager.MinionData data = minionManager.getMinion(minionId);
        if (data == null || !data.owner.equals(player.getUniqueId())) {
            player.sendMessage("Minion not found or does not belong to you.");
            return;
        }
        minionManager.removeMinion(minionId);
        player.sendMessage("Removed " + data.type.name() + " Minion (ID: "
                + minionId.toString().substring(0, 8) + ").");
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /minion info <id>");
            return;
        }
        UUID minionId = resolveId(player, args[1]);
        if (minionId == null) return;

        MinionManager.MinionData data = minionManager.getMinion(minionId);
        if (data == null) {
            player.sendMessage("Minion not found: " + args[1]);
            return;
        }
        player.sendMessage("=== Minion Info ===");
        player.sendMessage("Type : " + data.type.name());
        player.sendMessage("Tier : " + (data.getTier().ordinal() + 1)
                + " / " + MinionManager.MinionTier.values().length);
        player.sendMessage("Owner: " + data.owner);
        player.sendMessage("ID   : " + data.id);
    }

    /**
     * Resolves a full or partial UUID string entered by the player.
     * Matches against the player's own minions using a prefix match on the short form.
     */
    private UUID resolveId(Player player, String input) {
        // Try full UUID first
        try {
            return UUID.fromString(input);
        } catch (IllegalArgumentException ignored) {
            // fall through to prefix match
        }
        // Prefix match against the player's minions
        String lower = input.toLowerCase();
        for (UUID id : minionManager.getMinions(player.getUniqueId())) {
            if (id.toString().startsWith(lower)) {
                return id;
            }
        }
        player.sendMessage("Could not find a minion matching: " + input
                + ". Use /minion list to see your minions.");
        return null;
    }
}
