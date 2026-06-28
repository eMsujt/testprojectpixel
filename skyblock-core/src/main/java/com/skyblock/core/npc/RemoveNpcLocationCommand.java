package com.skyblock.core.npc;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code /removenpclocation <npc>} — removes a placed functional NPC and its
 * armor stand. Tab-completes the NPCs currently placed.
 */
public final class RemoveNpcLocationCommand implements TabExecutor {

    private final JavaPlugin plugin;

    public RemoveNpcLocationCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        FunctionalNpcManager manager = FunctionalNpcManager.getInstance();
        if (args.length == 0) {
            player.sendMessage("§6/removenpclocation <npc> §7— remove a placed NPC.");
            player.sendMessage("§7Placed: §f" + (placedIds(manager).isEmpty() ? "none" : String.join(", ", placedIds(manager))));
            return true;
        }
        FunctionalNpc npc = FunctionalNpc.byId(args[0]);
        if (npc == null) {
            player.sendMessage("§cUnknown NPC '" + args[0] + "'.");
            return true;
        }
        boolean removed = manager.remove(npc, plugin.getDataFolder());
        player.sendMessage(removed
                ? "§aRemoved the " + npc.displayName + " §aNPC."
                : "§7That NPC wasn't placed.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            for (String id : placedIds(FunctionalNpcManager.getInstance())) {
                if (id.startsWith(prefix)) {
                    out.add(id);
                }
            }
        }
        return out;
    }

    private static List<String> placedIds(FunctionalNpcManager manager) {
        List<String> ids = new ArrayList<>();
        for (FunctionalNpc npc : manager.getPlaced().keySet()) {
            ids.add(npc.id);
        }
        return ids;
    }
}
