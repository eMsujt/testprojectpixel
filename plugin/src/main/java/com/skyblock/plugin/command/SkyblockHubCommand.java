package com.skyblock.plugin.command;

import com.skyblock.core.warp.WarpManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class SkyblockHubCommand implements CommandExecutor {

    private final WarpManager warpManager;

    public SkyblockHubCommand(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Location hub = warpManager.getWarp("hub");
        if (hub == null) {
            player.sendMessage("§cHub warp is not configured.");
            return true;
        }

        player.teleport(hub);
        player.sendMessage("§aTeleported to the Hub!");
        return true;
    }
}
