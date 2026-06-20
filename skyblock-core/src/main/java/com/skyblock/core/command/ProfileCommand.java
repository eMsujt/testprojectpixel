package com.skyblock.core.command;

import com.skyblock.core.SkyBlockCore;
import com.skyblock.core.menu.ProfileMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles {@code /profile [player]}: opens the ProfileMenu for the target player
 * (defaults to self when no argument is given).
 */
public final class ProfileCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new ProfileMenu(SkyBlockCore.getInstance(), p).open(p);
    }

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            openMenu(player);
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            player.sendMessage("§cPlayer not found: " + args[0]);
            return true;
        }
        new ProfileMenu(SkyBlockCore.getInstance(), target).open(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(n -> n.toLowerCase().startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
