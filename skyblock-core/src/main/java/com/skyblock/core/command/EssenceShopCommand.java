package com.skyblock.core.command;

import com.skyblock.core.manager.EssenceShopManager;
import com.skyblock.core.menu.EssenceShopMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public final class EssenceShopCommand implements TabExecutor {

    private final EssenceShopManager essenceShopManager;

    public EssenceShopCommand(EssenceShopManager essenceShopManager) {
        this.essenceShopManager = essenceShopManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        new EssenceShopMenu(player).open(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
