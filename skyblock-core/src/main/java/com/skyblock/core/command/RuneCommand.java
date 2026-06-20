package com.skyblock.core.command;

import com.skyblock.core.menu.RuneMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class RuneCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public RuneCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        new RuneMenu(plugin, player).open(player);
        return true;
    }
}
