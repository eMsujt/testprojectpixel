package com.skyblock.core.event;

import com.skyblock.core.event.SkyblockEventManager.SkyblockEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /skylockevent} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /skylockevent list}            — list all SkyBlock events and their multipliers</li>
 *   <li>{@code /skylockevent join <event>}    — join an event</li>
 *   <li>{@code /skylockevent leave}           — leave the current event</li>
 *   <li>{@code /skylockevent status}          — show active event and score</li>
 *   <li>{@code /skylockevent reset}           — reset all event data</li>
 * </ul>
 * </p>
 */
public final class SkyblockEventCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "join", "leave", "status", "reset");

    private final SkyblockEventManager eventManager;

    public SkyblockEventCommand(SkyblockEventManager eventManager) {
        if (eventManager == null) {
            throw new IllegalArgumentException("eventManager must not be null");
        }
        this.eventManager = eventManager;
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
            case "list"   -> handleList(player);
            case "join"   -> handleJoin(player, args);
            case "leave"  -> handleLeave(player);
            case "status" -> handleStatus(player);
            case "reset"  -> handleReset(player);
            default       -> sendHelp(player);
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
        if (args.length == 2 && "join".equals(args[0].toLowerCase())) {
            String prefix = args[1].toLowerCase();
            return Arrays.stream(SkyblockEvent.values())
                    .map(e -> e.name().toLowerCase())
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        player.sendMessage("=== SkyBlock Events ===");
        for (SkyblockEvent event : SkyblockEvent.values()) {
            player.sendMessage("- " + event.getDisplayName()
                    + " (x" + String.format("%.2f", event.getMultiplier()) + ")");
        }
    }

    private void handleJoin(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skylockevent join <event>");
            return;
        }
        SkyblockEvent event;
        try {
            event = SkyblockEvent.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown event: " + args[1]);
            return;
        }
        SkyblockEvent current = eventManager.getActiveEvent(player.getUniqueId());
        if (current == event) {
            player.sendMessage("You are already participating in " + event.getDisplayName() + ".");
            return;
        }
        eventManager.joinEvent(player.getUniqueId(), event);
        player.sendMessage("You joined the " + event.getDisplayName()
                + "! Multiplier: x" + String.format("%.2f", event.getMultiplier()));
    }

    private void handleLeave(Player player) {
        SkyblockEvent current = eventManager.getActiveEvent(player.getUniqueId());
        if (current == null) {
            player.sendMessage("You are not participating in any event.");
            return;
        }
        eventManager.leaveEvent(player.getUniqueId());
        player.sendMessage("You left the " + current.getDisplayName() + ".");
    }

    private void handleStatus(Player player) {
        SkyblockEvent current = eventManager.getActiveEvent(player.getUniqueId());
        if (current == null) {
            player.sendMessage("You are not participating in any event.");
            return;
        }
        long score = eventManager.getScore(player.getUniqueId(), current);
        player.sendMessage("=== Event Status ===");
        player.sendMessage("Event: " + current.getDisplayName());
        player.sendMessage("Multiplier: x" + String.format("%.2f", current.getMultiplier()));
        player.sendMessage("Score: " + score);
    }

    private void handleReset(Player player) {
        eventManager.reset(player.getUniqueId());
        player.sendMessage("All event data has been reset.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== SkyBlock Event Commands ===");
        player.sendMessage("/skylockevent list — list all events and their multipliers");
        player.sendMessage("/skylockevent join <event> — join an event");
        player.sendMessage("/skylockevent leave — leave your current event");
        player.sendMessage("/skylockevent status — show your active event and score");
        player.sendMessage("/skylockevent reset — reset all event data");
    }
}
