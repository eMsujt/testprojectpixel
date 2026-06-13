package com.skyblock.core.profile;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ProfileCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            showProfile(player, player);
        } else {
            @SuppressWarnings("deprecation")
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (!target.hasPlayedBefore() && !target.isOnline()) {
                player.sendMessage("Player \"" + args[0] + "\" has never joined this server.");
                return true;
            }
            showProfile(player, target);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void showProfile(Player viewer, OfflinePlayer target) {
        String name = target.getName() != null ? target.getName() : target.getUniqueId().toString();
        viewer.sendMessage("=== Profile: " + name + " ===");
        viewer.sendMessage("  UUID     : " + target.getUniqueId());
        viewer.sendMessage("  Online   : " + (target.isOnline() ? "Yes" : "No"));
        if (target.getLastSeen() > 0) {
            viewer.sendMessage("  Last seen: " + new java.util.Date(target.getLastSeen()));
        }
        if (target.getFirstPlayed() > 0) {
            viewer.sendMessage("  Joined   : " + new java.util.Date(target.getFirstPlayed()));
        }
    }
}
