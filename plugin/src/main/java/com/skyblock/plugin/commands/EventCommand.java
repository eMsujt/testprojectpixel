package com.skyblock.plugin.commands;

import com.skyblock.core.event.EventManager;
import com.skyblock.core.event.EventManager.EventStatus;
import com.skyblock.core.event.EventManager.SkyBlockEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class EventCommand implements CommandExecutor {

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
            case "current" -> handleCurrent(player);
            case "list"    -> handleList(player);
            case "join"    -> handleJoin(player, args);
            case "status"  -> handleStatus(player, args);
            default        -> sendHelp(player);
        }
        return true;
    }

    private void handleCurrent(Player player) {
        EventManager.getInstance().getActiveEvent().ifPresentOrElse(
            event -> player.sendMessage("Current event: " + event.getDisplayName()),
            () -> player.sendMessage("No bonus event is currently active.")
        );
    }

    private void handleList(Player player) {
        player.sendMessage("=== SkyBlock Events ===");
        for (SkyBlockEvent event : SkyBlockEvent.values()) {
            EventStatus status = EventManager.getInstance().getStatus(player.getUniqueId(), event);
            player.sendMessage("  " + event.getDisplayName() + " — " + status.name());
        }
    }

    private void handleJoin(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblock event join <event>");
            return;
        }
        SkyBlockEvent event;
        try {
            event = SkyBlockEvent.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown event: " + args[1] + ". Use /skyblock event list to see available events.");
            return;
        }
        UUID id = player.getUniqueId();
        EventStatus current = EventManager.getInstance().getStatus(id, event);
        if (current == EventStatus.ACTIVE) {
            player.sendMessage("You have already joined " + event.getDisplayName() + ".");
            return;
        }
        EventManager.getInstance().joinEvent(id, event);
        player.sendMessage("You joined " + event.getDisplayName() + "! Good luck!");
    }

    private void handleStatus(Player player, String[] args) {
        UUID id = player.getUniqueId();
        if (args.length >= 2) {
            SkyBlockEvent event;
            try {
                event = SkyBlockEvent.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown event: " + args[1] + ". Use /skyblock event list to see available events.");
                return;
            }
            EventStatus status = EventManager.getInstance().getStatus(id, event);
            long score = EventManager.getInstance().getScore(id, event);
            player.sendMessage("=== " + event.getDisplayName() + " ===");
            player.sendMessage("Status: " + status.name());
            player.sendMessage("Score: " + score);
            return;
        }

        player.sendMessage("=== Your Event Status ===");
        boolean any = false;
        for (SkyBlockEvent event : SkyBlockEvent.values()) {
            EventStatus status = EventManager.getInstance().getStatus(id, event);
            if (status != EventStatus.NOT_JOINED) {
                long score = EventManager.getInstance().getScore(id, event);
                player.sendMessage("  " + event.getDisplayName() + " — " + status.name() + " (score: " + score + ")");
                any = true;
            }
        }
        if (!any) {
            player.sendMessage("You have not joined any events. Use /skyblock event join <event> to participate.");
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Event Commands ===");
        player.sendMessage("/skyblock event current          — show the active bonus event");
        player.sendMessage("/skyblock event list             — list all SkyBlock events and your status");
        player.sendMessage("/skyblock event join <event>     — join a SkyBlock event");
        player.sendMessage("/skyblock event status [event]   — show your event score and status");
    }
}
