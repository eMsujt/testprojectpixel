package com.skyblock.core.slayer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /slay} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /slay start <type> <tier>} — begin a slayer quest</li>
 *   <li>{@code /slay info}                — show the active quest status</li>
 *   <li>{@code /slay collect}             — collect a completed quest reward</li>
 *   <li>{@code /slay cancel}              — cancel the active quest</li>
 *   <li>{@code /slay xp [type]}           — view accumulated slayer XP</li>
 * </ul>
 * </p>
 */
public final class SlayerCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("start", "info", "collect", "cancel", "xp");

    private static final List<String> TYPE_NAMES = Arrays.stream(SlayerManager.SlayerType.values())
            .map(t -> t.name().toLowerCase())
            .collect(Collectors.toList());

    private static final List<String> TIER_NUMBERS = Arrays.asList("1", "2", "3", "4", "5");

    private final SlayerManager slayerManager;

    public SlayerCommand(SlayerManager slayerManager) {
        this.slayerManager = slayerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /slay <start|info|collect|cancel|xp>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start"   -> handleStart(player, args);
            case "info"    -> handleInfo(player);
            case "collect" -> handleCollect(player);
            case "cancel"  -> handleCancel(player);
            case "xp"      -> handleXp(player, args);
            default        -> player.sendMessage("Unknown subcommand. Usage: /slay <start|info|collect|cancel|xp>");
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
            String prefix = args[1].toLowerCase();
            if (sub.equals("start") || sub.equals("xp")) {
                return TYPE_NAMES.stream()
                        .filter(t -> t.startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("start")) {
            String prefix = args[2];
            return TIER_NUMBERS.stream()
                    .filter(n -> n.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleStart(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /slay start <type> <tier>");
            return;
        }
        SlayerManager.SlayerType type;
        try {
            type = SlayerManager.SlayerType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown slayer type: " + args[1]
                    + ". Valid types: " + TYPE_NAMES.stream().collect(Collectors.joining(", ")));
            return;
        }
        int tierNumber;
        try {
            tierNumber = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Tier must be a number between 1 and 5.");
            return;
        }
        try {
            slayerManager.startQuest(player.getUniqueId(), type, tierNumber);
            SlayerManager.SlayerTier tier = slayerManager.getTier(type, tierNumber);
            player.sendMessage(String.format("Started Tier %d %s slayer quest. Kill %d boss%s to complete it.",
                    tierNumber,
                    capitalize(type.name()),
                    tier.getBossesRequired(),
                    tier.getBossesRequired() == 1 ? "" : "es"));
        } catch (IllegalArgumentException e) {
            player.sendMessage("Tier must be between 1 and 5.");
        } catch (IllegalStateException e) {
            player.sendMessage("You already have an active slayer quest. Use /slay info to check it.");
        }
    }

    private void handleInfo(Player player) {
        SlayerManager.SlayerQuest quest = slayerManager.getActiveQuest(player.getUniqueId());
        if (quest == null) {
            player.sendMessage("You have no active slayer quest. Use /slay start <type> <tier> to begin one.");
            return;
        }
        int killed = quest.getBossesKilled();
        int required = quest.getTier().getBossesRequired();
        if (quest.isComplete()) {
            player.sendMessage(String.format("[SLAYER] %s Tier %d — COMPLETE! Use /slay collect to claim %d XP.",
                    capitalize(quest.getType().name()),
                    quest.getTier().getTier(),
                    quest.getTier().getXpReward()));
        } else {
            player.sendMessage(String.format("[SLAYER] %s Tier %d — %d/%d bosses killed.",
                    capitalize(quest.getType().name()),
                    quest.getTier().getTier(),
                    killed, required));
        }
    }

    private void handleCollect(Player player) {
        try {
            SlayerManager.SlayerQuest quest = slayerManager.collectQuest(player.getUniqueId());
            player.sendMessage(String.format("Quest complete! Earned %d %s Slayer XP.",
                    quest.getTier().getXpReward(),
                    capitalize(quest.getType().name())));
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("not yet complete")) {
                player.sendMessage("Your slayer quest is not complete yet. Use /slay info to check progress.");
            } else {
                player.sendMessage("You have no active slayer quest.");
            }
        }
    }

    private void handleCancel(Player player) {
        boolean cancelled = slayerManager.cancelQuest(player.getUniqueId());
        if (cancelled) {
            player.sendMessage("Your slayer quest has been cancelled.");
        } else {
            player.sendMessage("You have no active slayer quest to cancel.");
        }
    }

    private void handleXp(Player player, String[] args) {
        if (args.length >= 2) {
            SlayerManager.SlayerType type;
            try {
                type = SlayerManager.SlayerType.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown slayer type: " + args[1]
                        + ". Valid types: " + TYPE_NAMES.stream().collect(Collectors.joining(", ")));
                return;
            }
            long xp = slayerManager.getXp(player.getUniqueId(), type);
            player.sendMessage(capitalize(type.name()) + " Slayer XP: " + xp);
        } else {
            player.sendMessage("=== Slayer XP ===");
            for (SlayerManager.SlayerType type : SlayerManager.SlayerType.values()) {
                long xp = slayerManager.getXp(player.getUniqueId(), type);
                player.sendMessage(capitalize(type.name()) + ": " + xp + " XP");
            }
        }
    }

    private static String capitalize(String name) {
        if (name == null || name.isEmpty()) return name;
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}
