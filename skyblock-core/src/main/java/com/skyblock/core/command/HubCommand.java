package com.skyblock.core.command;

import com.skyblock.core.SkyBlockCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * Teleports a player to the configured hub. The destination is read from the
 * {@code hub} section of {@code config.yml}; if the configured world cannot be
 * found the first loaded world's spawn is used as a fallback.
 */
public final class HubCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        FileConfiguration config = SkyBlockCore.getInstance().getConfig();
        String worldName = config.getString("hub.world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            world = Bukkit.getWorlds().get(0);
        }

        Location destination = new Location(
                world,
                config.getDouble("hub.x", 0.5),
                config.getDouble("hub.y", 64.0),
                config.getDouble("hub.z", 0.5),
                (float) config.getDouble("hub.yaw", 0.0),
                (float) config.getDouble("hub.pitch", 0.0));

        player.teleport(destination);
        player.sendMessage("Teleported to the Hub.");
        return true;
    }
}
