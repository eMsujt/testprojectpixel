package com.skyblock.core.hub;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Handles the {@code /skyblock hub} subcommand.
 *
 * <p>Teleports the executing player to the hub world's spawn point.</p>
 */
public final class SkyblockHubCommand implements TabExecutor {

    private final String hubWorldName;

    /**
     * @param hubWorldName name of the hub world as configured on the server
     */
    public SkyblockHubCommand(String hubWorldName) {
        this.hubWorldName = hubWorldName;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        World hub = Bukkit.getWorld(hubWorldName);
        if (hub == null) {
            player.sendMessage("Hub world '" + hubWorldName + "' is not loaded.");
            return true;
        }

        Location spawn = hub.getSpawnLocation();
        player.teleport(spawn);
        player.sendMessage("Teleported to hub spawn.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
