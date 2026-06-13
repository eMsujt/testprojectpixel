package com.skyblock.core.npc;

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
 * Handles the {@code /npcmanager} command.
 *
 * <p>Usage:
 * <ul>
 *   <li>{@code /npcmanager list}              — list all registered NPCs</li>
 *   <li>{@code /npcmanager types}             — list all NPC types</li>
 *   <li>{@code /npcmanager info <uuid>}       — show details for an NPC</li>
 *   <li>{@code /npcmanager unregister <uuid>} — remove a registered NPC</li>
 * </ul>
 * </p>
 */
public final class NPCCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("list", "types", "info", "unregister");

    private final NPCManager npcManager;

    public NPCCommand(NPCManager npcManager) {
        this.npcManager = npcManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"       -> handleList(player);
            case "types"      -> handleTypes(player);
            case "info"       -> handleInfo(player, args);
            case "unregister" -> handleUnregister(player, args);
            default           -> sendUsage(player);
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

    private void handleList(Player player) {
        var all = npcManager.getAllNPCs();
        player.sendMessage("=== Registered NPCs ===");
        if (all.isEmpty()) {
            player.sendMessage("No NPCs are registered.");
            return;
        }
        for (NPCManager.NPCData npc : all) {
            player.sendMessage("- " + npc.id + " | " + npc.type.name() + " | " + npc.name
                    + " @ " + npc.world + " (" + npc.x + ", " + npc.y + ", " + npc.z + ")");
        }
    }

    private void handleTypes(Player player) {
        player.sendMessage("=== NPC Types ===");
        for (NPCManager.NPCType type : NPCManager.NPCType.values()) {
            player.sendMessage("- " + type.name());
        }
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /npcmanager info <uuid>");
            return;
        }
        UUID id = parseUUID(player, args[1]);
        if (id == null) return;
        NPCManager.NPCData npc = npcManager.getNPC(id);
        if (npc == null) {
            player.sendMessage("No NPC found with id: " + args[1]);
            return;
        }
        player.sendMessage("=== NPC Info ===");
        player.sendMessage("ID:       " + npc.id);
        player.sendMessage("Type:     " + npc.type.name());
        player.sendMessage("Name:     " + npc.name);
        player.sendMessage("World:    " + npc.world);
        player.sendMessage("Position: " + npc.x + ", " + npc.y + ", " + npc.z);
        player.sendMessage("Dialogue: " + npc.dialogue);
    }

    private void handleUnregister(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /npcmanager unregister <uuid>");
            return;
        }
        UUID id = parseUUID(player, args[1]);
        if (id == null) return;
        if (npcManager.unregister(id)) {
            player.sendMessage("NPC " + id + " unregistered.");
        } else {
            player.sendMessage("No NPC found with id: " + args[1]);
        }
    }

    private UUID parseUUID(Player player, String raw) {
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException e) {
            player.sendMessage("Invalid UUID: " + raw);
            return null;
        }
    }

    private void sendUsage(Player player) {
        player.sendMessage("Usage: /npcmanager <list|types|info <uuid>|unregister <uuid>>");
    }
}
