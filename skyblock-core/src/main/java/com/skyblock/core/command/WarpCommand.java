package com.skyblock.core.command;

import com.skyblock.core.manager.Warp;
import com.skyblock.core.manager.WarpManager;
import com.skyblock.core.menu.WarpMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class WarpCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new WarpMenu(p).open(p);
    }

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            openMenu(player);
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
