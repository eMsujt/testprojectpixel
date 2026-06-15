package com.skyblock.plugin.gui.menus;

import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.menu.EnchantingMenu} instead.
 */
@Deprecated
public class EnchantingMenu {

    private final UUID playerId;

    public EnchantingMenu(UUID playerId) {
        this.playerId = playerId;
    }

    public void open(Player player) {
        new com.skyblock.core.menu.EnchantingMenu(playerId).open(player);
    }
}
