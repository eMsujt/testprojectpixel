package com.skyblock.core.command;

import com.skyblock.core.menu.SkillsMenu;
import org.bukkit.entity.Player;

public final class SkillsCommand extends PlayerCommand {

    @Override
    protected void openMenu(Player p) {
        new SkillsMenu(p.getUniqueId()).open(p);
    }
}
