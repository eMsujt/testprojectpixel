package com.skyblock.core.command;

import com.skyblock.core.manager.SackManager;
import com.skyblock.core.manager.SackManager.SackType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Lists the contents of each of the player's sacks.
 */
public final class SackCommand extends PlayerCommand {

    private final SackManager sackManager;

    public SackCommand(SackManager sackManager) {
        this.sackManager = sackManager;
    }

    @Override
    protected void openMenu(Player p) {}

    @Override
    protected boolean execute(Player player, Command command, String label, String[] args) {
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
