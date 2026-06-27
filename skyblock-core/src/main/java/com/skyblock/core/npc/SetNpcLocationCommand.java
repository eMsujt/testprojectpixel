package com.skyblock.core.npc;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code /setnpclocation <npc>} — places (or moves) a functional NPC at the
 * sender's current location and spawns it immediately. Also supports
 * {@code /setnpclocation list} and {@code /setnpclocation remove <npc>}.
 */
public final class SetNpcLocationCommand implements TabExecutor {

    private final JavaPlugin plugin;

    public SetNpcLocationCommand(JavaPlugin plugin) {
        this.plugin = plugin;
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

        String sub = args[0].toLowerCase();
        FunctionalNpcManager manager = FunctionalNpcManager.getInstance();

        if (sub.equals("list")) {
            player.sendMessage("§6=== Placed NPCs ===");
            if (manager.getPlaced().isEmpty()) {
                player.sendMessage("§7None placed yet. Use §f/setnpclocation <npc>§7.");
            }
            manager.getPlaced().forEach((npc, loc) ->
                    player.sendMessage("§7- §f" + npc.id + " §7(" + npc.displayName + "§7) @ " + format(loc)));
            player.sendMessage("§7Available: §f" + allIds());
            return true;
        }

        if (sub.equals("remove")) {
            if (args.length < 2) {
                player.sendMessage("§cUsage: /setnpclocation remove <npc>");
                return true;
            }
            FunctionalNpc npc = FunctionalNpc.byId(args[1]);
            if (npc == null) {
                player.sendMessage("§cUnknown NPC '" + args[1] + "'. Available: §f" + allIds());
                return true;
            }
            boolean removed = manager.remove(npc, plugin.getDataFolder());
            player.sendMessage(removed
                    ? "§aRemoved the " + npc.displayName + " §aNPC."
                    : "§7That NPC wasn't placed.");
            return true;
        }

        FunctionalNpc npc = FunctionalNpc.byId(sub);
        if (npc == null) {
            player.sendMessage("§cUnknown NPC '" + args[0] + "'. Available: §f" + allIds());
            return true;
        }
        manager.place(npc, player.getLocation(), plugin.getDataFolder());
        player.sendMessage("§aPlaced §r" + npc.displayName + " §ahere. §7Right-click it to open its menu.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            if ("list".startsWith(prefix)) {
                out.add("list");
            }
            if ("remove".startsWith(prefix)) {
                out.add("remove");
            }
            for (FunctionalNpc npc : FunctionalNpc.values()) {
                if (npc.id.startsWith(prefix)) {
                    out.add(npc.id);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            String prefix = args[1].toLowerCase();
            for (FunctionalNpc npc : FunctionalNpc.values()) {
                if (npc.id.startsWith(prefix)) {
                    out.add(npc.id);
                }
            }
        }
        return out;
    }

    private static String allIds() {
        StringBuilder sb = new StringBuilder();
        for (FunctionalNpc npc : FunctionalNpc.values()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(npc.id);
        }
        return sb.toString();
    }

    private static String format(Location loc) {
        String world = loc.getWorld() == null ? "?" : loc.getWorld().getName();
        return "§f" + world + " " + (int) loc.getX() + ", " + (int) loc.getY() + ", " + (int) loc.getZ();
    }

    private void sendUsage(Player player) {
        player.sendMessage("§6/setnpclocation <npc> §7— place a functional NPC where you stand");
        player.sendMessage("§7  /setnpclocation list §8— show placed NPCs");
        player.sendMessage("§7  /setnpclocation remove <npc> §8— remove one");
        player.sendMessage("§7Available NPCs: §f" + allIds());
    }
}
