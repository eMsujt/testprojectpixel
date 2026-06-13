package com.skyblock.core.event;

import com.skyblock.core.event.EventManager.EventStatus;
import com.skyblock.core.event.EventManager.SkyBlockEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Handles the {@code /event} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /event list}               — list all SkyBlock events</li>
 *   <li>{@code /event join <event>}        — join an event</li>
 *   <li>{@code /event status [event]}      — show status/score for one or all events</li>
 *   <li>{@code /event reset}              — reset all event data</li>
 * </ul>
 * </p>
 */
public final class EventCommand implements TabExecutor {

    private final EventManager eventManager;

    public EventCommand(EventManager eventManager) {
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
            case "status" -> handleStatus(player, args);
            case "reset"  -> handleReset(player);
            default       -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Arrays.asList("list", "join", "status", "reset").stream()
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("join") || sub.equals("status")) {
                String lower = args[1].toLowerCase();
                return Arrays.stream(SkyBlockEvent.values())
                        .map(e -> e.name().toLowerCase())
                        .filter(s -> s.startsWith(lower))
                        .toList();
            }
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        player.sendMessage("=== SkyBlock Events ===");
        for (SkyBlockEvent event : SkyBlockEvent.values()) {
            player.sendMessage("- " + event.getDisplayName());
        }
    }

    private void handleJoin(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /event join <event>");
            return;
        }
        SkyBlockEvent event;
        try {
            event = SkyBlockEvent.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown event: " + args[1]);
            return;
        }
        EventStatus current = eventManager.getStatus(player.getUniqueId(), event);
        if (current == EventStatus.ACTIVE) {
            player.sendMessage("You have already joined " + event.getDisplayName() + ".");
            return;
        }
        eventManager.joinEvent(player.getUniqueId(), event);
        player.sendMessage("You joined the " + event.getDisplayName() + "!");
    }

    private void handleStatus(Player player, String[] args) {
        if (args.length >= 2) {
            SkyBlockEvent event;
            try {
                event = SkyBlockEvent.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown event: " + args[1]);
                return;
            }
            sendEventStatus(player, event);
            return;
        }
        player.sendMessage("=== Event Status ===");
        boolean any = false;
        for (SkyBlockEvent event : SkyBlockEvent.values()) {
            EventStatus status = eventManager.getStatus(player.getUniqueId(), event);
            if (status != EventStatus.NOT_JOINED) {
                long score = eventManager.getScore(player.getUniqueId(), event);
                player.sendMessage(event.getDisplayName() + ": " + status.name() + " (score: " + score + ")");
                any = true;
            }
        }
        if (!any) {
            player.sendMessage("You have not joined any events.");
        }
    }

    private void sendEventStatus(Player player, SkyBlockEvent event) {
        EventStatus status = eventManager.getStatus(player.getUniqueId(), event);
        if (status == EventStatus.NOT_JOINED) {
            player.sendMessage("You have not joined " + event.getDisplayName() + ".");
            return;
        }
        long score = eventManager.getScore(player.getUniqueId(), event);
        player.sendMessage("=== " + event.getDisplayName() + " ===");
        player.sendMessage("Status: " + status.name());
        player.sendMessage("Score: " + score);
    }

    private void handleReset(Player player) {
        eventManager.reset(player.getUniqueId());
        player.sendMessage("All event data has been reset.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Event Commands ===");
        player.sendMessage("/event list — list all SkyBlock events");
        player.sendMessage("/event join <event> — join an event");
        player.sendMessage("/event status [event] — show event status and score");
        player.sendMessage("/event reset — reset all event data");
    }
}
