package com.skyblock.core.mob;

import com.skyblock.core.manager.MobManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code /setmobspawn <mob>} and {@code /removemobspawn} — admin commands to place
 * and clear custom-mob spawn points (the Hypixel-style spawning model). Stand where
 * a mob should spawn, mark it, and {@link MobSpawnManager} keeps the spot populated.
 */
public final class MobSpawnCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        if (!player.hasPermission("skyblock.admin")) {
            player.sendMessage("§cYou don't have permission to use this.");
            return true;
        }
        MobSpawnManager spawns = MobSpawnManager.getInstance();

        if (command.getName().equalsIgnoreCase("removemobspawn")) {
            String removed = spawns.removeNear(player.getLocation(), 4.0);
            if (removed != null) {
                player.sendMessage("§aRemoved a §f" + removed + " §aspawn point.");
            } else {
                player.sendMessage("§cNo spawn point within 4 blocks.");
            }
            return true;
        }

        // /setmobspawn <mob>
        if (args.length < 1) {
            player.sendMessage("§eUsage: §6/setmobspawn <mob>");
            player.sendMessage("§7Mobs: §f" + String.join(", ", MobManager.getInstance().getMobs().keySet()));
            return true;
        }
        MobManager.MobDefinition def = MobManager.getInstance().getMob(args[0].toLowerCase());
        if (def == null) {
            player.sendMessage("§cUnknown mob '" + args[0] + "'.");
            return true;
        }
        spawns.add(def.getId(), player.getLocation());
        player.sendMessage("§aSpawn point added for §f" + def.getDisplayName()
                + " §7(max " + def.getMaxPerSpot() + "/spot, " + def.getRespawnSeconds() + "s"
                + (def.isNightOnly() ? ", night-only" : "") + "). §7Total: §e" + spawns.count(def.getId()));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("setmobspawn") && args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> out = new ArrayList<>();
            for (String id : MobManager.getInstance().getMobs().keySet()) {
                if (id.startsWith(prefix)) {
                    out.add(id);
                }
            }
            return out;
        }
        return List.of();
    }
}
