package com.skyblock.core.mining;

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
 * Handles the {@code /hotm} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /hotm}                     — show HOTM level, XP, and available tokens</li>
 *   <li>{@code /hotm perks}               — list all perks and the player's upgrade levels</li>
 *   <li>{@code /hotm upgrade <perk>}      — spend one token to upgrade a perk</li>
 * </ul>
 * </p>
 */
public final class HotmCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("perks", "upgrade");

    private final HotmManager hotmManager;

    public HotmCommand(HotmManager hotmManager) {
        if (hotmManager == null) {
            throw new IllegalArgumentException("hotmManager must not be null");
        }
        this.hotmManager = hotmManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleStatus(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "perks"   -> handlePerks(player);
            case "upgrade" -> handleUpgrade(player, args);
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
        if (args.length == 2 && args[0].equalsIgnoreCase("upgrade")) {
            String prefix = args[1].toLowerCase();
            return Arrays.stream(HotmManager.HotmPerk.values())
                    .map(p -> p.name().toLowerCase())
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleStatus(Player player) {
        UUID id = player.getUniqueId();
        int level = hotmManager.getLevel(id);
        double xp = hotmManager.getXp(id);
        int tok = hotmManager.getTokens(id);
        player.sendMessage("=== Heart of the Mountain ===");
        player.sendMessage("Level: " + level + "  XP: " + String.format("%.1f", xp));
        player.sendMessage("Tokens available: " + tok);
    }

    private void handlePerks(Player player) {
        UUID id = player.getUniqueId();
        player.sendMessage("=== HOTM Perks ===");
        for (HotmManager.HotmPerk perk : HotmManager.HotmPerk.values()) {
            int current = hotmManager.getPerkLevel(id, perk);
            String type = perk.isActive() ? "[Active]" : "[Passive]";
            player.sendMessage("  " + type + " " + perk.getDisplayName()
                    + ": " + current + "/" + perk.getMaxLevel());
        }
    }

    private void handleUpgrade(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /hotm upgrade <perk>");
            return;
        }
        String name = args[1].toUpperCase();
        HotmManager.HotmPerk perk;
        try {
            perk = HotmManager.HotmPerk.valueOf(name);
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown perk: " + args[1] + ". Use /hotm perks to see available perks.");
            return;
        }
        try {
            hotmManager.upgradePerk(player.getUniqueId(), perk);
            int newLevel = hotmManager.getPerkLevel(player.getUniqueId(), perk);
            player.sendMessage("Upgraded " + perk.getDisplayName() + " to level " + newLevel + "!");
        } catch (IllegalStateException e) {
            player.sendMessage("You have no tokens available. Mine to earn HOTM XP and level up.");
        } catch (IllegalArgumentException e) {
            player.sendMessage(perk.getDisplayName() + " is already at max level (" + perk.getMaxLevel() + ").");
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== HOTM Commands ===");
        player.sendMessage("/hotm                    — show HOTM level, XP, and tokens");
        player.sendMessage("/hotm perks              — list all perks and your upgrade levels");
        player.sendMessage("/hotm upgrade <perk>     — spend a token to upgrade a perk");
    }
}
