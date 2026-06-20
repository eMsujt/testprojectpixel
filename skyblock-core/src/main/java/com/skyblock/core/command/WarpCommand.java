package com.skyblock.core.command;

import com.skyblock.core.manager.Warp;
import com.skyblock.core.manager.WarpManager;
import com.skyblock.core.menu.WarpMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Handles the {@code /warp} command (alias {@code /warps}).
 *
 * <p>With no arguments it opens the {@link WarpMenu} for the player; with a
 * warp name it teleports the player directly to that registered warp.</p>
 */
public final class WarpCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            new WarpMenu(player).open(player);
            return true;
        }

        String name = args[0].toLowerCase();
        Optional<Warp> warp = WarpManager.getInstance().getWarp(name);
        if (warp.isEmpty()) {
            player.sendMessage("§cUnknown warp: " + name);
            return true;
        }
        player.teleport(warp.get().toLocation());
        player.sendMessage("§aWarped to §b" + name + "§a.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> matches = new ArrayList<>();
            for (String name : WarpManager.getInstance().getWarpNames()) {
                if (name.startsWith(prefix)) {
                    matches.add(name);
                }
            }
            Collections.sort(matches);
            return matches;
        }
        return Collections.emptyList();
    }
}
