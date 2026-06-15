package com.skyblock.plugin.menu;

import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.menu.SkillsMenu} instead.
 */
@Deprecated
public class SkillsMenu extends com.skyblock.core.menu.SkillsMenu {

    public SkillsMenu(Player player) {
        super(player.getUniqueId());
    }

    public SkillsMenu(UUID playerId) {
        super(playerId);
    }
}
