package com.skyblock.core;

import com.skyblock.core.command.PlayerCommand;
import com.skyblock.core.menu.SkillsMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Lightweight command stubs for commands that only open a menu.
 */
public final class Commands {

    private Commands() {}

    // =========================================================================
    // /skills (no-arg menu opener)
    // =========================================================================

    public static final class SkillsCmd extends PlayerCommand {

        @Override
        protected void openMenu(Player player) {
            new SkillsMenu(SkyBlockCore.getInstance(), player).open(player);
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            return List.of();
        }
    }
}
