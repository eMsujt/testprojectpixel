package com.skyblock.core.command;

import com.skyblock.core.manager.SackManager;
import com.skyblock.core.manager.SackManager.SackType;
import com.skyblock.core.menu.SackMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Opens the sack GUI, or lists sack contents as text with /sack list.
 */
public final class SackCommand extends PlayerCommand {

    private final SackManager sackManager;

    public SackCommand(SackManager sackManager) {
        this.sackManager = sackManager;
    }

    @Override
    protected void openMenu(Player p) {
        new SackMenu(p).open(p);
    }

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            openMenu(player);
            return true;
        }
        player.sendMessage(ChatColor.GOLD + "Your Sacks:");
        boolean any = false;
        for (SackType type : SackType.values()) {
            Map<String, Integer> contents = sackManager.getSackContents(player.getUniqueId(), type);
            if (contents.isEmpty()) {
                continue;
            }
            any = true;
            player.sendMessage(ChatColor.YELLOW + type.getDisplayName() + ":");
            for (Map.Entry<String, Integer> entry : contents.entrySet()) {
                player.sendMessage(ChatColor.GRAY + "  " + entry.getKey() + ": " + ChatColor.WHITE + entry.getValue());
            }
        }
        if (!any) {
            player.sendMessage(ChatColor.GRAY + "Your sacks are empty.");
        }
        return true;
    }
}
